package svptech.gpsmerge.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

import svptech.gpsmerge.location.GPSLocation;
import svptech.gpsmerge.location.GPXFileReader;
import svptech.gpsmerge.views.MergeMapView;
import svptech.image.utils.RenderImageFromFile;
import svptech.imaging.utils.ImageManipulationUtils;

/**
 * Methods are provided to merge of waypoint data from a GPX file to a set of
 * photo files defined by the content of a source directory.
 * 
 * @author Steve Harding
 *
 */
public class MergeProcessor
{

	private static final int FIRST = 0;
	private static final int LAST = 1;
	private static String cameraTimezone = "America/New_York";

	// Photo timestamps will be considered to be relative to the timezone used when
	// the time
	// was set for the camera. Call this 'Camera Standard Time'.
	// This is needed because the time zone used in the gpx file is GMT, so to
	// compare the timestamps
	// the offset between camera standard time and GMT is needed.
	// The next line of code establishes camera standard time
	private static ZoneId z = ZoneId.of(cameraTimezone);

	// Camera timestamps are in a particular format. To understand this format, the
	// following
	// formatter string is used. I don't know if this varies from camera to camera
	// or not.
	// The string "yyyy:MM:dd HH:mm:ss" is what my Nikon D500 uses.
	private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

	public MergeProcessor(String cameraTimezone)
	{
	}

	public Comparator<GPSLocation> waypointComparator = (p1, p2) -> p1.getLocationTime()
			.compareTo(p2.getLocationTime());

	// private Comparator<File> photoFileComparator = (p1, p2) ->
	// {
	// DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
	// ZoneId z = ZoneId.of(cameraTimezone);
	// String taken1 = "";
	// String taken2 = "";
	// try
	// {
	// taken1 = getWhenTaken(p1);
	// taken2 = getWhenTaken(p2);
	// } catch (Exception e)
	// {
	// e.printStackTrace();
	// }
	//
	// // Obtain the camera standard time from the exif data. Capture it in a
	// // LocalDateTime,
	// // which is useful for time manipulation whe making comparisons to GMT.
	// LocalDateTime localTimeTaken1 = LocalDateTime.parse(taken1, dtf);
	// ZonedDateTime localTimeTakenTZ1 = localTimeTaken1.atZone(z);
	// Instant takenInstant1 = localTimeTakenTZ1.toInstant();
	//
	// LocalDateTime localTimeTaken2 = LocalDateTime.parse(taken2, dtf);
	// ZonedDateTime localTimeTakenTZ2 = localTimeTaken2.atZone(z);
	// Instant takenInstant2 = localTimeTakenTZ2.toInstant();
	// return takenInstant1.compareTo(takenInstant2);
	// };

	public static void updateSourceFilesWithTrackData(String gpxTrackFileName, String photoDirectoryPath,
			String targetDirectoryName, String cameraTimezone, boolean debug, MergeMapView theMapView, RenderImageFromFile image) throws Exception
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
			throw new Exception("Invalid or non-existing gpx file name provided");
		}

		// Validate the target directory name, create it if not there already
		File targetDirectory = new File(targetDirectoryName);
		targetDirectory.mkdir();

		GPSLocation lastPoint = null;
		GPXFileReader gpxFile = null;
		try
		{
			gpxFile = new GPXFileReader(gpxTrackFile, false);

			List<GPSLocation> waypoints = gpxFile.getGPXFileLocations();

			if (debug)
			{
				System.out.println("The gpx file includes " + waypoints.size() + " waypoints.");
			}

			// Open the picture files one by one From their directory. Obtain their
			// timestamp from exif data and use it
			// to locate a GPSLocation in the list that is closest in time.
			File photoDirectory = new File(photoDirectoryPath);
			File[] sourceFiles = photoDirectory.listFiles(new PictureFileFilter());

			if (sourceFiles == null)
			{
				throw new Exception("No photo files were found in the source directory.");
			}
			if (debug)
			{
				System.out.println("The directory contains " + sourceFiles.length + " photos");
			}

			List<GPSLocation> targetPhotoGPSList = new ArrayList<>();

			for (File photoFile : sourceFiles)
			{
				// Obtain the camera standard time from the exif data. Capture it in a
				// LocalDateTime,
				// which is useful for time manipulation whe making comparisons to GMT.
				Instant takenInstant = getInstantWhenTaken(photoFile, dtf, z);

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

					// Add to the list of files changed
					GPSLocation photolocn = new GPSLocation(takenInstant.toString(), lastPoint.getLatitude(),
							lastPoint.getLongitude());
					
					photolocn.setPhotoFilePathname(targetDirectoryName + photoFile.getName());

					System.out.println("Mapped : " + photolocn);
					targetPhotoGPSList.add(photolocn);

				} else
				{
					System.err.println("No close points for the picture taken at time " + takenInstant);
				}

			}

			if (targetPhotoGPSList.size() > 0)
			{
				System.out.println("Scaling and plotting list of size: " + targetPhotoGPSList.size());
				theMapView.scaleToContainWaypoints(targetPhotoGPSList, true, image);
			}

		} 
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		} 
		finally
		{
			if (gpxFile != null)
			{
				gpxFile.close();
			}
		}

	}

	private static Instant getInstantWhenTaken(File photoFile, DateTimeFormatter dtf, ZoneId z)
			throws ImageReadException, IOException, Exception
	{
		LocalDateTime localTimeTaken = LocalDateTime.parse(getWhenTaken(photoFile), dtf);
		ZonedDateTime localTimeTakenTZ = localTimeTaken.atZone(z);
		Instant takenInstant = localTimeTakenTZ.toInstant();
		return takenInstant;
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
	public static String getWhenTaken(File photoFile) throws ImageReadException, IOException, Exception
	{
		// Get all metadata stored in EXIF header
		ImageMetadata metadata = null;
		try
		{
			metadata = Imaging.getMetadata(photoFile);
		}
		catch (ImageReadException ire)
		{
			throw new Exception ("ImageReadException : "+photoFile.toString()+" Details: "+ire.getMessage());
		}
		
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

	/**
	 * Find how many of the source photos overlap with the interval defined by the
	 * earliest and latest waypoints in the gpx file.
	 * 
	 * @return A count of how many source files will be mapped
	 * @param gpxFilePathname
	 * @param srcDirectoryPathname
	 * @throws Exception
	 * @throws IOException
	 * @throws ImageReadException
	 */
	public int getProjectedMergeCount(String gpxFilePathname, String srcDirectoryPathname)
			throws ImageReadException, IOException, Exception
	{

		List<GPSLocation> waypoints = getWaypointsFromFile(gpxFilePathname);

		List<GPSLocation> firstLast = getFirstLast(waypoints, waypointComparator);

		Instant first = firstLast.get(FIRST).getLocationTime();
		Instant last = firstLast.get(LAST).getLocationTime();

		// Now go through source files one at a time and see if their timestamp is in
		// the interval. Each that
		// is in this interval will be mapped.
		File srcDirectory = new File(srcDirectoryPathname);

		// Get a list of File instances
		File[] files = srcDirectory.listFiles(new PictureFileFilter());

		int mergeCount = 0;

		for (File photoFile : files)
		{
			Instant instantTaken = getInstantWhenTaken(photoFile, dtf, z);
			if (instantTaken.isAfter(first) && instantTaken.isBefore(last))
			{
				mergeCount++;
			}
		}

		return mergeCount;
	}

	/**
	 * Identify the first and last items from a stream of T, where
	 * firstness/lastness is determined by a comparator for T.
	 * 
	 * @param stream
	 * @param comp
	 *            Comparator that can determine the earliest of two T instances.
	 * @return
	 */
	public <T> List<T> getFirstLast(List<T> aList, Comparator<T> comp)
	{
		List<T> firstLast = new ArrayList<T>();

		firstLast.add(FIRST, aList.stream().min(comp).get());
		firstLast.add(LAST, aList.stream().max(comp).get());

		return firstLast;
	}

	public List<GPSLocation> getWaypointsFromFile(String gpxFilePath) throws FileNotFoundException, XMLStreamException
	{
		GPXFileReader gpxFile = null;
		List<GPSLocation> waypoints = null;
		try
		{
			gpxFile = new GPXFileReader(new File(gpxFilePath), false);
			waypoints = gpxFile.getGPXFileLocations();
		} catch (FileNotFoundException | XMLStreamException e)
		{
			throw e;
		} finally
		{
			if (gpxFile != null)
			{
				gpxFile.close();
			}
		}
		return waypoints;
	}

	public int getWaypointCount(String gpxFilePathname) throws FileNotFoundException, XMLStreamException
	{
		List<GPSLocation> waypoints = getWaypointsFromFile(gpxFilePathname);
		return waypoints.size();
	}

	public void updateStatusBasedOnGPX(JTextField gpxFileField, JLabel gpxInfoLabel, MergeMapView theMapView, RenderImageFromFile image)
	{
		String gpxStatus = "";
		try
		{
			List<GPSLocation> waypoints = getWaypointsFromFile(gpxFileField.getText());
			gpxStatus = "GPX file contains " + waypoints.size() + " waypoints.";
			theMapView.scaleToContainWaypoints(waypoints, false, image);
		} catch (FileNotFoundException | XMLStreamException e)
		{
			// File problem of some kind. Provide an error message and tell user to retry.
			gpxStatus = "Problem reading GPX file. Select another file.";
		}

		gpxInfoLabel.setText(gpxStatus);
	}

	public void updateDirectoryPhotoCount(String sourceDirectoryPathname, String targetDirectoryPathname,
			String gpxFilePathname, JLabel projectedMergeCount)
			throws Exception
	{

		if (validInputs(sourceDirectoryPathname, targetDirectoryPathname, gpxFilePathname))
		{
			int srcFileCount = countFilesInDirectory(sourceDirectoryPathname);
	
			int tgtFileCount = countFilesInDirectory(targetDirectoryPathname);
	
			// When there is a source directory and there is a gpx file, count how many
			// source photos will be mapped
			// when a merge is performed. Put this information on the window for the user.
			if (srcFileCount != 0 && getWaypointCount(gpxFilePathname) != 0)
			{
				// See how many will merge and place the message in the label named
				// projectedMergeCount
				// based on timestamp overlap
				// between the photos and the waypoints
				int mergeCount = getProjectedMergeCount(gpxFilePathname, sourceDirectoryPathname);
				String projectedAction = mergeCount
						+ " photos will be tagged.";
				projectedMergeCount.setText(projectedAction);
			}
		}
		else
		{
			projectedMergeCount.setText("");
		}
	}

	private boolean validInputs(String sourceDirectoryPathname, String targetDirectoryPathname, String gpxFilePathname)
	{
		boolean allValid = false;
		
		if (validDirectory(sourceDirectoryPathname) && validDirectory(targetDirectoryPathname) && validFile(gpxFilePathname))
		{
			allValid = true;
		}
		
		return allValid;
	}

	private boolean validDirectory(String pathname)
	{
		boolean validFlag = false;
		File path;
		if (pathname != null && pathname.length()!=0)
		{
			path = new File(pathname);
			validFlag = path.exists() && path.isDirectory();
		}
		
		return validFlag;
	}

	private boolean validFile(String pathname)
	{
		boolean validFlag = false;
		File path;
		if (pathname != null && pathname.length()!=0)
		{
			path = new File(pathname);
			validFlag = path.exists() && path.isFile();
		}
		
		return validFlag;
	}

	private int countFilesInDirectory(String targetDirectoryPathname)
	{
		File tgtFile = new File(targetDirectoryPathname);

		File[] files = tgtFile.listFiles(new PictureFileFilter());
		int fileCount = 0;
		if (files != null)
		{
			fileCount = files.length;
		}
		return fileCount;
	}

	public String getCameraTimezone()
	{
		return cameraTimezone;
	}

	public void setCameraTimezone(String cameraTimezone)
	{
		this.cameraTimezone = cameraTimezone;
	}

}
