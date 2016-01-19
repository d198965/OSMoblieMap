package org.osmdroid.shape.geom;

import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.shape.util.CCollection;
import org.osmdroid.shape.util.CIterator;

/**
 */
public class MultiLine extends MultiShape {
    public MultiLine() {
    }

    public MultiLine(CCollection collection) {
        this.addAll(collection);
    }

    public boolean hitTest( double x, double y, double offset )
    {
        CIterator it = this.iterator();
        Line l = null;
        Object obj = null;
        MultiLine ml = null;
        while ( it.hasNext() )
        {
            obj = it.next();
            if ( obj instanceof MultiLine )
            {
                ml = ( MultiLine ) obj;
                if ( ml.hitTest( x, y, offset ) )
                    return true;
            }
            else if ( obj instanceof Line )
            {
                l = ( Line ) obj;
                if ( l.hitTest( x, y, offset ) )
                    return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        StringBuffer sb1 = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        int i = 0, j = 0;
        CIterator it = this.iterator();
        CShape shape = null;
        while (it.hasNext()) {
            shape = (CShape) it.next();
            if (shape instanceof Line) {
                i++;
                sb1.append("\t" + ((Line) shape).toString() + "\n");
            } else if (shape instanceof MultiLine) {
                j++;
                sb2.append("\t" + ((MultiLine) shape).toString() + "\n");
            }
        }
        sb.append("MultiLine include " + i + " lines ," + j + " MultiLine\n");
        sb.append(sb1.toString() + "\n");
        sb.append(sb2.toString() + "\n");
        return sb.toString();
    }

    public static final Parcelable.Creator<MultiLine> CREATOR =
            new Parcelable.Creator<MultiLine>() {
                @Override
                public MultiLine createFromParcel(Parcel in) {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public MultiLine[] newArray(int size) {
                    return new MultiLine[size];
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

    }
}