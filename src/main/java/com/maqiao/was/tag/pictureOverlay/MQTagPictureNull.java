/**
 * 
 */
package com.maqiao.was.tag.pictureOverlay;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public class MQTagPictureNull extends MQTagPicture {

	/* (non-Javadoc)
	 * @see com.maqiao.was.tag.po.MQTagPicture#toHtmlString()
	 */
	@Override
	String toHtmlString() {
		this.pictype = 0;
		StringBuilder sb = new StringBuilder(1200);
		getHtmlInputElement(sb, "pictype", pictype);
		return sb.toString();
	}

}
