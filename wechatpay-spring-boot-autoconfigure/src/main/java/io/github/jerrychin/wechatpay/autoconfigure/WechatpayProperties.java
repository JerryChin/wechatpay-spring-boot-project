package io.github.jerrychin.wechatpay.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

@Setter
@Getter
@ConfigurationProperties("wechatpay")
public class WechatpayProperties {

	/**
	 * 商户号。
	 */
	private String merchantId;

	/**
	 * 商户API证书的证书序列号。
	 */
	private String merchantSerialNumber;

	/**
	 * 商户API私钥。
	 *
	 * <p/>
	 * 商户申请商户API证书时，会生成商户私钥，并保存在本地证书文件夹的文件apiclient_key.pem中。
	 *
	 * <p/>
	 * 注：不要把私钥文件暴露在公共场合，如上传到Github，写在客户端代码等。
	 */
	private String merchantPrivateKey;

	/**
	 * API v3密钥。
	 *
	 * <p/>
	 * 为了保证安全性，微信支付在回调通知和平台证书下载接口中，对关键信息进行了AES-256-GCM加密。
	 *
	 * <p/>
	 * API v3密钥是加密时使用的对称密钥。商户可以在【商户平台】->【API安全】的页面设置该密钥。
	 */
	private String apiV3Key;

	@PostConstruct
	public void postConstruct() {
		hasLength(merchantId, "wechatpay.merchantId is required.");
		hasLength(merchantSerialNumber, "wechatpay.merchantSerialNumber is required.");
		hasLength(merchantPrivateKey, "wechatpay.merchantPrivateKey is required.");
		hasLength(apiV3Key, "wechatpay.apiV3Key is required.");
	}

	private static void hasLength(String text, String message) {
		if (text == null || text.trim().length() == 0) {
			throw new IllegalArgumentException(message);
		}
	}
}