/*
 * Created on Feb 29, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.device.lm;

import com.cannontech.database.SqlUtils;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMControlScenarioProgram extends com.cannontech.database.db.NestedDBPersistent
{
	
	private Integer scenarioID;
	private Integer programID;     
	private Integer startOffset = new Integer(0);
	private Integer stopOffset = new Integer(0);
	private Integer startGear = new Integer(0);
	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"SCENARIOID", "PROGRAMID", "STARTOFFSET", "STOPOFFSET", "STARTGEAR"
		
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
			getScenarioID(), getProgramID(), getStartOffset(), 
			getStopOffset(), getStartGear()
		
		};

		add( TABLE_NAME, addValues );
	}


	public void delete() throws java.sql.SQLException
	{
		delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getScenarioID());
	}



	public Integer getScenarioID() {
		return scenarioID;
	}

	public Integer getProgramID() {
		return programID;
	}

	public Integer getStartOffset() {
		return startOffset;
	}

	public Integer getStopOffset() {
		return stopOffset;
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
				setProgramID((Integer) results[1] );
				setStartOffset( (Integer) results[2] );
				setStopOffset( (Integer) results[3] );
				setStartGear( (Integer) results[4] );
							
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

	public void setStartOffset(Integer offset) {
		startOffset = offset;
	}

	public void setStopOffset(Integer stOff) {
		stopOffset = stOff;
	}

	public void setStartGear(Integer gear) {
		startGear = gear;
	}

	

	public void update() 
	{
		Object setValues[] =
		{ 
			getScenarioID(), getProgramID(), getStartOffset(), 
			getStopOffset(), getStartGear()		
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
	
	public static final java.util.Vector getAllProgramsForAScenario( Integer scenarioID, java.sql.Connection conn)
	{
			java.util.Vector progList = new java.util.Vector();
			java.sql.PreparedStatement pstmt = null;
			java.sql.ResultSet rset = null;

			//get all the programs that are associated with the passed ScenarioID
			String sql = "select scenarioID, programID,"
						+ " STARTOFFSET, STOPOFFSET, STARTGEAR" 
						+ " from " + TABLE_NAME +
					" where scenarioid=? order by programID";
			try
			{
				if (conn == null)
					throw new IllegalArgumentException("Received a (null) database connection");

				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setInt(1, scenarioID.intValue());

				rset = pstmt.executeQuery();

				while (rset.next())
				{
					LMControlScenarioProgram prog = new LMControlScenarioProgram();	
					prog.setScenarioID( scenarioID );
					prog.setProgramID( new Integer(rset.getInt(2)) );
					prog.setStartOffset( new Integer(rset.getInt(3)) );
					prog.setStopOffset( new Integer(rset.getInt(4)) );
					prog.setStartGear( new Integer(rset.getInt(5)) );
					prog.setDbConnection(conn);

					progList.add(prog);
				}

			}
			catch (java.sql.SQLException e)
			{
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			}
			finally
			{
				SqlUtils.close(rset, pstmt);
			}

			return progList;
		}

}
