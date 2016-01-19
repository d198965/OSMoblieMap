package org.osmdroid.shape.geom;

import android.graphics.Canvas;
import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.shape.util.CCollection;
import org.osmdroid.shape.util.CIterator;

/**

 */
public class MultiPolygon extends MultiShape implements Area
{
    public MultiPolygon()
    {
    }

    public MultiPolygon( CCollection collection )
    {
        this.addAll( collection );
    }

    public boolean contains( double x, double y )
    {
        return this.contains( new CPoint( x, y,0) );
    }
    public boolean contains( double x, double y,double z)
    {
        return this.contains( new CPoint( x, y,z) );
    }
    public boolean contains( CPoint point )
    {
        CIterator it = this.iterator();
        while ( it.hasNext() )
        {
            if ( ( ( Area ) it.next() ).contains( point ) )
                return true;
        }
        return false;
    }

    /**
     * @param x
     * @param y
     * @param offset
     * @return
     */
    public boolean hitTest( double x, double y, double offset )
    {
        CIterator it = this.iterator();
        Object obj = null;
        Ring r = null;
        CPolygon plg = null;
        MultiPolygon mpl = null;

        while ( it.hasNext() )
        {
            obj = it.next();
            if ( obj instanceof MultiPolygon )
            {
                mpl = ( MultiPolygon ) obj;
                if ( mpl.hitTest( x, y, offset ) )
                    return true;
            }
            else if ( obj instanceof CPolygon )
            {
                plg = ( CPolygon ) obj;
                if ( plg.hitTest( x, y, offset ) )
                    return true;
            }
            else if ( obj instanceof Ring )
            {
                r = ( Ring ) obj;
                if ( r.hitTest( x, y, offset ) )
                    return true;
            }
        }
        return false;
    }

    /**
     * �ݹ齫MultiPolygon�����ַ�
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
            if ( shape instanceof Ring )
            {
                i++;
                sb1.append( "\t" + shape.toString() + "\n" );
            }
            else if ( shape instanceof MultiPolygon )
            {
                j++;
                sb2.append( "\t" + ( ( MultiPolygon ) shape ).toString() + "\n" );
            }
        }
        sb.append( "MultiPolygon include " + i + " polygons ," + j +
                   " MultiPolygon\n" );
        sb.append( sb1.toString() + "\n" );
        sb.append( sb2.toString() + "\n" );
        return sb.toString();
    }

    /**
     * @todo ��������״�ϲ�
     * @param mpObj
     * @return
     */
    public CShape unites( MultiPolygon mpObj )
    {
        CShape shape = null;
        if ( mpObj == null )
        {
            return this;
        }
        int num = mpObj.size();
        Ring ring[] = new Ring[num];
        shape = this;
        for ( int i = 0; i < num; i++ )
        {
            ring[i] = ( Ring ) mpObj.get( i );
            if ( shape instanceof Ring )
            {
                shape = ( ( Ring ) shape ).unites( ring[i] );
            }
            else if ( shape instanceof MultiPolygon )
            {
                shape = ( ( MultiPolygon ) shape ).unites( ring[i] );
            }
        }
        return shape;
    }

    /**
     * @todo ��������״�ϲ�
     * @param robj
     * @return
     */
    public CShape unites( Ring robj )
    {
        CShape shape = null;
        if ( robj == null )
        {
            return this;
        }
        int num = this.size();
        Ring ring[] = new Ring[num];
        CShape temp = null;
        for ( int i = 0; i < num - 1; i++ )
        {
            ring[i] = ( Ring )this.get( i );
            temp = ring[i].unites( robj );
            if ( temp instanceof Ring )
            {
                robj = ( Ring ) temp;
            }
            else if ( temp instanceof MultiPolygon )
            {
                if ( shape == null )
                {
                    shape = new MultiPolygon();
                }
                ( ( MultiPolygon ) shape ).add( ring[i] );
            }
        }
        ring[num - 1] = ( Ring )this.get( num - 1 );
        temp = ring[num - 1].unites( robj );
        if ( shape == null )
        {
            shape = temp;
        }
        else
        {
            if ( temp instanceof Ring )
            {
                ( ( MultiPolygon ) shape ).add( ( Ring ) temp );
            }
            else if ( temp instanceof MultiPolygon )
            {
                ( ( MultiPolygon ) shape ).add( ( Ring ) ring[num - 1] );
                ( ( MultiPolygon ) shape ).add( ( Ring ) robj );
            }
        }
        return shape;
    }

    /**
     * @todo �ж��Ƿ��������ཻ
     * @param ext
     * @return
     */
    public boolean intersects( Extent ext )
    {
        CIterator it = iterator();
        while ( it.hasNext() )
        {
            Ring ring = ( Ring ) it.next();
            if ( ring.intersects( ext ) )
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public double areaVaue() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
    }

    public static final Parcelable.Creator<MultiPolygon> CREATOR =
            new Parcelable.Creator<MultiPolygon>() {
                @Override
                public MultiPolygon createFromParcel(Parcel in) {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public MultiPolygon[] newArray(int size) {
                    return new MultiPolygon[size];
                }
            };
}
