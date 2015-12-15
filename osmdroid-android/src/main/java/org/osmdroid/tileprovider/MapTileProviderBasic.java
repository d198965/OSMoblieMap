package org.osmdroid.tileprovider;

import android.content.Context;

import org.osmdroid.tileprovider.cache.TileDBWriter;
import org.osmdroid.tileprovider.cache.TileFileWriter;
import org.osmdroid.tileprovider.modules.DatabaseFileArchive;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.INetworkAvailablityCheck;
import org.osmdroid.tileprovider.modules.MapTileAssetsProvider;
import org.osmdroid.tileprovider.modules.MapTileDownloader;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileFilesystemProvider;
import org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;

/**
 * This top-level tile provider implements a basic tile request chain which includes a
 * {@link MapTileFilesystemProvider} (a file-system cache), a {@link MapTileFileArchiveProvider}
 * (archive provider), and a {@link MapTileDownloader} (downloads map tiles via tile source).
 *
 * @author Marc Kurtz
 */
public class MapTileProviderBasic extends MapTileProviderArray implements IMapTileProviderCallback {

    // private static final Logger logger = LoggerFactory.getLogger(MapTileProviderBasic.class);

    /**
     * Creates a {@link MapTileProviderBasic}.
     */
    public MapTileProviderBasic(final Context pContext) {
        this(pContext, TileSourceFactory.DEFAULT_TILE_SOURCE);
    }

    /**
     * Creates a {@link MapTileProviderBasic}.
     */
    public MapTileProviderBasic(final Context pContext, final ITileSource pTileSource) {
        this(new SimpleRegisterReceiver(pContext), new NetworkAvailabliltyCheck(pContext),
                pTileSource, pContext);
    }

    /**
     * Creates a {@link MapTileProviderBasic}.
     */
    public MapTileProviderBasic(final IRegisterReceiver pRegisterReceiver,
                                final INetworkAvailablityCheck aNetworkAvailablityCheck, final ITileSource pTileSource,
                                final Context pContext) {
        super(pTileSource, pRegisterReceiver);

        final MapTileAssetsProvider assetsProvider = new MapTileAssetsProvider(
                pRegisterReceiver, pContext.getAssets(), pTileSource);
        mTileProviderList.add(assetsProvider);

//		final MapTileFilesystemProvider fileSystemProvider = new MapTileFilesystemProvider(
//				pRegisterReceiver, pTileSource);
//		mTileProviderList.add(fileSystemProvider);
//
//		final MapTileFileArchiveProvider archiveProvider = new MapTileFileArchiveProvider(
//				pRegisterReceiver, pTileSource);
//		mTileProviderList.add(archiveProvider);

// 		final TileFileWriter tileWriter = new TileFileWriter();
//		final MapTileDownloader downloaderProvider = new MapTileDownloader(pTileSource, tileWriter,
//				aNetworkAvailablityCheck);
//		mTileProviderList.add(downloaderProvider);

        IArchiveFile[] pArchives = new IArchiveFile[1];
        pArchives[0] = new DatabaseFileArchive();
        final MapTileFileArchiveProvider archiveProvider = new MapTileFileArchiveProvider(
                pRegisterReceiver, pTileSource,pArchives);//注册文件
        mTileProviderList.add(archiveProvider);

        mTileProviderList.add(archiveProvider);
        final TileDBWriter tileDBWriter = new TileDBWriter();
        final MapTileDownloader mapTileDownloader = new MapTileDownloader(pTileSource, tileDBWriter, aNetworkAvailablityCheck);
        mTileProviderList.add(mapTileDownloader);
    }
}
