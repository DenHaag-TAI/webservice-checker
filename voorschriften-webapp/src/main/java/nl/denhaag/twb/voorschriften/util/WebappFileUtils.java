package nl.denhaag.twb.voorschriften.util;

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

import java.io.File;

import javax.servlet.http.HttpSession;

import nl.denhaag.twb.voorschriften.VoorschriftenUtil;
import nl.denhaag.twb.voorschriften.common.util.CommonFileUtils;

public final class WebappFileUtils {

	public static final String SEPARATOR = "/";


	public static File getTempDir(HttpSession session) {
		File tempDir = new File(VoorschriftenUtil.getBaseLocation() + SEPARATOR + session.getId());
		tempDir.mkdirs();
		return tempDir;
	}

	public static void deleteTempDir(HttpSession session) {
		File tempDir = new File(VoorschriftenUtil.getBaseLocation() + SEPARATOR + session.getId());
		CommonFileUtils.delete(tempDir);
	}


	public static File getSourceDir(HttpSession session) {
		File tempDir = getTempDir(session);
		File sourceDir = new File(tempDir, "source");
		sourceDir.mkdirs();
		return sourceDir;
	}
	public static File getReportsDir(HttpSession session) {
		File tempDir = getTempDir(session);
		File reportsDir = new File(tempDir, "reports");
		reportsDir.mkdirs();
		return reportsDir;
	}
	public static File getTempFile(HttpSession session, String name) {
		File tempDir = getTempDir(session);
		File tempFile = new File(tempDir.getAbsolutePath() + SEPARATOR + name);
		return tempFile;
	}

	public static String getRelativePath(HttpSession session, File file) {
		File tempDir = getTempDir(session);
		String filePath = file.getAbsolutePath();
		filePath = filePath.substring(tempDir.getAbsolutePath().length() + 1);
		return filePath;
	}









}
