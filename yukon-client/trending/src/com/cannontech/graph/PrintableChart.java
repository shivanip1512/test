package com.cannontech.graph;

/**
 * PrintableChart addds printing capability to the base JCChart.
 * Creation date: (10/11/00 6:03:19 PM)
 * @author: Aaron Lauinger
 */
public class PrintableChart extends com.klg.jclass.chart.JCChart implements java.awt.print.Printable {

/**
 * PrintableChart constructor comment.
 */
public PrintableChart() {
	super();
}
/**
 * PrintableChart constructor comment.
 * @param arg1 int
 */
public PrintableChart(int arg1) {
	super(arg1);
}
	public int print(java.awt.Graphics g, java.awt.print.PageFormat pf, int pageIndex) throws java.awt.print.PrinterException
   {
	  if (pageIndex != 0)
		 return NO_SUCH_PAGE;

	  if (g instanceof java.awt.Graphics2D)
	  {
		 java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
		 g2.translate(pf.getImageableX(), pf.getImageableY());

		 g2.scale(com.cannontech.graph.GraphClient.scalePercent,com.cannontech.graph.GraphClient.scalePercent);
 			 
 		 paint(g2);
		 
		 return PAGE_EXISTS;
	  }
	  else
		 return NO_SUCH_PAGE;
   }                                                               
}
