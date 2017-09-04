/**
 * 
 */
package com.maqiao.was.tag.table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public class MQAbstractBody extends BodyTagSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	PageContext pageContext;

	@Override
	public void setPageContext(PageContext pageContext) {
		this.pageContext = pageContext;
	}

	BodyContent bodyContent;

	@Override
	public void setBodyContent(BodyContent bodyContent) {
		this.bodyContent = bodyContent;
	}

	@Override
	public void doInitBody() {
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
			return MQTTUtils.contentChange(content,(String[])arr);//content, MQTTUtils.changeArray(arr)
		} catch (Exception e) {
			e.printStackTrace();
			return content;
		}
	}

	/**
	 * 当isdynamic为真时，使用 "%{v0}" 显示内容
	 * @param array String[]
	 */
	void putAttribute(String[] array) {
		if (array == null) return;
		for (int i = 0; i < array.length; i++)
			pageContext.setAttribute("v" + i, array[i]);
	}

	void write(String content) {
		try {
			JspWriter jspWriter = ((BodyContent) pageContext.getOut()).getEnclosingWriter();
			jspWriter.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
