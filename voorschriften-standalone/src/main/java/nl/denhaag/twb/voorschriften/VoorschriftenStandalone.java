package nl.denhaag.twb.voorschriften;

/*
 * #%L
 * Webservice voorschriften applicatie
 * %%
 * Copyright (C) 2012 - 2015 Team Applicatie Integratie (Gemeente Den Haag)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import nl.denhaag.twb.voorschriften.common.VoorschriftenChecker;
import nl.denhaag.twb.voorschriften.table.IconTextCellRenderer;
import nl.denhaag.twb.voorschriften.table.TableCellValue;

import org.apache.log4j.Logger;

public class VoorschriftenStandalone {
	public static final String SOURCE_LOCATION = "source.location";
	public static final String REPORTS_LOCATION = "reports.location";
	protected static final String SETTINGS_PROPERTIES = "settings.properties";
	private static final String LABELS_PROPERTIES = "/labels.properties";
	private static final Logger LOGGER = Logger.getLogger(VoorschriftenStandalone.class);
	private JFrame applicationFrame;
	private JScrollPane resultScrollPane;
	private JTextField sourceLocation;
	private JTextField reportDirectory;
	private JButton sourceBrowseButton;
	private JButton reportDirBrowseButton;
	private JPanel inputPanel;
	private Properties properties = new Properties();
	private Properties labelProperties = new Properties();

	private int height;
	private int width;
	private JButton validateButton;
	private JPanel progressPanel;
	private JLabel progressLabel;
	private JProgressBar progressBar;
	private static VoorschriftenStandalone window;
	private JTable resultTable;
	private JTabbedPane tabbedPane;
	private JPanel resultsPanel;
	private JPanel logPanel;
	private JScrollPane logScrollPane;
	private JTextArea logTextArea;
	private JButton viewIndexPageButton;
	private JScrollPane scrollPane;
	private JPanel panel;
	private List<JCheckBox> excludedNamespacesCheckboxes;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new VoorschriftenStandalone();
					window.applicationFrame.setVisible(true);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public VoorschriftenStandalone() {
		initialize();
	}

	private String getTitle() {
		return getLabel("description") + " - " + getLabel("version") + " - " + getLabel("vendor");
	}

	private String getLabel(String propertyName) {
		return labelProperties.getProperty(propertyName, "???" + propertyName + "???");
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		loadProperties(properties, SETTINGS_PROPERTIES);
		loadPropertiesFromClasspath(labelProperties, LABELS_PROPERTIES);
		applicationFrame = new JFrame(getTitle());
		applicationFrame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int newWidth = e.getComponent().getWidth();
				int newHeight = e.getComponent().getHeight();
				if (newWidth != width || newHeight != height) {
					int changedWidth = newWidth - width;
					int changedHeight = newHeight - height;
					resizeFrame(tabbedPane, changedWidth, changedHeight);
					resizeFrame(resultsPanel, changedWidth, changedHeight);
					resizeFrame(logPanel, changedWidth, changedHeight);
					resizeFrame(logScrollPane, changedWidth, changedHeight);
					resizeFrame(logTextArea, changedWidth, changedHeight);
					resizeFrame(resultScrollPane, changedWidth, changedHeight);
					resizeFrame(resultTable, changedWidth, changedHeight);
					int progressPanelY = progressPanel.getY();
					int progressPanelWidth = progressPanel.getWidth();
					progressPanelWidth += changedWidth;
					progressPanelY += changedHeight;
					progressPanel.setBounds(progressPanel.getX(), progressPanelY, progressPanelWidth,
							progressPanel.getHeight());

					inputPanel.setBounds(inputPanel.getX(), inputPanel.getY(), inputPanel.getWidth() + changedWidth,
							inputPanel.getHeight());
					sourceLocation.setBounds(sourceLocation.getX(), sourceLocation.getY(), sourceLocation.getWidth()
							+ changedWidth, sourceLocation.getHeight());
					reportDirectory.setBounds(reportDirectory.getX(), reportDirectory.getY(),
							reportDirectory.getWidth() + changedWidth, reportDirectory.getHeight());
					sourceBrowseButton.setBounds(sourceBrowseButton.getX() + changedWidth, sourceBrowseButton.getY(),
							sourceBrowseButton.getWidth(), sourceBrowseButton.getHeight());
					reportDirBrowseButton.setBounds(reportDirBrowseButton.getX() + changedWidth,
							reportDirBrowseButton.getY(), reportDirBrowseButton.getWidth(),
							reportDirBrowseButton.getHeight());
					viewIndexPageButton.setBounds(viewIndexPageButton.getX() + changedWidth,
							viewIndexPageButton.getY(), viewIndexPageButton.getWidth(),
							viewIndexPageButton.getHeight());		
					progressLabel.setBounds(progressLabel.getX(), progressLabel.getY(),
							progressLabel.getWidth() + changedWidth, progressLabel.getHeight());
				}
				width = newWidth;
				height = newHeight;
			}
		});
		width = 800;
		height = 760;
		applicationFrame.setMinimumSize(new Dimension(width, height));
		applicationFrame.setTitle(getTitle());
		applicationFrame.setBounds(100, 100, 800, 760);
		applicationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		inputPanel = new JPanel();
		inputPanel.setBounds(0, 0, 784, 291);
		inputPanel.setLayout(null);

		JLabel lblSource = new JLabel("Zip bestand of directory:");
		lblSource.setBounds(10, 24, 138, 14);
		inputPanel.add(lblSource);

		sourceLocation = new JTextField();
		sourceLocation.setBounds(158, 21, 507, 20);
		inputPanel.add(sourceLocation);
		sourceLocation.setText(properties.getProperty(SOURCE_LOCATION));
		sourceLocation.setColumns(10);

		validateButton = new JButton("Valideer");
		validateButton.setBounds(158, 254, 89, 23);
		inputPanel.add(validateButton);
		validateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				new GenerateReports(window).start();

			}
		});
		applicationFrame.getContentPane().setLayout(null);
		applicationFrame.getContentPane().add(inputPanel);

		sourceBrowseButton = new JButton("Bladeren");
		sourceBrowseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sourceLocation.setText(openFileAndDirChooser(sourceLocation.getText(), (JButton) e.getSource()));
			}
		});
		sourceBrowseButton.setBounds(672, 20, 89, 23);
		inputPanel.add(sourceBrowseButton);

		JLabel lblReportDirectory = new JLabel("Report directory:");
		lblReportDirectory.setBounds(10, 52, 89, 14);
		inputPanel.add(lblReportDirectory);

		reportDirectory = new JTextField();
		reportDirectory.setBounds(158, 52, 507, 20);
		reportDirectory.setText(properties.getProperty(REPORTS_LOCATION));
		inputPanel.add(reportDirectory);
		reportDirectory.setColumns(10);

		reportDirBrowseButton = new JButton("Bladeren");
		reportDirBrowseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reportDirectory.setText(openDirChooser(reportDirectory.getText(), (JButton) e.getSource()));
			}
		});
		reportDirBrowseButton.setBounds(672, 52, 89, 23);
		inputPanel.add(reportDirBrowseButton);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(158, 111, 507, 132);
		inputPanel.add(scrollPane);

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		scrollPane.setViewportView(panel);

		JLabel lblNames = new JLabel("Negeer namespaces die beginnen met:");
		lblNames.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNames.setBounds(158, 86, 243, 14);
		inputPanel.add(lblNames);
		excludedNamespacesCheckboxes = new ArrayList<JCheckBox>();
		for (Entry<String, String> excludedNamespace : VoorschriftenChecker.getMapForExcludedNamespaces().entrySet()) {
			JCheckBox jCheckBox = new JCheckBox(excludedNamespace.getKey() + " (" + excludedNamespace.getValue() + ")",
					false);
			panel.add(jCheckBox);
			excludedNamespacesCheckboxes.add(jCheckBox);
		}

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 302, 784, 366);
		applicationFrame.getContentPane().add(tabbedPane);

		logPanel = new JPanel();
		tabbedPane.addTab("Logging", null, logPanel, null);
		logPanel.setLayout(null);

		logScrollPane = new JScrollPane();
		logScrollPane.setBounds(0, 0, 779, 356);
		logPanel.add(logScrollPane);

		logTextArea = new JTextArea();
		logTextArea.setEditable(false);
		logScrollPane.setViewportView(logTextArea);

		resultsPanel = new JPanel();
		tabbedPane.addTab("Resultaten", null, resultsPanel, null);
		tabbedPane.setEnabledAt(1, false);
		resultsPanel.setLayout(null);

		resultScrollPane = new JScrollPane();
		resultScrollPane.setBounds(0, 5, 779, 340);
		resultsPanel.add(resultScrollPane);

		resultTable = new JTable() {

			/**
					 * 
					 */
			private static final long serialVersionUID = 6162757058847749301L;

			/*
			 * (non-Javadoc)
			 * 
			 * @see javax.swing.JTable#isCellEditable(int, int)
			 */
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			// Returning the Class of each column will allow different
			// renderers to be used based on Class
			public Class<?> getColumnClass(int column) {
				Object object = getValueAt(0, column);
				if (object != null) {
					return object.getClass();
				}
				return null;
			}
		};
		resultTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = resultTable.getSelectedRow();
					TwDefaultTableModel model = (TwDefaultTableModel) resultTable.getModel();
					try {
						Desktop.getDesktop().browse(getURI(model.getBrowserFilename(row)));
					} catch (Exception e1) {
						log(e1.getMessage(),e1);
					}
				}
			}
		});
		resultTable.setRowHeight(20);
		resultTable.setAutoCreateRowSorter(true);
		resultTable.setDefaultRenderer(TableCellValue.class, new IconTextCellRenderer());
		resultScrollPane.setViewportView(resultTable);

		progressPanel = new JPanel();
		progressPanel.setBounds(0, 690, 784, 32);
		applicationFrame.getContentPane().add(progressPanel);
		progressPanel.setLayout(null);

		progressBar = new JProgressBar();
		progressBar.setBounds(10, 9, 146, 14);
		progressPanel.add(progressBar);

		progressLabel = new JLabel("Geen validatie gestart.");
		progressLabel.setBounds(166, 4, 442, 19);
		progressPanel.add(progressLabel);

		viewIndexPageButton = new JButton("Bekijk overzichtspagina");
		viewIndexPageButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					String full = new File(getReportDirectory().getText()).getCanonicalPath() + "/index.html";
					File file = new File (full);
					if (new File(full).exists()) {
						Desktop.getDesktop().browse(getURI(file));
					} else {
						log("Location: " + full + " does not exist.");
					}
				} catch (Exception e1) {
					log(e1.getMessage(),e1);
				}

			}
		});
		viewIndexPageButton.setBounds(614, 5, 160, 23);
		viewIndexPageButton.setVisible(false);
		progressPanel.add(viewIndexPageButton);
	}
	
	private URI getURI (File file) throws IOException, URISyntaxException{
    	return file.toURI();
	}

	private void resizeFrame(Component child, int changedWidth, int changedHeight) {

		if (child != null) {
			int x = child.getX();
			int y = child.getY();
			int scrollPaneWidth = child.getWidth();
			int scrollPaneHeight = child.getHeight();
			scrollPaneWidth += changedWidth;
			scrollPaneHeight += changedHeight;
			child.setBounds(x, y, scrollPaneWidth, scrollPaneHeight);
			child.revalidate();
		}

	}

	/**
	 * @return the resultTable
	 */
	protected JTable getResultTable() {
		return resultTable;
	}

	private String openDirChooser(String selectedItem, Component source) {
		File selectedFile = new File(selectedItem);
		final JFileChooser fc = new JFileChooser(selectedFile.getParentFile());
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		// In response to a button click:
		int returnVal = fc.showOpenDialog(source);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			return file.getAbsolutePath();
		}
		return selectedItem;
	}

	private String openFileAndDirChooser(String selectedItem, Component source) {
		File selectedFile = new File(selectedItem);
		final JFileChooser fc = new JFileChooser(selectedFile.getParentFile());
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setFileFilter(new SourceFileFilter());
		// fc.addChoosableFileFilter(new SourceFileFilter());
		fc.setAcceptAllFileFilterUsed(false);
		// In response to a button click:
		int returnVal = fc.showOpenDialog(source);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			return file.getAbsolutePath();
		}
		return selectedItem;
	}

	private static void loadProperties(Properties properties, String fileName) {
		try {
			properties.load(new FileInputStream(fileName));
		} catch (IOException ex) {
			LOGGER.warn(ex.getMessage());
		}
	}

	private static void loadPropertiesFromClasspath(Properties properties, String fileName) {
		try {
			properties.load(VoorschriftenStandalone.class.getResourceAsStream(fileName));
		} catch (Exception ex) {
			LOGGER.warn(ex.getMessage());
		}
	}

	protected static void storeProperties(Properties properties, String fileName) {
		try {
			properties.store(new FileOutputStream(fileName), "Stored by synchronizer");
		} catch (IOException ex) {
			LOGGER.warn(ex.getMessage());
		}
	}

	/**
	 * @return the validateButton
	 */
	protected JButton getValidateButton() {
		return validateButton;
	}

	/**
	 * @return the progressLabel
	 */
	protected JLabel getProgressLabel() {
		return progressLabel;
	}

	/**
	 * @return the oldWsdlLocation
	 */
	protected JTextField getSourceLocation() {
		return sourceLocation;
	}

	/**
	 * @return the progressBar
	 */
	protected JProgressBar getProgressBar() {
		return progressBar;
	}

	/**
	 * @return the properties
	 */
	protected Properties getProperties() {
		return properties;
	}

	/**
	 * @return the reportDirectory
	 */
	protected JTextField getReportDirectory() {
		return reportDirectory;
	}

	/**
	 * @return the logTextArea
	 */
	protected JTextArea getLogTextArea() {
		return logTextArea;
	}

	/**
	 * @return the tabbedPane
	 */
	protected JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	/**
	 * @return the viewIndexPageButton
	 */
	protected JButton getViewIndexPageButton() {
		return viewIndexPageButton;
	}

	/**
	 * @return the excludedNamespacesCheckboxes
	 */
	protected List<JCheckBox> getExcludedNamespacesCheckboxes() {
		return excludedNamespacesCheckboxes;
	}

	/**
	 * @return the applicationFrame
	 */
	protected JFrame getApplicationFrame() {
		return applicationFrame;
	}

	public void log(String message) {
		this.getTabbedPane().setSelectedIndex(0);   
		this.logTextArea.append(message + "\n");
		LOGGER.info(message);
	}
	
	public void log(String message, Exception e) {
		this.getTabbedPane().setSelectedIndex(0);   
		this.logTextArea.append("Exception: " + message + "\n");
		Throwable cause = e.getCause();
		while (cause != null){
			this.logTextArea.append(cause.getMessage() + "\n");
			cause = cause.getCause();
		}
		LOGGER.error(message,e);
	}

	static class SourceFileFilter extends FileFilter {

		public boolean accept(File path) {
			if (path.isDirectory()) {
				return true;
			} else if (path.getName().endsWith(".zip")) {
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return "Mappen of zip-bestanden";
		}

	}
}