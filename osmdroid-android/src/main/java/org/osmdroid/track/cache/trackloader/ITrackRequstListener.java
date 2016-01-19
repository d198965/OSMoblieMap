package org.osmdroid.track.cache.trackloader;

import org.osmdroid.track.ITrackPath;

/**
 * Created by zdh on 16/1/4.
 */
public interface ITrackRequstListener {
    void requestTrackFail(String message, TrackRequestState requestState);
    void requestTrackSuccess(ITrackPath trackPath,TrackRequestState requestState);
}
