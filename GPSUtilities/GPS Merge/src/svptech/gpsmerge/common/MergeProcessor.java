package svptech.gpsmerge.common;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

import svptech.gpsmerge.location.GPSLocation;
import svptech.gpsmerge.location.GPXFileReader;
import svptech.imaging.utils.ImageManipulationUtils;

public class MergeProcessor
{
	public static void updateSourceFilesWithTrackData(String gpxTrackFileName, String photoDirectoryPath,
			String targetDirectoryName, String cameraTimezone, boolean debug)
	{
		// Make sure the target directory ends with a / character (required later on)
		if (!targetDirectoryName.endsWith("/"))
		{
			targetDirectoryName = targetDirectoryName + "/";
		}

		// Validate the gpx track file existence
		File gpxTrackFile = new File(gpxTrackFileName);
		if (!gpxTrackFile.exists() || !gpxTrackFile.isFile())
		{
			System.err.println(
					"Invalid or non-existing gpx file name passed as the first argument : " + gpxTrackFileName);
			System.exit(-2);
		}

		// Validate the target directory name, create it if not there already
		File targetDirectory = new File(targetDirectoryName);
		targetDirectory.mkdir();

		GPSLocation lastPoint = null;

		try
		{
			GPXFileReader gpxFile = new GPXFileReader(gpxTrackFile, false);

			List<GPSLocation> waypoints = gpxFile.getGPXFileLocations();

			if (debug)
			{
				System.out.println("The gpx file includes " + waypoints.size() + " waypoints.");
			}

			// Open the picture files one by one From their directory. Obtain their
			// timestamp from exif data and use it
			// to locate a GPSLocation in the list that is closest in time.
			File photoDirectory = new File(photoDirectoryPath);
			File[] sourceFiles = photoDirectory.listFiles();

			if (sourceFiles == null)
			{
				System.err.println("No photo files were found in the source directory : " + photoDirectoryPath);
				System.exit(-3);
			}
			System.out.println("The directory contains " + sourceFiles.length + " photos");

			// Photo timestamps will be considered to be relative to the timezone used when
			// the time
			// was set for the camera. Call this 'Camera Standard Time'.
			// This is needed because the time zone used in the gpx file is GMT, so to
			// compare the timestamps
			// the offset between camera standard time and GMT is needed.
			// The next line of code establishes camera standard time
			ZoneId z = ZoneId.of(cameraTimezone);

			// Camera timestamps are in a particular format. To understand this format, the
			// following
			// formatter string is used. I don't know if this varies from camera to camera
			// or not.
			// The string "yyyy:MM:dd HH:mm:ss" is what my Nikon D500 uses.
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

			for (File photoFile : sourceFiles)
			{
				// Obtain the camera standard time from the exif data. Capture it in a
				// LocalDateTime,
				// which is useful for time manipulation whe making comparisons to GMT.
				LocalDateTime localTimeTaken = LocalDateTime.parse(getWhenTaken(photoFile), dtf);
				ZonedDateTime localTimeTakenTZ = localTimeTaken.atZone(z);
				Instant takenInstant = localTimeTakenTZ.toInstant();

				int withinSeconds = 0;
				Object[] closePoints = null;

				while (closePoints == null || closePoints.length == 0 && withinSeconds < 3600)
				{
					withinSeconds += 5;
					closePoints = findClosePoints(waypoints, takenInstant, withinSeconds);
				}

				if (closePoints != null && closePoints.length > 0)
				{
					if (debug)
					{
						System.out.println(
								"Picture at time " + takenInstant + " has " + closePoints.length + " close points");
					}
					for (int j = 0; debug && j < closePoints.length && j < 5; j++)
					{
						System.out.println("Found: " + closePoints[j].toString());
					}

					// @TODO Change this to use the most recent close point data-for now just use
					// the last point
					lastPoint = (GPSLocation) closePoints[closePoints.length - 1];

					// Rewrite the photo file into the target directory including the gps longitude
					// and latitude data
					// from the closest point in the gpx file
					ImageManipulationUtils.setExifGPSTag(photoFile, new File(targetDirectoryName + photoFile.getName()),
							lastPoint.getLongitude(), lastPoint.getLatitude());
				} else
				{
					System.err.println("No close points for the picture taken at time " + takenInstant);
				}

			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}

	private static Object[] findClosePoints(List<GPSLocation> waypoints, Instant takenInstant, int withinSeconds)
	{
		Stream<GPSLocation> streamPoints = waypoints.stream();

		Object[] closePoints = streamPoints
				.filter(p -> p.getLocationTime().isBefore(takenInstant.plusSeconds(withinSeconds))
						&& p.getLocationTime().isAfter(takenInstant.minusSeconds(withinSeconds)))
				.toArray();

		return closePoints;
	}

	/**
	 * Obtain from exif data the timestamp for when the shutter was snapped. This
	 * will be in the time zone used by the camera.
	 * 
	 * @param photoFile
	 * @return
	 * @throws ImageReadException
	 * @throws IOException
	 * @throws Exception
	 */
	private static String getWhenTaken(File photoFile) throws ImageReadException, IOException, Exception
	{
		// Get all metadata stored in EXIF header
		ImageMetadata metadata = Imaging.getMetadata(photoFile);
		String takenTimestamp;

		if (metadata instanceof JpegImageMetadata)
		{
			JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
			takenTimestamp = getTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);

		} else
		{
			throw new Exception("JPEG metadata not found in the photo file");
		}

		// exif tag values are surrounded with single quote marks. These need to be
		// removed.
		// Following assumes there are exactly two quotes, one at the beginning and one
		// at the end of the
		// string.
		takenTimestamp = takenTimestamp.replaceAll("'", "");

		return takenTimestamp;
	}

	private static String getTagValue(final JpegImageMetadata jpegMetadata, final TagInfo tagInfo)
	{
		final TiffField field = jpegMetadata.findEXIFValueWithExactMatch(tagInfo);
		return field.getValueDescription();
	}

}
