package com.cannontech.tdc.test;

/**
 * Insert the type's description here.
 * Creation date: (2/16/00 10:58:18 AM)
 * @author: 
 */

import java.awt.Graphics2D;
import javax.swing.RepaintManager;
import java.awt.print.Printable;

public class TablePrinter implements java.awt.print.Printable 
{
	private javax.swing.JTable table = null;
/**
 * TablePrinter constructor comment.
 */
public TablePrinter() {
	super();
}
/**
 * TablePrinter constructor comment.
 */
public TablePrinter( javax.swing.JTable t ) {
	super();

	table = t;
}
/**
 * print method comment.
 */
public int print(java.awt.Graphics graphics, java.awt.print.PageFormat pageFormat, int pageIndex) throws java.awt.print.PrinterException 
{

	Graphics2D g2 = (Graphics2D) graphics;

	try 
	{
		Printable formatPainter = (Printable) pageFormat;
		formatPainter.print(graphics, pageFormat, pageIndex);
	} 
	catch (ClassCastException exception) {}

	
	/* Move the origin from the corner of the Paper to the corner
	* of the imageable area.*/			
	g2.translate( pageFormat.getImageableX(), pageFormat.getImageableY() );
	
//	table.getTableHeader().paint( g2 );
//	g2.translate( pageFormat.getImageableX(), pageFormat.getImageableY() );

	java.awt.Rectangle componentBounds = table.getBounds(null);
	g2.translate(-componentBounds.x, (int)pageFormat.getImageableHeight() );


   RepaintManager currentManager = RepaintManager.currentManager( table );
   currentManager.setDoubleBufferingEnabled(false);
	
	table.paint( g2 );
	
   currentManager.setDoubleBufferingEnabled( true );
   	
	return PAGE_EXISTS;
}
}
