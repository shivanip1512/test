/*
 * Created on Jan 29, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.ArrayList;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMProgram;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.ImportManager;
import com.cannontech.stars.web.servlet.SOAPServer;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ImportCustAccountsTask implements TimeConsumingTask {

	int status = STATUS_NOT_INIT;
	boolean isCanceled = false;
	String errorMsg = null;
	
	StarsYukonUser user = null;
	ArrayList fieldsList = null;
	ArrayList programs = null;
	
	int numAcctImported = 0;
	int numAcctAdded = 0;
	int numAcctUpdated = 0;
	
	public ImportCustAccountsTask (StarsYukonUser user, ArrayList fieldsList, ArrayList programs) {
		this.user = user;
		this.fieldsList = fieldsList;
		this.programs = programs;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getStatus()
	 */
	public int getStatus() {
		return status;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#cancel()
	 */
	public void cancel() {
		isCanceled = true;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		if (fieldsList != null) {
			String msg = (status == STATUS_FINISHED)?
					numAcctImported + " customer accounts imported successfully" :
					numAcctImported + " of " + fieldsList.size() + " customer accounts imported";
			msg += " (" + numAcctAdded + " added, " + numAcctUpdated + " updated)";
			return msg;
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getErrorMsg()
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (fieldsList == null) {
			status = STATUS_ERROR;
			errorMsg = "Fields list cannot be null";
			return;
		}
        
        status = STATUS_RUNNING;
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		String lineNo = null;
		java.sql.Connection conn = null;
		
		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			for (numAcctImported = 0; numAcctImported < fieldsList.size(); numAcctImported++) {
				String[] custFields = (String[]) fieldsList.get(numAcctImported);
				String[] invFields = new String[ ImportManager.NUM_INV_FIELDS ];
				System.arraycopy( custFields, ImportManager.NUM_ACCOUNT_FIELDS, invFields, 0, ImportManager.NUM_INV_FIELDS );
				
				lineNo = custFields[ ImportManager.IDX_LINE_NUM ];
				
				ArrayList enrProgs = new ArrayList( programs );
				
				if (invFields[ImportManager.IDX_ADDR_GROUP].length() > 0) {
					// Assign group ID to program enrollment
					for (int i = 0; i < programs.size(); i++) {
						int[] program = (int[]) programs.get(i);
						LiteLMProgram liteProg = energyCompany.getLMProgram( program[0] );
						
						int groupID = 0;
						if (liteProg.getGroupIDs() != null) {
							for (int j = 0; j < liteProg.getGroupIDs().length; j++) {
								if (PAOFuncs.getYukonPAOName( liteProg.getGroupIDs()[j] ).equalsIgnoreCase( invFields[ImportManager.IDX_ADDR_GROUP] ))
								{
									groupID = liteProg.getGroupIDs()[j];
									break;
								}
							}
						}
						
						int[] prog = new int[3];
						prog[0] = program[0];
						prog[1] = program[1];
						prog[2] = groupID;
						enrProgs.set(i, prog);
					}
				}
				
				LiteStarsCustAccountInformation liteAcctInfo = energyCompany.searchByAccountNumber( custFields[ImportManager.IDX_ACCOUNT_NO] );
				
				if (liteAcctInfo == null) {
					liteAcctInfo = ImportManager.newCustomerAccount( custFields, user, energyCompany );
					
					LiteInventoryBase liteInv = null;
					if (!invFields[ImportManager.IDX_SERIAL_NO].equalsIgnoreCase(""))
						liteInv = ImportManager.insertLMHardware( invFields, liteAcctInfo, energyCompany, conn );
					
					Integer invID = null;
					if (liteInv != null)
						invID = new Integer( liteInv.getInventoryID() );
					ImportManager.programSignUp( programs, liteAcctInfo, invID, energyCompany, conn );
					
					numAcctAdded++;
				}
				else {
					ImportManager.updateCustomerAccount( custFields, liteAcctInfo, energyCompany, conn );
					//updateLogin( fields, liteAcctInfo, energyCompany, session );
					
					if (!invFields[ImportManager.IDX_SERIAL_NO].equalsIgnoreCase(""))
					{
						if (invFields[ImportManager.IDX_HARDWARE_ACTION].equalsIgnoreCase( ImportManager.HARDWARE_ACTION_INSERT )) {
							ImportManager.insertLMHardware( invFields, liteAcctInfo, energyCompany, conn );
						}
						else if (invFields[ImportManager.IDX_HARDWARE_ACTION].equalsIgnoreCase( ImportManager.HARDWARE_ACTION_UPDATE )) {
							ImportManager.updateLMHardware( invFields, liteAcctInfo, energyCompany, conn );
						}
						else if (invFields[ImportManager.IDX_HARDWARE_ACTION].equalsIgnoreCase( ImportManager.HARDWARE_ACTION_REMOVE )) {
							ImportManager.removeLMHardware( invFields, liteAcctInfo, energyCompany, conn );
						}
						else {	// Hardware action field not specified
							if (liteAcctInfo.getInventories().size() == 0)
								ImportManager.insertLMHardware( invFields, liteAcctInfo, energyCompany, conn );
							else
								ImportManager.updateLMHardware( invFields, liteAcctInfo, energyCompany, conn );
						}
					}
					
					if (liteAcctInfo.getInventories().size() > 0) {
						Integer dftInvID = null;
						if (liteAcctInfo.getInventories().size() == 1)
							dftInvID = (Integer) liteAcctInfo.getInventories().get(0);
						ImportManager.programSignUp( programs, liteAcctInfo, dftInvID, energyCompany, conn );
					}
					
					numAcctUpdated++;
				}
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					return;
				}
			}
			
			status = STATUS_FINISHED;
		}
		catch (Exception e) {
			e.printStackTrace();
			status = STATUS_ERROR;
			
			if (lineNo != null)
				errorMsg = "Error encountered when processing line #" + lineNo;
			else
				errorMsg = "Failed to import customer accounts";
			if (e instanceof WebClientException)
				errorMsg += ": " + e.getMessage();
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
	}

}
