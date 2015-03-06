package nl.denhaag.twb.voorschriften;

/*
 * #%L
 * Voorschriften webapplicatie
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


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.denhaag.twb.voorschriften.common.util.CommonFileUtils;
import nl.denhaag.twb.voorschriften.util.WebappFileUtils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Servlet implementation class UploadServlet
 */
public class UploadServlet extends HttpServlet {
	private final static Logger LOGGER = Logger.getLogger(UploadServlet.class);
	private static final long serialVersionUID = 1L;


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		ServletFileUpload.isMultipartContent(request);
		// Create a factory for disk-based file items
		FileItemFactory factory = new DiskFileItemFactory();

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		try {
			List<String> excludedNamespaces = new ArrayList<String>();
			// Parse the request
			@SuppressWarnings("unchecked")
			List<FileItem> items = upload.parseRequest(request);
			FileItem uploadFile = null;
			for (FileItem item : items) {
				String fieldName = item.getFieldName();
				if (item.isFormField()) {
					if ("excludedNamespaces".equals(fieldName)){
						String value = item.getString();
						if (StringUtils.isNotBlank(value)){
							excludedNamespaces.add(value);
						}
					}
				} else {
					if ("uploadFile".equals(fieldName) && item.getSize() > 0) {
						uploadFile = item;						
					}
				}
			}
			if (uploadFile == null){
				forwardToErrorPage(request, response, "Geen zip, wsdl of xsd bestand geselecteerd.");
			}else {
				process(request, response, uploadFile, excludedNamespaces);
				
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			forwardToErrorPage(request, response, e.getMessage());
		}

	}
	private void forwardToErrorPage(HttpServletRequest request, HttpServletResponse response, String message) throws ServletException, IOException{
		WebappFileUtils.deleteTempDir(request.getSession());
		request.setAttribute("errorMessage",message);
		getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);		
	}
	private void process(HttpServletRequest request, HttpServletResponse response, FileItem uploadFile, List<String> excludedNamespaces) throws Exception{
		long startTime = System.currentTimeMillis();
	
		String fileName = uploadFile.getName();
		if (fileName.contains("\\")){
			int lastIndex = fileName.lastIndexOf('\\');
			fileName = fileName.substring(lastIndex+1);
		}
		if (fileName.contains("/")){
			int lastIndex = fileName.lastIndexOf('/');
			fileName = fileName.substring(lastIndex+1);
		}
		String logString = "Start met genereren van rapportage van " + fileName + " (inclusief WSI rapportage)...";
		LOGGER.info(logString);
		File sourceDir = WebappFileUtils.getSourceDir(request.getSession());
		File reportsDir = WebappFileUtils.getReportsDir(request.getSession());
		if (fileName.endsWith(".zip")){
			File uploadedFile = WebappFileUtils.getTempFile(request.getSession(), fileName);
			uploadFile.write(uploadedFile);
			CommonFileUtils.unzip(sourceDir, uploadedFile);
		}else {
			File uploadedFile =new File(sourceDir, fileName);
			uploadFile.write(uploadedFile);
		}
		VoorschriftenUtil.getVoorschriftenChecker().generateReports(sourceDir, reportsDir, excludedNamespaces);
		String outputFileName = "reports-" + fileName;
		if (!outputFileName.endsWith(".zip")){
			outputFileName+=".zip";
		}
		File zipFile = WebappFileUtils.getTempFile(request.getSession(), outputFileName);
		CommonFileUtils.zip(reportsDir, zipFile);
		logString = "Genereren van rapportage van " + fileName + " voltooid " + (System.currentTimeMillis() - startTime) + "ms (inclusief WSI rapportage)";
		LOGGER.info(logString);
		doDownload(response, zipFile);
		WebappFileUtils.deleteTempDir(request.getSession());
	}
	private void doDownload(HttpServletResponse response, File zipFile) throws IOException{
        //
        //  Set the response and go!
        //
        //
		response.setContentType("application/zip");
		response.setContentLength( (int)zipFile.length() );
		response.setHeader( "Content-Disposition", "attachment; filename=\"" + zipFile.getName() + "\"" );
		ServletOutputStream op       = response.getOutputStream();
        //
        //  Stream to the requester.
        //
        byte[] bbuf = new byte[1024];
        DataInputStream in = new DataInputStream(new FileInputStream(zipFile));
        int                 length   = 0;
        while ((in != null) && ((length = in.read(bbuf)) != -1))
        {
            op.write(bbuf,0,length);
        }

        in.close();
        op.flush();
        op.close();
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("get");
	}

}
