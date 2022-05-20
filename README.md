# 微信支付 Spring Boot 项目

本项目基于微信支付官方 SDK [wechatpay-apache-httpclient](https://github.com/wechatpay-apiv3/wechatpay-apache-httpclient)，面向 Spring Boot 应用提供了自动化配置功能，降低了 Spring boot 项目接入微信支付 SDK 的难度。

## 版本状态

当前项目已发布 `0.0.1` 版本，微信支付 SDK 版本支持 `0.4.4` 及以上版本。

由于手头暂时没有微信支付相关秘钥因此暂时无法验证代码的正确性，希望有能力的同学可以帮忙验证，先谢为过。

## 引入项目

本项目已发布至中央仓库，可以直接引入。

### 引用依赖
引入 `wechatpay-spring-boot-starter` 和 `wechatpay-apache-httpclient`， 增加以下依赖到工程的 `pom.xml` 文件中：

```xml
<properties>
   <wechatpay.version>0.4.4</wechatpay.version>
   <wechatpay.starter.version>0.0.1</wechatpay.starter.version>
</properties>

<dependencies>

    <!-- WechatPay SDK -->
    <dependency>
        <groupId>com.github.wechatpay-apiv3</groupId>
        <artifactId>wechatpay-apache-httpclient</artifactId>
        <version>${wechatpay.version}</version>
    </dependency>

    <!-- WechatPay Spring Boot Starter -->
    <dependency>
       <groupId>io.github.jerrychin</groupId>
       <artifactId>wechatpay-spring-boot-starter</artifactId>
       <version>${wechatpay.starter.version}</version>
    </dependency>
</dependencies>
```

## 快速入门

本文所提及到的概念都可以在  [wechatpay-apache-httpclient](https://github.com/wechatpay-apiv3/wechatpay-apache-httpclient) 项目内查到。

1. 配置 `application.properties` :

    ```properties
    # 你的商户号，不存在则不会进行自动化配置，下面是个示例：
    wechatpay.merchantId=1230000109
    
    # API v3密钥，不存在则不会进行自动化配置，下面是个示例：
    wechatpay.apiV3Key=12341234123412341234123412341234
   
    # 商户API证书的证书序列号，若不存在则用户必须自行提供 WechatPay2Credentials，下面是个示例：
    wechatpay.merchantSerialNumber=1DDE55AD98ED71D6EDD4A4A16996DE7B47773A8C
   
    # 商户API私钥，若不存在则用户必须自行提供 WechatPay2Credentials，下面是个示例：
    wechatpay.merchantPrivateKey=-----BEGIN PRIVATE KEY-----\n-----END PRIVATE KEY-----
    
    # 日志等级，可选
    logging.level.io.github.jerrychin.wechatpay=trace
    ```

2. 实现 `DemoController`

    ```java 
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
    ```



3. 编写 Spring Boot 引导程序并启动

    ```java
    @SpringBootApplication
    public class DemoApplication {

        public static void main(String[] args) {
            SpringApplication.run(DemoApplication.class,args);
        }
    }
    ```
   
4. 请求接口

   ```shell
   curl http://localhost:8080/demo/certificates
   ```

## 模块说明

本项目使用了多 Maven 模块工程 , 各个模块如下：

### [wechatpay-spring-boot-parent](wechatpay-spring-boot-parent)

[wechatpay-spring-boot-parent](wechatpay-spring-boot-parent) 模块主要管理微信支付 Spring Boot 项目的 Maven 依赖

### [wechatpay-spring-boot-autoconfigure](wechatpay-spring-boot-autoconfigure)

[wechatpay-spring-boot-autoconfigure](wechatpay-spring-boot-autoconfigure) 模块提供 Spring Boot 的 `@EnableAutoConfiguration` 的实现 - `WechatPayAutoConfiguration`，
它简化了微信支付 SDK 核心组件的装配，强烈建议阅读了解。

### [wechatpay-spring-boot-starter](wechatpay-spring-boot-starter)

[wechatpay-spring-boot-starter](wechatpay-spring-boot-starter) 模块为标准的 Spring Boot Starter,
你需要在项目中引入此依赖，[wechatpay-spring-boot-autoconfigure](wechatpay-spring-boot-autoconfigure) 模块会一同被间接依赖。

### [wechatpay-sample-app](wechatpay-sample-app)

一个简单的使用示例，开发者可以了解到：
- 如何配置微信支付参数 —— `application.properties`
- 怎么注入微信支付组件 —— `DemoController`
- 如何自定义微信支付组件配置 —— `HttpClientBuilderConfigurerImpl`

## 问题反馈

如果遇到微信支付官方 SDK 相关问题请前往 [wechatpay-apache-httpclient](https://github.com/wechatpay-apiv3/wechatpay-apache-httpclient) 进行反馈，其它问题请在 issues 区进行反馈。

## 免责声明

本项目是我的个人开源项目，该项目的功能完整性和安全性不作任何保证，使用本项目时建议进行代码审查和功能测试！

当然，我肯定尽我所能来保证项目的整体质量。
