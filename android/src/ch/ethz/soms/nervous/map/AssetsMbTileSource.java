package ch.ethz.soms.nervous.map;

import java.io.InputStream;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.MBTilesFileArchive;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.tilesource.BitmapTileSourceBase.LowMemoryException;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import ch.ethz.soms.nervous.utils.DBAccessHelper;

public class AssetsMbTileSource extends MapTilesCustomSource{

	private String name;
	private MapTileProviderArray providerArray;
	private int minZoom;
	private int maxZoom;

	public AssetsMbTileSource(Context context, String name, int minZoom, int maxZoom) {
		super(context);
		this.name = name;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
		XYTileSource localSource = new XYTileSource("mbtiles", ResourceProxy.string.offline_mode, minZoom, maxZoom, 256, ".png", new String[] { "http://" });

		DBAccessHelper dbah = new DBAccessHelper(context, name + ".mbtiles");
		dbah.createDB();

		IArchiveFile[] files = { MBTilesFileArchive.getDatabaseFileArchive(dbah.getDatabaseFile()) };
		MapTileModuleProviderBase moduleProvider = new MapTileFileArchiveProvider(new SimpleRegisterReceiver(context), localSource, files);
		this.providerArray = new MapTileProviderArray(localSource, null, new MapTileModuleProviderBase[] { moduleProvider });

	}

	public MapTileProviderArray getProviderArray() {
		return providerArray;
	}

	/*
	 * @Override public Drawable getDrawable(String arg0) throws LowMemoryException { return enclosedSource.getDrawable(arg0); }
	 * 
	 * @Override public Drawable getDrawable(InputStream arg0) throws LowMemoryException { return enclosedSource.getDrawable(arg0); }
	 * 
	 * @Override public int getMaximumZoomLevel() { return maxZoom; }
	 * 
	 * @Override public int getMinimumZoomLevel() { return minZoom; }
	 * 
	 * @Override public String getTileRelativeFilenameString(MapTile arg0) { return enclosedSource.getTileRelativeFilenameString(arg0); }
	 * 
	 * @Override public int getTileSizePixels() { return enclosedSource.getTileSizePixels(); }
	 * 
	 * @Override public String localizedName(ResourceProxy arg0) { return enclosedSource.localizedName(arg0); }
	 * 
	 * @Override public String name() { return enclosedSource.name(); }
	 * 
	 * @Override public int ordinal() { return enclosedSource.ordinal(); }
	 */
}
