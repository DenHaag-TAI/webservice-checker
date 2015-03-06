package nl.denhaag.twb.voorschriften.common;

/*
 * #%L
 * Common classes for multiple modules
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


import java.text.DecimalFormat;

public class TransformResult {
    private int passed = 0;
    private int highpriority = 0;
    private int lowpriority =0;
    private int warning = 0;
    private int highpriorityIgnored = 0;
    private int lowpriorityIgnored =0;
    private int warningIgnored = 0;
    private String fatalMessage= null;
    private boolean wsiCompliance = true;
    private String type;
    private String sourceFileName;
    private String relativeReportFileName;
    private String dirPrefix;
    
    public TransformResult(String sourceFileName){
    	this.sourceFileName = sourceFileName;
    }
    public TransformResult(){
    }
       

	/**
	 * @return the wsiCompliance
	 */
	public boolean isWsiCompliance() {
		return wsiCompliance;
	}
	/**
	 * @param wsiCompliance the wsiCompliance to set
	 */
	public void setWsiCompliance(boolean wsiCompliance) {
		this.wsiCompliance = wsiCompliance;
	}
	public int getPassed() {
		return passed;
	}
	public void setPassed(int passed) {
		this.passed = passed;
	}
	public int getHighpriority() {
		return highpriority;
	}
	public void setHighpriority(int highpriority) {
		this.highpriority = highpriority;
	}
	public int getLowpriority() {
		return lowpriority;
	}
	public void setLowpriority(int lowpriority) {
		this.lowpriority = lowpriority;
	}
	public int getWarning() {
		return warning;
	}
	public void setWarning(int warning) {
		this.warning = warning;
	}
	
	/**
	 * @return the highpriorityIgnored
	 */
	public int getHighpriorityIgnored() {
		return highpriorityIgnored;
	}
	/**
	 * @param highpriorityIgnored the highpriorityIgnored to set
	 */
	public void setHighpriorityIgnored(int highpriorityIgnored) {
		this.highpriorityIgnored = highpriorityIgnored;
	}
	/**
	 * @return the lowpriorityIgnored
	 */
	public int getLowpriorityIgnored() {
		return lowpriorityIgnored;
	}
	/**
	 * @param lowpriorityIgnored the lowpriorityIgnored to set
	 */
	public void setLowpriorityIgnored(int lowpriorityIgnored) {
		this.lowpriorityIgnored = lowpriorityIgnored;
	}
	/**
	 * @return the warningIgnored
	 */
	public int getWarningIgnored() {
		return warningIgnored;
	}
	/**
	 * @param warningIgnored the warningIgnored to set
	 */
	public void setWarningIgnored(int warningIgnored) {
		this.warningIgnored = warningIgnored;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return the fatalMessage
	 */
	public String getFatalMessage() {
		return fatalMessage;
	}
	/**
	 * @param fatalMessage the fatalMessage to set
	 */
	public void setFatalMessage(String fatalMessage) {
		this.fatalMessage = fatalMessage;
	}
	public String cijfer(){
		double total = passed + highpriority + lowpriority + warning;
		DecimalFormat format = new DecimalFormat("#0.0");
		return format.format((((double) passed) / (total) )*100.0);
	}


	public String getSourceFileName() {
		return sourceFileName;
	}


	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}




	public String getRelativeReportFileName() {
		return relativeReportFileName;
	}


	public void setRelativeReportFileName(String relativeReportFileName) {
		this.relativeReportFileName = relativeReportFileName;
	}
	/**
	 * @return the dirPrefix
	 */
	public String getDirPrefix() {
		return dirPrefix;
	}
	/**
	 * @param dirPrefix the dirPrefix to set
	 */
	public void setDirPrefix(String dirPrefix) {
		this.dirPrefix = dirPrefix;
	}

	
    
}
