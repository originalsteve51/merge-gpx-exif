package svptech.image.utils;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

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
	private int width = 200;
	private int computedHeight;
	private String imageFilePathname;

	public void paint(Graphics g)
	{
		if (scaledImage != null)
		{
			g.drawImage(scaledImage, 0, 0, null);
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

		// Make it fit into 400 wide with a height that will not distort...
		computedHeight = new Double(width / aspect).intValue();

		scaledImage = img.getScaledInstance(width, computedHeight, Image.SCALE_DEFAULT);
		revalidate();
		repaint();

	}

}
