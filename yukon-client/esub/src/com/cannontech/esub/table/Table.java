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
	private static final Color MAIN_TITLE_COLOR = new Color(0,0,153);
	private static final Color COLUMN_HEADING_TEXT_COLOR = Color.BLACK;
	private static final Color COLUMN_HEADING_BACKGROUND_COLOR = new Color(204,204,128);
	private static final Color TABLE_BACKGROUND_COLOR = Color.BLACK;
	private static final Color TABLE_TEXT_COLOR = Color.WHITE;
	private static final Color COLUMN_SEPARATOR_COLOR = Color.WHITE;
	
	private static final int COMPONENT_INSETS = 0;
	
	private static final Font TITLE_FONT = new Font("Arial",Font.BOLD,12);
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
		g.fillRect(0,0,width+1,height+1);
		
		// header
		g.setColor(MAIN_TITLE_COLOR);
		g.setFont(TITLE_FONT);
		
		int textHeight = g.getFontMetrics().getHeight();
		int titleHeaderHeight = textHeight + 10;
		g.drawString(getTitle(), 5, titleHeaderHeight/2 + textHeight/2);
		
	
		// table background
		g.setColor(COLUMN_SEPARATOR_COLOR);
		x = 2;
		y = titleHeaderHeight;
		w = width;
		h = height - y;
			
		// column backgrounds
		int[] colWidth = calcColumnWidths(g, model, w);
		int colHeight = h;
				
		// draw columns
		y = y+2;
		for(int i = 0; i < model.getColumnCount(); i++ ) {
			drawColumn(g,x,y,model,i,colWidth[i],colHeight-3);
			x += colWidth[i]+1;
		}
					
		g.translate((int)rect.getMinX() * -1, (int)rect.getMinY() * -1);
	}
	

	private void drawColumn(Graphics g, int x, int y, TableModel model, int col, int colWidth, int colHeight) {
		String text = model.getColumnName(col);
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D strBounds = fm.getStringBounds(text,null);

		int cx = x + (colWidth/2);
		int colHeadingHeight = (int) strBounds.getHeight()+4+6;
		
		g.setColor(TABLE_BACKGROUND_COLOR);
		g.fillRect(x,y,colWidth,colHeight);
		
		g.setColor(COLUMN_HEADING_BACKGROUND_COLOR);
		g.fillRect(x,y,colWidth,colHeadingHeight);
		
		g.setColor(COLUMN_HEADING_TEXT_COLOR);
	  	g.drawString(text, cx - (int)(strBounds.getWidth()/2), (int) (y+strBounds.getHeight())+4);
	  	
	  	y += colHeadingHeight;
	  	
	  	g.setColor(TABLE_TEXT_COLOR);
	  	for(int i = 0; i < model.getRowCount(); i++) {
	  		text = model.getValueAt(i,col).toString();
	  		int valWidth = fm.stringWidth(text);
	  		g.drawString(text, x+2, y + (int)(strBounds.getHeight()*(i+1)));
	  		
	  	}
	}


	private int[] calcColumnWidths(Graphics g, TableModel model, int tableWidth) {
		int[] widths = new int[model.getColumnCount()];
		FontMetrics fm = g.getFontMetrics();

		tableWidth = tableWidth - (model.getColumnCount()+2);

		int allocatedWidth = 0;
		
		// find the minimum width needed for each column
		for(int col = model.getColumnCount()-1; col >= 0; col--) {
			for(int row = model.getRowCount()-1; row >= 0; row--) {
				String val = model.getValueAt(row,col).toString();
				int valWidth = fm.stringWidth(val);
				if(widths[col] < valWidth) {
					widths[col] = valWidth;
				}
			}
			allocatedWidth += widths[col];	
		}
		
		// if the min width is less than the table width, allocate the rest
		// of the space evenly, if it isn't then doh!
		int extraWidth = Math.max(tableWidth - allocatedWidth, 0); 
		int extraCellWidth = extraWidth / model.getColumnCount();
		int remainingWidth = extraWidth % model.getColumnCount();
		
		System.out.println("tablewidth:" + tableWidth + " allocated: " + allocatedWidth + "  extra:" + extraCellWidth);
		for(int col = model.getColumnCount()-1; col >= 0; col--) {
			widths[col] += extraCellWidth;	
		}
		widths[widths.length-1] += remainingWidth;
		
		return widths;
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

