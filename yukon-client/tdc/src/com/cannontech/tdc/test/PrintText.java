package com.cannontech.tdc.test;

/**
 * Insert the type's description here.
 * Creation date: (2/14/00 2:33:45 PM)
 * @author: 
 */
import java.awt.Graphics2D;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Vector;

public class PrintText implements java.awt.print.Printable 
{
	private PageFormat pageFormat = null;
	private Vector data = null;
	private boolean printerSetUp = false;
/**
 * PrintText constructor comment.
 */
public PrintText() 
{
	super();

	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/00 2:34:26 PM)
 * @param data java.lang.String[]
 */
public PrintText(Vector receivedData, java.awt.print.PageFormat recPageFormat, boolean setUp ) 
{
	data = receivedData;
	pageFormat = recPageFormat;
	printerSetUp = setUp;

	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/00 2:34:26 PM)
 * @param data java.lang.String[]
 */
public PrintText(Vector receivedData, boolean setUp) 
{
	data = receivedData;
	printerSetUp = setUp;

	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (5/12/00 11:22:35 AM)
 * Version: <version>
 */
private void displayData() 
{
	if( data != null && data.size() > 0 )
	{
		for( int i = 0; i < data.size(); i++ )
			com.cannontech.clientutils.CTILogger.info(data.elementAt(i));
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/00 2:36:06 PM)
 */
private void initialize() 
{
	if( data == null )
		data = new Vector( 20 );	
}
/**
 * print method comment.
 */
public int print(java.awt.Graphics pg, java.awt.print.PageFormat format, int pageIndex) throws java.awt.print.PrinterException
{
/*	if( pageFormat != null )
		format = pageFormat;
		
  	javax.swing.JLabel m_title = new javax.swing.JLabel("HI");
	
	pg.translate( (int)format.getImageableX(), 
	  			  (int)format.getImageableY());
	int wPage = 0;
	int hPage = 0;
	if (format.getOrientation() == format.PORTRAIT) 
	{
	  wPage = (int)format.getImageableWidth();
	  hPage = (int)format.getImageableHeight();
	}
	else 
	{
	  wPage = (int)format.getImageableWidth();
	  wPage += wPage/2;
	  hPage = (int)format.getImageableHeight();
	  pg.setClip(0,0,wPage,hPage);
	}

	int y = 0;	
	pg.setFont( new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 8) );
	FontMetrics fm = pg.getFontMetrics();

	int h = fm.getAscent();
	y += h;

	int rowCount = data.size();
	h = fm.getHeight();
	int rowH = Math.max((int)(h*1.5), 10);
	int rowPerPage = (hPage-y)/rowH;
	int m_maxNumPage = Math.max((int)Math.ceil( rowCount / (double)rowPerPage), 1);
	
	int iniRow = pageIndex*rowPerPage;
	int endRow = Math.min( rowCount, iniRow+rowPerPage);

	int row = 0, col = 0;
	for ( row = iniRow; row < endRow; row++ )
	{
	 	y += h;
	  
		Object obj =  data.elementAt( row );
		String str = obj.toString();
com.cannontech.clientutils.CTILogger.info("STR = " + str );		
		pg.drawString(str, 0, y);
	}
*/
Graphics2D g2d = (Graphics2D) pg.create();
g2d.translate( (int)format.getImageableX(), 
  			  (int)format.getImageableY());
g2d.setFont( new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 8) );

Object obj =  data.elementAt( 0 );
com.cannontech.clientutils.CTILogger.info("STR = " + obj.toString() );		
g2d.drawString(obj.toString(), 0, 30);

	System.gc();
	return PAGE_EXISTS;
}
/**
 * print method comment.
 */
public int printNew(java.awt.Graphics pg, java.awt.print.PageFormat format, int pageIndex) throws java.awt.print.PrinterException
{
/*	if( pageFormat != null )
		format = pageFormat;
		
  	javax.swing.JLabel m_title = new javax.swing.JLabel("HI");
	
	pg.translate( (int)format.getImageableX(), 
	  			  (int)format.getImageableY());
	int wPage = 0;
	int hPage = 0;
	if (format.getOrientation() == format.PORTRAIT) 
	{
	  wPage = (int)format.getImageableWidth();
	  hPage = (int)format.getImageableHeight();
	}
	else 
	{
	  wPage = (int)format.getImageableWidth();
	  wPage += wPage/2;
	  hPage = (int)format.getImageableHeight();
	  pg.setClip(0,0,wPage,hPage);
	}

	int y = 0;	
	pg.setFont( new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 8) );
	FontMetrics fm = pg.getFontMetrics();

	int h = fm.getAscent();
	y += h;

	int rowCount = data.size();
	h = fm.getHeight();
	int rowH = Math.max((int)(h*1.5), 10);
	int rowPerPage = (hPage-y)/rowH;
	int m_maxNumPage = Math.max((int)Math.ceil( rowCount / (double)rowPerPage), 1);
	
	int iniRow = pageIndex*rowPerPage;
	int endRow = Math.min( rowCount, iniRow+rowPerPage);

	int row = 0, col = 0;
	for ( row = iniRow; row < endRow; row++ )
	{
	 	y += h;
	  
		Object obj =  data.elementAt( row );
		String str = obj.toString();
com.cannontech.clientutils.CTILogger.info("STR = " + str );		
		pg.drawString(str, 0, y);
	}
*/
Graphics2D g2d = (Graphics2D) pg.create();

Object obj =  data.elementAt( 0 );
com.cannontech.clientutils.CTILogger.info("STR = " + obj.toString() );		
g2d.drawString(obj.toString(), 0, 0);

	System.gc();
	return PAGE_EXISTS;
}
/**
 * print method comment.
 */
public int printOLD(java.awt.Graphics graphics, java.awt.print.PageFormat format, int pageIndex) throws java.awt.print.PrinterException
{
	if( pageFormat != null )
		format = pageFormat;

	graphics.setFont( new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 8) );
	Graphics2D g2d = (Graphics2D) graphics.create();

	g2d.translate(format.getImageableX(), format.getImageableY());	
	g2d.setPaint(java.awt.Color.black);

	Point2D.Float pen = new Point2D.Float();

	if( data != null )
	{
		for( int i = 0; i < data.size(); i++ )
		{
			AttributedString mStyledText = new AttributedString( data.elementAt(i).toString() );

			/* Use a LineBreakMeasurer instance to break our text into
			* lines that fit the imageable area of the page.  */	
			AttributedCharacterIterator charIterator = mStyledText.getIterator();

			LineBreakMeasurer measurer = new LineBreakMeasurer(charIterator, g2d.getFontRenderContext());
			
			float wrappingWidth = (float) format.getImageableWidth();

			while (measurer.getPosition() < charIterator.getEndIndex()) 
			{
				TextLayout layout = measurer.nextLayout(wrappingWidth);
				
				pen.y += layout.getAscent();  //12
//				float dx = layout.isLeftToRight() ? 0 : (wrappingWidth - layout.getAdvance());				
//				layout.draw(g2d, pen.x + dx, pen.y );
				layout.draw(g2d, pen.x, pen.y );
				
				pen.y += layout.getDescent() + layout.getLeading(); // 3 + 1

			}
			pen.y += 1;  // add a line to it
		}
	}
	 	
	return Printable.PAGE_EXISTS;
}
/**
 * Insert the method's description here.
 * Creation date: (5/12/00 10:45:46 AM)
 * Version: <version>
 */
public void printPage( Vector lines ) 
{
com.cannontech.clientutils.CTILogger.info("		Printing Page");	
	if( lines == null )
		return;
		
	synchronized( lines )
	{
		for( int i = 0; i < lines.size(); i++ )		
			data.addElement( lines.elementAt( i ) );
	}
	
	
	PrinterJob printerJob = PrinterJob.getPrinterJob();	
	Book book = new Book();
	
	// Page Setup
	if( pageFormat != null )
	{
		PageFormat format = new PageFormat();
 	  	format = printerJob.pageDialog(format);
 	  	book.append(new TestFrame(), format );
	}
	else
		book.append(new PrintText( data, false ), new PageFormat());
	
	printerJob.setPageable(book);

	boolean doPrint = true;

	if( printerSetUp )
		doPrint = printerJob.printDialog();

	if (doPrint) 
	{
		try 
	    {
			printerJob.print();
com.cannontech.clientutils.CTILogger.info("		DONE Printing Page");
		} 
		catch (PrinterException exception) 
		{
			System.err.println("Printing error: " + exception);
		}
	}
	
   	data.removeAllElements();

}
}
