package org.osmdroid.pysicalmap.render;
import android.graphics.Color;

public class SimpleRender implements IRender{
	private int theColor;
	private float size = 1;
	private float outerLineWidth=1;
	private int outerLineColor;
	public SimpleRender(int transparent,int areaColor,float size,float outerLineWidth,int outerLineColor)
	{
		this.size = size;
		this.theColor = areaColor;
		this.outerLineWidth = outerLineWidth;
		this.outerLineColor = outerLineColor;
		setTransparent(transparent);
	}
	
	public void setTransparent(int transparent) {
		//int value = Color.argb(transparent, 255, 255, 255);
		this.theColor = Color.argb(transparent, Color.red(theColor), Color.green(theColor), Color.blue(theColor));
		this.outerLineColor = Color.argb(transparent, Color.red(outerLineColor), Color.green(outerLineColor), Color.blue(outerLineColor));
	}

	@Override
	public int getTransparent() {
		// TODO Auto-generated method stub
		return Color.alpha(theColor);
	}

	public float getOuterLineWidth() {
		return outerLineWidth;
	}

	public void setOuterLineWidth(int outerLineWidth) {
		this.outerLineWidth = outerLineWidth;
	}

	public int getOuterLineColor() {
		return outerLineColor;
	}

	public void setOuterLineColor(int outerLineColor) {
		this.outerLineColor = outerLineColor;
	}

	public int getTheColor() {
		return theColor;
	}

	public void setTheColor(int theColor) {
		this.theColor = theColor;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	
}
