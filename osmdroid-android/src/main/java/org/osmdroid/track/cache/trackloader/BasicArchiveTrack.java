package org.osmdroid.track.cache.trackloader;

import org.osmdroid.shape.geom.Extent;
import org.osmdroid.track.ITrackInfo;
import org.osmdroid.track.ITrackPath;
import org.osmdroid.track.cache.BasicSQLTrackPathStorage;

import java.util.List;

/**
 * Created by zdh on 15/12/28.
 */
// 读取数据库中的Track，给Provider
public class BasicArchiveTrack implements IArchiveTrack{
    BasicSQLTrackPathStorage trackPathStorage;
    @Override
    public void init(String trackSaveFilePath) throws Exception {
        trackPathStorage = BasicSQLTrackPathStorage.instance(trackSaveFilePath);
    }

    @Override
    public ITrackPath getTrackPath(ITrackInfo trackInfo) {
        if (trackPathStorage != null && trackInfo != null){
            return trackPathStorage.getTrack(trackInfo.getTrackName());
        }
        return null;
    }

    @Override
    public List<ITrackInfo> getTrackInfos(Extent extent) {
        if (trackPathStorage != null && extent != null){
            return trackPathStorage.getTrackInfos(extent);
        }
        return null;
    }

    @Override
    public void close() {
        if (trackPathStorage != null){
            trackPathStorage.close();
        }
    }
}
