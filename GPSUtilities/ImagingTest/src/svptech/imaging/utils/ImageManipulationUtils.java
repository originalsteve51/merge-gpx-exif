package svptech.imaging.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

public class ImageManipulationUtils
{

	/**
	 * This method sets the EXIF the of the JPEG file and outputs it to given
	 * directory.
	 * 
	 * @param jpegImageFile
	 *            Input jpeg file.
	 * @param dst
	 *            output jpeg file.
	 * @param longitude
	 *            Longitude to be tagged.
	 * @param latitude
	 *            Latitude to be tagged.
	 * @throws IOException
	 * @throws ImageReadException
	 * @throws ImageWriteException
	 */
	public static void setExifGPSTag(final File jpegImageFile, final File dst, final double longitude,
			final double latitude) throws IOException, ImageReadException, ImageWriteException
	{
		OutputStream os = null;
		try
		{

			final ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
			final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
			TiffOutputSet outputSet = setTiffOutputSet(jpegMetadata, longitude, latitude);
			os = new FileOutputStream(dst);
			os = new BufferedOutputStream(os);
			new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os, outputSet);
		} 
		catch (Exception e)
		{
			System.err.println("Problem updating exif data for file "+jpegImageFile+". This file was not processed.");
		}
		finally
		{
			os.close();
		}
	}

	/**
	 * @param jpegMetadata
	 *            Input jpeg file.
	 * @param longitude
	 *            Longitude to be tagged.
	 * @param latitude
	 *            Latitude to be tagged.
	 * @return TiffOutputSet with GPS set to new latlong.
	 * @throws IOException
	 * @throws ImageReadException
	 * @throws ImageWriteException
	 */
	public static TiffOutputSet setTiffOutputSet(final JpegImageMetadata jpegMetadata, final double longitude,
			final double latitude) throws IOException, ImageReadException, ImageWriteException
	{
		TiffOutputSet outputSet = null;
		if (null != jpegMetadata)
		{
			final TiffImageMetadata exif = jpegMetadata.getExif();
			if (null != exif)
			{
				outputSet = exif.getOutputSet();
			}
		}
		if (null == outputSet)
		{
			outputSet = new TiffOutputSet();
		}
		outputSet.setGPSInDegrees(longitude, latitude);
		return outputSet;
	}
}
