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

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import java.util.HashSet;
import java.util.Set;

import org.apache.cxf.common.i18n.Message;
import org.apache.cxf.tools.common.AbstractCXFToolContainer;
import org.apache.cxf.tools.common.CommandInterfaceUtils;
import org.apache.cxf.tools.common.ToolConstants;
import org.apache.cxf.tools.common.ToolContext;
import org.apache.cxf.tools.common.ToolException;
import org.apache.cxf.tools.common.toolspec.ToolRunner;
import org.apache.cxf.tools.common.toolspec.ToolSpec;
import org.apache.cxf.tools.common.toolspec.parser.BadUsageException;
import org.apache.cxf.tools.common.toolspec.parser.CommandDocument;
import org.apache.cxf.tools.common.toolspec.parser.ErrorVisitor;

/**
 * Extentions of CXF WS-I validator
 *
 */
public class WSDLValidator extends AbstractCXFToolContainer {
	   private static final String TOOL_NAME = "wsdlvalidator";

	    public WSDLValidator(ToolSpec toolspec) throws Exception {
	        super(TOOL_NAME, toolspec);
	    }

	    private Set<String> getArrayKeys() {
	        Set<String> set = new HashSet<String>();
	        set.add(ToolConstants.CFG_SCHEMA_URL);
	        return set;
	    }
	    
	    public boolean executeForMaven() {
	        super.execute(false);
	        ToolContext env = getContext();
	        env.setParameters(getParametersMap(getArrayKeys()));
	        if (isVerboseOn()) {
	            env.put(ToolConstants.CFG_VERBOSE, Boolean.TRUE);
	        }
	        env.put(ToolConstants.CFG_VALIDATE_WSDL, "all");
	        env.put(ToolConstants.CFG_CMD_ARG, getArgument());

	        WSDL11Validator wsdlValidator = new WSDL11Validator(null, env, getBus());
	        return wsdlValidator.isValid();
	    }

	    public void execute(boolean exitOnFinish) {
	        try {
	            super.execute(exitOnFinish);
	            if (!hasInfoOption()) {
	                ToolContext env = getContext();
	                env.setParameters(getParametersMap(getArrayKeys()));
	                if (isVerboseOn()) {
	                    env.put(ToolConstants.CFG_VERBOSE, Boolean.TRUE);
	                }
	                env.put(ToolConstants.CFG_VALIDATE_WSDL, "all");
	                env.put(ToolConstants.CFG_CMD_ARG, getArgument());

	                WSDL11Validator wsdlValidator = new WSDL11Validator(null, env, getBus());
	                if (wsdlValidator.isValid()) {
	                    System.out.println("Passed Validation : Valid WSDL ");
	                }
	            }
	        } catch (ValidationException ex) {
	        	throw ex;
	        } catch (Exception ex) {
	            err.println("WSDLValidator Error : " + ex.getMessage());
	            err.println();
	            if (isVerboseOn()) {
	                err.println("[+] Verbose turned on");
	                err.println();
	                ex.printStackTrace(err);
	            }
	        }
	    }

	    public static void validate(String file) throws Exception{
	        CommandInterfaceUtils.commandCommonMain();
            ToolRunner.runTool(WSDLValidator.class, org.apache.cxf.tools.validator.WSDLValidator.class
	               .getResourceAsStream("wsdlvalidator.xml"), false, new String[]{file});
	    }

	    public void checkParams(ErrorVisitor errors) throws ToolException {
	        CommandDocument doc = super.getCommandDocument();

	        if (!doc.hasParameter("wsdlurl")) {
	            errors.add(new ErrorVisitor.UserError("WSDL/SCHEMA URL has to be specified"));
	        }
	        if (errors.getErrors().size() > 0) {
	            Message msg = new Message("PARAMETER_MISSING", LOG);
	            throw new ToolException(msg, new BadUsageException(getUsage(), errors));
	        }
	    }
}
