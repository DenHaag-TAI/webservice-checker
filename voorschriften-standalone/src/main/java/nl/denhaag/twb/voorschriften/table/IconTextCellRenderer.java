package nl.denhaag.twb.voorschriften.table;

/*
 * #%L
 * Webservice voorschriften applicatie
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


import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class IconTextCellRenderer extends DefaultTableCellRenderer {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1296263661334297079L;

	public Component getTableCellRendererComponent(JTable table,
                                  Object value,
                                  boolean isSelected,
                                  boolean hasFocus,
                                  int row,
                                  int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof TableCellValue){
        	TableCellValue tableCellValue = (TableCellValue) value;
        	if (tableCellValue.getText() != null ){
        		setText(tableCellValue.getText());	
        	}else {
        		setText(null);
        	}
        	if (tableCellValue.getImageIcon() != null ){
        		setIcon(tableCellValue.getImageIcon());
        	}else {
        		setIcon(null);
        	}
        	if (isSelected){
        		setBackground(table.getSelectionBackground());
        	}else {
	        	if (tableCellValue.getBackgroundColor() != null ){
	        		setBackground(tableCellValue.getBackgroundColor());
	        	}else {
	        		setBackground(null);
	        	}
        	}
        	if (tableCellValue.getTooltip() != null ){
        		setToolTipText(tableCellValue.getTooltip());
        	}else {
        		setToolTipText(null);
        	}
        }
        return this;
    }

}
