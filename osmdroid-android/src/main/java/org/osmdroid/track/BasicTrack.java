package org.osmdroid.track;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.pysicalmap.CoorT;
import org.osmdroid.pysicalmap.render.SimpleRender;
import org.osmdroid.shape.Feature.IFeature;
import org.osmdroid.shape.geom.CPoint;
import org.osmdroid.shape.geom.CShape;
import org.osmdroid.shape.geom.Extent;
import org.osmdroid.shape.geom.Line;
import org.osmdroid.shape.util.CIterator;
import org.osmdroid.views.Projection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by zdh on 15/12/18.
 */
public class BasicTrack implements ITrackPath<BasicTrackPoint> {
    BasicTrackInfo mTrackInfo;
    private double mLength;
    private double mAverageSpeed;
    private double mDZ;// 总高差
    private List<BasicTrackPoint> mTrackPoints = new ArrayList<>();
    private int mFID;

    private Extent mExtent;

    public BasicTrack(int fid, String trackName) {
        this.mFID = fid;
        mTrackInfo = new BasicTrackInfo(trackName);
    }

    public BasicTrack(int fid, BasicTrackPoint[] trackPoints, BasicTrackInfo trackInfo) {
        this.mFID = fid;
        if (trackPoints != null) {
            this.mTrackPoints =new ArrayList<>(Arrays.asList(trackPoints));
        }
        this.mTrackInfo = trackInfo;
        this.generateAttrs();
    }

    public BasicTrack(int fid, String trackName, String description, boolean isVisible, BasicTrackPoint[] trackPoints, boolean isLike) {
        this(fid,trackName);
        mTrackInfo.setDescription(description);
        mTrackInfo.setLike(isLike);
        mTrackInfo.setVisible(isVisible);
        this.mTrackPoints = Arrays.asList(trackPoints);
        this.mFID = fid;
        this.generateAttrs();
    }

    private void generateAttrs() {
        if (mTrackPoints == null || mTrackPoints.size() <= 0) {
            return;
        }
        mAverageSpeed = 0;
        for (int k = 0; k < mTrackPoints.size(); k++) {
            if (isSpeedInvalid(mTrackPoints.get(k).getSpeed())) {
                mAverageSpeed += mTrackPoints.get(k).getSpeed() / mTrackPoints.size(); // 可能会小于mTrackPoints.length
            }
            if (k > 0 && mTrackPoints.get(k).getAltitude() > mTrackPoints.get(k - 1).getAltitude()) {
                mDZ += mTrackPoints.get(k).getAltitude() - mTrackPoints.get(k - 1).getAltitude();
                mLength += mTrackPoints.get(k).distanceTo(mTrackPoints.get(k - 1));
            }
        }
        mTrackInfo.setStartTime(mTrackPoints.get(0).getTime());
        mTrackInfo.setEndTime(mTrackPoints.get(mTrackPoints.size() - 1).getTime());
    }

    public static boolean isSpeedInvalid(double speed) {
        return speed != Double.MAX_VALUE && speed >= 0 && !Double.isNaN(speed) && !Double.isInfinite(speed);
    }

    @Override
    public double length() {
        return mLength;
    }

    @Override
    public long getStartTime() {
        return mTrackInfo.getStartTime();
    }

    @Override
    public long getEndTime() {
        return mTrackInfo.getEndTime();
    }

    @Override
    public boolean isVisible() {
        return mTrackInfo.isVisible();
    }

    @Override
    public void setVisible(boolean isVisible) {
        mTrackInfo.setVisible(isVisible);
    }

    public boolean isLike() {
        return mTrackInfo.isLike();
    }

    public void setLike(Boolean isLike) {
        mTrackInfo.setLike(isLike);
    }

    public void setTrackName(String trackName) {
        if (trackName == null) {
            trackName = "";
        }
        mTrackInfo.setTrackName(trackName);
    }

    public double getAverageSpeed() {
        return mAverageSpeed;
    }

    @Override
    public String getTrackDespretion() {
        return mTrackInfo.getTrackDespretion();
    }

    // 获取总高差
    public double getDZ() {
        return mDZ;
    }

    @Override
    public String getTrackName() {
        return mTrackInfo.getTrackName();
    }

    @Override
    public BasicTrackPoint[] getTrackPoints() {
        if (mTrackPoints == null || mTrackPoints.size()<=0){
            return null;
        }
        return mTrackPoints.toArray(new BasicTrackPoint[mTrackPoints.size()]);
    }

    /**
     */
    @Override
    public void onDraw(Canvas canvas, SimpleRender render, Projection coorT) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(render.getOuterLineWidth());
        paint.setColor(render.getOuterLineColor());
        // 考虑OPenGL绘制
        Path temNewPath = new Path();
        Point pixelPoint = new Point();
        Point prePixelPoint = new Point();
//        for (int i = 1; i < mTrackPoints.size() - 1; i++) {
//            float speed = (mTrackPoints.get(i - 1).getSpeed() + mTrackPoints.get(i).getSpeed()) / 2;
//            if (i == 1) {
//                BasicTrackPoint temPoint1 = mTrackPoints.get(0);
//                pixelPoint = coorT.toPixels(temPoint1.getLongitude(), temPoint1.getLatitude(), null);
//                points[0] = pixelPoint.x;
//                points[1] = pixelPoint.y;
//                BasicTrackPoint temPoint2 = mTrackPoints.get(1);
//                pixelPoint = coorT.toPixels(temPoint2.getLongitude(), temPoint2.getLatitude(), pixelPoint);
//                points[2] = pixelPoint.x;
//                points[3] = pixelPoint.y;
//            }
//            BasicTrackPoint stopPoint = mTrackPoints.get(i + 1);
//            pixelPoint = coorT.toPixels(stopPoint.getLongitude(), stopPoint.getLatitude(), pixelPoint);
//            points[4] = pixelPoint.x;
//            points[5] = pixelPoint.y;
//            temNewPath.reset();
//            temNewPath.moveTo(points[0], points[1]);
//            temNewPath.lineTo(points[2], points[3]);
//            temNewPath.lineTo(points[4], points[5]);
//            canvas.drawPath(temNewPath, paint);
//            points[0] = points[2];
//            points[1] = points[3];
//            points[2] = points[4];
//            points[3] = points[5];
//        }

        for (int i = 0; i < mTrackPoints.size(); i++) {
            pixelPoint = coorT.toPixels(mTrackPoints.get(i).getLongitude(), mTrackPoints.get(i).getLatitude(), null);
            if (i == 0) {
                temNewPath.moveTo(pixelPoint.x, pixelPoint.y);
            } else {
                temNewPath.quadTo(prePixelPoint.x, prePixelPoint.y, pixelPoint.x, pixelPoint.y);
            }
            prePixelPoint.set(pixelPoint.x, pixelPoint.y);
        }
        canvas.drawPath(temNewPath, paint);

        paint.setStrokeWidth(2);
        paint.setColor(render.getTheColor());
        for (BasicTrackPoint trackPoint : mTrackPoints) {
            pixelPoint = coorT.toPixels(trackPoint.getLongitude(), trackPoint.getLatitude(), pixelPoint);
            float X = pixelPoint.x;
            float Y = pixelPoint.y;
            canvas.drawCircle(X, Y, 5, paint);
        }
    }

    @Override
    public CShape getGeomtry() {
        Line myLine = new Line();
        for (BasicTrackPoint temPoint : mTrackPoints) {
            myLine.add(new CPoint(temPoint.getX(), temPoint.getY(), temPoint.getAltitude()));
        }
        return myLine;
    }

    @Override
    public Extent getExtent() {
        if (mExtent == null) {
            double minx = Double.MAX_VALUE;
            double miny = Double.MAX_VALUE;
            double maxx = -Double.MAX_VALUE;
            double maxy = -Double.MAX_VALUE;
            double minz = Double.MAX_VALUE;
            double maxz = -Double.MAX_VALUE;
            double tempX;
            double tempY;
            double tempZ;
            for (BasicTrackPoint temPoint : mTrackPoints) {
                tempX = temPoint.getX();
                tempY = temPoint.getY();
                tempZ = temPoint.getAltitude();
                minx = minx > tempX ? tempX : minx;
                miny = miny > tempY ? tempY : miny;
                maxx = maxx < tempX ? tempX : maxx;
                maxy = maxy < tempY ? tempY : maxy;
                minz = minz > tempZ ? tempZ : minz;
                maxz = maxz < tempZ ? tempZ : maxz;
            }
            mExtent = new Extent(new CPoint(minx, miny, minz), new CPoint(minx, miny, minz));
        }

        return mExtent;
    }

    public void addTrackPoint(BasicTrackPoint temTrackPoint) {
        if (temTrackPoint != null) {
            mTrackPoints.add(temTrackPoint);
        }
    }

    public void removeTrackPoint(BasicTrackPoint temTrackPoint) {
        if (temTrackPoint != null && mTrackPoints != null) {
            mTrackPoints.remove(temTrackPoint);
        }
    }


    @Override
    public Object getFieldValue(int index) {
        return new Object();
    }

    @Override
    public int getFID() {
        return mFID;
    }

    @Override
    public Object[] getDatas() {
        return new Object[0];
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof BasicTrack)) {
            return false;
        }
        return getTrackName().equals(((BasicTrack) obj).getTrackName()) && super.equals(obj);
    }

    @Override
    public int compareTo(IFeature another) {
        return another == null ? 1 : getFID() - another.getFID();
    }

    public static final Parcelable.Creator<BasicTrack> CREATOR =
            new Parcelable.Creator<BasicTrack>() {
                @Override
                public BasicTrack createFromParcel(Parcel in) {
                    int fid = in.readInt();
                    BasicTrackInfo trackInfo = BasicTrackInfo.CREATOR.createFromParcel(in);
                    int pointCount = in.readInt();
                    BasicTrackPoint[] trackPoints = new BasicTrackPoint[pointCount];
                    for (int k = 0; k < pointCount; k++) {
                        trackPoints[k] = BasicTrackPoint.CREATOR.createFromParcel(in);
                    }
                    return new BasicTrack(fid, trackPoints, trackInfo);
                }

                @Override
                public BasicTrack[] newArray(int size) {
                    return new BasicTrack[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mFID);
        mTrackInfo.writeToParcel(parcel, flags);
        if (mTrackPoints == null) {
            parcel.writeInt(0);
            parcel.writeByteArray(new byte[0]);
        } else {
            parcel.writeInt(mTrackPoints.size());
            for (int i = 0; i < mTrackPoints.size(); i++) {
                mTrackPoints.get(i).writeToParcel(parcel, flags);
            }
        }
    }

}
