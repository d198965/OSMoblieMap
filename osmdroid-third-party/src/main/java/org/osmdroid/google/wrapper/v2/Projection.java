package org.osmdroid.google.wrapper.v2;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IProjection;

import android.graphics.Point;

public class Projection implements IProjection {

	private final com.google.android.gms.maps.Projection mProjection;

	private final Point mPoint = new Point();

	public Projection(final com.google.android.gms.maps.Projection aProjection) {
		mProjection = aProjection;
	}

	@Override
	public Point toPixels(final IGeoPoint in, final Point out) {
		final LatLng latLng = new LatLng(in.getLatitude(), in.getLongitude());
		final Point point = mProjection.toScreenLocation(latLng);
		if (out != null) {
			out.x = point.x;
			out.y = point.y;
		}
		return point;
	}

	@Override
	public Point toPixels(double longitude, double latitude, Point reuse) {
		final LatLng latLng = new LatLng(latitude, longitude);
		final Point point = mProjection.toScreenLocation(latLng);
		if (reuse != null) {
			reuse.x = point.x;
			reuse.y = point.y;
		}
		return reuse;
	}
	@Override
	public IGeoPoint fromPixels(final int x, final int y) {
		mPoint.x = x;
		mPoint.y = y;
		final LatLng latLng = mProjection.fromScreenLocation(mPoint);
		return new GeoPoint(latLng);
	}

	@Override
	public float metersToEquatorPixels(final float meters) {
		return 0; // TODO implement this
	}

	@Override
	public IGeoPoint getNorthEast() {
		final LatLngBounds bounds = mProjection.getVisibleRegion().latLngBounds;
		return new GeoPoint(bounds.northeast);
	}

	@Override
	public IGeoPoint getSouthWest() {
		final LatLngBounds bounds = mProjection.getVisibleRegion().latLngBounds;
		return new GeoPoint(bounds.southwest);
	}
}
