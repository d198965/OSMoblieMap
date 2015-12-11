package org.osmdroid.tileprovider.cache;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import org.osmdroid.api.IMapView;
import org.osmdroid.tileprovider.BitmapPool;
import org.osmdroid.tileprovider.LRUMapTileCache;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.ReusableBitmapDrawable;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.util.BitmapUtils;
import org.osmdroid.util.ConstantValues;

import java.util.LinkedHashMap;

/**
 * Created by zdh on 15/12/9.
 */
public final class NewLRUMapTileCache extends LinkedHashMap<MapTile, Drawable> {

    public interface TileRemovedListener {
        void onTileRemoved(MapTile mapTile);
    }

    private static final long serialVersionUID = -541142277575493335L;

    private int mMaxSize;
    private int mSize = 0 ;
    private TileRemovedListener mTileRemovedListener;

    public NewLRUMapTileCache(final int maxSize) {
        super(2, 0.1f, true);
        mMaxSize = maxSize;
    }

    public void ensureCapacity(final int aMaxSize) {
        if (aMaxSize > mMaxSize) {
            Log.i(IMapView.LOGTAG, "Tile cache increased from " + mMaxSize + " to " + aMaxSize);
            mMaxSize = aMaxSize;
            LRUMapTileCache cd = new LRUMapTileCache(0);
        }
    }

    @Override
    public Drawable remove(final Object aKey) {
        final Drawable drawable = super.remove(aKey);
        // Only recycle if we are running on a project less than 2.3.3 Gingerbread.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            if (drawable instanceof BitmapDrawable) {
                final Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
        }
        if (getTileRemovedListener() != null && aKey instanceof MapTile){
            getTileRemovedListener().onTileRemoved((MapTile) aKey);
        }
        if (drawable instanceof ReusableBitmapDrawable) {//如果是可回收资源，则存入回收池内
            BitmapPool.getInstance().returnDrawableToPool((ReusableBitmapDrawable) drawable);
        }
        return drawable;
    }

    @Override
    public void clear() {
        // remove them all individually so that they get recycled
        while (!isEmpty()) {
            remove(keySet().iterator().next());
        }

        // and then clear
        super.clear();
    }
    @Override
    public Drawable put(MapTile key, Drawable value) {
        mSize += sizeOfDrawable(value);
        return super.put(key, value);
    }
    @Override
    protected boolean removeEldestEntry(final java.util.Map.Entry<MapTile, Drawable> aEldest) {
        if (mSize > mMaxSize) {
            final MapTile eldest = aEldest.getKey();
            if (OpenStreetMapTileProviderConstants.DEBUGMODE) {
                Log.d(IMapView.LOGTAG,"Remove old tile: " + eldest);
            }
            remove(eldest);
            mSize -= sizeOfDrawable(aEldest.getValue());
            // don't return true because we've already removed it
        }
        return false;
    }

    public TileRemovedListener getTileRemovedListener() {
        return mTileRemovedListener;
    }

    public void setTileRemovedListener(TileRemovedListener tileRemovedListener) {
        mTileRemovedListener = tileRemovedListener;
    }

    private int sizeOfDrawable(Drawable drawable){
        if (drawable == null){
            return 0;
        }
        if (drawable instanceof BitmapDrawable){
            return BitmapUtils.getBitmapSize(((BitmapDrawable) drawable).getBitmap());
        }else
        {
            return ConstantValues.DEFAULT_TILE_BITMAP_SIZE;
        }
    }
}
