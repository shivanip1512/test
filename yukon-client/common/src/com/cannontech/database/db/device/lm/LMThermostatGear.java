package com.cannontech.database.db.device.lm;
/**
 * This type was created in VisualAge.
 */

public abstract class LMThermostatGear extends com.cannontech.database.db.device.lm.LMProgramDirectGear
{
	private StringBuffer settings = null;
	private Integer minValue = new Integer(0);
	private Integer maxValue = new Integer(0);
	private Integer valueB = new Integer(0);
	private Integer valueD = new Integer(0);
	private Integer valueF = new Integer(0);
	private Integer random = new Integer(0);
	private Integer valueTa= new Integer(0);
	private Integer valueTb = new Integer(0);
	private Integer valueTc = new Integer(0);
	private Integer valueTd = new Integer(0);     
	private Integer valueTe = new Integer(0);
	private Integer valueTf = new Integer(0);
			
	public static final String SETTER_COLUMNS[] = 
	{ 
		"Settings", "MinValue", "MaxValue", "ValueB",
		"ValueD", "ValueF", "Random", "ValueTa",
		"ValueTb", "ValueTc", "ValueTd", "ValueTe",
		"ValueTf"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "GearID" };

	public static final String TABLE_NAME = "LMThermostatGear";


/**
 * LMGroupVersacomSerial constructor comment.
 */
public LMThermostatGear() {
	super();
}


/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	super.add();
	
	Object addValues[] = { getGearID(), getSettings().toString(), getMinValue(),
			getMaxValue(), getValueB(), getValueD(), getValueF(),
			getRandom(), getValueTa(), getValueTb(), getValueTc(), 
			getValueTd(), getValueTe(), getValueTf() };
	
	add( TABLE_NAME, addValues );
}


/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, "GearID", getGearID() );
	
	super.delete();

}


	/**
	 * This method was created in VisualAge.
	 * @return com.cannontech.database.db.point.State[]
	 * @param stateGroup java.lang.Integer
	 */
public static final void deleteSomeThermoGears(
		Integer gearID,
		java.sql.Connection conn)
		throws java.sql.SQLException
	{
		String sql = "DELETE FROM " + TABLE_NAME + " WHERE GearID=" + gearID;

		if (conn == null)
			throw new IllegalArgumentException("Received a (null) database connection");

		java.sql.PreparedStatement pstmt = null;

		try
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.execute();
		}
		catch (java.sql.SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close();
			}
			catch (java.sql.SQLException e2)
			{
				e2.printStackTrace(); //something is up
			}
		}

	}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMaxValue() {
	return maxValue;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMinValue() {
	return minValue;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:47:06 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getRandom() {
	return random;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @return java.lang.String
 */
public java.lang.StringBuffer getSettings() {
	if(settings == null)
		settings = new StringBuffer("----");
	return settings;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getValueB() {
	return valueB;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getValueD() {
	return valueD;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getValueF() {
	return valueF;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getValueTa() {
	return valueTa;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getValueTb() {
	return valueTb;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getValueTc() {
	return valueTc;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getValueTd() {
	return valueTd;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getValueTe() {
	return valueTe;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getValueTf() {
	return valueTf;
}


/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	super.retrieve();

	Object constraintValues[] = { getGearID() };	
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setSettings(new StringBuffer( (String) results[0] ));
		setMinValue( (Integer) results[1] );		
		setMaxValue( (Integer) results[2] );
		setValueB( (Integer) results[3] );
		setValueD( (Integer) results[4] );
		setValueF( (Integer) results[5] );
		setRandom( (Integer) results[6] );
		setValueTa( (Integer) results[7] );
		setValueTb( (Integer) results[8] );
		setValueTc( (Integer) results[9] );
		setValueTd( (Integer) results[10] );
		setValueTe( (Integer) results[11] );
		setValueTf( (Integer) results[12] );

	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @param newMaxValue java.lang.Integer
 */
public void setMaxValue(java.lang.Integer newMaxValue) {
	maxValue = newMaxValue;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @param newMinValue java.lang.Integer
 */
public void setMinValue(java.lang.Integer newMinValue) {
	minValue = newMinValue;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:47:06 PM)
 * @param newRandom java.lang.Integer
 */
public void setRandom(java.lang.Integer newRandom) {
	random = newRandom;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @param newSettings java.lang.String
 */
public void setSettings(java.lang.StringBuffer newSettings) {
	settings = newSettings;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @param newValueB java.lang.Integer
 */
public void setValueB(java.lang.Integer newValueB) {
	valueB = newValueB;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @param newValueD java.lang.Integer
 */
public void setValueD(java.lang.Integer newValueD) {
	valueD = newValueD;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @param newValueF java.lang.Integer
 */
public void setValueF(java.lang.Integer newValueF) {
	valueF = newValueF;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @param newValueTa java.lang.Integer
 */
public void setValueTa(java.lang.Integer newValueTa) {
	valueTa = newValueTa;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @param newValueTb java.lang.Integer
 */
public void setValueTb(java.lang.Integer newValueTb) {
	valueTb = newValueTb;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @param newValueTc java.lang.Integer
 */
public void setValueTc(java.lang.Integer newValueTc) {
	valueTc = newValueTc;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @param newValueTd java.lang.Integer
 */
public void setValueTd(java.lang.Integer newValueTd) {
	valueTd = newValueTd;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @param newValueTe java.lang.Integer
 */
public void setValueTe(java.lang.Integer newValueTe) {
	valueTe = newValueTe;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2002 4:40:15 PM)
 * @param newValueTf java.lang.Integer
 */
public void setValueTf(java.lang.Integer newValueTf) {
	valueTf = newValueTf;
}


/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	super.update();
	
	Object setValues[] = { getSettings().toString(), getMinValue(),
			getMaxValue(), getValueB(), getValueD(), getValueF(),
			getRandom(), getValueTa(), getValueTb(), getValueTc(), 
			getValueTd(), getValueTe(), getValueTf() };

	Object constraintValues[] = { getGearID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}