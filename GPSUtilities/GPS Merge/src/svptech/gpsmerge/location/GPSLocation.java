package svptech.gpsmerge.location;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.teamdev.jxmaps.LatLng;

public class GPSLocation
{
	private String photoFilePathname;
	
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

}
