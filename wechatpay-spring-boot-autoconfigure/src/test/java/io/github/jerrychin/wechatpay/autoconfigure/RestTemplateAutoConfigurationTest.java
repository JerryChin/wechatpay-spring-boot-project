package io.github.jerrychin.wechatpay.autoconfigure;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RestTemplateAutoConfigurationTest {

	private final ApplicationContextRunner runner = new ApplicationContextRunner();


	@Test
	public void whenRestTemplateClassNotPresent_thenRestTemplateBeanNotCreated() {
		runner.withClassLoader(new FilteredClassLoader(RestTemplate.class))
				.withUserConfiguration(WechatPayAutoConfiguration.RestTemplateAutoConfiguration.class).run(context -> {
					assertThat(context).doesNotHaveBean("restTemplate");
				});
	}

	@Test
	public void whenRestTemplateClassPresent_thenRestTemplateBeanCreated() {
		RestTemplateBuilder builder = mock(RestTemplateBuilder.class);

		RestTemplate template = mock(RestTemplate.class);
		when(builder.requestFactory(any(Supplier.class))).thenReturn(builder);
		when(builder.build()).thenReturn(template);

		runner.withBean(CloseableHttpClient.class, () -> mock(CloseableHttpClient.class))
				.withBean(RestTemplateBuilder.class, () -> builder)
				.withUserConfiguration(WechatPayAutoConfiguration.RestTemplateAutoConfiguration.class).run(context -> {
					assertThat(context).hasBean("restTemplate");
					assertThat(context).getBean(RestTemplate.class).isEqualTo(template);
				});
	}

	@Test
	public void whenRestTemplateBeanPresent_thenRestTemplateBeanNotCreated() {
		RestTemplate template = mock(RestTemplate.class);
		runner.withBean(RestTemplate.class, () -> template)
				.withUserConfiguration(WechatPayAutoConfiguration.RestTemplateAutoConfiguration.class).run(context -> {
					assertThat(context).hasBean("restTemplate");
					assertThat(context).getBean(RestTemplate.class).isEqualTo(template);
				});
	}


}
