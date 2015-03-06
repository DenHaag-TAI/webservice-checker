/**
 * 
 */
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


import nl.denhaag.twb.voorschriften.common.VoorschriftenChecker;
import nl.denhaag.twb.voorschriften.common.util.VoorschriftenLogger;

import org.apache.log4j.Logger;

public class VoorschriftenUtil {
	private final static Logger LOGGER = Logger.getLogger(VoorschriftenUtil.class);
	private static VoorschriftenChecker voorschriftenChecker;
	private static String baseLocation;

	
	public static VoorschriftenChecker getVoorschriftenChecker() {
		if (voorschriftenChecker == null){
			VoorschriftenLogger twbLogger = new VoorschriftenLogger() {
				
			
				@Override
				public void logShortMessage(String message) {
					LOGGER.info(message);
					
				}
				
				@Override
				public void logBigMessage(String message) {
					LOGGER.info(message);
					
				}
				
				@Override
				public void increment() {
									
				}
			};
			VoorschriftenUtil.voorschriftenChecker = new VoorschriftenChecker(twbLogger);
		}
		return voorschriftenChecker;
	}

	public static void printInfo(String string){
    	LOGGER.info(string);
    }
    public static void printError(String string, Exception e){
    	LOGGER.error(string, e);
    }
    
    public static void printError(String string){
    	LOGGER.error(string);
    }
    public static void printMessage(String string){
    	LOGGER.info(string);
    }
	public static String getBaseLocation() {
		return baseLocation;
	}
	public static void setBaseLocation(String baseLocation) {
		VoorschriftenUtil.baseLocation = baseLocation;
	}

    
}
