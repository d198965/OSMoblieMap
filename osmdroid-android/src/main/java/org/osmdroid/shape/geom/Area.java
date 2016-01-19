package org.osmdroid.shape.geom;

/**
 *
 * <p>Title: </p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: soft2com</p>
 * @author soft2com
 * @version 1.0
 */

public interface Area extends CShape
{
    /**
     * @todo
     * @param point
     * @return true or false
     */
    public boolean contains(CPoint point);

    /**
     * @todo
     * @param x
     * @param y
     * @return true or false
     */
    public boolean contains(double x, double y);
    public double areaVaue();
}