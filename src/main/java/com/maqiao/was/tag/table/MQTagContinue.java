/**
 * 
 */
package com.maqiao.was.tag.table;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public class MQTagContinue extends MQAbstractTest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int doStartTag() throws JspException {
		return SKIP_BODY;
	}

	@Override
	public int doAfterBody() throws JspException {
		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {
		condition = getConditionTest();
		if (!condition) return SKIP_BODY;
		InterfaceSetState e = MQTTUtils.getParentState(t);
		if (e == null) return SKIP_BODY;
		e.setState(EnumState.CONTINUE);
		return BodyTag.SKIP_BODY;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MQTagContinue [" + condition + "]");
		return builder.toString();
	}

}
