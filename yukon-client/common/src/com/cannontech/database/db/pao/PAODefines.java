/*
 * Created on May 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.pao;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface PAODefines
{
	// -------------- PAO Statistics -----------------------------
	public static final char STAT_HOURLY  = 'H';
	public static final char STAT_DAILY   = 'D';
	public static final char STAT_MONTHLY = 'M';



	// -------------- PAO Exclusions -----------------------------
	public static final String EXCL_REQ_OPTIMAL		= "Optimal";  //id 0 in C++
	public static final String EXCL_REQ_MAINTAIN		= "Maintain Order"; //id 1 in C++
	public static final String EXCL_REQ_PRIORITY		= "Priority"; //id 2 in C++

}