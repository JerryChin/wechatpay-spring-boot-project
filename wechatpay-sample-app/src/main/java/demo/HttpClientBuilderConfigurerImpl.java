package demo;

import io.github.jerrychin.wechatpay.autoconfigure.HttpClientBuilderConfigurer;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

@Component
public class HttpClientBuilderConfigurerImpl implements HttpClientBuilderConfigurer {

    @Override
    public void configure(HttpClientBuilder builder) {
        builder.setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(1000).build());
    }
}
