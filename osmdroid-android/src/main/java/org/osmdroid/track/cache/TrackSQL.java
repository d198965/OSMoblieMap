package org.osmdroid.track.cache;


public class TrackSQL {
	private static String table = BasicSQLTrackPathStorage.TRACKS_TABLE;
	public static final String SQL_UPDATE_1_1 = "DROP TABLE IF EXISTS '"+ table +"_2';";
	public static final String SQL_UPDATE_1_2 = "CREATE TABLE '"+ table +"_2' AS SELECT * FROM '"+ table +"';";
	public static final String SQL_UPDATE_1_3 = "DROP TABLE '"+ table +"';";
	public static final String SQL_UPDATE_1_5 = "INSERT INTO '"+ table +"' (" +
			BasicSQLTrackPathStorage.FIELD_trackid + ", " +
			BasicSQLTrackPathStorage.FIELD_name + ", " +
			BasicSQLTrackPathStorage.FIELD_description + ", " +
			BasicSQLTrackPathStorage.FIELD_startTime + ", " +
			//TrackManager.FIELD_totalTime + ", " +
			BasicSQLTrackPathStorage.FIELD_totalDistance +" , " +
			BasicSQLTrackPathStorage.FIELD_averageSpeed + ", " +
			BasicSQLTrackPathStorage.FIELD_maximumSpeed + ", " +
			BasicSQLTrackPathStorage.FIELD_trackSource + ", " +
			BasicSQLTrackPathStorage.FIELD_measureVersion +
			") SELECT " +
			BasicSQLTrackPathStorage.FIELD_trackid + ", " +
			BasicSQLTrackPathStorage.FIELD_name + ", " +
			BasicSQLTrackPathStorage.FIELD_description + ", " +
			BasicSQLTrackPathStorage.FIELD_startTime + ", " +
			//TrackManager.FIELD_totalTime + ", " +
			BasicSQLTrackPathStorage.FIELD_totalDistance +" , " +
			BasicSQLTrackPathStorage.FIELD_averageSpeed + ", " +
			BasicSQLTrackPathStorage.FIELD_maximumSpeed + ", " +
			BasicSQLTrackPathStorage.FIELD_trackSource + ", " +
			BasicSQLTrackPathStorage.FIELD_measureVersion +
			" FROM '"+ table +"_2';";
	public static final String SQL_UPDATE_1_6 = "UPDATE '"+ table +"' SET "+BasicSQLTrackPathStorage.FIELD_measureVersion+"=0;";
	public static final String SQL_UPDATE_1_7 = "DROP TABLE '"+ table +"_2';";
	
	
}
