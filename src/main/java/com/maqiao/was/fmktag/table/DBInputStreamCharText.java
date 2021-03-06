package com.maqiao.was.fmktag.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.maqiao.was.fmktag.table.dbtxt.BeanLine;

/**
 * 数据源text
 * 参数:
 * type: <br>
 * 0:本地工程下的目录资源[与index.html同级]<br>
 * 1:外部资源路径文件[http://static.99114.com/static/zhuanti/XXXX/db/YYYY.txt] <br>
 * sourcefile: 资源文件路径与文件名 <br>
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@SuppressWarnings("rawtypes")
public final class DBInputStreamCharText extends DBAbstractInputStreamCharacter implements InterfaceAccpetVal {
	/** 行标志 */
	String rowsign = "\n";
	/** 列标志 */
	String columnsign = "\t";
	
	
	@Override
	List<BeanLine> StringToBeanlineList(final String content) {
		if (content == null || content.length() == 0) return new ArrayList<BeanLine>(0);
		final String[] splitStr = content.split(rowsign);
		BeanLine e = null;
		int len = splitStr.length;
		final List<BeanLine> BeanLineList = new ArrayList<BeanLine>(len);
		for (int i = 0; i < len; i++)
			if ((e = beanLineChange(splitStr[i],columnsign)) != null) BeanLineList.add(e);
		return BeanLineList;
	}

	/**
	 * 把一行，转成beanLine对象
	 * @param str String
	 * @return beanLine
	 */
	public static final BeanLine beanLineChange(final String str,final String columnsign) {
		if (str == null || str.length() == 0) return null;
		BeanLine bean = new BeanLine();
		bean.setSplit(str, columnsign);
		return bean;
	}
	/**
	 * 数据源text 构造函数
	 * @param request HttpServletRequest
	 * @param params Map
	 */
	public DBInputStreamCharText(HttpServletRequest request, Map params) {
		super(request,params);
		super.acceptVal();
		acceptVal();
	}

	@Override
	public void acceptVal() {
		rowsign=getStringDef("\n","rowsign");
		columnsign=getStringDef("\t","columnsign");
	}

	@Override
	void changeSourcefile() {
		
	}

	@Override
	String[] getAutoSearchFilesExt() {
		String[] arr= {"txt"};
		return arr;
	}

}
