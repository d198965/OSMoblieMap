package org.osmdroid.shape.util;

public class CStack implements java.io.Serializable
{
    CList list = new SinglyLinkedListCollection();
    public CStack()
    {
    }

    public void push( Object obj )
    {
        list.addToHead( obj );
    }

    public Object pop()
    {
        return list.removeFromHead();
    }

    public Object top()
    {
        return list.peek();
    }

    public boolean isEmpty()
    {
        return list.size() <= 0;
    }

    public void clear()
    {
        list.clear();
    }
}