// Interface for lists.
// (c) 1998 McGraw-Hill

package org.osmdroid.shape.util;

public interface CList extends CCollection
{
    //public Iterator elements();
    // post: returns an iterator allowing
    //   ordered traversal of elements in list

    public int size();

    // post: returns number of elements in list

    public boolean isEmpty();

    // post: returns true iff list has no elements

    public void clear();

    // post: empties list

    public void add(Object value);

    // post: value is added to beginning of list (see addToHead)

    public void addToHead(Object value);

    // post: value is added to beginning of list

    public void addToTail(Object value);

    // post: value is added to end of list

    public Object peek();

    // pre: list is not empty
    // post: returns first value in list

    public Object tailPeek();

    // pre: list is not empty
    // post: returns last value in list

    public Object removeFromHead();

    // pre: list is not empty
    // post: removes first value from the list

    public Object removeFromTail();

    // pre: list is not empty
    // post: removes the last value from the list

    public boolean contains(Object value);

    // pre: value is not null
    // post: returns true iff list contains an object equal to value

    public void remove(Object value);
    // post: removes and returns element equal to value
    //       otherwise returns null
}