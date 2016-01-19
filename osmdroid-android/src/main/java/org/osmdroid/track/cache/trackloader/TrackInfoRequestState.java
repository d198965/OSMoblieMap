package org.osmdroid.track.cache.trackloader;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by zdh on 16/1/6.
 */
public class TrackInfoRequestState {
    private final Queue<TrackLoaderBase> mProviderQueue;
    private final Object mRequestObject;
    private TrackLoaderBase mCurrentProvider;
    private ITrackInfoRequestListener mTrackInfoRequestListener;
    public TrackInfoRequestState(final Object requestObject,
                             final TrackLoaderBase[] providers,
                             final ITrackInfoRequestListener trackListener) {
        mProviderQueue = new LinkedList<TrackLoaderBase>();
        Collections.addAll(mProviderQueue, providers);
        mRequestObject = requestObject;
        mTrackInfoRequestListener = trackListener;
    }

    public Object getRequestObject() {
        return mRequestObject;
    }

    public boolean isEmpty() {
        return mProviderQueue.isEmpty();
    }

    public ITrackInfoRequestListener getTrackInfoLoaderListener(){
        return mTrackInfoRequestListener;
    }

    public TrackLoaderBase getNextProvider() {
        mCurrentProvider = mProviderQueue.poll();
        return mCurrentProvider;
    }

    public TrackLoaderBase getCurrentProvider() {
        return mCurrentProvider;
    }
}
