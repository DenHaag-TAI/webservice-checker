package nl.denhaag.twb.voorschriften.common.cxf;

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


import java.util.ArrayList;
import java.util.List;
/**
 * Extentions of CXF WS-I validator
 *
 */
public class ValidationException extends RuntimeException {

	private List<String> bpMessages = new ArrayList<String>(); 
	private List<String> otherMessages = new ArrayList<String>(); 
	/**
	 * 
	 */
	private static final long serialVersionUID = 8292482795149967419L;

	public ValidationException(List<String> bpMessages, List<String> otherMessages){
		if (bpMessages != null){
			this.bpMessages.addAll(bpMessages);
		}
		if (otherMessages != null){
			this.otherMessages.addAll(otherMessages);
		}
	}

	/**
	 * @return the bpMessages
	 */
	public List<String> getBpMessages() {
		return bpMessages;
	}

	/**
	 * @return the otherMessages
	 */
	public List<String> getOtherMessages() {
		return otherMessages;
	}
	
}
