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
	 * 远程文件名，一般为 http://xxxx/yy.txt
	 */
	private String url = null;
	/**
	 * 行换标志
	 */
	private String linesign = MQTTConst.ACC_Enter;
	/**
	 * 列标志
	 */
	private String columnsign = "\t";

	/*
	 * (non-Javadoc)
	 * @see com.maqiao.was.tag.table.MQInterfaceTable#getDataTable()
	 */
	@Override
	public List<String[]> getDataTable() {
		try {
			String content = MQTTUtils.readFile(new URL(url), "\n", false).toString();
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
}
