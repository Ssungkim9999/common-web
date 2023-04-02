package com.ssungkim.web.service;

import com.ssungkim.model.RegexType;
import org.apache.commons.io.FilenameUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonService {
	
	private static Class<CommonService> thisClass = CommonService.class;
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param type {@link RegexType}
	 * @return 타입에 해당하는 정규식 문자열 리턴
	 * @throws IllegalArgumentException 올바르지 않은 {@link RegexType}이 파라미터로 들어올 경우
	 */
	private static String regex(RegexType type) throws IllegalArgumentException {
		String regex = "";
		if(type == RegexType.ID) regex = "[^a-zA-Z0-9_]";
		else if(type == RegexType.PASSWORD) regex = "[^a-zA-Z0-9!@#$%^&*?_~]";		// !@#$%^&*?_~
		else if(type == RegexType.EMAIL) regex = "^([\\S\\d]+)@(\\S+)[.](\\S+.?\\S+)$";
		else if(type == RegexType.EXPRESSION) regex = "(&gt;)|(&lt;)|(&amp;)|(&quot;)|(&#39;)";		// > < & " '
		else if(type == RegexType.INTEGER) regex = "[^0-9-]";
		else if(type == RegexType.DOUBLE) regex = "[^0-9.-]";
		else throw new IllegalArgumentException();
		return regex;
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param type {@link RegexType}
	 * @param data 정규식을 체크하기 위한 값
	 * @return 타입에 해당하는 정규식에 매칭되는 단어가 있는지 확인. 매칭되는 단어가 있을 경우 -1 리턴, 없을 경우 0 리턴. 만약 타입이 2 또는 3인 경우 매칭되는 단어가 있을 경우 0 리턴, 없을 경우 -1 리턴
	 * @throws IllegalArgumentException 올바르지 않은 {@link RegexType}이 파라미터로 들어올 경우
	 */
	public static int checkMatchingRegexFromDaata(RegexType type, String data) throws IllegalArgumentException {
		try{
			Pattern pattern = Pattern.compile(regex(type));
			Matcher matcher = pattern.matcher(data);
			int result = 0;
			while(matcher.find()) {
				result = -1;
				break;
			}
			return result;
		} catch(IllegalArgumentException e){
			LoggingService.error(thisClass, e.getClass()+" for matching regex from data. RegexType : "+type+" / Data : "+data, e);
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param type {@link RegexType}
	 * @param data 정규식을 체크하기 위한 값
	 * @return 타입에 해당하는 정규식에 매칭되는 단어를 빈 문자열로 치환한 문자열 리턴
	 * @throws IllegalArgumentException 올바르지 않은 {@link RegexType}이 파라미터로 들어올 경우
	 */
	public static String replaceMatchingValueRegexFromData(RegexType type, String data) throws IllegalArgumentException {
		try{
			Pattern pattern = Pattern.compile(regex(type));
			Matcher matcher = pattern.matcher(data);
			while(matcher.find()) {
				if(type == RegexType.EXPRESSION) data = data.replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&").replace("&quot;", "\"").replace("&#39;", "'");
				else data = data.replace(matcher.group(), "");
			}
			return data;
		} catch(IllegalArgumentException e){
			LoggingService.error(thisClass, e.getClass()+" for matching regex from data. RegexType : "+type+" / Data : "+data, e);
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param type {@link RegexType}
	 * @param data 정규식을 체크하기 위한 값
	 * @return data에 타입에 해당하는 정규식에 매칭되는 단어가 있을경우 해당 단어 리턴, 없을 경우 빈 문자열 리턴
	 * @throws IllegalArgumentException 올바르지 않은 {@link RegexType}이 파라미터로 들어올 경우
	 */
	public static String getInvalidRegexValueFromData(RegexType type, String data) throws IllegalArgumentException {
		try{
			Pattern pattern = Pattern.compile(regex(type));
			StringBuilder sb = new StringBuilder();
			Matcher matcher = pattern.matcher(data);
			while(matcher.find()) {
				String s = matcher.group();
				sb.append(s);
			}
			return sb.toString();
		} catch(IllegalArgumentException e){
			LoggingService.error(thisClass, e.getClass()+" for matching regex from data. RegexType : "+type+" / Data : "+data, e);
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param request {@link HttpServletRequest}
	 * @return request의 header에서 X-FORWARDED-FOR, Proxy-Client-IP, WL-Proxy-Client-IP, getRemoteAddr()의 값을 이용해 IP 문자열 리턴. IP가 로컬호스트에 해당하는 IP일 경우 개발 공용 IP 문자열 리턴
	 */
	public static String getClientIP(HttpServletRequest request) {
		String ip = request.getHeader("X-FORWARDED-FOR"); 
		if(ip == null || ip.length() == 0) ip = request.getHeader("Proxy-Client-IP");
		if(ip == null || ip.length() == 0) ip = request.getHeader("WL-Proxy-Client-IP");
		if(ip == null || ip.length() == 0) ip = request.getRemoteAddr();
		if(ip.indexOf(",") >= 0) ip = ip.substring(ip.indexOf(",")+1);
		return ip;
	 }
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param response {@link HttpServletResponse}
	 * @param directory 저장 파일 경로
	 * @param name 저장 파일 이름
	 */
	@SuppressWarnings("DefaultCharset")
	public static void downloadFile(HttpServletResponse response, String directory, String name) {
		File f = new File(directory, FilenameUtils.getName(name));
		FileInputStream fis = null;
		ServletOutputStream sos = null;
		try {
			if(f != null) {
				fis = new FileInputStream(f);
				sos = response.getOutputStream();
				response.reset();
				response.setContentType("application/file; charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Content-Disposition", "Attachment;Filename=\""+new String(f.getName().getBytes(), "ISO-8859-1")+"\"");
				
				byte[] outputByte = new byte[4096];
				int read = 0;
				while((read = fis.read(outputByte)) != -1 ){
					sos.write(outputByte , 0, read);
				}
				fis.close();
				sos.flush();
				sos.close();
			}
		} catch(Exception e) {
			LoggingService.error(thisClass, "Exception for download file. File name : "+name, e);
		} finally {
			try { if(fis != null) fis.close(); } catch(Exception e) { LoggingService.error(thisClass, e.getClass()+" for close FileInputStream.", e); }
			try { if(sos != null) sos.close(); } catch(Exception e) { LoggingService.error(thisClass, e.getClass()+" for close ServletOutputStream.", e); }
		}
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param e {@link Exception}
	 * @param type DB 쿼리 타입. ex) select, insert 등
	 * @param query xml 파일 등에 정의된 DB 쿼리 명, ID 등
	 * @return DB 처리 에러 로깅을 위한 {@link String} value 리턴
	 */
	public static String stringForDbException(Exception e, String type, String query) {
		return e.getClass()+" for "+type+" data for ["+query+"]";
	}
}
