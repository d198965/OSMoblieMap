package org.osmdroid.track.cache.trackloader;

import org.osmdroid.track.ITrackInfo;

import java.util.List;

/**
 * Created by zdh on 16/1/6.
 */
public interface ITrackInfoRequestListener {
    void requestInfoTrackFail(String message, TrackInfoRequestState requestState);
    void requestInfoTrackSuccess(List<ITrackInfo> trackInfos, TrackInfoRequestState requestState);
}
