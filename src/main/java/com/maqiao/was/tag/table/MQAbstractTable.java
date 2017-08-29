/**
 * 
 */
package com.maqiao.was.tag.table;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.Tag;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public abstract class MQAbstractTable extends BodyTagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PageContext pageContext;

	@Override
	public void setPageContext(PageContext pageContext) {
		this.pageContext = pageContext;
	}

	@Override
	public int doStartTag() throws JspException {
		//返回BodyTag.EVAL_BODY_BUFFERED，表示输出标签体内容
		//返回Tag.SKIP_BODY,表示不输出内容
		return BodyTag.EVAL_BODY_BUFFERED;
	}

	@Override
	public int doAfterBody() throws JspException {
		return BodyTag.SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {
		if (bodyContent == null) return super.doEndTag();
		String content = bodyContent.getString();
		List<String[]> list = getSelectList();
		try {
			String html = MQTTUtils.contentChange(content, list).toString();
			((BodyContent) pageContext.getOut()).getEnclosingWriter().println(html);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.doEndTag();
	}

	BodyContent bodyContent;

	public void setBodyContent(BodyContent b) {
		bodyContent = b;
	}

	/**
	 * 得到过滤的数据表
	 * @return List<String[]>
	 */
	private List<String[]> getFilterList() {
		List<String[]> list = getDataTable();
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
		if (t != null) mqTagTable = (MQTagTable) t;
	}

	/**
	 * 提取数据
	 * @return List<String[]>
	 */
	public abstract List<String[]> getDataTable();

}
