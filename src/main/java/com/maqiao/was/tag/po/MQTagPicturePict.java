/**
 * 
 */
package com.maqiao.was.tag.po;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public class MQTagPicturePict extends MQTagPicture {
	Integer shape = MQPOConst.ACC_NULL;
	boolean autoscale = false;
	boolean intercept = false;
	String url = null;
	boolean isurl = false;
	int shapewidth = MQPOConst.ACC_NULL;
	int shapeheight = MQPOConst.ACC_NULL;
	int shaperoundsize = MQPOConst.ACC_NULL;

	/*
	 * (non-Javadoc)
	 * @see com.maqiao.was.pictureOverlay.tag.MQTagPictureAbstract#toHtmlString()
	 */
	String toHtmlString() {
		if (isurl) this.pictype = 1;
		else this.pictype = 2;
		StringBuilder sb = new StringBuilder(1200);
		getHtmlInputElement(sb, "pictype", pictype);
		getHtmlInputElement(sb, "shape", shape);
		getHtmlInputElement(sb, "autoscale", autoscale);
		getHtmlInputElement(sb, "intercept", intercept);
		getHtmlInputElement(sb, "url", url);
		getHtmlInputElement(sb, "shape.circular.width", shapewidth);
		getHtmlInputElement(sb, "shape.circular.height", shapeheight);
		getHtmlInputElement(sb, "shape.round.size", shaperoundsize);

		return sb.toString();
	}

	public final Integer getShape() {
		return shape;
	}

	public final void setShape(Integer shape) {
		this.shape = shape;
	}

	public final boolean isAutoscale() {
		return autoscale;
	}

	public final void setAutoscale(boolean autoscale) {
		this.autoscale = autoscale;
	}

	public final boolean isIntercept() {
		return intercept;
	}

	public final void setIntercept(boolean intercept) {
		this.intercept = intercept;
	}

	public final String getUrl() {
		return url;
	}

	public final void setUrl(String url) {
		this.url = url;
	}

	public final boolean isIsurl() {
		return isurl;
	}

	public final void setIsurl(boolean isurl) {
		this.isurl = isurl;
	}

	public final int getShapewidth() {
		return shapewidth;
	}

	public final void setShapewidth(int shapewidth) {
		this.shapewidth = shapewidth;
	}

	public final int getShapeheight() {
		return shapeheight;
	}

	public final void setShapeheight(int shapeheight) {
		this.shapeheight = shapeheight;
	}

	public final int getShaperoundsize() {
		return shaperoundsize;
	}

	public final void setShaperoundsize(int shaperoundsize) {
		this.shaperoundsize = shaperoundsize;
	}

}
