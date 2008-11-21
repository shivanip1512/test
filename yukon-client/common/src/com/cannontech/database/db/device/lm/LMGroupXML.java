package com.cannontech.database.db.device.lm;

import java.sql.SQLException;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.database.db.DBPersistent;

public class LMGroupXML extends DBPersistent {
	
	private Integer lmGroupID = null;
	private String name = null;
	private String value = null;
	public static final String SETTER_COLUMNS[] = {"Name", "Value"};
	public static final String CONSTRAINT_COLUMNS[] = { "GroupId" };
	public static final String TABLE_NAME = "LMGroupXML";
	
	public LMGroupXML() {
		super();
	}
	
	public Integer getLmGroupID() {
		return lmGroupID;
	}
	
	public void setLmGroupID(Integer newLmGroupID) {
		lmGroupID = newLmGroupID;
	}
	
	@Override
	public void add() throws SQLException {
		Object addValues[] = { getLmGroupID(), getName(), getValue() };
		add( TABLE_NAME, addValues );
	}

	@Override
	public void delete() throws SQLException {
		delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getLmGroupID() );
	}

	@Override
	public void retrieve() throws SQLException {
		Object constraintValues[] = { getLmGroupID() };
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
		if( results.length == SETTER_COLUMNS.length ) {
			setName( (String) results[0] );
			setValue( (String) results[1] );
		} else {
			throw new IncorrectResultSizeDataAccessException(getClass().toString() + " - Incorrect Number of results retrieved", 2, results.length );
		}
	}

	@Override
	public void update() throws SQLException {
		Object setValues[] = { getName(), getValue() };
		Object constraintValues[] = { getLmGroupID() };
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	
	private String getName(){
		return name;
	}
	
	private void setName(String name){
		this.name = name;
	}
	
	private String getValue(){
		return value;
	}
	
	private void setValue(String value){
		this.value = value;
	}
	
}
