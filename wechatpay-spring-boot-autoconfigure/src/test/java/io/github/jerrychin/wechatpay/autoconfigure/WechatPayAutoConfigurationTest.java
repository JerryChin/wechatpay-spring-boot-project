package io.github.jerrychin.wechatpay.autoconfigure;

import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WechatPayAutoConfigurationTest {
	private static final String MOCK_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n" +
			"MIIEwAIBADANBgkqhkiG9w0BAQEFAASCBKowggSmAgEAAoIBAQDNgjMGMRu4jdK2\n" +
			"N9p6kVZ4iZNvS0qGKlmhO2wqOxBFxNVY71FvF0djshKsqs4n+5ZW3ww+6iJ3Dp72\n" +
			"EsaAJ0i4lupxBsxhyJNxbQ6sGMGH6g6n+3kazQnVqgLEYFBPNjeJEO+DUGKjCYvS\n" +
			"3oUW1p01n3UBW2S0NLqQ9HKfm0eZEQj4x6CKshZGbiIekhgUi+sL1qWS3jonGoJc\n" +
			"Rfh/xsbMNJjupZUHpKpQraQ3ZcqYXblX+kJStaCGE3npTJESiHlDKLec+PhoxXKD\n" +
			"vrJTPjVH9eNQuKARRP7SwwoPcDR4abZYMs0eM52TQ/wTX3xb9tUR0oEA1MFbAwDF\n" +
			"l0ZKIR39AgMBAAECggEBALW0exzsatBBRZ+rvtdEjipBqkRrU9dQtrMicT7dvkOS\n" +
			"B/l8kIojERILLwvQcjtT7dedm5w9NIAEQ3TboPtKwLfFaEDLfAaxa/yMKr7rnKJp\n" +
			"xs+sP6X8yMdMbOnpJtkC3ROqD7wRZUU2eZTW/uH0nEf/V1r9zgqv3I2yNW79ws6I\n" +
			"dbG3R5BYR9hte7ShDEgagW58ARevY8dpaAY4J4ht/t6eTP8ozmsF5KvkFG9j6+XS\n" +
			"Rjh1jYp14BhsiYzEiB0naiSJOoURluCl18/Jb+xUBnUqyrL6wpv3G30YnifUxh72\n" +
			"sn26e2sMYaw5/vSINKJXyW9iBO7/czI8/zudtHa0+5kCgYEA6bzXJkSXkNq6pKca\n" +
			"EF1ZL/Azc5NIWv0oaLUB3hTeS91cBAe+uq2eBLb5JxuFl9HJAxbJ/s4mYPSTOMi2\n" +
			"cOLCsIic83YHa0BCAdV9p4RMmsVp6bq9Rp5NHx5u9sf8mLgIGt80TIHnTfowST65\n" +
			"MZvcV0FWtYTgg5H1XV6PHJbelN8CgYEA4RUOt4sDySlotAS7hdwDdhrPNVb17Z/h\n" +
			"glCzZnJ7hSK5iMgXl9hisyEFrfbldXOuu7P+RU12Rp42gsWIc+zMmYBh8RAw3DhV\n" +
			"z1upA7bZRmxPc0GKVRS8TiIsvdf5kYDLj1kXsnJUMHU2vripU4xnPYUUxtdFP0nw\n" +
			"v113px9uLKMCgYEA2TQP5tLU2do4mStjcBOe/opC9cLG+S7Jr0uNWbegv3ULvf8I\n" +
			"gWc3MYBq1amgom1jU0iJGwys42pDqP/zccF3VB8PYrqquEK1VAj1RIcPvL945acP\n" +
			"nqEaX7GjQhajp7NUbMxASFxq+q5k0pvD8tK4lij0j6fUDs/C6Con6VRVsJcCgYEA\n" +
			"idSfQn36P4EAANy1CG+GHxijsoIh7vVs3qQUd7O7N/ffrYb6C54SMqcSF0mNkbLZ\n" +
			"WVALcNzShMP1lXbo7DQhxg376+aCRRiqU2RJx2B9BMbmkCmeUCN2fFIpG8fZp+Q6\n" +
			"wvlHrZqdO19LutbRubvMrLuF0Y2ZdfwNVOIjl0+D5AkCgYEA00fpB9KXoEm5zsfd\n" +
			"izJvjlD4oaHf8/AprkU8XIUqXsc6YWdHNxppD8HHVPBXNsZlYeU+K2PvIQ3NN21a\n" +
			"XpdBC1JBhoktpxqyArbUUrGkbsYz1eejnbYuwD+EIYa5/ajR3H/YxREITLh75vt2\n" +
			"J3k08LiITaNXpMJGuOHQ1FMNrJw=\n" +
			"-----END PRIVATE KEY-----";

	private final ApplicationContextRunner runner = new ApplicationContextRunner()
			// 禁止 RestTemplate 自动装配
			.withClassLoader(new FilteredClassLoader(RestTemplate.class));


	private void assertAllBeansNotCreated(AssertableApplicationContext context) {
		assertThat(context).doesNotHaveBean("wechatPay2Credentials");
		assertThat(context).doesNotHaveBean("certificatesManager");
		assertThat(context).doesNotHaveBean("wechatPayHttpClientBuilder");
		assertThat(context).doesNotHaveBean("closeableHttpClient");
		assertThat(context).doesNotHaveBean("notificationHandler");
		assertThat(context).doesNotHaveBean("restTemplate");
	}

	@Test
	public void whenPropertiesNotBothPresent_thenBeansNotCreated() {
		runner.withUserConfiguration(WechatPayAutoConfiguration.class).run(this::assertAllBeansNotCreated);

		runner.withPropertyValues("wechatpay.merchantId=1")
				.withUserConfiguration(WechatPayAutoConfiguration.class).run(this::assertAllBeansNotCreated);

		runner.withPropertyValues("wechatpay.apiV3Key=2")
				.withUserConfiguration(WechatPayAutoConfiguration.class).run(this::assertAllBeansNotCreated);
	}

	@Test
	public void whenPropertiesPresent_thenBeansCreated() {
		runner.withPropertyValues("wechatpay.merchantId=1", "wechatpay.apiV3Key=2")
				.withBean(WechatPay2Credentials.class, () -> mock(WechatPay2Credentials.class))
				.withBean(CertificatesManager.class, this::mockCertificatesManager)
				.withUserConfiguration(WechatPayAutoConfiguration.class).run(context -> {
					assertThat(context).hasBean("wechatPayHttpClientBuilder");
					assertThat(context).hasBean("closeableHttpClient");
					assertThat(context).hasBean("notificationHandler");
				});
	}


	@Test
	public void whenPropertiesNotPresent_thenWechatPay2CredentialBeanMissing() {
		ApplicationContextRunner configuredRunner = runner
				.withBean(CertificatesManager.class, this::mockCertificatesManager);

		configuredRunner.withPropertyValues("wechatpay.merchantId=1", "wechatpay.apiV3Key=2")
				.withUserConfiguration(WechatPayAutoConfiguration.class)
				.run(context -> {
					assertThat(context).hasFailed();
					assertThat(context).getFailure().hasCauseInstanceOf(NoSuchBeanDefinitionException.class);
				});

		configuredRunner.withPropertyValues("wechatpay.merchantId=1", "wechatpay.apiV3Key=2",
						"wechatpay.merchantSerialNumber=3")
				.withUserConfiguration(WechatPayAutoConfiguration.class).run(context -> {
					assertThat(context).hasFailed();
					assertThat(context).getFailure().hasCauseInstanceOf(NoSuchBeanDefinitionException.class);
				});

		configuredRunner.withPropertyValues("wechatpay.merchantId=1", "wechatpay.apiV3Key=2",
						"wechatpay.merchantPrivateKey=4")
				.withUserConfiguration(WechatPayAutoConfiguration.class).run(context -> {
					assertThat(context).hasFailed();
					assertThat(context).getFailure().hasCauseInstanceOf(NoSuchBeanDefinitionException.class);
				});
	}

	@Test
	public void whenPropertiesPresent_thenWechatPay2CredentialBeanCreated() {
		runner.withPropertyValues("wechatpay.merchantId=1", "wechatpay.apiV3Key=2",
						"wechatpay.merchantSerialNumber=3",
						"wechatpay.merchantPrivateKey=" + MOCK_PRIVATE_KEY)
				.withBean(CertificatesManager.class, this::mockCertificatesManager)
				.withUserConfiguration(WechatPayAutoConfiguration.class).run(context -> {
					assertThat(context).hasBean("wechatPay2Credentials");
				});
	}

	@Test
	public void whenHttpClientBuilderConfigurerPresent_thenItShouldBeCalled() {
		HttpClientBuilderConfigurer configurer = mock(HttpClientBuilderConfigurer.class);
		runner.withPropertyValues("wechatpay.merchantId=1", "wechatpay.apiV3Key=2",
						"wechatpay.merchantSerialNumber=3",
						"wechatpay.merchantPrivateKey=" + MOCK_PRIVATE_KEY)
				.withBean(CertificatesManager.class, this::mockCertificatesManager)
				.withBean(HttpClientBuilderConfigurer.class, ()-> configurer)
				.withUserConfiguration(WechatPayAutoConfiguration.class).run(context -> {
					verify(configurer).configure(context.getBean(WechatPayHttpClientBuilder.class));
				});
	}

	private CertificatesManager mockCertificatesManager() {
		CertificatesManager manager = mock(CertificatesManager.class);
		Verifier verifier = mock(Verifier.class);

		try {
			when(manager.getVerifier(any())).thenReturn(verifier);
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}

		return manager;
	}
}