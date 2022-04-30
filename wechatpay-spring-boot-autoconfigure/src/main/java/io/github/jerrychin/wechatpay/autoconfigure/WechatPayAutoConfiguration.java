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
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

@Configuration
@ConditionalOnClass(WechatPayHttpClientBuilder.class)
@EnableConfigurationProperties(WechatpayProperties.class)
public class WechatPayAutoConfiguration {

	@Autowired
	private WechatpayProperties properties;

	@Bean
	@ConditionalOnMissingBean
	public CertificatesManager certificatesManager() throws GeneralSecurityException, IOException, HttpCodeException {
		CertificatesManager certificatesManager = CertificatesManager.getInstance();

		PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
				new ByteArrayInputStream(properties.getMerchantPrivateKey().getBytes(StandardCharsets.UTF_8)));

		// 向证书管理器增加需要自动更新平台证书的商户信息
		certificatesManager.putMerchant(properties.getMerchantId(),
				new WechatPay2Credentials(properties.getMerchantId(),
				new PrivateKeySigner(properties.getMerchantSerialNumber(), merchantPrivateKey)),
				properties.getMerchantPrivateKey().getBytes(StandardCharsets.UTF_8));

		return certificatesManager;

	}

	@Bean
	public CloseableHttpClient closeableHttpClient(CertificatesManager certificatesManager) throws NotFoundException {

		PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
				new ByteArrayInputStream(properties.getMerchantPrivateKey().getBytes(StandardCharsets.UTF_8)));

		WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
				.withMerchant(properties.getMerchantId(), properties.getMerchantSerialNumber(), merchantPrivateKey)
				.withValidator(new WechatPay2Validator(certificatesManager.getVerifier(properties.getMerchantId())));
		return builder.build();
	}

	@Bean
	@ConditionalOnMissingBean
	public NotificationHandler notificationHandler(CertificatesManager certificatesManager) throws NotFoundException {
		return new NotificationHandler(certificatesManager.getVerifier(properties.getMerchantId()),
				properties.getApiV3Key().getBytes(StandardCharsets.UTF_8));
	}

}
