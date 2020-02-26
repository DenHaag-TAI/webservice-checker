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


import javax.servlet.ServletContextEvent;


import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Application Lifecycle Listener implementation class VoorschriftenListener
 *
 */
public class VoorschriftenListener implements ServletContextListener {
	private static final String BASE_LOCATION = "BASE_LOCATION";
	private final static Logger LOGGER = LogManager.getLogger(VoorschriftenListener.class);

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent servletContextEvent) {
    	String baseLocation = servletContextEvent.getServletContext().getInitParameter(BASE_LOCATION);
    	if (baseLocation == null || baseLocation.trim().length() == 0){
    		baseLocation = System.getProperty("java.io.tmpdir");
    		LOGGER.error("Geen BASE_LOCATION opgegeven. BASE_LOCATION is nu " + baseLocation);
    	}else {
    		LOGGER.info("BASE_LOCATION is " + baseLocation);
    	}
    	VoorschriftenUtil.setBaseLocation(baseLocation);
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    }
	
}
