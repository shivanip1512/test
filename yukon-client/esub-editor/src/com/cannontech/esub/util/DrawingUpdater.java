package com.cannontech.esub.util;

import java.text.DecimalFormat;
import java.util.TimerTask;

import com.cannontech.common.cache.PointChangeCache;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.StateFuncs;
import com.cannontech.database.cache.functions.UnitMeasureFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.esub.editor.Drawing;
import com.cannontech.esub.editor.element.DynamicGraphElement;
import com.cannontech.esub.editor.element.DynamicText;
import com.cannontech.esub.editor.element.StateImage;
import com.cannontech.message.dispatch.message.PointData;
import com.loox.jloox.LxComponent;
import com.loox.jloox.LxView;

/**
 * A runnable which updates its drawing on each call
 * to run.
 * 
 * The run method is getting a little ungainly
 * 
 * Designed to be used from say a timer
 * @author alauinger
 */
public class DrawingUpdater extends TimerTask {

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

		if (drawing == null) {
			return;
		} 	

		synchronized (drawing) {			
			try {
				// keep track if we changed anything
				boolean change = false; 
								
				PointChangeCache pcc = PointChangeCache.getPointChangeCache();

				LxComponent[] comp = drawing.getLxGraph().getComponents();
				if (comp != null) {

					for (int i = 0; i < comp.length; i++) {

						if (comp[i] instanceof DynamicText) {
							DynamicText dt = (DynamicText) comp[i];
							String text = UpdateUtil.getDynamicTextString(dt.getPointID(), dt.getDisplayAttribs());
	
							// only update if there is something to update
							if( !text.equals(dt.getText()) ) {
								if( !text.equals(dt.getText()) ) {
									dt.setText(text);
								}
							}
						}

						if (comp[i] instanceof StateImage) {							
							StateImage si = (StateImage) comp[i];							
							LitePoint lp = si.getPoint();
			
							if( lp != null ) {
								PointData pData = pcc.getValue(lp.getPointID());
								if( pData != null ) {
									LiteState ls = StateFuncs.getLiteState(lp.getStateGroupID(), (int) pData.getValue());
									if( ls != null ) {
										si.setCurrentState(ls);
										si.updateImage();
										change = true;
									}	
	
								}
							}

						}
						
						if( comp[i] instanceof DynamicGraphElement ) {
							DynamicGraphElement dge = (DynamicGraphElement) comp[i];
							if( dge.shouldUpdate() ) {
								System.out.println("found graph that needs updating");
								dge.updateGraph();
								change = true;
							}							
						}
					}

					//Only force an update if there is a view present
					//and there has been a change
					LxView view = drawing.getLxView();
					if (change && view != null) {
						view.repaint();
					}
				}

			} catch (Throwable t) {
				t.printStackTrace();
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
	public synchronized void setDrawing(Drawing drawing) {
		this.drawing = drawing;
	}

}
