package com.cannontech.tdc.test;

import java.awt.*;
import java.awt.print.*;
import javax.swing.*;

public class JComponentVista extends Vista implements Printable {

	private static final boolean SYMMETRIC_SCALING = true;
	private static final boolean ASYMMETRIC_SCALING = false;
	private double mScaleX;
	private double mScaleY;

	/**
	 * The Swing component to print.
	 */
	private JComponent mComponent;

	/**
	 * Create a Pageable that can print a
	 * Swing JComponent over multiple pages.
	 *
	 * @param c The swing JComponent to be printed.
	 *
	 * @param format The size of the pages over which
	 * the componenent will be printed.
	 */
	public JComponentVista(JComponent c, PageFormat format) 
	{
		setPageFormat(format);
		setPrintable(this);
		setComponent(c);

		/* Tell the Vista we subclassed the size of the canvas.
		 */
		Rectangle componentBounds = c.getBounds(null);
		setSize( componentBounds.width, componentBounds.height );
		
		setScale(1, 1);

	}
public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException 
{

	try 
	{
		Printable formatPainter = (Printable) pageFormat;
		formatPainter.print(graphics, pageFormat, pageIndex);
	} 
	catch (ClassCastException exception) {}
			
	Graphics2D g2 = (Graphics2D) graphics;

	/* Move the origin from the corner of the Paper to the corner
	* of the imageable area.*/			
	g2.translate( pageFormat.getImageableX(), ( pageFormat.getImageableY() + pageFormat.getImageableHeight() ) );

	Rectangle componentBounds = mComponent.getBounds(null);
	g2.translate( -componentBounds.x, -componentBounds.y );

	g2.scale(mScaleX, mScaleY);
	boolean wasBuffered = mComponent.isDoubleBuffered();
	mComponent.paint(g2);
	mComponent.setDoubleBuffered(wasBuffered);
			
	return PAGE_EXISTS;
}
	protected void setComponent(JComponent c) {

		mComponent = c;

	}
	protected void setScale(double scaleX, double scaleY) {

		mScaleX = scaleX;
		mScaleY = scaleY;

	}
}
