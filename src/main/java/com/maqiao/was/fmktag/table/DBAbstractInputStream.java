package com.maqiao.was.fmktag.table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.maqiao.was.fmktag.table.dbtxt.BeanLine;
import com.maqiao.was.fmktag.table.dbtxt.MQURL;

/**
 * 来自不同的数据流的数据源，如excel文件等不同的流文件，都要继承此类
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public abstract class DBAbstractInputStream extends DBAbstract {
	/* 来源类型 0:本地绝对地址 1:网络地址 */
	int type = -1;
	/* 数据源链接 */
	String sourcefile = null;

	/**
	 * 初始化
	 */
	public void Initialization() {
		super.Init();
		/* 来源类型 0:本地绝对地址 1:网络地址 */
		type = getInt("type");
		/* 数据源链接 */
		sourcefile = getString("sourcefile");
	}

	@Override
	public List<BeanLine> getList() {
		InputStream inputStream = this.getInputStream();
		if (inputStream == null) return new ArrayList<BeanLine>();
		return inputStreamToBeanlineList(inputStream);
	}

	/**
	 * @param is InputStream
	 * @return List<BeanLine>
	 */
	abstract List<BeanLine> inputStreamToBeanlineList(final InputStream is);

	/**
	 * 通过sourcefile和type得到输入流
	 * @return InputStream InputStream
	 */
	@SuppressWarnings("deprecation")
	public InputStream getInputStream() {
		Initialization();
		switch (type) {
		case 0:
			String sourceDBPath = new java.io.File(request.getRealPath(request.getRequestURI())).getParent() + "/" + sourcefile;
			System.out.println("sourceDBPath:" + sourceDBPath);
			try {
				return new FileInputStream(new File(sourceDBPath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		case 1:
			URL url = MQURL.getURL(request, sourcefile);
			return MQURL.getInputStream(url);
		default:
			return null;
		}
	}
}
