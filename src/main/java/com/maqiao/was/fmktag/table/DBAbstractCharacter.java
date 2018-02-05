package com.maqiao.was.fmktag.table;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

import com.maqiao.was.fmktag.table.dbtxt.BeanLine;
import com.maqiao.was.fmktag.table.dbtxt.MQURL;

/**
 * 来自不同的字符串数据源，如文本,json,xml等，都要继承此类
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public abstract class DBAbstractCharacter extends DBAbstract {
	/* 来源类型 0:本地绝对地址 1:网络地址 */
	int type = -1;
	/* 数据源链接 */
	String sourcefile = null;
	/* 是否进行编码转换 */
	String autochange = null;
	/* 读出是否进行utf-8编码 */
	boolean isReadUtf8 = false;

	/**
	 * 初始化
	 */
	public void Initialization() {
		super.Init();
		/* 来源类型 0:本地绝对地址 1:网络地址 */
		type = getInt("type");
		/* 数据源链接 */
		sourcefile = getString("sourcefile");
		/* 是否进行编码转换 */
		autochange = getString("autochange");
		/* 读出是否进行utf-8编码 */
		isReadUtf8 = getBoolean("readutf8");
	}

	/**
	 * 得到字符串 已经进行编码转换
	 * @return String
	 */
	@SuppressWarnings("deprecation")
	public String getContent() {
		Initialization();
		String outString = "";
		switch (type) {
		case 0:
			//String sourceDBPath = request.getSession().getServletContext().getRealPath("./") + "/" + sourcefile;
			String sourceDBPath=new java.io.File(request.getRealPath(request.getRequestURI())).getParent() + "/"  + sourcefile;;
			System.out.println("sourceDBPath:"+sourceDBPath);
			StringBuilder sb = readFile(sourceDBPath, "\n", false);
			if (sb == null) return "";
			outString = sb.toString();
		case 1:
			StringBuilder sb2 = readFile(MQURL.getURL(request, sourcefile), isReadUtf8, "\n", false);
			if (sb2 == null) return "";
			outString = sb2.toString();
		default:
		}
		return autoChange(outString, autochange);
	}

	@Override
	public List<BeanLine> getList() {
		String outString = this.getContent();
		if (outString == null || outString.length() == 0) return new ArrayList<BeanLine>();
		return StringToBeanlineList(outString);
	}

	/**
	 * 字符串转对象列表
	 * @param content String
	 * @return List<BeanLine>
	 */
	abstract List<BeanLine> StringToBeanlineList(final String content);

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

	/**
	 * 通过RandomAccessFile读文件 按行读 randomFile.readLine<br>
	 * 是否过滤#右侧数据
	 * @param filenamepath String
	 * @param enterStr String
	 * @param delnotes boolean
	 * @return StringBuilder
	 */
	static final StringBuilder readFile(String filenamepath, String enterStr, boolean delnotes) {
		StringBuilder sb = new StringBuilder(400);
		File file = new File(filenamepath);
		if (!file.exists()) return sb;
		try (RandomAccessFile randomFile = new RandomAccessFile(file, "r"); FileChannel filechannel = randomFile.getChannel();) {
			randomFile.seek(0);
			FileLock lock;
			do {
				lock = filechannel.tryLock(0L, Long.MAX_VALUE, true);
			} while (null == lock);
			Thread.sleep(10);
			while (randomFile.getFilePointer() < randomFile.length()) {
				String str = changedLine(randomFile.readLine());
				if (str == null) continue;
				str = str.trim();
				if (str.length() == 0) continue;
				if (delnotes && str.indexOf('#') >= 0) str = str.substring(0, str.indexOf('#'));
				sb.append(str);
				if (randomFile.getFilePointer() < randomFile.length()) sb.append(enterStr);
			}
			lock.release();
			randomFile.close();
		} catch (Exception e) {
		}
		return sb;
	}

	/**
	 * 通过URL得到文件内容<br>
	 * 是否过滤#右侧数据
	 * @param url URL
	 * @param isReadUtf8 boolean
	 * @param enterStr String
	 * @param delnotes boolean
	 * @return StringBuilder
	 */
	static final StringBuilder readFile(final URL url, final boolean isReadUtf8, String enterStr, boolean delnotes) {
		StringBuilder sb = new StringBuilder(20);
		if (url == null) return sb;
		try {
			HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setConnectTimeout(30000);
			urlcon.setReadTimeout(30000);
			urlcon.connect();
			String returnCode = new Integer(urlcon.getResponseCode()).toString();
			if (!returnCode.startsWith("2")) return null;
			InputStream is = urlcon.getInputStream();
			InputStreamReader isr = isReadUtf8 ? new InputStreamReader(is, "utf-8") : new InputStreamReader(is);
			BufferedReader buffer = new BufferedReader(isr);
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
}
