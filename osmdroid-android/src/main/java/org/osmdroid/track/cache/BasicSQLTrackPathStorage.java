package org.osmdroid.track.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;

import org.osmdroid.Enum;
import org.osmdroid.api.KeyPairValue;
import org.osmdroid.shape.geom.Extent;
import org.osmdroid.track.BasicTrack;
import org.osmdroid.track.BasicTrackInfo;
import org.osmdroid.track.BasicTrackPoint;
import org.osmdroid.track.ITrackInfo;
import org.osmdroid.track.TrackUtil;


public class BasicSQLTrackPathStorage {
    /**
     * TRACK_TABLE
     */
    protected final static String TRACKS_TABLE = "tracks";
    public final static String FIELD_trackid = "trackid"; // column 0 PK
    public final static String FIELD_name = "track_name"; // column 1
    public final static String FIELD_description = "description";// column 2
    public final static String FIELD_startTime = "start_time"; // column 3
    public final static String FIELD_endTime = "end_time"; // column 4
    public final static String FIELD_trackview = "isview";
    public final static String FIELD_like = "islike";
    public final static String FIELD_measureVersion = "measure_version"; // column 15
    public final static String FIELD_IsUpload = "upload";
    public final static String FIELD_XMAX = "xmax";
    public final static String FIELD_XMIN = "xmin";
    public final static String FIELD_YMAX = "ymax";
    public final static String FIELD_YMIN = "ymin";
    /**
     * TrackPointsTable
     */
    private final static String WAYPOINTS_TABLE = "waypoints";
    public final static String FIELD_pointid = "pointid"; // column 0 PK
    public final static String FIELD_pointname = "point_name"; // column 1
    public final static String FIELD_time = "time";// column 2
    public final static String FIELD_latitude = "latitude";// column 3
    public final static String FIELD_longitude = "longitude";// column 4
    public final static String FIELD_altitude = "altitude";// column 5
    public final static String FIELD_speed = "speed";// column 6
    public final static String FIELD_azimuth = "azimuth";// column 7
    private final static String FIELD_ADRRESS = "address";

    private final static String GMTTime = "GMTTime";

    protected static final String TRACKS_TABLE_DDL = "CREATE TABLE IF NOT EXISTS " + TRACKS_TABLE + " ("
            + FIELD_trackid + " INTEGER primary key autoincrement, "
            + " " + FIELD_name + " VARCHAR,"
            + " " + FIELD_description + " VARCHAR, "
            + " " + FIELD_startTime + " LONG, "
            + " " + FIELD_endTime + " LONG, "
            + " " + FIELD_trackview + " BOOLEAN, "
            + " " + FIELD_like + " BOOLEAN, "
            + " " + FIELD_IsUpload + " text, "
            + " " + FIELD_XMIN + " DOUBLE, "
            + " " + FIELD_XMAX + " DOUBLE, "
            + " " + FIELD_YMIN + " DOUBLE, "
            + " " + FIELD_YMAX + " DOUBLE, "
            + " " + FIELD_measureVersion + " INTEGER);";

    private static final String TRACK_POINTS_TABLE_DDL = "CREATE TABLE IF NOT EXISTS " + WAYPOINTS_TABLE + " ("
            + FIELD_pointid + " INTEGER primary key autoincrement, "
            + " " + FIELD_pointname + " CHAR(20),"
            + " " + FIELD_name + " VARCHAR,"  // Track Name
            + " " + FIELD_time + " LONG, "
            + " " + FIELD_latitude + " DOUBLE DEFAULT '0', "
            + " " + FIELD_longitude + " DOUBLE DEFAULT '0', "
            + " " + FIELD_altitude + " DOUBLE DEFAULT '0', "
            + " " + FIELD_speed + " FLOAT DEFAULT '0', "
            + " " + FIELD_ADRRESS + " VARCHAR, "
            + " " + FIELD_description + " VARCHAR, "
            + " " + FIELD_azimuth + " DOUBLE DEFAULT '0');";

    private SQLiteDatabase db;
    private static String table = TRACKS_TABLE;

    private static BasicSQLTrackPathStorage TrackStorage;
    private String dbPath = "";

    private static class SingletonHolder{
        //静态初始化器，由JVM来保证线程安全
        private static BasicSQLTrackPathStorage instance = new BasicSQLTrackPathStorage();
    }

    public static BasicSQLTrackPathStorage instance(String dbPath){
        synchronized (SingletonHolder.instance){
            if (!TextUtils.isEmpty(dbPath) && SingletonHolder.instance.dbPath == dbPath && SingletonHolder.instance.db != null){
                return SingletonHolder.instance;
            }else{
                SingletonHolder.instance.close();
                SingletonHolder.instance.open(dbPath);
                return SingletonHolder.instance;
            }
        }
    }

    private BasicSQLTrackPathStorage() {

    }

    //---opens the database---
    private BasicSQLTrackPathStorage open(String dataBasePath) {
        try {
            if (db == null) {
                File file = new File(dataBasePath);
                if (!file.exists()) {
                    throw new FileNotFoundException("没有找到文件");
                }
                db = SQLiteDatabase.openDatabase(dataBasePath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
                if (db.getVersion() < 1) {
                    db.beginTransaction();
                    try {
                        // has database of BPT 2.0 and update to BPT 2.1
                        db.execSQL(TrackSQL.SQL_UPDATE_1_1);
                        db.execSQL(TrackSQL.SQL_UPDATE_1_2);
                        db.execSQL(TrackSQL.SQL_UPDATE_1_3);
                        db.execSQL(TRACKS_TABLE_DDL);
                        db.execSQL(TrackSQL.SQL_UPDATE_1_5);
                        db.execSQL(TrackSQL.SQL_UPDATE_1_6);
                        db.execSQL(TrackSQL.SQL_UPDATE_1_7);
                        db.setTransactionSuccessful();
                    } catch (SQLiteException e) {
                        // no database of BPT 2.0 (i.e. new installation)
                        db.execSQL(TRACKS_TABLE_DDL);
                        db.execSQL(TRACK_POINTS_TABLE_DDL);
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                        db.setVersion(1);
                        System.out.println("Database has upgraded to version " + db.getVersion());
                    }
                } else {
                    db.execSQL(TRACKS_TABLE_DDL);
                    db.execSQL(TRACK_POINTS_TABLE_DDL);
                }
                dbPath = dataBasePath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    //---closes the database---
    public void close() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    public long insertTrack(BasicTrack pTrack, boolean isUpload) {
        long rowID = 0;
        db.beginTransaction();
        try {
            String trackName = pTrack.getTrackName();
            Cursor mCursor = db.query(TRACKS_TABLE, new String[]{FIELD_trackid}, FIELD_name + "='" + trackName + "'", null, null, null, null);
            if (mCursor != null && mCursor.moveToNext()) {
                deleteTrack(trackName);
                mCursor.close();
            }
            BasicTrackPoint[] locationList = pTrack.getTrackPoints();
            if (locationList == null)
                locationList = new BasicTrackPoint[0];
            for (BasicTrackPoint loc : locationList) {
                insertTrackPoint(loc, true, pTrack.getTrackName());
            }
            ContentValues cv = new ContentValues();
            cv.put(FIELD_name, pTrack.getTrackName());
            cv.put(FIELD_description, pTrack.getTrackDespretion());
            cv.put(FIELD_trackview, pTrack.isVisible());
            cv.put(FIELD_like, pTrack.isLike());
            cv.put(FIELD_startTime, pTrack.getStartTime());
            cv.put(FIELD_endTime, pTrack.getEndTime());
            cv.put(FIELD_XMAX, pTrack.getExtent().getMaxX());
            cv.put(FIELD_XMIN, pTrack.getExtent().getMinX());
            cv.put(FIELD_YMAX, pTrack.getExtent().getMaxY());
            cv.put(FIELD_YMIN, pTrack.getExtent().getMinY());
            if (isUpload) {
                String nullValue = null;
                cv.put(FIELD_IsUpload, nullValue);
            } else {
                cv.put(FIELD_IsUpload, Enum.HttpEnum.UploadTrack.toString());
            }

            rowID = db.insert(TRACKS_TABLE, null, cv);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return rowID;
    }

    //---deletes a particular track---
    public boolean deleteTrack(String pName) {
        int rowsAffected = 0;
        boolean isInTracksaction = false;
        if (db.inTransaction())
            isInTracksaction = true;
        else {
            db.beginTransaction();
        }
        try {
            String[] deletePoints = {pName};
            BasicTrack deleteTrack = getTrack(pName);
            if (deleteTrack != null) {
                String[] deleteTrackName = {"" + pName};
                rowsAffected = db.delete(WAYPOINTS_TABLE, FIELD_name + " = ?", deletePoints);
                rowsAffected = db.delete(TRACKS_TABLE, FIELD_name + " = ?", deleteTrackName);
                if (!isInTracksaction) {
                    db.setTransactionSuccessful();
                }
            }
        } finally {
            if (!isInTracksaction)
                db.endTransaction();
        }
        return rowsAffected > 0;
    }

    public int getTrackID(String trackName) {
        int trackid = -1;
        try {
            Cursor mCursor = db.query(TRACKS_TABLE, new String[]{FIELD_trackid}, FIELD_name + "='" + trackName + "'", null, null, null, null);
            if (mCursor != null && mCursor.moveToFirst()) {
                trackid = mCursor.getInt(mCursor.getColumnIndex(FIELD_trackid));
                mCursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trackid;
    }

    //---retrieves all the tracks---
    private BasicTrack readTrackFromCursor(Cursor mCursor, boolean readTrackPoint) {
        if (mCursor != null) {
            int trackid = mCursor.getInt(mCursor.getColumnIndex(FIELD_trackid));
            String trackName = mCursor.getString(mCursor.getColumnIndex(FIELD_name));
            String despretion = mCursor.getString(mCursor.getColumnIndex(FIELD_description));
            int isIntView = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(FIELD_trackview)));
            Boolean isView = isIntView == 1;
            int isIntLike = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(FIELD_like)));
            Boolean isLike = isIntLike == 1;
            long startTime = mCursor.getLong(mCursor.getColumnIndex(FIELD_startTime));
            long endTime = mCursor.getLong(mCursor.getColumnIndex(FIELD_endTime));
            double xMax = mCursor.getDouble(mCursor.getColumnIndex(FIELD_XMAX));
            double xMin = mCursor.getDouble(mCursor.getColumnIndex(FIELD_XMIN));
            double yMax = mCursor.getDouble(mCursor.getColumnIndex(FIELD_YMAX));
            double yMin = mCursor.getDouble(mCursor.getColumnIndex(FIELD_YMIN));

            BasicTrackPoint[] trackPoints = null;
            if (readTrackPoint) {
                trackPoints = getTrackPoints(trackName);
            }
            BasicTrackInfo trackInfo = new BasicTrackInfo(isView,isLike,trackName,despretion,startTime,endTime,new Extent(xMin,yMin,xMax,yMax));
            return new BasicTrack(trackid, trackPoints, trackInfo);
        }
        return null;
    }

    public ArrayList<BasicTrack> getAllTracks() {
        Cursor mCursor = db.query(TRACKS_TABLE, new String[]{
                        FIELD_trackid,
                        FIELD_name,
                        FIELD_description,
                        FIELD_startTime,
                        FIELD_endTime,
                        FIELD_trackview,
                        FIELD_like,
                        FIELD_measureVersion},
                null,
                null,
                null,
                null,
                null);
        ArrayList<BasicTrack> tracks = new ArrayList<BasicTrack>();
        while (mCursor != null && mCursor.moveToNext()) {
            BasicTrack newTrack = readTrackFromCursor(mCursor, true);
            if (newTrack != null) {
                tracks.add(newTrack);
            }
        }
        if (mCursor != null) {
            mCursor.close();
        }
        return tracks;
    }


    public ArrayList<ITrackInfo> getTrackInfos() {
        Cursor mCursor = db.query(TRACKS_TABLE, new String[]{
                        FIELD_trackid,
                        FIELD_name,
                        FIELD_description,
                        FIELD_startTime,
                        FIELD_endTime,
                        FIELD_trackview,
                        FIELD_like,
                        FIELD_XMIN,
                        FIELD_XMAX,
                        FIELD_YMIN,
                        FIELD_YMAX,
                        FIELD_measureVersion},
                null,
                null,
                null,
                null,
                null);
        ArrayList<ITrackInfo> tracks = new ArrayList<ITrackInfo>();
        while (mCursor != null && mCursor.moveToNext()) {
            BasicTrack newTrack = readTrackFromCursor(mCursor, false);
            if (newTrack != null && !TrackUtil.TemTrackName.equals(newTrack.getTrackName()))
                tracks.add(newTrack);
        }

        if (mCursor != null) {
            mCursor.close();
        }
        return tracks;
    }

    public ArrayList<ITrackInfo> getTrackInfos(Extent extent){
        if (extent == null){
            return null;
        }
        Cursor mCursor = db.query(TRACKS_TABLE, new String[]{
                        FIELD_trackid,
                        FIELD_name,
                        FIELD_description,
                        FIELD_startTime,
                        FIELD_endTime,
                        FIELD_trackview,
                        FIELD_like,
                        FIELD_XMIN,
                        FIELD_XMAX,
                        FIELD_YMIN,
                        FIELD_YMAX,
                        FIELD_measureVersion},
                null,
                null,
                null,
                null,
                null);
        ArrayList<ITrackInfo> tracks = new ArrayList<ITrackInfo>();
        while (mCursor != null && mCursor.moveToNext()) {
            BasicTrack newTrack = readTrackFromCursor(mCursor, false);
            if (newTrack != null && !TrackUtil.TemTrackName.equals(newTrack.getTrackName()) && extent.intersected(newTrack.getExtent()))
                tracks.add(newTrack);
        }

        if (mCursor != null) {
            mCursor.close();
        }
        return tracks;
    }

    public List<String> getAllTrackNameList() {
        List<String> trackNameList = new ArrayList<String>();
        try {
            Cursor mCursor = db.query(TRACKS_TABLE, new String[]{
                            FIELD_name},
                    null,
                    null,
                    null,
                    null,
                    null);
            while (mCursor != null && mCursor.moveToNext()) {
                String trackName = mCursor.getString(mCursor.getColumnIndex(FIELD_name));
                if (trackName != null && !TrackUtil.TemTrackName.equals(trackName)) {
                    trackNameList.add(trackName);
                }
            }
            if (mCursor != null) {
                mCursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trackNameList;
    }

    public List<BasicTrack> getAllLightTrackList() {
        List<BasicTrack> lightTrackList = new ArrayList<BasicTrack>();
        try {
            Cursor mCursor = db.query(TRACKS_TABLE, new String[]{
                            FIELD_trackid,
                            FIELD_name},
                    null,
                    null,
                    null,
                    null,
                    null);
            while (mCursor != null && mCursor.moveToNext()) {
                int trackid = mCursor.getInt(mCursor.getColumnIndex(FIELD_trackid));
                String trackName = mCursor.getString(mCursor.getColumnIndex(FIELD_name));
                if (trackName != null && !TrackUtil.TemTrackName.equals(trackName)) {
                    BasicTrack temTrack = new BasicTrack(trackid, trackName);
                    lightTrackList.add(temTrack);
                }
            }
            if (mCursor != null) {
                mCursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lightTrackList;
    }

    public ArrayList<BasicTrack> getVisibleTracks() {
        Cursor mCursor = db.query(TRACKS_TABLE, new String[]{
                        FIELD_trackid,
                        FIELD_name,
                        FIELD_description,
                        FIELD_startTime,
                        FIELD_endTime,
                        FIELD_trackview,
                        FIELD_like,
                        FIELD_XMIN,
                        FIELD_XMAX,
                        FIELD_YMIN,
                        FIELD_YMAX,
                        FIELD_measureVersion},
                FIELD_trackview + "=" + "1",
                null,
                null,
                null,
                null);
        ArrayList<BasicTrack> tracks = new ArrayList<BasicTrack>();
        while (mCursor != null && mCursor.moveToNext()) {
            BasicTrack newTrack = readTrackFromCursor(mCursor, true);
            if (newTrack != null)
                tracks.add(newTrack);
        }
        if (mCursor != null) {
            mCursor.close();
        }
        return tracks;
    }

    /**
     */
    public ArrayList<String> getUNUploadTracks() {
        String[] args = {Enum.HttpEnum.UploadTrack.toString()};
        Cursor mCursor = db.query(TRACKS_TABLE, new String[]{
                        FIELD_name},
                FIELD_IsUpload + " like? ",
                args,
                null,
                null,
                null);
        ArrayList<String> tracks = new ArrayList<String>();
        while (mCursor != null && mCursor.moveToNext()) {
            String trackName = mCursor.getString(mCursor.getColumnIndex(FIELD_name));
            if (TrackUtil.TemTrackName.equals(trackName))
                continue;
            tracks.add(trackName);
        }
        if (mCursor != null){
            mCursor.close();
        }
        return tracks;
    }

    /**
     */
    public ArrayList<List<KeyPairValue>> getUNDeleteTracks() {
        String[] args = {Enum.HttpEnum.DeleteTrack.toString()};
        Cursor mCursor = db.query(TRACKS_TABLE, new String[]{
                        FIELD_name,
                        FIELD_trackid},
                FIELD_IsUpload + " like? ",
                args,
                null,
                null,
                null);
        ArrayList<List<KeyPairValue>> tracks = new ArrayList<List<KeyPairValue>>();
        List<KeyPairValue> maps = null;
        while (mCursor!= null && mCursor.moveToNext()) {
            maps = new ArrayList<KeyPairValue>();
            String trackName = mCursor.getString(mCursor.getColumnIndex(FIELD_name));
            int trackID = mCursor.getInt(mCursor.getColumnIndex(FIELD_trackid));
            if (TrackUtil.TemTrackName.equals(trackName))
                continue;
            maps.add(new KeyPairValue("TrackName", trackName));
            maps.add(new KeyPairValue("Trackid", trackID));
            tracks.add(maps);
        }
        if (mCursor != null){
            mCursor.close();
        }
        return tracks;
    }

    public boolean setTrackIsUpload(String trackName, boolean isUpload) throws Exception {
        try {
            ContentValues cv = new ContentValues();
            String[] trackNames = {trackName};
            if (isUpload) {
                cv.put(FIELD_IsUpload, "");
            } else
                cv.put(FIELD_IsUpload, Enum.HttpEnum.UploadTrack.toString());

            return db.update(TRACKS_TABLE, cv, FIELD_name + " = ?", trackNames) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean setTrackIsUpdate(String trackName, boolean isUpdate) throws Exception {
        try {
            ContentValues cv = new ContentValues();
            String[] trackNames = {trackName};
            if (isUpdate) {
                cv.put(FIELD_IsUpload, "");
            } else
                cv.put(FIELD_IsUpload, Enum.HttpEnum.UpdateTrack.toString());

            return db.update(TRACKS_TABLE, cv, FIELD_name + " = ?", trackNames) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean setTrackDeleteFail(String trackName) throws Exception {
        try {
            ContentValues cv = new ContentValues();
            String[] trackNames = {trackName};
            cv.put(FIELD_IsUpload, Enum.HttpEnum.DeleteTrack.toString());
            return db.update(TRACKS_TABLE, cv, FIELD_name + " = ?", trackNames) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean checkIfDBHasTracks() {
        boolean result = false;
        Cursor mCursor = db.rawQuery("select count(" + FIELD_trackid + ")" + " from " + TRACKS_TABLE, null);
        if (mCursor != null) {
            if (!mCursor.moveToFirst())
                return false;
            result = mCursor.getFloat(0) > 0;
            mCursor.close();
        }
        return result;
    }

    //---retrieves a particular track---
    public BasicTrack getTrack(long trackid) throws SQLException {
        Cursor mCursor = db.query(true, TRACKS_TABLE, new String[]{
                        FIELD_trackid,
                        FIELD_name,
                        FIELD_description,
                        FIELD_startTime,
                        FIELD_endTime,
                        FIELD_trackview,
                        FIELD_like,
                        FIELD_XMIN,
                        FIELD_XMAX,
                        FIELD_YMIN,
                        FIELD_YMAX,
                        FIELD_measureVersion
                },
                FIELD_trackid + " = " + trackid,
                null,
                null,
                null,
                null,
                null);
        if (mCursor == null) {
            return null;
        }
        if (!mCursor.moveToFirst()) {
            mCursor.close();
            return null;
        }
        mCursor.close();
        return readTrackFromCursor(mCursor, true);
    }

    public BasicTrack getTrack(String pTrackName) throws SQLException {
        Cursor mCursor = db.query(true, TRACKS_TABLE, new String[]{
                        FIELD_trackid,
                        FIELD_name,
                        FIELD_description,
                        FIELD_startTime,
                        FIELD_endTime,
                        FIELD_trackview,
                        FIELD_like,
                        FIELD_XMIN,
                        FIELD_XMAX,
                        FIELD_YMIN,
                        FIELD_YMAX,
                        FIELD_measureVersion
                },
                FIELD_name + " = '" + pTrackName + "'",
                null,
                null,
                null,
                null,
                null);
        if (mCursor == null) {
            return null;
        }
        if (!mCursor.moveToFirst()) {
            mCursor.close();
            return null;
        }
        mCursor.close();
        return readTrackFromCursor(mCursor, true);
    }

    public boolean existTrack(long trackid) {
        Cursor mCursor = db.query(true, TRACKS_TABLE, new String[]{
                        FIELD_trackid,
                        FIELD_name
                },
                FIELD_trackid + " = " + trackid,
                null,
                null,
                null,
                null,
                null);
        if (mCursor == null) {
            return false;
        }
        if (!mCursor.moveToFirst()) {
            mCursor.close();
            return false;
        }
        mCursor.close();
        return true;
    }

    public boolean existTrack(String trackName) {
        Cursor mCursor = db.query(true, TRACKS_TABLE, new String[]{
                        FIELD_trackid,
                        FIELD_name
                },
                FIELD_name + " = '" + trackName + "'",
                null,
                null,
                null,
                null,
                null);
        if (mCursor == null) {
            return false;
        }
        if (!mCursor.moveToFirst()) {
            mCursor.close();
            return false;
        }
        mCursor.close();
        return true;
    }

    //---updates a track---
    public boolean updateTrack(BasicTrack updateTrack) throws Exception {
        if (updateTrack == null)
            return false;
        BasicTrack oldTrack = getTrack(updateTrack.getFID());
        if (oldTrack == null) {
            throw new Exception("Track is null");
        }
        try {
            ContentValues cv = new ContentValues();
            cv.put(FIELD_name, updateTrack.getTrackName());
            cv.put(FIELD_description, updateTrack.getTrackDespretion());
            cv.put(FIELD_trackview, updateTrack.isVisible());
            cv.put(FIELD_like, updateTrack.isLike());
            db.update(TRACKS_TABLE, cv, FIELD_trackid + "=" + updateTrack.getFID(), null);
            cv = new ContentValues();
            cv.put(FIELD_name, updateTrack.getTrackName());
            String[] trackName = {oldTrack.getTrackName()};
            return db.update(WAYPOINTS_TABLE, cv, FIELD_name + "= ?", trackName) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    public boolean updateTrackPoint(BasicTrackPoint trackPoint) {
        ContentValues cv = new ContentValues();
        cv.put(FIELD_pointname, trackPoint.getName());
        cv.put(FIELD_ADRRESS, trackPoint.getAddress());
        cv.put(FIELD_description, trackPoint.getDescription());
        long value = trackPoint.getTime();
        return db.update(WAYPOINTS_TABLE, cv, FIELD_time + " = " + value, null) > 0;
    }

    public int getPointID(String trackName, long pointTime) {
        int num = -1;
        try {
            Cursor cursor = db.query(WAYPOINTS_TABLE, new String[]{"count(*)"},
                    FIELD_name + "= '" + trackName + "' and " + FIELD_time + " < " + pointTime,
                    null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                num = (int) cursor.getLong(0);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    //---retrieves all trackPoints---
    public BasicTrackPoint[] getTrackPoints(String trackName) throws SQLException {

        Cursor mCursor = db.query(false, WAYPOINTS_TABLE, new String[]{
                        FIELD_pointname,
                        FIELD_time,
                        FIELD_latitude,
                        FIELD_longitude,
                        FIELD_altitude,
                        FIELD_speed,
                        FIELD_ADRRESS,
                        FIELD_description,
                        FIELD_azimuth,
                },
                FIELD_name + "= '" + trackName + "'",
                null,
                null,
                null,
                null,
                null);
        ArrayList<BasicTrackPoint> arrayList = new ArrayList<BasicTrackPoint>();
        if (mCursor == null) {
            return null;
        }
        while (mCursor.moveToNext()) {
            long time = mCursor.getLong(mCursor.getColumnIndex(FIELD_time));
            double lattitude = mCursor.getDouble(mCursor.getColumnIndex(FIELD_latitude));
            double longitude = mCursor.getDouble(mCursor.getColumnIndex(FIELD_longitude));
            double altitude = mCursor.getDouble(mCursor.getColumnIndex(FIELD_altitude));
            float speed = mCursor.getFloat(mCursor.getColumnIndex(FIELD_speed));
            double azimuth = mCursor.getDouble(mCursor.getColumnIndex(FIELD_azimuth));
            String desprition = mCursor.getString(mCursor.getColumnIndex(FIELD_description));
            String pointname = mCursor.getString(mCursor.getColumnIndex(FIELD_pointname));
            String address = mCursor.getString(mCursor.getColumnIndex(FIELD_ADRRESS));
            BasicTrackPoint newPoint = new BasicTrackPoint(longitude, lattitude, altitude, speed, azimuth, pointname, desprition, address, time);
            arrayList.add(newPoint);
        }
        mCursor.close();
        BasicTrackPoint[] trackPoints = new BasicTrackPoint[arrayList.size()];
        return arrayList.toArray(trackPoints);
    }

    public long insertTrackPoint(BasicTrackPoint newTrackPoint, boolean isInserTrack, String trackName) throws Exception {
        boolean needEndTransaction = false;
        if (!db.inTransaction()) {
            needEndTransaction = true;
            db.beginTransaction();
        }
        try {
            if (newTrackPoint == null)
                return 0;
            if (!isInserTrack) {
                if (!existTrack(trackName))
                    return 0;
            }
            long time = newTrackPoint.getTime();
            ContentValues cv = new ContentValues();
            cv.put(FIELD_pointname, newTrackPoint.getName());
            cv.put(FIELD_name, trackName);
            cv.put(FIELD_time, time);
            cv.put(FIELD_latitude, newTrackPoint.getLatitude());
            cv.put(FIELD_longitude, newTrackPoint.getLongitude());
            cv.put(FIELD_altitude, newTrackPoint.getAltitude());
            cv.put(FIELD_speed, newTrackPoint.getSpeed());

            cv.put(FIELD_azimuth, newTrackPoint.getAzimuth());
            cv.put(FIELD_ADRRESS, newTrackPoint.getAddress());
            cv.put(FIELD_description, newTrackPoint.getDescription());
            long count = db.insert(WAYPOINTS_TABLE, null, cv);
            if (needEndTransaction) {
                db.setTransactionSuccessful();
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (needEndTransaction)
                db.endTransaction();
        }
    }

    public static void setGMTTimeString(Location location, String strGMTTime) {
        Bundle bundle = new Bundle();
        bundle.putString(GMTTime, strGMTTime);
        location.setExtras(bundle);
    }

    public static String getGMTTimeString(Location location) {
        Bundle bundle = location.getExtras();
        return bundle.getString(GMTTime);
    }

    public boolean isMeasureUpdated(long trackID) {
        Cursor mCursor = db.rawQuery("select " + FIELD_measureVersion + " from " + TRACKS_TABLE + " where " + FIELD_trackid + "=" + trackID, null);
        int measureVersion = 0;
        if (mCursor != null) {
            mCursor.moveToFirst();
            measureVersion = mCursor.getInt(0);
            mCursor.close();
        }
        return measureVersion > 0;
    }

    public static BasicTrack getFakeTrack() {
        BasicTrackPoint temPoint1 = new BasicTrackPoint(114.35610362, 30.5310931, 37, 0, 0, "gps1", "", "", 1000000);
        temPoint1.setGPS(true);
        temPoint1.setTime(new Date(2014, 2, 21, 20, 23, 21).getTime());

        BasicTrackPoint temPoint2 = new BasicTrackPoint(114.34615338, 30.52105378, 37, 0, 0, "gps2", "", "", 100000);
        temPoint2.setGPS(true);
        temPoint2.setTime(new Date(2014, 2, 21, 20, 24, 21).getTime());

        BasicTrackPoint[] dddPoints = {temPoint1, temPoint2};
        return new BasicTrack(1, "first", "", true, dddPoints, true);
    }
}
