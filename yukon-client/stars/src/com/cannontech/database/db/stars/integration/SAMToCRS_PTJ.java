package com.cannontech.database.db.stars.integration;

import java.util.ArrayList;
import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.stars.hardware.MeterHardwareBase;
import com.cannontech.database.data.stars.report.WorkOrderBase;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.stars.hardware.LMHardwareBase;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.SwitchCommandQueue.SwitchCommand;

public class SAMToCRS_PTJ extends DBPersistent {

    private Integer ptjID = null; 
    private Integer premiseNumber = null;
    private String debtorNumber = "";
    private String workOrderNumber = "";
    private String statusCode = "";		//P-Processed, X-Cancelled, C-Completed
    private Date dateTime_Completed = null;
    private String starsUserName = "";
    private Character extract = null;

    public static final String CONSTRAINT_COLUMNS[] = { "PTJID" };

    public static final String SETTER_COLUMNS[] = { "PTJID", "PremiseNumber", "DebtorNumber", "WorkOrderNumber", "StatusCode", 
    												"DateTime_Completed", "StarsUserName", "Extract"};

    public static final String TABLE_NAME = "SAMToCRS_PTJ";


public SAMToCRS_PTJ() {
    super();
}

public SAMToCRS_PTJ(Integer ptjID, Integer premiseNumber, String debtorNumber, String workOrderNumber, String statusCode, Date dateTime_Completed, String starsUserName) {
	super();
	this.ptjID = ptjID;
	this.premiseNumber = premiseNumber;
	this.debtorNumber = debtorNumber;
	this.workOrderNumber = workOrderNumber;
	this.statusCode = statusCode;
	this.dateTime_Completed = dateTime_Completed;
	this.starsUserName = starsUserName;
}

public void add() throws java.sql.SQLException 
{
	if (getPTJID() == null)
		setPTJID( getNextPTJID() );

    Object setValues[] = { getPTJID(), getPremiseNumber(), getDebtorNumber(), getWorkOrderNumber(),
    		getStatusCode(), getDateTime_Completed(), getStarsUserName(), getExtract()};

    add( TABLE_NAME, setValues );
}

public void delete() throws java.sql.SQLException 
{
    Object constraintValues[] = { getPTJID() };

    delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
}


public void retrieve() throws java.sql.SQLException 
{
    Object constraintValues[] = { getPTJID() };

    Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

    if( results.length == SETTER_COLUMNS.length )
    {
        setPremiseNumber( (Integer) results[0] );
        setDebtorNumber( (String) results[1] );
        setWorkOrderNumber( (String) results[2] );
        setStatusCode( (String) results[3] );
        setDateTime_Completed( (Date) results[4]);
        setStarsUserName( (String) results[5] );
        setExtract((Character)results[6]);
    }
    else
        throw new Error( getClass() + "::retrieve - Incorrect number of results" );
}


public void update() throws java.sql.SQLException 
{
    Object setValues[] = { getPTJID(), getPremiseNumber(), getDebtorNumber(), 
    		getStatusCode(), getDateTime_Completed(), getStarsUserName(), getExtract()}; 
    		
    		Object constraintValues[] = { getPTJID() };

    update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}

public final Integer getNextPTJID() {
    java.sql.PreparedStatement pstmt = null;
    java.sql.ResultSet rset = null;

    int nextPTJID = -1;

    try {
        pstmt = getDbConnection().prepareStatement( "SELECT MIN(PTJID) FROM " + TABLE_NAME );
        rset = pstmt.executeQuery();

        if (rset.next())
        {
        	int tempID = rset.getInt(1) - 1;
        	if( tempID < -1)	//only load a min if it's lower, can't use possitive numbers becuase ptj's come across with possitive numbers
        		nextPTJID = tempID;
        }
    }
    catch (java.sql.SQLException e) {
        CTILogger.error( e.getMessage(), e );
    }
    finally {
        try {
            if (rset != null) rset.close();
            if (pstmt != null) pstmt.close();
        }
        catch (java.sql.SQLException e2) {}
    }

    return new Integer( nextPTJID );
}


public static ArrayList<SAMToCRS_PTJ> getAllCurrentPTJEntries()
{
    ArrayList<SAMToCRS_PTJ> changes = new ArrayList<SAMToCRS_PTJ>();
    
    //Join the additional meter number table to load all additional meter numbers too.
    SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME, CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            for( int i = 0; i < stmt.getRowCount(); i++ )
            {
            	SAMToCRS_PTJ currentEntry = new SAMToCRS_PTJ();
                currentEntry.setPTJID( new Integer(stmt.getRow(i)[0].toString()));
                currentEntry.setPremiseNumber( new Integer(stmt.getRow(i)[1].toString()));
                currentEntry.setDebtorNumber( stmt.getRow(i)[2].toString());
                currentEntry.setWorkOrderNumber( stmt.getRow(i)[3].toString());
                currentEntry.setStatusCode( stmt.getRow(i)[4].toString());
                currentEntry.setDateTime_Completed(new Date(((java.sql.Timestamp)stmt.getRow(i)[5]).getTime()));
                currentEntry.setStarsUserName( stmt.getRow(i)[6].toString());
                currentEntry.setExtract(new Character(stmt.getRow(i)[7].toString().charAt(0)));
                
                changes.add(currentEntry);
            }
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return changes;
}

public String getDebtorNumber() {
	return debtorNumber;
}

public void setDebtorNumber(String debtorNumber) {
	this.debtorNumber = debtorNumber;
}

public Integer getPremiseNumber() {
	return premiseNumber;
}

public void setPremiseNumber(Integer premiseNumber) {
	this.premiseNumber = premiseNumber;
}

public Integer getPTJID() {
	return ptjID;
}

public void setPTJID(Integer ptjID) {
	this.ptjID = ptjID;
}


public String getStarsUserName() {
	return starsUserName;
}

public void setStarsUserName(String starsUserName) {
	this.starsUserName = starsUserName;
}

public String getStatusCode() {
	return statusCode;
}

public void setStatusCode(String statusCode) {
	this.statusCode = statusCode;
}

public String getWorkOrderNumber() {
	return workOrderNumber;
}

public void setWorkOrderNumber(String workOrderNumber) {
	this.workOrderNumber = workOrderNumber;
}

public Date getDateTime_Completed() {
	return dateTime_Completed;
}

public void setDateTime_Completed(Date timestamp) {
	this.dateTime_Completed = timestamp;
}

public Character getExtract() {
	if( extract == null)
		extract = new Character(' ');
	return extract;
}

public void setExtract(Character extract) {
	this.extract = extract;
}

/**
 * @param stateYukDefID
 * @param workOrderBase
 * @param liteStarsCustAcctInfo
 * @param liteStarsEC
 * @param userID
 * @param meterNumber The meterNumber for SerialNumber lookup, when null the workOrderBase.Description field is parsed for it.  
 * @throws TransactionException
 */
public static void handleCRSIntegration(int stateYukDefID, WorkOrderBase workOrderBase, LiteStarsCustAccountInformation liteStarsCustAcctInfo, LiteStarsEnergyCompany liteStarsEC, int userID, String meterNumber) throws TransactionException
{
    YukonListEntry workTypeEntry = DaoFactory.getYukonListDao().getYukonListEntry(workOrderBase.getWorkOrderBase().getWorkTypeID().intValue());
    
//  Only run through the SAMToCRS process if workOrder came from CRS Integration.  The CRS PJT number is stored in the AdditionalOrderNumber field
    if(workOrderBase.getWorkOrderBase().getAdditionalOrderNumber() != null && 
       workOrderBase.getWorkOrderBase().getAdditionalOrderNumber().length() > 0)
    {
        String samToCrsStatus = null;
        if( stateYukDefID == YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_PROCESSED)
            samToCrsStatus = "P";
        else if ( stateYukDefID == YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_CANCELLED )
            samToCrsStatus = "X";
//      else if ( stateYukDefID == YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_COMPLETED)
//          samToCrsStatus = "C";
        
        if (samToCrsStatus != null && workTypeEntry.getYukonDefID() != YukonListEntryTypes.YUK_DEF_ID_SERV_TYPE_MAINTENANCE)
        {
            SAMToCRS_PTJ samToCrs_ptj = new SAMToCRS_PTJ();
            samToCrs_ptj.setDebtorNumber(liteStarsCustAcctInfo.getCustomer().getAltTrackingNumber());
            samToCrs_ptj.setPremiseNumber(Integer.valueOf(liteStarsCustAcctInfo.getCustomerAccount().getAccountNumber()));
            try{
                samToCrs_ptj.setPTJID(Integer.valueOf(workOrderBase.getWorkOrderBase().getAdditionalOrderNumber()));
            }catch(NumberFormatException nfe){}
            samToCrs_ptj.setStarsUserName(DaoFactory.getYukonUserDao().getLiteYukonUser(userID).getUsername());
            samToCrs_ptj.setStatusCode(samToCrsStatus);
            samToCrs_ptj.setDateTime_Completed(new Date());
            samToCrs_ptj.setWorkOrderNumber(workOrderBase.getWorkOrderBase().getOrderNumber());
            Transaction.createTransaction(Transaction.INSERT, samToCrs_ptj).execute();
        }
    }
    
    /* Handle config for Release Activation PTJ*/
    if( (stateYukDefID == YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_RELEASED && 
         workTypeEntry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_SERV_TYPE_ACTIVATION ) ||
        (stateYukDefID == YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_PROCESSED && 
         workTypeEntry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_SERV_TYPE_DEACTIVATION) )            
    {
        int devStatYukDefID = YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL;    //ACTIVATION
        String switchCommandStr = SwitchCommandQueue.SWITCH_COMMAND_ENABLE;     //ACTIVATION
        if( workTypeEntry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_SERV_TYPE_DEACTIVATION )
        {
            devStatYukDefID = YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL;
            switchCommandStr = SwitchCommandQueue.SWITCH_COMMAND_DISABLE;
        }
        
        if( meterNumber == null || meterNumber.length() <= 0)
        {
            int beginIndex = workOrderBase.getWorkOrderBase().getDescription().indexOf("Meter Number: ");    //"Meter Number: " is hardcoded in the creation of the PTJ using CRSIntegrator
            int endIndex = (beginIndex >= 0 ? workOrderBase.getWorkOrderBase().getDescription().indexOf(";", beginIndex + 14) : beginIndex);
            if( beginIndex > -1 && endIndex > -1)
                meterNumber = workOrderBase.getWorkOrderBase().getDescription().substring(beginIndex+14, endIndex);
        }
        
        MeterHardwareBase meterHardwareBase = MeterHardwareBase.retrieveMeterHardwareBase(workOrderBase.getWorkOrderBase().getAccountID(), meterNumber, liteStarsEC.getEnergyCompanyId());
        if( meterHardwareBase != null)
        {
            ArrayList<LMHardwareBase> lmHardwares = MeterHardwareBase.retrieveAssignedSwitches(meterHardwareBase.getInventoryBase().getInventoryID().intValue());
            if( lmHardwares.size() > 0)
            {
                YukonListEntry devStateEntry = null;
                YukonSelectionList invDevStateList = liteStarsEC.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS);
                for (int i = 0; i < invDevStateList.getYukonListEntries().size(); i++)
                {
                    if( ((YukonListEntry)invDevStateList.getYukonListEntries().get(i)).getYukonDefID() == devStatYukDefID)
                    {
                        devStateEntry = (YukonListEntry)invDevStateList.getYukonListEntries().get(i);
                        break;
                    }
                    
                }
                for (int i = 0; i < lmHardwares.size(); i++)
                {
                    try{
                        //Retrieve the lmhardwarebase data object
                        LMHardwareBase hardware = lmHardwares.get(i);
                        com.cannontech.database.data.stars.hardware.LMHardwareBase lmHardwareBase = new com.cannontech.database.data.stars.hardware.LMHardwareBase();
                        lmHardwareBase.setInventoryID(hardware.getInventoryID());
                        lmHardwareBase.setLMHardwareBase(hardware);
                        lmHardwareBase = (com.cannontech.database.data.stars.hardware.LMHardwareBase)Transaction.createTransaction(Transaction.RETRIEVE, lmHardwareBase).execute();
                        
                        //Update the lmHardwareBase data object
                        lmHardwareBase.getInventoryBase().setCurrentStateID(new Integer(devStateEntry.getEntryID()));
                        lmHardwareBase = (com.cannontech.database.data.stars.hardware.LMHardwareBase)Transaction.createTransaction(Transaction.UPDATE, lmHardwareBase).execute();
                        
                        //Log the inventory (lmHardwarebase) state change.
                        EventUtils.logSTARSEvent(userID, EventUtils.EVENT_CATEGORY_INVENTORY, lmHardwareBase.getInventoryBase().getCurrentStateID().intValue(), lmHardwareBase.getInventoryBase().getInventoryID().intValue(), null);

                        //Add a config to the queue to deactivate the switch
                        SwitchCommand switchCommand = new SwitchCommandQueue.SwitchCommand();
                        switchCommand.setAccountID(workOrderBase.getWorkOrderBase().getAccountID());
                        switchCommand.setCommandType(switchCommandStr);
                        switchCommand.setEnergyCompanyID(liteStarsEC.getEnergyCompanyId());
                        switchCommand.setInfoString("Released " + workTypeEntry.getEntryText() + " Work Order");
                        switchCommand.setInventoryID(lmHardwareBase.getInventoryBase().getInventoryID().intValue());
                        SwitchCommandQueue.getInstance().addCommand(switchCommand, true);
                        
                    }catch(TransactionException te)
                    {
                        te.printStackTrace();
                    }
                }
            }
        }
    }
}

}