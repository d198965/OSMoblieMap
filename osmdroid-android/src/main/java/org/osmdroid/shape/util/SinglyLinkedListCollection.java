/*-�ⲿ����
 *����:McGraw-Hill
 *ժ¼��:
 *ժ¼����:
 *�޸���:
 *�޸�����:
 -*/

package org.osmdroid.shape.util;

public class SinglyLinkedListCollection implements CList
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int count; // list size
    protected SinglyLinkedListElement head; // first elt
    public void addAll( CCollection c )
    {
        CIterator it = c.iterator();
        while ( it.hasNext() )
        {
            this.add( it.next() );
        }
    }

    public void removeAll( CCollection c )
    {
        CIterator it = c.iterator();
        while ( it.hasNext() )
        {
            this.remove( it.next() );
        }
    }

    public Object[] toArray()
    {
        Object[] obj = new Object[this.size()];
        CIterator it = this.iterator();
        int i = 0;
        while ( it.hasNext() )
        {
            obj[i++] = it.next();
        }
        return obj;
    }

    public Object[] toArray( Object[] objectArray )
    {
        return null;
    }

    public void sort()
    {}

    public SinglyLinkedListCollection()
    // post: generates an empty list.
    {
        head = null;
        count = 0;
    }

    public void add( Object value )
    // post: adds value to beginning of list.
    {
        addToHead( value );
    }

    public void addToHead( Object value )
    // post: adds value to beginning of list.
    {
        // note the order that things happen:
        // head is parameter, then assigned
        head = new SinglyLinkedListElement( value, head );
        count++;
    }

    public Object removeFromHead()
    // pre: list is not empty
    // post: removes and returns value from beginning of list
    {
        SinglyLinkedListElement temp = head;
        head = head.next(); // move head down the list
        count--;
        return temp.value();
    }

    public void addToTail( Object value )
    // post: adds value to end of list
    {
        // location for the new value
        SinglyLinkedListElement temp =
            new SinglyLinkedListElement( value, null );
        if ( head != null )
        {
            // pointer to possible tail
            SinglyLinkedListElement finger = head;
            while ( finger.next() != null )
            {
                finger = finger.next();
            }
            finger.setNext( temp );
        }
        else
            head = temp;
        count++;
    }

    public Object removeFromTail()
    // pre: list is not empty
    // post: last value in list is returned
    {
        SinglyLinkedListElement finger = head;
        SinglyLinkedListElement previous = null;
        //Assert.pre(head != null,"CList is not empty.");
        while ( finger.next() != null ) // find end of list
        {
            previous = finger;
            finger = finger.next();
        }
        // finger is null, or points to end of list
        if ( previous == null )
        {
            // has exactly 1 element
            head = null;
        }
        else
        {
            // pointer to last element is reset.
            previous.setNext( null );
        }
        count--;
        return finger.value();
    }

    public Object peek()
    // pre: list is not empty
    // post: returns the first value in the list
    {
        return head.value();
    }

    public Object tailPeek()
    // pre: list is not empty
    // post: returns the last value in the list
    {
        SinglyLinkedListElement finger = head;
        //Assert.condition(finger != null,"CList is not empty.");
        while ( finger != null &&
                finger.next() != null )
        {
            finger = finger.next();
        }
        return finger.value();
    }

    public boolean contains( Object value )
    // pre: value is not null
    // post: returns true iff value is found in list.
    {
        SinglyLinkedListElement finger = head;
        while ( finger != null &&
                !finger.value().equals( value ) )
        {
            finger = finger.next();
        }
        return finger != null;
    }

    public void remove( Object value )
    // pre: value is not null
    // post: removes first element with matching value, if any.
    {
        SinglyLinkedListElement finger = head;
        SinglyLinkedListElement previous = null;
        while ( finger != null &&
                !finger.value().equals( value ) )
        {
            previous = finger;
            finger = finger.next();
        }
        // finger points to target value
        if ( finger != null )
        {
            // we found the element to remove
            if ( previous == null ) // it is first
            {
                head = finger.next();
            }
            else
            { // it's not first
                previous.setNext( finger.next() );
            }
            count--;
            return; //finger.value();
        }
        // didn't find it, return null
        //return null;
    }

    public int size()
    // post: returns the number of elements in list
    {
        return count;
    }

    public boolean isEmpty()
    // post: returns true iff the list is empty
    {
        return size() == 0;
    }

    public void clear()
    // post: removes all elements from the list
    {
        head = null;
        count = 0;
    }

    public CIterator iterator()
    // post: returns enumeration allowing traversal of list
    {
        return new SinglyLinkedListIterator( head );
    }

    public String toString()
    // post: returns a string representing list
    {
        StringBuffer s = new StringBuffer();
        s.append( "<SinglyLinkedList:" );
        CIterator li = this.iterator();
        while ( li.hasNext() )
        {
            s.append( " " + li.next() );
        }
        s.append( ">" );
        return s.toString();
    }

}