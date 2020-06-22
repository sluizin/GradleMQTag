package com.maqiao.was.fmktag.table;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
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
public final class DBInputStreamFileExcel extends DBAbstractInputStream implements InterfaceAccpetVal{
	/** 附加数值 */
	int sourceNumber = -1;
	/** 附加字符串 */
	String sourceChar = null;
	/** 当前页编号 */
	int sheetid = -1;
	/** 当前页名称 */
	String sheetname = null;

	@Override
	List<BeanLine> inputStreamToBeanlineList(InputStream is) {
		if (sourcefile==null || is == null ||(sheetid==-1 && sheetname==null)) {
			return new ArrayList<BeanLine>(0);
		}
		List<BeanLine> list = new ArrayList<BeanLine>(10);
		Workbook workbook = null;
		try {
			String fileextname=Utils.getFileExtName(sourcefile);
			if (fileextname.equals("xlsx")) {
				workbook = new XSSFWorkbook(is);
			} else if (fileextname.equals("xls")) {
				workbook = new HSSFWorkbook(is);
			} else {
				throw new Exception("读取的不是excel文件(类型[filetype]:xlsx/xls)");
			}
			System.out.println("file:"+sourcefile);
			Sheet sheet = Utils.getSheet(workbook, sheetid, sheetname,sourceNumber, sourceChar);
			if (sheet == null) return new ArrayList<BeanLine>(0);
			System.out.println(sheet.getSheetName() + "  sheet");
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				BeanLine e = new BeanLine();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					e.setUnit(Utils.getCellValueByCell(cell));
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
	 * 数据源excel 构造函数
	 * @param request HttpServletRequest
	 * @param params Map
	 */
	public DBInputStreamFileExcel(HttpServletRequest request, Map params) {
		super(request, params);
		super.acceptVal();
		acceptVal();
	}

	@Override
	public void acceptVal() {
		sheetid = getInt(-1,"sheetid");
		sheetname = getString("sheetname");
	}

	@Override
	void changeSourcefile() {
		//if(sourcefile.indexOf(".xlsx")>-1)filetype="xlsx";
		//if(sourcefile.indexOf(".xls")>-1)filetype="xls";
		splitFile(sourcefile);
	}
	/**
	 * 分解链接地址得到主文件地址以及附属的信息，以#进行区分。
	 * @param file String
	 */
	void splitFile(String file) {
		sourcefile=Utils.getSource(file);
		sourceNumber=Utils.getNumber(file);
		sourceChar=Utils.getChar(file);
	}

	@Override
	String[] getAutoSearchFilesExt() {
		String[] arr= {"xlsx","xls"};
		return arr;
	}
}
