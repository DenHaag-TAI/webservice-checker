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

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nl.denhaag.twb.voorschriften.common.cxf.ValidationException;
import nl.denhaag.twb.voorschriften.common.cxf.WSDLValidator;
import nl.denhaag.twb.voorschriften.common.util.CommonFileUtils;
import nl.denhaag.twb.voorschriften.common.util.NotSVNFilenameFilter;
import nl.denhaag.twb.voorschriften.common.util.VoorschriftenLogger;
import nl.denhaag.twb.voorschriften.common.xslt.XMLTransformer;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class VoorschriftenChecker {
	private static final Logger LOGGER = Logger.getLogger(VoorschriftenChecker.class);
	private static final String TD_END = "</TD>";
	private static final String TD = "<TD class=\"right\">";
	public static final String SEPARATOR = "/";
	public final static String STANDARD_DATE_FORMAT = "dd MMMM yyyy HH:mm";
	private List<String> failedWSDLs = new ArrayList<String>();
	private XMLTransformer xmlTransformer;
	private VoorschriftenLogger twbLogger;

	public VoorschriftenChecker(VoorschriftenLogger twbLogger) {
		this.twbLogger = twbLogger;
		xmlTransformer = new XMLTransformer(twbLogger);

	}
	public static Map<String, String> getMapForExcludedNamespaces(){
		Map<String, String> excludedNamespaces = new LinkedHashMap<String, String>();
		excludedNamespaces.put("http://www.egem.nl", "StUF webservices");
		excludedNamespaces.put("http://www.opengis.net", "StUF webservices");
		excludedNamespaces.put("http://www.w3.org", "StUF webservices");
		excludedNamespaces.put("http://schemas.microsoft.com", "WCF webservices");
		return excludedNamespaces;
	}
	public static List<String> getListForExcludeNamespaces(){
		Map<String, String> excludedNamespaces = getMapForExcludedNamespaces();
		List<String> result = new ArrayList<String>();
		for (String key: excludedNamespaces.keySet()){
			result.add(key);
		}
		return result;
	}

	public List<TransformResult> generateReports(File sourceDir, File reportDir, List<String> excludedNamespaces) throws Exception {
		LOGGER.log(Level.INFO,"enter generateReports");
		LOGGER.log(Level.DEBUG,"enter generateReports: sourceDir = "+sourceDir+" reportDir = "+reportDir);
		FileUtils.deleteDirectory(reportDir);
		reportDir.mkdirs();
		File cssDir = new File(reportDir, "css");
		cssDir.mkdir();
		File imagesDir = new File(reportDir, "images");
		imagesDir.mkdir();
		CommonFileUtils.copyFileFromClasspath("default.css", cssDir);
		CommonFileUtils.copyFileFromClasspath("highpriority.png", imagesDir);
		CommonFileUtils.copyFileFromClasspath("lowpriority.png", imagesDir);
		CommonFileUtils.copyFileFromClasspath("warning.png", imagesDir);
		CommonFileUtils.copyFileFromClasspath("highpriority-ignored.png", imagesDir);
		CommonFileUtils.copyFileFromClasspath("lowpriority-ignored.png", imagesDir);
		CommonFileUtils.copyFileFromClasspath("warning-ignored.png", imagesDir);
		CommonFileUtils.copyFileFromClasspath("ok.png", imagesDir);
		String index = CommonFileUtils.readFromClasspath("index.html");
		StringBuilder table = new StringBuilder();
		String dateString = getDateString();
		index = index.replaceFirst("GENERATION_DATE", dateString);
		List<TransformResult> results = checkDir(sourceDir, reportDir, "", "", dateString, excludedNamespaces);
		for (TransformResult result : results) {
			table.append("<TR>");
			if (result.getFatalMessage() == null) {
				table.append("<TD class=\"right directory\">");
				table.append(result.getDirPrefix());
				table.append(TD_END);
				table.append("<TD class=\"left\">");
				table.append("<a href=\""
						+ CommonFileUtils.getRelativeFileName(result.getDirPrefix(), result.getRelativeReportFileName()) + "\">"
						+ result.getSourceFileName() + "</a>");
				table.append(TD_END);
				table.append(TD);
				table.append(result.getPassed());
				table.append("<img src=\"images/ok.png\"  title=\"Voldoet aan de voorschriften\"/>");
				table.append(TD_END);
				table.append(TD);
				if (result.getHighpriority() > 0) {
					table.append(result.getHighpriority());
					table.append("<img src=\"images/highpriority.png\"  title=\"Voldoet NIET aan de voorschriften, het heeft hoge prioriteit om dit op te lossen.\"/>");
				}
				if (result.getHighpriorityIgnored() > 0) {
					// table.append(result.getHighpriorityIgnored());
					table.append("<span class=\"ignored\"><img src=\"images/highpriority-ignored.png\"  title=\"Genegeerd\"/></span>");
				}
				table.append(TD_END);
				table.append(TD);
				if (result.getLowpriority() > 0) {
					table.append(result.getLowpriority());
					table.append("<img src=\"images/lowpriority.png\"  title=\"Voldoet NIET aan de voorschriften.\"/>");
				}
				if (result.getLowpriorityIgnored() > 0) {
					// table.append(result.getLowpriorityIgnored());
					table.append("<span class=\"ignored\"><img src=\"images/lowpriority-ignored.png\"  title=\"Genegeerd\"/></span>");
				}
				table.append(TD_END);
				table.append(TD);
				if (result.getWarning() > 0) {
					table.append(result.getWarning());
					table.append("<img src=\"images/warning.png\"  title=\"Voldoet aan de voorschriften, maar de voorschriften ontraden dit.\"/>");
				}
				if (result.getWarningIgnored() > 0) {
					// table.append(result.getWarning());
					table.append("<span class=\"ignored\"><img src=\"images/warning-ignored.png\"  title=\"Genegeerd\"/></span>");
				}
				table.append(TD_END);
				if (result.isWsiCompliance()) {
					table.append("<TD class=\"left\"  colspan=\"2\">");
					table.append(result.getType());
					table.append(TD_END);
				} else {
					table.append("<TD class=\"left\">");
					table.append(result.getType());
					table.append(TD_END);
					table.append("<TD class=\"left fatal\">");
					table.append("<img src=\"images/highpriority.png\"  title=\"Voldoet NIET aan de voorschriften, het heeft hoge prioriteit om dit op te lossen.\"/>");
					table.append("Voldoet NIET aan WS-I Basic Profile 1.1");
					table.append(TD_END);
				}
			} else {
				table.append("<TD class=\"left\">");
				table.append(result.getSourceFileName());
				table.append(TD_END);
				table.append("<td colspan=\"4\">");
				table.append(TD_END);
				table.append("<TD class=\"left\">");
				table.append(result.getType());
				table.append(TD_END);
				table.append("<TD class=\"left fatal\">");
				table.append("<img src=\"images/highpriority.png\"  title=\"Voldoet NIET aan de voorschriften, het heeft hoge prioriteit om dit op te lossen.\"/>");
				table.append(result.getFatalMessage());
				table.append(TD_END);
			}

			table.append("</TR>");
		}
		index = index.replaceFirst("REPLACE", table.toString());
		StringBuilder excludedNamespacesBuilder = new StringBuilder();
		if (excludedNamespaces != null && excludedNamespaces.size() > 0){
			excludedNamespacesBuilder.append("<br/><b>Namespaces zijn genegeerd die beginnen met:</b>\n<ul>");
			for (String excludedNamespace: excludedNamespaces){
				excludedNamespacesBuilder.append("<li>" + excludedNamespace + "</li>");
			}
			excludedNamespacesBuilder.append("</ul>");
		}
		index = index.replaceFirst("EXCLUDED_NAMESPACES", excludedNamespacesBuilder.toString());
		PrintWriter writer = new PrintWriter(new File(reportDir, "index.html"));
		writer.write(index);
		writer.flush();
		writer.close();
		for (String failedWSDL : failedWSDLs) {
			twbLogger.logBigMessage("WSDL not WSI-compliant: " + failedWSDL);

		}
		LOGGER.log(Level.DEBUG,"end generateReports");
		return results;
	}



	public List<TransformResult> checkDir(File sourceDir, File reportDir, String baseDir, String dirPrefix,
			String dateString, List<String> excludedNamespaces) throws Exception {
		
		List<TransformResult> results = new ArrayList<TransformResult>();
		
		LOGGER.log(Level.INFO,"enter checkDir");
		for (File sourceFile : sourceDir.listFiles(new NotSVNFilenameFilter())) {
			if (sourceFile.isDirectory()) {
				twbLogger.logShortMessage("Genereren van rapporten in " + sourceFile.getAbsolutePath());
				File newDestDir = new File(reportDir, sourceFile.getName());
				newDestDir.mkdirs();
				results.addAll(checkDir(sourceFile, newDestDir, baseDir + "../",
						CommonFileUtils.getRelativeFileName(dirPrefix, sourceFile.getName()), dateString, excludedNamespaces));
			} else {
				String fileName = sourceFile.getName();

				if (fileName.toLowerCase().endsWith(".wsdl")) {
					LOGGER.log(Level.DEBUG,"check file");
					boolean wsiCompliant = true;
					StringBuilder bpErrorMessages = new StringBuilder();
					StringBuilder otherErrorMessages = new StringBuilder();
					int highPriority = 0;
					int lowPriority = 0;
					try {
						WSDLValidator.validate(sourceFile.getCanonicalPath());
					} catch (ValidationException ex) {
						LOGGER.log(Level.WARN,"checkDir: ValidationException continue");
						if (ex.getBpMessages().size() > 0) {
							wsiCompliant = false;
							failedWSDLs.add(sourceFile.getAbsolutePath());
							for (String bpMessage : ex.getBpMessages()) {
								System.err.println ("Message: "+bpMessage);
								bpErrorMessages = appendMessage (bpErrorMessages,baseDir, bpMessage ,"high");
								highPriority++;
							}
						}
						if (ex.getOtherMessages().size() > 0) {
							for (String otherMessage : ex.getOtherMessages()) {
								System.err.println ("Message: "+otherMessage);
								otherErrorMessages = appendMessage (otherErrorMessages,baseDir, otherMessage ,"low");
								lowPriority++;
							}
						}
					} catch (Exception ex) {
						//Something went wrong
						LOGGER.log(Level.FATAL,ex.getMessage());
						twbLogger.logBigMessage(ex.getMessage());
						System.exit(-1);
					}
					TransformResult result = xmlTransformer.generateWSDLReport(sourceFile, reportDir, dateString,
							wsiCompliant, bpErrorMessages.toString(), otherErrorMessages.toString(), baseDir,
							dirPrefix, excludedNamespaces);
					result.setWsiCompliance(wsiCompliant);
					result.setHighpriority(result.getHighpriority() + highPriority);
					result.setLowpriority(result.getLowpriority() + lowPriority);
					results.add(result);
					twbLogger.increment();
				} else if (fileName.toLowerCase().endsWith(".xsd")) {
					boolean typesXsd = fileName.matches("[a-zA-Z0-9_-]+-types-v\\d+\\.\\d+\\.xsd");
					boolean messagesXsd = fileName.matches("[a-zA-Z0-9_-]+-messages-v\\d+\\.\\d+\\.xsd");
					boolean invalidFileName = !typesXsd && !messagesXsd;
					if (typesXsd || invalidFileName) {
						results.add(xmlTransformer.generateTypesXSDReport(sourceFile, reportDir, dateString, baseDir,
								dirPrefix, excludedNamespaces));
					}
					twbLogger.increment();
					if (messagesXsd || invalidFileName) {
						results.add(xmlTransformer.generateMessagesXSDReport(sourceFile, reportDir, dateString,
								baseDir, dirPrefix, excludedNamespaces));
					}
					twbLogger.increment();
				}
			}
		}
		LOGGER.log(Level.DEBUG,"end checkDir");
		return results;
	}
	
	
	private StringBuilder appendMessage (StringBuilder buildMessage, String baseDir, String message, String priority){
		LOGGER.log(Level.INFO,"enter appendMessage");
		LOGGER.log(Level.DEBUG,"enter appendMessage: buildMessage = "+buildMessage+" baseDir = "+baseDir+" message ="+message+" priority = "+priority);
		buildMessage.append("<TR>");
		buildMessage.append(TD);
		buildMessage.append("<img src=\"" +baseDir + "images/"+priority+"priority.png\"  title=\"Voldoet NIET aan de voorschriften, het heeft hoge prioriteit om dit op te lossen.\"/>");
		buildMessage.append(TD_END);
		buildMessage.append("<TD class=\"left\">");
		buildMessage.append(message);
		buildMessage.append(TD_END);
		buildMessage.append("</TR>");
		LOGGER.log(Level.DEBUG,"end appendMessage");
		return buildMessage;
	}

	private static String getDateString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		simpleDateFormat.applyPattern(STANDARD_DATE_FORMAT);
		return simpleDateFormat.format(Calendar.getInstance().getTime());
	}
}
