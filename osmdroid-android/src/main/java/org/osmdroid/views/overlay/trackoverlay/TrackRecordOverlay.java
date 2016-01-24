package org.osmdroid.views.overlay.trackoverlay;

import android.content.Context;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.api.Position;
import org.osmdroid.pysicalmap.render.SimpleRender;
import org.osmdroid.track.BasicTrack;
import org.osmdroid.track.BasicTrackPoint;
import org.osmdroid.track.ITrackInfo;
import org.osmdroid.track.ITrackPath;
import org.osmdroid.track.ITrackPoint;
import org.osmdroid.track.cache.tracksaver.TrackSaverManager;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

/**
 * Created by zdh on 16/1/20.
 */
public class TrackRecordOverlay extends Overlay implements TrackSaverManager.ITrackSaveListener{
    TrackSaverManager mTrackSaverManager;
    ITrackPath mTempTrack;

    private SimpleRender mRender;
    private Context mContext;
    private MapView mMapView;

    public TrackRecordOverlay(Context ctx,TrackSaverManager trackSaverManager) {
        super(ctx);
        mContext = ctx;
        mTrackSaverManager = trackSaverManager;

        if (mTrackSaverManager != null){
            mTrackSaverManager.setTrackSaveListener(this);
            mTempTrack = mTrackSaverManager.getTempTrack();
        }

        if (mTempTrack == null){
            mTempTrack = new BasicTrack(0,"temtrack");
        }
        mRender = new SimpleRender(255,0xff5566,5,4,0x323232);
    }

    @Override
    protected void draw(Canvas canvas, MapView osmv, boolean shadow) {
        if (mTempTrack != null && !shadow){
            mTempTrack.onDraw(canvas,mRender,osmv.getProjection());
            mMapView = osmv;
        }
    }

    @Override
    public void onDetach(MapView mapView) {
        if (mTrackSaverManager != null){
            mTrackSaverManager.onDestroy();
        }
        super.onDetach(mapView);
    }

    @Override
    public void onSaveTrackSuccess(ITrackPath trackPath) {
        Toast.makeText(mContext,"save trackSuccss :"+trackPath.getTrackName(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveTrackFail(ITrackPath trackPath, String message) {
        Toast.makeText(mContext,message+" save fail :"+trackPath.getTrackName(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInsertTrackPoint(ITrackInfo trackPath, ITrackPoint trackPoint) {
        Toast.makeText(mContext," save trackpoint :"+trackPoint.toString(),Toast.LENGTH_LONG).show();
        mTempTrack.addTrackPoint(trackPoint);
        if (mMapView != null){
            mMapView.postInvalidate();
        }
    }
}
