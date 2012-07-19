package com.cannontech.web.editor.point;

import org.apache.commons.lang.Validate;

import com.cannontech.database.db.point.PointControl;

/**
 * A point editor for Status points.
 * @author ryan
 */
public class PointControlEntry
{
	private PointControl pointControl = null;

	public PointControlEntry( PointControl control )
	{
		Validate.notNull(control, "PointControl can not be a NULL reference");
		
		pointControl = control;
	}

	public boolean isControlAvailable() {
		return getPointControl().hasControl();
	}

	/**
	 * The instance of the underlying base object
	 */
	private PointControl getPointControl() {
		return pointControl;
	}

}