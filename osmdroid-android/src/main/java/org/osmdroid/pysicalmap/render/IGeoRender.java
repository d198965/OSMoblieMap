package org.osmdroid.pysicalmap.render;

import org.osmdroid.pysicalmap.render.colormap.ColorMapContainer;

public interface IGeoRender extends IRender{
	public ColorMapContainer getColorMap();
	public float getOuterLineWidth();
	public float getSize();
	public int getOuterLineColor();
	public String getFieldName();
	public SimpleRender createSimpleRender(float value);
}
