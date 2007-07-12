package com.cannontech.database.db.loadcontrol;

import com.cannontech.database.SqlUtils;

/**
 * Insert the type's description here.
 * Creation date: (4/27/00 10:06:46 AM)
 * @author: 
 */
public class Program extends com.cannontech.database.db.DBPersistent {
	private Integer programID;
	private java.lang.String name;
	private java.lang.String state;
	private java.util.Date startTimeStamp;
	private java.util.Date stopTimeStamp;
	private Integer totalControlTime;
/**
 * Program constructor comment.
 */
public Program() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	throw new Error("Add not supported, sorry :(");
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	throw new Error("delete not supported, sorry :(");
}
/**
 * Insert the method's description here.
 * Creation date: (4/28/00 12:43:39 PM)
 * @return com.cannontech.database.db.loadcontrol.Program[]
 * @param userID java.lang.Long
 * @param databaseAlias java.lang.String
 */
public static Program[] getAllPrograms(Long userID, String databaseAlias) {
	String sqlString = "SELECT Program.ProgramID,Program.Name,Program.State,Program.StartTimeStamp,Program.StopTimeStamp,Program.TotalControlTime FROM Program,UserProgram WHERE Program.ProgramID=UserProgram.ProgramID AND UserProgram.UserID= " + userID.toString();

	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;

	java.util.LinkedList results = new java.util.LinkedList();
		
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);
		stmt = conn.createStatement();
	
		rset = stmt.executeQuery(sqlString);
		
		while( rset.next() )
		{			
			Program program = new Program();
			
			program.setProgramID( new Integer(rset.getInt(1)) );
			program.setName( rset.getString(2) );
			program.setState( rset.getString(3) );
			program.setStartTimeStamp( rset.getDate(4) );
			program.setStopTimeStamp( rset.getDate(5) );
			program.setTotalControlTime( new Integer( rset.getInt(6 ) ));

			results.add(program);
		com.cannontech.clientutils.CTILogger.info("got one->" + program );
		}
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, stmt, conn );
	}
			

	Program[] returnVal = new Program[results.size()];
	results.toArray(returnVal);
com.cannontech.clientutils.CTILogger.info("returning " + returnVal.length + " programs");	
	return returnVal;
}
/**
 * Insert the method's description here.
 * Creation date: (4/27/00 1:15:26 PM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (4/27/00 1:15:13 PM)
 * @return int
 */
public Integer getProgramID() {
	return programID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/27/00 1:15:50 PM)
 * @return java.util.Date
 */
public java.util.Date getStartTimeStamp() {
	return startTimeStamp;
}
/**
 * Insert the method's description here.
 * Creation date: (4/27/00 1:15:38 PM)
 * @return java.lang.String
 */
public java.lang.String getState() {
	return state;
}
/**
 * Insert the method's description here.
 * Creation date: (4/27/00 1:16:02 PM)
 * @return java.util.Date
 */
public java.util.Date getStopTimeStamp() {
	return stopTimeStamp;
}
/**
 * Insert the method's description here.
 * Creation date: (4/27/00 1:16:19 PM)
 * @return int
 */
public Integer getTotalControlTime() {
	return totalControlTime;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	String selectColumns[] = { "Name", "State", "StartTimeStamp", "StopTimeStamp", "TotalControlTime" };
	String constraintColumns[] = { "ProgramID" };
	Object constraintValues[] = { getProgramID() };
	
	Object results[] = retrieve( selectColumns, "Program", constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setName( (String) results[0] );
		setState( (String) results[1] );

		Object ts = results[2];
		if( ts != null )
			setStartTimeStamp( new java.util.Date( ((java.sql.Timestamp) ts).getTime()) );

		ts = results[3];
		if( ts != null )
			setStopTimeStamp( new java.util.Date( ((java.sql.Timestamp) ts).getTime()) );
			
		setTotalControlTime( (Integer) results[4] );				
	}

	com.cannontech.clientutils.CTILogger.info("retrieved->" + this );
}
/**
 * Insert the method's description here.
 * Creation date: (4/27/00 1:15:26 PM)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (4/27/00 1:15:13 PM)
 * @param newProgramID int
 */
public void setProgramID(Integer newProgramID) {
	programID = newProgramID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/27/00 1:15:50 PM)
 * @param newStartTimeStamp java.util.Date
 */
public void setStartTimeStamp(java.util.Date newStartTimeStamp) {
	startTimeStamp = newStartTimeStamp;
}
/**
 * Insert the method's description here.
 * Creation date: (4/27/00 1:15:38 PM)
 * @param newState java.lang.String
 */
public void setState(java.lang.String newState) {
	state = newState;
}
/**
 * Insert the method's description here.
 * Creation date: (4/27/00 1:16:02 PM)
 * @param newStopTimeStamp java.util.Date
 */
public void setStopTimeStamp(java.util.Date newStopTimeStamp) {
	stopTimeStamp = newStopTimeStamp;
}
/**
 * Insert the method's description here.
 * Creation date: (4/27/00 1:16:19 PM)
 * @param newTotalControlTime int
 */
public void setTotalControlTime(Integer newTotalControlTime) {
	totalControlTime = newTotalControlTime;
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/00 1:54:36 PM)
 * @return java.lang.String
 */
public String toString() {
	
	return  "[ com.cannontech.database.db.loadcontrol.Program - Program ID:    " + getProgramID() +	
			"  Program Name:  " + getName() +
			"  State:	     " + getState() +
			"  StartTimeStamp:" + getStartTimeStamp() +
			"  StopTimeStamp: " + getStopTimeStamp() +
			"  TotalControlTime:  " + getTotalControlTime() + " ]";
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	
	String setColumns[] = { "Name", "State", "StartTimeStamp", "StopTimeStamp", "TotalControlTime" };
	Object setValues[] = { getName(), getState(), getStartTimeStamp(), getStopTimeStamp(), getTotalControlTime() };

	String constraintColumns[] = { "ProgramID" };
	Object constraintValues[] = { getProgramID() };
com.cannontech.clientutils.CTILogger.info( "update->" + this );	
	update( "Program", setColumns, setValues, constraintColumns, constraintValues );	
}
}
