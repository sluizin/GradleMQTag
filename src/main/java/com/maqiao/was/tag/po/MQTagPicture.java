/**
 * 
 */
package com.maqiao.was.tag.po;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public abstract class MQTagPicture extends SimpleTagSupport {
	protected Integer pictype = 0;
	JspContext pc;

	public JspContext getJspContext() {
		return pc;
	}

	public void setJspContext(JspContext pc) {
		this.pc = pc;
	}

	JspTag parent;

	@Override
	public void setParent(JspTag parent) {
		this.parent = parent;
		this.basic = (MQTagBasic) parent;
	}

	@Override
	//当遇到标签时就会执行这个方法
	public void doTag() throws JspException, IOException {
		//pc.getOut().write(basic.toHtmlString());
		pc.getOut().write(toHtmlString());
	}

	abstract String toHtmlString();

	MQTagBasic basic;

	public void getHtmlInputElement(StringBuilder sb, String key, Object obj) {
		basic.getHtmlInputElement(sb, key, obj);
	}

}
