package com.cannontech.database.db.device.lm;

import java.util.List;
import java.util.Vector;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.RowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.spring.YukonSpringHook;

public class LMProgram extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;
	private String controlType = null;
	private Integer constraintID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private static YukonJdbcTemplate jdbcTemplate;
	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"ControlType", "ConstraintID"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "LMProgram";

public LMProgram() {
	super();
}

public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getControlType(), getConstraintID() };

	add( TABLE_NAME, addValues );
}

public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, "DeviceID", getDeviceID() );
}

public java.lang.String getControlType() {
	return controlType;
}

public Integer getDeviceID() {
	return deviceID;
}

public java.lang.Integer getConstraintID() {
	return constraintID;
}

/**
 * Checks to see if a given load management program has been assigned to an appliance category,
 * making it a STARS assigned program.
 * @param programId The LM program id to check.
 */
public static boolean isAssignedProgram(int programId) {
    SqlStatementBuilder sql = new SqlStatementBuilder();
    sql.append("SELECT COUNT(*) FROM LMProgramWebPublishing WHERE DeviceId").eq(programId);
    return getYukonJdbcTemplate().queryForInt(sql) > 0;
}

/**
 * This method returns all the LMProgram ID's that are not assigned to a Control Area.
 */
public static Vector getUnassignedPrograms() {
    Vector returnVector = new Vector<>();
    SqlStatementBuilder sql = new SqlStatementBuilder();
    sql.append("SELECT DeviceID FROM " + TABLE_NAME + " WHERE "
            + " DeviceId NOT IN (SELECT LmProgramDeviceId FROM " + LMControlAreaProgram.TABLE_NAME
            + ") ORDER BY DeviceId");
    List<Integer> unassignedProgramIds = getYukonJdbcTemplate().query(sql, RowMapper.INTEGER);
    returnVector.addAll(unassignedProgramIds);
    return returnVector;
}

/**
 * Obtains a reference to YukonJdbcTemplate for use in unassigned program queries.
 */
private static YukonJdbcTemplate getYukonJdbcTemplate() {
    if (jdbcTemplate == null) {
        jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);
    }
    return jdbcTemplate;
}

public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getDeviceID() };	
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setControlType( (String) results[0] );
		setConstraintID( (Integer) results[1] );

	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}

public void setControlType(java.lang.String newControlType) {
	controlType = newControlType;
}

public void setDeviceID(Integer newValue) {
	this.deviceID = newValue;
}

public void setConstraintID(java.lang.Integer newID) {
	constraintID = newID;
}

public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getControlType(), getConstraintID() };

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}

}
