package org.osmdroid.track.cache;

import org.osmdroid.track.ITrackPath;
import org.osmdroid.track.cache.trackloader.TrackRequestState;

/**
 * Created by zdh on 16/1/5.
 */
public interface IRequestTrackCallBack {
    void onRequestSuccess(TrackRequestState state, ITrackPath trackPath);
    void onRequestFailed(TrackRequestState state,String message);
}
