package io.github.jerrychin.wechatpay.autoconfigure;

import org.apache.http.impl.client.HttpClientBuilder;

/**
 *
 * 本接口允许开发者自定义 {@link HttpClientBuilder} 的各项配置。
 *
 */
public interface HttpClientBuilderConfigurer {
    void configure(HttpClientBuilder builder);
}
