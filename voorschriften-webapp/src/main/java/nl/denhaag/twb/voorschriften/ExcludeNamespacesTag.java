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


import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import nl.denhaag.twb.voorschriften.common.VoorschriftenChecker;

public class ExcludeNamespacesTag extends SimpleTagSupport {
	public void doTag() throws JspException, IOException {

		PageContext pageContext = (PageContext) getJspContext();
		JspWriter out = pageContext.getOut();
		StringBuilder result = new StringBuilder();
		Map<String, String> excludedNamespaces = VoorschriftenChecker.getMapForExcludedNamespaces();
		for (Entry<String, String> excludedNamespace: excludedNamespaces.entrySet()){
			result.append("<input type=\"checkbox\" name=\"excludedNamespaces\" value=\""+ excludedNamespace.getKey()+ "\">" + excludedNamespace.getKey()+"<span class=\"namespaceDesc\">(" + excludedNamespace.getValue() + ")</span><br/>");
		}
		out.println(result.toString());
	}
}
