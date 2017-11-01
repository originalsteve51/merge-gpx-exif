package svptech.gpsmerge.main;

import svptech.gpsmerge.common.MergeProcessor;

public class MergeGPSDataIntoPhotos
{

	public static boolean debug = false;

	/**
	 * main() provides a command-line entry point.
	 * <p>
	 * This program uses the timestamps in exif picture data to locate gps records
	 * that are in a GPX formatted file of gps tracking data.
	 * <p>
	 * A close match is chosen based on the timestamps in the picture and the gps
	 * tracking waypoints. Data from the waypoint is copied to the latitude and
	 * longitude in the picture exif data.
	 * <p>
	 * Modified picture files are written to a target directory.
	 * 
	 * @param args
	 *            Pass three args:
	 *            <ol>
	 *            <li>The full pathname of a gpx file, e.g.
	 *            "c:/dev/GPSMerge/TestData/Track 005.gpx"</li>
	 *            <li>The full path of a directory containing source pictures, e.g.
	 *            "c:/dev/GPSMerge/Test Photo Directory/Hudson Walkway 2017"</li>
	 *            <li>The full path of a target directory for GPS modified pictures,
	 *            e.g. "C:/Users/ssh75/Dropbox/Photos/HudsonWalkway2017/GPS Marked
	 *            Up/"</li>
	 *            </ol>
	 */
	public static void main(String[] args)
	{
		if (args.length != 3)
		{
			System.err.println("Three command line args are required:");
			System.err.println("\tThe full pathname of a gpx file, e.g. \"c:/dev/GPSMerge/TestData/Track 005.gpx\"");
			System.err.println(
					"\tThe full path of a directory containing source pictures, e.g. \"c:/dev/GPSMerge/Test Photo Directory/Hudson Walkway 2017\"");
			System.err.println(
					"\tThe full path of a target directory for GPS modified pictures, e.g. \"C:/Users/ssh75/Dropbox/Photos/HudsonWalkway2017/GPS Marked Up/\"");
			System.exit(-1);
		}
		String gpxTrackFileName = args[0];
		String photoDirectoryPath = args[1];
		String targetDirectoryName = args[2];
		String cameraTimezone = "America/New_York";

		MergeProcessor.updateSourceFilesWithTrackData(gpxTrackFileName, photoDirectoryPath, targetDirectoryName,
				cameraTimezone, debug);

	}

}
