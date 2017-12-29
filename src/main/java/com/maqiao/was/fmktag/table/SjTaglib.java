/**
 * 
 */
package com.maqiao.was.fmktag.table;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public final class SjTaglib  extends BodyTagSupport{

	private static final long serialVersionUID = 1L;
	
	private String http="";

	public final String getHttp() {
		return http;
	}

	public final void setHttp(String http) {
		this.http = http;
	}
	@Override
	public int doStartTag() throws JspException {
		try {
			
		} catch (Exception e) {
			System.out.println("标签取得数据有误");
		}
		return SKIP_BODY;
	}
	

}
