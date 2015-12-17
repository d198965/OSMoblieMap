package org.osmdroid.tileprovider.cache;

import android.graphics.drawable.Drawable;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;

/**
 * Created by zdh on 15/12/16.
 */
public final class NewMapTileCache {
    protected final Object mCachedTilesLockObject = new Object();
    protected NewLRUMapTileCache mCachedTiles;

    // ===========================================================
    // Constructors
    // ===========================================================

    public NewMapTileCache() {
        this(OpenStreetMapTileProviderConstants.CACHE_MAPTILESIZE_DEFAULT);
    }

    /**
     * @param maxCacheSize
     *            max storage size
     */
    public NewMapTileCache(final int maxCacheSize) {
        this.mCachedTiles = new NewLRUMapTileCache(maxCacheSize);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public void ensureCapacity(final int newCapacity) {
        synchronized (mCachedTilesLockObject) {
            mCachedTiles.ensureCapacity(newCapacity);
        }
    }

    public Drawable getMapTile(final MapTile aTile) {
        synchronized (mCachedTilesLockObject) {
            return this.mCachedTiles.get(aTile);
        }
    }

    public void putTile(final MapTile aTile, final Drawable aDrawable) {
        if (aDrawable != null) {
            synchronized (mCachedTilesLockObject) {
                this.mCachedTiles.put(aTile, aDrawable);
            }
        }
    }

    // ===========================================================
    // Methods from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    public boolean containsTile(final MapTile aTile) {
        synchronized (mCachedTilesLockObject) {
            return this.mCachedTiles.containsKey(aTile);
        }
    }

    public void clear() {
        synchronized (mCachedTilesLockObject) {
            this.mCachedTiles.clear();
        }
    }
}
