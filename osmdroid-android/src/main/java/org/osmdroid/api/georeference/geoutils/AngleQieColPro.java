package org.osmdroid.api.georeference.geoutils;


import org.osmdroid.api.GeoPoint;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.MapPoint;
import org.osmdroid.api.georeference.Datum;
import org.osmdroid.api.georeference.GeoProjection;

/**
 * @author Administrator
 *
 */
public class AngleQieColPro extends GeoProjection
{
	public double startLon = 0;
	public AngleQieColPro(){
		super("正轴等角圆柱投影");
	}
	public AngleQieColPro(String name,double startLon)
	{
		super(name);
		if (startLon>180) {
			startLon-=360;
		}else if (startLon<-180){
			startLon+=360;
		}		
		this.startLon = startLon;
	}
	
	@Override
	public IGeoPoint toDatumCoords(MapPoint flatCoord, Datum datum) {
		// TODO Auto-generated method stub
		double x = flatCoord.getX();
		double y = Math.abs(flatCoord.getY());
		double lon = (x/datum.a+startLon)* GeoUtils.arcToAngle;
		double factor1 = y/datum.a;
		double factor2 = Math.pow(Math.E, factor1);
		//һ�εݹ�
		double factor3 = 2*(Math.atan(factor2)-Math.PI/4);
		double theConstValue = datum.e*Math.sin(factor3+factor3/180);
		double factor4 = (1-theConstValue)/(1+theConstValue);
		double zoomValue = Math.pow(factor4, datum.e/2);
		
		factor2/=zoomValue;
		factor3 = 2*(Math.atan(factor2)-Math.PI/4);
		double lat = factor3 * GeoUtils.arcToAngle;		
		if(flatCoord.getY()<0)
			lat = -lat;
		return new GeoPoint(lat, lon);
	}

	@Override
	public MapPoint toFlatCoords(IGeoPoint datumCoord, Datum datum) {
		// TODO Auto-generated method stub
		double lat = Math.abs(datumCoord.getLatitude());
		double lon = datumCoord.getLongitude();
		lat = lat*Math.PI/180;
		lon = lon*Math.PI/180;
		double x = datum.a*(lon-startLon);		
		double factor1= (Math.PI/4+lat/2);
		double factor2 = datum.e*Math.sin(lat);
		double factor3 = (1-factor2)/( 1+factor2);
		double factor4 = Math.pow(factor3,  datum.e/2);		
		double facotor5=Math.tan(factor1)*factor4;		
		double y = datum.a*Math.log(facotor5);
		if(datumCoord.getLatitude()<=0)
			y = -y;
		MapPoint faltPoint = new MapPoint(x, y);
		return faltPoint;
	}
	
}