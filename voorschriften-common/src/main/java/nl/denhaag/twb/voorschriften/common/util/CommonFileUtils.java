package nl.denhaag.twb.voorschriften.common.util;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
/**
 * 
 */
public final class CommonFileUtils {

	public static final String SEPARATOR = "/";


	private static String getRelativePath(String prefix, File file) {
		String filePath = file.getAbsolutePath();
		filePath = filePath.substring(prefix.length() + 1);
		return filePath;
	}

	public static void unzip(File outputDir, File file) throws IOException {
		outputDir.mkdirs();
		String prefix = outputDir.getAbsolutePath() + CommonFileUtils.SEPARATOR;
		Enumeration<? extends ZipEntry> entries;
		ZipFile zipFile;
		zipFile = new ZipFile(file);
		entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			File extractedFile = new File(prefix + entry.getName());
			if (entry.isDirectory()) {
				extractedFile.mkdirs();
			} else {
				File parentDirectory = extractedFile.getParentFile();
				if (!parentDirectory.exists()) {
					parentDirectory.mkdirs();
				}
				FileOutputStream fileoutputstream = new FileOutputStream(extractedFile);
				InputStream inputStream = zipFile.getInputStream(entry);
				int n = -1;
				byte[] buf = new byte[1024];
				while ((n = inputStream.read(buf, 0, 1024)) > -1) {
					fileoutputstream.write(buf, 0, n);
				}
				fileoutputstream.close();
				zipFile.getInputStream(entry);

			}

		}

		zipFile.close();
	}

	public static void zip(File sourceDir, File zipfile) throws IOException {
		// Check that the directory is a directory, and get its contents
		if (!sourceDir.isDirectory()) {
			throw new IllegalArgumentException("Not a directory:  " + sourceDir.getPath());
		}
		FileOutputStream fos = new FileOutputStream(zipfile);
		ZipOutputStream zipOutputStream = new ZipOutputStream(fos);
		zip(sourceDir.listFiles(), zipOutputStream, sourceDir.getAbsolutePath());
		zipOutputStream.flush();
		zipOutputStream.close();
		fos.close();

	}

	private static void zip(File[] files, ZipOutputStream zipOutputStream, String sourceDirPath) throws IOException {
		for (File file : files) {
			if (file.isDirectory()) {
				zip(file.listFiles(), zipOutputStream, sourceDirPath);
			} else {
					byte[] buffer = new byte[1024]; // Create a buffer for
													// copying
					int bytesRead;
					ZipEntry entry = new ZipEntry(getRelativePath(sourceDirPath, file));
					zipOutputStream.putNextEntry(entry);
					FileInputStream fileInputStream = new FileInputStream(file); // Stream
																					// to
																					// read
																					// file
					while ((bytesRead = fileInputStream.read(buffer)) != -1) {
						zipOutputStream.write(buffer, 0, bytesRead);
					}
					zipOutputStream.closeEntry();
					fileInputStream.close();


			}
		}
	}

	public static void delete(File file) {
		delete(new File[] { file });
	}

	private static void delete(File[] files) {
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					// delete childs
					delete(files[i].listFiles());
				}
				files[i].delete();
			}
		}
	}

	public static void copyFile(File in, File out) throws Exception {
		FileInputStream fis = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (fis != null)
				fis.close();
			if (fos != null)
				fos.close();
		}
	}
	public static void copyFile(InputStream in, File out) throws Exception {
		FileOutputStream fos = new FileOutputStream(out);
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = in.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (in != null)
				in.close();
			if (fos != null)
				fos.close();
		}
	}
	public static String readFromClasspath(String name) throws IOException {
		ClassLoader classLoader = (ClassLoader) Thread.currentThread().getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream(name);
		StringBuilder emailContent = new StringBuilder();
		InputStreamReader fileReader = new InputStreamReader(is);
		BufferedReader bFileReader = new BufferedReader(fileReader);
		String line = null;
		
		while ((line = bFileReader.readLine()) != null) {
			emailContent.append(line + "\n");
		}
		return emailContent.toString();
	}

	public static void copyFileFromClasspath(String name, File outputDir) throws Exception {
		ClassLoader classLoader = (ClassLoader) Thread.currentThread().getContextClassLoader();
		InputStream image = classLoader.getResourceAsStream(name);
		CommonFileUtils.copyFile(image, new File(outputDir, name));
	}

	public static int count(File sourceDir) throws Exception {
		int count = 0;
		for (File sourceFile : sourceDir.listFiles(new NotSVNFilenameFilter())) {
			if (sourceFile.isDirectory()) {
				count += count(sourceFile);
			} else {
				String fileName = sourceFile.getName();

				if (fileName.toLowerCase().endsWith(".wsdl")) {
					//count++;
					count++;
				} else if (fileName.toLowerCase().endsWith(".xsd")) {
					count++;
					count++;
				}
			}
		}
		return count;
	}

	public static String getRelativeFileName(String dir, String filename) {
		String result = null;
		if (StringUtils.isEmpty(dir)) {
			result = filename;
		} else {
			result = dir + "/" + filename;
		}
		return result;
	}
}
