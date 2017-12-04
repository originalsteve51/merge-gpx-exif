package svptech.imaging.test;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

/**
 * Display a scaled image loaded from the file system
 */
public class LoadImageApp extends Component
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

	public void paint(Graphics g)
	{
		g.drawImage(scaledImage, 0, 0, null);
	}

	public LoadImageApp ()
	{
		this(800);
	}
	
	public LoadImageApp(int preferredWidth)
	{
		try
		{
			this.width = preferredWidth;
			
			img = ImageIO.read(new File(
					"C:\\dev\\GPSMerge\\TestData\\Test Photo Directory\\HudsonWalkway2017\\HudsonRTBridge-1.jpg"));
			
			// The natural size of the image is likely to be wrong, so it needs to be scaled.
			// To maintain the aspect, compute the ratio width/height
			aspect = new Double(img.getWidth())/new Double(img.getHeight());
			
			// Make it fit into 400 wide with a height that will not distort...
			computedHeight = new Double(width/aspect).intValue();
			
			
			scaledImage = img.getScaledInstance(width, computedHeight, Image.SCALE_DEFAULT);
			
		} 
		catch (IOException e)
		{
		}

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

//	public static void main(String[] args)
//	{
//
//		JFrame f = new JFrame("Load Image Sample");
//
//		f.addWindowListener(new WindowAdapter()
//		{
//			public void windowClosing(WindowEvent e)
//			{
//				System.exit(0);
//			}
//		});
//
//		f.add(new LoadImageApp());
//		f.pack();
//		f.setVisible(true);
//	}
	
}
