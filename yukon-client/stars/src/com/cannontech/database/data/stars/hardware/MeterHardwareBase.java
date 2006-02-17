package com.cannontech.database.data.stars.hardware;

import java.util.ArrayList;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class MeterHardwareBase extends InventoryBase {

	private com.cannontech.database.db.stars.hardware.MeterHardwareBase meterHardwareBase = null;
	public MeterHardwareBase() {
		super();
	}

	public void setInventoryID(Integer newID) {
		super.setInventoryID(newID);
		getMeterHardwareBase().setInventoryID(newID);
	}

	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection(conn);
		getMeterHardwareBase().setDbConnection(conn);
	}

	public void delete() throws java.sql.SQLException {
		getMeterHardwareBase().delete();
		super.deleteInventoryBase();
	}

	public void add() throws java.sql.SQLException {
		super.add();
        
		getMeterHardwareBase().setInventoryID( getInventoryBase().getInventoryID() );
		getMeterHardwareBase().add();
	}

	public void update() throws java.sql.SQLException {
		super.update();
		getMeterHardwareBase().update();
	}

	public void retrieve() throws java.sql.SQLException {
		super.retrieve();
		getMeterHardwareBase().retrieve();
	}

	public com.cannontech.database.db.stars.hardware.MeterHardwareBase getMeterHardwareBase() {
		if (meterHardwareBase == null)
			meterHardwareBase = new com.cannontech.database.db.stars.hardware.MeterHardwareBase();
		return meterHardwareBase;
	}

	public void setMeterHardwareBase(com.cannontech.database.db.stars.hardware.MeterHardwareBase meterHardwareBase) {
		this.meterHardwareBase = meterHardwareBase;
	}

    public static MeterHardwareBase retrieveMeterHardwareBase(int accountID, String meterNumber, int energyCompanyID)
    {
		MeterHardwareBase meterHardwareBase = null;
		
    	String sql = "SELECT IB.INVENTORYID, IB.ACCOUNTID, IB.INSTALLATIONCOMPANYID, IB.CATEGORYID, IB.RECEIVEDATE, IB.INSTALLDATE, " +
    			" IB.REMOVEDATE, IB.ALTERNATETRACKINGNUMBER, IB.VOLTAGEID, IB.NOTES, IB.DEVICEID, IB.DEVICELABEL, " + 
    			" MHB.METERNUMBER, MHB.METERTYPEID " +
    			" FROM " + com.cannontech.database.db.stars.hardware.MeterHardwareBase.TABLE_NAME + " MHB, " +
    			com.cannontech.database.db.stars.hardware.InventoryBase.TABLE_NAME + " IB, ECTOINVENTORYMAPPING MAP " +
    			" WHERE MHB.INVENTORYID = IB.INVENTORYID " +
    			" AND MHB.METERNUMBER = ? " +
    			" AND IB.ACCOUNTID = ? " +
    			" AND IB.INVENTORYID = MAP.INVENTORYID " +
    			" AND MAP.ENERGYCOMPANYID = ? ";
    	
    	java.sql.Connection conn = null;
    	java.sql.PreparedStatement stmt = null;
    	java.sql.ResultSet rset = null;
    	
    	try {
    		conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
    		
    		stmt = conn.prepareStatement(sql);
			stmt.setString(1, meterNumber);
			stmt.setInt(2, accountID);
			stmt.setInt(3, energyCompanyID);
			
			rset = stmt.executeQuery();
    		
			while (rset.next()) {
				meterHardwareBase = new MeterHardwareBase();
				meterHardwareBase.setInventoryID(new Integer(rset.getInt(1)) );
				meterHardwareBase.getInventoryBase().setAccountID( new Integer(rset.getInt(2)) );
				meterHardwareBase.getInventoryBase().setInstallationCompanyID( new Integer(rset.getInt(3)) );
				meterHardwareBase.getInventoryBase().setCategoryID(  new Integer(rset.getInt(4)) );
				meterHardwareBase.getInventoryBase().setReceiveDate(rset.getDate(5));
				meterHardwareBase.getInventoryBase().setInstallDate(rset.getDate(6));
				meterHardwareBase.getInventoryBase().setRemoveDate(rset.getDate(7));
				meterHardwareBase.getInventoryBase().setAlternateTrackingNumber(rset.getString(8));
				meterHardwareBase.getInventoryBase().setVoltageID( new Integer(rset.getInt(9)) );
				meterHardwareBase.getInventoryBase().setNotes(rset.getString(10));
				meterHardwareBase.getInventoryBase().setDeviceID( new Integer(rset.getInt(11)) );
				meterHardwareBase.getInventoryBase().setDeviceLabel(rset.getString(12));
				meterHardwareBase.getMeterHardwareBase().setMeterNumber( rset.getString(13) );
				meterHardwareBase.getMeterHardwareBase().setMeterTypeID( new Integer(rset.getInt(14)) );
				break;
			}
    	}
    	catch (java.sql.SQLException e) {
    		CTILogger.error( e.getMessage(), e );
    	}
    	finally {
    		try {
				if (rset != null) rset.close();
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
    		}
    		catch (java.sql.SQLException e) {}
    	}
    	
		return meterHardwareBase;
    }

    public static ArrayList retrieveMeterHardwareBase(int accountID, int energyCompanyID)
    {
		java.util.ArrayList meterList = new java.util.ArrayList();
		
    	String sql = "SELECT IB.INVENTORYID, IB.ACCOUNTID, IB.INSTALLATIONCOMPANYID, IB.CATEGORYID, IB.RECEIVEDATE, IB.INSTALLDATE, " +
				" IB.REMOVEDATE, IB.ALTERNATETRACKINGNUMBER, IB.VOLTAGEID, IB.NOTES, IB.DEVICEID, IB.DEVICELABEL, " + 
				" MHB.METERNUMBER, MHB.METERTYPEID " +
    			" FROM " + com.cannontech.database.db.stars.hardware.MeterHardwareBase.TABLE_NAME + " MHB, " +
    			com.cannontech.database.db.stars.hardware.InventoryBase.TABLE_NAME + " IB, ECTOINVENTORYMAPPING MAP " +
    			" WHERE MHB.INVENTORYID = IB.INVENTORYID " +
    			" AND IB.ACCOUNTID = ? " +
    			" AND IB.INVENTORYID = MAP.INVENTORYID " +
    			" AND MAP.ENERGYCOMPANYID = ? ";
    	
    	java.sql.Connection conn = null;
    	java.sql.PreparedStatement stmt = null;
    	java.sql.ResultSet rset = null;
    	
    	try {
    		conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
    		
    		stmt = conn.prepareStatement(sql);
			stmt.setInt(1, accountID);
			stmt.setInt(2, energyCompanyID);
			
			rset = stmt.executeQuery();
    		
			while (rset.next()) {
				MeterHardwareBase meterHardwareBase = new MeterHardwareBase();
				meterHardwareBase = new MeterHardwareBase();
				meterHardwareBase.setInventoryID(new Integer(rset.getInt(1)) );
				meterHardwareBase.getInventoryBase().setAccountID( new Integer(rset.getInt(2)) );
				meterHardwareBase.getInventoryBase().setInstallationCompanyID( new Integer(rset.getInt(3)) );
				meterHardwareBase.getInventoryBase().setCategoryID(  new Integer(rset.getInt(4)) );
				meterHardwareBase.getInventoryBase().setReceiveDate(rset.getDate(5));
				meterHardwareBase.getInventoryBase().setInstallDate(rset.getDate(6));
				meterHardwareBase.getInventoryBase().setRemoveDate(rset.getDate(7));
				meterHardwareBase.getInventoryBase().setAlternateTrackingNumber(rset.getString(8));
				meterHardwareBase.getInventoryBase().setVoltageID( new Integer(rset.getInt(9)) );
				meterHardwareBase.getInventoryBase().setNotes(rset.getString(10));
				meterHardwareBase.getInventoryBase().setDeviceID( new Integer(rset.getInt(11)) );
				meterHardwareBase.getInventoryBase().setDeviceLabel(rset.getString(12));
				meterHardwareBase.getMeterHardwareBase().setMeterNumber( rset.getString(13) );
				meterHardwareBase.getMeterHardwareBase().setMeterTypeID( new Integer(rset.getInt(14)) );
				meterList.add( meterHardwareBase );
			}
    	}
    	catch (java.sql.SQLException e) {
    		CTILogger.error( e.getMessage(), e );
    	}
    	finally {
    		try {
				if (rset != null) rset.close();
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
    		}
    		catch (java.sql.SQLException e) {}
    	}
    	return meterList;
    }
    
    public static MeterHardwareBase retrieveMeterHardwareBase(String meterNumber, int energyCompanyID)
    {
        MeterHardwareBase meterHardwareBase = null;
        
        String sql = "SELECT IB.INVENTORYID, IB.ACCOUNTID, IB.INSTALLATIONCOMPANYID, IB.CATEGORYID, IB.RECEIVEDATE, IB.INSTALLDATE, " +
                " IB.REMOVEDATE, IB.ALTERNATETRACKINGNUMBER, IB.VOLTAGEID, IB.NOTES, IB.DEVICEID, IB.DEVICELABEL, " + 
                " MHB.METERNUMBER, MHB.METERTYPEID " +
                " FROM " + com.cannontech.database.db.stars.hardware.MeterHardwareBase.TABLE_NAME + " MHB, " +
                com.cannontech.database.db.stars.hardware.InventoryBase.TABLE_NAME + " IB, ECTOINVENTORYMAPPING MAP " +
                " WHERE MHB.INVENTORYID = IB.INVENTORYID " +
                " AND MHB.METERNUMBER = ? " +
                " AND IB.INVENTORYID = MAP.INVENTORYID " +
                " AND MAP.ENERGYCOMPANYID = ? ";
        
        java.sql.Connection conn = null;
        java.sql.PreparedStatement stmt = null;
        java.sql.ResultSet rset = null;
        
        try {
            conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, meterNumber);
            stmt.setInt(2, energyCompanyID);
            
            rset = stmt.executeQuery();
            
            while (rset.next()) {
                meterHardwareBase = new MeterHardwareBase();
                meterHardwareBase.setInventoryID(new Integer(rset.getInt(1)) );
                meterHardwareBase.getInventoryBase().setAccountID( new Integer(rset.getInt(2)) );
                meterHardwareBase.getInventoryBase().setInstallationCompanyID( new Integer(rset.getInt(3)) );
                meterHardwareBase.getInventoryBase().setCategoryID(  new Integer(rset.getInt(4)) );
                meterHardwareBase.getInventoryBase().setReceiveDate(rset.getDate(5));
                meterHardwareBase.getInventoryBase().setInstallDate(rset.getDate(6));
                meterHardwareBase.getInventoryBase().setRemoveDate(rset.getDate(7));
                meterHardwareBase.getInventoryBase().setAlternateTrackingNumber(rset.getString(8));
                meterHardwareBase.getInventoryBase().setVoltageID( new Integer(rset.getInt(9)) );
                meterHardwareBase.getInventoryBase().setNotes(rset.getString(10));
                meterHardwareBase.getInventoryBase().setDeviceID( new Integer(rset.getInt(11)) );
                meterHardwareBase.getInventoryBase().setDeviceLabel(rset.getString(12));
                meterHardwareBase.getMeterHardwareBase().setMeterNumber( rset.getString(13) );
                meterHardwareBase.getMeterHardwareBase().setMeterTypeID( new Integer(rset.getInt(14)) );
                break;
            }
        }
        catch (java.sql.SQLException e) {
            CTILogger.error( e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) rset.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            }
            catch (java.sql.SQLException e) {}
        }
        
        return meterHardwareBase;
    }
}