package com.tweetapp.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class JavaMailSender {
	@Value("${email.service.emailId}")
	private String emailId;
	@Value("${email.service.password}")
	private String password;
	@Value("${email.service.protocol}")
	private String protocol;
	@Value("${email.service.auth}")
	private String auth;
	@Value("${email.service.enabled}")
	private String tlsEnabled;
	@Value("${email.service.dubug}")
	private String mailDebug;
	@Value("${email.service.port}")
	private int port;
	@Value("${email.service.host}")
	private String host;

	@Bean(name = "mailsender")
	public JavaMailSenderImpl getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(host);
		mailSender.setPort(port);

		mailSender.setUsername(emailId);
		mailSender.setPassword(password);

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", protocol);
		props.put("mail.smtp.auth", auth);
		props.put("mail.smtp.starttls.enable", tlsEnabled);
		props.put("mail.debug", mailDebug);

		return mailSender;
	}

}
