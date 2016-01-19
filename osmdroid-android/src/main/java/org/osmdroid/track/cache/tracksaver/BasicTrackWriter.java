package org.osmdroid.track.cache.tracksaver;

import android.util.Log;

import org.osmdroid.track.BasicTrack;
import org.osmdroid.track.BasicTrackPoint;
import org.osmdroid.track.ITrackInfo;
import org.osmdroid.track.cache.BasicSQLTrackPathStorage;

/**
 * Created by zdh on 15/12/28.
 */
// 将Track存入数据库中，实时存储
public class BasicTrackWriter implements ITrackFileSystemCache<BasicTrack, BasicTrackPoint> {
    private BasicSQLTrackPathStorage localStorageManager;

    public BasicTrackWriter(String dbPath) {
        localStorageManager = BasicSQLTrackPathStorage.instance(dbPath);
    }

    @Override
    public boolean saveFile(BasicTrack trackPath) {
        return localStorageManager.insertTrack(trackPath, false) > 0;
    }

    @Override
    public boolean insertTrackPoint(ITrackInfo trackInfo, BasicTrackPoint trackPoint) {
        boolean insertSuccess = false;
        try {
            insertSuccess = localStorageManager.insertTrackPoint(trackPoint, true, trackInfo.getTrackName()) > 0;
        } catch (Exception ex) {
            Log.e("insert track point fail", ex.getLocalizedMessage());
            ex.printStackTrace();
        }
        return insertSuccess;
    }

    public boolean existTrack(ITrackInfo trackInfo){
        if (trackInfo == null){
            return false;
        }
        return localStorageManager.existTrack(trackInfo.getTrackName());
    }

    public boolean deleteTrack(ITrackInfo trackInfo){
        if (trackInfo == null){
            return false;
        }
        return localStorageManager.deleteTrack(trackInfo.getTrackName());
    }

}
