package com.cannontech.export.record;

/**
 * @author snebben
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DynamicLMProgramRecord extends ImportRecordBase
{
	private String sqlString = "UPDATE DYNAMICLMPROGRAM SET PROGRAMSTATE = ? WHERE DEVICEID = ?";
	private String record = "DLMP";	//for 'Dynamic LM Program'
	
	private Integer deviceid = null;
	private Integer programState = null;
	
	/**
	 * Constructor for DynamicLMProgramRecord.
	 */
	public DynamicLMProgramRecord()
	{
		super();
	}

	/**
	 * Constructor for DynamicLMProgramRecord.
	 */
	public DynamicLMProgramRecord(int deviceid, int programState)
	{
		super();
		this.deviceid = new Integer(deviceid);
		this.programState = new Integer(programState);
	}

	/**
	 * @see com.cannontech.export.record.RecordBase#dataToString()
	 */
	public String dataToString()
	{
		StringBuffer dataString = new StringBuffer();
		
		dataString.append(getRecord() + ",");	//type of record DLMP
		dataString.append(getDeviceid() + ",");
		dataString.append(getProgramState() );
		
		dataString.append("\r\n");
		
		return dataString.toString();
	}

	public void parse(String line)
	{
		int beginIndex = 0;
		int csvIndex = line.indexOf(',');
		setRecord((String)line.substring(beginIndex, csvIndex));

		beginIndex = csvIndex + 1;
		csvIndex = line.indexOf(',', beginIndex);
		setDeviceid(Integer.valueOf(line.substring(beginIndex, csvIndex)));

		beginIndex = csvIndex + 1;
		setProgramState(Integer.valueOf(line.substring(beginIndex)));
	}					

	public void prepareStatement(java.sql.PreparedStatement pstmt) throws java.sql.SQLException
	{
		pstmt.setInt(1, getProgramState().intValue());
		pstmt.setInt(2, getDeviceid().intValue());
	}

	/**
	 * Returns the programState.
	 * @return Integer
	 */
	public Integer getProgramState()
	{
		return programState;
	}

	/**
	 * Sets the programState.
	 * @param programState The programState to set
	 */
	public void setProgramState(Integer programState)
	{
		this.programState = programState;
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
	 * Sets the record.
	 * @param record The record to set
	 */
	public void setRecord(String record)
	{
		this.record = record;
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
