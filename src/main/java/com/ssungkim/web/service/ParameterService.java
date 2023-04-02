package com.ssungkim.web.service;

import com.ssungkim.model.DataMap;
import com.ssungkim.model.RegexType;
import org.springframework.expression.ParseException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.NotDirectoryException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Enumeration;
import java.util.Iterator;

public class ParameterService {
	
	private static Class<ParameterService> thisClass = ParameterService.class;

	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param req {@link HttpServletRequest}
	 * @param key 파라미터 가져올 key 문자열
	 * @return key에 해당하는 문자열 값. 예상치 못한 예외 발생 시 null 리턴
	 * @throws NullPointerException 파라미터에 해당 key가 없을 경우
	 * @throws IllegalArgumentException 올바르지 않은 {@link RegexType}이 파라미터로 들어올 경우
	 */
	public static String getStringParameter(HttpServletRequest req, String key) throws NullPointerException, IllegalArgumentException {
		String s = null;
		try {
			s = req.getParameter(key);
			try {
				 s = CommonService.replaceMatchingValueRegexFromData(RegexType.EXPRESSION, req.getParameter(key).toString());
			} catch(Exception e) {
				LoggingService.error(thisClass, e.getClass()+" for parsing String data from request. Key : "+key+" / Data : "+req.getParameter(key), e);
				return null;
			}
		} catch(NullPointerException e) {
			LoggingService.error(thisClass, e.getClass()+". Because HttpServletRequest has not value about key. Key : "+key, e);
			throw new NullPointerException();
		}
		return s;
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param req {@link HttpServletRequest}
	 * @param key 파라미터 가져올 key 문자열
	 * @return key에 해당하는 정수형 값. 예상치 못한 예외 발생 시 0 리턴
	 * @throws NullPointerException 파라미터에 해당 key가 없을 경우
	 * @throws NumberFormatException key에 해당하는 값의 형식이 숫자 형식이 아닐 경우
	 * @throws ParseException key에 해당하는 값을 {@link Integer}로 parsing하지 못할 경우
	 * @see RegexType
	 */
	public static int getIntegerParameter(HttpServletRequest req, String key) throws NullPointerException, NumberFormatException, ParseException {
		int n = 0;
		try {
			String s = getStringParameter(req, key);
			try {
				s = CommonService.replaceMatchingValueRegexFromData(RegexType.INTEGER, s);
				n = Integer.parseInt(s);
			} catch(NumberFormatException e) {
				LoggingService.error(thisClass, e.getClass()+" for parsing data. Because value is not a number format. Key : "+key+" / Data : "+s, e);
				throw new NumberFormatException();
			} catch(ParseException e) {
				LoggingService.error(thisClass, e.getClass()+" for parsing data. Key : "+key+" / Data : "+s, e);
				throw new ParseException(e.getPosition(), e.getMessage());
			} catch(Exception e) {
				LoggingService.error(thisClass, e.getClass()+" for parsing Integer data from request. Key : "+key, e);
				return 0;
			}
		} catch(NullPointerException e) {
			LoggingService.error(thisClass, e.getClass()+" for parsing Integer data from request. Key : "+key+" / Data : "+req.getParameter(key), e);
			throw new NullPointerException();
		}
		return n;
	}

	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param req {@link HttpServletRequest}
	 * @param key 파라미터 가져올 key 문자열
	 * @return key에 해당하는 긴 정수형 값. 예상치 못한 예외 발생 시 0L 리턴
	 * @throws NullPointerException 파라미터에 해당 key가 없을 경우
	 * @throws NumberFormatException key에 해당하는 값의 형식이 숫자 형식이 아닐 경우
	 * @throws ParseException key에 해당하는 값을 {@link Long}으로 parsing하지 못할 경우
	 */
	public static long getLongParameter(HttpServletRequest req, String key) throws NullPointerException, NumberFormatException, ParseException {
		long n = 0L;
		try {
			String s = getStringParameter(req, key);
			try {
				n = Long.parseLong(s);
			} catch(NumberFormatException e) {
				LoggingService.error(thisClass, e.getClass()+" for parsing data. Because value is not a number format. Key : "+key+" / Data : "+s, e);
				throw new NumberFormatException();
			} catch(ParseException e) {
				LoggingService.error(thisClass, e.getClass()+" for parsing data. Key : "+key+" / Data : "+s, e);
				throw new ParseException(e.getPosition(), e.getMessage());
			} catch(Exception e) {
				LoggingService.error(thisClass, e.getClass()+" for parsing Long data from request. Key : "+key, e);
				return 0L;
			}
		} catch(NullPointerException e) {
			LoggingService.error(thisClass, e.getClass()+" for parsing Long data from request. Key : "+key+" / Data : "+req.getParameter(key), e);
			throw new NullPointerException();
		}
		return n;
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param req {@link HttpServletRequest}
	 * @param key 파라미터 가져올 key 문자열
	 * @return key에 해당하는 논리형 값. 예상치 못한 예외 발생 시 false 리턴
	 * @throws NullPointerException 파라미터에 해당 key가 없을 경우
	 * @throws ParseException key에 해당하는 값을 {@link Boolean}으로 parsing하지 못할 경우
	 */
	public static boolean getBooleanParameter(HttpServletRequest req, String key) throws NullPointerException, ParseException {
		boolean b = false;
		try {
			String s = getStringParameter(req, key);
			try {
				b = Boolean.parseBoolean(s);
			} catch(ParseException e) {
				LoggingService.error(thisClass, e.getClass()+" for parsing data. Key : "+key+" / Data : "+s, e);
				throw new ParseException(e.getPosition(), e.getMessage());
			} catch(Exception e) {
				LoggingService.error(thisClass, e.getClass()+" for parsing Boolean data from request. Key : "+key, e);
				return false;
			}
		} catch(NullPointerException e) {
			LoggingService.error(thisClass, e.getClass()+" for parsing Boolean data from request. Key : "+key+" / Data : "+req.getParameter(key), e);
			throw new NullPointerException();
		}
		return b;
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param req {@link HttpServletRequest}
	 * @param key 파라미터 가져올 key 문자열
	 * @return key에 해당하는 실수형 값. 예상치 못한 예외 발생 시 0.0 리턴
	 * @throws NullPointerException key에 해당하는 값이 없는 경우
	 * @throws NumberFormatException key에 해당하는 값의 형식이 숫자 형식이 아닐 경우
	 * @throws ParseException key에 해당하는 값을 {@link Double}로 parsing하지 못할 경우
	 * @see RegexType
	 */
	public static double getDoubleParameter(HttpServletRequest req, String key) throws NullPointerException, NumberFormatException, ParseException {
		double n = 0.0;
		try {
			String s =  getStringParameter(req, key);
			try {
				s = CommonService.replaceMatchingValueRegexFromData(RegexType.DOUBLE, s);
				n = Double.parseDouble(s);
			} catch(NumberFormatException e){
				LoggingService.error(thisClass, e.getClass()+" for parsing data. Because value is not a number format. Key : "+key+" / Data : "+s, e);
				throw new NumberFormatException();
			} catch(ParseException e){
				LoggingService.error(thisClass, e.getClass()+" for parsing data. Key : "+key+" / Data : "+s, e);
				throw new ParseException(e.getPosition(), e.getMessage());
			} catch(Exception e){
				LoggingService.error(thisClass, e.getClass()+" for parsing data. Key : "+key, e);
				return 0.0;
			}
		} catch(NullPointerException e) {
			LoggingService.error(thisClass, e.getClass()+" for parsing Double data from request. Key : "+key+" / Data : "+req.getParameter(key), e);
			throw new NullPointerException();
		}
		return n;
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param mreq {@link MultipartHttpServletRequest}
	 * @param key 파라미터 가져올 key 문자열
	 * @param directory 저장될 파일 경로
	 * @param name 저장될 파일 이름
	 * @return directory와 name으로 저장된 파일 객체 리턴. 예상치 못한 예외 발생 시 null 리턴
	 * @throws NullPointerException key에 해당하는 값이 없는 경우
	 * @throws NotDirectoryException directory가 없거나 경로가 아닐 경우
	 */
	public static File getRequestFile(MultipartHttpServletRequest mreq, String key, String directory, String name) throws NullPointerException, NotDirectoryException {
		MultipartFile file = mreq.getFile(key);
		File f = null;
		if(file == null) {
			LoggingService.info(thisClass, "MultipartHttpServletRequest has not file about key. Key : "+key);
			throw new NullPointerException();
		} else {
			try {
				f = new File(directory, name);
				file.transferTo(f);
				LoggingService.info(thisClass, "Success to make file about "+key+". Directory : "+directory+" / Name : "+name);
			} catch(NotDirectoryException e) {
				LoggingService.error(thisClass, e.getClass()+" for transfer file. Directory : "+directory, e);
				throw new NotDirectoryException(directory);
			} catch(Exception e) {
				LoggingService.error(thisClass, e.getClass()+" for transfer file. Directory : "+directory+" / Dame : "+name, e);
				return null;
			}
		}
		return f;
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param req {@link HttpServletRequest}
	 * @param key 파라미터 가져올 key 문자열
	 * @return key에 해당하는 문자열 값으로 이루어진 배열. 해당하는 파라미터 key가 없을 경우 길이가 0인 문자열 배열 리턴
	 * @throws NullPointerException key에 해당하는 값이 없는 경우
	 * @see RegexType
	 */
	public static String[] getRequestStringArray(HttpServletRequest req, String key) throws NullPointerException {
		String[] ar = req.getParameterValues(key);
		if(ar == null) {
			LoggingService.info(thisClass, "HttpServletRequest has not file about key. Key : "+key);
			throw new NullPointerException();
		} else {
			int len = ar.length;
			for(int i=0; i<len; i++) {
				String s = ar[i];
				s = CommonService.replaceMatchingValueRegexFromData(RegexType.EXPRESSION, s);
				ar[i] = s;
			}
		}
		return ar;
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param req {@link HttpServletRequest}
	 * @return 모든 파라미터의 key와 value으로 이루어진 {@link DataMap} 객체 리턴
	 */
	public static DataMap makeAllParameter(HttpServletRequest req) {
		DataMap param = new DataMap();
		Enumeration<String> keys = req.getParameterNames();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			Object value = req.getParameter(key);
			param.put(key, value);
		}
		return param;
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param req {@link HttpServletRequest}
	 * @return 모든 파라미터의 value를 포함한 {@link List<Object>} 객체 리턴
	 */
	public static List<Object> makeAllParameterToList(HttpServletRequest req) {
		List<Object> params = new ArrayList<Object>();
		DataMap map = makeAllParameter(req);
		Set<String> keySet = map.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()) params.add(map.get(it.next()));
		return params;
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param map 파라미터로 넘겨줄 데이터를 포함한 DataMap 객체
	 * @param page 현재 페이지
	 * @param count 총 데이터 개수
	 * @param countPerPage 한 페이지에 보여질 데이터 개수
	 * @return 파라미터로 받은 {@link DataMap} 객체에 페이징 처리를 위한 데이터가 계산된 내용을 포함한 {@link DataMap} 객체 리턴
	 */
	public static DataMap settingPageData(DataMap map, int page, int count, int countPerPage) {
		int first = page == 1 ? 0 : 1;
		int last = count%countPerPage == 0 ? count/countPerPage : count/countPerPage+1;
		int start = ((page-1)/10)*10+1;
		int end =  start+9 > last ? last : start+9;
		int prev = page < 10 ? 0 : start-1;
		int next = last > end ? end+1 : 0;
		if(count == 0) first = last = start = end = prev = next = page = 0;
		map.put("firstPage", first);
		map.put("lastPage", last);
		map.put("startPage", start);
		map.put("endPage", end);
		map.put("prevPage", prev);
		map.put("nextPage", next);
		map.put("nowPage", page);
		return map;
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param req {@link HttpServletRequest}
	 */
	public static void printAllParameters(HttpServletRequest req) {
		LoggingService.info(thisClass, "### Start Print All Parameters ###");
		Enumeration<String> enums = req.getParameterNames();
		while(enums.hasMoreElements()) {
			String key = enums.nextElement();
			Object value = req.getParameter(key);
			LoggingService.info(thisClass, "Key : "+key+" / Value : "+value);
		}
		LoggingService.info(thisClass, "### End Print All Parameters ###");
	}
}
