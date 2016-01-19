package org.osmdroid.track;

/**
 * Created by zdh on 15/12/18.
 */
public interface ITrackPoint {
    double getAltitude(); // m

    double getY(); // m

    double getX();// m

    String getName();

    String getDescription();

    float getSpeed();

    double getAzimuth();

    long getTime();
}
