/*
 * Created on Aug 18, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.lite;

import com.cannontech.common.util.CtiUtilities;
/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LiteLMPAOExclusion extends LiteBase
{
	
	private int masterPaoID = CtiUtilities.NONE_ID;
	private int excludedPaoID = CtiUtilities.NONE_ID;
	private int functionID = CtiUtilities.LM_SUBORDINATION_FUNC_ID.intValue();
	private String functionName = CtiUtilities.LM_SUBORDINATION_INFO;

/**
 *
 */
public LiteLMPAOExclusion( int exclusionID ) 
{
	super();
	setLiteID( exclusionID );
	setLiteType(LiteTypes.LM_PAO_EXCLUSION);	
}

/**
 * 
 */
public int getExclusionID() 
{
	return getLiteID();
}

/**
 * 
 */
public void setExclusionID(int newValue) 
{
	setLiteID(newValue);
}
/**
 * @return
 */
public int getMasterPaoID()
{
	return masterPaoID;
}

public void setMasterPaoID(int y)
{
	masterPaoID = y;
}

/**
 * @return
 */
public int getExcludedPaoID()
{
	return excludedPaoID;
}

public void setExcludedPaoID(int x)
{
	excludedPaoID = x;
}
/**
 * @return
 */
public int getFunctionID()
{
	return functionID;
}

/**
 * @param i
 */
public void setFunctionID(int i)
{
	functionID = i;
}

public String getFunctionName()
{
	return functionName;
}

/**
 * @param i
 */
public void setFunctionName(String j)
{
	functionName = j;
}


}
