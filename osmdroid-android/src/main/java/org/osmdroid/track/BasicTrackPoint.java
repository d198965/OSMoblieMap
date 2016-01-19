package org.osmdroid.track;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.api.GeoPoint;
import org.osmdroid.api.MapPoint;
import org.osmdroid.api.georeference.geoutils.AngleQieColPro;
import org.osmdroid.api.georeference.geoutils.GeoUtils;

import java.util.Date;

/**
 * Created by zdh on 15/12/18.
 */
public class BasicTrackPoint extends Location implements ITrackPoint {
    private String desprition="";
    private String address="";
    private String name="";
    private boolean isGPS;
    private double azimuth;
    private MapPoint point;

    private AngleQieColPro projecton = new AngleQieColPro();
    public BasicTrackPoint()
    {
        super("");
        desprition = "";
        isGPS = true;
    }

    public BasicTrackPoint(Location location,String name,String description,String address,double azimuth){
        super(location);
        this.name = name;
        this.desprition = description;
        this.azimuth = azimuth;
        this.address = address;
        setLatitude(location.getLatitude());
        setLongitude(location.getLongitude());
    }

    public BasicTrackPoint(double lon,double lat,double altitude,float speed,double pAzimuth,String name,String pDesprition,String pAddress,long time)
    {
        super(name);
        this.name = name;
        setLongitude(lon);
        setLatitude(lat);
        setAltitude(altitude);
        setSpeed(speed);
        setTime(time);
        azimuth = pAzimuth;
        desprition = pDesprition;
        address = pAddress;
        isGPS = true;
    }

    @Override
    public void setLatitude(double latitude) {
        super.setLatitude(latitude);

        //todo 目前写死为 WGS84坐标，全球墨卡托投影
        point  = projecton.toFlatCoords(new GeoPoint(getLatitude(),getLongitude()),GeoUtils.WGS84);
    }

    @Override
    public void setLongitude(double longitude) {
        super.setLongitude(longitude);

        //todo 目前写死为 WGS84坐标，全球墨卡托投影
        point  = projecton.toFlatCoords(new GeoPoint(getLatitude(),getLongitude()),GeoUtils.WGS84);
    }

    @Override
    public double getY() {
        return point == null ? 0:point.getY();
    }

    @Override
    public double getX() {
        return point == null ? 0:point.getX();
    }

    public boolean isGPS() {
        return isGPS;
    }

    public void setGPS(boolean isGPS) {
        this.isGPS = isGPS;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String pAddress)
    {
        address = pAddress;
    }

    public String getDescription()
    {
        return desprition;
    }

    public void setDesprition(String pDesprition)
    {
        desprition = pDesprition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTrackPoint(BasicTrackPoint trackPoint)
    {
        if(trackPoint==null)
            return ;
        this.desprition = trackPoint.getDescription();
        this.address = trackPoint.getAddress();
        this.name = trackPoint.getName();
        this.isGPS = trackPoint.isGPS();
        set(trackPoint);
    }

    public BasicTrackPoint clone()
    {
        BasicTrackPoint l = new BasicTrackPoint();
        l.setName(name);
        l.setDesprition(desprition);
        l.setAddress(address);
        l.setProvider(getProvider());
        l.setTime(getTime());
        l.setLatitude(getLatitude());
        l.setLongitude(getLongitude());
        l.setAltitude(getAltitude());

        l.setSpeed(getSpeed());
        l.setBearing(getBearing());
        l.setAccuracy(getAccuracy());
        l.setExtras(getExtras());
        return l;
    }

    @Override
    public double getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(double azimuth){
        this.azimuth = azimuth;
    }

    public static final Parcelable.Creator<BasicTrackPoint> CREATOR =
            new Parcelable.Creator<BasicTrackPoint>() {
                @Override
                public BasicTrackPoint createFromParcel(Parcel in) {
                    BasicTrackPoint l = new BasicTrackPoint();
                    l.setName(in.readString());
                    l.setDesprition(in.readString());
                    l.setAddress(in.readString());
                    l.setAzimuth(in.readDouble());
                    l.setGPS(in.readInt() == 1);
                    Location temLocation = Location.CREATOR.createFromParcel(in);
                    l.set(temLocation);
                    return l;
                }

                @Override
                public BasicTrackPoint[] newArray(int size) {
                    return new BasicTrackPoint[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeString(desprition);
        parcel.writeString(address);
        parcel.writeDouble(azimuth);
        parcel.writeInt(isGPS ? 1:0);
        super.writeToParcel(parcel, flags);
    }
}
