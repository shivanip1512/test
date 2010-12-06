package com.cannontech.dataCleanup;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.action.DeleteLMHardwareAction;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class DeleteInventory {

	private Logger log = YukonLogManager.getLogger(DeleteInventory.class);
	private String appName = "DeleteInventory";
	private ECMappingDao ecMappingDao;
	private StarsCustAccountInformationDao starsCustAccountInformationDao; 
	private StarsInventoryBaseDao starsInventoryBaseDao;
	
    private final static Function<String, Integer> stringToIntegerTransformer = new Function<String, Integer>() {
        @Override
        public Integer apply(String from) {
            return Integer.valueOf(from);
        }
    };
	        
	/**
	 * PowerFailPointCreate constructor comment.
	 */
	public DeleteInventory() 
	{
		super();
		init();
	}

	public void init() {
        YukonSpringHook.setDefaultContext("com.cannontech.context.tools");
        ecMappingDao = YukonSpringHook.getBean("ecMappingDao", ECMappingDao.class);
        starsCustAccountInformationDao = YukonSpringHook.getBean("starsCustAccountInformationDao", StarsCustAccountInformationDao.class);
        starsInventoryBaseDao = YukonSpringHook.getBean("starsInventoryBaseDao", StarsInventoryBaseDao.class);
        
        CtiUtilities.setDefaultApplicationName(appName);
        CtiUtilities.setRunningAsClient();
        CTILogger.info(appName + " starting...");
	}
	
	/**
	 * Main. Start the Power Fail Point creation/insertion process.
	 * Creation date: (1/10/2001 11:18:55 PM)
	 * @param args java.lang.String[]
	 */
	public static void main(String[] args)  {

		DeleteInventory deleteInventory = new DeleteInventory();

		if (args.length > 0) {
			String filename = args[0];
			deleteInventory.delete(filename);
		} else {
			deleteInventory.log.error("Must supply the fileName containing inventoryIds to run.");
		}
		YukonSpringHook.shutdownContext();
		System.exit(0);
	}
	
	public void delete(String filename) {

		List<Integer> inventoryIds;
		try {
			List<String> inventoryIdStrings = FileUtils.readLines(new File(filename));
			log.info("Loaded " + inventoryIdStrings.size() + " InventoryIds from file: " + filename);
			inventoryIds = Lists.transform(inventoryIdStrings, stringToIntegerTransformer);
			log.info("Start deletion of InventoryIds: " + inventoryIds.toString());
			
			List<LiteInventoryBase> liteInventoryBases = starsInventoryBaseDao.getByIds(inventoryIds);
			for (LiteInventoryBase liteInventoryBase : liteInventoryBases) {
				deleteInventory(liteInventoryBase);
			}
		} catch (IOException e) {
			log.error(e);
		}
	}
		
	private void deleteInventory(LiteInventoryBase liteInventoryBase) {

	    log.info("Attempting to delete inventory "+ liteInventoryBase.getDeviceLabel() + " (InventoryId = "+liteInventoryBase.getInventoryID()+", AccountId = "+liteInventoryBase.getAccountID()+") ");
	    LiteStarsEnergyCompany energyCompany = ecMappingDao.getInventoryEC(liteInventoryBase.getInventoryID());
        LiteStarsCustAccountInformation liteCustomerAccount = null;
        try {
            liteCustomerAccount = starsCustAccountInformationDao.getById(liteInventoryBase.getAccountID(), energyCompany.getEnergyCompanyID());
        } catch (EmptyResultDataAccessException e) {
        	//do nothing
        }
        
		StarsDeleteLMHardware starsDeleteLMHardware = new StarsDeleteLMHardware();
		starsDeleteLMHardware.setDeleteFromInventory(true);
		starsDeleteLMHardware.setInventoryID(liteInventoryBase.getInventoryID());
		try {
            DeleteLMHardwareAction.removeInventory(starsDeleteLMHardware, liteCustomerAccount, energyCompany );
            log.info("Deleted the inventory "+ liteInventoryBase.getDeviceLabel() +" (InventoryId = "+liteInventoryBase.getInventoryID()+", AccountId = "+liteInventoryBase.getAccountID()+ ") ");
        } catch (WebClientException e) {
            log.error(e);
        }
	}
}