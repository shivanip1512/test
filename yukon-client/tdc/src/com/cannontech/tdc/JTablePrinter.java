package com.cannontech.tdc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JTable;

import com.cannontech.clientutils.CTILogger;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class JTablePrinter implements Printable {

	private JTable tableView = null;
	private String title = null;

 
	/**
	 * Constructor for JTablePrinter.
	 */
	public JTablePrinter( JTable printedTable_ ) {		
		super();
		tableView = printedTable_;
	}

	/**
	 * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
	 */
	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
		throws PrinterException 
	{
  		Graphics2D g2 = (Graphics2D) g;
     	g2.setColor(Color.black);

     	int fontHeight = g2.getFontMetrics().getHeight();
     	int fontDesent = g2.getFontMetrics().getDescent();

     	//leave room for page number
     	double pageHeight = pageFormat.getImageableHeight() - fontHeight;
     	double pageWidth = pageFormat.getImageableWidth();
     	
     	double tableWidth = (double)tableView.getColumnModel().getTotalColumnWidth();

     	double scale = 1; 

     	if (tableWidth >= pageWidth)
			scale =  pageWidth / tableWidth;


     	double headerHeightOnPage=
                 tableView.getTableHeader().getHeight()*scale;

     	double tableWidthOnPage = tableWidth*scale;

     	double oneRowHeight=
     			( tableView.getRowHeight() +
              tableView.getRowMargin() )*scale;
              
     	int numRowsOnAPage=
              (int)((pageHeight-headerHeightOnPage)/
                                  oneRowHeight);
     	double pageHeightForTable=oneRowHeight*
     	                            numRowsOnAPage;
     	int totalNumPages= 
     	      (int)Math.ceil((
                (double)tableView.getRowCount())/
                                    numRowsOnAPage);

     	if( pageIndex >= totalNumPages )
			return NO_SUCH_PAGE;

     	g2.translate( pageFormat.getImageableX(), 
                    pageFormat.getImageableY() );


		//top center
		if( getTitle() != null )	
	     	g2.drawString("Top",
	     	    (int)pageWidth/2-35,
	     	    0 );


		//bottom center
     	g2.drawString("Page: "+(pageIndex+1),
     	    (int)pageWidth/2-35, (int)(pageHeight
     	    +fontHeight-fontDesent));

     	g2.translate(0f,headerHeightOnPage);
     	g2.translate(0f,-pageIndex*pageHeightForTable);

     	//If this piece of the table is smaller 
     	//than the size available,
     	//clip to the appropriate bounds.
     	if (pageIndex + 1 == totalNumPages) {
           int lastRowPrinted = 
                 numRowsOnAPage * pageIndex;
           int numRowsLeft = 
                 tableView.getRowCount() - lastRowPrinted;

           g2.setClip( 0, 
             (int)(pageHeightForTable * pageIndex),
             (int) Math.ceil(tableWidthOnPage),
             (int) Math.ceil(oneRowHeight * 
                               numRowsLeft));
     	}
     	//else clip to the entire area available.
     	else{    
             g2.setClip(0, 
             (int)(pageHeightForTable*pageIndex), 
             (int) Math.ceil(tableWidthOnPage),
             (int) Math.ceil(pageHeightForTable));        
     	}

     	g2.scale(scale,scale);

     	
     	tableView.paint(g2);
     	
     	g2.scale(1/scale,1/scale);
     	g2.translate(0f,pageIndex*pageHeightForTable);
     	g2.translate(0f, -headerHeightOnPage);
     	g2.setClip(0, 0,
     	  (int) Math.ceil(tableWidthOnPage), 
          (int)Math.ceil(headerHeightOnPage));
     	g2.scale(scale,scale);
     	tableView.getTableHeader().paint(g2);
     	//paint header at top

     	return Printable.PAGE_EXISTS;
	}

	public void printIt() {
		
		PrinterJob pj = PrinterJob.getPrinterJob();
		pj.setPrintable( this );
		pj.printDialog();
		
      try
      { 
      	pj.print();
		}
		catch( Exception ex ) 
		{
			CTILogger.error( "Unable to print", ex );
		}
		
	}
	

	public void setTitle( String title_ ) {
		title = title_;
	}

	public String getTitle() {
		return title;
	}
	
}
