package org.osmdroid.tileprovider.tilesource;

import org.osmdroid.tileprovider.MapTile;

public abstract class OnlineTileSourceBase extends BitmapTileSourceBase {

    private final String mBaseUrls[];

    /**
     * Constructor
     *
     * @param aName                a human-friendly name for this tile source
     * @param aZoomMinLevel        the minimum zoom level this tile source can provide
     * @param aZoomMaxLevel        the maximum zoom level this tile source can provide
     * @param aTileSizePixels      the tile size in pixels this tile source provides
     * @param aImageFilenameEnding the file name extension used when constructing the filename
     * @param aBaseUrl             the base url(s) of the tile server used when constructing the url to download the tiles
     */
    public OnlineTileSourceBase(final String aName,
                                final int aZoomMinLevel, final int aZoomMaxLevel, final int aTileSizePixels,
                                final String aImageFilenameEnding, final String[] aBaseUrl) {
        super(aName, aZoomMinLevel, aZoomMaxLevel, aTileSizePixels,
                aImageFilenameEnding,generateSourceID(aBaseUrl));
        mBaseUrls = aBaseUrl;
    }

    private static int generateSourceID(String[] baseUrls) {
        if (baseUrls == null || baseUrls.length == 0) {
            return 0;
        }
        int sourceID = 0;
        for (int k = 0; k < baseUrls.length; k++) {
            sourceID |= baseUrls[k].hashCode();
        }
        return sourceID;
    }


    public abstract String getTileURLString(MapTile aTile);

    /**
     * Get the base url, which will be a random one if there are more than one.
     */
    protected String getBaseUrl() {
        return mBaseUrls[random.nextInt(mBaseUrls.length)];
    }
}
