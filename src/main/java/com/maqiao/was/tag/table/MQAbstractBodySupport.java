package com.maqiao.was.tag.table;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.apache.taglibs.standard.tag.common.core.Util;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class MQAbstractBodySupport extends BodyTagSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	PageContext pageContext;

	@Override
	public void setPageContext(PageContext pageContext) {
		this.pageContext = pageContext;
	}

	public PageContext getPageContext() {
		return pageContext;
	}

	BodyContent bodyContent;

	@Override
	public void setBodyContent(BodyContent bodyContent) {
		this.bodyContent = bodyContent;
	}

	public BodyContent getBodyContent() {
		return bodyContent;
	}

	Tag t;

	@Override
	public void setParent(Tag t) {
		this.t = t;
	}

	@Override
	public Tag getParent() {
		return t;
	}

	@Override
	public void doInitBody() {
	}

	/**
	 * 保存对象位置<br>
	 * null : 1<br>
	 * request : 2<br>
	 * session : 3<br>
	 * application : 4<br>
	 */
	protected String scope;

	public void setScope(String scope) {
		this.scope = scope;
		this.scopeInt = Util.getScope(scope);
	}

	public String getScope() {
		return scope;
	}

	protected int scopeInt = 1;

	public int getScopeInt() {
		return scopeInt;
	}

	public void setScopeInt(int scopeInt) {
		this.scopeInt = scopeInt;
	}

}
