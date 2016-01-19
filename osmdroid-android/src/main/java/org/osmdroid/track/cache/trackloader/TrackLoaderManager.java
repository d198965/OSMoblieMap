package org.osmdroid.track.cache.trackloader;

import org.osmdroid.shape.geom.Extent;
import org.osmdroid.track.ITrackInfo;
import org.osmdroid.track.cache.trackloader.ITrackInfoRequestListener;
import org.osmdroid.track.ITrackPath;
import org.osmdroid.track.cache.trackloader.TrackInfoRequestState;
import org.osmdroid.track.cache.trackloader.TrackRequestState;
import org.osmdroid.track.cache.trackloader.ITrackRequstListener;
import org.osmdroid.track.cache.trackloader.TrackLoaderBase;

import java.util.HashMap;
import java.util.List;

/**
 * Created by zdh on 16/1/4.
 */
public class TrackLoaderManager extends TrackLoaderManagerBase implements ITrackRequstListener,ITrackInfoRequestListener {
    protected final HashMap<ITrackInfo, TrackRequestState> mWorking;
    private final List<TrackLoaderBase> mTrackLoaders;

    protected final HashMap<Object,TrackInfoRequestState> mRequestInfoWorking;

    public TrackLoaderManager(List<TrackLoaderBase> trackLoaders) {
        mTrackLoaders = trackLoaders;
        mWorking = new HashMap<>();
        mRequestInfoWorking = new HashMap<>();
    }

    // 应该是异步的请求，有的loader是网络loader，需要异步返回
    @Override
    public void requestTrackInfos(Extent extent) {
        if (extent == null ||  mTrackLoaders == null || mTrackLoaders.size()<=0){
            return;
        }
        boolean alreadyInProgress = false;
        synchronized (mRequestInfoWorking){
            alreadyInProgress = mRequestInfoWorking.containsKey(extent);
        }
        if (alreadyInProgress){
            return;
        }
        final TrackInfoRequestState state;
        synchronized (mTrackLoaders){
            final TrackLoaderBase[] loaderArray =
                    new TrackLoaderBase[mTrackLoaders.size()];
            state = new TrackInfoRequestState(extent,
                    mTrackLoaders.toArray(loaderArray), this);
        }

        synchronized (mRequestInfoWorking) {
            // Check again
            alreadyInProgress = mRequestInfoWorking.containsKey(extent);
            if (alreadyInProgress) {
                return;
            }
            mRequestInfoWorking.put(extent, state);
        }

        final TrackLoaderBase trackLoader = getNextTrackLoader(state);
        if (trackLoader != null) {
            trackLoader.requestMapTrackInfoAsync(state);// 异步加载
        } else {
            requestInfoTrackFail("can't find track info!",extent);
        }

    }

    @Override
    public void loadTrack(ITrackInfo trackInfo) {
        if (trackInfo == null){
            super.loadTrackFail("trackinfo is null",null);
            return;
        }
        final ITrackPath trackPath = getTrackFromCache(trackInfo);
        if (trackPath != null || mTrackLoaders == null) {
            if (mTrackLoaders == null){
                super.loadTrackFail("mTrackLoaders is null",trackInfo);
            }else{
                super.loadTrackSuccess(trackPath,trackInfo);
            }
        } else {
            boolean alreadyInProgress = false;
            synchronized (mWorking) {
                alreadyInProgress = mWorking.containsKey(trackInfo);
            }
            if (!alreadyInProgress) {
                final TrackRequestState state;
                synchronized (mTrackLoaders) {
                    final TrackLoaderBase[] loaderArray =
                            new TrackLoaderBase[mTrackLoaders.size()];
                    state = new TrackRequestState(trackInfo,
                            mTrackLoaders.toArray(loaderArray), this);
                }
                synchronized (mWorking) {
                    // Check again
                    alreadyInProgress = mWorking.containsKey(trackInfo);
                    if (alreadyInProgress) {
                        return; //可能为空，异步获取
                    }
                    mWorking.put(trackInfo, state);
                }

                final TrackLoaderBase trackLoader = getNextTrackLoader(state);
                if (trackLoader != null) {
                    trackLoader.loadMapTrackAsync(state);// 异步加载
                } else {
                    loadTrackFail("can't find track !",trackInfo);
                }
            }
        }
    }

    @Override
    public void requestTrackSuccess(ITrackPath trackPath, TrackRequestState requestState) {
        synchronized (mWorking) {
            mWorking.remove(requestState.getTrackInfo());
        }
        super.loadTrackSuccess(trackPath, requestState.getTrackInfo());
    }

    @Override
    public void requestTrackFail(String message, TrackRequestState state) {
        final TrackLoaderBase nextLoader = getNextTrackLoader(state);
        if (nextLoader != null) {
            nextLoader.loadMapTrackAsync(state);// 换个Loader再请求
        } else {
            synchronized (mWorking) {
                mWorking.remove(state.getTrackInfo());
            }
            super.loadTrackFail(message, state.getTrackInfo());
        }
    }

    @Override
    public void requestInfoTrackFail(String message, TrackInfoRequestState requestState) {
        final TrackLoaderBase nextLoader = getNextTrackLoader(requestState);
        if (nextLoader != null) {
            nextLoader.requestMapTrackInfoAsync(requestState);// 换个Loader再请求
        } else {
            synchronized (mRequestInfoWorking) {
                mRequestInfoWorking.remove(requestState.getRequestObject());
            }
            super.requestInfoTrackFail(message, requestState.getRequestObject());
        }
    }

    @Override
    public void requestInfoTrackSuccess(List<ITrackInfo> trackInfos, TrackInfoRequestState requestState) {
        synchronized (mRequestInfoWorking) {
            mRequestInfoWorking.remove(requestState.getRequestObject());
        }
        super.requestInfoTrackSuccess(trackInfos, requestState.getRequestObject());
    }

    @Override
    public void detach() {
        for (int k = 0; mTrackLoaders != null && k < mTrackLoaders.size(); k++) {
            mTrackLoaders.get(k).detach();
        }
        if (mWorking != null) {
            mWorking.clear();
        }
    }

    private TrackLoaderBase getNextTrackLoader(TrackRequestState state){
        if (mTrackLoaders == null || mTrackLoaders.size() <= 0 || state == null){
            return null;
        }
        return  state.getNextProvider();
    }

    private TrackLoaderBase getNextTrackLoader(TrackInfoRequestState state){
        if (mTrackLoaders == null || mTrackLoaders.size() <= 0 || state == null){
            return null;
        }
        return  state.getNextProvider();
    }
}
