package com.cannontech.esub.util;

import com.cannontech.common.cache.PointChangeCache;
import com.cannontech.esub.editor.Drawing;
import com.cannontech.esub.editor.element.DynamicText;
import com.cannontech.message.dispatch.message.PointData;

import com.loox.jloox.LxComponent;

/**
 * A runnable which updates its drawing on each call
 * to run.
 * 
 * Designed to be used from say a timer
 * @author alauinger
 */
public class DrawingUpdater implements Runnable {

	// The drawing to update
	private Drawing drawing;
	
	/**
	 * Constructor for DrawingUpdater.
	 */
	public DrawingUpdater() {
		super();
	}

	/**
	 * Method DrawingUpdater.
	 * @param d - Drawing to update
	 */
	public DrawingUpdater(Drawing d) {
		super();
		setDrawing(d);
	} 
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {		
		//assert( drawing != null );
		
		PointChangeCache pcc = PointChangeCache.getPointChangeCache();
		
		LxComponent[] comp = drawing.getLxGraph().getComponents();
		if( comp != null ) {
			
			for( int i = 0; i < comp.length; i++ ) {
				
				if( comp[i] instanceof DynamicText ) {
					DynamicText dt = (DynamicText) comp[i];
					
					PointData pData = pcc.getValue(dt.getPointID());
					
					if( pData != null ) {
						dt.setText( Double.toString(pData.getValue()));
					}
				}
			}
		}
		
		
		
		
	}

	/**
	 * Returns the drawing.
	 * @return Drawing
	 */
	public Drawing getDrawing() {
		return drawing;
	}

	/**
	 * Sets the drawing.
	 * @param drawing The drawing to set
	 */
	public void setDrawing(Drawing drawing) {
		this.drawing = drawing;
	}

}
