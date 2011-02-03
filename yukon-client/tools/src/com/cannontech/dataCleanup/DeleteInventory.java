package com.cannontech.dataCleanup;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.service.EnergyCompanyService;
import com.cannontech.stars.dr.hardware.service.HardwareService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.SystemUserContext;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class DeleteInventory {

	private Logger log = YukonLogManager.getLogger(DeleteInventory.class);
	private String appName = "DeleteInventory";
	private EnergyCompanyService energyCompanyService;
	private StarsInventoryBaseDao starsInventoryBaseDao;
	private HardwareService hardwareService;

	private final static Function<String, Integer> stringToIntegerTransformer = new Function<String, Integer>() {
		@Override
		public Integer apply(String from) {
			return Integer.valueOf(from);
		}
	};

	public DeleteInventory() {
		super();
		init();
	}

	public void init() {
		YukonSpringHook.setDefaultContext("com.cannontech.context.tools");
		energyCompanyService = YukonSpringHook.getBean("energyCompanyService", EnergyCompanyService.class);
		starsInventoryBaseDao = YukonSpringHook.getBean( "starsInventoryBaseDao", StarsInventoryBaseDao.class);
		hardwareService = YukonSpringHook.getBean("hardwareService", HardwareService.class);

		CtiUtilities.setDefaultApplicationName(appName);
		CtiUtilities.setRunningAsClient();
		CTILogger.info(appName + " starting...");
	}

	/**
	 * Read a fileName containing inventory id values. 
	 * Requires exactly one argument to be supplied. Else exit.
	 * @param args
	 *            args[0] - contains the filename to read list of inventory id values from.
	 */
	public static void main(String[] args) {

		DeleteInventory deleteInventory = new DeleteInventory();

		if (args.length == 1) {
			String filename = args[0];
			deleteInventory.delete(filename);
		} else {
			deleteInventory.log
					.error("Must supply the fileName containing inventoryIds to run.");
		}
		YukonSpringHook.shutdownContext();
		System.exit(0);
	}

	/**
	 * Read a list of inventory Id values from filename. Delete each inventory
	 * from the system.
	 * @param filename
	 */
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

	/**
	 * Helper method to delete an inventory object completely from the system.
	 * @param liteInventoryBase
	 */
	private void deleteInventory(LiteInventoryBase liteInventoryBase) {

		final int inventoryId = liteInventoryBase.getInventoryID();
		final int accountId = liteInventoryBase.getAccountID();

		log.info("Attempting to delete inventory " + liteInventoryBase.getDeviceLabel() + 
				" (InventoryId = " + inventoryId + ", AccountId = " + accountId + ") ");

		try {
			YukonEnergyCompany yukonEnergyCompany = energyCompanyService.getEnergyCompanyByInventoryId(inventoryId);
			hardwareService.deleteHardware(new SystemUserContext(), true, inventoryId, accountId, yukonEnergyCompany);

			log.info("Deleted the inventory " + liteInventoryBase.getDeviceLabel() + 
					" (InventoryId = " + inventoryId + ", AccountId = " + accountId + ") ");
		} catch (Exception e) {
			log.error(e);
			log.error("Failed to delete the inventory " + liteInventoryBase.getDeviceLabel() + 
					" (InventoryId = " + inventoryId + ", AccountId = " + accountId + ") ");
		}
	}
}