package com.cannontech.esub.util;

import java.text.DecimalFormat;
import java.util.TimerTask;

import com.cannontech.common.cache.PointChangeCache;
import com.cannontech.esub.editor.Drawing;
import com.cannontech.esub.editor.element.DynamicText;
import com.cannontech.esub.editor.element.StateImage;
import com.cannontech.message.dispatch.message.PointData;

import com.loox.jloox.LxComponent;
import com.loox.jloox.LxView;

/**
 * A runnable which updates its drawing on each call
 * to run.
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
				
				PointChangeCache pcc = PointChangeCache.getPointChangeCache();

				LxComponent[] comp = drawing.getLxGraph().getComponents();
				if (comp != null) {

					for (int i = 0; i < comp.length; i++) {

						if (comp[i] instanceof DynamicText) {
							DynamicText dt = (DynamicText) comp[i];

							PointData pData = pcc.getValue(dt.getPointID());

							if (pData != null) {
								DecimalFormat f = new DecimalFormat();
								f.setMaximumFractionDigits(2);

								dt.setText(f.format(pData.getValue()));
							}
						}

						if (comp[i] instanceof StateImage) {
							StateImage si = (StateImage) comp[i];
							int pointID = si.getPointID();

							if (pointID > 0) {
								PointData pData = pcc.getValue(si.getPointID());
								String state =
									pcc.getState(
										si.getPointID(),
										pData.getValue());

								si.setState(state);
							}
						}
					}

					LxView view = drawing.getLxView();
					if (view != null) {
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
