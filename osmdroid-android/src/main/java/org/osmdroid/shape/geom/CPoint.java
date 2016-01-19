package org.osmdroid.shape.geom;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * @version 1.0
 */
public class CPoint implements CShape
{
    /**
     * 
     */
    public double x;

    public double z;   
    /**
     *
     */
    public double y;

    public CPoint( double x, double y ,double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;       
    }

    public CPoint(double x, double y)
    {
       this.x = x;
        this.y = y;
        this.z = 0;
   }
    /**
     *
     * @param point
     */
    public CPoint( CPoint point )
    {
        this.x = point.x;
        this.y = point.y;
        this.z = point.z;       
    }
    public CPoint()
    {
    	this.x = 0;
    	this.y = 0;
    	this.z = 0;
    }
    public double getX()
    {
        return this.x;
    }

    public void setX( double x )
    {
        this.x = x;
    }

    public double getY()
    {
        return this.y;
    }

    public void setY( double y )
    {
        this.y = y;
    }
    public double getZ()
    {
        return this.z;
    }

    public void setZ( double z )
    {
        this.z = z;
    }
    
    /**
     * @param point
     * @param offset
     * @return
     */
    public boolean hitTest( CPoint point, double offset )
    {
        return (this.getDistance( point ) < offset);
    }

    /**
     * @param x
     * @param y
     * @param offset
     * @return
     */
    public boolean hitTest( double x, double y, double offset )
    {
        return this.hitTest( new CPoint( x, y,z ), offset );
    }

    /**
     *
     * @return
     */
    public Extent getExtent()
    {
        return new Extent( this.x, this.y,this.x,this.y);
    }

    /**
     * @todo 
     * @param point
     * @return
     */
    public double getDistance( CPoint point )
    {
        double dx = this.x - point.x;
        double dy = this.y - point.y;
        double dz = this.z - point.z;
        return Math.sqrt( dx * dx + dy * dy +dz*dz);
    }

    /**
     *
     * @param obj
     * @return
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof CPoint )
        {
            CPoint p = ( CPoint ) obj;
            return ( (this.x == p.x) && (this.y == p.y) );
        }
        else
        {
            return false;
        }
    }

    public String toString()
    {
        return "point(" + this.getX() + "," + this.getY() + ")";
    }
	
	 public static final Parcelable.Creator<CPoint> CREATOR =
		        new Parcelable.Creator<CPoint>() {
		        @Override
		        public CPoint createFromParcel(Parcel in) {		            
		        	double x = in.readDouble();
		        	double y = in.readDouble();
		        	double z = in.readDouble();
		        	return new CPoint(x,y,z);
		        }

		        @Override
		        public CPoint[] newArray(int size) {
		            return new CPoint[size];
		        }
		    };
	    
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub		
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeDouble(x);
		dest.writeDouble(y);
		dest.writeDouble(z);
	}
	
}
