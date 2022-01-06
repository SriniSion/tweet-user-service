package com.tweetapp.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GlobalLoggingAspect {
	final static Logger logger = LoggerFactory.getLogger("");

	@Around("execution(* com.tweetapp.*.*(..))")
	public Object contollerLogging(ProceedingJoinPoint joinPoint) throws Throwable {
		Object result = new Object();
		String source = joinPoint.getSignature().getDeclaringTypeName();
		String functionName = joinPoint.getSignature().getName() + "()";
		result = joinPoint.proceed();
		logger.info("Invoking " + source + " : " + functionName);
		logger.info("Execution started : " + result.toString() + "   , Args[] : " + joinPoint.getArgs());
		logger.info("Execution ended : " + functionName);
		return result;
	}

	
}
