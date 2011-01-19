package com.cannontech.stars.web.action;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.hardware.StaticLoadGroupMapping;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.service.StarsTwoWayLcrYukonDeviceAssignmentService;
import com.cannontech.stars.dr.hardware.exception.StarsTwoWayLcrYukonDeviceAssignmentException;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedule;
import com.cannontech.stars.dr.util.YukonListEntryHelper;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsLMProgram;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CreateLMHardwareAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;

			StarsOperation operation = null;
			if (req.getParameter("InvID") != null) {
				// Request from CreateHardware.jsp
				LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
				operation = getRequestOperation( req, energyCompany );
			}
			else {
				// Request redirected from InventoryManager
				operation = (StarsOperation) session.getAttribute(InventoryManagerUtil.STARS_INVENTORY_OPERATION);
				session.removeAttribute( InventoryManagerUtil.STARS_INVENTORY_OPERATION );
			}
			
			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (WebClientException we) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, we.getMessage() );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
		}

		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
		StarsOperation respOper = new StarsOperation();
        
		try {
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );

			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) session.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			if (liteAcctInfo == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
        	
			LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        	
			StarsCreateLMHardware createHw = reqOper.getStarsCreateLMHardware();
			LiteInventoryBase liteInv = null;
            
			try {
				liteInv = addInventory( createHw, liteAcctInfo, energyCompany );
			}
			catch (WebClientException e) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, e.getMessage()) );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			// Response will be handled here, instead of in parse()
			StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteAcctInfo.getAccountID() );
			if (starsAcctInfo != null) {
				StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
				parseResponse( createHw, starsInv, starsAcctInfo, session );
			}
            
			respOper.setStarsSuccess( new StarsSuccess() );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Failed to create the hardware") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			catch (Exception e2) {
				CTILogger.error( e2.getMessage(), e2 );
			}
		}
        
		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#parse(SOAPMessage, SOAPMessage, HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
		try {
			StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );
			
			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}

			StarsSuccess success = operation.getStarsSuccess();
			if (success == null)
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static StarsOperation getRequestOperation(HttpServletRequest req, LiteStarsEnergyCompany energyCompany) throws WebClientException {
		StarsCreateLMHardware createHw = new StarsCreateLMHardware();
		InventoryManagerUtil.setStarsInv( createHw, req, energyCompany );
		
		StarsOperation operation = new StarsOperation();
		operation.setStarsCreateLMHardware( createHw );
		return operation;
	}
	
	/**
	 * Add a hardware to a customer account or inventory.
	 * To add to a customer account: if the hardware doesn't exist before,
	 * then create it and add to the inventory. If it's in the warehouse,
	 * just add it to the account. If it's currently assigned to another account,
	 * remove it from that account first, then add it to the new account. 
	 */
	public static LiteInventoryBase addInventory(StarsCreateLMHardware createHw, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
		throws WebClientException
	{
		if (createHw.getLMHardware() != null && createHw.getLMHardware().getManufacturerSerialNumber().equals(""))
			throw new WebClientException( "Serial # cannot be empty" );
		
		try {
			com.cannontech.database.db.stars.hardware.InventoryBase invDB = new com.cannontech.database.db.stars.hardware.InventoryBase();
			com.cannontech.database.data.stars.hardware.LMHardwareBase hw = null;
			
			if (createHw.getLMHardware() != null) {
				hw = new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				hw.setInventoryBase( invDB );
				
				com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hw.getLMHardwareBase();
				hwDB.setManufacturerSerialNumber( createHw.getLMHardware().getManufacturerSerialNumber() );
				hwDB.setLMHardwareTypeID( new Integer(createHw.getDeviceType().getEntryID()) );
				hwDB.setRouteID( new Integer(createHw.getLMHardware().getRouteID()) );
			}
			
			StarsFactory.setInventoryBase( invDB, createHw );
			
			int categoryID = InventoryUtils.getInventoryCategoryID(createHw.getDeviceType().getEntryID(), energyCompany);
			if( categoryID == 0 && createHw.getCategory() != null && createHw.getCategory().equalsIgnoreCase("Non Yukon Meter"))
			{
				 YukonListEntry categoryEntry = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_NON_YUKON_METER);
			     if( categoryEntry != null)
			    	 categoryID = categoryEntry.getEntryID();
			}
			invDB.setCategoryID( new Integer(categoryID) );
			
			if (liteAcctInfo != null)
				invDB.setAccountID( new Integer(liteAcctInfo.getAccountID()) );
			
			LiteInventoryBase liteInv = null;
			int invID = createHw.getInventoryID();
			
			if (invID == -1) {
				// Create new hardware
				if (createHw.getLMHardware() != null) {
					hw.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
					hw = Transaction.createTransaction( Transaction.INSERT, hw ).execute();
					
					LiteStarsLMHardware liteHw = new LiteStarsLMHardware();
					StarsLiteFactory.setLiteStarsLMHardware( liteHw, hw );
					
					if (liteHw.isThermostat()) {
						// Get the energy company default schedule and save a copy for this
						// thermostat
						
//						ThermostatScheduleDao thermostatScheduleDao = 
//							YukonSpringHook.getBean(
//									"thermostatScheduleDao", ThermostatScheduleDao.class);
						
						int lmHardwareTypeID = liteHw.getLmHardwareTypeID();
						int typeDefinitionId = YukonListEntryHelper.getYukonDefinitionId(
													energyCompany, 
													YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, 
													lmHardwareTypeID);
						HardwareType hardwareType = HardwareType.valueOf(typeDefinitionId);
						AccountThermostatScheduleDao accountThermostatScheduleDao = YukonSpringHook.getBean("accountThermostatScheduleDao", AccountThermostatScheduleDao.class);
						SchedulableThermostatType schedulableThermostatType = SchedulableThermostatType.getByHardwareType(hardwareType);
						AccountThermostatSchedule schedule = accountThermostatScheduleDao.getEnergyCompanyDefaultScheduleByType(energyCompany.getLiteID(), schedulableThermostatType);
//						ThermostatSchedule schedule = 
//							thermostatScheduleDao.getCopyOfEnergyCompanyDefaultSchedule(
//								energyCompany, hardwareType);
						
						schedule.setAccountId(liteHw.getAccountID());
						schedule.setScheduleName(liteHw.getDeviceLabel());
			            accountThermostatScheduleDao.save(schedule);
//						schedule.setInventoryId(liteHw.getInventoryID());
//						thermostatScheduleDao.save(schedule, energyCompany);
						
					}
					liteInv = liteHw;
				}
				else {
					com.cannontech.database.data.stars.hardware.InventoryBase inventory =
							new com.cannontech.database.data.stars.hardware.InventoryBase();
					inventory.setInventoryBase( invDB );
					inventory.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
					
					inventory = Transaction.createTransaction( Transaction.INSERT, inventory ).execute();
					
					liteInv = new LiteInventoryBase();
					StarsLiteFactory.setLiteInventoryBase( liteInv, invDB );
				}
			}
			else {
			    StarsSearchDao starsSearchDao = 
					YukonSpringHook.getBean("starsSearchDao", StarsSearchDao.class);
				
				// Add hardware in the inventory to customer account
			    try {
			        liteInv = starsSearchDao.getById(invID, energyCompany);
			    } catch (ObjectInOtherEnergyCompanyException e) {
			        throw new WebClientException("The hardware is found in another energy company [" + e.getEnergyCompany().getName() + "]");
			    }
				
				if (liteInv.getAccountID() > 0) {
					// Remove hardware from previous account
					LiteStarsCustAccountInformation litePrevAccount = energyCompany.getCustAccountInformation( liteInv.getAccountID(), true );
					
					StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
					deleteHw.setInventoryID( invID );
					deleteHw.setDeleteFromInventory( false );
					
					DeleteLMHardwareAction.removeInventory(deleteHw, litePrevAccount, energyCompany);
					
					// The liteInv object is changed in the method above, so we need to retrieve it again
					liteInv = starsSearchDao.getById(invID, energyCompany);
					
					StarsCustAccountInformation starsPrevAccount = energyCompany.getStarsCustAccountInformation( litePrevAccount.getAccountID() );
					if (starsPrevAccount != null)
						DeleteLMHardwareAction.parseResponse( invID, starsPrevAccount );
				}
            	
				if (createHw.getLMHardware() != null && liteInv instanceof LiteStarsLMHardware) {
					hw.setInventoryID( new Integer(invID) );
					hw = Transaction.createTransaction( Transaction.UPDATE, hw ).execute();
            		
					StarsLiteFactory.setLiteStarsLMHardware( (LiteStarsLMHardware)liteInv, hw );
				}
				else {
					invDB.setInventoryID( new Integer(invID) );
					invDB = Transaction.createTransaction( Transaction.UPDATE, invDB ).execute();
					
					StarsLiteFactory.setLiteInventoryBase( liteInv, invDB );
				}
			}
			
			if (liteAcctInfo != null) {
				if (liteInv instanceof LiteStarsLMHardware) {
					LiteStarsLMHardware liteHw = (LiteStarsLMHardware) liteInv;
					
                        //TODO Add auto assignment here
                    
					if(VersionTools.staticLoadGroupMappingExists())
                    {
                        com.cannontech.database.db.stars.hardware.LMHardwareConfiguration config =
                            new com.cannontech.database.db.stars.hardware.LMHardwareConfiguration();
                        int appID = 0;
                        int appCatID = 0;
                        LiteStarsAppliance keeperApp = null;
                        if(createHw.getLMHardware().getStarsLMHardwareConfigCount() < 1) {
                            for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
                                LiteStarsAppliance liteApp = liteAcctInfo.getAppliances().get(j);
                                if(liteApp.getInventoryID() < 1) {
                                    appID = liteApp.getApplianceID();
                                    appCatID = liteApp.getApplianceCategory().getApplianceCategoryId();
                                    keeperApp = liteApp;
                                    /*
                                     * Let's keep looping so we get the most recently created appliance since we are sort of 
                                     * guessing anyway.  Newer should be safer.
                                     */
                                }
                            }
                        }
                        else {
                            for (int i = 0; i < createHw.getLMHardware().getStarsLMHardwareConfigCount(); i++) {
                                StarsLMHardwareConfig starsConfig = createHw.getLMHardware().getStarsLMHardwareConfig(i);                                
                                for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
                                    LiteStarsAppliance liteApp = liteAcctInfo.getAppliances().get(j);
                                    if (liteApp.getApplianceID() == starsConfig.getApplianceID() && liteApp.getAccountID() == liteAcctInfo.getAccountID()) {
                                        appID = liteApp.getApplianceID();
                                        appCatID = liteApp.getApplianceCategory().getApplianceCategoryId();
                                        keeperApp = liteApp;
                                    }
                                }
                            }
                        }

                        //get zipCode
                        String zip = energyCompany.getAddress(liteAcctInfo.getAccountSite().getStreetAddressID()).getZipCode();
                        if(zip.length() > 5)
                            zip = zip.substring(0, 5);
                        //get ConsumptionType
                        LiteCustomer cust = liteAcctInfo.getCustomer();
                        Integer consumptionType = -1;
                        if(cust instanceof LiteCICustomer && cust.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI)
                            consumptionType = ((LiteCICustomer)cust).getCICustType();
                        StaticLoadGroupMapping mapping = StaticLoadGroupMapping.getAStaticLoadGroupMapping(appCatID, zip, consumptionType, createHw.getDeviceType().getEntryID());
                        if(mapping == null) {
                            CTILogger.error("A static mapping could not be determined for serial number " + liteHw.getManufacturerSerialNumber() 
                                + ".  ApplianceCategoryID=" + appCatID + ", ZipCode=" + zip + ", ConsumptionTypeID=" 
                                + consumptionType + ",SwitchTypeID=" + createHw.getDeviceType().getEntryID());
                        }
                        else {
                            /*
                             * All this to set a load group ID.
                             */
                            config.setApplianceID(appID);
                            config.setInventoryID(createHw.getInventoryID());
                            config.setAddressingGroupID(mapping.getLoadGroupID());
                            config = Transaction.createTransaction( Transaction.INSERT, config ).execute();
                            if(keeperApp != null) {
                                keeperApp.setAddressingGroupID(config.getAddressingGroupID());
                                keeperApp.setInventoryID(config.getInventoryID());
                                keeperApp.setLoadNumber(config.getLoadNumber());
                            }
                        }
                    }
                    else {
                        for (int i = 0; i < createHw.getLMHardware().getStarsLMHardwareConfigCount(); i++) {
                            StarsLMHardwareConfig starsConfig = createHw.getLMHardware().getStarsLMHardwareConfig(i);
    						if (starsConfig.getGroupID() == 0) continue;
                			
    						com.cannontech.database.db.stars.hardware.LMHardwareConfiguration config =
    								new com.cannontech.database.db.stars.hardware.LMHardwareConfiguration();
    						config.setInventoryID( invDB.getInventoryID() );
    						config.setApplianceID( new Integer(starsConfig.getApplianceID()) );
    						config.setAddressingGroupID( new Integer(starsConfig.getGroupID()) );
    						config.setLoadNumber( new Integer(starsConfig.getLoadNumber()) );
    						
    						config = Transaction.createTransaction( Transaction.INSERT, config ).execute();
                			
    						for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
    							LiteStarsAppliance liteApp = liteAcctInfo.getAppliances().get(j);
    							if (liteApp.getApplianceID() == starsConfig.getApplianceID()) {
    								liteApp.setInventoryID( liteHw.getInventoryID() );
    								liteApp.setAddressingGroupID( config.getAddressingGroupID().intValue() );
    								liteApp.setLoadNumber( config.getLoadNumber().intValue() );
                					
    								for (int k = 0; k < liteAcctInfo.getPrograms().size(); k++) {
    									LiteStarsLMProgram liteProg = liteAcctInfo.getPrograms().get(k);
    									if (liteProg.getProgramID() == liteApp.getProgramID()) {
    										liteProg.setGroupID( config.getAddressingGroupID().intValue() );
    										break;
    									}
    								}
    								break;
    							}
    						}
    					}
                    }
    			}
				
				int hwEventEntryID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE ).getEntryID();
				int installActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL ).getEntryID();
				
				// Add "Install" to hardware events
				com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
				com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
				com.cannontech.database.db.stars.event.LMCustomerEventBase eventBaseDB = event.getLMCustomerEventBase();
				
				eventBaseDB.setEventTypeID( new Integer(hwEventEntryID) );
				eventBaseDB.setActionID( new Integer(installActID) );
				eventBaseDB.setEventDateTime( new Date(liteInv.getInstallDate()) );
				if (createHw.getInstallationNotes() != null)
					eventBaseDB.setNotes( createHw.getInstallationNotes() );
				eventDB.setInventoryID( invDB.getInventoryID() );
				event.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
				
				event = Transaction.createTransaction( Transaction.INSERT, event ).execute();
				
				liteInv.updateDeviceStatus();
				
				if (createHw.getDeviceStatus() != null) {
					int statusDefID = DaoFactory.getYukonListDao().getYukonListEntry( createHw.getDeviceStatus().getEntryID() ).getYukonDefID();
					if (statusDefID != liteInv.getDeviceStatus()) {
						// Add customer event to match the device status
						event = null;
						
						if (statusDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL) {
							// If device status is available, add "Activation Completed" event
							int activActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED ).getEntryID();
							
							event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
							eventDB = event.getLMHardwareEvent();
							eventBaseDB = event.getLMCustomerEventBase();
							
							eventBaseDB.setEventTypeID( new Integer(hwEventEntryID) );
							eventBaseDB.setActionID( new Integer(activActID) );
							eventBaseDB.setEventDateTime( new Date() );
							eventBaseDB.setNotes( "Event added to match the device status" );
							eventDB.setInventoryID( invDB.getInventoryID() );
							event.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
							
							event = Transaction.createTransaction( Transaction.INSERT, event ).execute();
						}
						else if (statusDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL) {
							// If device status is unavailable, add "Termination" event
							int termActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION ).getEntryID();
							
							event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
							eventDB = event.getLMHardwareEvent();
							eventBaseDB = event.getLMCustomerEventBase();
							
							eventBaseDB.setEventTypeID( new Integer(hwEventEntryID) );
							eventBaseDB.setActionID( new Integer(termActID) );
							eventBaseDB.setEventDateTime( new Date() );
							eventBaseDB.setNotes( "Event added to match the device status" );
							eventDB.setInventoryID( invDB.getInventoryID() );
							event.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
							
							event = Transaction.createTransaction( Transaction.INSERT, event ).execute();
						}
						else if (statusDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL) {
							// If device status is temporary unavailable, add "Temp Termination" event
							int tempTermActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION ).getEntryID();
							
							event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
							eventDB = event.getLMHardwareEvent();
							eventBaseDB = event.getLMCustomerEventBase();
							
							eventBaseDB.setEventTypeID( new Integer(hwEventEntryID) );
							eventBaseDB.setActionID( new Integer(tempTermActID) );
							eventBaseDB.setEventDateTime( new Date() );
							eventBaseDB.setNotes( "Event added to match the device status" );
							eventDB.setInventoryID( invDB.getInventoryID() );
							event.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
							
							event = Transaction.createTransaction( Transaction.INSERT, event ).execute();
						}
						
						if (event != null) {
							liteInv.updateDeviceStatus();
						}
					}
				}
				
				StarsLiteFactory.extendLiteInventoryBase( liteInv, energyCompany );
                List<Integer> inventories = liteAcctInfo.getInventories();
                if (!inventories.contains(invDB.getInventoryID())){
                    inventories.add( invDB.getInventoryID() );
                }
			}
			
			// TWO WAY LCR DEVICE ASSIGNMENT
            if (InventoryUtils.isTwoWayLcr(createHw.getDeviceType().getEntryID())) {
            	StarsTwoWayLcrYukonDeviceAssignmentService starsTwoWayLcrYukonDeviceAssignmentService = YukonSpringHook.getBean("starsTwoWayLcrYukonDeviceAssignmentService", StarsTwoWayLcrYukonDeviceAssignmentService.class);
            	starsTwoWayLcrYukonDeviceAssignmentService.assignTwoWayLcrDevice(createHw, liteInv, energyCompany);
            }
			
			return liteInv;
		}
		catch (Exception e) {
			if (e instanceof WebClientException)
				throw (WebClientException)e;
			else if (liteAcctInfo != null)
				throw new WebClientException( "Failed to add the hardware to the customer account", e );
			else if (e instanceof StarsTwoWayLcrYukonDeviceAssignmentException)
				throw new WebClientException(e.getMessage(), e);
			else
				throw new WebClientException( "Failed to create the hardware", e );
		}
	}
	
	public static void parseResponse(StarsCreateLMHardware createHw, StarsInventory starsInv, StarsCustAccountInformation starsAcctInfo, HttpSession session) {
		StarsInventories starsInvs = starsAcctInfo.getStarsInventories();
		
		String invLabel = ServletUtils.getInventoryLabel( starsInv );
		int invNo = 0;
		for (;invNo < starsInvs.getStarsInventoryCount(); invNo++) {
			String label = ServletUtils.getInventoryLabel( starsInvs.getStarsInventory(invNo) );
			if (label.compareTo(invLabel) > 0) break;
		}
		
		starsInvs.addStarsInventory( invNo, starsInv );
		
		if (createHw.getLMHardware() != null) {
			for (int i = 0; i < createHw.getLMHardware().getStarsLMHardwareConfigCount(); i++) {
				StarsLMHardwareConfig config = createHw.getLMHardware().getStarsLMHardwareConfig(i);
				
				for (int j = 0; j < starsAcctInfo.getStarsAppliances().getStarsApplianceCount(); j++) {
					StarsAppliance app = starsAcctInfo.getStarsAppliances().getStarsAppliance(j);
					if (app.getApplianceID() == config.getApplianceID()) {
						app.setInventoryID( starsInv.getInventoryID() );
						
						for (int k = 0; k < starsAcctInfo.getStarsLMPrograms().getStarsLMProgramCount(); k++) {
							StarsLMProgram prog = starsAcctInfo.getStarsLMPrograms().getStarsLMProgram(k);
							if (prog.getProgramID() == app.getProgramID()) {
								prog.setGroupID( config.getGroupID() );
								break;
							}
						}
						break;
					}
				}
			}
		}
		
		session.removeAttribute( InventoryManagerUtil.STARS_INVENTORY_TEMP );
		if (session.getAttribute(ServletUtils.ATT_REDIRECT) == null)
			session.setAttribute( ServletUtils.ATT_REDIRECT, "/operator/Consumer/Inventory.jsp?InvNo=" + String.valueOf(invNo) );
	}

}
