package org.osmdroid.shape.geom;

import android.os.Parcelable;

/**
 *
 * <p>Title: ������״����</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: soft2com</p>
 * @author soft2com
 * @version 1.0
 */
public interface CShape extends Parcelable
{
    /**
     * @return
     */
    public abstract Extent getExtent();

    public abstract boolean equals(Object obj);

    /**
     * <pre>
     *用于探测，点击的点，是否选中shape
     * </pre>
     * @param point
     * @param offset
     * @return
     */
    public abstract boolean hitTest( CPoint point, double offset );

    /**
     * @param x
     * @param y
     * @param offset
     * @return
     */
    public abstract boolean hitTest( double x, double y, double offset );
}
