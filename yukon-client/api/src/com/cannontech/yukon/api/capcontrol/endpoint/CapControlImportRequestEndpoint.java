package com.cannontech.yukon.api.capcontrol.endpoint;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.w3c.dom.Node;

import com.cannontech.capcontrol.creation.model.CapControlXmlImportEnum;
import com.cannontech.capcontrol.creation.model.CbcImportData;
import com.cannontech.capcontrol.creation.model.CbcImportResult;
import com.cannontech.capcontrol.creation.model.CbcImportResultTypesEnum;
import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.capcontrol.creation.model.HierarchyImportResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportResultTypesEnum;
import com.cannontech.capcontrol.creation.model.ImportActionsEnum;
import com.cannontech.capcontrol.creation.service.CapControlImportService;
import com.cannontech.capcontrol.exception.CapControlImportException;
import com.cannontech.capcontrol.exception.CbcImporterWebServiceException;
import com.cannontech.capcontrol.exception.HierarchyImporterWebServiceException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.google.common.collect.Lists;

@Endpoint
public class CapControlImportRequestEndpoint {
	private static final Logger log = YukonLogManager.getLogger(CapControlImportRequestEndpoint.class);

	private CapControlImportService capControlImportService;
	
    private final static Namespace ns = YukonXml.getYukonNamespace();
    
    @Transactional
	@PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="capControlImportRequest")
	public Element invoke(Element capControlImportRequest, LiteYukonUser user) {
		XmlVersionUtils.verifyYukonMessageVersion(capControlImportRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
		Element response = new Element("capControlImportResponse", ns);
		
		SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(capControlImportRequest);

		List<HierarchyImportResult> hierarchyResults = performHierarchyImport(requestTemplate);
		List<CbcImportResult> cbcResults = performCbcImport(requestTemplate);
		
		Element hierarchyList = createHierarchyResponseElement(hierarchyResults);
		Element cbcList = createCbcResponseElement(cbcResults);
		
		response.addContent(hierarchyList);
		response.addContent(cbcList);
		
		return response;
	}
	
	private Element createHierarchyResponseElement(List<HierarchyImportResult> results) {
		if (results.isEmpty()) {
			return null;
		}
		
		Element hierarchyRepsonse = new Element("hierarchyResponseList");
		
		for (HierarchyImportResult result : results) {
			Element response = new Element("hierarchyImportResponse");
			
			HierarchyImportResultTypesEnum resultType = result.getResultType();
			
			if (!resultType.isSuccess()) {
				Element resultTypeElement = new Element("hierarchyImportResultType");
				resultTypeElement.addContent(result.getResultType().getDbString());
				response.addContent(resultTypeElement);

				// Add more detail if we can.
				if (result.getHierarchyImportData() != null) {
					Element detailElement = createHierarchyDetailElement(result);
					response.addContent(detailElement);
				}
			} else {
				Element detailElement = createHierarchyDetailElement(result);
				response.addContent(detailElement);
			}
		
			response.setAttribute(new Attribute("success", result.getResultType().isSuccess().toString()));
			
			hierarchyRepsonse.addContent(response);
		}
		
		return hierarchyRepsonse;
	}
	
	private Element createCbcResponseElement(List<CbcImportResult> results) {
		if (results.isEmpty()) {
			return null;
		}
		
		Element cbcResponse = new Element("cbcResponseList");
		
		for (CbcImportResult result : results) {
			Element response = new Element("cbcImportResponse");
			
			CbcImportResultTypesEnum resultType = result.getResultType();
			
			if (!resultType.isSuccess()) {
				Element cbcResult = new Element("cbcImportResultType");
				cbcResult.addContent(result.getResultType().getDbString());
				response.addContent(cbcResult);
				
				// Add more detail if we can.
				if (result.getCbcImportData() != null) {
					Element detailElement = createCbcDetailElement(result);
					response.addContent(detailElement);
				}
			} else {
				Element detailElement = createCbcDetailElement(result);
				response.addContent(detailElement);
			}
			
			response.setAttribute(new Attribute("success", result.getResultType().isSuccess().toString()));
			
			cbcResponse.addContent(response);
		}
		
		return cbcResponse;
	}
	
	private Element createCbcDetailElement(CbcImportResult result) {
		ImportActionsEnum action = result.getCbcImportData().getImportAction();
		String name = result.getCbcImportData().getCbcName();
		String outcome = result.getResultType().isSuccess() ? "succeeded" : "failed";
		String actionString = (action != null) ? action.getDbString() : "Import"; 
		
		String detail = actionString + " " + outcome + " for CBC with name \"" + name + "\".";
		
		Element detailElement = new Element("resultDetail");
		detailElement.addContent(detail);
		
		return detailElement;
	}
	
	private Element createHierarchyDetailElement(HierarchyImportResult result) {
		ImportActionsEnum action = result.getHierarchyImportData().getImportAction();
		String name = result.getHierarchyImportData().getName();
		String outcome = result.getResultType().isSuccess() ? "succeeded" : "failed";
		String actionString = (action != null) ? action.getDbString() : "Import"; 
		
		String detail = actionString + " " + outcome + " for object with name \"" + name + "\".";
		
		Element detailElement = new Element("resultDetail");
		detailElement.addContent(detail);
		
		return detailElement;
	}
	
	private List<CbcImportResult> performCbcImport(SimpleXPathTemplate requestTemplate) {
		List<Node> cbcNodes = requestTemplate.evaluateAsNodeList("/y:capControlImportRequest/y:cbcList/y:cbc");
		
		if (cbcNodes.isEmpty()) {
			return null;
		}
		
		List<CbcImportResult> results = Lists.newArrayList();
		
		processCbcImport(cbcNodes, results);

		return results;
	}
	
	private void processCbcImport(List<Node> cbcList, List<CbcImportResult> cbcResults) {
		for (Node node : cbcList) {
			CbcImportData data = null;
			
			try {
				data = createCbcImportData(node);
			} catch (CbcImporterWebServiceException c) {
				log.debug(c);
				cbcResults.add(new CbcImportResult(null, c.getResultType()));
			} catch (CapControlImportException e) {
				log.debug(e);
				cbcResults.add(new CbcImportResult(null, CbcImportResultTypesEnum.MISSING_DATA));
			}
			
			if (data != null) {
			    try {
    				switch(data.getImportAction()) {
    					case ADD:
    						if (data.isTemplate()) {
    							capControlImportService.createCbcFromTemplate(data, cbcResults);
    						} else {
    							capControlImportService.createCbc(data, cbcResults);
    						}
    						break;
    						
    					case UPDATE:
    						capControlImportService.updateCbc(data, cbcResults);
    						break;
    						
    					case REMOVE:
    						capControlImportService.removeCbc(data, cbcResults);
    						break;
    				}
			    } catch (NotFoundException e) {
	                log.debug(e);
	                cbcResults.add(new CbcImportResult(data, CbcImportResultTypesEnum.INVALID_PARENT));
	            }
			}
		}
	}
	
	private List<HierarchyImportResult> performHierarchyImport(SimpleXPathTemplate requestTemplate) {
		List<Node> hierarchyList = requestTemplate.evaluateAsNodeList("/y:capControlImportRequest/y:hierarchyList/y:*");
		
		if (hierarchyList.isEmpty()) {
			return null;
		}
		
		List<HierarchyImportResult> results = Lists.newArrayList();
		
		processHierarchyImport(hierarchyList, results);
		
		return results;
	}
	
	private void processHierarchyImport(List<Node> hierarchyList, List<HierarchyImportResult> hierarchyResults) {
		for (Node hierarchyNode : hierarchyList) {
			HierarchyImportData data = null;
			
			try {
				data = createHierarchyImportData(hierarchyNode);
			} catch (HierarchyImporterWebServiceException h) {
				log.debug(h);
				hierarchyResults.add(new HierarchyImportResult(null, h.getResultType()));
			} catch (CapControlImportException e) {
				log.debug(e);
				hierarchyResults.add(new HierarchyImportResult(null, HierarchyImportResultTypesEnum.MISSING_DATA));
			} 
			
			if (data != null) {
				try {
					switch(data.getImportAction()) {
						case ADD:
							capControlImportService.createHierarchyObject(data, hierarchyResults);
							break;
							
						case UPDATE:
							capControlImportService.updateHierarchyObject(data, hierarchyResults);
							break;
							
						case REMOVE:
							capControlImportService.removeHierarchyObject(data, hierarchyResults);
							break;
					}
				} catch (NotFoundException e) {
					log.debug("Parent " + data.getParent() + " was not found for hierarchy object " + data.getName() +
							  ". No import occured for this object.");
					hierarchyResults.add(new HierarchyImportResult(data, HierarchyImportResultTypesEnum.INVALID_PARENT));
				}
			}
		}
	}
	
	private CbcImportData createCbcImportData(Node cbcNode) {
		final String missingText = "Cbc XML import is missing required element: ";
		CbcImportData cbcImportData = new CbcImportData();

		SimpleXPathTemplate cbcTemplate = YukonXml.getXPathTemplateForNode(cbcNode);
		
		// Required fields, guaranteed present. Throw if they aren't.
		String cbcName = cbcTemplate.evaluateAsString("y:name");
		if (cbcName == null) {
			throw new CapControlImportException(missingText + "'name'");
		}
		cbcImportData.setCbcName(cbcName);
		
		String cbcType = cbcTemplate.evaluateAsString("y:type");
		if (cbcType == null) {
			throw new CapControlImportException(missingText + "'type'");
		}
		
		// Will throw if cbcType is invalid.
		try {
			PaoType paoType = PaoType.getForDbString(cbcType);
			cbcImportData.setCbcType(paoType);
		} catch (IllegalArgumentException e) {
			throw new CbcImporterWebServiceException(CbcImportResultTypesEnum.INVALID_TYPE);
		}
		
		Integer serialNumber = cbcTemplate.evaluateAsInt("y:serialNumber");
		if (serialNumber == null) {
			throw new CapControlImportException(missingText + "'serialNumber");
		}
		cbcImportData.setCbcSerialNumber(serialNumber);
		
		Integer masterAddress = cbcTemplate.evaluateAsInt("y:masterAddress");
		if (masterAddress == null) {
			throw new CapControlImportException(missingText + "'masterAddress'");
		}
		cbcImportData.setMasterAddress(masterAddress);
		
		Integer slaveAddress = cbcTemplate.evaluateAsInt("y:slaveAddress");
		if (slaveAddress == null) {
			throw new CapControlImportException(missingText + "'slaveAddress'");
		}
		cbcImportData.setSlaveAddress(slaveAddress);
		
		String commChannel = cbcTemplate.evaluateAsString("y:commChannel");
		if (commChannel == null) {
			throw new CapControlImportException(missingText + "'commChannel'");
		}
		cbcImportData.setCommChannel(commChannel);
		
		String importAction = cbcTemplate.evaluateAsString("@action");
		if (importAction == null) {
			throw new CapControlImportException(missingText + "'importAction'");
		}
		
		try {
			ImportActionsEnum action = ImportActionsEnum.getForDbString(importAction);
			cbcImportData.setImportAction(action);
		} catch (IllegalArgumentException e) {
			throw new CbcImporterWebServiceException(CbcImportResultTypesEnum.INVALID_IMPORT_ACTION);
		}
		
		// Not required. Check for nulls.
		String templateName = cbcTemplate.evaluateAsString("y:templateName");
		if (templateName != null) {
			cbcImportData.setTemplateName(templateName);
		}
		
		String capBankName = cbcTemplate.evaluateAsString("y:capBankName");
		if (capBankName != null) {
			cbcImportData.setCapBankName(capBankName);
		}
		
		String scanEnabled = cbcTemplate.evaluateAsString("y:scanEnabled");
		if (scanEnabled != null) {
			cbcImportData.setScanEnabled(scanEnabled);
		}
		
		Integer scanInterval = cbcTemplate.evaluateAsInt("y:scanInterval");
		if (scanInterval != null) {
			cbcImportData.setScanInterval(scanInterval);
		}
		
		Integer altInterval = cbcTemplate.evaluateAsInt("y:altInterval");
		if (altInterval != null) {
			cbcImportData.setAltInterval(altInterval);
		}
		
		return cbcImportData;
	}
	
	private HierarchyImportData createHierarchyImportData(Node hierarchyNode) throws CapControlImportException {
		final String missingText = "Hierarchy XML import is missing required element: ";
		HierarchyImportData data = new HierarchyImportData();
		
		SimpleXPathTemplate hierarchyTemplate = YukonXml.getXPathTemplateForNode(hierarchyNode);
		
		// Required fields, guaranteed present. Throw if they aren't.
		String name = hierarchyTemplate.evaluateAsString("y:name");
		if (name == null) {
			throw new CapControlImportException(missingText + "'name'");
		}
		data.setName(name);
		
		String paoType = hierarchyNode.getNodeName();
		if (paoType == null) { // Weird, but check just in case...
			throw new CapControlImportException(missingText + "'type'");
		}
		
		// This will throw an IllegalArgumentException if the type was invalid.
		try {
			data.setPaoType(CapControlXmlImportEnum.getPaoTypeForXmlString(paoType));
		} catch (IllegalArgumentException e) {
			throw new HierarchyImporterWebServiceException(HierarchyImportResultTypesEnum.INVALID_TYPE);
		}
		
		String importAction = hierarchyTemplate.evaluateAsString("@action");
		if (importAction == null) {
			throw new CapControlImportException(missingText + "'importAction'");
		}
		
		// This will throw an IllegalArgumentException as well.
		try {
			data.setImportAction(ImportActionsEnum.getForDbString(importAction));
		} catch (IllegalArgumentException e) {
			throw new HierarchyImporterWebServiceException(HierarchyImportResultTypesEnum.INVALID_IMPORT_ACTION);
		}
		
		// Not required. Check for nulls.
		String parent = hierarchyTemplate.evaluateAsString("y:parent");
		if (parent != null) {
			data.setParent(parent);
		}
		
		String description = hierarchyTemplate.evaluateAsString("y:description");
		if (description != null) {
			data.setDescription(description);
		}
		
		String disabled = hierarchyTemplate.evaluateAsString("y:disabled");
		if (disabled != null) {
			if (disabled.equalsIgnoreCase("Y")) {
				data.setDisabled(true);
			} else if (disabled.equalsIgnoreCase("N")) {
				data.setDisabled(false);
			} else {
				throw new HierarchyImporterWebServiceException(HierarchyImportResultTypesEnum.INVALID_DISABLED_VALUE);
			}
		}
		
		String mapLocationId = hierarchyTemplate.evaluateAsString("y:mapLocationId");
		if (mapLocationId != null) {
			data.setMapLocationId(mapLocationId);
		}
		
		return data;
	}
	
	@Autowired
	public void setCapControlImportService(CapControlImportService capControlImportService) {
		this.capControlImportService = capControlImportService;
	}
}
