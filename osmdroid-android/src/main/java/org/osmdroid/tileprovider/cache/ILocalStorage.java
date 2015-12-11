package org.osmdroid.tileprovider.cache;

import java.io.BufferedInputStream;
import org.osmdroid.tileprovider.MapTile;


public interface ILocalStorage {

	public abstract void clear();

	public abstract boolean isExists(MapTile tile,long sourceID);

	public abstract void put(MapTile tile,long sourceID, byte[] data);

	public abstract BufferedInputStream get(MapTile tile,long sourceID);

	public abstract long localStorageSize();
}