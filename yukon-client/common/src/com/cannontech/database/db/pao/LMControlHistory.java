package com.cannontech.database.db.pao;

/**
 * This type was created in VisualAge.
 */
public class LMControlHistory extends com.cannontech.database.db.DBPersistent 
{	
	private Integer lmCtrlHistID = null;
	private Integer paObjectID = null;
	private java.util.Date startDateTime = null;
	private Integer soeTag = null;
	private Integer controlDuration = null;
	private String controlType = null;
	private Integer currentDailyTime = null;
	private Integer currentMonthlyTime = null;
	private Integer currentSeasonalTime = null;
	private Integer currentAnnualTime = null;
	private String activeRestore = null;
	private Double reductionValue = null;
	private java.util.Date stopDateTime = null;
	
	public final static String SETTER_COLUMNS[] = 
	{ 
		"paObjectID", "StartDateTime", "SOETag", "ControlDuration",
		"ControlType", "CurrentDailyTime", "CurrentMonthlyTime",
		"CurrentSeasonalTime", "CurrentAnnualTime", "ActiveRestore",
		"ReductionValue", "StopDateTime"
	};

	public final static String CONSTRAINT_COLUMNS[] = { "LMCtrlHistID" };

	public final static String TABLE_NAME = "LMControlHistory";
/**
 * PointDispatch constructor comment.
 */
public LMControlHistory() 
{
	super();
}
/**
 * PointDispatch constructor comment.
 */
public LMControlHistory(Integer lmctrhstID) 
{
	super();
	setLmCtrlHistID( lmctrhstID );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	//***************************************
	//**  We should never add to this table!!!
	//**  Add is implemented for a custom web page of LMControlHistory.
	//***************************************
	Object addValues[] = { getLmCtrlHistID(), getPaObjectID(), getStartDateTime(),
					getSoeTag(), getControlDuration(), getControlType(),
					getCurrentDailyTime(), getCurrentMonthlyTime(), getCurrentSeasonalTime(), getCurrentAnnualTime(),
					getActiveRestore(), getReductionValue(), getStopDateTime()};

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getLmCtrlHistID() );
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @return java.lang.String
 */
public java.lang.String getActiveRestore() {
	return activeRestore;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getControlDuration() {
	return controlDuration;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @return java.lang.String
 */
public java.lang.String getControlType() {
	return controlType;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentAnnualTime() {
	return currentAnnualTime;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentDailyTime() {
	return currentDailyTime;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentMonthlyTime() {
	return currentMonthlyTime;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentSeasonalTime() {
	return currentSeasonalTime;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLmCtrlHistID() {
	return lmCtrlHistID;
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
 * @return java.lang.Double
 */
public java.lang.Double getReductionValue() {
	return reductionValue;
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
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:25:43 PM)
 * @return java.util.Date
 */
public java.util.Date getStartDateTime() {
	return startDateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/11/2003 1:25:43 PM)
 * @return java.util.Date
 */
public java.util.Date getStopDateTime() {
	return stopDateTime;
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasPointLMControlHistory( Integer paoID ) throws java.sql.SQLException 
{	
	return hasPointLMControlHistory( paoID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasPointLMControlHistory(Integer paoID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT * FROM " + TABLE_NAME + " WHERE PointID=" + paoID,
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
	Object constraintValues[] = { getLmCtrlHistID() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{		
		setPaObjectID( (Integer) results[0] );
		setStartDateTime( new java.util.Date( ((java.sql.Timestamp)results[1]).getTime() ) );		
		setSoeTag( (Integer) results[2] );
		setControlDuration( (Integer) results[3] );
		setControlType( (String) results[4] );
		setCurrentDailyTime( (Integer) results[5] );
		setCurrentMonthlyTime( (Integer) results[6] );
		setCurrentSeasonalTime( (Integer) results[7] );
		setCurrentAnnualTime( (Integer) results[8] );
		setActiveRestore( (String) results[9] );		
		setReductionValue( (Double) results[10] );
		setStopDateTime( new java.util.Date( ((java.sql.Timestamp) results[11]).getTime()) );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
	
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @param newActiveRestore java.lang.String
 */
public void setActiveRestore(java.lang.String newActiveRestore) {
	activeRestore = newActiveRestore;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @param newControlDuration java.lang.Integer
 */
public void setControlDuration(java.lang.Integer newControlDuration) {
	controlDuration = newControlDuration;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @param newControlType java.lang.String
 */
public void setControlType(java.lang.String newControlType) {
	controlType = newControlType;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @param newCurrentAnnualTime java.lang.Integer
 */
public void setCurrentAnnualTime(java.lang.Integer newCurrentAnnualTime) {
	currentAnnualTime = newCurrentAnnualTime;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @param newCurrentDailyTime java.lang.Integer
 */
public void setCurrentDailyTime(java.lang.Integer newCurrentDailyTime) {
	currentDailyTime = newCurrentDailyTime;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @param newCurrentMonthlyTime java.lang.Integer
 */
public void setCurrentMonthlyTime(java.lang.Integer newCurrentMonthlyTime) {
	currentMonthlyTime = newCurrentMonthlyTime;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @param newCurrentSeasonalTime java.lang.Integer
 */
public void setCurrentSeasonalTime(java.lang.Integer newCurrentSeasonalTime) {
	currentSeasonalTime = newCurrentSeasonalTime;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:21:13 PM)
 * @param newLmCtrlHistID java.lang.Integer
 */
public void setLmCtrlHistID(java.lang.Integer newLmCtrlHistID) {
	lmCtrlHistID = newLmCtrlHistID;
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
 * @param newReductionValue java.lang.Double
 */
public void setReductionValue(java.lang.Double newReductionValue) {
	reductionValue = newReductionValue;
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
 * Insert the method's description here.
 * Creation date: (9/25/2001 1:25:43 PM)
 * @param newStartDateTime java.util.Date
 */
public void setStartDateTime(java.util.Date newStartDateTime) {
	startDateTime = newStartDateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/11/2003 1:25:43 PM)
 * @param newStopDateTime java.util.Date
 */
public void setStopDateTime(java.util.Date newStopDateTime) {
	stopDateTime = newStopDateTime;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	//***************************************
	//**  We should never update this table!!!
	//**  Update is implemented for a custom web page of LMControlHistory.
	//***************************************
	Object setValues[] = {getPaObjectID(), getStartDateTime(),
					getSoeTag(), getControlDuration(), getControlType(),
					getCurrentDailyTime(), getCurrentMonthlyTime(), getCurrentSeasonalTime(), getCurrentAnnualTime(),
					getActiveRestore(), getReductionValue(), getStopDateTime() };
					
	Object constraintValues[] = { getLmCtrlHistID()};

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );

}
}
