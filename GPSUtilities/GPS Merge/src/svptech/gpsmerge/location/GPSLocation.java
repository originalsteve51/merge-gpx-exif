package svptech.gpsmerge.location;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class GPSLocation
{
	// We need the time zone used by the camera for its local timestamps.
	static final ZoneId z = ZoneId.of( "America/Montreal" );

	public GPSLocation(String locationTimeString, double latitude, double longitude)
	{
		super();

		ZonedDateTime zdt = Instant.parse(locationTimeString).atZone( z );
		this.locationTime = zdt.toInstant();
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * The 'Instant' locally (where the camera is) when a point's latitude and longitude are recorded.
	 */
	private Instant locationTime;
	
	private double latitude;
	private double longitude;

	public Instant getLocationTime()
	{
		return locationTime;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	@Override
	public String toString()
	{
		return "GPSLocation [locationTime=" + locationTime + ", latitude=" + latitude + ", longitude=" + longitude
				+ "]";
	}

}
