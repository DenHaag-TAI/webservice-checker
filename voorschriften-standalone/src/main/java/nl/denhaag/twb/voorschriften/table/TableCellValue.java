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


import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;

public class TableCellValue {
	private ImageIcon imageIcon;
	private Color backgroundColor = Color.WHITE;
	private String text;
	private String tooltip;

	public TableCellValue (Color backgroundColor, String tooltip) {
		super();
		this.backgroundColor =backgroundColor;
		this.tooltip = tooltip;
	}	
	
	public TableCellValue (Color backgroundColor, String text, String tooltip) {
		super();
		this.backgroundColor =backgroundColor;
		this.text =text;
		this.tooltip = tooltip;
	}
	
	
	/**
	 * @return the imageIcon
	 */
	public ImageIcon getImageIcon() {
		return imageIcon;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @return the backgroundColor
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param imageIcon the imageIcon to set
	 */
	public void setImageIcon(ImageIcon imageIcon) {
		this.imageIcon = imageIcon;
	}

	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the tooltip
	 */
	public String getTooltip() {
		return tooltip;
	}

	
	
}
