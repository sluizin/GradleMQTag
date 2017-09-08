/**
 * 
 */
package com.maqiao.was.tag.table;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public class MQTableSourceUrl extends MQAbstractTable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 远程文件名，一般为 http://xxxx/yy.txt
	 */
	private String url = null;
	/**
	 * 行换标志
	 */
	private String linesign = "\n";
	/**
	 * 列标志
	 */
	private String columnsign = "\t";
	/**
	 * 是否需要修改信息编码 "iso-8859-1 to utf-8"
	 */
	private String codechange = null;

	/*
	 * (non-Javadoc)
	 * @see com.maqiao.was.tag.table.MQInterfaceTable#getDataTable()
	 */
	@Override
	public List<String[]> getDataTable() {
		try {
			String content = MQTTUtils.readFile(new URL(url), "\n", false).toString();
			if (codechange == null || codechange.length() == 0) content = MQTTUtils.changeUtf8(content);
			else content = MQTTUtils.autoChange(content, codechange);
			if (content == null || content.length() == 0) return null;
			String[] array = content.split(linesign);
			List<String[]> list = new ArrayList<String[]>(array.length);
			for (int i = 0; i < array.length; i++)
				list.add(array[i].split(columnsign));
			return list;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public final String getUrl() {
		return url;
	}

	public final void setUrl(String url) {
		this.url = url;
	}

	public final String getLinesign() {
		return linesign;
	}

	public final void setLinesign(String linesign) {
		this.linesign = linesign;
	}

	public final String getColumnsign() {
		return columnsign;
	}

	public final void setColumnsign(String columnsign) {
		this.columnsign = columnsign;
	}

	public final String getCodechange() {
		return codechange;
	}

	public final void setCodechange(String codechange) {
		this.codechange = codechange;
	}


}
