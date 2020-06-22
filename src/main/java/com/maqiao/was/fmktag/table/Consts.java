package com.maqiao.was.fmktag.table;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.ModelAndView;
import sun.misc.Unsafe;
/**
 * 常量池
 * @author Sunjian
 * @since jdk1.7
 * @version 1.0
 */
@SuppressWarnings("restriction")
public final class Consts {
	public static final Unsafe UNSAFE;

	/**
	 * ModelAndView对象中ModelMap地址偏移量
	 */
	public static long ACC_OffsetModelAndView_ModelMap = 0L;
	static {
		try {
			final Field field = Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			UNSAFE = (Unsafe) field.get(null);
			ACC_OffsetModelAndView_ModelMap = UNSAFE.objectFieldOffset(ModelAndView.class.getDeclaredField("model"));/* 得到ModelAndView对象中model的偏移量 */
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	static final Map<String,Object> DefConfig=new HashMap<>();
	static {
		DefConfig.put("attrcolumn",-1);
		DefConfig.put("orderby","");
		DefConfig.put("comple",false);
		DefConfig.put("filterinvalid",false);
		DefConfig.put("distinct",false);
		DefConfig.put("istrim",true);
		DefConfig.put("isutf8",true);
		DefConfig.put("autochange","");
		DefConfig.put("autosearch",false);
		DefConfig.put("autosearchid",0);
	}
	/** 文件名以以下字符为结束，如http://xxx/aa.bmp#1 特殊符号右侧为特殊标记 */
	public static final char[] ACC_fileExtRightSingArr= {'#','$','&'};
	
	public static final String[] ACC_AllowFileExtendName= {"txt","text","json","xml","xls","xlsx"};
}
