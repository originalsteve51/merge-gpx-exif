package svptech.gpsmerge.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.teamdev.jxmaps.ControlPosition;
import com.teamdev.jxmaps.InfoWindow;
import com.teamdev.jxmaps.LatLng;
import com.teamdev.jxmaps.LatLngBounds;
import com.teamdev.jxmaps.Map;
import com.teamdev.jxmaps.MapMouseEvent;
import com.teamdev.jxmaps.MapOptions;
import com.teamdev.jxmaps.MapReadyHandler;
import com.teamdev.jxmaps.MapStatus;
import com.teamdev.jxmaps.MapTypeControlOptions;
import com.teamdev.jxmaps.Marker;
import com.teamdev.jxmaps.MouseEvent;
import com.teamdev.jxmaps.swing.MapView;

import svptech.gpsmerge.location.GPSLocation;
import svptech.image.utils.RenderImageFromFile;

public class MergeMapView extends MapView
{
	private static final long serialVersionUID = 1L;
	private static List<Marker> allMarkers = new ArrayList<>();
	private static int markerCounter = 0;

	public MergeMapView()
	{

		setOnMapReadyHandler(new MapReadyHandler()
		{
			@Override
			public void onMapReady(MapStatus status)
			{
				if (status == MapStatus.MAP_STATUS_OK)
				{
					final Map map = getMap();
					MapOptions options = new MapOptions();
					MapTypeControlOptions controlOptions = new MapTypeControlOptions();
					controlOptions.setPosition(ControlPosition.TOP_RIGHT);
					options.setMapTypeControlOptions(controlOptions);
					map.setOptions(options);
				}
			}
		} // end MapReadyHandler

		);

	} // end constructor

	public void scaleToContainWaypoints(List<GPSLocation> waypoints, boolean plotEveryWaypoint, RenderImageFromFile image)
	{
		removeAllMarkers();

		// Find the farthest southwest and northeast waypoints. These define the
		// corners of the map view we need to contain the track described by the
		// waypoints.
		GPSLocation minLatitude = waypoints.stream().min((p1, p2) -> p1.getLatitude() > p2.getLatitude() ? -1 : 1)
				.get();

		GPSLocation minLongitude = waypoints.stream().min((p1, p2) -> p1.getLongitude() > p2.getLongitude() ? -1 : 1)
				.get();

		GPSLocation maxLatitude = waypoints.stream().max((p1, p2) -> p1.getLatitude() > p2.getLatitude() ? -1 : 1)
				.get();

		GPSLocation maxLongitude = waypoints.stream().max((p1, p2) -> p1.getLongitude() > p2.getLongitude() ? -1 : 1)
				.get();

		LatLng ne = new LatLng(minLatitude.getLatitude(), minLongitude.getLongitude());
		LatLng sw = new LatLng(maxLatitude.getLatitude(), maxLongitude.getLongitude());

		LatLngBounds bounds = new LatLngBounds(sw, ne);

		// Move and scale the map view to contain the southwest and northeast corners
		// found from the waypoint list
		Map map = getMap();
		map.fitBounds(bounds);

		// When dealing with long gpx paths, there are many waypoints very close to
		// eachother.
		// To keep the path looking like a set of points not all on top of one another,
		// some
		// will be skipped. Determine how many to skip between plotted ones next...
		_SKIP = waypoints.size() / new Double(map.getZoom()).intValue();

		// Never skip more than 75 points.
		if (_SKIP > 75)
			_SKIP = 75;

		// Reset the marker counter, which is used to visually tag the markers with the
		// sequence in which photos
		// were taken.
		makeInfoWindowContent(true);
		double plotpointCount = waypoints.size();

		// Sort the waypoints where photos were taken by time taken, then decorate each
		// waypoint with an
		// ordinal flag that shows the order in which they were taken.
		waypoints.stream().sorted((w1, w2) -> w1.getLocationTime().compareTo(w2.getLocationTime()))
				.forEach(p -> plotWaypoint(p, map, plotpointCount, plotEveryWaypoint, image));
	}

	private void plotWaypoint(GPSLocation p, Map map, double plotpointCount, boolean plotEveryWaypoint, RenderImageFromFile image)
	{
		if (plotPoint(p.getLocation(), plotEveryWaypoint))
		{
			Marker aMarker = new Marker(map);

			// Only add tags to photos. Photo waypoints are all plotted and
			// never skipped. This same method is called to plot the entire
			// GPX path, and when this is done some waypoints are skipped. No
			// tags are added when the GPX path points are plotted.
			if (plotEveryWaypoint)
			{
				String infoTag = makeInfoWindowContent(false);

				InfoWindow info = new InfoWindow(map);
				info.setContent(infoTag);
				info.open(map, aMarker);
				aMarker.setTitle(p.getPhotoFilePathname());

				aMarker.addEventListener("click", new MapMouseEvent()
				{
					@Override
					public void onEvent(MouseEvent mouseEvent)
					{
						System.out.println(aMarker.getTitle());
						try
						{
							image.setImageFilePathname(aMarker.getTitle());
						} 
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}

			aMarker.setPosition(p.getLocation());

			// Keep the list of Markers up to date, as it must be cleared
			// each time the map changes.
			allMarkers.add(aMarker);
		}

	}

	private String makeInfoWindowContent(boolean reset)
	{
		String count = "";
		if (reset)
		{
			markerCounter = 0;
		} else
		{
			count = new Integer(++markerCounter).toString();
		}
		return count;
	}

	/**
	 * Remove markers from the map and from the List of markers in preparation for
	 * placing new markers on the map.
	 */
	private void removeAllMarkers()
	{
		// Remove all markers from the map and then clear the allMarkers
		// collection.
		allMarkers.forEach(m -> m.remove());
		allMarkers.clear();
	}

	static int _COUNT = 0;
	static int _SKIP = 1;

	private boolean plotPoint(LatLng p, boolean plotAll)
	{
		boolean retcode = false;
		if (!plotAll)
		{
			if (_COUNT++ % _SKIP == 0)
				retcode = true;
		} else
		{
			retcode = true;
		}

		return retcode;
	}

}// end class MyMap