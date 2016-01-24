package org.osmdroid.track.cache.tracksaver;

import android.location.Location;
import android.os.Handler;
import android.widget.Toast;

import org.osmdroid.track.BasicTrack;
import org.osmdroid.track.BasicTrackInfo;
import org.osmdroid.track.BasicTrackPoint;
import org.osmdroid.track.ITrackInfo;
import org.osmdroid.track.ITrackPath;
import org.osmdroid.track.ITrackPoint;
import org.osmdroid.track.TrackUtil;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

/**
 * Created by zdh on 16/1/7.
 */
public class TrackSaverManager implements TrackSaverManagerBase<BasicTrack,BasicTrackPoint> ,IMyLocationConsumer {
    public interface ITrackSaveListener{
        void onSaveTrackSuccess(ITrackPath trackPath);
        void onSaveTrackFail(ITrackPath trackPath,String message);
        void onInsertTrackPoint(ITrackInfo trackPath, ITrackPoint trackPoint);
    }
    private BasicTrackInfo mTemTrackInfo;
    private BasicTrackWriter mTrackWriter;
    private ITrackSaveListener mTrackSaverListener;

    public IMyLocationProvider mMyLocationProvider;
    Handler mRecordHandler;
    private boolean mIsRecordEnabled;

    private  final int subTime;
    Runnable recordPositionRunnable = new Runnable() {
        Location prePosition = new Location("");
        @Override
        public void run() {
            Location currentPosition = mMyLocationProvider.getLastKnownLocation();
            if (mIsRecordEnabled && currentPosition!=null && currentPosition.getLatitude() != prePosition.getLatitude()
                    &&currentPosition.getLongitude() != prePosition.getLongitude()){
                insertTempTrackPoint(new BasicTrackPoint(currentPosition,"","","",0));
                prePosition = currentPosition;
            }
            mRecordHandler.postDelayed(this,subTime);
        }
    };

    public TrackSaverManager(String dbPath,int internRecordTime,IMyLocationProvider locationProvider) {
        mTemTrackInfo = new BasicTrackInfo(TrackUtil.TemTrackName);
        mTrackWriter = new BasicTrackWriter(dbPath);
        subTime = internRecordTime;
        mMyLocationProvider = locationProvider;
        mRecordHandler = new Handler();

        if (!existTempTrack()){
            mTrackWriter.saveFile(new BasicTrack(0,TrackUtil.TemTrackName));
        }
    }

    public void setTrackSaveListener(ITrackSaveListener trackSaveListener){
        mTrackSaverListener = trackSaveListener;
    }

    @Override
    public void insertTempTrackPoint(BasicTrackPoint trackPoint) {
        mTrackWriter.insertTrackPoint(mTemTrackInfo,trackPoint);
        if (mTrackSaverListener != null){
            mTrackSaverListener.onInsertTrackPoint(mTemTrackInfo,trackPoint);
        }
    }

    @Override
    public boolean saveTrack(BasicTrack trackPath) {
       boolean isSuccess = mTrackWriter.saveFile(trackPath);
        if (mTrackSaverListener != null){
            if (isSuccess){
                mTrackSaverListener.onSaveTrackSuccess(trackPath);
            }else {
                mTrackSaverListener.onSaveTrackFail(trackPath,"insert dataBase fail");
            }
        }
        return isSuccess;
    }

    @Override
    public boolean saveTrack(String trackName) {
        disableRecord();
        BasicTrack temTrack = getTempTrack();
        temTrack.setTrackName(trackName);
        boolean isSuccess = saveTrack(temTrack);
        if (isSuccess){
            mTrackWriter.deleteTrack(getTempTrackInfo());
        }
        enableRecord();
        return isSuccess;
    }

    public boolean existTempTrack() {
        return mTrackWriter.existTrack(mTemTrackInfo);
    }

    public BasicTrack getTempTrack(){
        return mTrackWriter.getTrack(mTemTrackInfo);
    }

    @Override
    public boolean existTrack(ITrackInfo track) {
        return mTrackWriter.existTrack(track);
    }

    @Override
    public void resetSaverManager() {
        mTrackWriter.deleteTrack(mTemTrackInfo);
        mTemTrackInfo = new BasicTrackInfo(TrackUtil.TemTrackName);
    }

    public BasicTrackInfo getTempTrackInfo(){
        return new BasicTrackInfo(TrackUtil.TemTrackName);
    }


    public boolean enableRecord(){
        boolean success = mMyLocationProvider.startLocationProvider(this);
        mIsRecordEnabled = success;

        // set initial location when enabled
        if (success) {
            mRecordHandler.post(recordPositionRunnable);
        }

        return success;
    }

    public void disableRecord(){
        mMyLocationProvider.stopLocationProvider();
        mRecordHandler.removeCallbacks(recordPositionRunnable);

        mIsRecordEnabled = false;
    }

    public boolean isRecordEnable(){
        return  mIsRecordEnabled;
    }

    @Override
    public void onDestroy() {
        if (mRecordHandler != null && recordPositionRunnable != null) {
            disableRecord();
            recordPositionRunnable = null;
            mRecordHandler = null;
        }
    }

    @Override
    public void onLocationChanged(Location location, IMyLocationProvider source) {

    }
}
