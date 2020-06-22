package com.maqiao.was.fmktag.table;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;

import com.maqiao.was.fmktag.table.dbtxt.BeanLine;

/**
 * 来自不同的字符串数据源，如文本,json,xml等，都要继承此类
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@SuppressWarnings("rawtypes")
public abstract class DBAbstractInputStreamCharacter extends DBAbstractInputStream implements InterfaceAccpetVal {

	public DBAbstractInputStreamCharacter(HttpServletRequest request, Map params) {
		super(request, params);
		super.acceptVal();
	}
	@Override
	public void acceptVal() {
		
	}


	/**
	 * 字符串转对象列表
	 * @param content String
	 * @return List<BeanLine>
	 */
	abstract List<BeanLine> StringToBeanlineList(final String content);

	@Override
	List<BeanLine> inputStreamToBeanlineList(InputStream is) {
		if (is == null) return new ArrayList<BeanLine>();
		String outString = "";
		try {
			outString = IOUtils.toString(is, encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Utils.autoChange(outString, autochange);
		if (outString == null || outString.length() == 0) return new ArrayList<BeanLine>();
		return StringToBeanlineList(outString);
	}

}
