package org.osmdroid.shape.geom;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * <p>Title: ��ͼ������״�߽���</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: soft2com</p>
 * @author not attributable
 * @version 1.0
 */
public class CBox implements Parcelable
{
    private double minx;
    private double miny;
    private double maxx;
    private double maxy;
    private double minz;
    private double maxz;
    public CBox( double minx, double miny, double maxx, double maxy )
    {
        this.minx = minx;
        this.miny = miny;
        this.maxx = maxx;
        this.maxy = maxy;
        this.minz = 0;
        this.maxz = 0;
    }
    public CBox( double minx, double miny, double maxx, double maxy,double minz,double maxz)
    {
        this.minx = minx;
        this.miny = miny;
        this.maxx = maxx;
        this.maxy = maxy;
        this.minz = minz;
        this.maxz = maxz;
    }

    public double getMaxx()
    {
        return maxx;
    }

    public void setMaxx( double maxx )
    {
        this.maxx = maxx;
    }

    public void setMaxy( double maxy )
    {
        this.maxy = maxy;
    }

    public double getMaxy()
    {
        return maxy;
    }

    public double getMinx()
    {
        return minx;
    }

    public void setMinx( double minx )
    {
        this.minx = minx;
    }

    public double getMiny()
    {
        return miny;
    }

    public void setMiny( double miny )
    {
        this.miny = miny;
    }
    public double getMaxz()
    {
        return maxz;
    }

    public void setMaxz( double maxz )
    {
        this.maxz = maxz;
    }
    public double getMinz()
    {
        return minz;
    }

    public void setMinz( double minz )
    {
        this.minz = minz;
    }
    
    
    
    public String toString()
    {
        return new StringBuffer().append( "minx=").append(minx).append(",miny=").append(miny).append(",maxx=").append(maxx).append(",maxy=").append(maxy ).toString();
    }
    
    
    public static final Creator<CBox> CREATOR =
	        new Creator<CBox>() {
	        @Override
	        public CBox createFromParcel(Parcel in) {		            
	        	double temMinx = in.readDouble();
	        	double temMiny = in.readDouble();
	        	double temMinz = in.readDouble();
	        	double temMaxx = in.readDouble();
	        	double temMaxy = in.readDouble();
	        	double temMaxz = in.readDouble();
	        	return new CBox(temMinx, temMiny, temMaxx, temMaxy, temMinz, temMaxz);
	        }

	        @Override
	        public CBox[] newArray(int size) {
	            return new CBox[size];
	        }
	    };
    
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		// TODO Auto-generated method stub
		parcel.writeDouble(minx);
    	parcel.writeDouble(miny);
    	parcel.writeDouble(minz);
    	parcel.writeDouble(maxx);
    	parcel.writeDouble(maxy);
    	parcel.writeDouble(maxz);
	}
}