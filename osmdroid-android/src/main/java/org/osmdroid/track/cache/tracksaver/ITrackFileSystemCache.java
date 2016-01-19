package org.osmdroid.track.cache.tracksaver;

import org.osmdroid.track.ITrackInfo;
import org.osmdroid.track.ITrackPath;
import org.osmdroid.track.ITrackPoint;

/**
 * Created by zdh on 15/12/28.
 */
// 本地存储Track
public interface ITrackFileSystemCache<P extends ITrackPath,T extends ITrackPoint>{

    boolean saveFile(final P trackPath);

    boolean insertTrackPoint(ITrackInfo trackInfo, T trackPoint);// 临时存储
}
