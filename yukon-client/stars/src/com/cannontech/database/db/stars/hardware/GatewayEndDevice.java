package com.cannontech.database.db.stars.hardware;

import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.DBPersistent;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class GatewayEndDevice extends DBPersistent {
	
	private String serialNumber = null;
	private Integer hardwareType = null;
	private Integer dataType = new Integer( CtiUtilities.NONE_ZERO_ID );
	private String dataValue = "";
	
	public static final String TABLE_NAME = "GatewayEndDevice";
	
	public static final String[] CONSTRAINT_COLUMNS = {
		"SerialNumber", "HardwareType"
	};
	
	public static final String[] SETTER_COLUMNS = {
		"DataType", "DataValue"
	};
	
	public GatewayEndDevice() {
		super();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = {
			getSerialNumber(), getHardwareType(), getDataType(), getDataValue()
		};
		
		add( TABLE_NAME, addValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		Object[] constraintValues = {
			getSerialNumber(), getHardwareType()
		};
		
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		Object[] constraintValues = {
			getSerialNumber(), getHardwareType()
		};
		
		Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
		
		if( results.length == SETTER_COLUMNS.length )
		{
			setDataType( (Integer) results[0] );
			setDataValue( (String) results[1] );
		}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] constraintValues = {
			getSerialNumber(), getHardwareType()
		};
		
		Object[] setValues = {
			getDataType(), getDataValue()
		};
		
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	
	public static Object[][] getHardwareData(String serialNumber, Integer hardwareType) {
		String sql = "SELECT DataType, DataValue FROM " + TABLE_NAME +
				" WHERE SerialNumber LIKE '" + serialNumber + "' AND HardwareType = " + hardwareType;
		com.cannontech.database.SqlStatement stmt =
				new com.cannontech.database.SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			
			Object[][] data = new Object[stmt.getRowCount()][];
			for (int i = 0; i < stmt.getRowCount(); i++) {
				Object[] row = stmt.getRow(i);
				data[i] = new Object[2];
				data[i][0] = new Integer( ((java.math.BigDecimal) row[0]).intValue() );
				data[i][1] = (String) row[1];
			}
			
			return data;
		}
		catch (Exception e) {
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}

	/**
	 * Returns the dataType.
	 * @return Integer
	 */
	public Integer getDataType() {
		return dataType;
	}

	/**
	 * Returns the dataValue.
	 * @return String
	 */
	public String getDataValue() {
		return dataValue;
	}

	/**
	 * Returns the hardwareType.
	 * @return Integer
	 */
	public Integer getHardwareType() {
		return hardwareType;
	}

	/**
	 * Returns the serialNumber.
	 * @return String
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * Sets the dataType.
	 * @param dataType The dataType to set
	 */
	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}

	/**
	 * Sets the dataValue.
	 * @param dataValue The dataValue to set
	 */
	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}

	/**
	 * Sets the hardwareType.
	 * @param hardwareType The hardwareType to set
	 */
	public void setHardwareType(Integer hardwareType) {
		this.hardwareType = hardwareType;
	}

	/**
	 * Sets the serialNumber.
	 * @param serialNumber The serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

}
