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


import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.EmptySequence;
import net.sf.saxon.value.SequenceType;
import nl.denhaag.twb.voorschriften.common.TransformResult;

public class MessageExtension extends ExtensionFunctionDefinition {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7083194477835103960L;

	private TransformResult result;
	public MessageExtension(TransformResult result){
		this.result = result;
	}
	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] {SequenceType.SINGLE_STRING};
	}

	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName("tw", "http://www.denhaag.nl/tw/extensions", "message");
	}

	@Override
	public SequenceType getResultType(SequenceType[] arg0) {
		return SequenceType.EMPTY_SEQUENCE;
	}

	@Override
	public ExtensionFunctionCall makeCallExpression() {
		return new MessageFunctionCall(result);
	}
	
	static class MessageFunctionCall extends ExtensionFunctionCall{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1791107476338758202L;
		private TransformResult result;
		
		public MessageFunctionCall(TransformResult result){
			this.result = result;
		}

		@Override
		public Sequence call(XPathContext arg0, Sequence[] arg1) throws XPathException {
			if (arg1.length == 1){
				String value = arg1[0].iterate().next().getStringValue();
            	if ("passed".equalsIgnoreCase(value)){
            		result.setPassed(result.getPassed() + 1);
            	}else if ("highpriority".equalsIgnoreCase(value)){
            		result.setHighpriority(result.getHighpriority() + 1);
            	}else if ("lowpriority".equalsIgnoreCase(value)){
            		result.setLowpriority(result.getLowpriority() + 1);
            	}else if  ("warning".equalsIgnoreCase(value)){
            		result.setWarning(result.getWarning() + 1);
            	}else if ("highpriority-ignored".equalsIgnoreCase(value)){
            		result.setHighpriorityIgnored(result.getHighpriorityIgnored() + 1);
            	}else if ("lowpriority-ignored".equalsIgnoreCase(value)){
            		result.setLowpriorityIgnored(result.getLowpriorityIgnored() + 1);
            	}else if  ("warning-ignored".equalsIgnoreCase(value)){
            		result.setWarningIgnored(result.getWarningIgnored() + 1);
            	}
			}
			//return null;
			return EmptySequence.getInstance();
		}
		
	}

}
