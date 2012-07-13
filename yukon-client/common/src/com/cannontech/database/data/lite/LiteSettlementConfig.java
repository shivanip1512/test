package com.cannontech.database.data.lite;

import java.sql.Connection;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.company.SettlementConfig;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteSettlementConfig extends LiteBase {
	
	private String fieldName = CtiUtilities.STRING_NONE;
	private String fieldValue = CtiUtilities.STRING_NONE;
	private String desc = CtiUtilities.STRING_NONE;
	private int refEntryID = 0;
	
	
	public LiteSettlementConfig() {
		super();
		setLiteType( LiteTypes.SETTLEMENT);
	}
	
	public LiteSettlementConfig(int configID_) {
		this();
		setLiteID( configID_);
	}
	
	public LiteSettlementConfig(int configID_, String fieldName_, String fieldValue_, String desc_, int refEntryID_) {
		this(configID_);
		setFieldName(fieldName_);
		setFieldValue(fieldValue_);
		setDescription(desc_);
		setRefEntryID(refEntryID_);		
	}
	
	public int getConfigID() {
		return getLiteID();
	}
	
	public void setConfigID(int configID_) {
		setLiteID( configID_ );
	}
	
	public void retrieve(String dbAlias) {
		Connection conn = null;
		try {
			conn = PoolManager.getInstance().getConnection( dbAlias );
			
			SqlStatement stat = new SqlStatement(
					"SELECT FieldName, FieldValue, Description, RefEntryID " +
					" FROM " + SettlementConfig.TABLE_NAME +
					" WHERE ConfigID = " + getConfigID(),
					conn );
			
			stat.execute();
			
			if( stat.getRowCount() <= 0 )
				throw new IllegalStateException("Unable to find the SettlementConfig with ConfigID = " + getConfigID() );
			
			Object[] objs = stat.getRow(0);
			
			setFieldName( objs[0].toString() );
			setFieldValue( objs[1].toString() );
			setDescription( objs[2].toString());
			setRefEntryID( ((java.math.BigDecimal) objs[3]).intValue() );
		}
		catch (Exception e) {
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
	}

	/**
	 * @return
	 */
	public int getRefEntryID()
	{
		return refEntryID;
	}

	/**
	 * @return
	 */
	public String getFieldName()
	{
		return fieldName;
	}

	/**
	 * @return
	 */
	public String getFieldValue()
	{
		return fieldValue;
	}

	/**
	 * @param i
	 */
	public void setRefEntryID(int i)
	{
		refEntryID = i;
	}

	/**
	 * @param string
	 */
	public void setFieldName(String string)
	{
		fieldName = string;
	}

	/**
	 * @param string
	 */
	public void setFieldValue(String string)
	{
		fieldValue = string;
	}

	public String getDescription()
	{
		return desc;
	}
	public void setDescription(String desc_)
	{
		desc = desc_;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((desc == null) ? 0 : desc.hashCode());
        result = prime * result
                 + ((fieldName == null) ? 0 : fieldName.hashCode());
        result = prime * result
                 + ((fieldValue == null) ? 0 : fieldValue.hashCode());
        result = prime * result + refEntryID;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        LiteSettlementConfig other = (LiteSettlementConfig) obj;
        if (desc == null) {
            if (other.desc != null)
                return false;
        } else if (!desc.equals(other.desc))
            return false;
        if (fieldName == null) {
            if (other.fieldName != null)
                return false;
        } else if (!fieldName.equals(other.fieldName))
            return false;
        if (fieldValue == null) {
            if (other.fieldValue != null)
                return false;
        } else if (!fieldValue.equals(other.fieldValue))
            return false;
        if (refEntryID != other.refEntryID)
            return false;
        return true;
    }
	
	
}
