/*
 * Created on Feb 29, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.device.lm;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMControlScenarioProgram extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	
	private Integer scenarioID;
	private Integer programID;     
	private Integer startDelay;    
	private Integer duration;    
	private Integer startGear;     
	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"SCENARIOID", "PROGRAMID", "STARTDELAY", "DURATION", "STARTGEAR"
		
	};

	public static final String CONSTRAINT_COLUMNS[] = { "scenarioID", "programID" };

	public static final String TABLE_NAME = "LMControlScenarioProgram";

	/**
	 * LMControlScenarioProgram constructor comment.
	 */
	public LMControlScenarioProgram() {
		super();
	}
	/**
	 * LMControlScenarioProgram constructor comment.
	 */
	public LMControlScenarioProgram(Integer sID) {
		super();
		scenarioID = sID;
		
	}

	public LMControlScenarioProgram(Integer sID, Integer pID) {
		super();
		scenarioID = sID;
		programID = pID;
	
	}

	public void add() throws java.sql.SQLException
	{
		Object addValues[] = 
		{ 
			getScenarioID(), getProgramID(), getStartDelay(), 
			getDuration(), getStartGear()
		
		};

		add( TABLE_NAME, addValues );
	}


	public void delete() throws java.sql.SQLException
	{
		delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getScenarioID());
	}



	public static synchronized Integer getNextScenarioID( java.sql.Connection conn )
		{
			if( conn == null )
				throw new IllegalStateException("Database connection should not be null.");
	
			java.sql.Statement stmt = null;
			java.sql.ResultSet rset = null;
		
			try 
			{		
				stmt = conn.createStatement();
				 rset = stmt.executeQuery( "SELECT Max(ScenarioID)+1 FROM " + TABLE_NAME );	
				
				 //get the first returned result
				 rset.next();
				return new Integer( rset.getInt(1) );
			}
			catch (java.sql.SQLException e) 
			{
				e.printStackTrace();
			}
			finally 
			{
				try 
				{
					if ( stmt != null) stmt.close();
				}
				catch (java.sql.SQLException e2) 
				{
					e2.printStackTrace();
				}
			}
		
			//strange, should not get here
			return new Integer(com.cannontech.common.util.CtiUtilities.NONE_ID);
		}


	public com.cannontech.message.dispatch.message.DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
	{
		com.cannontech.message.dispatch.message.DBChangeMsg[] msgs =
		{
			new com.cannontech.message.dispatch.message.DBChangeMsg(
				getScenarioID().intValue(),
				com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_LMSCENARIO_DB,
				com.cannontech.message.dispatch.message.DBChangeMsg.CAT_LMSCENARIO,
				com.cannontech.message.dispatch.message.DBChangeMsg.CAT_LMSCENARIO,
				typeOfChange)
		};


		return msgs;
	}
	
		
	public Integer getScenarioID() {
		return scenarioID;
	}

	public Integer getProgramID() {
		return programID;
	}

	public Integer getStartDelay() {
		return startDelay;
	}

	public Integer getDuration() {
		return duration;
	}

	public Integer getStartGear() {
		return startGear;
	}

	public void retrieve() 
	{
		Integer constraintValues[] = { getScenarioID(), getProgramID() };	
	
		try
		{
			Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
			if( results.length == SETTER_COLUMNS.length )
			{
				setStartDelay( (Integer) results[1] );
				setDuration( (Integer) results[2] );
				setStartGear( (Integer) results[3] );
							
			}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
		}
		catch (Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}


	public void setScenarioID(Integer newScenarioID) {
		scenarioID = newScenarioID;
	}

	public void setProgramID(Integer newProgramID) {
		programID = newProgramID;
	}

	public void setStartDelay(Integer delay) {
		startDelay = delay;
	}

	public void setDuration(Integer dur) {
		duration = dur;
	}

	public void setStartGear(Integer gear) {
		startGear = gear;
	}

	

	public void update() 
	{
		Object setValues[] =
		{ 
			getScenarioID(), getProgramID(), getStartDelay(), 
			getDuration(), getStartGear()		
		};
	
		Object constraintValues[] = { getScenarioID(), getProgramID() };
	
		try
		{
			update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
		}
		catch (Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}
}
