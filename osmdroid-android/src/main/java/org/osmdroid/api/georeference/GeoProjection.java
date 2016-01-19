package org.osmdroid.api.georeference;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.MapPoint;

public abstract class GeoProjection {
	public String proName = "";
	public abstract IGeoPoint toDatumCoords(MapPoint flatCoord, Datum datum);
	public abstract MapPoint toFlatCoords(IGeoPoint datumCoord,Datum datum);
	public  GeoProjection(String name) {
		proName = name;
	}
	
}
