package com.cannontech.yukon.api.capcontrol.endpoint;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.xml.validation.XmlValidationException;
import org.springframework.xml.xpath.XPathException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.capcontrol.creation.model.CapControlXmlImport;
import com.cannontech.capcontrol.creation.model.CbcImportData;
import com.cannontech.capcontrol.creation.model.CbcImportResult;
import com.cannontech.capcontrol.creation.model.CbcImportResultType;
import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.capcontrol.creation.model.HierarchyImportResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportResultType;
import com.cannontech.capcontrol.creation.model.ImportAction;
import com.cannontech.capcontrol.creation.service.CapControlImportService;
import com.cannontech.capcontrol.exception.CapControlImportException;
import com.cannontech.capcontrol.exception.ImporterInvalidDisabledValueException;
import com.cannontech.capcontrol.exception.ImporterInvalidImportActionException;
import com.cannontech.capcontrol.exception.ImporterInvalidOpStateException;
import com.cannontech.capcontrol.exception.ImporterInvalidPaoTypeException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.google.common.collect.Lists;

@Endpoint
public class CapControlImportRequestEndpoint {
	private static final Logger log = YukonLogManager.getLogger(CapControlImportRequestEndpoint.class);

	@Autowired private CapControlImportService capControlImportService;
	
    private final static Namespace ns = YukonXml.getYukonNamespace();
    
	@PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="capControlImportRequest")
	public Element invoke(Element capControlImportRequest, LiteYukonUser user) {
	    log.debug("import request received");
	    
	    XmlVersionUtils.verifyYukonMessageVersion(capControlImportRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        Element response = new Element("capControlImportResponse", ns);
	    try {
    		SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(capControlImportRequest);
    
    		List<HierarchyImportResult> hierarchyResults = performHierarchyImport(requestTemplate);
    		List<CbcImportResult> cbcResults = performCbcImport(requestTemplate);
    		
    		Element hierarchyList = createHierarchyResponseElement(hierarchyResults);
    		Element cbcList = createCbcResponseElement(cbcResults);
    		
    		if (hierarchyList != null) {
    		    response.addContent(hierarchyList);
    		}
    		
    		if (cbcList != null) {
    		    response.addContent(cbcList);
    		}
	    } catch (XmlValidationException xmle) {
            log.error("XML validation error", xmle);
            Element error = XMLFailureGenerator.generateFailure(response, xmle, "InvalidResponseType",
                                                                xmle.getMessage());
            response.addContent(error);
        } catch (XPathException xpe) {
            log.error("XML validation error", xpe);
            Element error = XMLFailureGenerator.generateFailure(response, xpe, "XmlParseError",
                                                                xpe.getMessage());
            response.addContent(error);
        } catch (DOMException dome) {
            log.error("XML validation error", dome);
            Element error = XMLFailureGenerator.generateFailure(response, dome, "XmlParseError",
                                                                dome.getMessage());
            response.addContent(error);
        } catch (Exception exception) {
            log.error("other error", exception);
            Element error = XMLFailureGenerator.generateFailure(response, exception, "OtherError",
                                                                exception.getMessage());
            response.addContent(error);
        }
        
        log.debug("returning response");
        return response;
	}
	
	private Element createHierarchyResponseElement(List<HierarchyImportResult> results) {
	    if (CollectionUtils.isEmpty(results)) {
	        return null;
	    }
	    
		Element hierarchyRepsonse = new Element("hierarchyResponseList");
		
		for (HierarchyImportResult result : results) {
			Element response = new Element("hierarchyImportResponse");
			
			HierarchyImportResultType resultType = result.getResultType();
			
			if (!resultType.isSuccess()) {
				Element resultTypeElement = new Element("hierarchyImportResultType");
				resultTypeElement.addContent(result.getResultType().getDbString());
				response.addContent(resultTypeElement);
			}

			// Add more detail if we can.
			if (result.getHierarchyImportData() != null) {
                Element detailElement = createHierarchyDetailElement(result);
                response.addContent(detailElement);
            }
		
			response.setAttribute(new Attribute("success", result.getResultType().isSuccess().toString()));
			response.setAttribute(new Attribute("errorCode", Integer.toString(result.getResultType().getErrorCode())));
			
			hierarchyRepsonse.addContent(response);
		}
		
		return hierarchyRepsonse;
	}
	
	private Element createCbcResponseElement(List<CbcImportResult> results) {	
	    if (CollectionUtils.isEmpty(results)) {
	        return null;
	    }
	    
		Element cbcResponse = new Element("cbcResponseList");
		
		for (CbcImportResult result : results) {
			Element response = new Element("cbcImportResponse");
			
			CbcImportResultType resultType = result.getResultType();
			
			if (!resultType.isSuccess()) {
				Element cbcResult = new Element("cbcImportResultType");
				cbcResult.addContent(result.getResultType().getDbString());
				response.addContent(cbcResult);
			} 

			// Add more detail if we can.
            if (result.getCbcImportData() != null) {
                Element detailElement = createCbcDetailElement(result);
                response.addContent(detailElement);
            }
			
			response.setAttribute(new Attribute("success", result.getResultType().isSuccess().toString()));
			response.setAttribute(new Attribute("errorCode", Integer.toString(result.getResultType().getErrorCode())));
			
			cbcResponse.addContent(response);
		}
		
		return cbcResponse;
	}
	
	private Element createCbcDetailElement(CbcImportResult result) {
		ImportAction action = result.getCbcImportData().getImportAction();
		String name = result.getCbcImportData().getCbcName();
		String outcome = result.getResultType().isSuccess() ? "succeeded" : "failed";
		String actionString = (action != null) ? action.getDbString() : "Import"; 
		
		String detail = actionString + " " + outcome + " for CBC with name \"" + name + "\".";
		
		Element detailElement = new Element("resultDetail");
		detailElement.addContent(detail);
		
		return detailElement;
	}
	
	private Element createHierarchyDetailElement(HierarchyImportResult result) {
		ImportAction action = result.getHierarchyImportData().getImportAction();
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

		List<CbcImportResult> results = Lists.newArrayList();
		
		processCbcImport(cbcNodes, results);

		return results;
	}
	
	private void processCbcImport(List<Node> cbcList, List<CbcImportResult> cbcResults) {
		for (Node node : cbcList) {
			CbcImportData data = null;
			
			try {
				data = createCbcImportData(node);
				
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
			} catch (ImporterInvalidPaoTypeException c) {
				log.debug(c);
				cbcResults.add(new CbcImportResult(null, CbcImportResultType.INVALID_TYPE));
			} catch (ImporterInvalidImportActionException c) {
                log.debug(c);
                cbcResults.add(new CbcImportResult(null, CbcImportResultType.INVALID_IMPORT_ACTION));
            } catch (CapControlImportException e) {
				log.debug(e);
				cbcResults.add(new CbcImportResult(null, CbcImportResultType.MISSING_DATA));
			} catch (NotFoundException e) {
                log.debug(e);
                cbcResults.add(new CbcImportResult(data, CbcImportResultType.INVALID_PARENT));
            }
		}
	}
	
	private List<HierarchyImportResult> performHierarchyImport(SimpleXPathTemplate requestTemplate) {
		List<Node> hierarchyList = requestTemplate.evaluateAsNodeList("/y:capControlImportRequest/y:hierarchyList/y:*");

		List<HierarchyImportResult> results = Lists.newArrayList();
		
		processHierarchyImport(hierarchyList, results);
		
		return results;
	}
	
	private void processHierarchyImport(List<Node> hierarchyList, List<HierarchyImportResult> hierarchyResults) {
		for (Node hierarchyNode : hierarchyList) {
			HierarchyImportData data = null;
			
			try {
			    /*
			     *  Separate parsing into two steps so we can catch structural problems separately
			     *  from data integrity problems.
			     */
				data = createHierarchyImportData(hierarchyNode);
				
				populateHierarchyImportData(hierarchyNode, data);
				
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
			} catch (ImporterInvalidOpStateException e) {
			    log.debug(e);
			    hierarchyResults.add(new HierarchyImportResult(data, HierarchyImportResultType.INVALID_OPERATIONAL_STATE));
			} catch (ImporterInvalidDisabledValueException e) {
                log.debug(e);
                hierarchyResults.add(new HierarchyImportResult(data, HierarchyImportResultType.INVALID_DISABLED_VALUE));
            } catch (ImporterInvalidPaoTypeException e) {
                log.debug(e);
                hierarchyResults.add(new HierarchyImportResult(data, HierarchyImportResultType.INVALID_TYPE));
            } catch (ImporterInvalidImportActionException h) {
				log.debug(h);
				hierarchyResults.add(new HierarchyImportResult(null, HierarchyImportResultType.INVALID_IMPORT_ACTION));
			} catch (CapControlImportException e) {
				log.debug(e);
				hierarchyResults.add(new HierarchyImportResult(null, HierarchyImportResultType.MISSING_DATA));
			} catch (NotFoundException e) {
                log.debug("Parent " + data.getParent() + " was not found for hierarchy object " + data.getName() +
                        ". No import occured for this object.");
                hierarchyResults.add(new HierarchyImportResult(data, HierarchyImportResultType.INVALID_PARENT));
			}
		}
	}
	
	private CbcImportData createCbcImportData(Node cbcNode) {
		final String missingText = "Cbc XML import is missing required element: ";
		PaoType paoType;
		ImportAction importAction;

		SimpleXPathTemplate cbcTemplate = YukonXml.getXPathTemplateForNode(cbcNode);
		
		// Required fields, guaranteed present. Throw if they aren't.
		String cbcName = cbcTemplate.evaluateAsString("y:name");
		if (cbcName == null) {
			throw new CapControlImportException(missingText + "'name'");
		}
		
		String cbcType = cbcTemplate.evaluateAsString("y:type");
		if (cbcType == null) {
			throw new CapControlImportException(missingText + "'type'");
		}
		
		// Will throw if cbcType is invalid.
		try {
		    paoType = PaoType.getForDbString(cbcType);
		} catch (IllegalArgumentException e) {
			throw new ImporterInvalidPaoTypeException("Import of " + cbcName + " failed. Unknown Type: " + cbcType);
		}
		
		Integer serialNumber = cbcTemplate.evaluateAsInt("y:serialNumber");
		if (serialNumber == null) {
			throw new CapControlImportException(missingText + "'serialNumber");
		}
		
		Integer masterAddress = cbcTemplate.evaluateAsInt("y:masterAddress");
		if (masterAddress == null) {
			throw new CapControlImportException(missingText + "'masterAddress'");
		}
		
		Integer slaveAddress = cbcTemplate.evaluateAsInt("y:slaveAddress");
		if (slaveAddress == null) {
			throw new CapControlImportException(missingText + "'slaveAddress'");
		}
		
		String commChannel = cbcTemplate.evaluateAsString("y:commChannel");
		if (commChannel == null) {
			throw new CapControlImportException(missingText + "'commChannel'");
		}
		
		String importActionStr = cbcTemplate.evaluateAsString("@action");
		if (importActionStr == null) {
			throw new CapControlImportException(missingText + "'importAction'");
		}
		
		try {
			importAction = ImportAction.getForDbString(importActionStr);
		} catch (IllegalArgumentException e) {
			throw new ImporterInvalidImportActionException("Import of " + cbcName + " failed. Unknown Action: " + importActionStr);
		}
		
		// If we got to this point, we have all required data with no errors.
		CbcImportData cbcImportData = new CbcImportData(cbcName,
		                                                importAction,
		                                                paoType, 
		                                                commChannel, 
		                                                serialNumber.intValue(), 
		                                                masterAddress.intValue(), 
		                                                slaveAddress.intValue());
		
		// Not required. Check for nulls.
		String templateName = cbcTemplate.evaluateAsString("y:templateName");
		if (templateName != null) {
			cbcImportData.setTemplateName(templateName);
		}
		
		String capBankName = cbcTemplate.evaluateAsString("y:capBankName");
		if (capBankName != null) {
			cbcImportData.setCapBankName(capBankName);
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
		PaoType paoType;
		ImportAction importAction;
		
		SimpleXPathTemplate hierarchyTemplate = YukonXml.getXPathTemplateForNode(hierarchyNode);
		
		// Required fields, guaranteed present. Throw if they aren't.
		String name = hierarchyTemplate.evaluateAsString("y:name");
		if (name == null) {
			throw new CapControlImportException(missingText + "'name'");
		}
		
		String paoTypeStr = hierarchyNode.getNodeName();
		if (paoTypeStr == null) { // Weird, but check just in case...
			throw new CapControlImportException(missingText + "'type'");
		}
		
		// This will throw an IllegalArgumentException if the type was invalid.
		try {
			paoType = CapControlXmlImport.getPaoTypeForXmlString(paoTypeStr);
		} catch (IllegalArgumentException e) {
			throw new ImporterInvalidPaoTypeException("Import of " + name + " failed. Unknown Type: " + paoTypeStr);
		}
		
		String importActionStr = hierarchyTemplate.evaluateAsString("@action");
		if (importActionStr == null) {
			throw new CapControlImportException(missingText + "'importAction'");
		}
		
		// This will throw an IllegalArgumentException as well.
		try {
			importAction = ImportAction.getForDbString(importActionStr);
		} catch (IllegalArgumentException e) {
			throw new ImporterInvalidImportActionException("Import of " + name + " failed. Unknown Action: " + importActionStr);
		}

		// We have all required data. Let's make the HierarchyImportData object.
		return new HierarchyImportData(paoType, name, importAction);
	}
	
	private void populateHierarchyImportData(Node hierarchyNode, HierarchyImportData data) {
        SimpleXPathTemplate hierarchyTemplate = YukonXml.getXPathTemplateForNode(hierarchyNode);

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
			    throw new ImporterInvalidDisabledValueException("Disabled field contained invalid data. Please " +
                                                                "change to 'Y' or 'N'");
			}
		}
		
		String mapLocationId = hierarchyTemplate.evaluateAsString("y:mapLocationId");
		if (mapLocationId != null) {
			data.setMapLocationId(mapLocationId);
		}
		
		String opState = hierarchyTemplate.evaluateAsString("y:operationalState");
		if (opState != null) {
		    try {
		        BankOpState bankOpState = BankOpState.getStateByName(opState);
		        data.setBankOpState(bankOpState);
		    } catch (IllegalArgumentException e) {
		        throw new ImporterInvalidOpStateException("Operational state field contained invalid data.");
		    }
		}
		
		Integer capBankSize = hierarchyTemplate.evaluateAsInt("y:capBankSize");
		if (capBankSize != null) {
		    data.setCapBankSize(capBankSize);
		}
	}
}
