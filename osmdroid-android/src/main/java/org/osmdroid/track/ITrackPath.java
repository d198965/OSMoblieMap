package org.osmdroid.track;

import org.osmdroid.pysicalmap.IDrawableFeature;

/**
 * Created by zdh on 15/12/18.
 */
public interface ITrackPath<T extends ITrackPoint> extends IDrawableFeature,ITrackInfo {

    double length();

    void addTrackPoint(T newPoint);

    T [] getTrackPoints();
}
