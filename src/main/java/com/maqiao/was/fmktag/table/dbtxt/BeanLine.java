/**
 * 
 */
package com.maqiao.was.fmktag.table.dbtxt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public final class BeanLine {
	HttpServletRequest request;
	/** 在freemarker中的html里作属性标志时使用，属性值[FieldName]替换 */
	static final String ACC_fmHtml_sign = "{@FieldName}";
	/** value值串 */
	List<String> list = new ArrayList<String>(10);

	public BeanLine() {

	}

	/**
	 * 构造(赋值)
	 * @param splitStr String[]
	 */
	public BeanLine(String... splitStr) {
		set(splitStr);
	}
	/**
	 * 构造(赋值)
	 * @param parr com.alibaba.fastjson.JSONArray
	 */
	public BeanLine(com.alibaba.fastjson.JSONArray parr) {
		set(parr);
	}
	/**
	 * 长度
	 * @return int
	 */
	public final int length() {
		return list.size();
	}

	/**
	 * 依次向BeanLine对象中的各属性赋值
	 * @param parr com.alibaba.fastjson.JSONArray
	 */
	public final void set(com.alibaba.fastjson.JSONArray parr) {
		Object[] arrs = parr.toArray();
		for (int i = 0; i < arrs.length; i++)
			list.add(arrs[i].toString());
	}

	/**
	 * 依次向BeanLine对象中的各属性赋值 以关键字对字符串进行分组赋值
	 * @param str String
	 * @param splitKey String
	 */
	public final void setSplit(String str, String splitKey) {
		if (str == null || str.length() == 0 || splitKey == null) return;
		String[] splitStr = str.split(splitKey);
		set(splitStr);
	}

	/**
	 * 依次向BeanLine对象中的各属性赋值
	 * @param splitStr String[]
	 */
	public final void set(String... splitStr) {
		list = Arrays.asList(splitStr);
	}
	/**
	 * 向list里添加单元
	 * @param str String
	 */
	public final void setUnit(String str) {
		list.add(str);
	}

	/**
	 * 把一段含有<code>{\@fiela}</code>的代码的html LI转换成BeanLine单元，进行替换<br>
	 * <code>
	 * "&lt;li&gt;{\@ v0} /{\@ v0}&lt;/li&gt;"
	 * "&lt;li&gt;XXXXXXXXXXX  /YYYYYYYYYYY&lt;/li&gt;"
	 * </code>
	 * @param htmlBeanLine String
	 * @param e BeanLine
	 * @return String
	 */
	public String SjFMBeanLineHtml(final String htmlBeanLine, final BeanLine e) {
		if (htmlBeanLine == null || htmlBeanLine.length() == 0) return "";
		String htmlStr = htmlBeanLine;
		for (int i = 0, len = list.size(); i < len; i++) {
			String fieldsign = ACC_fmHtml_sign.replace("FieldName", "v" + i);
			htmlStr = htmlStr.replace(fieldsign, list.get(i));

		}
		return htmlStr;
	}

	/**
	 * 判断index是否合法
	 * @param index int
	 * @return boolean
	 */
	public boolean isSafeIndex(int index) {
		return 0 <= index && index <= list.size();
	}

	/**
	 * 得到vI的值
	 * @param i int
	 * @return String
	 */
	public String get(int i) {
		if(i<0 || i>=list.size())return null;
		return list.get(i);
	}

	private static final char jsonVkey = '"';

	/**
	 * 把BeanLine对象转成Json，并是否过滤空属性(null || "")
	 * @param delSpace boolean
	 * @return String
	 */
	public String exportJsonObject(final boolean delSpace) {
		StringBuilder sb = new StringBuilder(20);
		if (list.size() == 0) return "";
		sb.append('{');
		for (int i = 0, len = list.size(); i < len; i++) {
			String value = list.get(i);
			if (delSpace && (value == null || value.length() == 0)) continue;
			sb.append("v" + i);
			sb.append(':');
			if (value == null) {
				sb.append("null");
			} else {
				sb.append(jsonVkey);
				sb.append(value);
				sb.append(jsonVkey);
			}
		}
		sb.append('}');
		if (sb.length() < 3) return "";
		return sb.toString();
	}

	/**
	 * 判断各段中是否含有关键字，如果含有则以\t进行组合
	 * @param key String
	 * @return String[]
	 */
	public final String[] getTransverseIndexOf(final String key) {
		List<String> newList = new ArrayList<String>(4);
		for (int i = 0, len = list.size(); i < len; i++) {
			String value = list.get(i);
			if (value != null && value.indexOf(key) > -1) newList.add(value);
		}
		String[] t = {};
		return newList.toArray(t);
	}

	public final HttpServletRequest getRequest() {
		return request;
	}

	public final void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public String toString() {
		return "BeanLine [" + (request != null ? "request=" + request + ", " : "") + (list != null ? "list=" + list : "") + "]";
	}
	/**
	 * 截取含有中文的字符串
	 * @param suffix int
	 * @param size int
	 * @return String
	 */
	public final String cut(int suffix,final int size) {
		return cut(get(suffix),size);
	}

	/**
	 * 截取含有中文的字符串
	 * @param str String
	 * @param size int
	 * @return String
	 */
	public final String cut(final String str, final int size) {
		if (str == null || str.length() == 0 || size <= 0) return "";
		String out = getSubString(str, 0, size);
		if (!str.equals(out)) return out + "..";
		return out;
	}

	/**
	 * 得到含有中文的字符串，进行截取
	 * @param str String
	 * @param pstart int
	 * @param pend int
	 * @return String
	 */
	public final String getSubString(final String str, final int pstart, final int pend) {
		String resu = "";
		int beg = 0, end = 0, count1 = 0;
		int i, j, y, cont, count, len;
		final int strlength = str.length();
		final char[] temp = new char[strlength];
		str.getChars(0, strlength, temp, 0);
		boolean[] bol = new boolean[strlength];
		for (i = 0, len = temp.length; i < len; i++)
			if (bol[i] = ((int) temp[i] > 255)) count1++;
			else bol[i] = false;
		if (pstart > strlength + count1) resu = null;
		if (pstart > pend) resu = null;
		if (pstart < 1) beg = 0;
		else beg = pstart - 1;
		if (pend > strlength + count1) end = strlength + count1;
		else end = pend;
		if (resu != null) {
			if (beg == end) {
				if (beg == (count = 0)) if (bol[0] == true) return null;
				else return new String(temp, 0, 1);
				else {
					len = beg;
					for (y = 0; y < (len--); y++)
						if (bol[y]) count++;
					if (count == 0) if ((int) temp[beg] > 255) return null;
					else return new String(temp, beg, 1);
					else if ((int) temp[len + 1] > 255) return null;
					else return new String(temp, len + 1, 1);
				}
			} else {
				int temSt = beg;
				int temEd = end - 1;
				for (i = 0; i < temSt; i++)
					if (bol[i]) temSt--;
				for (j = 0; j < temEd; j++)
					if (bol[j]) temEd--;
				if (bol[temSt]) {
					for (i = cont = 0; i <= temSt; i++)
						if (bol[i]) cont += 2;
						else cont++;
					if (pstart == cont) temSt++;
				}
				if (bol[temEd]) {
					for (i = cont = 0; i <= temEd; i++)
						if (bol[i]) cont += 2;
						else cont++;
					if (pend < cont) temEd--;
				}
				if (temSt == temEd) return new String(temp, temSt, 1);
				else if (temSt > temEd) return null;
				else return str.substring(temSt, temEd + 1);
			}
		}
		return resu;
	}

	/**
	 * 通过3个字段得到正则结果字符串
	 * @param urlIndex int
	 * @param regExIndex int
	 * @param groupIndex int
	 * @return String
	 */
	public String getRegEx(int urlIndex, int regExIndex, int groupIndex) {
		if (!(isSafeIndex(urlIndex) && isSafeIndex(regExIndex) && isSafeIndex(groupIndex))) return null;
		String url = this.get(urlIndex);
		String regEx = this.get(regExIndex);
		int group = -1;
		try {
			group = Integer.parseInt(this.get(groupIndex));
		} catch (NumberFormatException e) {
		}
		return DbtxtUtils.getRegExUrl(request, url, regEx, group);
	}
}
