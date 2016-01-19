package org.osmdroid.shape.util;

import java.util.Vector;

/**
 * ������
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: soft2com</p>
 * @author soft2com
 * @version 1.0
 */
public class VectorCollection implements CCollection
{
    Vector v;
    public VectorCollection( Vector v )
    {
        this.v = v;
    }

    public VectorCollection()
    {
        this( 10 );
    }

    public VectorCollection( int size )
    {
        this( size, 10 );
    }

    public VectorCollection( int size, int increment )
    {
        v = new Vector( size, increment );
    }

    public void add( Object object )
    {
        v.addElement( object );
    }

    public void remove( Object object )
    {
        v.removeElement( object );
    }

    public CIterator iterator()
    {
        return new VectorIterator( v );
    }

    public void sort()
    {
    }

    public boolean contains( Object object )
    {
        return v.contains( object );
    }

    public Object[] toArray()
    {
        Object[] obj = new Object[v.size()];
        v.copyInto( obj );
        return obj;
    }

    public Object[] toArray( Object[] object )
    {
        return null;
    }

    public void clear()
    {
        v.removeAllElements();
    }

    public void removeAll( CCollection collection )
    {
        Object obj = null;
        CIterator it = collection.iterator();
        while ( it.hasNext() )
        {
            obj = it.next();
            v.removeElement( obj );
        }

    }

    public void addAll( CCollection collection )
    {
        Object obj = null;
        CIterator it = collection.iterator();
        int i = 0;
        int length = collection.size();
        while ( it.hasNext() )
        {
            obj = it.next();
            v.addElement( obj );
        }
    }

    public int size()
    {
        return v.size();
    }

    public boolean isEmpty()
    {
        return v.isEmpty();
    }

    protected class VectorIterator implements CIterator
    {
        Vector v = null;
        int pointer = 0;
        public VectorIterator( Vector v )
        {
            this.v = v;
        }

        public boolean hasNext()
        {
            return pointer < v.size();
        }

        public Object next()
        {
            Object obj = v.elementAt( pointer++ );
            return obj;
        }

        public void reset()
        {
            pointer = 0;
        }
    }

    public boolean equals( Object obj )
    {
        boolean bequals = false;
        if ( obj instanceof CCollection )
        {
            CCollection c = ( CCollection ) obj;
            if ( c.size() == this.size() )
            {
                CIterator it1 = this.iterator();
                CIterator it2 = c.iterator();
                bequals = true;
                while ( it1.hasNext() )
                {
                    if ( it1.next().equals( it2.next() ) )
                        continue;
                    else
                    {
                        bequals = false;
                        break;
                    }
                }
            }
        }
        return bequals;
    }

    public Object get( int i )
    {
        return v.elementAt( i );
    }

    public int get( Object obj )
    {
        return v.indexOf( obj );
    }
}
