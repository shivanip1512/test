package com.cannontech.esub.editor.element;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Dimension2D;

import com.loox.jloox.LxAbstractGroup;
import com.loox.jloox.LxRectangle;
import com.loox.jloox.LxText;

/**
 * @author alauinger
 */
public class TableCell extends LxAbstractGroup {
	private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 12);
	
	private LxText text = new LxText();
	private LxRectangle rect = new LxRectangle();
	
	/**
	 * Returns the text.
	 * @return String
	 */
	public String getText() {
		return text.getText();
	}

	/**
	 * Sets the text.
	 * @param text The text to set
	 */ 
	public void setText(String text) {
		this.text.setText(text);
	}

	private void initialize() {
		text.setFont(DEFAULT_FONT);
		text.setPaint(Color.WHITE);
		Dimension2D d = text.getSize();
		
		rect.setLineColor(Color.RED);
		rect.setSize(d.getWidth() + 5, d.getHeight() + 5);
		
		add(rect);		
		add(text);

	}
	
}
