package org.osmdroid.pysicalmap;

import android.graphics.Canvas;

import org.osmdroid.pysicalmap.render.SimpleRender;
import org.osmdroid.shape.Feature.IFeature;
import org.osmdroid.views.Projection;

/**
 * Created by zdh on 15/12/18.
 */
public interface IDrawableFeature extends IFeature{
    void onDraw(Canvas canvas, SimpleRender render,Projection coorT);

    boolean isVisible();

    void setVisible(boolean isVisible);

}
