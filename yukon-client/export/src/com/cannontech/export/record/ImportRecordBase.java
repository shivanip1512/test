package com.cannontech.export.record;

/**
 * @author snebben
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public abstract class ImportRecordBase implements RecordBase
{

	/**
	 * Constructor for ImportRecordBase.
	 */
	public ImportRecordBase()
	{
		super();
	}
	
	public abstract void parse(String line);
	public abstract void prepareStatement(java.sql.PreparedStatement pstmt) throws java.sql.SQLException;
	public abstract String getSqlString();
	public abstract String getRecord();
}
