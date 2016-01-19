package org.osmdroid.shape.geom;

import android.os.Parcel;

import org.osmdroid.shape.util.CIterator;
import org.osmdroid.shape.util.VectorCollection;

//this class shouldn't be public.
abstract class MultiShape extends VectorCollection implements CShape
{
    protected Extent savedExtent = null;

    public MultiShape()
    {
    }

    public void update()
    {
        savedExtent = null;
    }

    public void setExtent( Extent ex )
    {
        savedExtent = ex;
    }

    public Extent getExtent()
    {
        if ( savedExtent == null )
        {
            CIterator it = super.iterator();
            Object obj = null;
            CShape shape = null;
            Extent ext = null;
            while ( it.hasNext() )
            {
                obj = it.next();
                if ( obj instanceof CShape )
                {
                    shape = ( CShape ) obj;
                    ext = shape.getExtent().getMaxExtent( ext );
                }
            }
            savedExtent = ext;
        }
        return savedExtent;
    }

    public boolean hitTest( CPoint point, double offset )
    {
        return hitTest( point.x, point.y, offset );
    }

    public abstract boolean hitTest( double x, double y, double offset );

}