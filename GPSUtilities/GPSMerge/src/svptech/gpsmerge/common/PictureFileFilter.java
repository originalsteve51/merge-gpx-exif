package svptech.gpsmerge.common;

import java.io.File;
import java.io.FileFilter;

public class PictureFileFilter implements FileFilter
{
	/**
	 * Make a FileFilter for jpg files.
	 */
	public PictureFileFilter()
	{
		super();
	}
	
	/**
	 * Make a FileFilter for one or more file extensions such as jpg, gif, etc.
	 * @param pictureExtensions An array of strings, each of which is a file extension.
	 */
	public PictureFileFilter(String[] pictureExtensions)
	{
		super();
		this.pictureExtensions = pictureExtensions;
	}

	private String[] pictureExtensions = new String[]{ 	"jpg"  };

	public boolean accept(File file)
	{
		for (String extension : pictureExtensions)
		{
			if (file.getName().toLowerCase().endsWith(extension))
			{
				return true;
			}
		}
		return false;
	}
}