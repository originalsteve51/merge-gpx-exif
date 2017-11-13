package svptech.gpsmerge.views;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

import org.eclipse.wb.swing.FocusTraversalOnArray;

import com.teamdev.jxmaps.LatLng;

import svptech.gpsmerge.common.MergeProcessor;

/**
 * Use the main() entry point in this class to display a Swing UI front-end for
 * a program that allows GPX data to be merged into picture files from a track
 * recorded while the pictures were taken.
 * 
 * @author Steve Harding
 *
 */
public class GPSMerge extends JFrame
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MergeMapView theMapView;
	private JPanel contentPane;
	private JTextField textFieldSourceFolder;
	private JTextField textFieldGPXFile;
	private JTextField textFieldTargetFolder;
	private JButton btnBrowseSrcFolder;
	private JButton btnBrowseGPXFile;
	private JButton btnBrowseTargetFolder;
	private JButton btnMergeGpsLocations;
	private JComboBox<String> comboBoxTZ;
	private JLabel projectedMergeCount;
	private JLabel lblSourceInfo;
	private JLabel lblGPXInfo;
	private JLabel lblTargetInfo;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
		} catch (Throwable e)
		{
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					GPSMerge frame = new GPSMerge();
					frame.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GPSMerge()
	{
		initComponents();
		createEvents();
	}

	private void initComponents()
	{
		setTitle("GPSMerge");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(2000, 1500);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JLabel lblSourceFolder = new JLabel("Source folder:");

		textFieldSourceFolder = new JTextField();
		textFieldSourceFolder.setText("C:\\dev\\GPSMerge\\TestData\\Test Photo Directory\\HudsonWalkway2017");
		textFieldSourceFolder.setColumns(10);

		btnBrowseSrcFolder = new JButton("Browse");

		JLabel lblGpxFile = new JLabel("GPX file:");

		textFieldGPXFile = new JTextField();
		textFieldGPXFile.setText("C:\\dev\\GPSMerge\\TestData\\Track005.gpx");
		textFieldGPXFile.setColumns(10);

		btnBrowseGPXFile = new JButton("Browse");

		JLabel lblTargetFolder = new JLabel("Target folder:");

		textFieldTargetFolder = new JTextField();
		textFieldTargetFolder.setText("c:\\dev\\GPSMerge");
		textFieldTargetFolder.setColumns(10);

		btnBrowseTargetFolder = new JButton("Browse");

		JLabel lblCameraTimezone = new JLabel("Camera TZ:");
		lblCameraTimezone.setToolTipText(
				"Your camera's time is set for a timezone. Select the timezone used by your camera here.");

		String[] ids = TimeZone.getAvailableIDs();
		DefaultComboBoxModel<String> cbTZModel = new DefaultComboBoxModel<String>(ids);
		comboBoxTZ = new JComboBox<String>(cbTZModel);

		// Default to America/New_York, this will almost always be the case.
		String defaultTZ = "America/New_York";
		comboBoxTZ.setSelectedItem(defaultTZ);

		btnMergeGpsLocations = new JButton("Merge GPS Locations");

		projectedMergeCount = new JLabel(" ");
		
		theMapView = new MergeMapView();
		
		lblSourceInfo = new JLabel("");
		
		lblGPXInfo = new JLabel("");
		
		lblTargetInfo = new JLabel("");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
									.addComponent(lblGpxFile)
									.addComponent(lblSourceFolder))
								.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
									.addComponent(lblCameraTimezone)
									.addComponent(lblTargetFolder)))
							.addGap(26)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(textFieldTargetFolder, GroupLayout.PREFERRED_SIZE, 430, GroupLayout.PREFERRED_SIZE)
										.addComponent(textFieldGPXFile, GroupLayout.PREFERRED_SIZE, 430, GroupLayout.PREFERRED_SIZE)
										.addComponent(textFieldSourceFolder, GroupLayout.PREFERRED_SIZE, 430, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
										.addComponent(btnBrowseSrcFolder, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(btnBrowseGPXFile, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(btnBrowseTargetFolder))
									.addGap(26)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(lblSourceInfo)
										.addComponent(lblGPXInfo)
										.addComponent(lblTargetInfo)))
								.addComponent(comboBoxTZ, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(337)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(btnMergeGpsLocations)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(137)
									.addComponent(projectedMergeCount)))))
					.addContainerGap(1135, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addContainerGap(91, Short.MAX_VALUE)
					.addComponent(theMapView, GroupLayout.PREFERRED_SIZE, 1832, GroupLayout.PREFERRED_SIZE)
					.addGap(35))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblSourceFolder)
										.addComponent(textFieldSourceFolder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblGpxFile)
										.addComponent(textFieldGPXFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addGap(18)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblTargetFolder)
										.addComponent(textFieldTargetFolder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(btnBrowseSrcFolder)
										.addComponent(lblSourceInfo))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(btnBrowseGPXFile)
										.addComponent(lblGPXInfo))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnBrowseTargetFolder)))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(comboBoxTZ, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblCameraTimezone)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(145)
							.addComponent(lblTargetInfo)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(projectedMergeCount)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnMergeGpsLocations)
					.addPreferredGap(ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
					.addComponent(theMapView, GroupLayout.PREFERRED_SIZE, 967, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
		
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]
		{ textFieldSourceFolder, btnBrowseSrcFolder, textFieldGPXFile, btnBrowseGPXFile, textFieldTargetFolder,
				btnBrowseTargetFolder, comboBoxTZ, btnMergeGpsLocations }));

	}

	private void createEvents()
	{
		MergeProcessor mp = new MergeProcessor(comboBoxTZ.getSelectedItem().toString());
		textFieldSourceFolder.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent arg0)
			{
				mp.updateDirectoryPhotoCount(textFieldSourceFolder.getText(), textFieldTargetFolder.getText(),
						textFieldGPXFile.getText(), lblSourceInfo, lblTargetInfo, projectedMergeCount);
			}
		});
		btnBrowseSrcFolder.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent action)
			{
				textFieldSourceFolder.setText(obtainFolderPathname(textFieldSourceFolder.getText()));

				mp.updateDirectoryPhotoCount(textFieldSourceFolder.getText(), textFieldTargetFolder.getText(),
						textFieldGPXFile.getText(), lblSourceInfo, lblTargetInfo, projectedMergeCount);
			}
		});

		textFieldGPXFile.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				mp.updateStatusBasedOnGPX(textFieldGPXFile, lblGPXInfo, theMapView);
			}
		});

		btnBrowseGPXFile.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent action)
			{
				textFieldGPXFile.setText(obtainFilePathname(textFieldGPXFile.getText()));

				mp.updateStatusBasedOnGPX(textFieldGPXFile, lblGPXInfo, theMapView);
			}

		});

		btnBrowseTargetFolder.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				textFieldTargetFolder.setText(obtainFolderPathname(textFieldTargetFolder.getText()));

				mp.updateDirectoryPhotoCount(textFieldSourceFolder.getText(), textFieldTargetFolder.getText(),
						textFieldGPXFile.getText(), lblSourceInfo, lblTargetInfo, projectedMergeCount);
			}

		});

		comboBoxTZ.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				System.out.println("Timezone selection may have changed.");
				String timezoneString = ((JComboBox<String>) e.getSource()).getSelectedItem().toString();
				System.out.println("Timezone is now: " + timezoneString);
				
				// Push the change over to the MergeProcessor class, which needs to know the camera 
				// timezone when it converts camera times on the photos to GMT, which is what the GPX
				// file uses.
				mp.setCameraTimezone(timezoneString);
			}
		});

		btnMergeGpsLocations.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String gpxTrackFileName = textFieldGPXFile.getText();
				String photoDirectoryPath = textFieldSourceFolder.getText();
				String targetDirectoryName = textFieldTargetFolder.getText();
				String cameraTimezone = comboBoxTZ.getSelectedItem().toString();

				if (gpxTrackFileName.length() == 0 || photoDirectoryPath.length() == 0
						|| targetDirectoryName.length() == 0 || cameraTimezone.length() == 0)
				{
					JOptionPane.showMessageDialog(null,
							"You need to enter data in all fields before a merge can be performed.");
				} else
				{
					MergeProcessor.updateSourceFilesWithTrackData(gpxTrackFileName, photoDirectoryPath,
							targetDirectoryName, cameraTimezone, false, theMapView);

					// After the update, it is likely that additional files were written to
					// the target folder. Update the count that shows on the UI.
					mp.updateDirectoryPhotoCount(textFieldSourceFolder.getText(), textFieldTargetFolder.getText(),
							textFieldGPXFile.getText(), lblSourceInfo, lblTargetInfo, projectedMergeCount);
				}
			}
		});

	}

	protected String obtainFolderPathname(String previous)
	{
		File initialFile = obtainInitialFile(previous);

		String selectedFolderPath = null;
		JFileChooser jfc = new JFileChooser(initialFile);
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// int returnValue = jfc.showOpenDialog(null);
		int returnValue = jfc.showSaveDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = jfc.getSelectedFile();
			selectedFolderPath = selectedFile.getAbsolutePath();
		}

		return selectedFolderPath;
	}

	protected String obtainFilePathname(String previous)
	{
		File initialFile = obtainInitialFile(previous);

		String selectedFilePathname = null;
		JFileChooser jfc = new JFileChooser(initialFile);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int returnValue = jfc.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = jfc.getSelectedFile();
			selectedFilePathname = selectedFile.getAbsolutePath();
		}

		return selectedFilePathname;
	}

	private File obtainInitialFile(String previous)
	{
		File previousFile;
		if (previous == null)
		{
			previousFile = FileSystemView.getFileSystemView().getHomeDirectory();
		} else
		{
			previousFile = new File(previous);
		}
		return previousFile;
	}
}
