package org.osmdroid.tileprovider.cache;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import org.osmdroid.api.IMapView;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.modules.IFilesystemCache;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.util.StreamUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by zdh on 15/12/10.
 */
public class TileDBWriter implements IFilesystemCache{
    private static long mUsedCacheSpace;

    private SQLLocalStorageManager localStorageManager;
    public TileDBWriter() {
        final Thread t = new Thread() {
            @Override
            public void run() {
                mUsedCacheSpace = 0; // because it's static

                calculateDirectorySize(OpenStreetMapTileProviderConstants.TILE_DB_PATH);

                if (mUsedCacheSpace > OpenStreetMapTileProviderConstants.TILE_MAX_CACHE_SIZE_BYTES) {
                    cutCurrentCache();
                }
                if (OpenStreetMapTileProviderConstants.DEBUGMODE) {
                    Log.d(IMapView.LOGTAG,"Finished init thread");
                }
            }
        };
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    /**
     * Get the amount of disk space used by the tile cache. This will initially be zero since the
     * used space is calculated in the background.
     *
     * @return size in bytes
     */
    public static long getUsedCacheSpace() {
        return mUsedCacheSpace;
    }

    // ===========================================================
    // Methods from SuperClass/Interfaces
    // ===========================================================

    @Override
    public boolean saveFile(final ITileSource pTileSource, final MapTile pTile,
                            final InputStream pStream) {

        final File file = new File(OpenStreetMapTileProviderConstants.TILE_DB_PATH.getPath());
        localStorageManager = SQLLocalStorageManager.getInstance(file);
        if (!file.exists()) {
            return false;
        }
        try {
            localStorageManager.put(pTile,pTileSource.getTileSourceID(),pTileSource.name(),CacheUtils.inputStreamToBytes(pStream));
            mUsedCacheSpace = localStorageManager.localStorageSize();
            if (mUsedCacheSpace > OpenStreetMapTileProviderConstants.TILE_MAX_CACHE_SIZE_BYTES) {
                cutCurrentCache(); // TODO perhaps we should do this in the background
            }
        } catch (final IOException e) {
            return false;
        } finally {
        }
        return true;
    }


    private void calculateDirectorySize(final File pDBFile) {
        if (!pDBFile.exists()){
            return;
        }
        mUsedCacheSpace = pDBFile.length();
    }

    /**
     * If the cache size is greater than the max then trim it down to the trim level. This method is
     * synchronized so that only one thread can run it at a time.
     */
    private void cutCurrentCache() {

        final File lock = OpenStreetMapTileProviderConstants.TILE_PATH_BASE;
        synchronized (lock) {
            if (mUsedCacheSpace > OpenStreetMapTileProviderConstants.TILE_TRIM_CACHE_SIZE_BYTES) {

                Log.d(IMapView.LOGTAG, "Trimming tile cache from " + mUsedCacheSpace + " to "
                        + OpenStreetMapTileProviderConstants.TILE_TRIM_CACHE_SIZE_BYTES);
                while (localStorageManager.deleteLastTile() && mUsedCacheSpace > OpenStreetMapTileProviderConstants.TILE_TRIM_CACHE_SIZE_BYTES){
                    mUsedCacheSpace = localStorageManager.localStorageSize();
                    Log.d(IMapView.LOGTAG, "remove one tile success");
                }
                Log.d(IMapView.LOGTAG, "Finished trimming tile cache");
            }
        }
    }


}
