package com.cannontech.database.data.lite;

import com.cannontech.common.util.CtiUtilities;

/*
 */
public class LiteLMProgScenario extends LiteBase
{
	private int programID = CtiUtilities.NONE_ID;
	private int scenarioID = CtiUtilities.NONE_ID;
	private int startOffset = 0;
	private int stopOffset = 0;
	private int startGear = 0;

/**
 * LiteState
 */
public LiteLMProgScenario( int progID ) 
{
	super();
	setLiteID( progID );
	setLiteType(LiteTypes.LMSCENARIO_PROG);	
}

/**
 * 
 */
public int getProgramID() 
{
	return getLiteID();
}

/**
 * 
 */
public void setProgramID(int newValue) 
{
	setLiteID(newValue);
}
	/**
	 * @return
	 */
	public int getStartOffset()
	{
		return startOffset;
	}

	/**
	 * @return
	 */
	public int getStartGear()
	{
		return startGear;
	}

	/**
	 * @return
	 */
	public int getStopOffset()
	{
		return stopOffset;
	}

	/**
	 * @param i
	 */
	public void setStartOffset(int i)
	{
		startOffset = i;
	}

	/**
	 * @param i
	 */
	public void setStartGear(int i)
	{
		startGear = i;
	}

	/**
	 * @param i
	 */
	public void setStopOffset(int i)
	{
		stopOffset = i;
	}

	/**
	 * @return
	 */
	public int getScenarioID()
	{
		return scenarioID;
	}

	/**
	 * @param i
	 */
	public void setScenarioID(int i)
	{
		scenarioID = i;
	}

}
