package com.cannontech.yukon.api.capcontrol.endpoint;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.xml.validation.XmlValidationException;
import org.springframework.xml.xpath.XPathException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.cannontech.capcontrol.creation.model.CbcImportCompleteDataResult;
import com.cannontech.capcontrol.creation.model.CbcImportData;
import com.cannontech.capcontrol.creation.model.CbcImportMissingDataResult;
import com.cannontech.capcontrol.creation.model.CbcImportResult;
import com.cannontech.capcontrol.creation.model.CbcImportResultType;
import com.cannontech.capcontrol.creation.model.HierarchyImportCompleteDataResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.capcontrol.creation.model.HierarchyImportMissingDataResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportResultType;
import com.cannontech.capcontrol.creation.model.ImportAction;
import com.cannontech.capcontrol.creation.service.CapControlImportService;
import com.cannontech.capcontrol.exception.CapControlCbcImportException;
import com.cannontech.capcontrol.exception.CapControlHierarchyImportException;
import com.cannontech.capcontrol.exception.ImporterCbcMissingDataException;
import com.cannontech.capcontrol.exception.ImporterHierarchyMissingDataException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.requests.service.CapControlImportXmlHelper;
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
        response.setAttribute("version", XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
	    try {
    		SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(capControlImportRequest);
    
    		List<HierarchyImportResult> hierarchyResults = performHierarchyImport(requestTemplate);
    		List<CbcImportResult> cbcResults = performCbcImport(requestTemplate);
    		
    		if (!hierarchyResults.isEmpty()) {
    		    Element hierarchyList = new Element("hierarchyResponseList", ns);
    		    CapControlImportXmlHelper.populateHierarchyResponseElement(hierarchyResults, hierarchyList);
    		    response.addContent(hierarchyList);
    		}
    		
    		if (!cbcResults.isEmpty()) {
    		    Element cbcList = new Element("cbcResponseList", ns);
    		    CapControlImportXmlHelper.populateCbcResponseElement(cbcResults, cbcList);
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
				data = CapControlImportXmlHelper.parseCbcImportData(node);
				
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
			} catch (CapControlCbcImportException c) {
				log.debug(c);
				cbcResults.add(new CbcImportCompleteDataResult(data, c.getImportResultType()));
			} catch (ImporterCbcMissingDataException e) {
				log.debug(e);
				cbcResults.add(new CbcImportMissingDataResult(e.getMissingField()));
			} catch (NotFoundException e) {
                log.debug(e);
                cbcResults.add(new CbcImportCompleteDataResult(data, CbcImportResultType.INVALID_PARENT));
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
				data = CapControlImportXmlHelper.parseHierarchyImportData(hierarchyNode);
				
				if (data.getImportAction() == ImportAction.REMOVE) {
				    capControlImportService.removeHierarchyObject(data, hierarchyResults);
				} else {
				    CapControlImportXmlHelper.populateHierarchyImportData(hierarchyNode, data);
    				
    				if (data.getImportAction() == ImportAction.ADD) {
                        capControlImportService.createHierarchyObject(data, hierarchyResults);
    				} else {
                        capControlImportService.updateHierarchyObject(data, hierarchyResults);
                    }
				}
			} catch (CapControlHierarchyImportException e) {
                log.debug(e);
                hierarchyResults.add(new HierarchyImportCompleteDataResult(data, e.getImportResultType()));
            } catch (ImporterHierarchyMissingDataException e) {
				log.debug(e);
				hierarchyResults.add(new HierarchyImportMissingDataResult(e.getMissingField()));
			} catch (NotFoundException e) {
                log.debug("Parent " + data.getParent() + " was not found for hierarchy object " + data.getName() +
                        ". No import occured for this object.");
                hierarchyResults.add(new HierarchyImportCompleteDataResult(data, HierarchyImportResultType.INVALID_PARENT));
			}
		}
	}
}
