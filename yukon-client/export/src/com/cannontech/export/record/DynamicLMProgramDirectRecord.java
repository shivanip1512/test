package com.cannontech.export.record;

/**
 * @author snebben
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DynamicLMProgramDirectRecord extends ImportRecordBase
{
	private final String sqlString = "UPDATE DYNAMICLMPROGRAMDIRECT SET STARTTIME = ?, STOPTIME = ? WHERE DEVICEID = ?";
	private final String record = "DLMPD";
	private Integer deviceid = null;
	private java.util.Date startTime = null;
	private java.util.Date stopTime = null;
	
	/**
	 * Constructor for DynamicLMProgramDirectRecord
	 */
	public DynamicLMProgramDirectRecord()
	{
		super();
	}

	/**
	 * Constructor for DynamicLMProgramDirectRecord
	 */
	public DynamicLMProgramDirectRecord(int deviceid, java.util.Date startTime, java.util.Date stopTime)
	{
		super();
		this.deviceid = new Integer(deviceid);
		this.startTime = startTime;
		this.stopTime = stopTime;
	}

	/**
	 * @see com.cannontech.export.record.RecordBase#dataToString()
	 */
	public String dataToString()
	{
		StringBuffer dataString = new StringBuffer();
		
		dataString.append(getRecord() + ",");	//type of record DLMPD
		dataString.append(getDeviceid() + ",");		
		dataString.append(getStartTime() + ",");
		dataString.append(getStopTime());
		
		dataString.append("\r\n");
		
		return dataString.toString();
	}

	public void parse(String line)
	{

		try
		{
			int beginIndex = 0;
			int csvIndex = line.indexOf(',');
			//this is the record...we already have it finalized

			beginIndex = csvIndex + 1;
			csvIndex = line.indexOf(',', beginIndex);
			setDeviceid(Integer.valueOf(line.substring(beginIndex, csvIndex)));

			beginIndex = csvIndex + 1;
			csvIndex = line.indexOf(',', beginIndex);
			setStartTime(parseDateFormat.parse(line.substring(beginIndex, csvIndex)));
			
			beginIndex = csvIndex + 1;
			setStopTime(parseDateFormat.parse(line.substring(beginIndex)));
		}
		catch(java.text.ParseException pe)
		{
			com.cannontech.clientutils.CTILogger.error("Unable to parse Date from file");
		}
	}					

	public void prepareStatement(java.sql.PreparedStatement pstmt) throws java.sql.SQLException
	{
		long startTime = getStartTime().getTime();
		pstmt.setTimestamp(1, new java.sql.Timestamp(startTime));
		long stopTime = getStopTime().getTime();
		pstmt.setTimestamp(2, new java.sql.Timestamp(stopTime));						
		pstmt.setInt(3, getDeviceid().intValue());
	}

	/**
	 * Returns the startTime.
	 * @return java.util.Date
	 */
	public java.util.Date getStartTime()
	{
		return startTime;
	}

	/**
	 * Returns the stopTime.
	 * @return java.util.Date
	 */
	public java.util.Date getStopTime()
	{
		return stopTime;
	}

	/**
	 * Sets the startTime.
	 * @param startTime The startTime to set
	 */
	public void setStartTime(java.util.Date startTime)
	{
		this.startTime = startTime;
	}

	/**
	 * Sets the stopTime.
	 * @param stopTime The stopTime to set
	 */
	public void setStopTime(java.util.Date stopTime)
	{
		this.stopTime = stopTime;
	}

	/**
	 * Returns the record.
	 * @return String
	 */
	public String getRecord()
	{
		return record;
	}

	/**
	 * Returns the deviceid.
	 * @return int
	 */
	public Integer getDeviceid()
	{
		return deviceid;
	}

	/**
	 * Sets the deviceid.
	 * @param deviceid The deviceid to set
	 */
	public void setDeviceid(Integer deviceid)
	{
		this.deviceid = deviceid;
	}

	public String getSqlString()
	{
		return sqlString;
	}	
}
