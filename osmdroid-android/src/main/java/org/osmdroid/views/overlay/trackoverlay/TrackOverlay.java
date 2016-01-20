package org.osmdroid.views.overlay.trackoverlay;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.pysicalmap.render.SimpleRender;
import org.osmdroid.shape.geom.CPoint;
import org.osmdroid.shape.geom.Extent;
import org.osmdroid.track.ITrackInfo;
import org.osmdroid.track.ITrackPath;
import org.osmdroid.track.cache.trackloader.TrackLoaderManager;
import org.osmdroid.track.cache.trackloader.TrackLoaderManagerBase;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

/**
 * Created by zdh on 15/12/23.
 */
public class TrackOverlay extends Overlay implements TrackLoaderManagerBase.ITrackLoaderListener {
    private static final String TAG = TrackOverlay.class.getName();
    TrackLoaderManager mTrackManager;
    private final Extent mViewPort = new Extent(0, 0, 0, 0);
    private IGeoPoint mTopRightGeoPoint;
    private IGeoPoint mBottomLeftGeoPoint;

    private SimpleRender mRender;

    private Canvas mCanvas;
    private Projection mProjection;
    public TrackOverlay(TrackLoaderManager trackLoaderManager, Context ctx) {
        super(ctx);
        mTrackManager = trackLoaderManager;
        mRender = new SimpleRender(255,0xff5566,5,4,0x323232);
    }

    @Override
    protected void draw(Canvas canvas, MapView osmv, boolean shadow) {
        if (shadow) {
            return;
        }
        mProjection = osmv.getProjection();
        mCanvas = canvas;
        // Get the area we are drawing to
        mTopRightGeoPoint = mProjection.getNorthEast();
        mBottomLeftGeoPoint = mProjection.getSouthWest();
        mViewPort.setLeftdown(new CPoint(mBottomLeftGeoPoint.getLongitude(), mBottomLeftGeoPoint.getLatitude()));
        mViewPort.setLeftdown(new CPoint(mTopRightGeoPoint.getLongitude(), mTopRightGeoPoint.getLatitude()));
        drawTracks(mViewPort);
    }

    private void drawTracks(Extent viewExtent) {
        mTrackManager.setTrackLoaderListener(this);
        mTrackManager.onExtentChange(viewExtent);
    }

    @Override
    public void onDetach(MapView mapView) {
        mTrackManager.onDetach();
        super.onDetach(mapView);
    }

    @Override
    public void loadTrackSuccess(ITrackPath trackPath, ITrackInfo trackInfo) {
        if (trackPath == null) {
            return;
        }
        trackPath.onDraw(mCanvas,mRender,mProjection);
    }

    @Override
    public void loadTrackFail(String message, ITrackInfo trackInfo) {
        if (trackInfo == null) {
            return;
        }
        message = message == null ? "" : message;
        Log.e(TAG, trackInfo.getTrackName() + "加载失败：" + message);
    }
}
