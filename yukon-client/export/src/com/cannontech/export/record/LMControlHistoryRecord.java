package com.cannontech.export.record;

import java.sql.SQLException;

/**
 * @author snebben
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LMControlHistoryRecord extends ImportRecordBase
{
	private String sqlString = "INSERT INTO LMCONTROLHISTORY VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private com.cannontech.database.db.pao.LMControlHistory lmControlHistory = null;
	private String record = "LMCH";	//for 'LM Control History'
	/**
	 * Constructor for LMControlHistoryRecord.
	 */
	public LMControlHistoryRecord()
	{
		super();
	}

	/**
	 * Constructor for LMControlHistoryRecord.
	 */
	public LMControlHistoryRecord(com.cannontech.database.db.pao.LMControlHistory newLMControlHistory)
	{
		super();
		lmControlHistory = newLMControlHistory;
	}


	/**
	 * @see com.cannontech.export.record.RecordBase#dataToString()
	 */
	public String dataToString()
	{
		StringBuffer dataString = new StringBuffer();
		
		dataString.append(getRecord() + ",");	//type of record LMCH
		dataString.append(lmControlHistory.getLmCtrlHistID() + ",");
		dataString.append(lmControlHistory.getPaObjectID() + ",");
		dataString.append(lmControlHistory.getStartDateTime() + ",");
		dataString.append(lmControlHistory.getSoeTag() + ",");
		dataString.append(lmControlHistory.getControlDuration() + ",");
		dataString.append(lmControlHistory.getControlType() + ",");
		dataString.append(lmControlHistory.getCurrentDailyTime() + ",");
		dataString.append(lmControlHistory.getCurrentMonthlyTime() + ",");
		dataString.append(lmControlHistory.getCurrentSeasonalTime() + ",");
		dataString.append(lmControlHistory.getCurrentAnnualTime() + ",");
		dataString.append(lmControlHistory.getActiveRestore() + ",");
		dataString.append(lmControlHistory.getReductionValue() + ",");
		dataString.append(lmControlHistory.getStopDateTime());
		
		dataString.append("\r\n");
		
		return dataString.toString();
	}
	
	public void parse(String line)
	{
		try
		{
			int beginIndex = 0;
			int csvIndex = line.indexOf(',');
			setRecord((String)line.substring(beginIndex, csvIndex));
			
			beginIndex = csvIndex + 1;
			csvIndex = line.indexOf(',', beginIndex);
			getLmControlHistory().setLmCtrlHistID(Integer.valueOf(line.substring(beginIndex, csvIndex)));
			
			beginIndex = csvIndex + 1;
			csvIndex = line.indexOf(',', beginIndex);
			getLmControlHistory().setPaObjectID(Integer.valueOf(line.substring(beginIndex, csvIndex)));
			
			beginIndex = csvIndex + 1;
			csvIndex = line.indexOf(',', beginIndex);
			getLmControlHistory().setStartDateTime(parseDateFormat.parse(line.substring(beginIndex, csvIndex)));
			
			beginIndex = csvIndex + 1;
			csvIndex = line.indexOf(',', beginIndex);
			getLmControlHistory().setSoeTag(Integer.valueOf(line.substring(beginIndex, csvIndex)));

			beginIndex = csvIndex + 1;
			csvIndex = line.indexOf(',', beginIndex);
			getLmControlHistory().setControlDuration(Integer.valueOf(line.substring(beginIndex, csvIndex)));
	
			beginIndex = csvIndex + 1;
			csvIndex = line.indexOf(',', beginIndex);
			getLmControlHistory().setControlType((String)line.substring(beginIndex, csvIndex));
	
			beginIndex = csvIndex + 1;
			csvIndex = line.indexOf(',', beginIndex);
			getLmControlHistory().setCurrentDailyTime(Integer.valueOf(line.substring(beginIndex, csvIndex)));
	
			beginIndex = csvIndex + 1;
			csvIndex = line.indexOf(',', beginIndex);
			getLmControlHistory().setCurrentMonthlyTime(Integer.valueOf(line.substring(beginIndex, csvIndex)));
	
			beginIndex = csvIndex + 1;
			csvIndex = line.indexOf(',', beginIndex);
			getLmControlHistory().setCurrentSeasonalTime(Integer.valueOf(line.substring(beginIndex, csvIndex)));
	
			beginIndex = csvIndex + 1;
			csvIndex = line.indexOf(',', beginIndex);
			getLmControlHistory().setCurrentAnnualTime(Integer.valueOf(line.substring(beginIndex, csvIndex)));
	
			beginIndex = csvIndex + 1;
			csvIndex = line.indexOf(',', beginIndex);
			getLmControlHistory().setActiveRestore((String)line.substring(beginIndex, csvIndex));
	
			beginIndex = csvIndex + 1;
			csvIndex = line.indexOf(',', beginIndex);
			getLmControlHistory().setReductionValue(Double.valueOf(line.substring(beginIndex, csvIndex)));
	
			beginIndex = csvIndex + 1;
			getLmControlHistory().setStopDateTime(parseDateFormat.parse(line.substring(beginIndex)));
		}
		catch(java.text.ParseException pe)
		{
			com.cannontech.clientutils.CTILogger.error("Unable to parse Date from file");
		}
	}		

	/**
	 * Returns the lmControlHistory.
	 * @return com.cannontech.database.db.pao.LMControlHistory
	 */
	public com.cannontech.database.db.pao.LMControlHistory getLmControlHistory()
	{
		if( lmControlHistory == null)
		{
			lmControlHistory = new com.cannontech.database.db.pao.LMControlHistory();
		}
		return lmControlHistory;
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
	 * Sets the lmControlHistory.
	 * @param lmControlHistory The lmControlHistory to set
	 */
	public void setLmControlHistory(
		com.cannontech.database.db.pao.LMControlHistory lmControlHistory)
	{
		this.lmControlHistory = lmControlHistory;
	}

	/**
	 * Sets the record.
	 * @param record The record to set
	 */
	public void setRecord(String record)
	{
		this.record = record;
	}

	public void prepareStatement(java.sql.PreparedStatement pstmt) throws SQLException
	{
		pstmt.setInt(1, getLmControlHistory().getLmCtrlHistID().intValue());
		pstmt.setInt(2, getLmControlHistory().getPaObjectID().intValue());			
		long startTime = getLmControlHistory().getStartDateTime().getTime();
		pstmt.setTimestamp(3, new java.sql.Timestamp(startTime));
		pstmt.setInt(4, getLmControlHistory().getSoeTag().intValue());
		pstmt.setInt(5, getLmControlHistory().getControlDuration().intValue());
		pstmt.setString(6, getLmControlHistory().getControlType().toString());
		pstmt.setInt(7, getLmControlHistory().getCurrentDailyTime().intValue());
		pstmt.setInt(8, getLmControlHistory().getCurrentMonthlyTime().intValue());
		pstmt.setInt(9, getLmControlHistory().getCurrentSeasonalTime().intValue());
		pstmt.setInt(10, getLmControlHistory().getCurrentAnnualTime().intValue());
		pstmt.setString(11, getLmControlHistory().getActiveRestore().toString());
		pstmt.setDouble(12, getLmControlHistory().getReductionValue().doubleValue());
		long stopTime = getLmControlHistory().getStopDateTime().getTime();
		pstmt.setTimestamp(13, new java.sql.Timestamp(stopTime));
	}
	/**
	 * Returns the sqlString.
	 * @return String
	 */
	public String getSqlString()
	{
		return sqlString;
	}

}
