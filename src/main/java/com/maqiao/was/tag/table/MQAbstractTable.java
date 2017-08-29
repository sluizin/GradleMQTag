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
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public abstract class MQAbstractTable extends SimpleTagSupport {
	JspContext jspContext;

	public JspContext getJspContext() {
		return jspContext;
	}

	public void setJspContext(JspContext pc) {
		this.jspContext = pc;
	}

	@Override
	//当遇到标签时就会执行这个方法
	public void doTag() throws JspException, IOException {
		StringWriter writer = new StringWriter();
		JspFragment jspBody = this.getJspBody();
		final String content = writer.toString();
		List<String[]> list=getSelectList();
		jspBody.invoke(writer);
		jspContext.getOut().write(MQTTUtils.contentChange(content, list).toString());
	}
	/**
	 * 得到过滤的数据表
	 * @return  List<String[]>
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
			int b = (mqTagTable.p + 1) * mqTagTable.psize - 1;
			if (b >= list.size()) b = list.size() - 1;
			newList = list.subList(a, b);
			return newList;
		}
		return newList;
	}

	MQTagTable mqTagTable = null;

	@Override
	public void setParent(JspTag parent) {
		if (parent != null) mqTagTable = (MQTagTable) parent;
	}

	/**
	 * 提取数据
	 * @return List<String[]>
	 */
	public abstract List<String[]> getDataTable();
}
