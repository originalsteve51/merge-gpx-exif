package svptech.gpsmerge.location;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.teamdev.jxmaps.LatLng;
import com.teamdev.jxmaps.Marker;

public class GPSLocation
{
	private String photoFilePathname;
	private double zIndex;
	private Marker theMarker;
	
	// We need the time zone used by the camera for its local timestamps.
	static final ZoneId z = ZoneId.of( "America/Montreal" );

	public GPSLocation(String locationTimeString, double latitude, double longitude)
	{
		super();

		ZonedDateTime zdt = Instant.parse(locationTimeString).atZone( z );
		this.locationTime = zdt.toInstant();
		
		this.location = new LatLng(latitude, longitude);
	}
	
	public GPSLocation(Instant anInstant, double latitude, double longitude)
	{
		this.locationTime = anInstant;
		this.location = new LatLng(latitude, longitude);
	}

	/**
	 * The 'Instant' locally (where the camera is) when a point's latitude and longitude are recorded.
	 */
	private Instant locationTime;
	private LatLng location;
	
	public Instant getLocationTime()
	{
		return locationTime;
	}

	public double getLatitude()
	{
		return location.getLat();
	}

	public double getLongitude()
	{
		return location.getLng();
	}

	@Override
	public String toString()
	{
		return "GPSLocation [locationTime=" + locationTime + ", latitude=" + getLatitude() + ", longitude=" + getLongitude()
				+ "]";
	}

	public LatLng getLocation()
	{
		return location;
	}

	public String getPhotoFilePathname()
	{
		return photoFilePathname;
	}

	public void setPhotoFilePathname(String photoFilePathname)
	{
		this.photoFilePathname = photoFilePathname;
	}

	public double getzIndex()
	{
		return zIndex;
	}

	/**
	 * A GPSLocation encapsulates geo data about a photo along with information pertaining
	 * to its display on a Map. It is displayed using a Marker on the Map. The Marker has a
	 * z-index that controls how other objects and it overlay each other on the Map when they
	 * overlap. The application allows a user to click Markers to cause the associated photo to
	 * be displayed in a small viewer. When a Marker is clicked, its z-index is changed to place
	 * it as the lowest Marker on the Map, so it cannot overlay any others. This allows
	 * close Markers to be clicked, revealing their photo, and also revealing any Markers
	 * that lie beneath them.
	 * @param aMarker
	 */
	public void setzIndex(Marker aMarker)
	{
		// Use the system time millis (negated) as the z index for map markers. 
		// This means
		// that each time a z index is set, it will be the lowest in the
		// system because time is marching on. This is useful when setting
		// the zindex on a click of a marker because the marker will move to
		// the bottom (lowest z index) of a stack of overlapped markers.
		double zIndex = new Double(System.currentTimeMillis()*-1);
		aMarker.setZIndex(zIndex);

		this.zIndex = zIndex;
		this.setTheMarker(aMarker);
	}

	public Marker getTheMarker()
	{
		return theMarker;
	}

	/**
	 * The marker is only set when the zIndex is set.
	 * @param theMarker
	 */
	private void setTheMarker(Marker theMarker)
	{
		this.theMarker = theMarker;
	}

}
