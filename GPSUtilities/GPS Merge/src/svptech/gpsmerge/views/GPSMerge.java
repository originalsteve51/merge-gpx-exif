package svptech.gpsmerge.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.TimeZone;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
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

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.eclipse.wb.swing.FocusTraversalOnArray;

import svptech.gpsmerge.common.MergeProcessor;
import svptech.imaging.test.LoadImageApp;

import java.awt.Color;

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

	private String pathroot;
	private JLabel lblStatus;
	private JLabel lblSourceInfo;
	private JLabel lblGPXInfo;
	private JLabel lblTargetInfo;
	private JPanel imagePanel;

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
	 * @throws ConfigurationException 
	 */
	public GPSMerge() throws ConfigurationException
	{
		initComponents();
		createEvents();
	}

	private void initComponents() throws ConfigurationException
	{
		Configurations configs = new Configurations();
		Configuration config = configs.properties(new File("config.properties"));
		// access configuration properties
		pathroot = config.getString("common.path");
		System.out.println("common.path = " + pathroot);

		setTitle("GPSMerge");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(2000, 1500);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JLabel lblSourceFolder = new JLabel("Source folder:");

		textFieldSourceFolder = new JTextField();
		textFieldSourceFolder.setText(pathroot);
		textFieldSourceFolder.setColumns(10);

		btnBrowseSrcFolder = new JButton("Browse");

		JLabel lblGpxFile = new JLabel("GPX file:");

		textFieldGPXFile = new JTextField();
		textFieldGPXFile.setText(pathroot);
		textFieldGPXFile.setColumns(10);

		btnBrowseGPXFile = new JButton("Browse");

		JLabel lblTargetFolder = new JLabel("Target folder:");

		textFieldTargetFolder = new JTextField();
		textFieldTargetFolder.setText(pathroot);
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

		theMapView = new MergeMapView();
		
		lblStatus = new JLabel(" ");
		
		lblSourceInfo = new JLabel(" ");
		
		lblGPXInfo = new JLabel(" ");
		
		lblTargetInfo = new JLabel(" ");
		
		imagePanel = new JPanel();
		imagePanel.setBackground(Color.LIGHT_GRAY);

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(theMapView, GroupLayout.PREFERRED_SIZE, 1832, GroupLayout.PREFERRED_SIZE))
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
										.addComponent(comboBoxTZ, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addGroup(gl_contentPane.createSequentialGroup()
											.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
												.addComponent(lblStatus, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addGroup(gl_contentPane.createSequentialGroup()
													.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
														.addComponent(textFieldTargetFolder, GroupLayout.PREFERRED_SIZE, 430, GroupLayout.PREFERRED_SIZE)
														.addComponent(textFieldGPXFile, GroupLayout.PREFERRED_SIZE, 430, GroupLayout.PREFERRED_SIZE)
														.addComponent(textFieldSourceFolder, GroupLayout.PREFERRED_SIZE, 430, GroupLayout.PREFERRED_SIZE))
													.addPreferredGap(ComponentPlacement.UNRELATED)
													.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
														.addComponent(btnBrowseTargetFolder)
														.addComponent(btnBrowseSrcFolder, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(btnBrowseGPXFile, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
												.addComponent(lblGPXInfo)
												.addComponent(lblSourceInfo)
												.addComponent(lblTargetInfo)))))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(283)
									.addComponent(btnMergeGpsLocations)))
							.addPreferredGap(ComponentPlacement.RELATED, 366, Short.MAX_VALUE)
							.addComponent(imagePanel, GroupLayout.PREFERRED_SIZE, 661, GroupLayout.PREFERRED_SIZE)))
					.addGap(100))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblSourceFolder)
								.addComponent(textFieldSourceFolder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnBrowseSrcFolder)
								.addComponent(lblSourceInfo))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblGpxFile)
								.addComponent(textFieldGPXFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnBrowseGPXFile)
								.addComponent(lblGPXInfo))
							.addGap(18)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblTargetFolder)
								.addComponent(textFieldTargetFolder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnBrowseTargetFolder)
								.addComponent(lblTargetInfo))
							.addGap(26)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(comboBoxTZ, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblCameraTimezone))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblStatus)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnMergeGpsLocations)
							.addPreferredGap(ComponentPlacement.RELATED, 29, Short.MAX_VALUE))
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(imagePanel, GroupLayout.PREFERRED_SIZE, 435, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)))
					.addComponent(theMapView, GroupLayout.PREFERRED_SIZE, 967, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);

		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]
		{ textFieldSourceFolder, btnBrowseSrcFolder, textFieldGPXFile, btnBrowseGPXFile, textFieldTargetFolder,
				btnBrowseTargetFolder, comboBoxTZ, btnMergeGpsLocations }));
		
		LoadImageApp imageApp = new LoadImageApp();
		imagePanel.add(imageApp);
		imagePanel.setVisible(true);


	}

	private void createEvents()
	{
		MergeProcessor mp = new MergeProcessor(comboBoxTZ.getSelectedItem().toString());
		btnBrowseSrcFolder.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent action)
			{				
				// Clear any old status because something new is occurring
				lblStatus.setText("");

				textFieldSourceFolder.setText(obtainFolderPathname(textFieldSourceFolder.getText()));

				try
				{
					mp.updateDirectoryPhotoCount(textFieldSourceFolder.getText(), textFieldTargetFolder.getText(),
							textFieldGPXFile.getText(), lblSourceInfo, lblTargetInfo, lblStatus);
				} 
				catch (Exception e1)
				{
					lblStatus.setText(e1.getMessage());
				}
			}
		});

		btnBrowseGPXFile.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent action)
			{
				// Clear any old status because something new is occurring
				lblStatus.setText("");

				textFieldGPXFile.setText(obtainFilePathname(textFieldGPXFile.getText()));

				mp.updateStatusBasedOnGPX(textFieldGPXFile, lblGPXInfo, theMapView);
			}

		});

		btnBrowseTargetFolder.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// Clear any old status because something new is occurring
				lblStatus.setText("");

				textFieldTargetFolder.setText(obtainFolderPathname(textFieldTargetFolder.getText()));

				try
				{
					mp.updateDirectoryPhotoCount(textFieldSourceFolder.getText(), textFieldTargetFolder.getText(),
							textFieldGPXFile.getText(), lblSourceInfo, lblTargetInfo, lblStatus);
				} 
				catch (Exception e1)
				{
					lblStatus.setText(e1.getMessage());
				}
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

				// Push the change over to the MergeProcessor class, which needs to know the
				// camera
				// timezone when it converts camera times on the photos to GMT, which is what
				// the GPX
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
				
				// Clear any old status because something new is occurring
				lblStatus.setText("");

				if (gpxTrackFileName.length() == 0 || photoDirectoryPath.length() == 0
						|| targetDirectoryName.length() == 0 || cameraTimezone.length() == 0)
				{
					JOptionPane.showMessageDialog(null,
							"You need to enter data in all fields before a merge can be performed.");
				} else
				{
					try
					{
						MergeProcessor.updateSourceFilesWithTrackData(gpxTrackFileName, photoDirectoryPath,
								targetDirectoryName, cameraTimezone, false, theMapView);

						// After the update, it is likely that additional files were written to
						// the target folder. Update the count that shows on the UI.
						mp.updateDirectoryPhotoCount(textFieldSourceFolder.getText(), textFieldTargetFolder.getText(),
								textFieldGPXFile.getText(), lblSourceInfo, lblTargetInfo, lblStatus);
					} 
					catch (Exception e1)
					{
						// Display the Exception text on the UI for user corrective action.
						lblStatus.setText(e1.getMessage());
					}

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
