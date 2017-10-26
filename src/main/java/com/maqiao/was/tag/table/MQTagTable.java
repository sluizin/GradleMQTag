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
public class MQTagTable extends MQAbstractBody {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	/**
	 * 结果列表保存对象
	 */
	String datasave = null;
	/**
	 * 是否输出列表，如果为true，则为输出页面，如果为false，则跳过输出，只保存到session中，默认为true
	 */
	boolean isPrint = true;

	//当遇到标签时就会执行这个方法
	@Override
	public int doStartTag() throws JspException {
		return BodyTag.EVAL_BODY_BUFFERED;
	}

	/**
	 * 对字符串数组过滤
	 * @param array String[]
	 * @return boolean
	 */
	public boolean isFilter(String... array) {
		return MQTTUtils.test(test, array);
	}

	public final void setTest(String test) {
		this.test = test;
	}

	public final int getPsize() {
		return psize;
	}

	public final void setPsize(int psize) {
		this.psize = psize;
	}

	public final int getP() {
		return p;
	}

	public final void setP(int p) {
		this.p = p;
	}

	public final String getRange() {
		return range;
	}

	public final void setRange(String range) {
		this.range = range;
	}

	public final String getTest() {
		return test;
	}

	public void setDatasave(String datasave) {
		this.datasave = datasave;
	}

	public boolean isPrint() {
		return isPrint;
	}

	public void setPrint(boolean isPrint) {
		this.isPrint = isPrint;
	}

	public void setIsPrint(boolean isPrint) {
		this.isPrint = isPrint;
	}

	public String getDatasave() {
		return datasave;
	}

}
