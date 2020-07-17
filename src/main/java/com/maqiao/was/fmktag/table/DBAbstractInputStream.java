package com.maqiao.was.fmktag.table;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.maqiao.was.fmktag.table.dbtxt.BeanLine;
import com.maqiao.was.fmktag.table.dbtxt.MQURL;
/**
 * 来自不同的数据流的数据源，如excel文件等不同的流文件，都要继承此类
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@SuppressWarnings("rawtypes")
public abstract class DBAbstractInputStream extends DBAbstract  implements InterfaceAccpetVal{

	/* 来源类型 0:本地绝对地址 1:网络地址 */
	int type = -1;
	/** 数据源链接 */
	String sourcefile = null;
	/** 是否为utf-8 默认为真 */
	boolean isutf8 = true;
	String encoding =null;
	/** 是否进行编码转换 */
	String autochange = null;
	/** 目录中自动检索文件，只针对本地目录 File格式 */
	boolean autosearch =false;
	/** 自动检索文件下标数量 */
	int autosearchid=-1;
	/** 判断excel的两种格式xls,xlsx */
	//String fileextname = null;

	public DBAbstractInputStream(HttpServletRequest request, Map params) {
		super(request, params);
		super.acceptVal();
	}
	@Override
	public void acceptVal() {
		/* 来源类型 0:本地绝对地址 1:网络地址 */
		type = getInt(-1,"type");
		sourcefile = getString("sourcefile");
		isutf8 = getBoolean(true,"isutf8","readutf8");
		if(isutf8) {
			encoding ="UTF-8";
		}
		/* 是否进行编码转换 */
		autochange = getString("autochange");
		/* 目录中自动检索文件，只针对本地目录 File格式 */
		autosearch = getBoolean("autosearch");
		autosearchid = getInt(-1,"autosearchid");
	}
	/**
	 * 修改sourcefile值，在读取数据流之前
	 */
	abstract void changeSourcefile();
	/**
	 * 自动读取文件时的扩展名<br>
	 * 用于本地文件的自动检索<br>
	 * @return String[]
	 */
	abstract String[] getAutoSearchFilesExt();
	/**
	 * 把InputStream流转成最终结果集
	 * @param is InputStream
	 * @return List&lt;BeanLine&gt;
	 */
	abstract List<BeanLine> inputStreamToBeanlineList(InputStream is);
	
	@Override
	public List<BeanLine> getList() {
		return inputStreamToBeanlineList(getInputStream());
	}
	/**
	 * 通过sourcefile和type得到输入流
	 * @return InputStream
	 */
	public InputStream getInputStream() {
		changeSourcefile();
		//fileextname=Utils.getFileExtName(sourcefile);
		//System.out.println("fileextname:"+fileextname);
		switch (type) {
		case 0:
			String sourceDBPath = getBasicPath() + sourcefile;
			sourceDBPath=Utils.groomingPath(sourceDBPath);
			System.out.println("sourceDBPath:" + sourceDBPath);
			try {
				if(autosearch)sourceDBPath=Utils.getBasicAutoFile(sourceDBPath, getAutoSearchFilesExt(), autosearch, autosearchid);//getBasicFile(sourceDBPath);
				if(sourceDBPath==null)			return null;
				File f=new File(sourceDBPath);
				return new FileInputStream(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		case 1:
			changeSourcefileCode();
			URL url = MQURL.getURL(request, sourcefile);
			return MQURL.getInputStream(url);
		case -1:
		case -2:
			return MQURL.getInputStreamBySource(request, sourcefile);
		default:
			return null;
		}
	}
	/**
	 * 如果编码为utf-8，则把sourcefile转成utf-8
	 */
	private void changeSourcefileCode() {
		if(!isutf8)return;
		try {
			sourcefile = java.net.URLDecoder.decode(sourcefile, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 得到本地一个文件，如果没有，则试着检索出一个，用自动检索<br>
	 * 如果没有，则返回null
	 * @param source String
	 * @return String
	 */
	@Deprecated
	String getBasicFile(String source) {
		if(source==null || source.trim().length()==0)return getBasicAutoFile(source);
		File file=new File(source);
		if(!file.exists())return getBasicAutoFile(source);
		if(file.isFile())return source;
		return getBasicAutoFile(source);
	}
	/**
	 * 得到自寻流文件
	 * @param source String
	 * @return String
	 */
	@Deprecated
	private String getBasicAutoFile(String source) {
		if(!autosearch)return null;
		List<String> list=Utils.getFileList(source, getAutoSearchFilesExt());
		if(list.size()==0)return null;
		if(autosearchid>-1 && autosearchid<list.size())	return list.get(autosearchid);
		return list.get(0);
	}
	
	
	
}
