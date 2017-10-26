/**
 * 
 */
package com.maqiao.was.tag.table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.Tag;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public abstract class MQAbstractTable extends MQAbstractBody implements DynamicAttributes, InterfaceData, InterfaceSetState {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 结果列表
	 */
	List<String[]> list = null;
	/**
	 * 循环下标
	 */
	int point = 0;
	/**
	 * 输出结果html
	 */
	StringBuilder sb = new StringBuilder();

	/**
	 * 状态 NORMAL continue break;
	 */
	EnumState isState = EnumState.NORMAL;

	@Override
	public int doStartTag() throws JspException {
		if (t != null) mqTagTable = (MQTagTable) t;
		if (mqTagTable == null) return BodyTag.SKIP_PAGE;
		if (list == null) {
			list = getCacheList();
			if (list == null) {
				list = getSelectList();
				saveSession(list);
			}
		} else {
			System.out.println("init session:" + list.size());
		}
		pageContext.getRequest().setAttribute(n3, list.size() + "");
		if (list == null || list.size() == 0) return SKIP_BODY;
		if (!mqTagTable.isPrint) return BodyTag.SKIP_BODY;
		if (isState == EnumState.BREAK) return BodyTag.SKIP_PAGE;
		//HttpSession session = pageContext.getSession();
		//String username = (String) session.getAttribute("username");
		//HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
		//返回BodyTag.EVAL_BODY_BUFFERED，表示输出标签体内容
		//返回Tag.SKIP_BODY,表示不输出内容
		return BodyTag.EVAL_BODY_BUFFERED;
	}

	@Override
	public void doInitBody() {
		//System.out.println("Table--doInitBody");
		putAttrHtml();
	}

	/**
	 * 把参数放入到标签内容中 当isdynamic为true时，支持${v0}
	 */
	void putAttrHtml() {
		pageContext.getRequest().setAttribute(n0, point + "");
		if (!isdynamic) return;
		if (point < 0 || point >= list.size()) return;
		String[] array = (String[]) getDataArray();
		putAttribute(array);
	}

	/**
	 * 把转换后的标签内容存入StringBuilder中
	 */
	void putStringBuilder() {
		String content = getContentString(list.get(point));
		if (content != null) sb.append(content);
	}

	@Override
	public int doAfterBody() throws JspException {
		if (!mqTagTable.isPrint) return BodyTag.SKIP_BODY;
		if (isState == EnumState.BREAK) return BodyTag.SKIP_BODY;
		//System.out.println("Table--doAfterBody:" + point + "/" + list.size());
		if (list.size() == 0 || point < 0 || point >= list.size()) return BodyTag.SKIP_BODY;
		switch (isState) {
		case NORMAL:
			putStringBuilder(); /* 输出 */
			break;
		case CONTINUE:
			isState = EnumState.NORMAL;
			break;
		default:
			putStringBuilder(); /* 输出 */
		}
		point++;
		if (point >= list.size()) return BodyTag.SKIP_BODY;
		clean();
		putAttrHtml();
		return BodyTag.EVAL_BODY_AGAIN;// 循环
	}

	@Override
	public int doEndTag() throws JspException {
		//System.out.println("Table--doEndTag");
		this.write(sb.toString());
		return EVAL_PAGE;
	}

	/**
	 * 清除缓存
	 */
	private void clean() {
		try {
			bodyContent.clearBuffer();
			//bodyContent.clearBody();
			//bodyContent.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void release() {
		System.out.println("Table--release");
	}

	/**
	 * 得到过滤的数据表
	 * @return List<String[]>
	 */
	private List<String[]> getFilterList() {
		List<String[]> list = getDataTable();
		pageContext.getRequest().setAttribute(n1, list.size() + "");
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
		pageContext.getRequest().setAttribute(n2, list.size() + "");
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

	@Override
	public Tag getParent() {
		return mqTagTable;
	}

	/**
	 * 支持{v0,"default"}
	 */
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

	/**
	 * 把list保存到session中
	 * @param list List<String[]>
	 */
	private void saveSession(List<String[]> list) {
		if (mqTagTable.datasave == null || mqTagTable.datasave.length() == 0) return;
		String key = MQTTConst.ACC_SaveSessionKeyHead + mqTagTable.datasave;
		this.valueSave(key, list);
	}

	/**
	 * 从session提取数据集
	 * @return List<String[]>
	 */
	@SuppressWarnings("unchecked")
	private List<String[]> getCacheList() {
		if (mqTagTable.datasave == null || mqTagTable.datasave.length() == 0) return null;
		Object obj = null;
		String key = MQTTConst.ACC_SaveSessionKeyHead + mqTagTable.datasave;
		obj = this.valueGet(key);
		if (obj == null) return null;
		return (List<String[]>) obj;
	}

	/**
	 * 下标
	 */
	private String n0 = "index";
	/**
	 * 总数
	 */
	private String n1 = "countdata";
	/**
	 * 过滤后的总数
	 */
	private String n2 = "countfilter";
	/**
	 * 当前页的信息量
	 */
	private String n3 = "countcurrent";

	public final void setN0(String n0) {
		this.n0 = n0;
	}

	public final void setN1(String n1) {
		this.n1 = n1;
	}

	public final void setN2(String n2) {
		this.n2 = n2;
	}

	public final void setN3(String n3) {
		this.n3 = n3;
	}

	/*
	 * (non-Javadoc)
	 * @see com.maqiao.was.tag.table.InterfaceData#getDataArray()
	 */
	@Override
	public Object[] getDataArray() {
		if (point < 0 || point >= list.size()) return null;
		return list.get(point);
	}

	/*
	 * (non-Javadoc)
	 * @see com.maqiao.was.tag.table.InterfaceSetState#setState(com.maqiao.was.tag.table.EnumState)
	 */
	@Override
	public void setState(EnumState state) {
		this.isState = state;

	}

	/**
	 * 提取数据
	 * @return List<String[]>
	 */
	public abstract List<String[]> getDataTable();

}
