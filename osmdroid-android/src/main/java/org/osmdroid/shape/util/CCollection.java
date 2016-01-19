package org.osmdroid.shape.util;

import java.io.Serializable;

import android.R.integer;

/**
 * ���Ϲ�����
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: soft2com</p>
 * @author soft2com
 * @version 1.0
 */
public interface CCollection extends Serializable
{
    /**
     */
    void add(Object object);
    /**
     */
    void remove(Object object);

    /**
     */
    CIterator iterator();

    /**
     */
    void sort();

    /**
     *
     */
    boolean contains(Object object);

    /**
     *
     */
    Object[] toArray();

    /**
     * i don't what exacty this method mean,but i have some other use
     * so i defined here
     */
    Object[] toArray(Object[] object);

    /**
     *
     */
    void clear();

    /**
     *
     */
    void removeAll(CCollection collection);

    /**
     */
    void addAll(CCollection collection);
 
    
    /**
     */
    int size();

    /**
     */
    boolean isEmpty();

    int hashCode();

    public boolean equals(Object obj);
}
