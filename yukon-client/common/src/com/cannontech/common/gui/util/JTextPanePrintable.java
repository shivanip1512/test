/*
 * Created on Jul 1, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.common.gui.util;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JTextPane;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JTextPanePrintable implements Printable
{
	// The JTextPane to print [the text area will be parsed for lines]
	private JTextPane textPane = null;
	
	private static Font f = new Font("Arial", Font.PLAIN, 10);
	
	//Vector of strings for each line to be printed.
	private Vector vectorOfLines = null;
	//current index of vectorOfLines.
	private int rowCount = 0;
	
	//boolean used to control printing of every other pageIndex, as the pageIndex comes through 2 times for each value.
	private boolean skipping = true;


	public JTextPanePrintable(JTextPane textPane_)
	{
		super();
		textPane = textPane_;
		textPane.setDoubleBuffered(false);
	}

	
	/* (non-Javadoc)
	 * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
	 */
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
		throws PrinterException
	{
		//setup a 2D graphic
		Graphics2D g2 = (Graphics2D) graphics;
		g2.setFont(f);
		int fontHeight = g2.getFontMetrics().getHeight();
		int fontDesent = g2.getFontMetrics().getDescent();
	
		int fontAscent = g2.getFontMetrics().getMaxAscent();
	
		//leave room for page number
		double pageHeight = pageFormat.getImageableHeight() - fontHeight;
		double pageWidth = pageFormat.getImageableWidth();

		int imgX =(int)pageFormat.getImageableX();
		int imgY =(int)pageFormat.getImageableY() + fontAscent;
		 
		g2.translate( imgX, imgY );
		
		int numRowsOnAPage = (int)((pageHeight - imgX) / fontHeight) - 1;
		
		int totalNumPages = (int)Math.ceil(( (double)getVectorOfLines().size()) / numRowsOnAPage);
		
		if( pageIndex >= totalNumPages)
			return NO_SUCH_PAGE;
	
		//Only print rows on every other pageIndex, starting with the second one.
		// For some reason, the pageIndex repeats itself and only prints every other time,
		// therefore we skip the first occurance of each pageIndex value
		if( !skipping )
		{			
			//top right, print the date and time
			SimpleDateFormat format = new SimpleDateFormat("MMMMM dd, yyyy HH:mm");
			g2.drawString(format.format(new Date()), (int)pageWidth-100, 0 );
		
			int i = 0;
			for (i = rowCount; i < (numRowsOnAPage * (pageIndex + 1)) && i < getVectorOfLines().size(); i++)
			{
				g2.drawString(getVectorOfLines().get(i).toString(), imgX , imgY);
				imgY+= fontHeight;
			}
			rowCount = i;

			// bottom center, print the page number
			g2.drawString("Page: " + (pageIndex+1), (int)pageWidth/2 - 20, (int)(pageHeight - fontDesent));
		}
		skipping=!skipping;

		return Printable.PAGE_EXISTS;
	}

	/**
	 * Creates a vector of text lines.  Uses the getText() document to create
	 * a vector for each line (breaks on '\n').
	 * @return Vector
	 */
	public Vector getVectorOfLines()
	{
		if (vectorOfLines == null)
		{
			vectorOfLines = new Vector();
			int beginIndex = 0;
			int i = 0;
			for (i = 0; i < textPane.getText().length(); i++)
			{
				//check if count between \n is greater than 85 chars, this will be too long
				if( i - beginIndex > 85)
				{
					int endIndex = textPane.getText().lastIndexOf(' ', i);
					if( endIndex < 0)	//no blank found
					{
						endIndex = textPane.getText().lastIndexOf('\t', i);
						if( endIndex < 0)	//still not found
							endIndex = i;	//truncate right where we are!
					}
					if( endIndex < beginIndex)	//found one too far back
						endIndex = i;
					vectorOfLines.add(textPane.getText().substring(beginIndex, endIndex));
					i = endIndex;
					beginIndex = i+1;
				}
				if (textPane.getText().charAt(i) == '\n')
				{
					if( beginIndex < i)
						vectorOfLines.add( textPane.getText().substring(beginIndex, i));
					else
						vectorOfLines.add("");
					beginIndex = i+1;
				}
			}
			//MUST DO THE LAST ROW!!!
			vectorOfLines.add(textPane.getText().substring(beginIndex));
		}
		return vectorOfLines;
	}
}
