package org.osmdroid.pysicalmap.render.colormap;

import java.util.Random;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;

public class ColorMap {
	public static final int picHeight = 300;
	public static final int picWidth = 80;
	private static final int nonBrokenColorNum = 100;
	public static final int maxBrokenColorNum = 15;
	public static final int minBrokenColorNum = 2;
	

	private int minColor = Color.GREEN;
	private int maxColor = Color.RED;
	private boolean isBroken = false;
	private boolean isRandom = false;
	private int brokenColorNum = 2;	
	private int [] colorArray = null;
	
	/**
	 *
	 * @param pIsBroken//ɫͼ�Ƿ��Ƿּ���
	 * @param pIsRandom//���ּ��Ƿ�������
	 * @param pBrokenColorNum//ɫͼ����ж���ɫ��
	 * @param pMaxColor//ɫͼ��ʼ��ɫ
	 * @param pMinColor//ɫͼ��ֹ��ɫ
	 */
	public ColorMap(boolean pIsBroken,boolean pIsRandom,int pBrokenColorNum,int pMinColor,int pMaxColor)
	{
		
		isBroken = pIsBroken;
		isRandom = pIsRandom;
		minColor = pMinColor;
		maxColor = pMaxColor;
		int maxRed = Color.red(pMaxColor);
		int maxBlue = Color.blue(pMaxColor);
		int maxGreen = Color.green(pMaxColor);
		
		int minRed = Color.red(pMinColor);
		int minBlue = Color.blue(pMinColor);
		int minGreen = Color.green(pMinColor);
		
		brokenColorNum = minBrokenColorNum;
		if(pIsBroken)
		{
			if(pBrokenColorNum>minBrokenColorNum)//����������ɫ
				brokenColorNum = pBrokenColorNum;
			if(brokenColorNum>maxBrokenColorNum)//���15����ɫ
				brokenColorNum = maxBrokenColorNum;
			
		}else
			brokenColorNum = nonBrokenColorNum;	
		
		//���ɫ�ʸ����ȡɫ������
		colorArray = new int [brokenColorNum];
		if(pIsRandom)
		{
			Random random = new Random();
			for (int i = 0; i <brokenColorNum; i++)				
				colorArray[i] = 0xFF000000+random.nextInt(0xFFFFFF);								
		}else
		{
			float subRed = ((float)(maxRed - minRed))/(brokenColorNum-1);
			float subBlue = ((float)(maxBlue - minBlue))/(brokenColorNum-1);
			float subGreen = ((float)(maxGreen - minGreen))/(brokenColorNum-1);
			for (int i = 0; i <brokenColorNum; i++)
				colorArray[i] = Color.rgb((int)(minRed+subRed*i),(int)(minGreen+subGreen*i), (int)(minBlue+subBlue*i));
		}	
	}
	
	
	
	public Bitmap getColorMapImage()
	{
		if(colorArray==null||colorArray.length<=0)
			return null;
		Bitmap colorMap = Bitmap.createBitmap(picWidth, picHeight, Bitmap.Config.ARGB_8888);
		//����ɫ��ͼ
		Canvas canvas = new Canvas();
		canvas.setBitmap(colorMap);
		float subHeight = picHeight/(float)colorArray.length;
		Paint thePaint = new Paint();
		thePaint.setStrokeWidth(subHeight);
		float x =0;
		float y = picHeight;		
		for (int j=0;j<colorArray.length;j++) {
			thePaint.setColor(colorArray[j]);
			canvas.drawLine(x, y, picWidth,colorArray.length<=maxBrokenColorNum?(y-subHeight):y, thePaint);
			y-=subHeight;
		}
		//thePaint.setTextAlign(Align.CENTER);
		//thePaint.setTextSize(30);	
		//int value = ~(colorArray[0]^0xFF000000);
		//thePaint.setColor(value);
		//canvas.drawText(String.valueOf(minValue), 40, picHeight-20, thePaint);
		//value = ~(colorArray[colorArray.length-1]^0xFF000000);
		//thePaint.setColor(value);
		//canvas.drawText(""+maxValue, 40, 20, thePaint);
		return colorMap;
	}

	public int getColor(int colorIndex)
	{
		if(colorArray==null||colorArray.length<colorIndex)
			return 0;
		return this.colorArray[colorIndex];
	}
	
	public int getMinColor() {
		return minColor;
	}

	public int getMaxColor() {
		return maxColor;
	}

	public boolean isBroken() {
		return isBroken;
	}

	public boolean isRandom() {
		return isRandom;
	}

	public int getBrokenColorNum() {
		return brokenColorNum;
	}
	
}
