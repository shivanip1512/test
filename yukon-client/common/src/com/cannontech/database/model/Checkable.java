/*
 * Created on Oct 6, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.model;

import java.util.Vector;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface Checkable
{
	
	public Vector getCheckedNodes();
	/**
	 * @param vector
	 */
	public void setCheckedNodes(Vector vector);
}
