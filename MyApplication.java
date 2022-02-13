package com.project;

import com.project.db.MySqlDBClient;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * ALL SPRING ANNOTATIONS:
 * https://github.com/spring-projects/spring-framework/tree/master/spring-web/src/main/java/org/springframework/web/bind/annotation
 */
@SpringBootApplication
public class MyApplication {
	private String TMP_FOLDER = "/tmp";
	private int MAX_UPLOAD_SIZE = 100 * 1024 * 1024;

	public static void main(String[] args) {
//		SpringApplication.run(DemoApplication.class, args);
		SpringApplication app = new SpringApplication(MyApplication.class);
		//Properties properties = new Properties();
//		properties.put("server.address", "172.31.45.73");
//		properties.put("server.address", "13.59.228.49");
//		properties.put("server.address", "192.168.0.103");
		//properties.put("serv	er.port", "8080");
		//app.setDefaultProperties(properties);
//		app.setDefaultProperties(Collections.singletonMap("server.port", "8080"));
		app.run(args);
	}


	@Bean(initMethod="init")
	public startup exBean() {
		return new startup();
	}

	public class startup {
		@Autowired
		public void init(){
			// THIS IS THE PLACE TO INITIALIZE ALL

			// INIT SQL DB CLIENT

			System.out.println("VERSION 1.3");
			System.out.println("STARTUP STARTED");

			// TODO integration patch
			if (!MySqlDBClient.init()) {
				System.err.println("Failed to establish SQL connection");
				System.exit(-1);
			}
			ExamScheduleCalculator.getTheInstance().load();


			System.out.println("STARTUP ENDED");



		}
	}
//	@Bean
//	public MultipartConfigElement multipartConfigElement() {
//		MultipartConfigFactory factory = new MultipartConfigFactory();
//		factory.setMaxFileSize(DataSize.ofBytes(100000000L));
//		factory.setMaxRequestSize(DataSize.ofBytes(100000000L));
//		return factory.createMultipartConfig();
//	}
// Set maxPostSize of embedded tomcat server to 10 megabytes (default is 2 MB, not large enough to support file uploads > 1.5 MB)

	// Set maxPostSize of embedded tomcat server to 10 megabytes (default is 2 MB, not large enough to support file uploads > 1.5 MB)
	//Tomcat large file upload connection reset
	// https://stackoverflow.com/questions/47700115/tomcatembeddedservletcontainerfactory-is-missing-in-spring-boot-2
	@Bean
	public TomcatServletWebServerFactory containerFactory() {
		return new TomcatServletWebServerFactory() {
			protected void customizeConnector(Connector connector) {
				int maxSize = 50000000;
				DataSize dataSize = DataSize.ofMegabytes(100);
				super.customizeConnector(connector);
				connector.setMaxPostSize(maxSize);
				connector.setMaxPostSize((int) dataSize.toBytes());
				connector.setMaxSavePostSize(maxSize);
				connector.setMaxSavePostSize((int) dataSize.toBytes());
				if (connector.getProtocolHandler() instanceof AbstractHttp11Protocol) {

					((AbstractHttp11Protocol <?>) connector.getProtocolHandler()).setMaxSwallowSize(maxSize);
					logger.info("Set MaxSwallowSize "+ maxSize);
				}
			}
		};

	}
//	@Bean(name = "multipartResolver")
//	public CommonsMultipartResolver multipartResolver() {
//		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
//		DataSize dataSize = DataSize.ofMegabytes(100);
//		multipartResolver.setMaxUploadSize((int) dataSize.toBytes());
//		return multipartResolver;
//	}

	@Configuration
	public class MyWebApplicationInitializer
			implements WebApplicationInitializer {

		@Override
		public void onStartup(ServletContext sc) throws ServletException {

			ServletRegistration.Dynamic appServlet = sc.addServlet("mvc", new DispatcherServlet(
					new GenericWebApplicationContext()));

			appServlet.setLoadOnStartup(1);

			MultipartConfigElement multipartConfigElement = new MultipartConfigElement(TMP_FOLDER,
					MAX_UPLOAD_SIZE, MAX_UPLOAD_SIZE * 2, MAX_UPLOAD_SIZE / 2);

			appServlet.setMultipartConfig(multipartConfigElement);
		}
	}

	@Bean
	public StandardServletMultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}

}
