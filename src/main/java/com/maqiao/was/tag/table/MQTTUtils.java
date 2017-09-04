/**
 * 
 */
package com.maqiao.was.tag.table;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public class MQTTUtils {
	/**
	 * 把字符串中所有属性，换成数组中的值
	 * @param content String
	 * @param list List<String[]>
	 * @return StringBuilder
	 */
	public static final StringBuilder contentChange(String content, List<String[]> list) {
		if (content == null || content.length() == 0) return new StringBuilder(0);
		if (list == null || list.size() == 0) {
			StringBuilder sb = new StringBuilder(content.length());
			sb.append(content);
			return sb;
		}
		StringBuilder sb = new StringBuilder(content.length() * list.size());
		for (int i = 0, len = list.size(); i < len; i++)
			sb.append(contentChange(content, list.get(i)));
		return sb;
	}

	/**
	 * 把字符串中所有属性，换成数组中的值，如果没有的话，使用默认值，没有默认值则返回null
	 * @param content String
	 * @param array String[]
	 * @return String
	 */
	public static final String contentChange(String content, String... array) {
		if (content == null || content.length() == 0 || array == null || array.length == 0) return "";
		String regEx = "\\{[\\s]?(v[0123456789]+)[\\s]?(,[\\s]?(\\\"[\\S\\s]*?\\\"))?[\\s]?\\}";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(content);
		String newContent = content;
		while (m.find()) {
			String str = m.group();
			String value = null;
			int num = attributeNum(str);
			String def = attributeDef(str);
			if (num < array.length) value = array[num];
			if (value == null) value = def;
			newContent = newContent.replace(str, value == null ? "null" : value);
		}
		return newContent;
	}

	/**
	 * 得到属性栏目编号
	 * @param str String
	 * @return int
	 */
	private static final int attributeNum(String str) {
		int p = str.indexOf(',');
		String s;
		if (p > -1) s = str.substring(1, p).trim();
		else s = str.substring(1, str.length() - 1).trim();
		return Integer.parseInt(s.substring(1, s.length()));
	}

	/**
	 * 得到默念值
	 * @param str String
	 * @return String
	 */
	private static final String attributeDef(String str) {
		int p = str.indexOf(',');
		if (p == -1) return null;
		String value = str.substring(p + 1, str.length() - 1).trim();
		value = value.substring(1, value.length() - 1);
		return value;
	}

	public static void main(String[] args) {
		String str = "aadf{v001}f s{v0}s{v0}s{v0}s8{ v1}9{v2,\"eee'\"}t{v2,\"eee'\"}cc{v2,\"eee'\"}de{av3}eefaa {v4,\"abc\"}884 55{v5,\"'tesa'\"}a-124  85-451,4512,1512-48-44,121 333 3dd488eea1{v6}2,b45aa33{v7,\"tes,t\"}33a33{v11,\"\"}a{ v111}{v111 }{v12 , \"test\"}{v110 , \"test\" }{ v100 , \"te\\\"st\" }aa";
		String[] array = { "AA", "BB", null, "CC", "DD", "EE" };
		System.out.println(contentChange(str, array));
	}

	/**
	 * 测试条件，如果无效，则返回false
	 * @param test String
	 * @param array String[]
	 * @return boolean
	 */
	public static final boolean test(String test, String... array) {
		if (array == null || array.length == 0 || test == null || test.length() == 0) return true;
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine se = manager.getEngineByName("js");
			for (int i = 0; i < array.length; i++)
				se.put("v" + i, array[i]);
			return test(se, test);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 测试条件，如果无效，则返回false
	 * @param test String
	 * @param array Object[]
	 * @return boolean
	 */
	public static final boolean test(String test, Object... array) {
		if (array == null || array.length == 0 || test == null || test.length() == 0) return true;
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine se = manager.getEngineByName("js");
			for (int i = 0; i < array.length; i++)
				se.put("v" + i, array[i]);
			return test(se, test);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 测试条件，如果无效，则返回false
	 * @param se ScriptEngine
	 * @param test String
	 * @return boolean
	 */
	public static final boolean test(ScriptEngine se, String test) {
		if (se == null || test == null || test.length() == 0) return false;
		try {
			return (boolean) se.eval(test);
		} catch (ScriptException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 判断范围串是否合法<br>
	 * 标号从0开始<br>
	 * 范围:1,5,7,9-15,20-25,5-2,50-(大于等于50)<br>
	 * 不允许出现 -15 但允许 0-15
	 * @param range String
	 * @return boolean
	 */
	public static final boolean isRange(String range) {
		if (range == null || range.length() == 0) return false;
		if (!range.matches("^[0123456789,-]+$")) return false;
		String[] array = range.split(",");
		for (int i = 0; i < array.length; i++) {
			String str = array[i];
			if (isNumber(str)) continue;/* 独立的数字 */
			String[] arr = str.split("-");
			if (arr.length != 2) return false;
			if (arr[0].length() == 0) return false;
		}
		return true;
	}

	/**
	 * 判断value是否在范围之内
	 * @param range String
	 * @param value int
	 * @return boolean
	 */
	public static final boolean isRange(String range, int value) {
		if (!isRange(range)) return false;
		String[] array = range.split(",");
		try {
			for (int i = 0; i < array.length; i++) {
				String str = array[i];
				;/* 独立的数字 */
				if (isNumber(str) && value == Integer.parseInt(str)) return true;
				String[] arr = str.split("-");
				int a = Integer.parseInt(arr[0]);
				if (arr[1].length() == 0 && value >= a) return true; /* 10- (大于等于10) */
				int b = Integer.parseInt(arr[1]);
				if ((value >= a && value <= b) || (value >= b && value <= a)) return true; /* 在a与b之间 */
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	/**
	 * 判断是否是数字
	 * @param number String
	 * @return boolean
	 */
	public static final boolean isNumber(final String number) {
		return (number == null || !number.matches("^[0123456789]+$")) ? false : true;
	}

	/**
	 * 通过URL得到文件内容<br>
	 * 是否过滤#右侧数据
	 * @param url URL
	 * @param enterStr String
	 * @param delnotes boolean
	 * @return StringBuilder
	 */
	public static final StringBuilder readFile(final URL url, String enterStr, boolean delnotes) {
		StringBuilder sb = new StringBuilder(20);
		try {
			HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setConnectTimeout(30000);
			urlcon.setReadTimeout(30000);
			urlcon.connect();
			String returnCode = new Integer(urlcon.getResponseCode()).toString();
			if (!returnCode.startsWith("2")) return null;
			InputStream is = urlcon.getInputStream();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
			String l = null;
			while ((l = buffer.readLine()) != null) {
				if (delnotes && l.indexOf('#') >= 0) l = l.substring(0, l.indexOf('#'));
				if (l.length() == 0) continue;
				sb.append(l);
				sb.append(enterStr);
			}
			buffer.close();
			is.close();
			return sb;
		} catch (IOException e) {
			return sb;
		}
	}

	/**
	 * 把字符串转成中文
	 * @param value String
	 * @return String
	 */
	static final String changeUtf8(String value) {
		if (value == null || value.length() == 0) return value;
		try {
			//if (MQEnvParaVariable.ACC_ENV == Env.DEV) return value;
			return URLDecoder.decode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return value;
		}
	}

	/**
	 * 把字符串按规则进行转化<br>
	 * autoChange: "iso-8859-1 to utf-8"
	 * @param string String
	 * @param autoChange String
	 * @return String
	 */
	public static final String autoChange(final String string, final String autoChange) {
		if (string == null || string.length() == 0 || autoChange == null || autoChange.length() == 0 || autoChange.trim().toLowerCase().indexOf("to") == -1) return string;
		return autoChange(string, autoChange.trim().toLowerCase().split("to"));
	}

	/**
	 * 把字符串按规则进行转化<br>
	 * autoArray: {"iso-8859-1","utf-8"} <br>
	 * 注意:autoArray数组必须是2个单元
	 * @param string String
	 * @param autoArray String[]
	 * @return String
	 */
	public static final String autoChange(String string, String... autoArray) {
		if (autoArray.length != 2) return string;
		try {
			return new String(string.getBytes(autoArray[0].trim()), autoArray[1].trim());
		} catch (UnsupportedEncodingException e) {
		}
		return string;
	}
}
