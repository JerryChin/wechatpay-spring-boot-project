package demo;

import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;


@SpringBootApplication
public class DemoApplication implements ApplicationListener<ApplicationStartedEvent> {

	@Autowired
	private CloseableHttpClient closeableHttpClient;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		System.out.println(closeableHttpClient);
	}

}
