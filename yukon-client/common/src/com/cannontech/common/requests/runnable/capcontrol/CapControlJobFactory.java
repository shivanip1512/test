package com.cannontech.common.requests.runnable.capcontrol;

import org.jdom2.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.creation.model.CbcImportCompleteDataResult;
import com.cannontech.capcontrol.creation.model.CbcImportData;
import com.cannontech.capcontrol.creation.model.CbcImportResultType;
import com.cannontech.capcontrol.creation.model.HierarchyImportCompleteDataResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.capcontrol.creation.model.HierarchyImportResultType;
import com.cannontech.capcontrol.creation.service.CapControlImportService;
import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.common.requests.runnable.YukonJob;
import com.cannontech.common.requests.service.CapControlImportJob;
import com.cannontech.core.dao.NotFoundException;

public class CapControlJobFactory {
    @Autowired
    private CapControlImportService capControlImportService;

    public class CapControlAddJob extends CapControlImportJob {
        public CapControlAddJob(Element elem) {
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

    public class CapControlUpdateJob extends CapControlImportJob {
        public CapControlUpdateJob(Element elem) {
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

    public class CapControlRemoveJob extends CapControlImportJob {
        public CapControlRemoveJob(Element elem) {
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

    public YukonJob createRunnable(ImportAction importAction, Element elem) {
        switch (importAction) {
        case ADD:
            return new CapControlAddJob(elem);
        case UPDATE:
            return new CapControlUpdateJob(elem);
        case REMOVE:
            return new CapControlRemoveJob(elem);
        default:
            return null;
        }
    }
}
