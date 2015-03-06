/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.table.TableColumnModel;

import nl.denhaag.twb.voorschriften.common.TransformResult;
import nl.denhaag.twb.voorschriften.common.VoorschriftenChecker;
import nl.denhaag.twb.voorschriften.common.util.CommonFileUtils;
import nl.denhaag.twb.voorschriften.common.util.VoorschriftenLogger;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


public class GenerateReports extends Thread implements VoorschriftenLogger {

	private static final Logger LOGGER = Logger.getLogger(GenerateReports.class);
	private VoorschriftenStandalone voorschriftenStandalone;
	private int max;
	private int current = 1;
	private String id;

	public GenerateReports(VoorschriftenStandalone voorschriftenStandalone) {
		this.voorschriftenStandalone = voorschriftenStandalone;
		id = "reports-" + System.currentTimeMillis();

	}

	private void initReports(){
		this.voorschriftenStandalone.getLogTextArea().setText("");
		logBigMessage("=============================================");
		logShortMessage("Initialisatie van rapportgeneratie...");
		voorschriftenStandalone.getTabbedPane().setEnabledAt(1, false);
		voorschriftenStandalone.getTabbedPane().setSelectedIndex(0);
		voorschriftenStandalone.getValidateButton().setEnabled(false);
		voorschriftenStandalone.getProgressLabel().setText("Validation in progress...");
		voorschriftenStandalone.getProgressLabel().setIcon(null);
		voorschriftenStandalone.getViewIndexPageButton().setEnabled(false);
	}
	private List<String> getExcludedNamespaces(){
		List<JCheckBox> excludedNamespacesCheckBoxes = voorschriftenStandalone.getExcludedNamespacesCheckboxes();
		List<String> excludedNamespaces = new ArrayList<String>();
		for (JCheckBox excludedNamespaceCheckBox : excludedNamespacesCheckBoxes) {
			if (excludedNamespaceCheckBox.isSelected()) {
				String excludedNamespace = excludedNamespaceCheckBox.getText();
				int index = excludedNamespace.indexOf('(');
				excludedNamespace = excludedNamespace.substring(0, index).trim();
				excludedNamespaces.add(excludedNamespace);
			}
		}
		return excludedNamespaces;
	}
	
	@Override
	public void run() {
		initReports();
		String sourceLocationString = voorschriftenStandalone.getSourceLocation().getText();
		String reportDirectoryString = voorschriftenStandalone.getReportDirectory().getText();
		voorschriftenStandalone.getProperties().setProperty(VoorschriftenStandalone.SOURCE_LOCATION,
				sourceLocationString);
		voorschriftenStandalone.getProperties().setProperty(VoorschriftenStandalone.REPORTS_LOCATION,
				reportDirectoryString);
		VoorschriftenStandalone.storeProperties(voorschriftenStandalone.getProperties(),
				VoorschriftenStandalone.SETTINGS_PROPERTIES);
		File sourceLocation = new File(sourceLocationString);
		File finalReportsDir = new File(reportDirectoryString);
		File sourceDir = null;
		File reportsDir = null;
		boolean zipReports = false;

		List<String> excludedNamespaces = getExcludedNamespaces();
		logBigMessage("Source location: " + sourceLocation);
		logBigMessage("Reports dir: " + finalReportsDir);
		for (String excludedNamespace : excludedNamespaces) {
			logBigMessage("Namespace to be excluded: " + excludedNamespace);
		}
		logBigMessage("=============================================");
		if (!sourceLocation.exists()){
			logBigMessage("Source location: " + sourceLocation + " does not exist");
		}
		try {

			if (sourceLocation.isFile() && sourceLocation.getName().endsWith(".zip")) {
				sourceDir = getSourceDir();
				CommonFileUtils.unzip(sourceDir, sourceLocation);
			} else {
				sourceDir = sourceLocation;
			}
			VoorschriftenChecker checker = new VoorschriftenChecker(this);
			max = CommonFileUtils.count(sourceDir);
			// max++;
			initProgressBar();
			// increment();
			if (zipReports) {
				reportsDir = getReportsDir();

			} else {
				reportsDir = finalReportsDir;
			}

			List<TransformResult> results = checker.generateReports(sourceDir, reportsDir, excludedNamespaces);

			if (zipReports) {
				String outputFileName = "reports-" + sourceLocation.getName();
				if (!outputFileName.endsWith(".zip")) {
					outputFileName += ".zip";
				}
				File zipFile = new File(finalReportsDir, outputFileName);
				if (zipFile.exists()) {
					zipFile.delete();
				}
				CommonFileUtils.zip(reportsDir, zipFile);
			}
			FileUtils.deleteDirectory(getTempDir());
			updateResults(results, reportsDir);
		} catch (Exception e) {
			displayErrors(e);
		}



	}
	private void displayErrors (Exception e){
		String message = "Fout tijdens het genereren van rapporten.";
		logBigMessage(message, e);	
		voorschriftenStandalone.getProgressLabel().setIcon(createImageIcon("/lowpriority.png"));
		voorschriftenStandalone.getProgressLabel().setText(message);
		voorschriftenStandalone.getValidateButton().setEnabled(true);		
	}
	private void updateResults(List<TransformResult> results, File reportsDir){
		TwDefaultTableModel model = new TwDefaultTableModel(results, reportsDir);
		voorschriftenStandalone.getResultTable().setModel(model);
		TableColumnModel columnModel = voorschriftenStandalone.getResultTable().getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(200);
		columnModel.getColumn(1).setPreferredWidth(200);
		columnModel.getColumn(2).setPreferredWidth(40);
		columnModel.getColumn(3).setPreferredWidth(40);
		columnModel.getColumn(4).setPreferredWidth(30);
		columnModel.getColumn(5).setPreferredWidth(30);
		String message = "Generatie rapporten voltooid.";
		logBigMessage(message);
		voorschriftenStandalone.getTabbedPane().setEnabledAt(1, true);
		voorschriftenStandalone.getTabbedPane().setSelectedIndex(1);
		voorschriftenStandalone.getViewIndexPageButton().setVisible(true);
		voorschriftenStandalone.getViewIndexPageButton().setEnabled(true);
		voorschriftenStandalone.getProgressLabel().setIcon(createImageIcon("/ok.png"));
		voorschriftenStandalone.getProgressLabel().setText(message);
		voorschriftenStandalone.getValidateButton().setEnabled(true);		

	}

	public void initProgressBar() {
		this.voorschriftenStandalone.getProgressBar().setMaximum(max);
		this.voorschriftenStandalone.getProgressBar().setMinimum(current);
	}

	public void increment() {
		this.voorschriftenStandalone.getProgressBar().setValue(++current);
	}

	public void logBigMessage(String logMessage) {
		this.voorschriftenStandalone.log(logMessage);
	}

	public void logBigMessage(String logMessage, Exception e) {
		this.voorschriftenStandalone.log(logMessage, e);
	}

	public void logShortMessage(String logMessage) {
		setMessage(logMessage);
		this.voorschriftenStandalone.log(logMessage);

	}

	private File getTempDir() {
		File tempDir = new File(System.getProperty("java.io.tmpdir") + CommonFileUtils.SEPARATOR + id);
		return tempDir;
	}

	private File getSourceDir() {
		File tempDir = getTempDir();
		File sourceDir = new File(tempDir, "source");
		return sourceDir;
	}

	private File getReportsDir() {
		File tempDir = getTempDir();
		File sourceDir = new File(tempDir, "reports");
		return sourceDir;
	}

	private void setMessage(String message) {
		this.voorschriftenStandalone.getProgressLabel().setText(message);
	}

	private ImageIcon createImageIcon(String path) {
		URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			LOGGER.error("Couldn't find file: " + path);
			return null;
		}
	}
}
