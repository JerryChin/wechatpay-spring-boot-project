package io.github.jerrychin.wechatpay.autoconfigure;

import org.junit.After;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class WechatPayAutoConfigurationTest {

	private AnnotationConfigApplicationContext context;

	@After
	public void tearDown() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void defaultNativeConnectionFactory() {
//		load(EmptyConfiguration.class, "hornetq.mode=native");
//		JmsTemplate jmsTemplate = this.context.getBean(JmsTemplate.class);
	}

	private void load(Class<?> config, String... environment) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.register(config);
		applicationContext.register(WechatPayAutoConfiguration.class);
		applicationContext.refresh();
		this.context = applicationContext;
	}

}
