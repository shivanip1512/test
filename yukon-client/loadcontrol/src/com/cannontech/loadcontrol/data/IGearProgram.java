package com.cannontech.loadcontrol.data;

import java.util.Vector;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface IGearProgram
{
	Integer getCurrentGearNumber();
	void setCurrentGearNumber( Integer newCurrentGearNumber );

	Vector getDirectGearVector();
	void setDirectGearVector( Vector newDirectGearVector );


}
