package org.osmdroid.pysicalmap.render.colormap;

import android.graphics.Color;

public class ColorMapContainer extends ColorMap{
	private float maxValue = 0;
	private float minValue = 0;
	private float subValue = 0;
	public ColorMapContainer(float pMinValue,float pMaxValue,boolean pIsBroken,boolean pIsRandom,int pBrokenColorNum,int pMinColor,int pMaxColor)
	{
		super(pIsBroken, pIsRandom, pBrokenColorNum, pMinColor,pMaxColor);
		if(pMaxValue<0)
			pMaxValue = 0;
		if(pMinValue<0)
			pMinValue = 0;
		if(pMaxValue<pMinValue)
		{
			maxValue = pMinValue;
			minValue = pMaxValue;
		}else {
			maxValue = pMaxValue;
			minValue = pMinValue;
		}
		//��ȡ����
		if(maxValue==minValue)
			subValue = 0;
		else 
			subValue = getBrokenColorNum()/(maxValue-minValue);
	}
	
	public int getColor(float value)
	{
		if(getBrokenColorNum()<=0)
			return Color.BLUE;
		if(value<=minValue)
			return getColor(0);
		else if(value>=maxValue)
			return getColor(getBrokenColorNum()-1);
		int index = (int)((value-minValue)*subValue+0.5);
		if(index>=getBrokenColorNum()-1)
			return getColor(getBrokenColorNum()-1);
		return getColor(index);
	}
	
	public float getMaxValue() {
		return maxValue;
	}

	public float getMinValue() {
		return minValue;
	}
	
	
	

}
