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

import nl.denhaag.twb.voorschriften.common.util.VoorschriftenLogger;

import org.junit.Ignore;
import org.junit.Test;
@Ignore
public class VoorschriftenCheckerTest {

	@Test @Ignore
	public void testGenerateReportsFileFileBoolean() throws Exception {
		File source = new File("C:/development/wsdl");
		File target = new File("C:/development/wsdl-report");
		VoorschriftenLogger twbLogger = new VoorschriftenLogger() {
			
			
			@Override
			public void logShortMessage(String arg0) {
				//System.out.println(arg0);
				
			}
			
			@Override
			public void logBigMessage(String arg0) {
				//System.out.println(arg0);
			}
			
			@Override
			public void increment() {
				// TODO Auto-generated method stub
				
			}
		};
		VoorschriftenChecker checker = new VoorschriftenChecker(twbLogger);
		checker.generateReports(source, target, VoorschriftenChecker.getListForExcludeNamespaces());
	}

}
