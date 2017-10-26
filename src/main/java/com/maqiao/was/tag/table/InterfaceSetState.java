/**
 * 
 */
package com.maqiao.was.tag.table;

/**
 * 允许设置流程控制
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public interface InterfaceSetState {
	/**
	 * 设置状态枚举 如 正常、Continue,Break，用于控制流程
	 * @param state EnumState
	 */
	public void setState(EnumState state);
}
