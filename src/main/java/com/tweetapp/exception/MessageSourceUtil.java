package com.tweetapp.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageSourceUtil implements MessageSourceAware {
	
	
	

	@Autowired
	private MessageSource messageSource;

	private static final String ERRORBASEKEY = "ERROR_CONFIG";
	private static final String ERRORSEPERATOR = ".";
	private static final String ERRORMESSAGE = "message";
	private static final String BASEKEY = ERRORBASEKEY + ERRORSEPERATOR;

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public String getLocalisedText(String errorCode, String module) {
		String message = messageSource.getMessage(BASEKEY + module + ERRORSEPERATOR + errorCode + ERRORSEPERATOR + ERRORMESSAGE,
				new Object[0], LocaleContextHolder.getLocale());
		System.out.println(message);
		
		return message;

	}

}
