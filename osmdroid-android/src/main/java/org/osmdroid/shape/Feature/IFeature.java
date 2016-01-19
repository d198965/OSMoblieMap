package org.osmdroid.shape.Feature;

import android.os.Parcelable;

import org.osmdroid.shape.geom.CShape;
import org.osmdroid.shape.geom.Extent;

/**
 * Created by zdh on 15/12/18.
 */
public interface IFeature extends Comparable<IFeature>,Parcelable {
    CShape getGeomtry();
    Object getFieldValue(int index);
    int getFID();
    Object[] getDatas();
    Extent getExtent();
}
