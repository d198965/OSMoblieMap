package org.osmdroid.track.cache.trackloader;

import android.util.Log;

import org.osmdroid.shape.geom.Extent;
import org.osmdroid.track.ITrackInfo;
import org.osmdroid.track.ITrackPath;
import org.osmdroid.track.cache.LRUTrackCache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by zdh on 16/1/3.
 * Track加载管理，可以是远程、也可以是本地
 */
public abstract class TrackLoaderManagerBase {
    private static String TAG = "TrackLoaderManagerBase";
    private final LRUTrackCache mTrackCache;

    final TrackInfoLooper mTrackLooper;

    public interface ITrackLoaderListener {
        void loadTrackSuccess(ITrackPath trackPath, ITrackInfo trackInfo);

        void loadTrackFail(String message, ITrackInfo trackInfo);
    }

    private ITrackLoaderListener mTrackLoaderListener;

    // 异步 回调
    public abstract void requestTrackInfos(Extent extent);

    // 异步 回调
    public abstract void loadTrack(ITrackInfo trackInfo);

    public void onDetach() {
        if (mTrackCache != null) {
            mTrackCache.clear();
        }
    }

    public TrackLoaderManagerBase() {
        this.mTrackCache = new LRUTrackCache(10);
        mTrackLooper = new TrackInfoLooper();
        this.mTrackCache.setOnTrackRemovedListener(mTrackLooper);
    }

    public void setTrackLoaderListener(ITrackLoaderListener trackLoaderListener) {
        mTrackLoaderListener = trackLoaderListener;
    }

    protected void loadTrackSuccess(ITrackPath trackPath, ITrackInfo trackInfo) {
        if (trackPath != null && trackInfo != null) {
            putTrackIntoCache(trackInfo, trackPath);
            Log.i(TAG, "requestsuccess:" + trackPath.getTrackName());
        }
        // TODO
        if (mTrackLoaderListener != null) {
            mTrackLoaderListener.loadTrackSuccess(trackPath, trackInfo);
        }
    }

    protected void loadTrackFail(String message, ITrackInfo trackInfo) {
        if (trackInfo != null) {
            Log.e(TAG, "onRequestFailed:" + trackInfo.toString() + ":" + message);
        }
        if (mTrackLoaderListener != null) {
            mTrackLoaderListener.loadTrackFail(message, trackInfo);
        }
    }

    protected void requestInfoTrackFail(String message,Object requestObject) {
        Log.e(TAG,message);
    }

    protected void requestInfoTrackSuccess(List<ITrackInfo> trackInfos,Object requestObject) {
        mTrackLooper.loop(trackInfos);
    }

    public void onExtentChange(final Extent newExtent) {
        mTrackLooper.loop(newExtent);
    }

    public void ensureCapacity(final int pCapacity) {
        mTrackCache.ensureCapacity(pCapacity);
    }

    protected ITrackPath getTrackFromCache(ITrackInfo trackInfo) {
        return mTrackCache.get(trackInfo);
    }

    protected void putTrackIntoCache(ITrackInfo trackInfo, ITrackPath trackPath) {
        if (trackInfo != null && trackPath != null && !mTrackCache.containsKey(trackInfo)) {
            mTrackCache.put(trackInfo, trackPath);
        }
    }

    private class TrackInfoLooper implements LRUTrackCache.TrackRemovedListener {
        protected Extent mTrackRect;
        protected Extent mLoopRect;
        protected final List<ITrackInfo> mTrackInfos;

        public TrackInfoLooper() {
            mTrackInfos = new ArrayList<>();
        }

        public synchronized final void loop(final Extent extent) {
            initialiseLooperRect(extent);
            if (mLoopRect == null) {
                return;
            }
            // 启动请求TrackInfos
            mTrackInfos.clear();
            requestTrackInfos(extent);
        }

        public synchronized final void loop(List<ITrackInfo> trackInfos) {
            synchronized (mTrackInfos){
                for (int n = 0; trackInfos != null && n < trackInfos.size(); n++) {
                    if (mTrackInfos.indexOf(trackInfos.get(n)) < 0) {
                        mTrackInfos.add(trackInfos.get(n));
                        loadTrack(trackInfos.get(n));
                    }
                }
            }
        }

        public void onDetach() {
            mTrackInfos.clear();
            mTrackRect = null;
            mLoopRect = null;
        }

        public synchronized void initialiseLooperRect(Extent extent) {
            if (mTrackRect == null) {
                mTrackRect = extent;
                mLoopRect = extent;
            } else if (mTrackRect.covers(extent)) {
                mLoopRect = null;// 无需加载
            } else {
                mTrackRect = mTrackRect.getUnion(extent);
                mLoopRect = mTrackRect;
            }
        }

        @Override
        public void onTrackRemoved(ITrackInfo trackInfo) {
            Extent extent = null;
            Set<ITrackInfo> trackSet = mTrackCache.keySet();
            Iterator<ITrackInfo> iterator = trackSet.iterator();
            ITrackInfo temTrackInfo;
            while (iterator.hasNext()) {
                temTrackInfo = iterator.next();
                if (extent == null) {
                    extent = temTrackInfo.getExtent();
                } else {
                    extent = extent.getUnion(temTrackInfo.getExtent());
                }
            }
            mTrackRect = extent;
        }

    }
}
