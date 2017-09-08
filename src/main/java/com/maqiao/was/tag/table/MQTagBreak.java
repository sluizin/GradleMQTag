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
public class MQTagBreak extends MQAbstractTest {

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
		//System.out.println(MQTTUtils.getPrefix(this)+"Break doEndTag:"+this.toString());
		if (!condition) return SKIP_BODY;
		InterfaceSetState e = MQTTUtils.getParentState(t);
		if (e != null) {
			e.setState(EnumState.BREAK);
			System.out.println("doEndTag     SKIP_PAGE");
			return BodyTag.SKIP_BODY;
		}
		return SKIP_BODY;
	}

	@Override
	public void release() {
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MQTagBreak ["+condition+"]");
		return builder.toString();
	}

}
