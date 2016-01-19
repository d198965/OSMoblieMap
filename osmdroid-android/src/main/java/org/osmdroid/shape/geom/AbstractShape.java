package org.osmdroid.shape.geom;
import android.os.Parcel;

import org.osmdroid.shape.util.CCollection;
import org.osmdroid.shape.util.CIterator;
import org.osmdroid.shape.util.VectorCollection;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: soft2com</p>
 * @author soft2com
 * @version 1.0
 */
public abstract class AbstractShape implements CShape , CCollection
{
    VectorCollection collection = new VectorCollection( 20, 10 );

    /**
     * @todo
     * @return
     */
    public CCollection getPoints()
    {
        return collection;
    }

    protected Extent savedExtent = null;

    public void update()
    {
        savedExtent = null;
    }

    public void setExtent( Extent ex )
    {
        savedExtent = ex;
    }

    /**
     * @todo
     * @return
     */
    public Extent getExtent()
    {
        if ( savedExtent == null )
        {
            CPoint pt = null;
            double minx = Double.MAX_VALUE;
            double miny = Double.MAX_VALUE;
            double maxx = -Double.MAX_VALUE;
            double maxy = -Double.MAX_VALUE;
            double minz = Double.MAX_VALUE;
            double maxz = -Double.MAX_VALUE;
            double tempX;
            double tempY;
            double tempZ;
            CIterator it = collection.iterator();
            while ( it.hasNext() )
            {
                pt = ( CPoint ) it.next();
                tempX = pt.x;
                tempY = pt.y;
                tempZ = pt.z;
                minx = minx > tempX ? tempX : minx;
                miny = miny > tempY ? tempY : miny;
                maxx = maxx < tempX ? tempX : maxx;
                maxy = maxy < tempY ? tempY : maxy;
                minz = minz > tempZ ? tempZ : minz;
                maxz = maxz < tempZ ? tempZ : maxz;
            }
            savedExtent = new Extent( new CPoint( minx, miny,minz),
                                      new CPoint( maxx, maxy,maxz ) );
        }
        return savedExtent;
    }

    public void add( Object object )
    {
        collection.add( object );
        update();
    }
    public Object get(int index)
    {
    	return collection.get(index);
    }
    public void remove( Object object )
    {
        collection.remove( object );
        update();
    }

    public CIterator iterator()
    {
        return collection.iterator();
    }

    public void sort()
    {
    //throw new java.lang.UnsupportedOperationException("Method sort() not yet implemented.");
    }

    public boolean contains( Object object )
    {
        return collection.contains( object );
    }

    public Object[] toArray()
    {
        return collection.toArray();
    }

    public Object[] toArray( Object[] object )
    {
        //throw new java.lang.UnsupportedOperationException("Method toArray() not yet implemented.");
        //throw new java.lang.Exception();
        return null;
    }

    public void clear()
    {
        collection.clear();
    }

    public void removeAll( CCollection collection )
    {
        collection.remove( collection );
        update();
    }

    public void addAll( CCollection collection )
    {
        collection.addAll( collection );
        update();
    }

    public int size()
    {
        return collection.size();
    }

    
    public boolean isEmpty()
    {
        return collection.isEmpty();
    }

    /**
     * @todo
     * @param obj
     * @return
     */
    public boolean equals( Object obj )
    {
        return false;
    }

}
