package de.edittrich.alexa.spring;

import com.amazon.speech.Sdk;
import com.amazon.speech.speechlet.servlet.SpeechletServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class Application {

	@Autowired
    public void setEnvironment(Environment env) {
    	System.setProperty(Sdk.DISABLE_REQUEST_SIGNATURE_CHECK_SYSTEM_PROPERTY, env.getProperty("application.disableSignature"));
        System.setProperty(Sdk.SUPPORTED_APPLICATION_IDS_SYSTEM_PROPERTY, env.getProperty("application.id"));
        System.setProperty(Sdk.TIMESTAMP_TOLERANCE_SYSTEM_PROPERTY, env.getProperty("application.timestampTolerance"));
    }
     
    @Bean
    public ServletRegistrationBean alexaServlet(HelloWorldSpeechlet speechlet) {
        SpeechletServlet speechServlet = new SpeechletServlet();
        speechServlet.setSpeechlet(speechlet);

        ServletRegistrationBean servlet = new ServletRegistrationBean(speechServlet, "/alexa");
        servlet.setName("alexa");

        return servlet;
    }	

    public static void main(String[] args) {
         SpringApplication.run(Application.class, args);
    }

}