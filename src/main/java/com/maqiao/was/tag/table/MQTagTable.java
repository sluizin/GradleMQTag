/**
 * 
 */
package com.maqiao.was.tag.table;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public class MQTagTable extends SimpleTagSupport {
	/**
	 * 条件 v1=='test' || v2==v1+'st'
	 */
	String test = null;
	/**
	 * 每页数量
	 */
	int psize = 10;
	/**
	 * 当前页
	 */
	int p = 0;
	/**
	 * 范围 0,1,5,8,9,12 或 1,5,7,9-15,20-25,5-2
	 */
	String range = null;

	@Override
	//当遇到标签时就会执行这个方法
	public void doTag() throws JspException, IOException {
		getJspBody().invoke(null);
	}
	/**
	 * 对字符串数组过滤
	 * @param array String[]
	 * @return boolean
	 */
	public boolean isFilter(String[] array){
		return MQTTUtils.test(array, test);
	}
	public final void setTest(String test) {
		this.test = test;
	}

}
