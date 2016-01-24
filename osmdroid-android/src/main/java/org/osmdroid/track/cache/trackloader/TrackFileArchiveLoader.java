package org.osmdroid.track.cache.trackloader;

import android.support.annotation.NonNull;

import org.osmdroid.shape.geom.Extent;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.track.ITrackInfo;
import org.osmdroid.track.ITrackPath;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zdh on 16/1/4.
 */
public class TrackFileArchiveLoader extends TrackFileStorageLoaderBase {
    private static String TAG = "TrackFileArchiveLoader";
    private final ArrayList<AbstractArchiveTrack> mArchiveFiles = new ArrayList<AbstractArchiveTrack>();

    public TrackFileArchiveLoader(IRegisterReceiver pRegisterReceiver, int pThreadPoolSize,
                                  int pPendingQueueSize, @NonNull ArrayList<AbstractArchiveTrack> archiveTracks) {
        super(pRegisterReceiver, pThreadPoolSize, pPendingQueueSize);
        for (AbstractArchiveTrack temArchiveTrack : archiveTracks) {
            mArchiveFiles.add(temArchiveTrack);
        }
    }

    @Override
    protected String getName() {
        return "Track File Loader";
    }

    @Override
    protected String getThreadGroupName() {
        return "Track File Loader Thread";
    }

    @Override
    public void detach() {
        while (!mArchiveFiles.isEmpty()) {
            AbstractArchiveTrack t = mArchiveFiles.get(0);
            if (t != null)
                mArchiveFiles.get(0).close();
            mArchiveFiles.remove(0);
        }
        super.detach();
    }

    public ITrackPath getTrackPath(ITrackInfo trackInfo) {
        if (mArchiveFiles == null) {
            return null;
        }
        ITrackPath trackPath = null;
        for (int k = 0; k < mArchiveFiles.size(); k++) {
            trackPath = mArchiveFiles.get(k).getTrackPath(trackInfo);
            if (trackPath != null) {
                break;
            }
        }
        return trackPath;
    }

    @Override
    public ArrayList<ITrackInfo> getTrackInfos(Extent extent) {
        ArrayList<ITrackInfo> trackInfos = new ArrayList<>();
        for (int k = 0; k < mArchiveFiles.size(); k++) {
            List<ITrackInfo> trackInfoList = mArchiveFiles.get(k).getTrackInfos(extent);
            for (int n = 0; trackInfoList != null && n < trackInfoList.size(); n++) {
                if (trackInfos.indexOf(trackInfoList.get(n)) < 0) {
                    trackInfos.add(trackInfoList.get(n));
                }
            }
        }
        return trackInfos;
    }

    @Override
    protected Runnable getTrackLoader() {
        return new TrackLoader();
    }

    @Override
    protected Runnable getTrackInfoLoader() {
        return new Runnable() {
            @Override
            public void run() {
                TrackInfoRequestState requestState = nextRquestTrackInfo();
                if (requestState == null || !(requestState.getRequestObject() instanceof Extent)){
                    requestTrackInfoFail("requestState is invalid",requestState);
                    return;
                }

                if (!getSdCardAvailable()) {
                    requestTrackInfoFail("SdCard is Unavailable",requestState);
                    return;
                }
                try {
                    ArrayList<ITrackInfo> trackInfos = getTrackInfos((Extent)requestState.getRequestObject());
                    if (trackInfos != null){
                        requestTrackInfoSuccess(trackInfos,requestState);
                    }else {
                        requestTrackInfoFail("can't find trackinfo",requestState);
                    }
                } catch (final Throwable e) {
                    requestTrackInfoFail("Error loading trackinfo:"+e.toString(),requestState);
                }
            }
        };
    }

    protected class TrackLoader extends TrackLoaderBase.TrackLoader {

        @Override
        public void requestTrack(final TrackRequestState pState) {
            final ITrackInfo trackInfo = pState.getTrackInfo();

            // if there's no sdcard then don't do anything
            if (!getSdCardAvailable()) {
                requestTrackFail("SdCard is Unavailable",pState);
            }
            try {
                ITrackPath trackPath = getTrackPath(trackInfo);
                if (trackPath != null){
                    requestTrackSuccess(trackPath,pState);
                }else {
                    requestTrackFail("can't find track",pState);
                }
            } catch (final Throwable e) {
                requestTrackFail("Error loading track:"+e.toString(),pState);
            }
        }
    }
}
