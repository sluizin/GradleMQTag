/**
 * 
 */
package com.maqiao.was.tag.table;


/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public class MQAbstractTest extends MQAbstractBody {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 条件 v1=='test' || v2==v1+'st'
	 */
	String test = null;

	boolean condition = false;

	InterfaceData data = null;

	/**
	 * 得到多级父级状态与设置本地 test过滤
	 */
	public void conditionAfter() {
		boolean tt = MQTTUtils.getParentIFCondition(t);
		condition = tt;
	}

	/**
	 * 得到test条件是否成立
	 * @return boolean
	 */
	public boolean getConditionTest() {
		if (test == null || test.length() == 0) return true;
		data = MQTTUtils.getParentObj(t);
		if (data == null) return false;
		return MQTTUtils.isFilter(test, data);
	}

	public final void setTest(String test) {
		this.test = test;
	}
}
