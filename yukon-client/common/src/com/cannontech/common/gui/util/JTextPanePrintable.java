/*
 * Created on Jul 1, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.common.gui.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Vector;

import javax.swing.JTextPane;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JTextPanePrintable extends JTextPane implements Printable, Pageable
{
	private int rowCount = 0;
	private int pageCount  = 1;
	private static Font f = new Font("Arial", Font.PLAIN, 10);
	
	private Vector vectorOfLines = null;
	private boolean skipping = true;
	public JTextPanePrintable(){
		super();
		setDoubleBuffered(false);
	}
	/* (non-Javadoc)
	 * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
	 */
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
		throws PrinterException
	{
		if( !skipping )
		{		
			//getVectorOfLines();	//setup the vector of text lines.
			Graphics2D g2 = (Graphics2D) graphics;
			g2.setFont(f);
						
			FontMetrics fontMetrics = g2.getFontMetrics(f);
			int fontAscent = fontMetrics.getMaxAscent();
			int fontHeight = fontMetrics.getHeight();
			int x =(int)pageFormat.getImageableX();
			int y =(int)pageFormat.getImageableY() + fontAscent;
			int linesPerPage = (int)(pageFormat.getImageableHeight()/fontHeight);
			
			int i;
			for (i = rowCount; i < (linesPerPage*pageCount) && i < vectorOfLines.size(); i++)
			{
				g2.drawString(vectorOfLines.get(i).toString(), x, y);
				y+= fontHeight;
			}
			pageCount++;
			rowCount = i;
		}
		skipping=!skipping;
		return PAGE_EXISTS;
			
	}
	
	PageFormat pf = null;
	
	// READ ME!!!
	//We are going to init all we can in this function cause I'm not
	// really sure where else to do it at this time.
	// Vector of Lines get's reset with text that is in the textPane at the time of print.
	// PageCount is reset to 1 as we are starting a new print.
	// RowCount is reset to 0 as we are starting our total row count with the new print process.
	/** 
	 * This function must be called in order for the row counting and page counting to be correct.
	 * The text Vector of text lines is also populated in this function.
	 * Not calling this function will cause a null pointer on the vector of lines.
	 * */
	public int getNumberOfPages()
	{
		getVectorOfLines();
		pageCount = 1;
		rowCount = 0;
		return (int)(getBounds().height / pf.getImageableHeight()) + 1;
	}
	/* (non-Javadoc)
	 * @see java.awt.print.Pageable#getPageFormat(int)
	 */
	public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException
	{
		return pf;
	}
	
	/**
	 * @param pgFormat
	 */
	public void setPageFormat(PageFormat pgFormat)
	{
		pf = pgFormat;
	}
	/* (non-Javadoc)
	 * @see java.awt.print.Pageable#getPrintable(int)
	 */
	public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException
	{
		return this;
	}

	/**
	 * Creates a vector of text lines.  Uses the getText() document to create
	 * a vector for each line (breaks on '\n').
	 * @return Vector
	 */
	public Vector getVectorOfLines()
	{
		vectorOfLines = new Vector();
		int beginIndex = 0;
		int i = 0;
		for (i = 0; i < getText().length(); i++)
		{
			if( getText().charAt(i) == '\n')
			{
				vectorOfLines.add(getText().substring(beginIndex, i-1));
				beginIndex = i+1;
			}
		}
		//MUST DO THE LAST ROW!!!
		vectorOfLines.add(getText().substring(beginIndex));
		return vectorOfLines;
	}
}
