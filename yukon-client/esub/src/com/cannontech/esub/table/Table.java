package com.cannontech.esub.table;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.table.TableModel;

/**
 * @author alauinger
 */
public class Table {
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
		
	private TableModel model;
	private String title;
	
	public void draw(Graphics g, Rectangle rect) {
		TableModel model = getModel();
		
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
		g.drawString(getTitle(), COMPONENT_INSETS, textHeight + COMPONENT_INSETS);
		
	
		// table background
		g.setColor(COLUMN_SEPARATOR_COLOR);
		x = COMPONENT_INSETS;
		y = textHeight + (2*COMPONENT_INSETS);
		w = width - (2*COMPONENT_INSETS);
		h = height - (COMPONENT_INSETS + y);
		g.fillRect(x,y,w,h);
			
		// column backgrounds
		int colWidth = w / model.getColumnCount();		
		int colHeight = h;
				
		// draw columns
		y = y+2;
		for(int i = 0; i < model.getColumnCount(); i++ ) {
//x = drawColumn(g,x+2,y,model,i,colWidth,//lHeight);
			drawColumn(g,x+2,y,model,i,colWidth,colHeight);
			x += colWidth;
		}
					
		g.translate((int)rect.getMinX() * -1, (int)rect.getMinY() * -1);
	}
	

	private void drawColumn(Graphics g, int x, int y, TableModel model, int col, int colWidth, int colHeight) {
		//int textHeight =  g.getFontMetrics().getHeight() + 4;
		String text = model.getColumnName(col);
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D strBounds = fm.getStringBounds(text,null);

		int cx = x + (colWidth/2);
		
		g.setColor(TABLE_BACKGROUND_COLOR);
		g.fillRect(x,y,colWidth-4,colHeight);
		
		g.setColor(COLUMN_HEADING_BACKGROUND_COLOR);
		g.fillRect(x,y,colWidth-4,(int)strBounds.getHeight()+4);
		
		g.setColor(COLUMN_HEADING_TEXT_COLOR);
	  	g.drawString(text, cx - (int)(strBounds.getWidth()/2), (int) (y+strBounds.getHeight()));
	  	
	  	g.setColor(TABLE_TEXT_COLOR);
	  	for(int i = 0; i < model.getRowCount(); i++) {
	  		text = model.getValueAt(i,col).toString();
	  		g.drawString(text, x+2, y + (int)(strBounds.getHeight()*(i+2)));
	  		
	  	}
	}

	



	/**
	 * Returns the model.
	 * @return TableModel
	 */
	public TableModel getModel() {
		return model;
	}

	/**
	 * Returns the title.
	 * @return String
	 */
	public String getTitle() {
		if(title == null) {
			title = "";
		}
		return title;
	}

	/**
	 * Sets the model.
	 * @param model The model to set
	 */
	public void setModel(TableModel model) {
		this.model = model;
	}

	/**
	 * Sets the title.
	 * @param title The title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}

