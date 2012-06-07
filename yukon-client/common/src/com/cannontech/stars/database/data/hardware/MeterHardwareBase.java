package com.cannontech.stars.database.data.hardware;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.stars.database.db.hardware.LMHardwareBase;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class MeterHardwareBase extends InventoryBase {

	private com.cannontech.stars.database.db.hardware.MeterHardwareBase meterHardwareBase = null;
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
		delete( "LMHardwareToMeterMapping", "MeterInventoryID", getInventoryBase().getInventoryID() );
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

	public com.cannontech.stars.database.db.hardware.MeterHardwareBase getMeterHardwareBase() {
		if (meterHardwareBase == null)
			meterHardwareBase = new com.cannontech.stars.database.db.hardware.MeterHardwareBase();
		return meterHardwareBase;
	}

	public void setMeterHardwareBase(com.cannontech.stars.database.db.hardware.MeterHardwareBase meterHardwareBase) {
		this.meterHardwareBase = meterHardwareBase;
	}

    public static MeterHardwareBase retrieveMeterHardwareBase(int accountID, String meterNumber, int energyCompanyID)
    {
		MeterHardwareBase meterHardwareBase = null;
		
    	String sql = "SELECT IB.INVENTORYID, IB.ACCOUNTID, IB.INSTALLATIONCOMPANYID, IB.CATEGORYID, IB.RECEIVEDATE, IB.INSTALLDATE, " +
    			" IB.REMOVEDATE, IB.ALTERNATETRACKINGNUMBER, IB.VOLTAGEID, IB.NOTES, IB.DEVICEID, IB.DEVICELABEL, " + 
    			" MHB.METERNUMBER, MHB.METERTYPEID " +
    			" FROM " + com.cannontech.stars.database.db.hardware.MeterHardwareBase.TABLE_NAME + " MHB, " +
    			com.cannontech.stars.database.db.hardware.InventoryBase.TABLE_NAME + " IB, ECTOINVENTORYMAPPING MAP " +
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
				meterHardwareBase.getInventoryBase().setReceiveDate(rset.getTimestamp(5));
				meterHardwareBase.getInventoryBase().setInstallDate(rset.getTimestamp(6));
				meterHardwareBase.getInventoryBase().setRemoveDate(rset.getTimestamp(7));
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
    			" FROM " + com.cannontech.stars.database.db.hardware.MeterHardwareBase.TABLE_NAME + " MHB, " +
    			com.cannontech.stars.database.db.hardware.InventoryBase.TABLE_NAME + " IB, ECTOINVENTORYMAPPING MAP " +
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
				meterHardwareBase.getInventoryBase().setReceiveDate(rset.getTimestamp(5));
				meterHardwareBase.getInventoryBase().setInstallDate(rset.getTimestamp(6));
				meterHardwareBase.getInventoryBase().setRemoveDate(rset.getTimestamp(7));
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
                " FROM " + com.cannontech.stars.database.db.hardware.MeterHardwareBase.TABLE_NAME + " MHB, " +
                com.cannontech.stars.database.db.hardware.InventoryBase.TABLE_NAME + " IB, ECTOINVENTORYMAPPING MAP " +
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
                meterHardwareBase.getInventoryBase().setReceiveDate(rset.getTimestamp(5));
                meterHardwareBase.getInventoryBase().setInstallDate(rset.getTimestamp(6));
                meterHardwareBase.getInventoryBase().setRemoveDate(rset.getTimestamp(7));
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
    
    public static boolean hasSwitchAssigned(int meterInvenID)
    {
        boolean truth = false;
        
        SqlStatement stmt = new SqlStatement("SELECT LMHARDWAREINVENTORYID FROM LMHARDWARETOMETERMAPPING WHERE METERINVENTORYID = " + meterInvenID, CtiUtilities.getDatabaseAlias());
        
        try
        {
            stmt.execute();
            
            if( stmt.getRowCount() > 0 )
            {
                truth = true;
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        
        return truth;
    }
    
    public static ArrayList<LMHardwareBase> retrieveAssignedSwitches(int meterInvenID)
    {
    	ArrayList<LMHardwareBase> lmHardwares = new ArrayList<LMHardwareBase>();
        
        SqlStatement stmt = new SqlStatement("SELECT LMHB.INVENTORYID, LMHB.MANUFACTURERSERIALNUMBER, LMHB.LMHARDWARETYPEID, LMHB.ROUTEID, LMHB.CONFIGURATIONID " + 
        									" FROM LMHARDWAREBASE LMHB, LMHARDWARETOMETERMAPPING MAP " + 
        									" WHERE METERINVENTORYID = " + meterInvenID  + 
        									" AND MAP.LMHARDWAREINVENTORYID = LMHB.INVENTORYID ", CtiUtilities.getDatabaseAlias());
        
        try
        {
            stmt.execute();
            
        	for( int i = 0; i < stmt.getRowCount(); i++ )
            {
        		LMHardwareBase lmHardwareBase = new LMHardwareBase();
        		lmHardwareBase.setInventoryID(new Integer(stmt.getRow(i)[0].toString()));
        		lmHardwareBase.setManufacturerSerialNumber(stmt.getRow(i)[1].toString());
        		lmHardwareBase.setLMHardwareTypeID(new Integer(stmt.getRow(i)[2].toString()));
        		lmHardwareBase.setRouteID(new Integer(stmt.getRow(i)[3].toString()));
        		lmHardwareBase.setConfigurationID(new Integer(stmt.getRow(i)[4].toString()));
        		lmHardwares.add(lmHardwareBase);
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        
        return lmHardwares;
    }
    
    /**
     * this method assumes that this switch's inventoryID is valid for the correct customer account already 
     */
    public static boolean switchIsNotAvailable(int switchInventoryID)
    {
        boolean truth = false;
        
        SqlStatement stmt = new SqlStatement("SELECT INVENTORYID FROM LMHARDWAREBASE WHERE INVENTORYID " +
                "NOT IN (SELECT LMHARDWAREINVENTORYID FROM LMHARDWARETOMETERMAPPING WHERE LMHARDWAREINVENTORYID = " + switchInventoryID + ")", CtiUtilities.getDatabaseAlias());
        
        try
        {
            stmt.execute();
            
            if( stmt.getRowCount() > 0 )
            {
                truth = true;
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        
        return truth;
    }

    public static List<Pair> getAssignedSwitchesForDisplay(int meterInvenID)
    {
        List<Pair> switchPair = new ArrayList<Pair>();
        
        SqlStatement stmt = new SqlStatement("SELECT INVENTORYID, MANUFACTURERSERIALNUMBER FROM LMHARDWAREBASE WHERE INVENTORYID IN " +
                "(SELECT LMHARDWAREINVENTORYID FROM LMHARDWARETOMETERMAPPING WHERE METERINVENTORYID = " + meterInvenID + ")", CtiUtilities.getDatabaseAlias());
        
        try
        {
            stmt.execute();
            
            if( stmt.getRowCount() > 0 )
            {
                for( int i = 0; i < stmt.getRowCount(); i++ )
                {
                    Pair switchInfo = new Pair(new Integer(stmt.getRow(i)[0].toString()), stmt.getRow(i)[1].toString());
                    switchPair.add(switchInfo);
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        
        return switchPair;
    }
    
    public static boolean isSwitchAssignedToAnyMeters(int switchID)
    {
        boolean isAssigned = false;
        
        SqlStatement stmt = new SqlStatement("SELECT * FROM LMHARDWARETOMETERMAPPING WHERE LMHARDWAREINVENTORYID = " + switchID, CtiUtilities.getDatabaseAlias());
        
        try
        {
            stmt.execute();
            
            if( stmt.getRowCount() > 0 )
            {
               isAssigned = true;
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        
        return isAssigned;
    }
    
    public static boolean deleteAssignedSwitches(int meterID)
    {
        boolean truth = false;
        
        SqlStatement stmt = new SqlStatement("DELETE FROM LMHARDWARETOMETERMAPPING WHERE METERINVENTORYID = " + meterID, CtiUtilities.getDatabaseAlias());
        
        try
        {
            stmt.execute();
            truth = true;
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        
        return truth;
    }
    
    public static boolean updateAssignedSwitch(int switchID, int meterID)
    {
        boolean truth = false;
        
        SqlStatement stmt = new SqlStatement("INSERT INTO LMHARDWARETOMETERMAPPING VALUES(" + switchID + ", " + meterID + ")", CtiUtilities.getDatabaseAlias());
        
        try
        {
            stmt.execute();
            truth = true;
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        
        return truth;
    }
    
    public String getDeviceLabel()
    {
        if(this.getDeviceLabel().equals(""))
        {
           getInventoryBase().setDeviceLabel(meterHardwareBase.getMeterNumber());
        }
        
        return getDeviceLabel();
    }
}