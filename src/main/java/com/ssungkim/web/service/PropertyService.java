package com.ssungkim.web.service;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class PropertyService {

	private static Class<PropertyService> thisClass = PropertyService.class;
	
	/**
	 * @since v1.0.0
	 * @param file 확장자를 포함한 저장된 파일 명
	 * @param directory 저장된 파일 경로
	 * @param key 파일에서 가져올 key 문자열
	 * @return 파일에서 key에 해당하는 문자열 값. 해당하는 값이 없을경우 빈 문자열 리턴
	 */
	public static String getProperty(String file, String directory, String key) {
		Properties properties = new Properties();
		FileInputStream fis = null;
		try {
			String path = thisClass.getResource("/").getPath()+"/"+directory+"/";
			File f = new File(path, FilenameUtils.getName(file));
			fis = new FileInputStream(f);
			properties.load(fis);
			return new String(properties.getProperty(key).getBytes("ISO-8859-1"), "UTF-8");
		} catch(UnsupportedEncodingException e) {
			LoggingService.error(thisClass, e.getClass()+" during encode to UTF-8.", e);
			return properties.getProperty(key);
		} catch (Exception e) {
			LoggingService.error(thisClass, e.getClass()+" for get "+key+" from "+directory+"/"+file, e);
			return properties.getProperty(key);
		} finally {
			try { if(fis != null) fis.close(); } catch(Exception e) { LoggingService.error(thisClass, e.getClass()+" for close FileInputStream.", e); }
		}
	}
}
