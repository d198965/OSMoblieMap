package org.osmdroid.views.overlay.pathoverlay;

import android.content.Context;
import android.graphics.Canvas;

import org.osmdroid.ResourceProxy;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

/**
 * Created by zdh on 15/12/10.
 */
public class PathOverlay extends Overlay{
    public PathOverlay(ResourceProxy pResourceProxy) {
        super(pResourceProxy);
    }

    @Override
    protected void draw(Canvas c, MapView osmv, boolean shadow) {

    }
}
