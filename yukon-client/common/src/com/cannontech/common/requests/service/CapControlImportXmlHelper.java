package com.cannontech.common.requests.service;

import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.w3c.dom.Node;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.capcontrol.creation.CapControlImporterCbcField;
import com.cannontech.capcontrol.creation.CapControlImporterHierarchyField;
import com.cannontech.capcontrol.creation.model.CapControlXmlImport;
import com.cannontech.capcontrol.creation.model.CbcImportData;
import com.cannontech.capcontrol.creation.model.CbcImportResult;
import com.cannontech.capcontrol.creation.model.CbcImportResultType;
import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.capcontrol.creation.model.HierarchyImportResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportResultType;
import com.cannontech.capcontrol.exception.CapControlCbcImportException;
import com.cannontech.capcontrol.exception.CapControlHierarchyImportException;
import com.cannontech.capcontrol.exception.ImporterCbcMissingDataException;
import com.cannontech.capcontrol.exception.ImporterHierarchyMissingDataException;
import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;

public class CapControlImportXmlHelper {
    private static final String missingHierarchyText = "Hierarchy XML import is missing required element: ";
    private static final String missingCbcText = "CBC XML import is missing required element: ";

    /**
     * This method parses the data of a cbc node except for the action, which has either
     * already been parsed in the calling {@link CapControlImportXmlHelper#parseCbcImportData(Node)}
     * method or has been deduced by the type of import that was invoked via web services.
     * @param node the node representing a cap control cbc object.
     * @param importAction the import action for the object.
     * @return a populated CbcImportData object representing the data from the node.
     * @throws ImporterCbcMissingDataException if the node is missing required fields.
     * @throws CapControlCbcImportException if a parsed value was invalid.
     */
    public static CbcImportData parseCbcJobData(Node node, ImportAction importAction)      
            throws ImporterCbcMissingDataException, CapControlCbcImportException {
        SimpleXPathTemplate cbcTemplate = YukonXml.getXPathTemplateForNode(node);
        
        PaoType paoType;
        
        // Required fields, guaranteed present. Throw if they aren't.
        String cbcName = cbcTemplate.evaluateAsString("y:name");
        if (cbcName == null) {
            throw new ImporterCbcMissingDataException(missingCbcText + "'name'", CapControlImporterCbcField.CBC_NAME);
        }
        
        // If we're removing an object, there's no need to look at any of the rest of the data.
        if (importAction == ImportAction.REMOVE) {
            return new CbcImportData(cbcName, importAction, null); // PaoType is irrelevant.
        }
        
        String cbcType = cbcTemplate.evaluateAsString("y:type");
        if (cbcType == null) {
            throw new ImporterCbcMissingDataException(missingCbcText + "'type'", CapControlImporterCbcField.CBC_TYPE);
        }
        
        // Will throw if cbcType is invalid.
        try {
            paoType = PaoType.getForDbString(cbcType);
        } catch (IllegalArgumentException e) {
            throw new CapControlCbcImportException("Import of " + cbcName + " failed. Unknown Type: " + cbcType, 
                                                     CbcImportResultType.INVALID_TYPE, e);
        }
        
        CbcImportData cbcImportData = new CbcImportData(cbcName, importAction, paoType);
        
        Integer serialNumber = cbcTemplate.evaluateAsInt("y:serialNumber");
        if (serialNumber == null) {
            if (importAction == ImportAction.ADD) {
                throw new ImporterCbcMissingDataException(missingCbcText + "'serialNumber", CapControlImporterCbcField.CBC_SERIAL_NUMBER);
            }
        } else {
            cbcImportData.setCbcSerialNumber(serialNumber);
        }
        
        Integer masterAddress = cbcTemplate.evaluateAsInt("y:masterAddress");
        if (masterAddress == null) {
            if (importAction == ImportAction.ADD) {
                throw new ImporterCbcMissingDataException(missingCbcText + "'masterAddress'", CapControlImporterCbcField.MASTER_ADDRESS);
            }
        } else {
            cbcImportData.setMasterAddress(masterAddress.intValue());
        }
             
        Integer slaveAddress = cbcTemplate.evaluateAsInt("y:slaveAddress");
        if (slaveAddress == null) {
            if (importAction == ImportAction.ADD) {
                throw new ImporterCbcMissingDataException(missingCbcText + "'slaveAddress'", CapControlImporterCbcField.SLAVE_ADDRESS);
            }
        } else {
            cbcImportData.setSlaveAddress(slaveAddress.intValue());
        }
        
        String commChannel = cbcTemplate.evaluateAsString("y:commChannel");
        if (commChannel == null && (importAction == ImportAction.ADD)) {
            throw new ImporterCbcMissingDataException(missingCbcText + "'commChannel'", CapControlImporterCbcField.COMM_CHANNEL);
        }
        
        cbcImportData.setCommChannel(commChannel);
        
        // Not required. Check for nulls.
        String templateName = cbcTemplate.evaluateAsString("y:templateName");
        if (templateName != null) {
            cbcImportData.setTemplateName(templateName);
        }
        
        String capBankName = cbcTemplate.evaluateAsString("y:capBankName");
        if (capBankName != null) {
            cbcImportData.setCapBankName(capBankName);
        }
        
        Boolean scanEnabled = cbcTemplate.evaluateAsBoolean("y:scanEnabled", false);
        if (scanEnabled) {
            cbcImportData.setScanEnabled(true);
            
            Integer scanInterval = cbcTemplate.evaluateAsInt("y:scanInterval");
            if (scanInterval != null) {
                cbcImportData.setScanInterval(scanInterval);
            }
            
            Integer altInterval = cbcTemplate.evaluateAsInt("y:altInterval");
            if (altInterval != null) {
                cbcImportData.setAltInterval(altInterval);
            }
        }
        
        return cbcImportData;
    }
    
    /**
     * This method is used mainly for the CapControlImportRequest, since nodes for those
     * requests contain the action attribute. This method parses out the action attribute,
     * then calls {@link CapControlImportXmlHelper#parseCbcJobData(Node, ImportAction)} to parse
     * the remaining data.
     * @param node the node representing a cap control cbc object.
     * @return a populated CbcImportData object holding the data parsed from the node.
     * @throws ImporterCbcMissingDataException if the node doesn't have an action attribute.
     * @throws CapControlCbcImportException if the import action parse wasn't a valid action.
     */
    public static CbcImportData parseCbcImportData(Node node)     
            throws ImporterCbcMissingDataException, CapControlCbcImportException {
        ImportAction importAction;

        SimpleXPathTemplate cbcTemplate = YukonXml.getXPathTemplateForNode(node);

        String importActionStr = cbcTemplate.evaluateAsString("@action");
        if (importActionStr == null) {
            throw new ImporterCbcMissingDataException(missingCbcText + "'importAction'", CapControlImporterCbcField.IMPORT_ACTION);
        }
        
        try {
            importAction = ImportAction.getForDbString(importActionStr);
        } catch (IllegalArgumentException e) {
            throw new CapControlCbcImportException("Import failed. Unknown Action: " + importActionStr, 
                                                   CbcImportResultType.INVALID_IMPORT_ACTION, e);
        }
        
        return parseCbcJobData(node, importAction);
    }
    
    public static HierarchyImportData parseHierarchyJobData(Node node, ImportAction importAction)
            throws ImporterHierarchyMissingDataException, CapControlHierarchyImportException {
        SimpleXPathTemplate hierarchyTemplate = YukonXml.getXPathTemplateForNode(node);
        PaoType paoType;
        
        // Required fields, guaranteed present. Throw if they aren't.
        String name = hierarchyTemplate.evaluateAsString("y:name");
        if (name == null) {
            throw new ImporterHierarchyMissingDataException(missingHierarchyText + "'name'", CapControlImporterHierarchyField.NAME);
        }
        
        String paoTypeStr = node.getLocalName();
        if (paoTypeStr == null) { // Weird, but check just in case...
            throw new ImporterHierarchyMissingDataException(missingHierarchyText + "'type'", CapControlImporterHierarchyField.TYPE);
        }
        
        // This will throw an IllegalArgumentException if the type was invalid.
        try {
            paoType = CapControlXmlImport.getPaoTypeForXmlString(paoTypeStr);
        } catch (IllegalArgumentException e) {
            throw new CapControlHierarchyImportException("Import of " + name + " failed. Unknown Type: " + paoTypeStr, 
                                                         HierarchyImportResultType.INVALID_TYPE, e);
        }

        // We have all required data. Let's make the HierarchyImportData object.
        return new HierarchyImportData(paoType, name, importAction);
    }

    public static HierarchyImportData parseHierarchyImportData(Node node) 
            throws ImporterHierarchyMissingDataException, CapControlHierarchyImportException {
        ImportAction importAction;
        
        SimpleXPathTemplate hierarchyTemplate = YukonXml.getXPathTemplateForNode(node);
        
        String importActionStr = hierarchyTemplate.evaluateAsString("@action");
        if (importActionStr == null) {
            throw new ImporterHierarchyMissingDataException(missingHierarchyText + "'importAction'", CapControlImporterHierarchyField.IMPORT_ACTION);
        }
        
        // This will throw an IllegalArgumentException as well.
        try {
            importAction = ImportAction.getForDbString(importActionStr);
        } catch (IllegalArgumentException e) {
            throw new CapControlHierarchyImportException("Import failed. Unknown Action: " + importActionStr, 
                                                         HierarchyImportResultType.INVALID_IMPORT_ACTION, e);
        }
        
        return parseHierarchyJobData(node, importAction);
    }
    
    public static void populateHierarchyImportData(Node hierarchyNode, HierarchyImportData data) {
        SimpleXPathTemplate hierarchyTemplate = YukonXml.getXPathTemplateForNode(hierarchyNode);
        
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
                throw new CapControlHierarchyImportException("Disabled field contained invalid data. Please " +
                                                             "change to 'Y' or 'N'",
                                                             HierarchyImportResultType.INVALID_DISABLED_VALUE);
            }
        }
        
        if (data.getPaoType() != PaoType.CAP_CONTROL_AREA && 
            data.getPaoType() != PaoType.CAP_CONTROL_SPECIAL_AREA) {
            
            // We don't care about the parent or location if it's an Area or Special area, ignore them.
            String parent = hierarchyTemplate.evaluateAsString("y:parent");
            if (parent != null) {
                data.setParent(parent);
            }
            
            String mapLocationId = hierarchyTemplate.evaluateAsString("y:mapLocationId");
            if (mapLocationId != null) {
                data.setMapLocationId(mapLocationId);
            }
            
            if (data.getPaoType() == PaoType.CAPBANK) {
                // These columns only matter for cap banks, no need to validate or even look at them otherwise.
                String opState = hierarchyTemplate.evaluateAsString("y:operationalState");
                if (opState != null) {
                    try {
                        BankOpState bankOpState = BankOpState.getStateByName(opState);
                        data.setBankOpState(bankOpState);
                    } catch (IllegalArgumentException e) {
                        throw new CapControlHierarchyImportException("Operational state field contained invalid data.",
                                                                     HierarchyImportResultType.INVALID_OPERATIONAL_STATE, e);
                    }
                }
                
                try {
                    Integer capBankSize = hierarchyTemplate.evaluateAsInt("y:capBankSize");
                    if (capBankSize != null) {
                        data.setCapBankSize(capBankSize);
                    }
                } catch (NumberFormatException e) {
                    // no op
                }
            }
        }
    }
    
    public static void populateHierarchyResponseElement(List<HierarchyImportResult> results, Element hierarchyResponse) {
        for (HierarchyImportResult result : results) {
            Element response = result.getResponseElement(hierarchyResponse.getNamespace());
        
            response.setAttribute(new Attribute("success", result.getResultType().isSuccess().toString()));
            response.setAttribute(new Attribute("errorCode", Integer.toString(result.getResultType().getErrorCode())));
            
            hierarchyResponse.addContent(response);
        }
    }
    
    public static void populateCbcResponseElement(List<CbcImportResult> results, Element cbcResponse) {   
        for (CbcImportResult result : results) {
            Element response = result.getResponseElement(cbcResponse.getNamespace());
            
            response.setAttribute(new Attribute("success", result.getResultType().isSuccess().toString()));
            response.setAttribute(new Attribute("errorCode", Integer.toString(result.getResultType().getErrorCode())));
            
            cbcResponse.addContent(response);
        }
    }
}
