/**
 * 
 */
package com.maqiao.was.tag.table;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public class MQTagIf extends MQAbstractTest {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int doStartTag() throws JspException {
		//System.out.println(MQTTUtils.getPrefix(this)+"IF--doStartTag:" + this.toString2());
		return BodyTag.EVAL_BODY_BUFFERED;
	}

	@Override
	public int doAfterBody() throws JspException {
		condition = getConditionTest();
		//System.out.println(MQTTUtils.getPrefix(this)+"IF--doAfterBody\tcondition[" + condition + "]\tdata[" + data + "]");
		if (!condition || data == null) return SKIP_BODY;
		Object[] arr = data.getDataArray();
		//System.out.println("this.bodyContent.getRemaining():"+ getBufferString());
		write(getContentString(arr));
		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {
		//System.out.println(MQTTUtils.getPrefix(this)+"IF--doEndTag:" + this.toString2());
		return super.doEndTag();
	}

	public String toString2() {
		StringBuilder builder = new StringBuilder();
		builder.append("MQTagIf[" + this.toString() + "] [" + getBufferString() + "]");
		return builder.toString();
	}

	public String getBufferString() {
		if (bodyContent == null) return "null";
		Reader reader = bodyContent.getReader();
		BufferedReader r = new BufferedReader(reader);
		StringBuilder b = new StringBuilder();
		String line;
		try {
			while ((line = r.readLine()) != null) /* { */
				b.append(line);
				/* b.append('\n');} */
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b.toString();
		/*
		if(content.length()==0)return content;
		return content.substring(0,content.length()-1);*/
	}

	@Override
	public void release() {

	}
}
