/**
 * 
 */
package com.maqiao.was.tag.pictureOverlay;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public class MQTagPictureText extends MQTagPicture {
	String text = null;
	String name = null;
	int style = MQPOConst.ACC_NULL;
	int size = MQPOConst.ACC_NULL;
	String color = null;
	int linespacing = MQPOConst.ACC_NULL;
	boolean issmooth = false;

	/*
	 * (non-Javadoc)
	 * @see com.maqiao.was.pictureOverlay.tag.MQTagPicture#toHtmlString()
	 */
	@Override
	String toHtmlString() {
		this.pictype = 3;
		StringBuilder sb = new StringBuilder(1200);
		getHtmlInputElement(sb, "pictype", pictype);
		getHtmlInputElement(sb, "font.text", text);
		getHtmlInputElement(sb, "font.name", name);
		getHtmlInputElement(sb, "font.style", style);
		getHtmlInputElement(sb, "font.size", size);
		getHtmlInputElement(sb, "font.color", color);
		getHtmlInputElement(sb, "font.linespacing", linespacing);
		getHtmlInputElement(sb, "font.issmooth", issmooth);
		return sb.toString();
	}

	public final String getText() {
		return text;
	}

	public final void setText(String text) {
		this.text = text;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final int getStyle() {
		return style;
	}

	public final void setStyle(int style) {
		this.style = style;
	}

	public final int getSize() {
		return size;
	}

	public final void setSize(int size) {
		this.size = size;
	}

	public final String getColor() {
		return color;
	}

	public final void setColor(String color) {
		this.color = color;
	}

	public final int getLinespacing() {
		return linespacing;
	}

	public final void setLinespacing(int linespacing) {
		this.linespacing = linespacing;
	}

	public final boolean isIssmooth() {
		return issmooth;
	}

	public final void setIssmooth(boolean issmooth) {
		this.issmooth = issmooth;
	}

}
