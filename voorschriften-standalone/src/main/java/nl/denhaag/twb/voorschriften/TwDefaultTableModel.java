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


import java.awt.Color;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

import nl.denhaag.twb.voorschriften.common.TransformResult;
import nl.denhaag.twb.voorschriften.table.TableCellValue;

import org.apache.commons.lang3.StringUtils;

public class TwDefaultTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3462534699172097961L;

	private List<TransformResult> results; 
	private File reportDir;
	public TwDefaultTableModel(List<TransformResult> results, File reportDir){
		this.results = results;
		this.reportDir = reportDir;
        addColumn("Directory");
        addColumn("File");
        addColumn("Passed");
        addColumn("High");
        addColumn("Low");
        addColumn("Warn");
        addColumn("");
        ImageIcon passedIcon = createImageIcon("/ok.png");
        ImageIcon highPriorityIcon = createImageIcon("/highpriority.png");
        ImageIcon highPriorityIgnoredIcon = createImageIcon("/highpriority-ignored.png");
        ImageIcon lowPriorityIcon = createImageIcon("/lowpriority.png");
        ImageIcon lowPriorityIgnoredIcon = createImageIcon("/lowpriority-ignored.png");        
        ImageIcon warningIcon = createImageIcon("/warning.png");
        ImageIcon warningIgnoredIcon = createImageIcon("/warning-ignored.png");     
        for (int i=0; i < results.size(); i++){
        	TransformResult result = results.get(i);
        	Color backgroundColor = null;
        	String tooltip = null;
        	if (!result.isWsiCompliance()){
        		backgroundColor = Color.YELLOW;
        		tooltip = "Not WSI-compliant!";
        	}
        	TableCellValue passed = new TableCellValue(backgroundColor,tooltip);
        	if (result.getPassed() > 0){
        		passed.setImageIcon(passedIcon);
        		passed.setText(result.getPassed() + "");
        	}
        	TableCellValue highpriority  = new TableCellValue(backgroundColor,tooltip);
        	if (result.getHighpriority() > 0){
        		highpriority.setImageIcon(highPriorityIcon);
        		highpriority.setText(result.getHighpriority() + "");        		
        	}else if (result.getHighpriorityIgnored()> 0 ){
        		highpriority.setImageIcon(highPriorityIgnoredIcon);
        	}
        	TableCellValue lowpriority  = new TableCellValue(backgroundColor,tooltip);
        	if (result.getLowpriority() > 0){
        		lowpriority.setImageIcon(lowPriorityIcon);
        		lowpriority.setText(result.getLowpriority() + "");
        	}else if (result.getLowpriorityIgnored()> 0 ){
        		lowpriority.setImageIcon(lowPriorityIgnoredIcon);
        	}
        	TableCellValue warning  = new TableCellValue(backgroundColor,tooltip);
        	if (result.getWarning() > 0){
        		warning.setImageIcon(warningIcon);
        		warning.setText(result.getWarning() + "");
        	}else if (result.getWarningIgnored()> 0 ){
        		warning.setImageIcon(warningIgnoredIcon);
        	}
        	TableCellValue dirPrefix = new TableCellValue(backgroundColor, result.getDirPrefix(),tooltip);
        	TableCellValue sourceFilename = new TableCellValue(backgroundColor, result.getSourceFileName(),tooltip);
        	TableCellValue type = new TableCellValue(backgroundColor, result.getType(),tooltip);
        	addRow(new Object[]{dirPrefix, sourceFilename, passed,highpriority,lowpriority,warning, type});
        }
	}

	public File getBrowserFilename (int row) throws IOException{
		TransformResult result = results.get(row);
		String fileName =  getRelativeFileName(result.getDirPrefix(), result.getRelativeReportFileName());
    	return new File(reportDir.getCanonicalPath() + "/"+ fileName);
	}
	
	private static String getRelativeFileName(String dir, String filename) {
		String result = null;
		if (StringUtils.isEmpty(dir)) {
			result = filename;
		} else {
			result = dir + "/" + filename;
		}
		return result;
	}
	private ImageIcon createImageIcon(String path) {
		URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		}
		return null;
	}
	
}
