package com.cannontech.database.data.baseline;

/**
 * Insert the type's description here.
 * Creation date: (7/24/2003 2:05:21 PM)
 * @author: The Intern
 */
public class Baseline extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	private com.cannontech.database.db.baseline.Baseline basil = null;
	public final static Integer IDForDefaultBaseline = new Integer(1);
	

/**
 * Baseline constructor comment.
 */
public Baseline() {
	super();
}
/**
 * Baseline constructor comment.
 */
public Baseline(Integer id)
{
	super();

	setBaselineID(id);
}
/**
 * Baseline constructor comment.
 */
public Baseline(Integer id, String name)
{
	super();

	setBaselineID(id);
	setBaselineName(name);
}

public Baseline(Integer id, String name, Integer usedDays, Integer windowPercent, Integer daysCalc, String weekdaysExcluded, Integer usedHolidays )
{
	super();

	setBaselineID(id);
	setBaselineName(name);
	setDaysUsed(usedDays);
	setPercentWindow(windowPercent);
	setCalcDays(daysCalc);
	setExcludedWeekdays(weekdaysExcluded);
	setHolidaysUsed(usedHolidays);
}

public void add() throws java.sql.SQLException 
{
	if(getBaselineID() == null)
		setBaselineID(com.cannontech.database.db.baseline.Baseline.getNextBaselineID(getDbConnection()));
	getBaseline().add();
		
}


public void delete() throws java.sql.SQLException 
{
	//com.cannontech.database.db.baseline.Baseline.deleteAllBaselines(getBaseline().getBaselineID(), getDbConnection());
	
	getBaseline().delete();	
}


public com.cannontech.message.dispatch.message.DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
{
	com.cannontech.message.dispatch.message.DBChangeMsg[] msgs =
	{
		new com.cannontech.message.dispatch.message.DBChangeMsg(
					getBaselineID().intValue(),
					com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_BASELINE_DB,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_BASELINE,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_BASELINE,
					typeOfChange)
	};


	return msgs;
}

public com.cannontech.database.db.baseline.Baseline getBaseline()
{
	if (basil == null)
		basil = new com.cannontech.database.db.baseline.Baseline();

	return basil;
}


public Integer getBaselineID()
{
	return getBaseline().getBaselineID();
}


public String getBaselineName()
{
	return getBaseline().getBaselineName();
}

public Integer getDaysUsed()
{
	return getBaseline().getDaysUsed();
}

public Integer getPercentWindow()
{
	return getBaseline().getPercentWindow();
}

public Integer getCalcDays()
{
	return getBaseline().getCalcDays();
}

public String getExcludedWeekdays()
{
	return getBaseline().getExcludedWeekdays();
}

public Integer getHolidaysUsed()
{
	return getBaseline().getHolidaysUsed();
}

public void retrieve() throws java.sql.SQLException 
{
	
	getBaseline().retrieve();

}

public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getBaseline().setDbConnection(conn);

}


public void setBaselineID( Integer newID )
{
	getBaseline().setBaselineID( newID );

}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2003 12:21:32 PM)
 * @return com.cannontech.database.data.holiday.Baseline
 */
public void setBaselineName( String newName )
{
	getBaseline().setBaselineName( newName );	
}

public void setDaysUsed( Integer usedDays)
{
	getBaseline().setDaysUsed(usedDays);
}

public void setPercentWindow(Integer windowPercent)
{
	getBaseline().setPercentWindow(windowPercent);
}

public void setCalcDays(Integer daysCalc)
{
	getBaseline().setCalcDays(daysCalc);
}

public void setExcludedWeekdays(String weekdaysExcluded)
{
	getBaseline().setExcludedWeekdays(weekdaysExcluded);
}

public void setHolidaysUsed(Integer usedHolidays)
{
	getBaseline().setHolidaysUsed(usedHolidays);
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2003 10:36:31 AM)
 */
public String toString()
{
	return getBaselineName();
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2003 10:36:31 AM)
 */
public void update() throws java.sql.SQLException
{
	getBaseline().update();
	
}

}
