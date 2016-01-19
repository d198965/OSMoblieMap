package org.osmdroid.pysicalmap;

import android.graphics.Canvas;

import org.osmdroid.pysicalmap.render.SimpleRender;
import org.osmdroid.shape.Feature.IFeature;

/**
 * Created by zdh on 15/12/18.
 */
public interface IDrawableFeature extends IFeature{
    void onDraw(Canvas canvas, SimpleRender render,CoorT coorT);

    boolean isVisible();

    void setVisible(boolean isVisible);

}
