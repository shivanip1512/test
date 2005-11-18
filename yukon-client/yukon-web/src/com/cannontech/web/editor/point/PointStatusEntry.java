package com.cannontech.web.editor.point;

import javax.faces.event.ValueChangeEvent;

import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.StatusPoint;

/**
 * A point editor for Status points.
 * @author ryan
 */
public class PointStatusEntry
{
	private StatusPoint statusPoint = null;
	


	public PointStatusEntry( StatusPoint stPoint )
	{
		super();
		
		if( stPoint == null )
			throw new IllegalArgumentException("StatusPoint can not be a NULL reference");
		
		statusPoint = stPoint;
	}

	public void showReasonabilityLimit( ValueChangeEvent ev ) {
	}


	/**
	 * Determines if the current control type has control
	 * @return
	 */
	public boolean isControlAvailable() {
		return PointTypes.hasControl( getStatusPoint().getPointStatus().getControlType() );
	}


	/**
	 * The instance of the underlying base object
	 */
	private StatusPoint getStatusPoint() {
		return statusPoint;
	}

}