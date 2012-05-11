package com.cannontech.common.requests.runnable.capcontrol;

import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.creation.model.CbcImportCompleteDataResult;
import com.cannontech.capcontrol.creation.model.CbcImportData;
import com.cannontech.capcontrol.creation.model.CbcImportResultType;
import com.cannontech.capcontrol.creation.model.HierarchyImportCompleteDataResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.capcontrol.creation.model.HierarchyImportResultType;
import com.cannontech.capcontrol.creation.model.ImportAction;
import com.cannontech.capcontrol.creation.service.CapControlImportService;
import com.cannontech.common.requests.runnable.YukonJobRunnable;
import com.cannontech.common.requests.service.CapControlXmlParser;
import com.cannontech.core.dao.NotFoundException;

public class CapControlRunnableFactory {
    @Autowired
    private CapControlImportService capControlImportService;

    public class CapControlAddRunnable extends CapControlXmlParser {
        public CapControlAddRunnable(Element elem) {
            super(elem, ImportAction.ADD);
        }

        @Override
        public void run() {
            parseImportData();

            for (CbcImportData cbcImportData : cbcImportList) {
                try {
                    if (cbcImportData.isTemplate()) {
                        capControlImportService.createCbcFromTemplate(cbcImportData, cbcImportResults);
                    } else {
                        capControlImportService.createCbc(cbcImportData, cbcImportResults);
                    }
                } catch (NotFoundException e) {
                    log.debug(e);
                    cbcImportResults.add(new CbcImportCompleteDataResult(cbcImportData, CbcImportResultType.INVALID_PARENT));
                }
            }

            for (HierarchyImportData hierarchyImportData : hierarchyImportList) {
                try {
                    capControlImportService.createHierarchyObject(hierarchyImportData, hierarchyImportResults);
                } catch (NotFoundException e) {
                    log.debug(e);
                    hierarchyImportResults.add(new HierarchyImportCompleteDataResult(hierarchyImportData, HierarchyImportResultType.INVALID_PARENT));
                }
            }
        }
    }

    public class CapControlUpdateRunnable extends CapControlXmlParser {
        public CapControlUpdateRunnable(Element elem) {
            super(elem, ImportAction.UPDATE);
        }

        @Override
        public void run() {
            parseImportData();

            for (CbcImportData cbcImportData : cbcImportList) {
                capControlImportService.updateCbc(cbcImportData, cbcImportResults);
            }

            for (HierarchyImportData hierarchyImportData : hierarchyImportList) {
                capControlImportService.updateHierarchyObject(hierarchyImportData, hierarchyImportResults);
            }
        }
    }

    public class CapControlRemoveRunnable extends CapControlXmlParser {
        public CapControlRemoveRunnable(Element elem) {
            super(elem, ImportAction.REMOVE);
        }

        @Override
        public void run() {
            parseImportData();

            for (CbcImportData cbcImportData : cbcImportList) {
                capControlImportService.removeCbc(cbcImportData, cbcImportResults);
            }

            for (HierarchyImportData hierarchyImportData : hierarchyImportList) {
                capControlImportService.removeHierarchyObject(hierarchyImportData, hierarchyImportResults);
            }
        }
    }

    public YukonJobRunnable createRunnable(ImportAction importAction, Element elem) {
        switch (importAction) {
        case ADD:
            return new CapControlAddRunnable(elem);
        case UPDATE:
            return new CapControlUpdateRunnable(elem);
        case REMOVE:
            return new CapControlRemoveRunnable(elem);
        default:
            return null;
        }
    }
}
