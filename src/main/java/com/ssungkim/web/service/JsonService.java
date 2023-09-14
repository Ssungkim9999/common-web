package com.ssungkim.web.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class JsonService {

	private static final Class<JsonService> thisClass = JsonService.class;
	
	/**
	 * @since v1.0.0
	 * @param file 저장 파일 이름(확장자가 포함된 JSON 파일). ex) static/json/test.json
	 * @return {@link JsonObject} 파일에서 읽어온 데이터를 이용해 생성된 {@link JsonObject}
	 */
	public static JsonObject getJson(String file) {
		FileReader reader = null;
		JsonObject jsonObject = new JsonObject();
		try {
			ClassPathResource cpr = new ClassPathResource(file);
			byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
			String jsonTxt = new String(bdata, StandardCharsets.UTF_8);
			JsonParser parser = new JsonParser();
			jsonObject = (JsonObject)parser.parse(jsonTxt);
		} catch(Exception e) {
			LoggingService.error(thisClass, e.getClass()+" for make JsonObject that is data from "+file+".json.", e);
		} finally {
			try { if(reader != null) reader.close(); } catch(Exception e) { LoggingService.error(thisClass, e.getClass()+" for close FileReader.", e); }
		}
		return jsonObject;
	}
	
	/**
	 * @since v1.0.0
	 * @param jsonString JSON 형식으로 이루어진 문자열
	 * @return JSON 형식으로 이루어진 문자열에서 변환한 {@link JsonObject}
	 */
	public static JsonObject stringToJson(String jsonString) {
		try {
			return (JsonObject)new JsonParser().parse(jsonString);
		} catch(Exception e) {
			LoggingService.error(thisClass, e.getClass()+" for parsing json String. Data : "+jsonString, e);
			return new JsonObject();
		}
	}
	
	/**
	 * @since v1.0.0
	 * @param json HashMap 형식으로 변환할 JsonObject
	 * @return {@link JsonObject}의 데이터를 저장한 {@link HashMap<String, Object>}
	 */
	public static HashMap<String, Object> jsonToMap(JsonObject json) {
		Set<String> keySet = json.keySet();
		Iterator<String> it = keySet.iterator();
		HashMap<String, Object> map = new HashMap<>();
		while(it.hasNext()) {
			String key = it.next();
			Object value = json.get(key);
			map.put(key, value);
		}
		return map;
	}
}
