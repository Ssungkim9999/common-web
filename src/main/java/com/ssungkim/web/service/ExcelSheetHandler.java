package com.ssungkim.web.service;

import org.apache.poi.ooxml.util.SAXHelper;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelSheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {
	
	private static final Class<ExcelSheetHandler> thisClass = ExcelSheetHandler.class;

	private int headerCol = 0;
	private int currentCol = 0;
	
	private List<List<String>> dataList = new ArrayList<List<String>>();
	private List<String> rowData = new ArrayList<String>();
		
	/**
	 * @since v1.0.0
	 * @param file 저장 파일
	 * @return ExcelSheetHandler 객체
	 */
	public static ExcelSheetHandler readExcel(File file) {
		ExcelSheetHandler sheetHandler = new ExcelSheetHandler();
		OPCPackage opc = null;
		InputStream is = null;
		try {
			opc = OPCPackage.open(file.getPath().toString(), PackageAccess.READ);
			XSSFReader reader = new XSSFReader(opc);
			SheetIterator it = (SheetIterator)reader.getSheetsData();
			StylesTable styles = reader.getStylesTable();
			ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(opc);

			is = it.next();
			InputSource source = new InputSource(is);

			ContentHandler handler = new XSSFSheetXMLHandler(styles, strings, sheetHandler, false);
			XMLReader xmlReader = SAXHelper.newXMLReader();
			xmlReader.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			xmlReader.setContentHandler(handler);
			xmlReader.parse(source);
		} catch (Exception e) {
			LoggingService.error(thisClass, e.getClass()+" for read file.", e);
		} finally {
			try { if(opc != null) opc.close(); } catch(Exception e) { LoggingService.error(thisClass, e.getClass()+" for close OPCPackage.", e); }
			try { if(is != null) is.close(); } catch(Exception e) { LoggingService.error(thisClass, e.getClass()+" for close InputStream.", e); }
		}
		return sheetHandler;
    }
	
	/**
	 * @since v1.0.0
	 * @return 파일에 있는 모든 셀 데이터가 저장된 {@code List<List<String>>} 객체 리턴
	 */
	public List<List<String>> getDataList(){
		return dataList;
	}

	@Override
	public void startRow(int rowNum) {
		currentCol = 0;
		rowData = new ArrayList<String>();
	}

	@Override
	public void endRow(int rowNum) {
		for(int i=0; i<headerCol-currentCol; i++) rowData.add("");
		if(headerCol == 0) headerCol = currentCol == rowData.size() ? currentCol : currentCol-1;
		dataList.add(rowData);
	}

	@Override
	public void cell(String cellReference, String formattedValue, XSSFComment comment) {
		CellReference cell = new CellReference(cellReference);
		int iCol = cell.getCol();
		int emptyCol = iCol - currentCol;
		
		if(emptyCol > 0) {
			for(int i=0; i<emptyCol; i++) rowData.add("");
			currentCol += emptyCol;
		}
		
		currentCol++;
		rowData.add(formattedValue);
	}
}
