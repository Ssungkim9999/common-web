package com.ssungkim.web.service;

import org.apache.commons.io.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.*;
import org.mozilla.universalchardet.*;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FileService {
	
	private static Class<FileService> thisClass = FileService.class;

	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param file 저장 파일
	 * @param extension 저장 파일 확장자
	 * @return 파일에 있는 모든 데이터가 저장된 {@code List<List<String>>} 객체 리턴
	 */
	public static List<List<String>> parsingFromFile(File file, String extension) {
		String fileName = file.getName();
		long time = System.currentTimeMillis();
		LoggingService.info(thisClass, "Start read to file. File name : "+fileName+"."+extension);
		List<List<String>> returnList = new ArrayList<List<String>>();
		List<String> valueList = new ArrayList<String>();
		Workbook wb = null;
		InputStreamReader isr = null;
		FileInputStream fis = null;
		BufferedReader br = null;
		try {
			if("xls".equalsIgnoreCase(extension)) {
				wb = WorkbookFactory.create(file);
				Sheet sheet = wb.getSheetAt(0);
				int totalRow = sheet.getLastRowNum()+1;
				if(totalRow == 0) return new ArrayList<List<String>>();
				int cellCnt = sheet.getRow(0).getLastCellNum();
				for(int i=0; i<totalRow; i++) {
					valueList = new ArrayList<String>();
					Row row = sheet.getRow(i);
					for(int j=0; j<cellCnt; j++) {
						if(row == null) valueList.add("");
						else valueList.add(getCellData(i, row.getCell(j)));
					}
					returnList.add(valueList);
				}
			} else if("xlsx".equalsIgnoreCase(extension)) {
				ExcelSheetHandler excelSheetHandler = ExcelSheetHandler.readExcel(file);
				returnList = excelSheetHandler.getDataList();
			} else if("csv".equalsIgnoreCase(extension) || "txt".equalsIgnoreCase(extension)) {
				String encType = getFileEncType(file);
				fis = new FileInputStream(file);
				if(encType == null) isr = new InputStreamReader(fis, "euc-kr");
				else isr = new InputStreamReader(fis, encType);
				br = new BufferedReader(isr);
				String line = "";
				List<String> rowList = new ArrayList<String>();
				while((line = br.readLine()) != null) rowList.add(line);
				int rowSize = rowList.size();
				int varCnt = 0;
				for(int index=0; index<rowSize; index++) {
					String s = rowList.get(index);
					String[] token = null;
					if("csv".equals(extension)) token = s.split(",");
					else token = s.split("\t");
					int tokenLen = token.length;
					if(varCnt == 0) varCnt = tokenLen;
					valueList = new ArrayList<String>();
					for(int i=0; i<tokenLen; i++) valueList.add(token[i]);
					if(varCnt != tokenLen) {
						for(int i=0; i<varCnt-tokenLen; i++) valueList.add("");
					}
					returnList.add(valueList);
				}
			}
			LoggingService.info(thisClass, "Success read to file. File name : "+fileName+"."+extension+" / Time : "+(System.currentTimeMillis()-time)/1000.0+"s");
			return returnList;
		} catch(Exception e) {
			LoggingService.error(thisClass, e.getClass()+" for parsing data from file ... file : " + fileName, e);
			return null;
		} finally {
			try { if(wb != null) wb.close(); } catch(Exception e) { LoggingService.error(thisClass, e.getClass()+" to close WorkBook.", e); }
			try { if(isr != null) isr.close(); } catch(Exception e) { LoggingService.error(thisClass, e.getClass()+" to close InputStreamReader.", e); }
			try { if(fis != null) fis.close(); } catch(Exception e) { LoggingService.error(thisClass, e.getClass()+" to close FileInputStream.", e); }
			try { if(br != null) br.close(); } catch(Exception e) { LoggingService.error(thisClass, e.getClass()+" to close BufferedReader.", e); }
		}
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param row 엑셀 행 번호
	 * @param cell 엑셀 열 번호
	 * @return row와 cell에 해당하는 cell에서 읽어온 문자열 리턴
	 */
	private static String getCellData(int row, Cell cell) {
		if(cell == null) return "";
		String imsi = "";
		if(!"".equals(cell.toString())) {
			DataFormatter df = new DataFormatter();
			int cellFormat = cell.getCellStyle().getDataFormat();
			if(row == 0) cellFormat = 49;
			switch (cellFormat) {
			case 0: // 일반 [abcd1234]
			case 2: // 사용자 지정(0.00) -> total.00
			case 4: // 통화 [123,456,789.00]
			case 9: // 백분율 [123%]
			case 12: // 분수 [1234 ]
			case 14: // 날짜 [yyyy-MM-dd]
			case 42: // 회계 [ ₩12,345 ]
			case 49: // 텍스트 [abcd1234]
			case 176: // 숫자 [12345 ]
			case 177: // 통화 [₩44,511 ]
			case 184: // 사용자 지정 [0##-####-####] -> ["total-2#"-n#-n#]
			case 186: // 기타(전화번호_국번 4자리) [(01) 2345-6789] -> [("total-8") 4-4]
			case 187: // 기타(전화번호_국번 3자리) [(012) 345-6789] -> [("total-7") 3-4]
			case 191: // 숫자 [123,456,789,00]
				if(HSSFDateUtil.isInternalDateFormat(cellFormat)) {
					String dateFmt = "";
					if(cellFormat == 14) dateFmt = "yyyy-MM-dd";
					else if(cellFormat == 21) dateFmt = "HH:mm:ss";
					else if(cellFormat == 165) dateFmt = "m/d/yy";
					else if(cellFormat == 166) dateFmt = "d-mmm-yy";
					else if(cellFormat == 167) dateFmt = "mmmm d yyyy ";
					else if(cellFormat == 168) dateFmt = "m/d/yyyy";
					else if(cellFormat == 169) dateFmt = "d-mmm-yyyy";
					imsi = new SimpleDateFormat(dateFmt).format(cell.getDateCellValue());
				} else imsi = df.formatCellValue(cell);
				break;
			case 181: // 날짜 [yyyy년 MM월 dd일 E요일]
				imsi = new SimpleDateFormat("yyyy년 M월 d일 E").format(cell.getDateCellValue());
				String s = imsi.substring(0, imsi.indexOf("일")+2);
				String t = imsi.substring(imsi.indexOf("일")+2);
				String rpl = "";
				if("MON".equalsIgnoreCase(t)) rpl = "월요일";
				else if("TUE".equalsIgnoreCase(t)) rpl = "화요일";
				else if("WED".equalsIgnoreCase(t)) rpl = "수요일";
				else if("THU".equalsIgnoreCase(t)) rpl = "목요일";
				else if("FRI".equalsIgnoreCase(t)) rpl = "금요일";
				else if("SAT".equalsIgnoreCase(t)) rpl = "토요일";
				else if("SUN".equalsIgnoreCase(t)) rpl = "일요일";
				else if(t.indexOf("요일") >= 0) rpl = t;
				else rpl = t + "요일";
				imsi = s + rpl;
				break;
			case 182: // 시간 [오전 HH:mm:ss]
				imsi = new SimpleDateFormat("a hh:mm:ss").format(cell.getDateCellValue());
				if(imsi.startsWith("AM")) imsi = imsi.replace("AM", "오전");
				else if(imsi.startsWith("PM")) imsi = imsi.replace("PM", "오후");
				break;
			case 183: // 지수 [1.E+01]
				imsi = df.formatCellValue(cell);
				imsi = imsi.substring(0, imsi.indexOf("E")+1)+imsi.substring(imsi.indexOf("E")+1);
				break;
			case 185: // 기타(우편번호) [123-456] -> ["total-3"-3]
				imsi = df.formatCellValue(cell);
				int len = imsi.length();
				imsi = imsi.substring(0, len - 3).replace("-", "")+"-"+imsi.substring(len-3);
				break;
			case 188: // 기타(주민등록번호) [000000-0000000] -> ["total-7"-7]
				imsi = df.formatCellValue(cell);
				int len2 = imsi.length();
				imsi = imsi.substring(0, len2-7).replace("-", "")+"-"+imsi.substring(len2-7);
				break;
//			case 189:		// 기타(숫자-한자) [一千二百三十四]
//				break;
//			case 190:		// 기타(숫자-한글) [일천이백삼십사]
//				break;
			default:
				imsi = df.formatCellValue(cell);
			}
		}
		return imsi.trim();
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param file 저장 파일
	 * @return 파일 인코딩 문자열 리턴
	 */
	private static String getFileEncType(File file) {
		byte[] buf = new byte[4096];
		FileInputStream fis = null;
		String encoding = null;
		try {
			fis = new FileInputStream(file);
			UniversalDetector detector = new UniversalDetector(null);

			int read;
			while ((read = fis.read(buf)) > 0 && !detector.isDone()) detector.handleData(buf, 0, read);
			detector.dataEnd();
			encoding = detector.getDetectedCharset();
		} catch(Exception e) {
			LoggingService.error(thisClass, e.getClass()+" to read File.", e);
		} finally {
			try { if(fis != null) fis.close(); } catch (Exception e) { LoggingService.error(thisClass, e.getClass()+" to close FileInputStream.", e); }
		}
		return encoding;
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.0.0
	 * @param wb SXSSFWorkbook
	 * @param valueData 엑셀에 쓰기 위한 데이터가 포함된 {@code List<List<String>>} 객체
	 */
	public static void createXlsx(SXSSFWorkbook wb, List<List<String>> valueData) {
		List<String> valueRow = new ArrayList<String>();
		int valueRowNum = 0;
		try {
			SXSSFSheet sheet = wb.createSheet("sheet1");
			Font font = wb.createFont();
			CellStyle style = wb.createCellStyle();
			DataFormat format = wb.createDataFormat();
			
			font.setFontHeightInPoints((short)12);
			font.setFontName("맑은 고딕");
			style.setFont(font);
			style.setDataFormat(format.getFormat("@"));
			
			List<String> titleData = valueData.get(0);
			int titleCnt = titleData == null ? 0 : titleData.size();
			for(int i=0; i<titleCnt; i++) {
				sheet.setDefaultColumnStyle(i, style);
				sheet.setColumnWidth(i, 5000);
			}
			
			Row row = sheet.createRow(0);
			for(int i=0; i<titleCnt; i++) {
				Cell cell = row.createCell(i);
				cell.setCellStyle(style);
				cell.setCellValue(titleData.get(i));
			}
			
			int valueCnt = valueData.size();
			int columnCnt = valueData.get(0).size();
			for(int i=1; i<valueCnt; i++) {
				valueRowNum = i;
				row = sheet.createRow(valueRowNum);
				valueRow = valueData.get(i);
				int cellIndex = 0;
				for(int j=0; j<columnCnt; j++) {
					Cell cell = row.createCell(cellIndex++);
					cell.setCellStyle(style);
					cell.setCellValue(valueRow.get(j));
				}
			}
		} catch(Exception e) {
			LoggingService.error(thisClass, e.getClass()+" for create xlsx. Exception Row : "+valueRowNum+" / Data : "+valueRow, e);
		}
	}
	
	/**
	 * @author Aiden Kim
	 * @since v1.1.0
	 * @param dataList 텍스트 파일에 한줄씩 입력될 {@code List<String>} 객체
	 * @param filePath 텍스트 파일이 저장될 경로와 파일명
	 * @return {@code List<String>} 객체의 데이터가 저장된 파일 객체
	 */
	public static File createTextFile(List<String> dataList, String filePath) {
		ClassPathResource resource = new ClassPathResource(filePath);
		File f = new File(resource.getPath());
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			FileUtils.touch(f);
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
			for(String s : dataList) bw.write(s+"\n");
			bw.flush();
		} catch(Exception e) {
			LoggingService.error(thisClass, e.getClass()+" for create text file.", e);
		} finally {
			try { if(fw != null) fw.close(); } catch(Exception e) { LoggingService.error(thisClass, e.getClass()+" for close FileWriter.", e); }
			try { if(bw != null) bw.close(); } catch(Exception e) { LoggingService.error(thisClass, e.getClass()+" for close BufferedWriter.", e); }
		}
		return f;
	}
}
