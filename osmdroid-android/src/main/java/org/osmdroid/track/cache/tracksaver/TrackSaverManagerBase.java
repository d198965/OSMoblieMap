package org.osmdroid.track.cache.tracksaver;

import org.osmdroid.track.ITrackInfo;
import org.osmdroid.track.ITrackPath;
import org.osmdroid.track.ITrackPoint;

/**
 * Created by zdh on 16/1/7.
 */
public interface TrackSaverManagerBase<T extends ITrackPath,P extends ITrackPoint>{

    boolean saveTrack(T trackPath);

    boolean saveTrack(String trackName);// 存储temptrack

    void insertTempTrackPoint(P trackPoint);

    T getTempTrack();

    void  resetSaverManager();

    boolean existTrack(ITrackInfo track);

    void onDestroy();
}
