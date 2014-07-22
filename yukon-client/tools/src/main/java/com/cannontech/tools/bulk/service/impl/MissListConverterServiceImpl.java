package com.cannontech.tools.bulk.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.importer.ImportData;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.tools.bulk.service.MissListConverterService;

public class MissListConverterServiceImpl implements MissListConverterService {

    @Autowired private DeviceGroupService deviceGroupService;
	public void convert(File missListFile, File bulkImporterFile,
			String routeName, boolean importFlag) {

		// Gets all the device names of all the devices on the miss list.
		List<String> deviceNames = null;
		try {
			deviceNames = getDeviceNames(missListFile);
		} catch (IOException e) {
			CTILogger.error(e);
		}

		// Creates the new bulk importer file.
		List<ImportData> importDataList = generateBulkImporterData(bulkImporterFile, deviceNames, routeName);
		generateBulkImporterFile(bulkImporterFile, importDataList);

		// Submits the new file to the bulk importer
		if (importFlag) {
			for (int i = 0; i < importDataList.size(); i++) {
				ImportData currentEntry = (ImportData) importDataList.get(i);
				try {
					Transaction.createTransaction(Transaction.INSERT, currentEntry).execute();
				} catch (TransactionException e) {
					CTILogger.error(e);
				}
			}
		}
	}

	private void generateBulkImporterFile(File bulkImporterFile,
			List importDataList) {

		String headerLine = "Address,Name,RouteName,MeterNumber,CollectionGrp,AltGrp,TemplateName,BillGrp,SubstationName \n";
        
		try {
			FileWriter fw = new FileWriter(bulkImporterFile);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(headerLine);
			
			for (int i = 0; i < importDataList.size(); i++) {
				ImportData importData = (ImportData) importDataList.get(i);
				String line = getCsvFormat(importData);
				bw.write(line);
			}
			
			bw.close();

		} catch (IOException ioe) {
			CTILogger.error(ioe);
		}

	}

	/**
	 * 
	 * @param missListFile
	 * @return
	 * @throws IOException
	 */
	private List<String> getDeviceNames(File missListFile) throws IOException {

		List<String> deviceNames = new ArrayList<String>();
		FileReader fileReader = new FileReader(missListFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		String line;
		while ((line = bufferedReader.readLine()) != null) {
			deviceNames.add(line);
		}

		return deviceNames;
	}

	/**
	 * 
	 * @param bulkImporterFile
	 * @param deviceNames
	 * @return
	 */
	private List<ImportData> generateBulkImporterData(File bulkImporterFile,
			List<String> deviceNames, String routeName) {
		List<ImportData> importData = new ArrayList<ImportData>();
		for (int i = 0; i < deviceNames.size(); i++) {
			String deviceName = deviceNames.get(i);
			ImportData tempData = getImportDataInfo(deviceName, routeName);
			if(tempData != null){
				importData.add(tempData);
			} else {
				CTILogger.warn(deviceName+" was not added to the csv file due to the fact that it could not be found in the system.");
			}
		}

		return importData;
	}

	private ImportData getImportDataInfo(String deviceName, String routeName) {
		ImportData tempData = null;

		DeviceDao deviceDao = YukonSpringHook.getBean("deviceDao", DeviceDao.class);
        DeviceGroupProviderDao deviceGroupDao =  YukonSpringHook.getBean("deviceGroupDao", DeviceGroupProviderDao.class);
        DeviceGroupMemberEditorDao deviceGroupMemberEditorDao =  YukonSpringHook.getBean("deviceGroupMemberEditorDao", DeviceGroupMemberEditorDao.class);

        LiteYukonPAObject liteYukonPAObject = deviceDao.getLiteYukonPaobjectByDeviceName(deviceName);
        
        if (liteYukonPAObject != null) {
			tempData = new ImportData();
			tempData.setAddress(String.valueOf(liteYukonPAObject.getAddress()));
			tempData.setName(liteYukonPAObject.getPaoName());
			if (routeName != null) {
				tempData.setRouteName(routeName);
			} else {
				PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
				LiteYukonPAObject liteDevice = paoDao.getLiteYukonPAO(liteYukonPAObject.getRouteID());
				if(liteDevice != null) {
					tempData.setRouteName(liteDevice.getPaoName());
				} else {
					tempData.setRouteName(" ");
				}
			}
			tempData.setTemplateName(liteYukonPAObject.getPaoName());

            SimpleDevice yukonDevice = deviceDao.getYukonDevice(liteYukonPAObject);
            StoredDeviceGroup base = (StoredDeviceGroup)deviceGroupDao.getRootGroup();
            Set<StoredDeviceGroup> deviceGroups = deviceGroupMemberEditorDao.getGroupMembership(base, yukonDevice);
            
            LiteDeviceMeterNumber liteDeviceMeterNumber = deviceDao.getLiteDeviceMeterNumber(liteYukonPAObject.getLiteID());
			if (liteDeviceMeterNumber != null) {
				tempData.setMeterNumber(liteDeviceMeterNumber.getMeterNumber());
                String alternatePath = deviceGroupService.getFullPath(SystemGroupEnum.ALTERNATE);
                String billingPath = deviceGroupService.getFullPath(SystemGroupEnum.BILLING);
                String collectionPath = deviceGroupService.getFullPath(SystemGroupEnum.COLLECTION);
				for (StoredDeviceGroup group : deviceGroups) {
                    if(group.getFullName().contains(alternatePath)){
                        String groupName = group.getFullName().replace(alternatePath, "");
                        tempData.setAltGrp(groupName);
                        continue;
                    }
                    
                    if(group.getFullName().contains(billingPath)){
                        String groupName = group.getFullName().replace(billingPath, "");
                        tempData.setBillingGroup(groupName);
                        continue;
                    }
                    
                    if(group.getFullName().contains(collectionPath)){
                        String groupName = group.getFullName().replace(collectionPath, "");
                        tempData.setCollectionGrp(groupName);
                        continue;
                    }
                }
                
				tempData.setSubstationName(" ");
			}
		}
		return tempData;
	}

	private String getCsvFormat(ImportData importData){
		String line = importData.getAddress()+","+
					  importData.getName()+","+
					  importData.getRouteName()+","+
					  importData.getMeterNumber()+","+
					  importData.getCollectionGrp()+","+
					  importData.getAltGrp()+","+
					  importData.getTemplateName()+","+
					  importData.getBillingGroup()+","+
					  importData.getSubstationName()+"\n";
		return line;
	}
	
}