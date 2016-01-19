package org.osmdroid.track.cache.trackloader;

import org.osmdroid.track.ITrackInfo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class TrackRequestState {

	private final Queue<TrackLoaderBase> mProviderQueue;
	private final ITrackInfo mTrackInfo;
	private TrackLoaderBase mCurrentProvider;
	private ITrackRequstListener mTrackLoaderListener;
	public TrackRequestState(final ITrackInfo trackInfo,
							 final TrackLoaderBase[] providers,
							 final ITrackRequstListener trackListener) {
		mProviderQueue = new LinkedList<TrackLoaderBase>();
		Collections.addAll(mProviderQueue, providers);
		mTrackInfo = trackInfo;
		mTrackLoaderListener = trackListener;
	}

	public ITrackInfo getTrackInfo() {
		return mTrackInfo;
	}

	public boolean isEmpty() {
		return mProviderQueue.isEmpty();
	}

	public ITrackRequstListener getTrackLoaderListener(){
		return mTrackLoaderListener;
	}

	public TrackLoaderBase getNextProvider() {
		mCurrentProvider = mProviderQueue.poll();
		return mCurrentProvider;
	}

	public TrackLoaderBase getCurrentProvider() {
		return mCurrentProvider;
	}
}
