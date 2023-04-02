package com.ssungkim.model;

import com.ssungkim.web.service.CommonService;
import com.ssungkim.web.service.LoggingService;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;

/**
 * @author Aiden Kim
 * @since v1.0.0
 * @see HashMap
 * @serial
 */
@SuppressWarnings("serial")
@Component
public class DataMap extends HashMap<String, Object> {

	@Override
	public Object get(Object key) throws NullPointerException {
		Object o = super.get(key);
		if(o == null) throw new NullPointerException();
		return o;
	}

	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param key 가져올 데이터에 대한 key 문자열
	 * @return key에 해당하는 trim 처리된 문자열 값
	 * @throws {@link NullPointerException} key에 해당하는 값이 없는 경우
	 */
	public String getString(String key) throws NullPointerException {
		return get(key).toString().trim();
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param key 가져올 데이터에 대한 key 문자열
	 * @return key에 해당하는 정수형 값. 예상치 못한 예외 발생 시 0 리턴
	 * @throws {@linke NullPointerException} key에 해당하는 값이 없는 경우
	 * @throws {@link NumberFormatException} key에 해당하는 값의 형식이 숫자 형식이 아닐 경우
	 * @throws {@link ParseException} key에 해당하는 값을 {@link Integer}로 parsing하지 못할 경우
	 */
	public int getInt(String key) throws NullPointerException, NumberFormatException, ParseException {
		int n = 0;
		try {
			Object o = get(key);
			String s = o == null ? "0" : o.toString().trim();
			try{
				s = CommonService.replaceMatchingValueRegexFromData(RegexType.INTEGER, s);
				n = Integer.parseInt(s);
			} catch(NumberFormatException e){
				LoggingService.error(getClass(), e.getClass()+" for parsing data. Because value is not a number format. Key : "+key+" / Data : "+o, e);
				throw new NumberFormatException();
			} catch(ParseException e){
				LoggingService.error(getClass(), e.getClass()+" for parsing data. Key : "+key+" / Data : "+o, e);
				throw new ParseException(e.getPosition(), e.getMessage());
			} catch(Exception e){
				LoggingService.error(getClass(), e.getClass()+" for parsing data. Key : "+key, e);
				return 0;
			}
		} catch(NullPointerException e){
			LoggingService.error(getClass(), e.getClass()+" for parsing data. Because DataMap has not value about key. Key : "+key, e);
			throw new NullPointerException();
		}
		return n;
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param key 가져올 데이터에 대한 key 문자열
	 * @return key에 해당하는 실수형 값. 예상치 못한 예외 발생 시 0.0 리턴
	 * @throws {@link NullPointerException} key에 해당하는 값이 없는 경우
	 * @throws {@link NumberFormatException} key에 해당하는 값의 형식이 숫자 형식이 아닐 경우
	 * @throws {@link ParseException} key에 해당하는 값을 {@link Double}로 parsing하지 못할 경우
	 */
	public double getDouble(String key) throws NullPointerException, NumberFormatException, ParseException {
		double n = 0.0;
		try {
			Object o = get(key);
			String s = o == null ? "0" : o.toString().trim();
			try{
				s = CommonService.replaceMatchingValueRegexFromData(RegexType.DOUBLE, s);
				n = Double.parseDouble(s);
			} catch(NumberFormatException e){
				LoggingService.error(getClass(), e.getClass()+" for parsing data. Because value is not a number format. Key : "+key+" / Data : "+o, e);
				throw new NumberFormatException();
			} catch(ParseException e){
				LoggingService.error(getClass(), e.getClass()+" for parsing data. Key : "+key+" / Data : "+o, e);
				throw new ParseException(e.getPosition(), e.getMessage());
			} catch(Exception e){
				LoggingService.error(getClass(), e.getClass()+" for parsing data. Key : "+key, e);
				return 0.0;
			}
		} catch(NullPointerException e) {
			LoggingService.error(getClass(), e.getClass()+" for parsing data. Because DataMap has not value about key. Key : "+key, e);
			throw new NullPointerException();
		}
		return n;
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param key 가져올 데이터에 대한 key 문자열
	 * @return key에 해당하는 논리형 값. 예상치 못한 예외 발생 시 false 리턴
	 * @throws {@link NullPointerException} key에 해당하는 값이 없는 경우
	 * @throws {@link ParseException} key에 해당하는 값을 {@link Boolean}으로 parsing하지 못할 경우
	 */
	public boolean getBoolean(String key) throws NullPointerException, ParseException {
		boolean b = false;
		try {
			Object o = get(key);
			try{
				b = Boolean.parseBoolean(o.toString().trim());
			} catch(ParseException e){
				LoggingService.error(getClass(), e.getClass()+" for parsing data. Key : "+key+" / Data : "+o, e);
				throw new ParseException(e.getPosition(), e.getMessage());
			} catch(Exception e){
				LoggingService.error(getClass(), e.getClass()+"for parsing data. Key : "+key, e);
				return false;
			}
		} catch(NullPointerException e) {
			LoggingService.error(getClass(), e.getClass()+" for parsing data. Because DataMap has not value about key. Key : "+key, e);
			throw new NullPointerException();
		}
		return b;
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param key 가져올 데이터에 대한 key 문자열
	 * @return key에 해당하는 파일 객체. 예상치 못한 예외 발생 시 null 리턴
	 * @throws {@link NullPointerException} key에 해당하는 값이 없는 경우
	 * @throws {@link ClassCastException} key에 해당하는 값을 {@link File}객체로 casting하지 못할 경우
	 */
	public File getFile(String key) throws NullPointerException, ClassCastException {
		File f = null;
		try {
			Object o = get(key);
			try{
				f = (File)o;
			} catch(ClassCastException e){
				LoggingService.error(getClass(), e.getClass()+" for cast File from Object. Key : "+key, e);
				throw new ClassCastException();
			} catch(Exception e){
				LoggingService.error(getClass(), e.getClass()+" for parsing data. Key : "+key, e);
				return null;
			}
		} catch(NullPointerException e) {
			LoggingService.error(getClass(), e.getClass()+" for parsing data. Because DataMap has not value about key. Key : "+key, e);
			throw new NullPointerException();
		}
		return f;
	}
}
