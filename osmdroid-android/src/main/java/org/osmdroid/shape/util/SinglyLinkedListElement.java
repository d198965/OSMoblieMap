/*-�ⲿ����
 *����:McGraw-Hill
 *ժ¼��:
 *ժ¼����:
 *�޸���:
 *�޸�����:
 -*/
package org.osmdroid.shape.util;

class SinglyLinkedListElement
{
    protected Object data; // value stored in this element
    protected SinglyLinkedListElement nextElement; // ref to next

    public SinglyLinkedListElement( Object v,
                                    SinglyLinkedListElement next )
    // post: constructs a new element with value v,
    //       followed by next element
    {
        data = v;
        nextElement = next;
    }

    public SinglyLinkedListElement( Object v )
    // post: constructs a new tail of a list with value v
    {
        this( v, null );
    }

    public SinglyLinkedListElement next()
    // post: returns reference to next value in list
    {
        return nextElement;
    }

    public void setNext( SinglyLinkedListElement next )
    // post: sets reference to new next value
    {
        nextElement = next;
    }

    public Object value()
    // post: returns value associated with this element
    {
        return data;
    }

    public void setValue( Object value )
    // post: sets value associated with this element
    {
        data = value;
    }

    public String toString()
    // post: returns string representation of element
    {
        return "<SinglyLinkedListElement: " + value() + ">";
    }
}