package co.com.lotopunto.mslotopunto;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * main class to run api.
 * @author jhovannycanas
 * @see Application
 * @see SpringBootApplication
 * @see ServletRegistrationBean
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
		ServletRegistrationBean register = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/persons/v1/*");
		register.setName("CamelServlet");
		return register;
	}
}
