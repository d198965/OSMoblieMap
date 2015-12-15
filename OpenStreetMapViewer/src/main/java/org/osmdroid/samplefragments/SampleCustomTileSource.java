package org.osmdroid.samplefragments;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.util.SourceUtil;

/**
 * Simple how to for setting a custom tile source
 * @author alex
 */
public class SampleCustomTileSource extends BaseSampleFragment{

     @Override
     public String getSampleTitle() {
          return "Custom Tile Source";
          
     }
      @Override
     public void addOverlays() {
          mMapView.setTileSource(new OnlineTileSourceBase("USGS Topo", 0, 18, 256, "", 
               new String[] { "http://basemap.nationalmap.gov/ArcGIS/rest/services/USGSTopo/MapServer/tile/" }) {
               @Override
               public String getTileURLString(MapTile aTile) {
                    return getBaseUrl() + aTile.getZoomLevel() + "/" + aTile.getY() + "/" + aTile.getX()
				+ mImageFilenameEnding;
               }

               @Override
               public int getTileSourceType() {
                    return SourceUtil.XY_TILE_SOURCE_TYPE;
               }
          });
          
     }
     
}
