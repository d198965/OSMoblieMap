package org.osmdroid.tileprovider.modules;

import java.io.File;
import java.io.InputStream;
import java.util.Set;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.cache.SQLLocalStorageManager;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.ITileSource;

import android.database.sqlite.SQLiteException;
import android.util.Log;
import org.osmdroid.api.IMapView;

/**
 * This is the OSMdroid style database provider. It's an extremely simply sqlite database schema.
 * CREATE TABLE tiles (key INTEGER PRIMARY KEY, provider TEXT, tile BLOB)
 * where the key is the X/Y/Z coordinates bitshifted using the following algorithm
 * key = ((z << z) + x << z) + y;
 */
public class DatabaseFileArchive implements IArchiveFile {

	private SQLLocalStorageManager sqlLocalStorageManager;

	public DatabaseFileArchive(){

	}

	private DatabaseFileArchive(File pFile) {
		sqlLocalStorageManager = SQLLocalStorageManager.getInstance(pFile);
	}

	public static DatabaseFileArchive getDatabaseFileArchive(final File pFile) throws SQLiteException {
		return new DatabaseFileArchive(pFile);

	}

	public Set<String> getTileSources(){
		Set<String> ret = null;
		try {
			ret = sqlLocalStorageManager.getProviders();
		} catch (final Exception e) {
			Log.w(IMapView.LOGTAG,"Error getting tile sources: ", e);
		}
		return ret;
	}

	@Override
	public void init(File pFile) throws Exception {
		sqlLocalStorageManager = SQLLocalStorageManager.getInstance(pFile);
		if (sqlLocalStorageManager == null){
			SQLLocalStorageManager.getInstance(OpenStreetMapTileProviderConstants.TILE_DB_PATH);
		}
	}

	@Override
	public InputStream getInputStream(final ITileSource pTileSource, final MapTile pTile) {
		try {
			return sqlLocalStorageManager.get(pTile,pTileSource.getTileSourceID());
		} catch(final Throwable e) {
			Log.w(IMapView.LOGTAG,"Error getting db stream: " + pTile, e);
		}
		return null;
	}

	@Override
	public void close() {
		if (sqlLocalStorageManager != null){
			sqlLocalStorageManager.resetLocalStorage();
		}
	}

	@Override
	public String toString() {
		return "DatabaseFileArchive [mDatabase=" + sqlLocalStorageManager.toString() + "]";
	}

}
