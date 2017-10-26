/**
 * 
 */
package com.maqiao.was.tag.table;

import java.io.IOException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class MQAbstractBody extends MQAbstractBodySupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 从request session application 提出数据
	 * @param key String
	 * @return Object
	 */
	public Object valueGet(String key) {
		if (this.pageContext == null || key == null || key.length() == 0) return null;
		if (scopeInt < 2) return null;
		return this.pageContext.getAttribute(key, scopeInt);
	}

	/**
	 * 从request session application 保存数据
	 * @param key String
	 * @param obj Object
	 */
	public void valueSave(String key, Object obj) {
		if (this.pageContext == null || key == null || key.length() == 0 || obj == null) return;
		if (scopeInt < 2) return;
		this.pageContext.setAttribute(key, obj, scopeInt);
	}


	/**
	 * 把内容提出
	 */
	String getContentString(String... arr) {
		String content = bodyContent.getString();
		if (arr == null) return content;
		return MQTTUtils.contentChange(content, arr);
	}

	/**
	 * 把内容提出
	 */
	String getContentString(Object... arr) {
		String content = bodyContent.getString();
		if (arr == null || arr.length == 0) return content;
		try {
			return MQTTUtils.contentChange(content, (String[]) arr);//content, MQTTUtils.changeArray(arr)
		} catch (Exception e) {
			e.printStackTrace();
			return content;
		}
	}

	/**
	 * 当isdynamic为真时，使用 "%{v0}" 显示内容
	 * @param array String[]
	 */
	void putAttribute(String... array) {
		if (array == null) return;
		for (int i = 0; i < array.length; i++)
			pageContext.setAttribute("v" + i, array[i]);
	}
	/**
	 * 输出到页面
	 * @param content String
	 */
	void write(String content) {
		try {
			JspWriter jspWriter = ((BodyContent) pageContext.getOut()).getEnclosingWriter();
			jspWriter.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
