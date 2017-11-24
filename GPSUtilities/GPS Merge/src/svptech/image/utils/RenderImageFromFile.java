package svptech.image.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Display scaled images loaded from the file system
 */
public class RenderImageFromFile extends Component
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage img;
	private Image scaledImage;
	private double aspect;
	private int width = 800;
	private int computedHeight;
	private String imageFilePathname;

	@Override
	public void paint(Graphics g)
	{
		if (scaledImage != null)
		{
			g.drawImage(scaledImage, 0, 0, width, computedHeight, null);
		}
	}

	public RenderImageFromFile()
	{
		this(800);
	}

	public RenderImageFromFile(int preferredWidth)
	{
		this.width = preferredWidth;

	}

	public Dimension getPreferredSize()
	{
		if (img == null)
		{
			return new Dimension(100, 100);
		} 
		else
		{
			return new Dimension(width, computedHeight);
		}
	}

	public String getImageFilePathname()
	{
		return imageFilePathname;
	}

	public void setImageFilePathname(String imageFilePathname) throws IOException
	{
		this.imageFilePathname = imageFilePathname;

		img = ImageIO.read(new File(imageFilePathname));

		// The natural size of the image is likely to be wrong, so it needs to be
		// scaled.
		// To maintain the aspect, compute the ratio width/height
		aspect = new Double(img.getWidth()) / new Double(img.getHeight());

		// Make it fit without distortion...
		computedHeight = new Double(width / aspect).intValue();
		
		if (computedHeight>600)
		{
			computedHeight = 600;
			width = new Double(computedHeight * aspect).intValue();
		}

		scaledImage = img.getScaledInstance(width, computedHeight, Image.SCALE_DEFAULT);
		revalidate();
		repaint();

	}

}
