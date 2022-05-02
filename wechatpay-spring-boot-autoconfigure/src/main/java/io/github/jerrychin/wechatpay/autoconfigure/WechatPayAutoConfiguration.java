package io.github.jerrychin.wechatpay.autoconfigure;

import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.exception.HttpCodeException;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationHandler;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

@Slf4j
@Configuration
@ConditionalOnClass(WechatPayHttpClientBuilder.class)
@EnableConfigurationProperties(WechatpayProperties.class)
@ConditionalOnProperty(prefix = "wechatpay", name = {"merchant-id", "api-v3-key"})
public class WechatPayAutoConfiguration {

	@Autowired(required = false)
	private HttpClientBuilderConfigurer configurer;

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "wechatpay", name = {"merchant-serial-number", "merchant-private-key"})
	public WechatPay2Credentials wechatPay2Credentials(WechatpayProperties properties) {
		log.trace("wechatPay2Credentials(...) entered.");

		PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
				new ByteArrayInputStream(properties.getMerchantPrivateKey().getBytes(StandardCharsets.UTF_8)));

		return new WechatPay2Credentials(properties.getMerchantId(),
				new PrivateKeySigner(properties.getMerchantSerialNumber(), merchantPrivateKey));
	}

	@Bean
	@ConditionalOnMissingBean
	public CertificatesManager certificatesManager(WechatpayProperties properties, WechatPay2Credentials credentials)
			throws GeneralSecurityException, IOException, HttpCodeException {
		log.trace("certificatesManager(..) entered.");

		CertificatesManager certificatesManager = CertificatesManager.getInstance();

		// 向证书管理器增加需要自动更新平台证书的商户信息
		certificatesManager.putMerchant(properties.getMerchantId(), credentials,
				properties.getApiV3Key().getBytes(StandardCharsets.UTF_8));

		return certificatesManager;
	}

	@Bean
	@ConditionalOnMissingBean
	public WechatPayHttpClientBuilder wechatPayHttpClientBuilder(WechatpayProperties properties,
																 WechatPay2Credentials credentials,
																 CertificatesManager certificatesManager) throws NotFoundException {
		log.trace("wechatPayHttpClientBuilder(..) entered.");

		WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
				.withCredentials(credentials)
				.withValidator(new WechatPay2Validator(certificatesManager.getVerifier(properties.getMerchantId())));

		if(configurer != null) {
			log.trace("configurer found.");
			configurer.configure(builder);
		}

		return builder;
	}

	@Bean
	@ConditionalOnMissingBean
	public CloseableHttpClient closeableHttpClient(WechatPayHttpClientBuilder builder) {
		log.trace("closeableHttpClient() entered.");

		return builder.build();
	}

	@Bean
	@ConditionalOnMissingBean
	public NotificationHandler notificationHandler(WechatpayProperties properties, CertificatesManager certificatesManager) throws NotFoundException {
		log.trace("notificationHandler() entered.");

		return new NotificationHandler(certificatesManager.getVerifier(properties.getMerchantId()),
				properties.getApiV3Key().getBytes(StandardCharsets.UTF_8));
	}

	@Configuration
	@ConditionalOnClass(RestTemplate.class)
	static class RestTemplateAutoConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public RestTemplate restTemplate(CloseableHttpClient httpClient, RestTemplateBuilder builder) {
			log.trace("restTemplate() entered.");

			return builder.requestFactory(() -> new HttpComponentsClientHttpRequestFactory(httpClient)).build();
		}

	}

}
