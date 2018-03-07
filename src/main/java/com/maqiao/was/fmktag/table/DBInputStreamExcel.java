package com.maqiao.was.fmktag.table;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.maqiao.was.fmktag.table.dbtxt.BeanLine;

/**
 * excel文件，通过poi进行读取
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@SuppressWarnings("rawtypes")
public class DBInputStreamExcel extends DBAbstractInputStream {
	/** 当前页编号 */
	int sheetid = -1;
	/** 当前页名称 */
	String sheetname = null;
	/** 判断excel的两种格式xls,xlsx */
	String filetype = null;

	@Override
	List<BeanLine> inputStreamToBeanlineList(InputStream is) {
		if (is == null || sheetid < 0) return new ArrayList<BeanLine>(0);
		List<BeanLine> list = new ArrayList<BeanLine>(10);
		Workbook workbook = null;
		try {
			if (filetype.toLowerCase().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(is);
			} else if (filetype.toLowerCase().endsWith("xls")) {
				workbook = new HSSFWorkbook(is);
			} else {
				throw new Exception("读取的不是excel文件(类型[filetype]:xlsx/xls)");
			}
			Sheet sheet = getSheet(workbook);
			if (sheet == null) return new ArrayList<BeanLine>(0);
			System.out.println(sheet.getSheetName() + "  sheet");
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				BeanLine e = new BeanLine();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					e.setUnit(getCellValueByCell(cell));
				}
				list.add(e);
			}

		} catch (Exception ee) {
			ee.printStackTrace();
		} finally {
			try {
				if (workbook != null) workbook.close();
				if (is != null) is.close();
			} catch (IOException ee) {
				ee.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * 通过下标或sheetName得到sheet
	 * @param workbook Workbook
	 * @return Sheet
	 */
	private Sheet getSheet(Workbook workbook) {
		int numberOfSheets = workbook.getNumberOfSheets();
		System.out.println("一共" + numberOfSheets + "个sheet");
		if (sheetid > -1 && sheetid < numberOfSheets) return workbook.getSheetAt(sheetid);
		if (sheetname != null && sheetname.length() > 0) return workbook.getSheet(sheetname);
		return null;
	}

	/**
	 * 获取单元格各类型值，返回字符串类型
	 * @param cell Cell
	 * @return String
	 */
	private static String getCellValueByCell(Cell cell) {
		//判断是否为null或空串
		if (cell == null || cell.toString().trim().equals("")) return "";
		CellType cellType = cell.getCellTypeEnum();
		if (cellType == null) return "";
		switch (cellType) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {  //判断日期类型
				/*
				 * Date ee = cell.getDateCellValue();
				 * return ee.toString();
				 * double dd = cell.getNumericCellValue();
				 * return String.valueOf(dd);
				 * return (new DecimalFormat("#.######").format(cell.getNumericCellValue()));
				 * cellValue = DateUtil.formatDateByFormat(cell.getDateCellValue(), "yyyy-MM-dd");
				 */
				return convertCellToString(cell);
			} else {
				double dd = cell.getNumericCellValue();
				return String.valueOf(dd);
			}
		case BOOLEAN:
			boolean bb = cell.getBooleanCellValue();
			return String.valueOf(bb);
		default:
			return "";
		}
	}

	/**
	 * 分多种格式解析单元格的值
	 * @param cell 单元格
	 * @return 单元格的值
	 */
	public static String convertCellToString(Cell cell) {
		/* 如果为null会抛出异常，应当返回空字符串 */
		if (cell == null) return "";
		/*
		 * POI对单元格日期处理很弱，没有针对的类型，日期类型取出来的也是一个double值，所以同样作为数值类型
		 * 解决日期2006/11/02格式读入后出错的问题，POI读取后变成“02-十一月-2006”格式
		 */
		if (cell.toString().contains("-") && checkDate(cell.toString())) {
			String ans = "";
			try {
				ans = new SimpleDateFormat("yyyy/MM/dd").format(cell.getDateCellValue());
			} catch (Exception e) {
				ans = cell.toString();
			}
			return ans;
		}
		cell.setCellType(CellType.STRING);
		return cell.getStringCellValue();
	}

	/**
	 * 判断是否是“02-十一月-2006”格式的日期类型
	 * @param str String
	 * @return boolean
	 */
	private static boolean checkDate(String str) {
		String[] dataArr = str.split("-");
		if (dataArr.length != 3) return false;
		try {
			int x = Integer.parseInt(dataArr[0]);
			String y = dataArr[1];
			int z = Integer.parseInt(dataArr[2]);
			if (x > 0 && x < 32 && z > 0 && z < 10000 && y.endsWith("月")) return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public DBInputStreamExcel() {

	}

	public DBInputStreamExcel(HttpServletRequest request, Map params) {
		this.request = request;
		this.params = params;
	}

	@Override
	void ProjectInitialization() {
		sheetid = getInt("sheetid");
		sheetname = getString("sheetname");
		filetype = getString("filetype");
	}
}
