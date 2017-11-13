package svptech.gpsmerge.views;

import java.util.List;
import java.util.stream.Collectors;

import com.teamdev.jxmaps.ControlPosition;
import com.teamdev.jxmaps.LatLng;
import com.teamdev.jxmaps.LatLngBounds;
import com.teamdev.jxmaps.Map;
import com.teamdev.jxmaps.MapOptions;
import com.teamdev.jxmaps.MapReadyHandler;
import com.teamdev.jxmaps.MapStatus;
import com.teamdev.jxmaps.MapTypeControlOptions;
import com.teamdev.jxmaps.Marker;
import com.teamdev.jxmaps.swing.MapView;

import svptech.gpsmerge.location.GPSLocation;

public class MergeMapView extends MapView
{
	private static final long serialVersionUID = 1L;

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

					// LatLng sw = new LatLng(35.4088, -80.5795);
					// LatLng ne = new LatLng(35.7079, -79.8136);
					// map.fitBounds(new LatLngBounds(sw, ne));

				}
			}
		} // end MapReadyHandler

		);

	} // end constructor

	public void scaleToContainWaypoints(List<GPSLocation> waypoints, boolean plotEveryWaypoint)
	{
		// Find the farthest southwest and northeast waypoints. These define the
		// corners of the map view we need to contain the track described by the
		// waypoints.

		GPSLocation minLatitude = waypoints.stream()
										   .min((p1, p2) -> p1.getLatitude() > p2.getLatitude() ? -1 : 1)
										   .get();

		GPSLocation minLongitude = waypoints.stream()
										    .min((p1, p2) -> p1.getLongitude() > p2.getLongitude() ? -1 : 1)
										    .get();

		GPSLocation maxLatitude = waypoints.stream()
										   .max((p1, p2) -> p1.getLatitude() > p2.getLatitude() ? -1 : 1)
										   .get();

		GPSLocation maxLongitude = waypoints.stream()
										    .max((p1, p2) -> p1.getLongitude() > p2.getLongitude() ? -1 : 1)
										    .get();

		LatLng ne = new LatLng(minLatitude.getLatitude(), minLongitude.getLongitude());
		LatLng sw = new LatLng(maxLatitude.getLatitude(), maxLongitude.getLongitude());
		
		LatLngBounds bounds = new LatLngBounds(sw, ne);
		
		// Move and scale the map view to contain the southwest and northeast corners
		// found from the waypoint list
	    Map map = getMap();
		map.fitBounds(bounds);
		_SKIP = waypoints.size()/new Double(map.getZoom()).intValue();
		if (_SKIP>75) _SKIP=75;
		
		System.out.println("_SKIP is : "+_SKIP);

		List<LatLng> plotpoints = waypoints.stream()
										   .map(w -> new LatLng(w.getLatitude(), w.getLongitude()))
										   .collect(Collectors.toList());
		
		plotpoints.stream()
				  .forEach(p -> {
					  				if (plotPoint(p, plotEveryWaypoint))
					  				{
					  					new Marker(map).setPosition(p);
					  				}
					  						
					  			}
						  );
	}

	static int _COUNT = 0;
	static int _SKIP = 1;
	private boolean plotPoint(LatLng p, boolean plotAll)
	{
		boolean retcode = false;
		if (!plotAll)
		{
			if (_COUNT++ % _SKIP==0) retcode = true;
		}
		else
		{
			retcode = true;
		}
			
		return retcode;
	}

}// end class MyMap