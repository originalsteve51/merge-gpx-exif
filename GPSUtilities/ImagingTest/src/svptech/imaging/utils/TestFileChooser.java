package svptech.imaging.utils;

import java.awt.Font;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class TestFileChooser
{
	public static void main (String[] args)
	{
		
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e)
		{
			e.printStackTrace();
		}

		JFrame frame = new JFrame();
		
		JFileChooser fc = new JFileChooser();

		fc.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fc.showOpenDialog(frame);
		if (result == fc.APPROVE_OPTION) {
		    File selectedFile = fc.getSelectedFile();
		    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
		}
	}
}
