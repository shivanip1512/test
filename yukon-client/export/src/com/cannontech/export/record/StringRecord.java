package com.cannontech.export.record;

/**
 * @author snebben
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StringRecord implements RecordBase
{
	private String stringData = null;

	/**
	 * Constructor for ImportRecordBase.
	 */
	public StringRecord()
	{
		super();
	}

	/**
	 * Constructor for ImportRecordBase.
	 */
	public StringRecord(String stringData)
	{
		super();
		this.stringData = stringData;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (3/21/2002 1:11:49 PM)
	 * @return java.lang.String
	 * @param record com.cannontech.export.DBPurgeRecord
	 */
	public String dataToString()
	{		
		String dataString = getStringData().toString();
		
		return dataString;
	}
	/**
	 * Returns the stringData.
	 * @return String
	 */
	public String getStringData()
	{
		return stringData;
	}

	/**
	 * Sets the stringData.
	 * @param stringData The stringData to set
	 */
	public void setStringData(String stringData)
	{
		this.stringData = stringData;
	}

}
