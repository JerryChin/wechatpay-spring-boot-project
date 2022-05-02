package io.github.jerrychin.wechatpay.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties("wechatpay")
public class WechatpayProperties {

	/**
	 * 商户号，可选，不存在则不会进行自动化配置。
	 */
	private String merchantId;

	/**
	 * API v3密钥，可选，不存在则不会进行自动化配置。
	 *
	 * <p/>
	 * 为了保证安全性，微信支付在回调通知和平台证书下载接口中，对关键信息进行了AES-256-GCM加密。
	 *
	 * <p/>
	 * API v3密钥是加密时使用的对称密钥。商户可以在【商户平台】->【API安全】的页面设置该密钥。
	 */
	private String apiV3Key;

	/**
	 * 商户API证书的证书序列号，可选，若不存在则用户必须自行提供 {@link com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials} 依赖。
	 */
	private String merchantSerialNumber;

	/**
	 * 商户API私钥，可选，若不存在则用户必须自行提供 {@link com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials} 依赖。
	 *
	 * <p/>
	 * 商户申请商户API证书时，会生成商户私钥，并保存在本地证书文件夹的文件apiclient_key.pem中。
	 *
	 * <p/>
	 * 注：不要把私钥文件暴露在公共场合，如上传到Github，写在客户端代码等。
	 */
	private String merchantPrivateKey;
}