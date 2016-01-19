package org.osmdroid.track;

import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.shape.geom.Extent;

/**
 * Created by zdh on 16/1/3.
 */
public class BasicTrackInfo implements ITrackInfo {
    private Boolean mIsVisible = false;// 是否可视
    private Boolean mIsLike = false;// 是否喜爱
    private String mTrackName;
    private String mDescription;
    private long mStartTime;
    private long mEndTime;
    private Extent mExtent;

    public BasicTrackInfo(String mTrackName) {
        this.mTrackName = mTrackName;
        mExtent = new Extent(0,0,0,0);
    }

    public BasicTrackInfo(Boolean mIsVisible, Boolean mIsLike, String mTrackName, String mDescription, long mStartTime, long mEndTime, Extent extent) {
        this.mIsVisible = mIsVisible;
        this.mIsLike = mIsLike;
        this.mTrackName = mTrackName;
        this.mDescription = mDescription;
        this.mStartTime = mStartTime;
        this.mEndTime = mEndTime;
        this.mExtent = extent;
    }

    public void setVisible(Boolean mIsVisible) {
        this.mIsVisible = mIsVisible;
    }

    public void setLike(Boolean mIsLike) {
        this.mIsLike = mIsLike;
    }

    public void setTrackName(String mTrackName) {
        this.mTrackName = mTrackName;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public void setStartTime(long mStartTime) {
        this.mStartTime = mStartTime;
    }

    public void setEndTime(long mEndTime) {
        this.mEndTime = mEndTime;
    }

    @Override
    public long getStartTime() {
        return mStartTime;
    }

    @Override
    public long getEndTime() {
        return mEndTime;
    }

    @Override
    public String getTrackName() {
        return mTrackName;
    }

    @Override
    public String getTrackDespretion() {
        return mDescription;
    }

    @Override
    public boolean isVisible() {
        return mIsVisible;
    }

    @Override
    public boolean isLike() {
        return mIsLike;
    }

    @Override
    public Extent getExtent() {
        return null;
    }

    @Override
    public String toString() {
        return mTrackName + " desciprition: " + mDescription;
    }

    @Override
    public int describeContents() {
        return 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTrackName);
        dest.writeString(mDescription);
        // 没必要存储
        dest.writeInt(mIsLike ? 1 : 0);
        dest.writeInt(mIsVisible ? 1 : 0);
        dest.writeLong(mStartTime);
        dest.writeLong(mEndTime);
        if (mExtent != null) {
            mExtent.writeToParcel(dest, flags);
        }
    }

    public static final Parcelable.Creator<BasicTrackInfo> CREATOR =
            new Parcelable.Creator<BasicTrackInfo>() {
                @Override
                public BasicTrackInfo createFromParcel(Parcel in) {
                    String trackName = in.readString();
                    String description = in.readString();
                    boolean isLike = in.readInt() == 1;
                    boolean isVisible = in.readInt() == 1;
                    long startTime = in.readLong();
                    long endTime = in.readLong();
                    Extent extent = Extent.CREATOR.createFromParcel(in);
                    return new BasicTrackInfo(isVisible, isLike, trackName, description, startTime, endTime, extent);
                }

                @Override
                public BasicTrackInfo[] newArray(int size) {
                    return new BasicTrackInfo[size];
                }
            };

}
