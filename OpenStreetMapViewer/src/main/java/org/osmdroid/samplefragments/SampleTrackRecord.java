package org.osmdroid.samplefragments;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.osmdroid.track.cache.tracksaver.TrackSaverManager;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.trackoverlay.TrackRecordOverlay;

import java.io.File;

/**
 * Created by zdh on 16/1/21.
 */
public class SampleTrackRecord extends BaseSampleFragment {
    TrackSaverManager trackSaverManager;
    @Override
    public String getSampleTitle() {
        return "Track Record 测试";
    }

    @Override
    protected void addOverlays() {
        super.addOverlays();
        File OSMDROID_PATH = new File(Environment.getExternalStorageDirectory(),
                "osmdroid/sqlite/tracks.db");
        trackSaverManager = new TrackSaverManager(OSMDROID_PATH.getPath(),10000,new GpsMyLocationProvider(getContext()));

        TrackRecordOverlay overlay = new TrackRecordOverlay(getContext(), trackSaverManager);
        mMapView.getOverlays().add(overlay);

        //开始记录
        trackSaverManager.enableRecord();
        setHasOptionsMenu(true);
        setMenuVisibility(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add("保存");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("保存")){
            final EditText inputEditText = new EditText(getContext());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("输入Track Name").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (trackSaverManager.existTempTrack()){
                        String trackName = inputEditText.getText().toString();
                        if ("".equals(trackName)){
                            return;
                        }

                        trackSaverManager.disableRecord();
                        boolean isSaveSuccess = trackSaverManager.saveTrack(trackName);
                        Toast.makeText(getContext(),"保存"+(isSaveSuccess? "成功":"失败"),Toast.LENGTH_LONG).show();
                    }
                }
            }).setView(inputEditText).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
