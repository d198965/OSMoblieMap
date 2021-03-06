package org.osmdroid.track.cache.trackloader;

import org.osmdroid.shape.geom.Extent;
import org.osmdroid.track.ITrackInfo;
import org.osmdroid.track.ITrackPath;

import java.io.File;
import java.util.List;

/**
 * Created by zdh on 15/12/28.
 */
// 用于获取本地Track的接口
public abstract class AbstractArchiveTrack {
    protected String mTrackSaveFilePath;
    public AbstractArchiveTrack(String trackSaveFilePath){
        mTrackSaveFilePath = trackSaveFilePath;
    }

    public String getTrackSaveFilePath(){
        return  mTrackSaveFilePath;
    }

    public abstract ITrackPath getTrackPath(ITrackInfo trackInfo);

    public abstract List<ITrackInfo> getTrackInfos(Extent extent);
    /**
     * Closes the archive file and releases resources.
     */
    public abstract void close();
}
