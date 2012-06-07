/*
 * Created on Apr 28, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteStarsLMHardware;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.UpdateLMHardwareConfigAction;
import com.cannontech.stars.web.action.YukonSwitchCommandAction;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ConfigSNRangeTask extends TimeConsumingTask {
	
	private final LiteStarsEnergyCompany energyCompany;
	private final HttpSession session;
	private final ConfigSNRangeTaskDTO dto;
	
	ArrayList invToConfig = null;
	ArrayList hwsToConfig = null;
	ArrayList hardwareSet = new ArrayList();
	int numSuccess = 0, numFailure = 0;
	int numToBeConfigured = 0;
	
	public ConfigSNRangeTask(LiteStarsEnergyCompany energyCompany, HttpSession session, ConfigSNRangeTaskDTO dto) {
		this.energyCompany = energyCompany;
		this.session = session;
		this.dto = dto;
	}

	@Override
    public String getProgressMsg() {
		if (dto.isConfigNow()) {
			if (status == STATUS_FINISHED && numFailure == 0) {
				if (invToConfig.size() == 1 && invToConfig.get(0) instanceof Long[]) {
				    Long[] snRange = (Long[]) invToConfig.get(0);
					return "The serial numbers " + snRange[1] + " to " + snRange[2] + " have been configured successfully.";
				}
				else
					return numSuccess + " devices have been configured successfully.";
			}
			else
				return numSuccess + " of " + numToBeConfigured + " devices have been configured.";
		}
		else {
			if (status == STATUS_FINISHED && numFailure == 0) {
				if (invToConfig.size() == 1 && invToConfig.get(0) instanceof Long[]) {
				    Long[] snRange = (Long[]) invToConfig.get(0);
					return "The serial numbers " + snRange[1] + " to " + snRange[2] + " have been saved to batch.";
				}
				else
					return numSuccess + " hardware configuration saved to batch.";
			}
			else
				return numSuccess + " of " + numToBeConfigured + " hardware configuration saved to batch.";
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
		
		invToConfig = (ArrayList) session.getAttribute( InventoryManagerUtil.SN_RANGE_TO_CONFIG );
		if (invToConfig == null) {
			status = STATUS_ERROR;
			errorMsg = "There is no hardware to configure";
			return;
		}
		
		status = STATUS_RUNNING;
		
		boolean searchMembers = DaoFactory.getAuthDao().checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS )
				&& energyCompany.hasChildEnergyCompanies();
		hwsToConfig = new ArrayList();
		try {
		    for (int i = 0; i < invToConfig.size(); i++) {
		        if (invToConfig.get(i) instanceof Long[]) {
		            Long[] snRange = (Long[]) invToConfig.get(i);
		            int devTypeDefID = DaoFactory.getYukonListDao().getYukonListEntry(snRange[0].intValue()).getYukonDefID();

		            if (!searchMembers) {
		                List<LiteStarsLMHardware> hwsInRange = InventoryUtils.getLMHardwareInRange( energyCompany, devTypeDefID, snRange[1], snRange[2] );
		                for (int j = 0; j < hwsInRange.size(); j++) {
		                    if (!hwsToConfig.contains( hwsInRange.get(j) ))
		                        hwsToConfig.add( hwsInRange.get(j) );
		                }
		            }
		            else {
		                List<LiteStarsEnergyCompany> descendants = ECUtils.getAllDescendants( energyCompany );
		                for (int j = 0; j < descendants.size(); j++) {
		                    LiteStarsEnergyCompany company = descendants.get(j);
		                    List<LiteStarsLMHardware> hwsInRange = InventoryUtils.getLMHardwareInRange( company, devTypeDefID, snRange[1], snRange[2] );
		                    for (int k = 0; k < hwsInRange.size(); k++) {
		                        if (!isHardwareContained( hwsToConfig, hwsInRange.get(k) ))
		                            hwsToConfig.add( new Pair(hwsInRange.get(k), company) );
		                    }
		                }
		            }
		        }
		        else if (invToConfig.get(i) instanceof Pair) {
		            if (!isHardwareContained( hwsToConfig, (LiteStarsLMHardware)((Pair)invToConfig.get(i)).getFirst() ))
		                hwsToConfig.add( invToConfig.get(i) );
		        }
		        else {
		            if (!hwsToConfig.contains( invToConfig.get(i) ))
		                hwsToConfig.add( invToConfig.get(i) );
		        }
		    }
        } catch (PersistenceException e){
            status = STATUS_ERROR;
            errorMsg = e.getMessage();
            return;
        }
		
		numToBeConfigured = hwsToConfig.size();
		if (numToBeConfigured == 0) {
			status = STATUS_ERROR;
			errorMsg = "There is no hardware to configure";
			return;
		}
		
		StarsLMConfiguration hwConfig = null;
		String options = null;
		
		if (dto.getUseConfig()) {
			// User has specified a new configuration
			if (dto.isUseHardwareAddressing()) {
			    hwConfig = dto.getHwConfig();
			}
			else {
				int groupID = dto.getGroup();
				options = "GroupID:" + groupID;
			}
		}
		
		if (dto.getRoute() != null) {
			int routeID = dto.getRoute().intValue();
			if (options != null)
				options += ";RouteID:" + routeID;
			else
				options = "RouteID:" + routeID;
		}
		
		for (int i = 0; i < hwsToConfig.size(); i++) {
			LiteStarsLMHardware liteHw = null;
			LiteStarsEnergyCompany company = null;
			if (hwsToConfig.get(i) instanceof Pair) {
				liteHw = (LiteStarsLMHardware) ((Pair)hwsToConfig.get(i)).getFirst();
				company = (LiteStarsEnergyCompany) ((Pair)hwsToConfig.get(i)).getSecond();
			}
			else {
				liteHw = (LiteStarsLMHardware) hwsToConfig.get(i);
				company = energyCompany;
			}
			
			try {
				if (hwConfig != null)
					UpdateLMHardwareConfigAction.updateLMConfiguration( hwConfig, liteHw, company );
				
				if (dto.isConfigNow()) {
					YukonSwitchCommandAction.sendConfigCommand(company, liteHw, true, options);
					
					if (liteHw.getAccountID() > 0) {
						StarsCustAccountInformation starsAcctInfo = company.getStarsCustAccountInformation( liteHw.getAccountID() );
						if (starsAcctInfo != null) {
							StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteHw, company );
							parseResponse( starsAcctInfo, starsInv );
						}
					}
				}
				else {
					SwitchCommandQueue.SwitchCommand cmd = new SwitchCommandQueue.SwitchCommand();
					cmd.setEnergyCompanyID( company.getLiteID() );
					cmd.setAccountID( liteHw.getAccountID() );
					cmd.setInventoryID( liteHw.getInventoryID() );
					cmd.setCommandType( SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE );
					cmd.setInfoString( options );
					
					SwitchCommandQueue.getInstance().addCommand( cmd, false );
				}
				
				numSuccess++;
			}
			catch (WebClientException e) {
				CTILogger.error( e.getMessage() , e );
				if (errorMsg == null) errorMsg = e.getMessage();
				hardwareSet.add( hwsToConfig.get(i) );
				numFailure++;
			}
			
			if (isCanceled) {
				status = STATUS_CANCELED;
				return;
			}
		}
		
		if (!dto.isConfigNow()) SwitchCommandQueue.getInstance().addCommand( null, true );
		
		String logMsg = "Serial Range:";
		for (int i = 0; i < invToConfig.size(); i++) {
			if (invToConfig.get(i) instanceof Long[]) {
			    Long[] snRange = (Long[]) invToConfig.get(i);
				logMsg += snRange[1] + " - " + snRange[2] + ",";
			}
			else {
				Object hwObj = invToConfig.get(i);
				if (hwObj instanceof Pair) hwObj = ((Pair)hwObj).getFirst();
				logMsg += ((LiteStarsLMHardware)hwObj).getManufacturerSerialNumber() + ",";
			}
		}
		if (options != null) logMsg += options;
		ActivityLogger.logEvent( user.getUserID(), ActivityLogActions.INVENTORY_CONFIG_RANGE, logMsg );
		
		status = STATUS_FINISHED;
		
		if (numFailure == 0)
			session.removeAttribute( InventoryManagerUtil.SN_RANGE_TO_CONFIG );
		session.removeAttribute( InventoryManagerUtil.INVENTORY_SET );
		
		if (numFailure > 0) {
			String resultDesc = "<span class='ConfirmMsg'>" + "Configuration of " + numSuccess + " devices " +
					((dto.isConfigNow())? "sent out" : "scheduled") + " successfully.</span><br>";
			resultDesc += "<span class='ErrorMsg'>" + numFailure + " hardware(s) failed (listed below).</span><br>";
			if (errorMsg != null)
				resultDesc += "<span class='ErrorMsg'>First error message: " + errorMsg + "</span><br>";
			
			session.setAttribute(InventoryManagerUtil.INVENTORY_SET_DESC, resultDesc);
			session.setAttribute(InventoryManagerUtil.INVENTORY_SET, hardwareSet);
			session.setAttribute(ServletUtils.ATT_REDIRECT, dto.getRedirect());
			if (dto.getConfirmOnMessagePage())
				session.setAttribute(ServletUtils.ATT_REFERRER, session.getAttribute(ServletUtils.ATT_MSG_PAGE_REFERRER));
		}
	}
	
	static boolean isHardwareContained(ArrayList hwList, LiteStarsLMHardware liteHw) {
		for (int i = 0; i < hwList.size(); i++) {
			Object hwObj = hwList.get(i);
			if (hwObj instanceof Pair) hwObj = ((Pair)hwObj).getFirst();
			if (hwObj.equals( liteHw )) return true;
		}
		return false;
	}

	private static void parseResponse(StarsCustAccountInformation starsAcctInfo, StarsInventory starsInv) {
        StarsInventories inventories = starsAcctInfo.getStarsInventories();
        
        for (int i = 0; i < inventories.getStarsInventoryCount(); i++) {
            StarsInventory inv = inventories.getStarsInventory(i);
            if (inv.getInventoryID() == starsInv.getInventoryID()) {
                inventories.setStarsInventory(i, starsInv);
                break;
            }
        }
    }
}
