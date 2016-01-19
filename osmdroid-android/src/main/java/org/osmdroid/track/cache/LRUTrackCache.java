package org.osmdroid.track.cache;

import android.util.Log;

import org.osmdroid.track.ITrackInfo;
import org.osmdroid.track.ITrackPath;

import java.util.LinkedHashMap;

/**
 * Created by zdh on 15/12/28.
 */
// 缓存Track
public class LRUTrackCache extends LinkedHashMap<ITrackInfo, ITrackPath> {
    private int mCapacity;
    private TrackRemovedListener mTrackRemovedListener;

    public LRUTrackCache(final int maxCapacity) {
        super(maxCapacity + 2, 0.1f, true);
        mCapacity = maxCapacity;
    }

    public void ensureCapacity(final int aCapacity) {
        if (aCapacity > mCapacity) {
            Log.i("LRUTrackCache", "Tile cache increased from " + mCapacity + " to " + aCapacity);
            mCapacity = aCapacity;
        }
    }

    public void setOnTrackRemovedListener(TrackRemovedListener listener){
        mTrackRemovedListener = listener;
    }

    @Override
    public ITrackPath remove(Object key) {
        if (mTrackRemovedListener != null) {
            mTrackRemovedListener.onTrackRemoved((ITrackInfo) key);
        }
        return super.remove(key);
    }

    @Override
    protected boolean removeEldestEntry(Entry<ITrackInfo, ITrackPath> aEldest) {
        if (size() > mCapacity) {
            final ITrackInfo eldest = aEldest.getKey();
            return remove(eldest) != null;
        }
        return false;
    }

    public interface TrackRemovedListener {
        void onTrackRemoved(ITrackInfo trackInfo);
    }

}
