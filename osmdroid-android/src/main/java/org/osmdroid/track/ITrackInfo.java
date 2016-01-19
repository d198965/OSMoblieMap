package org.osmdroid.track;

import android.os.Parcelable;

import org.osmdroid.shape.geom.Extent;

/**
 * Created by zdh on 16/1/3.
 */
public interface ITrackInfo extends Parcelable{

    long getStartTime();

    long getEndTime();

    String getTrackName();

    String getTrackDespretion();

    boolean isVisible();

    boolean isLike();

    Extent getExtent();

    boolean equals(Object object);
}
