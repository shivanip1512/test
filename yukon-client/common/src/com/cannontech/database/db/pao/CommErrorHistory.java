package com.cannontech.database.db.pao;

/**
 * This type was created in VisualAge.
 */
public class CommErrorHistory extends com.cannontech.database.db.DBPersistent 
{	
	private Integer commErrorID = null;
	private Integer paObjectID = null;
	private java.util.Date dateTime = null;
	private Integer soeTag = null;
	private Integer errorType = null;
	private Integer errorNumber = null;
	private String command = null;
	private String outMessage = null;
	private String inMessage = null;

	
	public final static String SETTER_COLUMNS[] = 
	{ 
		"paObjectID", "DateTime", "SOETag", "ErrorType",
		"ErrorNumber", "Command", "OutMessage", "InMessage"
	};

	public final static String CONSTRAINT_COLUMNS[] = { "CommErrorID" };

	public final static String TABLE_NAME = "CommErrorHistory";
/**
 * PointDispatch constructor comment.
 */
public CommErrorHistory() 
{
	super();
}
/**
 * PointDispatch constructor comment.
 */
public CommErrorHistory(Integer commHstID) 
{
	super();
	setCommErrorID( commHstID );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	//***************************************************
	//**  We do not add to this table for now!!!
	//***************************************************
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getCommErrorID() );
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:35:59 PM)
 * @return java.lang.String
 */
public java.lang.String getCommand() {
	return command;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:35:59 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCommErrorID() {
	return commErrorID;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:35:59 PM)
 * @return java.util.Date
 */
public java.util.Date getDateTime() {
	return dateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:35:59 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getErrorNumber() {
	return errorNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:35:59 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getErrorType() {
	return errorType;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:35:59 PM)
 * @return java.lang.String
 */
public java.lang.String getInMessage() {
	return inMessage;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:35:59 PM)
 * @return java.lang.String
 */
public java.lang.String getOutMessage() {
	return outMessage;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPaObjectID() {
	return paObjectID;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSoeTag() {
	return soeTag;
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasCommErrorHistory( Integer commErrID ) throws java.sql.SQLException 
{	
	return hasCommErrorHistory( commErrID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasCommErrorHistory(Integer commErrID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT * FROM " + TABLE_NAME + " WHERE PointID=" + commErrID,
													databaseAlias );

	try
	{
		stmt.execute();
		return (stmt.getRowCount() > 0 );
	}
	catch( Exception e )
	{
		return false;
	}
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getCommErrorID() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{		
		setPaObjectID( (Integer) results[0] );
		setDateTime( new java.util.Date( ((java.sql.Timestamp)results[1]).getTime() ) );
		setSoeTag( (Integer) results[2] );
		setErrorType( (Integer) results[3] );
		setErrorNumber( (Integer) results[4] );		
		setCommand( (String) results[5] );
		setOutMessage( (String) results[6] );
		setInMessage( (String) results[7] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
	
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:35:59 PM)
 * @param newCommand java.lang.String
 */
public void setCommand(java.lang.String newCommand) {
	command = newCommand;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:35:59 PM)
 * @param newCommErrorID java.lang.Integer
 */
public void setCommErrorID(java.lang.Integer newCommErrorID) {
	commErrorID = newCommErrorID;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:35:59 PM)
 * @param newDateTime java.util.Date
 */
public void setDateTime(java.util.Date newDateTime) {
	dateTime = newDateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:35:59 PM)
 * @param newErrorNumber java.lang.Integer
 */
public void setErrorNumber(java.lang.Integer newErrorNumber) {
	errorNumber = newErrorNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:35:59 PM)
 * @param newErrorType java.lang.Integer
 */
public void setErrorType(java.lang.Integer newErrorType) {
	errorType = newErrorType;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:35:59 PM)
 * @param newInMessage java.lang.String
 */
public void setInMessage(java.lang.String newInMessage) {
	inMessage = newInMessage;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:35:59 PM)
 * @param newOutMessage java.lang.String
 */
public void setOutMessage(java.lang.String newOutMessage) {
	outMessage = newOutMessage;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @param newPaObjectID java.lang.Integer
 */
public void setPaObjectID(java.lang.Integer newPaObjectID) {
	paObjectID = newPaObjectID;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @param newSoeTag java.lang.Integer
 */
public void setSoeTag(java.lang.Integer newSoeTag) {
	soeTag = newSoeTag;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	//***************************************************
	//**  We do not update this table for now!!!
	//***************************************************
	
}
}
