package svptech.imaging.readtest;

import java.io.File;

import org.apache.commons.imaging.examples.MetadataExample;

public class ExploreExifData
{

	public static void main(String[] args)
	{
		File imgFile = new File("C:\\\\dev\\\\GPSMerge\\\\TestData\\\\Test Photo Directory\\\\HudsonWalkway2017\\HudsonRTBridge-1.jpg");
		// File imgFilePlusGPS = new File("C:\\Users\\ssh75\\Desktop\\BESecond-35-gps.jpg");
		
//		double longitude = -79.909808;
//		double latitude = 32.860069;

		try
		{
			System.out.println("=== Original Photo Data ============================================================================================");
			MetadataExample.metadataExample(imgFile);
			
//			System.out.println("===============================================================================================");
//			System.out.println("Modifying the gps data fields in the exif metadata ...");
//			ImageManipulationUtils.setExifGPSTag(imgFile, imgFilePlusGPS, longitude, latitude);
//
//			System.out.println("==== Photo data including GPS information ===========================================================================================");
//			MetadataExample.metadataExample(imgFilePlusGPS);
		} 
		catch (Exception e)
		{
			System.out.println(e);
		}
	}

}
