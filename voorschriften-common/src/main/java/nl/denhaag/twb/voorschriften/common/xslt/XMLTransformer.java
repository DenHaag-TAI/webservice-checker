package nl.denhaag.twb.voorschriften.common.xslt;

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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;
import nl.denhaag.twb.voorschriften.common.TransformResult;
import nl.denhaag.twb.voorschriften.common.util.VoorschriftenLogger;

import org.apache.log4j.Logger;

public class XMLTransformer {

    private final static Logger LOGGER = Logger.getLogger(XMLTransformer.class);

    private VoorschriftenLogger twbLogger;
    public XMLTransformer(VoorschriftenLogger twbLogger){
    	this.twbLogger = twbLogger;
    }

    public TransformResult generateWSDLReport(File inputFile, File outputDir, String generationDate, boolean wsiCompliant,String wsiComplianceText, String otherErrorsText, String baseDir, String dirPrefix, List<String> excludedNamespaces
            ) throws Exception {
		String reportFilename = "WSDL-report-" + inputFile.getName() + ".html";
        try {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("filename", inputFile.getName());
            parameters.put("generationDate", generationDate);
            parameters.put("wsiCompliance", wsiCompliant);
            parameters.put("wsiComplianceText", wsiComplianceText);
            parameters.put("otherErrorsText", otherErrorsText);
            parameters.put("basedir", baseDir);
            return transformClassloader(inputFile, outputDir, "checklist-wsdl.xsl", parameters,  dirPrefix, reportFilename, "WSDL report", excludedNamespaces);
        } catch (Exception e) {
            LOGGER.error(inputFile.getAbsolutePath() + " error: " + e.getMessage(), e);
            twbLogger.logBigMessage(inputFile.getName() + " error: " + e.getMessage());
            return new TransformResult(inputFile.getName());
        }

    }

    public TransformResult generateTypesXSDReport(File inputFile, File outputDir, String generationDate, String baseDir, String dirPrefix, List<String> excludedNamespaces)
            throws TransformerException {
		String reportFilename = "XSD-types-report-" + inputFile.getName() + ".html";
        try {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("filename", inputFile.getName());
            parameters.put("generationDate", generationDate);
            parameters.put("basedir", baseDir);
            return transformClassloader(inputFile, outputDir, "checklist-types-xsd.xsl", parameters, dirPrefix, reportFilename, "XSD types report",excludedNamespaces);
        } catch (Exception e) {
            LOGGER.error(inputFile.getAbsolutePath() + " error: " + e.getMessage(), e);
            twbLogger.logBigMessage(inputFile.getName() + " error: " + e.getMessage());
            return new TransformResult(inputFile.getName());
        }

    }

    public TransformResult generateMessagesXSDReport(File inputFile, File outputDir, String generationDate, String baseDir, String dirPrefix, List<String> excludedNamespaces)
            throws TransformerException {
		String reportFilename = "XSD-messages-report-" + inputFile.getName() + ".html";
        try {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("filename", inputFile.getName());
            parameters.put("generationDate", generationDate);
            parameters.put("basedir", baseDir);
            return transformClassloader(inputFile, outputDir, "checklist-messages-xsd.xsl", parameters, dirPrefix, reportFilename, "XSD messages report",excludedNamespaces);
        } catch (Exception e) {
            LOGGER.error(inputFile.getAbsolutePath() + " error: " + e.getMessage(), e);
            twbLogger.logBigMessage(inputFile.getName() + " error: " + e.getMessage());
            return new TransformResult(inputFile.getName());
        }

    }

    private TransformResult transformClassloader(File inputFile, File outputDir, String xsltLocation, Map<String, Object> parameters, String dirPrefix, String reportFilename, String type, List<String> excludedNamespaces)
            throws Exception {
    	TransformResult result = new TransformResult();
        ClassLoader classLoader = (ClassLoader) Thread.currentThread().getContextClassLoader();
        Source xsltSource = new StreamSource(classLoader.getResourceAsStream(xsltLocation));
    	Processor processor = new Processor(false);
    	processor.registerExtensionFunction(new ExcludedNamespacesExtension(excludedNamespaces));
    	processor.registerExtensionFunction(new MessageExtension(result));
    	XsltCompiler compiler = processor.newXsltCompiler();
    	compiler.setURIResolver(new CustomURIResolver());
    	XsltExecutable executable = compiler.compile(xsltSource);
    	XsltTransformer transformer = executable.load();
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                transformer.setParameter(new QName(entry.getKey()), new XdmAtomicValue(entry.getValue().toString()));
            }
        }
        File reportFile = new File(outputDir, reportFilename);
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(reportFile);
        Source inputSource = new StreamSource(inputStream);
        transformer.setSource(inputSource);
        Serializer destination = processor.newSerializer(outputStream);
        transformer.setDestination(destination);

        try {
        	transformer.transform();
            result.setType(type);
            result.setSourceFileName(inputFile.getName());
            result.setRelativeReportFileName(reportFilename);
            result.setDirPrefix(dirPrefix);
            inputStream.close();
        	outputStream.flush();
        	outputStream.close();
            return result;
        }catch(Exception e){
        	e.printStackTrace();
        	result.setFatalMessage(e.getMessage());
            result.setType(type);
            result.setSourceFileName(inputFile.getName());
            result.setRelativeReportFileName(reportFilename);        	
            result.setDirPrefix(dirPrefix);
        	inputStream.close();
        	outputStream.flush();
        	outputStream.close();
        	reportFile.delete();
        	return result;
        }

    }

}
