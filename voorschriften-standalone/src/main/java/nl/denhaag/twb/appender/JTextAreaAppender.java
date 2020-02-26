package nl.denhaag.twb.appender;

/*
 * #%L
 * Webservice voorschriften applicatie
 * %%
 * Copyright (C) 2012 - 2017 Applicatiekoppelingen
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


/*import javax.swing.JTextArea;




//import org.apache.log4j.AppenderSkeleton;
//import org.apache.log4j.Layout;
//import org.apache.log4j.pattern.LogEvent;
//import org.apache.log4j.spi.LoggingEvent;

public class JTextAreaAppender /*extends AppenderSkeleton*/
/*{*/
	//Een iets te eenvoudige Appender
	
/*	
	private JTextArea logTextArea = null;
	public JTextAreaAppender() {
		
	}
	
	public JTextAreaAppender(JTextArea logTextArea) {
		this.logTextArea = logTextArea;
	}
	
//	public JTextAreaAppender(Layout layout,JTextArea logTextArea) {
//		this.layout = layout;
//		this.logTextArea = logTextArea;
//	}

	/**
	 * @return the logTextArea
	 */
/*	
	public JTextArea getLogTextArea() {
		return logTextArea;
	}
*/	

	/**
	 * @param logTextArea the logTextArea to set
	 */
/*	
	public void setLogTextArea(JTextArea logTextArea) {
		this.logTextArea = logTextArea;
	}

/*	
//	@Override
	protected void append(LogEvent event) {
		System.err.println ("JTextAreaAppender append");
		event.getRenderedMessage();
//		String logOutput = this.layout.format(event);
		String logOutput = event.getRenderedMessage();
		System.out.println ("logOutput: "+logOutput);
		logTextArea.append(logOutput +"\n" );
	}
*/	
/*
//	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

//	@Override
	public boolean requiresLayout() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void append(LoggingEvent arg0) {
		// TODO Auto-generated method stub
		
	}
*/	
/*
}
*/
	import org.apache.logging.log4j.core.Filter;
	import org.apache.logging.log4j.core.Layout;
	import org.apache.logging.log4j.core.LogEvent;
	import org.apache.logging.log4j.core.appender.AbstractAppender;
	import org.apache.logging.log4j.core.config.plugins.Plugin;
	import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
	import org.apache.logging.log4j.core.config.plugins.PluginElement;
	import org.apache.logging.log4j.core.config.plugins.PluginFactory;

	import javax.swing.*;
	import java.util.ArrayList;

	import static javax.swing.SwingUtilities.invokeLater;
	import static org.apache.logging.log4j.core.config.Property.EMPTY_ARRAY;
	import static org.apache.logging.log4j.core.layout.PatternLayout.createDefaultLayout;

	@Plugin(name = "JTextAreaAppender", category = "Core", elementType = "appender", printObject = true)
	public class JTextAreaAppender extends AbstractAppender{
	    private static volatile ArrayList<JTextArea> textAreas = new ArrayList<>();

	    private int maxLines;

	    private JTextAreaAppender(String name, Layout<?> layout, Filter filter, int maxLines, boolean ignoreExceptions)	    {
	        super(name, filter, layout, ignoreExceptions, EMPTY_ARRAY);
	        this.maxLines = maxLines;
	    }

	    @SuppressWarnings("unused")
	    @PluginFactory
	    public static JTextAreaAppender createAppender(@PluginAttribute("name") String name,
	                                                   @PluginAttribute("maxLines") int maxLines,
	                                                   @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
	                                                   @PluginElement("Layout") Layout<?> layout,
	                                                   @PluginElement("Filters") Filter filter){
	        if (name == null)	        {
	            LOGGER.error("No name provided for JTextAreaAppender");
	            return null;
	        }

	        if (layout == null){
	            layout = createDefaultLayout();
	        }
	        return new JTextAreaAppender(name, layout, filter, maxLines, ignoreExceptions);
	    }

	    // Add the target JTextArea to be populated and updated by the logging information.
	    public static void addLog4j2TextAreaAppender(final JTextArea textArea)	    {
	        JTextAreaAppender.textAreas.add(textArea);
	    }

	    @Override
	    public void append(LogEvent event){
	        String message = new String(this.getLayout().toByteArray(event));

	        // Append formatted message to text area using the Thread.
	        try{
	            invokeLater(() ->{
	                for (JTextArea textArea : textAreas){
	                    try{
	                        if (textArea != null){
	                            if (textArea.getText().length() == 0){
	                                textArea.setText(message);
	                            } else{
	                                textArea.append("\n" + message);
	                                if (maxLines > 0 & textArea.getLineCount() > maxLines + 1)
	                                {
	                                    int endIdx = textArea.getDocument().getText(0, textArea.getDocument().getLength()).indexOf("\n");
	                                    textArea.getDocument().remove(0, endIdx + 1);
	                                }
	                            }
	                            String content = textArea.getText();
	                            textArea.setText(content.substring(0, content.length() - 1));
	                        }
	                    } catch (Throwable throwable){
	                        throwable.printStackTrace();
	                    }
	                }
	            });
	        } catch (IllegalStateException exception){
	            exception.printStackTrace();
	        }
	    }
	}

