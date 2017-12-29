package com.maqiao.was.fmktag.table;

import java.lang.reflect.Field;

import org.springframework.web.servlet.ModelAndView;
import sun.misc.Unsafe;
/**
 * 常量池
 * @author Sunjian
 * @since jdk1.7
 * @version 1.0
 */
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
}
