package org.osmdroid.shape.geom;

import android.os.Parcel;

/**
 *
 */
public class CPolyLine implements CShape
{
    Extent box = null;
    int[] partIndex;
    CPoint[] pts = null;
    int FID = 0;

    public CPolyLine( Extent box,  int[] partIndex,  CPoint[] pts,int fid)
    {
        this.box = box;
        this.partIndex = partIndex;
        this.pts = pts;
        FID = fid;
        if(partIndex==null)
        {
        	partIndex = new int[1];
        	partIndex[0] = 0;
        }
    }

    public int getNumParts()
    {
        return partIndex.length;
    }

    public int getNumPoints()
    {
        return pts.length;
    }

    public int[] getPartIndex()
    {
        return partIndex;
    }

    public CPoint[] getPts()
    {
        return pts;
    }
    /**
     * @return
     */
    public CPoint[] getPoints( int index )
    {
        int partIndex = this.partIndex[index];
        int nextPartPointIndex = 0;
        if( index == this.partIndex.length-1 )
        {
            nextPartPointIndex = getNumPoints();
        }
        else
        {
            nextPartPointIndex = this.partIndex[index+1];
        }

        int count = nextPartPointIndex - this.partIndex[index]; //�����
        CPoint[] points = new CPoint[count];
        int j = 0;
        for( int i = this.partIndex[index]; i < nextPartPointIndex;i++ )
        {
            points[j++] = (CPoint)pts[i];
        }
        return points;
    }
	@Override
	public Extent getExtent() {
		return box;
	}

	@Override
	public boolean hitTest(double x, double y, double offset) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hitTest(CPoint point, double offset) {
		// TODO Auto-generated method stub
		return false;
	}

	 public static final Creator<CPolyLine> CREATOR =
		        new Creator<CPolyLine>() {
		        @Override
		        public CPolyLine createFromParcel(Parcel in) {
		        	int fid = in.readInt();
		        	Extent theBox = Extent.CREATOR.createFromParcel(in);
		        	int partLength = in.readInt();
		        	int [] thePartIndex = null;
		        	if(partLength>0)
		        	{
		        		thePartIndex = new int[partLength];
		        		in.readIntArray(thePartIndex);
		        	}	        	
		        	
		        	int pointNum = in.readInt();
		        	CPoint [] cPoints = new CPoint[pointNum];
		        	if(pointNum>0)
		        	{		        		
		        		for (int i = 0; i < pointNum; i++) {
		        			cPoints[i] = CPoint.CREATOR.createFromParcel(in);
						}
		        	}
		        	return new CPolyLine(theBox,  thePartIndex,  cPoints,fid);
		        }

		        @Override
		        public CPolyLine[] newArray(int size) {
		            return new CPolyLine[size];
		        }
		    };
	    
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
	    dest.writeInt(FID);
	    box.writeToParcel(dest, flags);
	    if(partIndex==null||partIndex.length==0)
	    	dest.writeInt(0);
	    else {
	    	dest.writeInt(partIndex.length);
	    	dest.writeIntArray(partIndex);
		}
	    if(pts==null||pts.length==0)
	    {
	    	dest.writeInt(0);
	    }else {
	    	dest.writeInt(pts.length);
	    	for (int i = 0; i < pts.length; i++) {
	    		pts[i].writeToParcel(dest, flags);
			}
		}
	    
	}	
}