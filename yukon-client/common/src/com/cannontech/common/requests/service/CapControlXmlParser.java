package com.cannontech.common.requests.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
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
import com.cannontech.capcontrol.creation.model.ImportAction;
import com.cannontech.capcontrol.exception.CapControlCbcImportException;
import com.cannontech.capcontrol.exception.CapControlHierarchyImportException;
import com.cannontech.capcontrol.exception.ImporterCbcMissingDataException;
import com.cannontech.capcontrol.exception.ImporterHierarchyMissingDataException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.requests.runnable.YukonJobRunnable;
import com.cannontech.common.requests.util.CapControlXmlUtils;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.NotFoundException;
import com.google.common.collect.Lists;

public abstract class CapControlXmlParser implements YukonJobRunnable {
    protected static final Logger log = YukonLogManager.getLogger(CapControlXmlParser.class);
    
    protected final Element elem;
    protected final List<Node> requestNodes;
    protected final List<CbcImportData> cbcImportList = Lists.newArrayList();
    protected final List<HierarchyImportData> hierarchyImportList = Lists.newArrayList();
    protected final List<CbcImportResult> cbcImportResults = Lists.newArrayList();
    protected final List<HierarchyImportResult> hierarchyImportResults = Lists.newArrayList();
    protected final ImportAction importAction;
    
    protected CapControlXmlParser(Element elem, ImportAction importAction) {
        this.elem = elem;
        this.importAction = importAction;
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(elem);
        String requestName = "capControl" + importAction.getDbString() + "Request";
        requestNodes = requestTemplate.evaluateAsNodeList("/y:" + requestName + "/y:*");
    }
    
    private enum HierarchyOrder {
        area(0),
        specialArea(0),
        substation(1),
        substationBus(2),
        feeder(3),
        capBank(4),
        cbc(5)
        ;
        
        private final int order;
        
        HierarchyOrder(int order) {
            this.order = order;
        }
        
        public int getOrder() {
            return order;
        }
    }
    
    protected void parseImportData() {
        Collections.sort(requestNodes, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                int o1Order = HierarchyOrder.valueOf(o1.getLocalName()).getOrder();
                int o2Order = HierarchyOrder.valueOf(o2.getLocalName()).getOrder();

                return (o1Order == o2Order) ? 0 : ((o1Order < o2Order) ? 1 : -1);
            }
        });
        
        for (Node node : requestNodes) {
            String nodeType = node.getLocalName();
            
            if ("cbc".equals(nodeType)) {
                CbcImportData cbcImportData = null;
                try {
                    // This is a CBC. Parse the CBC data and toss it onto the cbcImportList.
                    cbcImportData = CapControlXmlUtils.parseCbcJobData(node, importAction);
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
                    hierarchyImportData = CapControlXmlUtils.parseHierarchyJobData(node, importAction);
                    if (importAction != ImportAction.REMOVE) {
                        CapControlXmlUtils.populateHierarchyImportData(node, hierarchyImportData);
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
            CapControlXmlUtils.populateHierarchyResponseElement(hierarchyImportResults, element);
        }
        
        if (!cbcImportResults.isEmpty()) {
            CapControlXmlUtils.populateCbcResponseElement(cbcImportResults, element);
        }
    }
    
    @Override
    public double getProgress() {
        synchronized (this) {
            double complete = cbcImportResults.size() + hierarchyImportResults.size();
            
            return ((complete / requestNodes.size()) * 100.0);
        }
    }
}
