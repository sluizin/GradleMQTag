/**
 * 
 */
package com.maqiao.was.tag.pictureOverlay;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public class MQTagPictureSpecial_BG extends MQTagPicture {
	int spid = MQPOConst.ACC_NULL;

	/*
	 * (non-Javadoc)
	 * @see com.maqiao.was.pictureOverlay.tag.MQTagPicture#toHtmlString()
	 */
	@Override
	String toHtmlString() {
		this.pictype = 4;
		this.spid = 1;
		StringBuilder sb = new StringBuilder(1200);
		getHtmlInputElement(sb, "pictype", pictype);
		getHtmlInputElement(sb, "sp.id", spid);
		return sb.toString();
	}

	public final int getSpid() {
		return spid;
	}

	public final void setSpid(int spid) {
		this.spid = spid;
	}

}
