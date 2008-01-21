package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.bulk.importdata.dao.BulkImportDataDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.db.importer.ImportDataBase;
import com.cannontech.user.YukonUserContext;

public class BulkImportResultsModel extends BareReportModelBase<BulkImportResultsModel.ModelRow> implements ReportModelMetaInfo {
    
    // dependencies
    DateFormattingService dateFormattingService = null;
    BulkImportDataDao bulkImportDataDao = null;
    
    // inputs
    String reportType;
    
    // member variables
    private static String title = "Bulk Import Results Report";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
    
    static public class ModelRow {
        public String address;
        public String name;
        public String routeName;
        public String meterNumber;
        public String collectionGroup;
        public String altGroup;
        public String templateName;
        public String billingGroup;
        public String substationName;
    }
    
    public void doLoadData() {
        
        BulkImportResultstReportType reportTypeEnum = BulkImportResultstReportType.valueOf(reportType);
        
        List<? extends ImportDataBase> importDataList = null;
        if (reportTypeEnum == BulkImportResultstReportType.FAILURES) {
            importDataList = bulkImportDataDao.getAllDataFailures();
        }
        else if (reportTypeEnum == BulkImportResultstReportType.PENDING_COMMS) {
            importDataList = bulkImportDataDao.getAllPending();
        }
        else if (reportTypeEnum == BulkImportResultstReportType.FAILED_COMMS) {
            importDataList = bulkImportDataDao.getAllCommunicationFailures();
        }
        
        for (ImportDataBase importData : importDataList) {
            BulkImportResultsModel.ModelRow row = new BulkImportResultsModel.ModelRow();
            row.address = importData.getAddress();
            row.name = importData.getName();
            row.routeName = importData.getRouteName();
            row.meterNumber = importData.getMeterNumber();
            row.collectionGroup = importData.getCollectionGrp();
            row.altGroup = importData.getAltGrp();
            row.templateName = importData.getTemplateName();
            row.billingGroup = importData.getBillGrp();
            row.substationName = importData.getSubstationName();
            data.add(row);
        }

        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    @Override
    public LinkedHashMap<String, String> getMetaInfo(YukonUserContext userContext) {
        
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();

        info.put("Report Type", BulkImportResultstReportType.valueOf(reportType).getReportDecription());
        info.put("Generated", dateFormattingService.formatDate(new Date(), DateFormattingService.DateFormatEnum.BOTH, userContext));
        return info;
    }

    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getTitle() {
        return title;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType.toUpperCase();
    }

    @Required
    public void setBulkImportDataDao(BulkImportDataDao bulkImportDataDao) {
        this.bulkImportDataDao = bulkImportDataDao;
    }

    @Required
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
   
    
  

}
