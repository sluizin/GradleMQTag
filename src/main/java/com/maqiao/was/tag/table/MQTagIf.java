/**
 * 
 */
package com.maqiao.was.tag.table;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public class MQTagIf extends MQAbstractBody {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 条件 v1=='test' || v2==v1+'st'
	 */
	String test = null;

	/**
	 * 对字符串数组过滤
	 * @param array String[]
	 * @return boolean
	 */
	public boolean isFilter() {
		if (data == null) return false;
		Object[] arr = data.getDataArray();
		if (arr == null) return false;
		return MQTTUtils.test(test, arr);
	}

	@Override
	public int doStartTag() throws JspException {
		System.out.println("\t\t\tIF--doStartTag");
		return BodyTag.EVAL_BODY_BUFFERED;
	}

	@Override
	public int doAfterBody() throws JspException {
		System.out.println("\t\t\tIF--doAfterBody:" + isFilter());
		if (!isFilter()) return SKIP_BODY;
		Object[] arr = data.getDataArray();
		write(getContentString(arr));
		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {
		System.out.println("\t\t\tIF--doEndTag:" + isFilter());
		return super.doEndTag();
	}

	@Override
	public void release() {

	}

	InterfaceData data = null;

	@Override
	public void setParent(Tag t) {
		if (t == null) return;
		if (t instanceof InterfaceData) {
			data = (InterfaceData) t;
		}
	}

	public final void setTest(String test) {
		this.test = test;
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
