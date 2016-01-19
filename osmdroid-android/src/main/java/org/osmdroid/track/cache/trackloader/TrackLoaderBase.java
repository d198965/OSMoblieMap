package org.osmdroid.track.cache.trackloader;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import org.osmdroid.ConfigurablePriorityThreadFactory;
import org.osmdroid.shape.geom.Extent;
import org.osmdroid.track.ITrackInfo;
import org.osmdroid.track.ITrackPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zdh on 16/1/4.
 */
public abstract class TrackLoaderBase{
    private static String TAG = "TrackLoaderBase";

    /**
     * Gets the human-friendly name assigned to this track provider.
     *
     * @return the thread name
     */
    protected abstract String getName();

    /**
     * Gets the name assigned to the thread for this provider.
     *
     * @return the thread name
     */
    protected abstract String getThreadGroupName();

    /**
     * It is expected that the implementation will construct an internal member which internally
     * implements a {@link TrackLoader}. This method is expected to return a that internal member to
     * methods of the parent methods.
     *
     * @return the internal member of this track loader.
     */
    protected abstract Runnable getTrackLoader();

    protected abstract Runnable getTrackInfoLoader();

    private final ExecutorService mExecutor;

    protected final Object mQueueLockObject = new Object();
    protected final HashMap<ITrackInfo, TrackRequestState> mWorking;// 请求获取Track
    protected final LinkedHashMap<ITrackInfo, TrackRequestState> mPending;// 请求获取Track

    private final Object mWorkingLockObject = new Object();
    private final ArrayMap<Object,TrackInfoRequestState> mTrackInfoWorking;// 请求获取TrackInfo

    public TrackLoaderBase(int pThreadPoolSize, final int pPendingQueueSize) {
        if (pPendingQueueSize < pThreadPoolSize) {
            Log.w(TAG, "The pending queue size is smaller than the thread pool size. Automatically reducing the thread pool size.");
            pThreadPoolSize = pPendingQueueSize;
        }
        mExecutor = Executors.newFixedThreadPool(pThreadPoolSize,
                new ConfigurablePriorityThreadFactory(Thread.NORM_PRIORITY, getThreadGroupName()));

        mTrackInfoWorking = new ArrayMap<>();

        mWorking = new HashMap<ITrackInfo, TrackRequestState>();
        mPending = new LinkedHashMap<ITrackInfo, TrackRequestState>(pPendingQueueSize + 2, 0.1f,
                true) {

            private static final long serialVersionUID = 6455337315681858866L;

            @Override
            protected boolean removeEldestEntry(
                    final Map.Entry<ITrackInfo, TrackRequestState> pEldest) {
                if (size() > pPendingQueueSize) {
                    ITrackInfo result = null;

                    // get the oldest track that isn't in the mWorking queue
                    Iterator<ITrackInfo> iterator = mPending.keySet().iterator();

                    while (result == null && iterator.hasNext()) {
                        final ITrackInfo trackInfo = iterator.next();
                        if (!mWorking.containsKey(trackInfo)) {
                            result = trackInfo;
                        }
                    }

                    if (result != null) {
                        removeTrackFromQueues(result);
                    }
                }
                return false;
            }
        };
    }

    public abstract ArrayList<ITrackInfo> getTrackInfos(Extent extent);

    protected TrackInfoRequestState nextRquestTrackInfo(){
        synchronized (mWorkingLockObject){
            if (mTrackInfoWorking.size() <=0 ) {
                return null;
            }
            return mTrackInfoWorking.valueAt(mTrackInfoWorking.size()-1);
        }
    }

    public void requestMapTrackInfoAsync(final TrackInfoRequestState requestState){
        if (mExecutor.isShutdown()){
            return;
        }
        synchronized (mWorkingLockObject){
            if (mTrackInfoWorking.containsKey(requestState.getRequestObject())){
                return;
            }else{
                mTrackInfoWorking.put(requestState.getRequestObject(),requestState);
            }
        }
        try
        {
            mExecutor.execute(getTrackInfoLoader());
        }catch (Exception ex){
            requestTrackInfoFail(ex.toString(),requestState);
        }
    }

    public void loadMapTrackAsync(final TrackRequestState pState) {
        // Make sure we're not detached
        if (mExecutor.isShutdown())
            return;

        synchronized (mQueueLockObject) {
            // this will put the track in the queue, or move it to the front of
            // the queue if it's already present
            mPending.put(pState.getTrackInfo(), pState);
        }
        try {
            mExecutor.execute(getTrackLoader());
        } catch (final Exception e) {
            Log.w(TAG, "RejectedExecutionException", e);
            requestTrackFail(e.toString(),pState);
        }
    }

    private void clearQueue() {
        synchronized (mQueueLockObject) {
            mPending.clear();
            mWorking.clear();
        }
    }

    private void clearInfoWorking(){
        synchronized (mWorkingLockObject){
            mTrackInfoWorking.clear();
        }
    }

    /**
     * Detach, we're shutting down - Stops all workers.
     */
    public void detach() {
        this.clearQueue();
        this.clearInfoWorking();
        this.mExecutor.shutdown();
    }

    void removeTrackFromQueues(final ITrackInfo mapTrack) {
        synchronized (mQueueLockObject) {
            mPending.remove(mapTrack);
            mWorking.remove(mapTrack);
        }
    }

    protected void requestTrackFail(String message, TrackRequestState requestState){
        removeTrackFromQueues(requestState.getTrackInfo());
        requestState.getTrackLoaderListener().requestTrackFail(message, requestState);
    }

    protected void requestTrackSuccess(ITrackPath trackPath,TrackRequestState requestState){
        removeTrackFromQueues(requestState.getTrackInfo());
        requestState.getTrackLoaderListener().requestTrackSuccess(trackPath, requestState);
    }

    protected void requestTrackInfoFail(String message, TrackInfoRequestState requestState){
        synchronized (mWorkingLockObject){
            mTrackInfoWorking.remove(requestState.getRequestObject());
        }
        requestState.getTrackInfoLoaderListener().requestInfoTrackFail(message, requestState);
    }

    protected void requestTrackInfoSuccess(List<ITrackInfo> trackInfos,TrackInfoRequestState requestState){
        synchronized (mWorkingLockObject){
            mTrackInfoWorking.remove(requestState.getRequestObject());
        }
        requestState.getTrackInfoLoaderListener().requestInfoTrackSuccess(trackInfos, requestState);
    }

    /**
     * Load the requested track. An abstract internal class whose objects are used by worker threads
     * to acquire tracks from servers. It processes tracks from the 'pending' set to the 'working' set
     * as they become available. The key unimplemented method is 'loadTrack'.
     */
    protected abstract class TrackLoader implements Runnable {

        protected abstract void requestTrack(TrackRequestState pState)
                throws CantContinueException;

        protected void onTrackLoaderInit() {
            // Do nothing by default
        }

        protected void onTrackLoaderShutdown() {
            // Do nothing by default
        }

        protected TrackRequestState nextTrack() {

            synchronized (mQueueLockObject) {
                ITrackInfo result = null;

                // get the most recently accessed track
                // - the last item in the iterator that's not already being
                // processed
                Iterator<ITrackInfo> iterator = mPending.keySet().iterator();

                // TODO this iterates the whole list, make this faster...
                while (iterator.hasNext()) {
                    final ITrackInfo trackInfo = iterator.next();
                    if (!mWorking.containsKey(trackInfo)) {
                        result = trackInfo;
                    }
                }

                if (result != null) {
                    mWorking.put(result, mPending.get(result));
                }

                return (result != null ? mPending.get(result) : null);
            }
        }

        /**
         * This is a functor class of type Runnable. The run method is the encapsulated function.
         */
        @Override
        final public void run() {

            onTrackLoaderInit();

            TrackRequestState state;
            while ((state = nextTrack()) != null) {
                try {
                    requestTrack(state);
                } catch (final CantContinueException e) {
                    Log.i(TAG, "Track loader can't continue: " + state.getTrackInfo(), e);
                    clearQueue();
                } catch (final Throwable e) {
                    Log.i(TAG, "Error downloading track: " + state.getTrackInfo(), e);
                }
            }

            onTrackLoaderShutdown();
        }
    }

    /**
     * Thrown by a track loader module in TrackLoader.loadTrack() to signal that it can no longer
     * function properly. This will typically clear the pending queue.
     */
    public class CantContinueException extends Exception {

        public CantContinueException(final String pDetailMessage) {
            super(pDetailMessage);
        }

        public CantContinueException(final Throwable pThrowable) {
            super(pThrowable);
        }
    }
}
