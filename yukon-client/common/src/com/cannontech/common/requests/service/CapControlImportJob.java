package com.cannontech.common.requests.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.w3c.dom.Node;

import com.cannontech.capcontrol.creation.model.CbcImportCompleteDataResult;
import com.cannontech.capcontrol.creation.model.CbcImportData;
import com.cannontech.capcontrol.creation.model.CbcImportInvalidDataResult;
import com.cannontech.capcontrol.creation.model.CbcImportMissingDataResult;
import com.cannontech.capcontrol.creation.model.CbcImportResult;
import com.cannontech.capcontrol.creation.model.CbcImportResultType;
import com.cannontech.capcontrol.creation.model.HierarchyImportCompleteDataResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.capcontrol.creation.model.HierarchyImportInvalidDataResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportMissingDataResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportResultType;
import com.cannontech.capcontrol.exception.CapControlCbcImportException;
import com.cannontech.capcontrol.exception.CapControlHierarchyImportException;
import com.cannontech.capcontrol.exception.ImporterCbcMissingDataException;
import com.cannontech.capcontrol.exception.ImporterHierarchyMissingDataException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.common.requests.runnable.YukonJob;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.NotFoundException;
import com.google.common.collect.Lists;

public abstract class CapControlImportJob implements YukonJob {
    protected static final Logger log = YukonLogManager.getLogger(CapControlImportJob.class);
    
    protected final Element elem;
    protected final List<Node> requestNodes;
    protected final List<CbcImportData> cbcImportList = Lists.newArrayList();
    protected final List<HierarchyImportData> hierarchyImportList = Lists.newArrayList();
    protected final List<CbcImportResult> cbcImportResults = Lists.newArrayList();
    protected final List<HierarchyImportResult> hierarchyImportResults = Lists.newArrayList();
    protected final ImportAction importAction;
    
    protected CapControlImportJob(Element elem, ImportAction importAction) {
        this.elem = elem;
        this.importAction = importAction;
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(elem);
        String requestName = "capControl" + importAction.getDbString() + "Request";
        requestNodes = requestTemplate.evaluateAsNodeList("/y:" + requestName + "/y:*");
    }
    
    private enum HierarchyOrder {
        area,
        specialArea,
        substation,
        substationBus,
        feeder,
        capBank,
        cbc
        ;
    }
    
    protected void parseImportData() {
        /*
         * Sort the collection in top-down hierarchy order so that if the import is unordered
         * and has parent requirements internal to the import, they can be addressed without
         * fear of a failed import.
         */
        Collections.sort(requestNodes, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                int o1Order = HierarchyOrder.valueOf(o1.getLocalName()).ordinal();
                int o2Order = HierarchyOrder.valueOf(o2.getLocalName()).ordinal();

                return o1Order - o2Order;
            }
        });
        
        for (Node node : requestNodes) {
            String nodeType = node.getLocalName();
            
            if ("cbc".equals(nodeType)) {
                CbcImportData cbcImportData = null;
                try {
                    // This is a CBC. Parse the CBC data and toss it onto the cbcImportList.
                    cbcImportData = CapControlImportXmlHelper.parseCbcJobData(node, importAction);
                    cbcImportList.add(cbcImportData);
                } catch (CapControlCbcImportException c) {
                    log.debug(c);
                    if (cbcImportData != null) {
                        cbcImportResults.add(new CbcImportCompleteDataResult(cbcImportData, c.getImportResultType()));
                    } else {
                        cbcImportResults.add(new CbcImportInvalidDataResult(c.getImportResultType()));
                    }
                } catch (ImporterCbcMissingDataException e) {
                    log.debug(e);
                    cbcImportResults.add(new CbcImportMissingDataResult(e.getMissingField()));
                } catch (NotFoundException e) {
                    log.debug(e);
                    cbcImportResults.add(new CbcImportCompleteDataResult(cbcImportData, CbcImportResultType.INVALID_PARENT));
                }
            } else {
                HierarchyImportData hierarchyImportData = null;
                
                try {
                    // We got ourselves a hierarchy node! Let's parse the data and put it onto the hierarchyImportList!
                    hierarchyImportData = CapControlImportXmlHelper.parseHierarchyJobData(node, importAction);
                    if (importAction != ImportAction.REMOVE) {
                        CapControlImportXmlHelper.populateHierarchyImportData(node, hierarchyImportData);
                    }
                    hierarchyImportList.add(hierarchyImportData);
                } catch (CapControlHierarchyImportException e) {
                    log.debug(e);
                    if (hierarchyImportData != null) {
                        hierarchyImportResults.add(new HierarchyImportCompleteDataResult(hierarchyImportData, 
                                                                                         e.getImportResultType()));
                    } else {
                        hierarchyImportResults.add(new HierarchyImportInvalidDataResult(e.getImportResultType()));
                    }
                } catch (ImporterHierarchyMissingDataException e) {
                    log.debug(e);
                    hierarchyImportResults.add(new HierarchyImportMissingDataResult(e.getMissingField()));
                } catch (NotFoundException e) {
                    log.debug("Parent " + hierarchyImportData.getParent() + " was not found for hierarchy object " + 
                              hierarchyImportData.getName() + ". No import occured for this object.");
                    HierarchyImportCompleteDataResult result = 
                            new HierarchyImportCompleteDataResult(hierarchyImportData, 
                                                                  HierarchyImportResultType.INVALID_PARENT);
                    hierarchyImportResults.add(result);
                }
            }
        }
    }

    @Override
    public boolean isFinished() {
        return (getProgress() == 100.0);
    }

    @Override
    public void reportResults(Element element) {
        if (!isFinished()) {
            // We're in progress. No way we're giving them results. What do we do?
            Element inProcessElem = new Element("failure", element.getNamespace());
            inProcessElem.addContent("The job is still in process.");
            element.addContent(inProcessElem);
            return;
        }
        
        if (!hierarchyImportResults.isEmpty()) {
            CapControlImportXmlHelper.populateHierarchyResponseElement(hierarchyImportResults, element);
        }
        
        if (!cbcImportResults.isEmpty()) {
            CapControlImportXmlHelper.populateCbcResponseElement(cbcImportResults, element);
        }
    }
    
    @Override
    synchronized public double getProgress() {
        double complete = cbcImportResults.size() + hierarchyImportResults.size();
        
        // We want this to have only two decimal places.
        double result = (complete / requestNodes.size()) * 10000.0;
        long rounded = Math.round(result);
        
        return (rounded / 100.0);
    }
}
