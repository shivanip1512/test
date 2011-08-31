package com.cannontech.capcontrol.creation.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.capcontrol.creation.model.CbcImportData;
import com.cannontech.capcontrol.creation.model.CbcImportResult;
import com.cannontech.capcontrol.creation.model.CbcImportResultTypesEnum;
import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.capcontrol.creation.model.HierarchyImportResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportResultTypesEnum;
import com.cannontech.capcontrol.creation.service.CapControlImportService;
import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.capcontrol.dao.providers.fields.AreaFields;
import com.cannontech.capcontrol.dao.providers.fields.CapBankFields;
import com.cannontech.capcontrol.dao.providers.fields.CapbankAdditionalFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceAddressFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceCbcFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceDirectCommSettingsFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceScanRateFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceWindowFields;
import com.cannontech.capcontrol.dao.providers.fields.FeederFields;
import com.cannontech.capcontrol.dao.providers.fields.SpecialAreaFields;
import com.cannontech.capcontrol.dao.providers.fields.SubstationBusFields;
import com.cannontech.capcontrol.dao.providers.fields.SubstationFields;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.pao.service.PaoCreationService;
import com.cannontech.common.pao.service.PaoTemplate;
import com.cannontech.common.pao.service.PaoTemplatePart;
import com.cannontech.common.pao.service.providers.fields.YukonPaObjectFields;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

public class CapControlImportServiceImpl implements CapControlImportService {
	
	private PaoCreationService paoCreationService;
	private PaoDefinitionService paoDefinitionService;
	private PaoDao paoDao;
	private SubstationDao substationDao;
	private SubstationBusDao substationBusDao;
	private FeederDao feederDao;
	private CapbankDao capbankDao;
	private CapbankControllerDao capbankControllerDao;
	
	private PaoTemplate createCbcPaoTemplate(CbcImportData cbcImportData, List<CbcImportResult> results) {
		PaoType paoType = cbcImportData.getCbcType();
		
		ClassToInstanceMap<PaoTemplatePart> paoFields = MutableClassToInstanceMap.create();
		YukonPaObjectFields yukonPaObjectFields = new YukonPaObjectFields(cbcImportData.getCbcName());
		paoFields.put(YukonPaObjectFields.class, yukonPaObjectFields);
		
		DeviceFields deviceFields = new DeviceFields();
		paoFields.put(DeviceFields.class, deviceFields);
		
		// Set up the fields based on the CBC type.
		switch (paoType) {
			case CBC_7020:
			case CBC_7022:
			case CBC_7023:
			case CBC_7024:
			case CBC_8020:
			case CBC_8024:
			case CBC_DNP:
			case DNP_CBC_6510:
				DeviceScanRateFields deviceScanRateFields = new DeviceScanRateFields();
				Integer altInterval = cbcImportData.getAltInterval();
				if (altInterval != null) {
					deviceScanRateFields.setAlternateRate(altInterval);
				}
				
				Integer scanInterval = cbcImportData.getScanInterval();
				if (scanInterval != null) {
					deviceScanRateFields.setIntervalRate(scanInterval);
				}
				paoFields.put(DeviceScanRateFields.class, deviceScanRateFields);
				
				DeviceWindowFields deviceWindowFields = new DeviceWindowFields();
				paoFields.put(DeviceWindowFields.class, deviceWindowFields);
				
				int portId = getPaoIdByName(cbcImportData.getCommChannel());
				if (portId == -1) {
					// Can't create this guy without a valid comm channel.
					results.add(new CbcImportResult(cbcImportData, CbcImportResultTypesEnum.INVALID_COMM_CHANNEL));
					return null;
				}
				DeviceDirectCommSettingsFields commSettingsFields = new DeviceDirectCommSettingsFields(portId);
				paoFields.put(DeviceDirectCommSettingsFields.class, commSettingsFields);
				
				if (paoType != PaoType.DNP_CBC_6510) {
					DeviceCbcFields cbcFields = new DeviceCbcFields();
					cbcFields.setSerialNumber(cbcImportData.getCbcSerialNumber());
					paoFields.put(DeviceCbcFields.class, cbcFields);
				}
				
				DeviceAddressFields addressFields = new DeviceAddressFields();
				addressFields.setMasterAddress(cbcImportData.getMasterAddress());
				addressFields.setSlaveAddress(cbcImportData.getSlaveAddress());
				paoFields.put(DeviceAddressFields.class, addressFields);
				
				break;

			case CBC_7010:
			case CBC_7011:
			case CBC_7012:
			case CBC_EXPRESSCOM:
			case CBC_FP_2800:
			case CAPBANKCONTROLLER:
				DeviceCbcFields deviceCbcFields = new DeviceCbcFields();
				deviceCbcFields.setSerialNumber(cbcImportData.getCbcSerialNumber());
				paoFields.put(DeviceCbcFields.class, deviceCbcFields);
				
				break;
				
			default:
				results.add(new CbcImportResult(cbcImportData, CbcImportResultTypesEnum.INVALID_TYPE));	
				return null;
		}
		
        PaoTemplate paoTemplate = new PaoTemplate(paoType, paoFields);
        
        return paoTemplate;
	}
	
	private PaoTemplate createHierarchyPaoTemplate(HierarchyImportData hierarchyImportData, List<HierarchyImportResult> results) {
		ClassToInstanceMap<PaoTemplatePart> paoFields = MutableClassToInstanceMap.create();
		
		String mapLocationId = hierarchyImportData.getMapLocationId();
		
		switch(hierarchyImportData.getPaoType()) {
		case CAP_CONTROL_AREA:
			paoFields.put(AreaFields.class, new AreaFields());
			
			break;
		case CAP_CONTROL_SPECIAL_AREA:
			paoFields.put(SpecialAreaFields.class, new SpecialAreaFields());
			
			break;
		case CAP_CONTROL_SUBSTATION:
			SubstationFields substationFields = new SubstationFields();
			substationFields.setMapLocationId(mapLocationId);
			paoFields.put(SubstationFields.class, substationFields);
			
			break;
		case CAP_CONTROL_SUBBUS:
			SubstationBusFields substationBusFields = new SubstationBusFields();
			substationBusFields.setMapLocationId(mapLocationId);
			paoFields.put(SubstationBusFields.class, substationBusFields);
			
			break;
		case CAP_CONTROL_FEEDER:
			FeederFields feederFields = new FeederFields();
			feederFields.setMapLocationId(mapLocationId);
			paoFields.put(FeederFields.class, feederFields);
			
			break;
		case CAPBANK:
			CapBankFields capBankFields = new CapBankFields();
			capBankFields.setMapLocationId(mapLocationId);
			paoFields.put(CapBankFields.class, capBankFields);
			
			paoFields.put(DeviceFields.class, new DeviceFields());
			paoFields.put(CapbankAdditionalFields.class, new CapbankAdditionalFields());
			
			break;
		default:
			results.add(new HierarchyImportResult(hierarchyImportData, HierarchyImportResultTypesEnum.INVALID_TYPE));
			return null;
		}
		
		// Create and set-up the YukonPaObjectFields
		YukonPaObjectFields yukonPaObjectFields = new YukonPaObjectFields(hierarchyImportData.getName());
		yukonPaObjectFields.setDescription(hierarchyImportData.getDescription());
		yukonPaObjectFields.setDisabled(hierarchyImportData.isDisabled());
		paoFields.put(YukonPaObjectFields.class, yukonPaObjectFields);
		
		PaoTemplate paoTemplate = new PaoTemplate(hierarchyImportData.getPaoType(), paoFields);
		
		return paoTemplate;
	}
	
	private PaoTemplate createTemplateDeviceData(PaoIdentifier paoIdentifier, CbcImportData cbcImportData,
												 List<CbcImportResult> results) {
		ClassToInstanceMap<PaoTemplatePart> paoFields = MutableClassToInstanceMap.create();
		
		paoFields.put(YukonPaObjectFields.class, paoDao.getYukonPaObjectData(paoIdentifier, cbcImportData.getCbcName()));
		
		// Nothing in DeviceDirectCommSettings comes from the template device. Validate comm channel though.
		int portId = getPaoIdByName(cbcImportData.getCommChannel());
		if (portId == -1) {
			// Can't create this guy without a valid comm channel.
			results.add(new CbcImportResult(cbcImportData, CbcImportResultTypesEnum.INVALID_COMM_CHANNEL));
			return null;
		}
		DeviceDirectCommSettingsFields commSettingsFields = new DeviceDirectCommSettingsFields(portId);
		paoFields.put(DeviceDirectCommSettingsFields.class, commSettingsFields);
		
		DeviceAddressFields addressFields = capbankControllerDao.getDeviceAddressData(paoIdentifier);
		addressFields.setMasterAddress(cbcImportData.getMasterAddress());
		addressFields.setSlaveAddress(cbcImportData.getSlaveAddress());
		paoFields.put(DeviceAddressFields.class, addressFields);
		
		// Nothing in DeviceCBC needs to come from the Template Device.
		DeviceCbcFields deviceCbcFields = new DeviceCbcFields();
		deviceCbcFields.setSerialNumber(cbcImportData.getCbcSerialNumber());
		paoFields.put(DeviceCbcFields.class, deviceCbcFields);
		
		paoFields.put(DeviceFields.class, capbankControllerDao.getDeviceData(paoIdentifier));
		
		DeviceScanRateFields scanRateFields = capbankControllerDao.getDeviceScanRateData(paoIdentifier);
		scanRateFields.setAlternateRate(cbcImportData.getAltInterval());
		scanRateFields.setIntervalRate(cbcImportData.getScanInterval());
		paoFields.put(DeviceScanRateFields.class, scanRateFields);
		
		try {
			// This stuff might not be here, so we could get a DataAccessException. If we do, just create and use the default one.
			paoFields.put(DeviceWindowFields.class, capbankControllerDao.getDeviceWindowData(paoIdentifier));
		} catch (DataAccessException e) {
			paoFields.put(DeviceWindowFields.class, new DeviceWindowFields());
		}
		
		PaoTemplate paoTemplate = new PaoTemplate(paoIdentifier.getPaoType(), paoFields);
		
		return paoTemplate;
	}
	
	@Override
	public void createCbc(CbcImportData cbcImportData, List<CbcImportResult> results) {
		LiteYukonPAObject pao = getCbcPao(cbcImportData);
		if (pao != null) {
			// We were told to add a device that already exists. This is an error!
			results.add(new CbcImportResult(cbcImportData, CbcImportResultTypesEnum.DEVICE_EXISTS));
			return;
		}
		
		PaoTemplate paoTemplate = createCbcPaoTemplate(cbcImportData, results);
        
		if (paoTemplate == null) {
			return;
		}
		
        PaoIdentifier paoIdentifier = paoCreationService.createPao(paoTemplate);
        
        paoDefinitionService.createAllPointsForPao(paoIdentifier);
        
        createCbcParentLink(cbcImportData.getCapBankName(), paoIdentifier.getPaoId());
        
        results.add(new CbcImportResult(cbcImportData, CbcImportResultTypesEnum.SUCCESS));
    }
	
	@Override
	public void createCbcFromTemplate(CbcImportData cbcImportData, List<CbcImportResult> results) {
		LiteYukonPAObject templatePao = getTemplateCbcPao(cbcImportData);
		if (templatePao == null) {
			// The template didn't exist.
			results.add(new CbcImportResult(cbcImportData, CbcImportResultTypesEnum.NO_SUCH_OBJECT));
			return;
		}
		
		LiteYukonPAObject pao = getCbcPao(cbcImportData);
		if (pao != null) {
			// The object we're trying to make already exists.
			results.add(new CbcImportResult(cbcImportData, CbcImportResultTypesEnum.DEVICE_EXISTS));
			return;
		}
		
		PaoIdentifier templateIdentifier = templatePao.getPaoIdentifier();
		
		// Load up the template device data, then inject our import data.
		PaoTemplate newCbcTemplate = createTemplateDeviceData(templateIdentifier, cbcImportData, results);
		
		// Get a copy of the points from the template to copy to the new CBC.
		int templatePaoId = templatePao.getPaoIdentifier().getPaoId();
		List<PointBase> copyPoints = capbankControllerDao.getPointsForPao(templatePaoId);

		PaoIdentifier paoIdentifier = paoCreationService.createTemplatePao(newCbcTemplate, copyPoints);
		
		createCbcParentLink(cbcImportData.getCapBankName(), paoIdentifier.getPaoId());
		
		results.add(new CbcImportResult(cbcImportData, CbcImportResultTypesEnum.SUCCESS));
	}
	
	@Override
	public void updateCbc(CbcImportData cbcImportData, List<CbcImportResult> results) {
		LiteYukonPAObject pao = getCbcPao(cbcImportData);
		if (pao == null) {
			// We were told to update a device that doesn't exist. This is an error!
			results.add(new CbcImportResult(cbcImportData, CbcImportResultTypesEnum.NO_SUCH_OBJECT));
			return;
		}
		
		PaoTemplate paoTemplate = createCbcPaoTemplate(cbcImportData, results);
		
		if (paoTemplate == null) {
			// There was a problem creating the template. The results list already has the reason why.
			return;
		}
		
		paoCreationService.updatePao(pao.getPaoIdentifier().getPaoId(), paoTemplate);
		
		createCbcParentLink(cbcImportData.getCapBankName(), pao.getPaoIdentifier().getPaoId());
        
        results.add(new CbcImportResult(cbcImportData, CbcImportResultTypesEnum.SUCCESS));
	}
	
	@Override
	public void removeCbc(CbcImportData cbcImportData, List<CbcImportResult> results) {
		LiteYukonPAObject pao = getCbcPao(cbcImportData);
		if (pao == null) {
			// We were told to remove a device that doesn't exist. This is an error!
			results.add(new CbcImportResult(cbcImportData, CbcImportResultTypesEnum.NO_SUCH_OBJECT));
			return;
		}
		
		PaoIdentifier paoIdentifier = pao.getPaoIdentifier();
		
		// Unassign the device from any capbank it may be attached to.
		capbankControllerDao.unassignController(paoIdentifier.getPaoId());
		
		paoCreationService.deletePao(paoIdentifier);
		
		results.add(new CbcImportResult(cbcImportData, CbcImportResultTypesEnum.SUCCESS));
	}

	@Override
	public void createHierarchyObject(HierarchyImportData hierarchyImportData, List<HierarchyImportResult> results) {
		LiteYukonPAObject pao = getHierarchyPao(hierarchyImportData);
		if (pao != null) {
			// We were told to add an object that already exists. This is an error!
			results.add(new HierarchyImportResult(hierarchyImportData, HierarchyImportResultTypesEnum.OBJECT_EXISTS));
			return;
		}
		
		PaoTemplate paoTemplate = createHierarchyPaoTemplate(hierarchyImportData, results);
        
		// This will throw if the parent doesn't exist, preventing creation of the child, as desired.
		Integer parentId = getParentId(hierarchyImportData, paoTemplate.getPaoType());
		
        int childId = paoCreationService.createPao(paoTemplate).getPaoId();
        
        if (parentId != null) {
        	createHierarchyParentLink(hierarchyImportData, parentId, childId, results);
        }
        
        results.add(new HierarchyImportResult(hierarchyImportData, HierarchyImportResultTypesEnum.SUCCESS));
	}
	
	@Override
	public void updateHierarchyObject(HierarchyImportData hierarchyImportData, List<HierarchyImportResult> results) {
		LiteYukonPAObject pao = getHierarchyPao(hierarchyImportData);
		if (pao == null) {
			// We were told to remove an object that doesn't exist. This is an error!
			results.add(new HierarchyImportResult(hierarchyImportData, HierarchyImportResultTypesEnum.NO_SUCH_OBJECT));
			return;
		}
		
		int childId = pao.getPaoIdentifier().getPaoId();
		PaoTemplate paoTemplate = createHierarchyPaoTemplate(hierarchyImportData, results);
		
		// This will throw if the parent doesn't exist, preventing the update of the child, as desired.
		Integer parentId = getParentId(hierarchyImportData, paoTemplate.getPaoType());
		
		paoCreationService.updatePao(childId, paoTemplate);
		
		if (parentId != null) {
        	createHierarchyParentLink(hierarchyImportData, parentId, childId, results);
        }
		
		results.add(new HierarchyImportResult(hierarchyImportData, HierarchyImportResultTypesEnum.SUCCESS));
	};
	
	@Override
	public void removeHierarchyObject(HierarchyImportData hierarchyImportData, List<HierarchyImportResult> results) {
		LiteYukonPAObject pao = getHierarchyPao(hierarchyImportData);
		if (pao == null) {
			// We were told to remove a device that doesn't exist. This is an error!
			results.add(new HierarchyImportResult(hierarchyImportData, HierarchyImportResultTypesEnum.NO_SUCH_OBJECT));
			return;
		}
		
		PaoIdentifier paoIdentifier = pao.getPaoIdentifier();
		
		paoCreationService.deletePao(paoIdentifier);
		
		results.add(new HierarchyImportResult(hierarchyImportData, HierarchyImportResultTypesEnum.SUCCESS));
	};
	
	private Integer getParentId(HierarchyImportData hierarchyImportData, PaoType childType) {
		String parentName = hierarchyImportData.getParent();
		Integer parentId = null;
		PaoType parentType = null;
		if (!parentName.isEmpty()) {
			parentType = getHierarchyParentType(childType);
			parentId = paoDao.getLiteYukonPAObject(parentName, parentType).getPaoIdentifier().getPaoId();
		}
		
		return parentId;
	}

	private LiteYukonPAObject getHierarchyPao(HierarchyImportData data) {
		String hierarchyName = data.getName();
		PaoType paoType = data.getPaoType();
		
		try {
			if (paoType == null) {
				// Delete doesn't require a type.
				List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaoByName(hierarchyName, false);
				if (paos == null || paos.isEmpty()) {
					return null;
				} else {
					return paos.get(0);
				}
			}
			LiteYukonPAObject pao = paoDao.getLiteYukonPAObject(hierarchyName, paoType);
			return pao;
		} catch (NotFoundException e) {
			return null;
		}
	}
	
	private LiteYukonPAObject getTemplateCbcPao(CbcImportData data) {
		String templateName = data.getTemplateName();
		PaoType paoType = data.getCbcType();
		
		return retrieveCbcPao(templateName, paoType);
	}
	
	private LiteYukonPAObject getCbcPao(CbcImportData data) {
		String cbcName = data.getCbcName();
		PaoType paoType = data.getCbcType();
		
		return retrieveCbcPao(cbcName, paoType);
	}
	
	private LiteYukonPAObject retrieveCbcPao(String name, PaoType paoType) {
		try {
			if (paoType == null) {
				// Not necessary for a delete, so just grab by name.
				List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaoByName(name, false);
				if (paos == null || paos.isEmpty()) {
					return null;
				} else {
					return paos.get(0);
				}
			} else {
				LiteYukonPAObject pao = paoDao.getLiteYukonPAObject(name, paoType);
				return pao;
			}
		} catch (NotFoundException e) {
			return null;
		}
	}
	
	private void createHierarchyParentLink(HierarchyImportData hierarchyImportData, int parentId, int childId, 
								  List<HierarchyImportResult> results) {
		boolean success;
		switch (hierarchyImportData.getPaoType()) {
		case CAP_CONTROL_AREA:
		case CAP_CONTROL_SPECIAL_AREA:
			// We don't want to do anything here, areas don't have parents
			success = true;
			break;
		case CAP_CONTROL_SUBSTATION:
			success = substationDao.assignSubstation(parentId, childId);
			break;
		case CAP_CONTROL_SUBBUS:
			success = substationBusDao.assignSubstationBus(parentId, childId);
			break;
		case CAP_CONTROL_FEEDER:
			success = feederDao.assignFeeder(parentId, childId);
			break;
		case CAPBANK:
			success = capbankDao.assignCapbank(parentId, childId);
			break;
		default:
			results.add(new HierarchyImportResult(hierarchyImportData, HierarchyImportResultTypesEnum.INVALID_TYPE));
			return;
		}
		
		if (!success) {
			results.add(new HierarchyImportResult(hierarchyImportData, HierarchyImportResultTypesEnum.INVALID_PARENT));
		}
	}
	
	private void createCbcParentLink(String parentName, int childId) {
        if (parentName != null) {
        	LiteYukonPAObject parent = paoDao.getLiteYukonPAObject(parentName, PaoType.CAPBANK);
        	if (parent != null) {
        		capbankControllerDao.assignController(parent.getPaoIdentifier().getPaoId(), childId);
        	}
        }
	}
	
	private PaoType getHierarchyParentType(PaoType childType) {
		switch (childType) {
		case CAP_CONTROL_AREA:
		case CAP_CONTROL_SPECIAL_AREA:
			return null;
		case CAP_CONTROL_SUBSTATION:
			return PaoType.CAP_CONTROL_AREA;
		case CAP_CONTROL_SUBBUS:
			return PaoType.CAP_CONTROL_SUBSTATION;
		case CAP_CONTROL_FEEDER:
			return PaoType.CAP_CONTROL_SUBBUS;
		case CAPBANK:
			return PaoType.CAP_CONTROL_FEEDER;
		default:
			return null;
		}
	}
	
	private LiteYukonPAObject getPaoByName(String paoName) {
		List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaoByName(paoName, false);
		
		if (paos.size() != 1) {
			return null;
		}
		LiteYukonPAObject litePao = paos.get(0);
		
		return litePao;
	}
	
	private int getPaoIdByName(String paoName) {
		LiteYukonPAObject litePao = getPaoByName(paoName);
		
		if (litePao == null) {
			return -1;
		}
		
		return litePao.getYukonID();
	}
	
	@Autowired
	public void setPaoCreationService(PaoCreationService paoCreationService) {
		this.paoCreationService = paoCreationService;
	}
	
	@Autowired
	public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}
	
	@Autowired
	public void setSubstationDao(SubstationDao substationDao) {
		this.substationDao = substationDao;
	}
	
	@Autowired
	public void setSubstationBusDao(SubstationBusDao substationBusDao) {
		this.substationBusDao = substationBusDao;
	}
	
	@Autowired
	public void setFeederDao(FeederDao feederDao) {
		this.feederDao = feederDao;
	}
	
	@Autowired
	public void setCapbankDao(CapbankDao capbankDao) {
		this.capbankDao = capbankDao;
	}
	
	@Autowired
	public void setCapbankControllerDao(CapbankControllerDao capbankControllerDao) {
		this.capbankControllerDao = capbankControllerDao;
	}
	
	@Autowired
	public void setPaoDefinitionService(PaoDefinitionService paoDefinitionService) {
		this.paoDefinitionService = paoDefinitionService;
	}
}
