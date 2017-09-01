/**
 * 
 */
package com.maqiao.was.tag.table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.Tag;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public abstract class MQAbstractTable extends BodyTagSupport implements DynamicAttributes {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PageContext pageContext;

	@Override
	public void setPageContext(PageContext pageContext) {
		System.out.println("setPageContext:");
		this.pageContext = pageContext;
	}

	List<String[]> list = null;
	int point = 0;
	StringBuilder sb = new StringBuilder();

	@Override
	public int doStartTag() throws JspException {
		System.out.println("doStartTag");
		if (list == null) list = getSelectList();
		pageContext.getRequest().setAttribute(c3, list.size() + "");
		if (list == null || list.size() == 0) { return SKIP_BODY; }
		if (isdynamic) { return BodyTag.EVAL_BODY_BUFFERED; }
		HttpSession session = pageContext.getSession();
		String username = (String) session.getAttribute("username");
		HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
		//返回BodyTag.EVAL_BODY_BUFFERED，表示输出标签体内容
		//返回Tag.SKIP_BODY,表示不输出内容
		return BodyTag.EVAL_BODY_BUFFERED;
	}

	@Override
	public void doInitBody() {
		System.out.println("doInitBody");
	}

	/**
	 * 把参数放入到标签内容中
	 */
	void putAttrHtml() {
		if (point >= list.size()) return;
		String[] array = list.get(point);
		putAttribute(array);
		try {
			bodyContent.clearBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 把转换后的标签内容存入StringBuilder中
	 */
	void getContent() {
		String content = bodyContent.getString();
		content = MQTTUtils.contentChange(content, list.get(point - 1));
		sb.append(content);
	}

	@Override
	public int doAfterBody() throws JspException {
		System.out.println("doAfterBody:" + point + "/" + list.size());
		if (!isdynamic) return BodyTag.SKIP_BODY;
		if (list.size() == 0 || point > list.size()) return SKIP_BODY;
		if (point > 0 && point <= list.size()) getContent();
		if (point < list.size()) putAttrHtml();
		point++;
		return EVAL_BODY_AGAIN;// 循环
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

	@Override
	public int doEndTag() throws JspException {
		System.out.println("doEndTag");
		if (bodyContent == null) return super.doEndTag();
		String content;
		if (isdynamic) {
			write(sb.toString());
			return super.doEndTag();
		}
		content = bodyContent.getString();
		String html = MQTTUtils.contentChange(content, list).toString();
		write(html);
		return super.doEndTag();
	}

	void write(String content) {
		try {
			JspWriter jspWriter = ((BodyContent) pageContext.getOut()).getEnclosingWriter();
			jspWriter.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void release() {
		System.out.println("release");

	}

	BodyContent bodyContent;

	@Override
	public void setBodyContent(BodyContent bodyContent) {
		System.out.println("setBodyContent");
		this.bodyContent = bodyContent;
	}

	/**
	 * 得到过滤的数据表
	 * @return List<String[]>
	 */
	private List<String[]> getFilterList() {
		List<String[]> list = getDataTable();
		pageContext.getRequest().setAttribute(c1, list.size() + "");
		for (int i = 0; i < list.size(); i++)
			if (!mqTagTable.isFilter(list.get(i))) list.remove(i--);
		return list;
	}

	/**
	 * 得到分页后的数据表
	 * @return List<String[]>
	 */
	private List<String[]> getSelectList() {
		List<String[]> list = getFilterList();
		pageContext.getRequest().setAttribute(c2, list.size() + "");
		if (mqTagTable == null) return list;
		List<String[]> newList = new ArrayList<String[]>();
		if (mqTagTable.range != null && mqTagTable.range.length() > 0) {
			if (MQTTUtils.isRange(mqTagTable.range)) for (int i = 0; i < list.size(); i++)
				if (MQTTUtils.isRange(mqTagTable.range, i)) newList.add(list.get(i));
			return newList;
		}
		if (mqTagTable.psize > 0 && mqTagTable.p >= 0) {
			int a = mqTagTable.p * mqTagTable.psize;
			if (a >= list.size()) return newList;
			int b = (mqTagTable.p + 1) * mqTagTable.psize;
			if (b >= list.size()) b = list.size();
			newList = list.subList(a, b);
			return newList;
		}
		return newList;
	}

	MQTagTable mqTagTable = null;

	public void setParent(Tag t) {
		System.out.println("setParent");
		if (t != null) mqTagTable = (MQTagTable) t;
	}

	private boolean isdynamic = false;

	public final boolean isIsdynamic() {
		return isdynamic;
	}

	public final void setIsdynamic(boolean isdynamic) {
		this.isdynamic = isdynamic;
	}

	private ArrayList<String> keys = new ArrayList<String>();
	private ArrayList<Object> values = new ArrayList<Object>();

	/*
	 * 设置动态属性(non-Javadoc)
	 * @see javax.servlet.jsp.tagext.DynamicAttributes#setDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public void setDynamicAttribute(String url, String localName, Object value) throws JspException {
		keys.add(localName);
		values.add(value);
	}

	/**
	 * 显示动态属性
	 */
	public void showDynamicAttribute() {
		if (bodyContent == null) return;
		JspWriter jspWriter = ((BodyContent) pageContext.getOut()).getEnclosingWriter();
		try {
			for (int i = 0; i < keys.size(); i++) {
				String key = keys.get(i);
				Object value = values.get(i);
				jspWriter.println("<li>" + key + "=" + value + "</li>");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String c1 = "countdata";
	private String c2 = "countfilter";
	private String c3 = "countcurrent";

	public final void setC1(String c1) {
		this.c1 = c1;
	}

	public final void setC2(String c2) {
		this.c2 = c2;
	}

	public final void setC3(String c3) {
		this.c3 = c3;
	}

	/**
	 * 提取数据
	 * @return List<String[]>
	 */
	public abstract List<String[]> getDataTable();

}
