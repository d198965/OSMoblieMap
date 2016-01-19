package org.osmdroid.views.overlay.trackoverlay;

import android.content.Context;
import android.graphics.Canvas;

import org.osmdroid.track.cache.trackloader.TrackLoaderManager;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

/**
 * Created by zdh on 15/12/23.
 */
public class TrackOverlay extends Overlay{
    TrackLoaderManager mTrackManager;
    public TrackOverlay(Context ctx) {
        super(ctx);
    }

    @Override
    protected void draw(Canvas c, MapView osmv, boolean shadow) {

    }
}
