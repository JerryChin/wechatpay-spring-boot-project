package demo.controller;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/demo")
public class DemoController {

    private final CloseableHttpClient httpClient;

    public DemoController(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @GetMapping("/certificates")
    public String getCertificates() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("https://api.mch.weixin.qq.com/v3/certificates");
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.addHeader("Accept", "application/json");
        return EntityUtils.toString(httpClient.execute(httpGet).getEntity());
    }
}
