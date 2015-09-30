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


import java.util.List;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.SequenceType;

public class ExcludedNamespacesExtension extends ExtensionFunctionDefinition {

	private List<String> excludedNamespaces;
	public ExcludedNamespacesExtension(List<String> excludedNamespaces){
		this.excludedNamespaces = excludedNamespaces;
	}
	
	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] {SequenceType.SINGLE_STRING};
	}

	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName("tw", "http://www.denhaag.nl/tw/extensions", "excludedNamespace");
	}

	@Override
	public SequenceType getResultType(SequenceType[] arg0) {
		return SequenceType.SINGLE_BOOLEAN;
	}

	@Override
	public ExtensionFunctionCall makeCallExpression() {
		return new ExcludedNamespacesFunctionCall(excludedNamespaces);
	}
	static class ExcludedNamespacesFunctionCall extends ExtensionFunctionCall{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1791107476338758202L;
		private List<String> excludedNamespaces;
		
		public ExcludedNamespacesFunctionCall(List<String> excludedNamespaces){
			this.excludedNamespaces = excludedNamespaces;
		}

		@Override
		public Sequence call(XPathContext arg0, Sequence[] arg1) throws XPathException {
			if (arg1.length == 1){
				String value = arg1[0].iterate().next().getStringValue();
				boolean found = false;
				if (excludedNamespaces != null){
					for (int i =0; !found && i < excludedNamespaces.size(); i++){
						found = value.startsWith(excludedNamespaces.get(i));
					}
				}	
				if (found){
					return BooleanValue.TRUE;
				}
			}
			return BooleanValue.FALSE;
		}
		
	}
}
