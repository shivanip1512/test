package com.cannontech.esub.table;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import com.cannontech.esub.util.Util;

/**
 * @author alauinger
 */
public class AlarmTable {
	
	private static final String TITLE_TEXT = "Current Alarms";
	
	private static final Color COMPONENT_BACKGROUND_COLOR = Color.WHITE;
	private static final Color MAIN_TITLE_COLOR = Color.RED;
	private static final Color COLUMN_HEADING_TEXT_COLOR = Color.WHITE;
	private static final Color COLUMN_HEADING_BACKGROUND_COLOR = new Color(.35f,.22f,0.0f);
	private static final Color TABLE_BACKGROUND_COLOR = new Color(0.0f, 0.53f,0.73f);
	private static final Color TABLE_TEXT_COLOR = Color.BLACK;
	private static final Color COLUMN_SEPARATOR_COLOR = new Color(0.0f, 0.06f,0.48f);
	
	private static final int COMPONENT_INSETS = 3;
	
	private static final Font TITLE_FONT = new Font("Arial",Font.BOLD,14);
	private static final Font COLUMN_HEADER_FONT = new Font("Arial",Font.PLAIN,12);
	private static final Font TABLE_FONT = COLUMN_HEADER_FONT;
		
	public AlarmTable() {
	}
	
	public void draw(Graphics g, Rectangle rect) {
		int x,y,w,h;
		int width = (int) rect.getWidth();
		int height = (int) rect.getHeight();
		
		int midX = width / 2;
		int midY = height / 2;
		
		g.translate((int)rect.getMinX(),(int)rect.getMinY());
		
		// background
		g.setColor(COMPONENT_BACKGROUND_COLOR);
		g.fillRect(0,0,width,height);
		
		// header
		g.setColor(MAIN_TITLE_COLOR);
		g.setFont(TITLE_FONT);
		
		int textHeight = g.getFontMetrics().getHeight();
		g.drawString(TITLE_TEXT, COMPONENT_INSETS, textHeight + COMPONENT_INSETS);
		
	
		// table background
		g.setColor(COLUMN_SEPARATOR_COLOR);
		x = COMPONENT_INSETS;
		y = textHeight + (2*COMPONENT_INSETS);
		w = width - (2*COMPONENT_INSETS);
		h = height - (COMPONENT_INSETS + y);
		g.fillRect(x,y,w,h);
			
		// column backgrounds
		int colWidth = w / 6;		
		int colHeight = h;
				
		//timestamp		
		y = y+2;
		drawColumn(g,x+2,y,"Timestamp",colWidth,colHeight);
		
		x += colWidth;
		drawColumn(g,x+2,y,"Device Name",colWidth,colHeight);
		
		x += colWidth;
		drawColumn(g,x+2,y,"Point Name", colWidth,colHeight);
		
		x += colWidth;
		drawColumn(g,x+2,y,"Text Message",colWidth*2,colHeight);
		
		x += colWidth*2;
		drawColumn(g,x+2,y,"Username",colWidth,colHeight);
			
		// table text
		
		g.translate((int)rect.getMinX() * -1, (int)rect.getMinY() * -1);
	}
	
	private void drawColumn(Graphics g, int x, int y, String text, int colWidth, int colHeight) {
		//int textHeight =  g.getFontMetrics().getHeight() + 4;
		Rectangle2D strBounds = g.getFontMetrics().getStringBounds(text,null);
	
		int cx = x + (colWidth/2);
		
		g.setColor(TABLE_BACKGROUND_COLOR);
		g.fillRect(x,y,colWidth-4,colHeight);
		
		g.setColor(COLUMN_HEADING_BACKGROUND_COLOR);
		g.fillRect(x,y,colWidth-4,(int)strBounds.getHeight()+4);
		
		g.setColor(COLUMN_HEADING_TEXT_COLOR);
	  	g.drawString(text, cx - (int)(strBounds.getWidth()/2), (int) (y+strBounds.getHeight()));
	}

	

}
