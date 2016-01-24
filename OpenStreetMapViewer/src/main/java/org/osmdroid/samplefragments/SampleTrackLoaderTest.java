package org.osmdroid.samplefragments;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;

import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.modules.MapTileFileStorageProviderBase;
import org.osmdroid.track.cache.trackloader.BasicArchiveTrack;
import org.osmdroid.track.cache.trackloader.AbstractArchiveTrack;
import org.osmdroid.track.cache.trackloader.TrackFileArchiveLoader;
import org.osmdroid.track.cache.trackloader.TrackLoaderBase;
import org.osmdroid.track.cache.trackloader.TrackLoaderManager;
import org.osmdroid.views.overlay.trackoverlay.TrackLoaderOverlay;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zdh on 16/1/20.
 */
public class SampleTrackLoaderTest extends BaseSampleFragment {
    IRegisterReceiver registerReceiver = new IRegisterReceiver() {
        @Override
        public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
            return null;
        }

        @Override
        public void unregisterReceiver(BroadcastReceiver receiver) {

        }
    };
    @Override
    public String getSampleTitle() {
        return "TrackLoader 测试";
    }

    @Override
    protected void addOverlays() {
        super.addOverlays();
        // 组建起来
        List<TrackLoaderBase> trackLoaderBases = new ArrayList<>();

        File OSMDROID_PATH = new File(Environment.getExternalStorageDirectory(),
                "osmdroid/sqlite/tracks.db");
        BasicArchiveTrack archiveTrack = new BasicArchiveTrack(OSMDROID_PATH.getPath());

        ArrayList<AbstractArchiveTrack> archiveTracks = new ArrayList<>();
        archiveTracks.add(archiveTrack);
        trackLoaderBases.add(new TrackFileArchiveLoader(registerReceiver,8,10,archiveTracks));
        TrackLoaderManager trackLoaderManager = new TrackLoaderManager(trackLoaderBases);

        mMapView.getOverlays().add(new TrackLoaderOverlay(trackLoaderManager,getContext()));
    }
}
