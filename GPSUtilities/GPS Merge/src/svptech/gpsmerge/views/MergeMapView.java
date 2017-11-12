package svptech.gpsmerge.views;

import java.util.List;

import com.teamdev.jxmaps.ControlPosition;
import com.teamdev.jxmaps.LatLng;
import com.teamdev.jxmaps.LatLngBounds;
import com.teamdev.jxmaps.Map;
import com.teamdev.jxmaps.MapOptions;
import com.teamdev.jxmaps.MapReadyHandler;
import com.teamdev.jxmaps.MapStatus;
import com.teamdev.jxmaps.MapTypeControlOptions;
import com.teamdev.jxmaps.swing.MapView;

import svptech.gpsmerge.common.MergeProcessor;
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

	public void scaleToContainWaypoints(List<GPSLocation> waypoints)
	{
		// Find the farthest southwest and northeast waypoints. These define the
		// corners of the map view we need to contain the track described by the
		// waypoints.
		MergeProcessor mp = new MergeProcessor(null);
		

		GPSLocation minLatitude = waypoints.stream()
										   .min((p1, p2) -> p1.getLatitude() > p2.getLatitude() ? -1 : 0)
										   .get();

		GPSLocation minLongitude = waypoints.stream()
										    .min((p1, p2) -> p1.getLongitude() > p2.getLongitude() ? -1 : 0)
										    .get();

		GPSLocation maxLatitude = waypoints.stream()
										   .max((p1, p2) -> p1.getLatitude() > p2.getLatitude() ? -1 : 0)
										   .get();

		GPSLocation maxLongitude = waypoints.stream()
										    .max((p1, p2) -> p1.getLongitude() > p2.getLongitude() ? -1 : 0)
										    .get();

		LatLng ne = new LatLng(minLatitude.getLatitude(), minLongitude.getLongitude());
		LatLng sw = new LatLng(maxLatitude.getLatitude(), maxLongitude.getLongitude());
		
		LatLngBounds bounds = new LatLngBounds(sw, ne);
		
		// Move and scale the map view to contain the southwest and northeast corners
		// found from the waypoint list
	    Map map = getMap();
		map.fitBounds(bounds);
	}

}// end class MyMap