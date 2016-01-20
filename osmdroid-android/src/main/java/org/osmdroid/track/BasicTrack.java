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
import org.osmdroid.shape.geom.CShape;
import org.osmdroid.shape.geom.Line;
import org.osmdroid.views.Projection;

/**
 * Created by zdh on 15/12/18.
 */
public class BasicTrack extends Line implements ITrackPath<BasicTrackPoint> {
    BasicTrackInfo mTrackInfo;
    private double mLength;
    private double mAverageSpeed;
    private double mDZ;// 总高差
    private BasicTrackPoint[] mTrackPoints;
    private int mFID;

    public BasicTrack(int fid, String trackName) {
        this.mFID = fid;
        mTrackInfo = new BasicTrackInfo(trackName);
    }

    public BasicTrack(int fid, BasicTrackPoint[] trackPoints, BasicTrackInfo trackInfo) {
        this.mFID = fid;
        this.mTrackPoints = trackPoints;
        this.mTrackInfo = trackInfo;
    }

    public BasicTrack(int fid, String trackName, String description, boolean isVisible, BasicTrackPoint[] trackPoints, boolean isLike) {
        mTrackInfo = new BasicTrackInfo(trackName);
        mTrackInfo.setDescription(description);
        mTrackInfo.setLike(isLike);
        mTrackInfo.setVisible(isVisible);
        this.mTrackPoints = trackPoints;
        this.mFID = fid;
        this.generateAttrs();
    }

    private void generateAttrs() {
        if (mTrackPoints == null || mTrackPoints.length <= 0) {
            return;
        }
        mAverageSpeed = 0;
        for (int k = 0; k < mTrackPoints.length; k++) {
            addPoint(mTrackPoints[k].getX(), mTrackPoints[k].getY(), mTrackPoints[k].getAltitude());
            if (isSpeedInvalid(mTrackPoints[k].getSpeed())) {
                mAverageSpeed += mTrackPoints[k].getSpeed() / mTrackPoints.length; // 可能会小于mTrackPoints.length
            }
            if (k > 0 && mTrackPoints[k].getAltitude() > mTrackPoints[k - 1].getAltitude()) {
                mDZ += mTrackPoints[k].getAltitude() - mTrackPoints[k - 1].getAltitude();
                mLength += mTrackPoints[k].distanceTo(mTrackPoints[k - 1]);
            }
        }
        mTrackInfo.setStartTime(mTrackPoints[0].getTime());
        mTrackInfo.setEndTime(mTrackPoints[mTrackPoints.length - 1].getTime());
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
        return mTrackPoints;
    }

    /**
     */
    @Override
    public void onDraw(Canvas canvas, SimpleRender render, Projection coorT) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(render.getOuterLineWidth());
        paint.setColor(render.getOuterLineColor());
        float[] points = new float[6];
        // 考虑OPenGL绘制
        Path temNewPath = new Path();
        Point pixelPoint = new Point();
        for (int i = 1; i < mTrackPoints.length - 1; i++) {
            float speed = (mTrackPoints[i - 1].getSpeed() + mTrackPoints[i].getSpeed()) / 2;
            if (i == 1) {
                ITrackPoint temPoint1 = mTrackPoints[0];
                pixelPoint = coorT.toPixels(temPoint1.getX(), temPoint1.getY(), null);
                points[0] = pixelPoint.x;
                points[1] = pixelPoint.y;
                ITrackPoint temPoint2 = mTrackPoints[1];
                pixelPoint = coorT.toPixels(temPoint1.getX(), temPoint1.getY(), pixelPoint);
                points[2] = pixelPoint.x;
                points[3] = pixelPoint.y;
            }
            ITrackPoint stopPoint = mTrackPoints[i + 1];
            pixelPoint = coorT.toPixels(stopPoint.getX(), stopPoint.getY(), pixelPoint);
            points[4] = pixelPoint.x;
            points[5] = pixelPoint.y;
            temNewPath.reset();
            temNewPath.moveTo(points[0], points[1]);
            temNewPath.lineTo(points[2], points[3]);
            temNewPath.lineTo(points[4], points[5]);
            canvas.drawPath(temNewPath, paint);
            points[0] = points[2];
            points[1] = points[3];
            points[2] = points[4];
            points[3] = points[5];
        }

        paint.setStrokeWidth(2);
        paint.setColor(render.getTheColor());
        for (ITrackPoint trackPoint : mTrackPoints) {
            pixelPoint = coorT.toPixels(trackPoint.getX(), trackPoint.getY(), pixelPoint);
            float X = pixelPoint.x;
            float Y = pixelPoint.y;
            canvas.drawCircle(X, Y, 5, paint);
        }
    }

    @Override
    public CShape getGeomtry() {
        return this;
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
            parcel.writeInt(mTrackPoints.length);
            for (int i = 0; i < mTrackPoints.length; i++) {
                mTrackPoints[i].writeToParcel(parcel, flags);
            }
        }
    }

}
