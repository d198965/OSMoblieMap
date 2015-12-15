package org.osmdroid.tileprovider.cache;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.util.BitmapUtils;

public class SQLLocalStorageManager implements ILocalStorage {

    private static SQLLocalStorageManager localStorage;

    private static String X_COLUMN = "x";

    private static String Y_COLUMN = "y";

    private static String Z_COLUMN = "z";

    private static String S_COLUMN = "s";

    private static String P_COLUMN = "provider"; //sourcename

    private static String UPDATE_TIME_COLUMN = "updatetime";

    private static final String TILEZ = "tilez";

    private static final String TILEY = "tiley";

    private static final String TILEX = "tilex";

    private static final Bitmap.CompressFormat IMAGE_FORMATE = Bitmap.CompressFormat.JPEG;

    private static SharedPreferences prefs;

    private static String IMAGE_COLUMN = "image";

    private static String TILES_TABLE = "tiles";

    private static String TABLE_DDL = "CREATE TABLE IF NOT EXISTS tiles (x int, y int, z int, s long,provider VARCHAR, image blob,uptatetime TimeStamp NOT NULL DEFAULT (datetime('now','localtime')), PRIMARY KEY (x,y,z,s))";

    private static String INDEX_DDL = "CREATE INDEX IF NOT EXISTS IND on tiles (x,y,z,s)";

    private static String DELETE_SQL = "DELETE FROM tiles";

    private static String GET_SQL = "SELECT * FROM tiles WHERE x=? AND y=? AND z=? AND s=?";

    private static String COUNT_SQL = "SELECT COUNT(*) FROM tiles WHERE x=? AND y=? AND z=? AND s=?";

    private static SQLiteDatabase db;

    public static void init(Application app) {
        prefs = app.getSharedPreferences("iwhere", Context.MODE_PRIVATE);
    }

    public static SQLLocalStorageManager getInstance(File sqliteFile) {
        if (localStorage == null || db == null || !db.isOpen() || !sqliteFile.getPath().equals(db.getPath())) {
            localStorage = new SQLLocalStorageManager(sqliteFile);
        }
        return localStorage;
    }

    public void resetLocalStorage() {
        db.close();
        localStorage = null;
    }

    /**
     *
     */
    private SQLLocalStorageManager(final File pFile) {
        // for dynamically loading different DBs from Preferences.getSQLitePath()
        String sqliteFilePath = pFile.getPath();//TODO
        File mapFile = new File(pFile.getParent());//TODO
        if (!mapFile.exists()) {
            mapFile.mkdirs();
        }
        db = SQLiteDatabase.openDatabase(sqliteFilePath, null,
                SQLiteDatabase.CREATE_IF_NECESSARY);
        db.execSQL(SQLLocalStorageManager.TABLE_DDL);
        db.execSQL(SQLLocalStorageManager.INDEX_DDL);
    }

    public void clear() {
        db.execSQL(SQLLocalStorageManager.DELETE_SQL);
    }

    public BufferedInputStream get(MapTile tile, long sourceID) {
        String sql = SQLLocalStorageManager.GET_SQL;

        Cursor c = db.rawQuery(sql,
                new String[]{
                        String.valueOf(tile.getX()),
                        String.valueOf(tile.getY()),
                        String.valueOf(tile.getZoomLevel()),
                        String.valueOf(sourceID),});

        BufferedInputStream io = null;
        if (c.getCount() != 0) {
            c.moveToFirst();
            byte[] d = c.getBlob(c.getColumnIndex(SQLLocalStorageManager.IMAGE_COLUMN));
            io = new BufferedInputStream(new ByteArrayInputStream(d), 4096);
        }
        c.close();
        return io;
    }

    @Override
    public boolean isExists(MapTile tile, long sourceID) {
        Cursor c = db.rawQuery(SQLLocalStorageManager.COUNT_SQL,
                new String[]{
                        String.valueOf(tile.getX()),
                        String.valueOf(tile.getY()),
                        String.valueOf(tile.getZoomLevel()),
                        String.valueOf(sourceID),});
        c.moveToFirst();
        int count = c.getInt(0);
        c.close();
        return count == 1;
    }

    public void put(MapTile tile, long sourceID,String provider, byte[] data) {
        if (tile == null || data == null)
            return;
        db.delete(SQLLocalStorageManager.TILES_TABLE,
                SQLLocalStorageManager.X_COLUMN + " = ? and " +
                        SQLLocalStorageManager.Y_COLUMN + " = ? and " +
                        SQLLocalStorageManager.Z_COLUMN + " = ? and " +
                        SQLLocalStorageManager.S_COLUMN + " = ?"
                , new String[]{"" + tile.getX(), "" + tile.getY(), "" + tile.getZoomLevel(), "" + sourceID});
        ContentValues initialValues = new ContentValues();
        initialValues.put(SQLLocalStorageManager.X_COLUMN, tile.getX());
        initialValues.put(SQLLocalStorageManager.Y_COLUMN, tile.getY());
        initialValues.put(SQLLocalStorageManager.Z_COLUMN, tile.getZoomLevel());
        initialValues.put(SQLLocalStorageManager.S_COLUMN, sourceID);
        initialValues.put(SQLLocalStorageManager.P_COLUMN, provider);
        initialValues.put(SQLLocalStorageManager.IMAGE_COLUMN, data);
        db.insert(SQLLocalStorageManager.TILES_TABLE, null, initialValues);
    }


    public void put(MapTile tile, long sourceID, byte[] data) {
        if (tile == null || data == null)
            return;
        put(tile,sourceID,null,data);
    }

    public void put(MapTile tile, long sourceID, Bitmap data) throws Exception {
        if (data == null) {
            throw new Exception("data is null!");
        }
        put(tile, sourceID, BitmapUtils.bitmapToBytes(data, IMAGE_FORMATE));
    }

    public Bitmap getRaw(MapTile tile, long sourceID) {
        Cursor mCursor = db.query(SQLLocalStorageManager.TILES_TABLE, new String[]{SQLLocalStorageManager.IMAGE_COLUMN},
                SQLLocalStorageManager.X_COLUMN + " = " + tile.getX() + " AND " +
                        SQLLocalStorageManager.Y_COLUMN + " = " + tile.getY() + " AND " +
                        SQLLocalStorageManager.Z_COLUMN + " = " + tile.getZoomLevel() + " AND " +
                        SQLLocalStorageManager.S_COLUMN + " = " + sourceID,
                null, null, null, null);
        if (mCursor == null) {
            return null;
        }
        if (!mCursor.moveToNext()) {
            mCursor.close();
            return null;
        }
        Bitmap pic = BitmapUtils.bytesToBitmap(mCursor.getBlob(mCursor.getColumnIndex(SQLLocalStorageManager.IMAGE_COLUMN)));
        return pic;
    }

    public Set<String> getProviders(){
        Set<String> providers = new HashSet<>();
        try{
            Cursor cursor = db.rawQuery("SELECT distinct "+P_COLUMN+" FROM "+TILES_TABLE,null);
            if (cursor ==null){
                return providers;
            }
            while(cursor.moveToNext()) {
                providers.add(cursor.getString(0));
            }
            cursor.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return providers;
    }

    public boolean deleteLastTile() {
        if (db == null) {
            return false;
        }
       // String selectSQL = "delete * from " + SQLLocalStorageManager.TILES_TABLE + " order by " + SQLLocalStorageManager.UPDATE_TIME + " desc limit 1";
        int itemCount = db.delete(SQLLocalStorageManager.TILES_TABLE,
                "1=1 order by " + SQLLocalStorageManager.UPDATE_TIME_COLUMN + " desc limit 1",
                null);
        return itemCount > 0;
    }

    public long localStorageSize() {
        if (db == null) {
            return 0;
        }
        File temFile = new File(db.getPath());
        return temFile.length();
    }

    public String toString(){
        if (db == null){
            return "db is null";
        }
        return db.getPath();
    }

    /**
     *
     */
//    public static void putTile(MapTile tile) {
//        put(TILEX, tile.getX());
//        put(TILEY, tile.getY());
//        put(TILEZ, tile.getZoomLevel());
//    }

    private static MapTile getTile(double longitude, double latitude, int zoom) {
        double maxlat = Math.PI;
        double lat = latitude;

        if (lat > 90) lat = lat - 180;
        if (lat < -90) lat = lat + 180;
        // conversion degre=>radians
        double phi = Math.PI * lat / 180;
        double res;
        res = 0.5 * Math.log((1 + Math.sin(phi)) / (1 - Math.sin(phi)));
        double maxTileY = Math.pow(2, (double) zoom);
        int tiley = (int) (((1 - res / maxlat) / 2) * (maxTileY));


        double maxLong = Math.PI;
        double lon = longitude;

        if (lon > 180) lon = lon - 360;
        if (lon < -180) lon = lon + 360;
        // conversion degre=>radians
        phi = Math.PI * lon / 360;

        res = 0.5 * Math.log((1 + Math.sin(phi)) / (1 - Math.sin(phi)));
        double maxTileX = Math.pow(2, (double) zoom);
        int tilex = (int) (((1 - res / maxLong) / 2) * (maxTileX));

        MapTile theMapTile = new MapTile(tilex, tiley, zoom);
        return theMapTile;
    }

    /**
     * @return
     */
    public static MapTile getTile() {
        int x, y, z; //
        x = prefs.getInt(TILEX, 113); //0
        y = prefs.getInt(TILEY, 30);  //0
        z = prefs.getInt(TILEZ, 13);  //16

        return getTile(113.9, 30, 13);
    }

    private static void put(String name, Object value) {
        SharedPreferences.Editor editor = prefs.edit();
        if (value.getClass() == Boolean.class) {
            editor.putBoolean(name, (Boolean) value);
        }
        if (value.getClass() == String.class) {
            editor.putString(name, (String) value);
        }
        if (value.getClass() == Integer.class) {
            editor.putInt(name, ((Integer) value).intValue());
        }
        editor.commit();
    }


}
