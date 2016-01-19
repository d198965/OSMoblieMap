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
public interface IArchiveTrack {

    /**
     * @throws Exception
     */
    void init(String trackSaveFilePath) throws Exception;

    ITrackPath getTrackPath(ITrackInfo trackInfo);

    List<ITrackInfo> getTrackInfos(Extent extent);
    /**
     * Closes the archive file and releases resources.
     */
    void close();
}
