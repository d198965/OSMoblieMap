package org.osmdroid.api.georeference.geoutils;

import org.osmdroid.api.GeoPoint;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.MapPoint;
import org.osmdroid.api.georeference.Datum;
import org.osmdroid.tileprovider.MapTile;


public class GeoUtils {
	static int TILE_SIZE = 256;
	public static double Radius = 6378137;//KM
	public static double datumE = 0;//
	public static double LAT = 0;
	public static double LON = 0;//
	public final static MapPoint defaultCenter = new MapPoint(114.3,30.59);//
	public final static double arcToAngle = 57.295779513082320876798154814105;
	public final static double angleToArc = 0.01745329251994329576923690768489;
	
	public final static Datum WGS84 = new Datum("WGS84", 6378137, 1/298.257223563);
	public final static Datum Beijing54 = new Datum("Beijing54", 6378245, 1/298.2997381);
	public final static Datum Xian80 = new Datum("Xian80", 6378137, 1/298.26);
	public final static Datum CGCS2000 = new Datum("CGCS2000",6378137,0.00335281068118231893543414612613);
	public final static AngleQieColPro MectorWorld = new AngleQieColPro("MectorWorld",0);
	
	/**
	 * returns a Rectangle2D with x = lon, y = lat, width=lonSpan,
	 * height=latSpan for an x,y,zoom as used by google.
	 */
	public static IGeoPoint getLatLong(int x, int y, int zoom) {
		double lon = -180; // x
		double lonWidth = 360; // width 360

		// double lat = -90; // y
		// double latHeight = 180; // height 180
		double lat = -1;
		double latHeight = 2;

		int tilesAtThisZoom = 1 << (17 - zoom);
		lonWidth = 360.0 / tilesAtThisZoom;
		lon = -180 + (x * lonWidth);
		latHeight = -2.0 / tilesAtThisZoom;
		lat = 1 + (y * latHeight);

		// convert lat and latHeight to degrees in a transverse mercator projection
		// note that in fact the coordinates go from about -85 to +85 not -90 to 90!
		latHeight += lat;
		latHeight = (2 * Math.atan(Math.exp(Math.PI * latHeight))) - (Math.PI / 2);
		latHeight *= (180 / Math.PI);

		lat = (2 * Math.atan(Math.exp(Math.PI * lat))) - (Math.PI / 2);
		lat *= (180 / Math.PI);

		latHeight -= lat;

		if (lonWidth < 0) {
			lon = lon + lonWidth;
			lonWidth = -lonWidth;
		}

		if (latHeight < 0) {
			lat = lat + latHeight;
			latHeight = -latHeight;
		}


		IGeoPoint point = new GeoPoint(lat,lon);
		return point;
	}
	
	public static boolean isValid(MapTile tile){
		int tileCount = (int) Math.pow(2, 17 - tile.getZoomLevel());
		return (tile.getX()<tileCount && tile.getY()<tileCount);
	}
	
	/**
	 * Returns the pixel offset of a latitude and longitude within a single typical google tile.
	 * @param lat
	 * @param lng
	 * @param zoom
	 * @return
	 */
	public static MapPoint getPixelOffsetInTile(double lat, double lng, int zoom) {
		MapPoint pixelCoords = toZoomedPixelCoords(lat, lng, zoom);

		return new MapPoint(pixelCoords.getX() % TILE_SIZE, pixelCoords.getY() % TILE_SIZE);
	}

	/**
	 * returns the lat/lng as an "Offset Normalized Mercator" pixel coordinate,
	 * this is a coordinate that runs from 0..1 in latitude and longitude with 0,0 being
	 * top left. Normalizing means that this routine can be used at any zoom level and
	 * then multiplied by a power of two to get actual pixel coordinates.
	 * @param lat in degrees
	 * @param lng in degrees
	 * @return
	 */
	public static MapPoint toNormalisedPixelCoords(double lat, double lng) {
		// first convert to Mercator projection
		// first convert the lat lon to mercator coordintes.
		if (lng > 180) {
			lng -= 360;
		}

		lng /= 360;
		lng += 0.5;
		
		lat = 0.5 - ((Math.log(Math.tan((Math.PI / 4)+ ((0.5 * lat) / arcToAngle))) / Math.PI) / 2.0);
		return new MapPoint(lng, lat);
	}

	public static IGeoPoint normalisedPixelCoordsToDatums(double x,double y)
	{
	  double lng= x;
		 lng-=0.5;
		 lng*=360;
		if (lng<-180) {
			lng+=360;
		}
		if (lng>180) {
			lng-=360;
		}
		
		double lat = y;
		double factor1 = 90;
		double factor2= (0.5-lat)*Math.PI*2;
		double factor3 = Math.exp(factor2);
		double factor4 = Math.atan(factor3)*2*arcToAngle;
		lat = factor4-factor1;
		GeoPoint datumPoint = new GeoPoint(lat,lng);
		return datumPoint;
	}
	/**
	 * returns a point that is a google tile reference for the tile containing
	 * the lat/lng and at the zoom level.
	 * 
	 * @param lat
	 * @param lng
	 * @param zoom
	 * @return
	 */
	public static MapPoint toTileXY(double lat, double lng, int zoom) {
		MapPoint normalised = toNormalisedPixelCoords(lat, lng);
		int scale = 1 << (17 - zoom);

		// can just truncate to integer, this looses the fractional
		// "pixel offset"
		return new MapPoint((int) (normalised.getX() * scale), (int) (normalised.getY() * scale));
	}

	/**
	 * returns a point that is a google pixel reference for the particular
	 * lat/lng and zoom assumes tiles are 256x256.
	 * 
	 * @param lat
	 * @param lng
	 * @param zoom
	 * @return
	 */
	public static MapPoint toZoomedPixelCoords(double lat, double lng, int zoom) {
		MapPoint normalised = toNormalisedPixelCoords(lat, lng);
		double scale = (1 << (17 - zoom)) * TILE_SIZE;

		return new MapPoint((normalised.getX() * scale),(normalised.getY() * scale));
	}
	
	public static IGeoPoint checkDatumPoint(IGeoPoint checkPoint)
	{
		double x =0,y = 0;
		if(checkPoint.getLongitude()>180)
			x = 180;
		else if (checkPoint.getLongitude()<-180)
			x = -180;
		else 
			x =checkPoint.getLongitude();
		if(checkPoint.getLatitude()>90)
			y = 90;
		else if (checkPoint.getLatitude()<-90)
			y = -90;
		else 
			y =checkPoint.getLatitude();
		return new GeoPoint(y,x);
	}
	
	public static String TileXYToQuadKey(int tileX, int tileY, int levelOfDetail) {
		StringBuilder quadKey = new StringBuilder();
		for (int i = levelOfDetail; i > 0; i--) {
			char digit = '0';
			int mask = 1 << (i - 1);
			if ((tileX & mask) != 0) {
				digit++;
			}
			if ((tileY & mask) != 0) {
				digit++;
				digit++;
			}
			quadKey.append(digit);
		}
		return quadKey.toString();
	}

}
