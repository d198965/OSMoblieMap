package org.osmdroid.track.cache.tracksaver;

import org.osmdroid.track.BasicTrack;
import org.osmdroid.track.BasicTrackInfo;
import org.osmdroid.track.BasicTrackPoint;
import org.osmdroid.track.ITrackPath;
import org.osmdroid.track.TrackUtil;

/**
 * Created by zdh on 16/1/7.
 */
public class TrackSaverManager implements TrackSaverManagerBase<BasicTrack,BasicTrackPoint>{
    public interface ITrackSaveListener{
        void onSaveTrackSuccess(ITrackPath trackPath);
        void onSaveTrackFaild(ITrackPath trackPath,String message);
    }
    private BasicTrackInfo mTemTrackInfo;
    private BasicTrackWriter mTrackWriter;
    private  ITrackSaveListener mTrackSaverListener;
    public TrackSaverManager(String dbPath) {
        mTemTrackInfo = new BasicTrackInfo(TrackUtil.TemTrackName);
        mTrackWriter = new BasicTrackWriter(dbPath);
    }

    public void setTrackSaveListener(ITrackSaveListener trackSaveListener){
        mTrackSaverListener = trackSaveListener;
    }

    @Override
    public void insertTempTrackPoint(BasicTrackPoint trackPoint) {
        mTrackWriter.insertTrackPoint(mTemTrackInfo,trackPoint);
    }

    @Override
    public void saveTrack(BasicTrack trackPath) {
       boolean isSuccess = mTrackWriter.saveFile(trackPath);
        if (mTrackSaverListener != null){
            if (isSuccess){
                mTrackSaverListener.onSaveTrackSuccess(trackPath);
            }else {
                mTrackSaverListener.onSaveTrackFaild(trackPath,"insert dataBase fail");
            }
        }
    }

    @Override
    public boolean existTempTrack() {
        return mTrackWriter.existTrack(mTemTrackInfo);
    }

    @Override
    public void resetSaverManager() {
        mTrackWriter.deleteTrack(mTemTrackInfo);
        mTemTrackInfo = new BasicTrackInfo(TrackUtil.TemTrackName);
    }
}
