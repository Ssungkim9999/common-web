package com.ssungkim.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingService {

	/**
	 * @since v1.0.0
	 * @param className 로깅 처리될 클래스
	 * @return 로깅 처리를 진행할 {@link Logger} 객체
	 * @see Logger
	 */
	private static Logger createLogger(Class<?> className) {
		return LoggerFactory.getLogger(className);
	}
	
	/**
	 * @since v1.0.0
	 * @param className 로깅 처리될 클래스
	 * @param logData Info 레벨로 로깅 처리될 문자열
	 */
	public static void info(Class<?> className, String logData) {
		createLogger(className).info(logData);
	}
	
	/**
	 * @since v1.0.0
	 * @param className 로깅 처리될 클래스
	 * @param logData Debug 레벨로 로깅 처리될 문자열
	 */
	public static void debug(Class<?> className, String logData) {
		createLogger(className).debug(logData);
	}
	
	/**
	 * @since v1.0.0
	 * @param className 로깅 처리될 클래스
	 * @param logData Error 레벨로 로깅 처리될 문자열
	 * @param e 발생한 {@code Exception} 클래스
	 */
	public static void error(Class<?> className, String logData, Exception e) {
		createLogger(className).error(logData);
		e.printStackTrace();
	}
	
	/**
	 * @since v1.0.0
	 * @param className 로깅 처리될 클래스
	 * @return Info 레벨의 로깅 처리가 가능할 경우 true 리턴, 아닐 경우 false 리턴
	 */
	public static boolean isInfoEnabled(Class<?> className) {
		return createLogger(className).isInfoEnabled();
	}
}
