package com.maqiao.was.fmktag.table;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

/**
 * 来自不同的字符串数据源，如文本,json,xml等，都要继承此类
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public abstract class DBCharacterAbstract extends DBAbstract {
	boolean isReadUtf8 = false;

	public final boolean isReadUtf8() {
		return isReadUtf8;
	}

	public final void setReadUtf8(boolean isReadUtf8) {
		this.isReadUtf8 = isReadUtf8;
	}

	public InputStreamReader getISR(InputStream is) {
		try {
			return isReadUtf8 ? new InputStreamReader(is, "utf-8") : new InputStreamReader(is);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new InputStreamReader(is);
		}
	}

	/**
	 * RandomAccessFile RandomAccessFile读出时，转换成UTF-8
	 * @param line String
	 * @return String
	 */
	static final String changedLine(final String line) {
		if (line == null) return null;
		try {
			byte buf[] = new byte[1];
			byte[] byteArray = new byte[line.length()];
			StringReader aStringReader = new StringReader(line);
			int character;
			int i = 0;
			while ((character = aStringReader.read()) != -1)
				byteArray[i++] = buf[0] = (byte) character;
			return new String(byteArray, "UTF-8");
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 把字符串按规则进行转化<br>
	 * autoChange: "iso-8859-1 to utf-8"
	 * @param string String
	 * @param autoChange String
	 * @return String
	 */
	static final String autoChange(final String string, final String autoChange) {
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
	static final String autoChange(String string, String... autoArray) {
		if (autoArray.length != 2) return string;
		try {
			return new String(string.getBytes(autoArray[0].trim()), autoArray[1].trim());
		} catch (UnsupportedEncodingException e) {
		}
		return string;
	}
}
