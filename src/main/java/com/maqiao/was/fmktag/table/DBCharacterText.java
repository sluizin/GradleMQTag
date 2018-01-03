package com.maqiao.was.fmktag.table;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.maqiao.was.fmktag.table.dbtxt.BeanLine;
import com.maqiao.was.fmktag.table.dbtxt.MQURL;

/**
 * 数据源text
 * 参数:
 * type: <br>
 * 0:本地工程下的目录资源[与index.html同级]<br>
 * 1:外部资源路径文件[http://static.99114.com/static/zhuanti/XXXX/db/YYYY.txt] <br>
 * sourcefile: 资源文件路径与文件名 <br>
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@SuppressWarnings("rawtypes")
public class DBCharacterText extends DBCharacterAbstract {
	/** 行标志 */
	String rowSign = "\n";
	/** 列标志 */
	String columnSign = "\t";

	/**
	 * 把Content转成 <code>List&lt;beanLine&gt;</code>
	 * @param content String
	 * @return List&lt;BeanLine&gt;
	 */
	public final List<BeanLine> StringToBeanlineList(final String content) {
		if (content == null || content.length() == 0) return new ArrayList<BeanLine>(0);
		final String[] splitStr = content.split(rowSign);
		BeanLine e = null;
		int len = splitStr.length;
		final List<BeanLine> BeanLineList = new ArrayList<BeanLine>(len);
		for (int i = 0; i < len; i++)
			if ((e = beanLineChange(splitStr[i])) != null) BeanLineList.add(e);
		return BeanLineList;
	}

	/**
	 * 把一行，转成beanLine对象
	 * @param str String
	 * @return beanLine
	 */
	public final BeanLine beanLineChange(final String str) {
		if (str == null || str.length() == 0) return null;
		BeanLine bean = new BeanLine();
		String[] splitStr = str.split(columnSign);
		bean.set(splitStr);
		return bean;
	}

	@Override
	public List<BeanLine> getList() {
		List<BeanLine> list = new ArrayList<BeanLine>();
		int type = -1;
		/* 来源类型 0:本地绝对地址 1:网络地址 */
		type = getInt("type");
		/* 数据源链接 */
		String sourcefile = getString("sourcefile");
		/* 是否进行编码转换 */
		String autochange = getString("autochange");
		String outString="";
		switch (type) {
		case 0:
			final String sourceDBPath = request.getSession().getServletContext().getRealPath("") + "/" + sourcefile;
			outString = readFile(sourceDBPath, "\n", false).toString();
			return StringToBeanlineList(autoChange(outString, autochange));
			//return getBeanlineTxt(sourceDBPath, autochange);
		case 1:
			/* 读出是否进行utf-8编码 */
			isReadUtf8 = getBoolean("readutf8");
			outString = readFile(MQURL.getURL(request, sourcefile), isReadUtf8, "\n", false).toString();
			return StringToBeanlineList(autoChange(outString, autochange));
			//return getBeanlineTxtUrl(request, sourcefile, isReadUtf8, autochange);
		default:
			return list;
		}
	}

	public DBCharacterText() {

	}

	public DBCharacterText(HttpServletRequest request, Map params) {
		this.request = request;
		this.params = params;
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
