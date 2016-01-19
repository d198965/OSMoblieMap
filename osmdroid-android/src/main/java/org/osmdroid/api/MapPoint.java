package org.osmdroid.api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zdh on 15/12/19.
 */
public class MapPoint implements Parcelable {
    private double x;
    private double y;

    public MapPoint() {
        x = 0;
        y = 0;
    }

    public MapPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(x);
        dest.writeDouble(y);
    }

    public static final Parcelable.Creator<MapPoint> CREATOR = new Parcelable.Creator<MapPoint>() {
        /**
         * Return a new point from the data in the specified parcel.
         */
        public MapPoint createFromParcel(Parcel in) {
            MapPoint r = new MapPoint();
            r.readFromParcel(in);
            return r;
        }

        /**
         * Return an array of rectangles of the specified size.
         */
        public MapPoint[] newArray(int size) {
            return new MapPoint[size];
        }
    };

    public void readFromParcel(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
    }
}
