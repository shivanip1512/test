/*
 * Created on Oct 22, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.common.gui.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.JEditorPane;
import javax.swing.RepaintManager;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JEditorPanePrintable extends JEditorPane implements Printable
{
	private static Font f = new Font("Arial", Font.PLAIN, 8);
	private final double SCALE_PERCENT = 0.7;	//Maximum percentage we will scale. 	

	/* (non-Javadoc)
	 * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
	 */
	public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException
	{
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.black);    //set default foreground color to black
		//for faster printing, turn off double buffering
		RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);

		Dimension d = this.getSize();                  //get size of document
		double panelWidth  = d.width;                  //width in pixels
		double panelHeight = d.height;                 //height in pixels
        
		double pageHeight = pf.getImageableHeight();   //height of printer page 
		double pageWidth  = pf.getImageableWidth();    //width of printer page
            
		double scale = pageWidth/panelWidth;           // scale factor
			
		if( scale > SCALE_PERCENT )
			scale = SCALE_PERCENT;

		int totalNumPages = (int)(getBounds().height / pageHeight*scale) + 1;
		
		//make sure not printing empty page
		if(pageIndex >= totalNumPages)
		{
			return Printable.NO_SUCH_PAGE;
		}

		double pageTop = pf.getImageableY()-(pageIndex*pageHeight);

		//shift Graphic to line up with beginning of print-imageable region 
		g2.translate(pf.getImageableX(), pf.getImageableY());
        
		//shift Graphic to line up with beginning of next page to print
		//  (could combine with above translation, but this is clearer)
		g2.translate(0f, -pageIndex*pageHeight);

		//scale the page so the width fits...
		g2.scale(scale, scale);

		this.paint(g2);   //repaint the page for printing
        
		return Printable.PAGE_EXISTS;
	}
}
