package com.ssungkim.web.service;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ViewService {

	/**
	 * @since v1.0.0
	 * @param viewName 확장자를 포함하지 않는 /WEB-INF 이후 JSP 파일 경로. 만약 이 파라미터가 null 이거나 빈 문자열일 경우 page 파라미터의 값으로 대체
	 * @param page 확장자를 포함하지 않는 /WEB-INF 이후 JSP 파일 경로
	 * @param param 페이지에 같이 전달될 {@link HashMap<String, Object>} 객체
	 * @param className 로깅 처리될 클래스
	 * @return 일반적인 페이지 이동에 사용. 파라미터와 페이지 정보가 포함된 {@link ModelAndView} 객체 리턴
	 * @throws NullPointerException 이동할 page가 없거나 올바르지 않은 경우
	 */
	@SuppressWarnings("YodaCondition")
	public static ModelAndView returnPage(String viewName, String page, HashMap<String, Object> param, Class<?> className) throws NullPointerException {
		ModelAndView mav = null;
		if(viewName == null || "".equals(viewName.trim())) {
			if(page == null || "".equals(page.trim())) {
				LoggingService.info(className, "Page can not empty.");
				throw new NullPointerException();
			}
			mav = new ModelAndView("/WEB-INF/"+page+".jsp");
		} else {
			mav = new ModelAndView("/WEB-INF/"+viewName+".jsp");
			mav.addObject("page", "/WEB-INF/"+page+".jsp");
		}
		LoggingService.info(className, "Direction page ::: "+page);
		if(param != null) {
			Set<String> set = param.keySet();
			Iterator<String> it = set.iterator();
			while(it.hasNext()) {
				String key = it.next();
				Object value = param.get(key);
				mav.addObject(key, value);
			}
		}
		return mav;
	}

	/**
	 * @since v1.0.0
	 * @param errorPage 확장자를 포함하지 않는 /WEB-INF 이후 JSP 에러 파일 경로. 만약 이 파라미터가 null 이거나 빈 문자열일 경우 PageNotFoundException 던짐
	 * @param errorCode HTTP 상태 코드
	 * @param param 페이지에 같이 전달될 {@link HashMap<String, Object>} 객체
	 * @param className 로깅 처리될 클래스
	 * @return 오류 페이지 이동에 사용. 파라미터와 페이지 정보가 포함된 {@link ModelAndView} 객체 리턴
	 * @throws NullPointerException 이동할 page가 없거나 올바르지 않은 경우
	 */
	@SuppressWarnings("YodaCondition")
	public static ModelAndView returnError(String errorPage, int errorCode, HashMap<String, Object> param, Class<?> className) throws NullPointerException {
		if(errorPage == null || "".equals(errorPage.trim())) {
			LoggingService.info(className, "ErrorPage can not empty.");
			throw new NullPointerException();
		}
		ModelAndView mav = new ModelAndView(errorPage);
		mav.addObject("errorCode", errorCode);
		LoggingService.info(className, "Error code ::: "+errorCode);
		return getModelAndView(param, mav);
	}

	/**
	 * @since v1.0.0
	 * @param page 확장자를 포함하지 않는 /WEB-INF 이후 JSP 파일 경로
	 * @param param 페이지에 같이 전달될 {@link HashMap<String, Object>} 객체
	 * @param className 로깅 처리될 클래스
	 * @return AJAX 호출 시 페이지 리턴에 사용. 파라미터와 페이지 정보가 포함된 {@link ModelAndView} 객체 리턴
	 * @throws NullPointerException 이동할 page가 없거나 올바르지 않은 경우
	 */
	@SuppressWarnings("YodaCondition")
	public static ModelAndView returnView(String page, HashMap<String, Object> param, Class<?> className) throws NullPointerException {
		if(page == null || "".equals(page.trim())) {
			LoggingService.info(className, "Page can not empty.");
			throw new NullPointerException();
		}
		ModelAndView mav = new ModelAndView("/WEB-INF/"+page+".jsp");
		LoggingService.info(className, "Direction page ::: "+page);
		return getModelAndView(param, mav);
	}

	private static ModelAndView getModelAndView(HashMap<String, Object> param, ModelAndView mav) {
		if(param != null) {
			Set<String> set = param.keySet();
			for (String key : set) {
				Object value = param.get(key);
				mav.addObject(key, value);
			}
		}
		return mav;
	}
}
