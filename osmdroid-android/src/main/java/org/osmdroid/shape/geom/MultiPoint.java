package org.osmdroid.shape.geom;

import android.graphics.Canvas;
import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.shape.util.CIterator;


/**
 *
 */
public class MultiPoint extends MultiShape
{

    public MultiPoint()
    {
    }

    public boolean hitTest( double x, double y, double offset )
    {
        return false;
    }

    public boolean equals( Object obj )
    {
        return false;
    }

    /**
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        StringBuffer sb1 = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        int i = 0, j = 0;
        CIterator it = this.iterator();
        CShape shape = null;
        while ( it.hasNext() )
        {
            shape = ( CShape ) it.next();
            if ( shape instanceof CPoint )
            {
                i++;
                sb1.append( "\t" + ( ( CPoint ) shape ).toString() + "\n" );
            }
            else if ( shape instanceof MultiPoint )
            {
                j++;
                sb2.append( "\t" + ( ( MultiPoint ) shape ).toString() + "\n" );
            }
        }
        sb.append( "MultiPoint include " + i + " points ," + j +
                   " MultiPoint\n" );
        sb.append( sb1.toString() + "\n" );
        sb.append( sb2.toString() + "\n" );
        return sb.toString();
    }

    /**new added begin**/
    private CBox box = null;
    private int numPoints;
    private CPoint[] pts = null;

    /**
     */
    public MultiPoint( CBox box, int numPoints, CPoint[] pts )
    {
        this.box = box;
        this.numPoints = numPoints;
        this.pts = pts;
    }

    /**
     * @return
     */
    public CBox getBox()
    {
        return box;
    }

    /**
     * @return
     */
    public int getNumPoints()
    {
        return numPoints;
    }

    /**
     * @return
     */
    public CPoint[] getPts()
    {
        return pts;
    }

    public void setBox(CBox box)
    {
        this.box = box;
    }
    public void setNumPoints(int numPoints)
    {
        this.numPoints = numPoints;
    }
    public void setPts(CPoint[] pts)
    {
        this.pts = pts;
    }


    public static final Parcelable.Creator<MultiPoint> CREATOR =
            new Parcelable.Creator<MultiPoint>() {
                @Override
                public MultiPoint createFromParcel(Parcel in) {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public MultiPoint[] newArray(int size) {
                    return new MultiPoint[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
    }
}