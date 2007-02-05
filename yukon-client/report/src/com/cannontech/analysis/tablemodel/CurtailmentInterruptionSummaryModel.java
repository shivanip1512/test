package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.cannontech.cc.dao.CustomerStubDao;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.service.IsocCommonStrategy;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.db.customer.CICustomerPointType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.support.CustomerPointTypeHelper;

public class CurtailmentInterruptionSummaryModel extends BareReportModelBase<CurtailmentInterruptionSummaryModel.ModelRow> implements LoadableModel, CommonModelAttributes {
    private int energyCompanyId;
    private Date fromDate;
    private Date toDate;
    
    private CustomerStubDao customerStubDao = (CustomerStubDao) YukonSpringHook.getBean("customerStubDao");
    private IsocCommonStrategy isocCommonStrategy = (IsocCommonStrategy) YukonSpringHook.getBean("isocCommonStrategy");
    private CustomerPointTypeHelper customerPointTypeHelper = (CustomerPointTypeHelper) YukonSpringHook.getBean("customerPointTypeHelper");
    
    private List<ModelRow> data;
    
    private static final List<ColumnData> columnData = new ArrayList<ColumnData>();

    public CurtailmentInterruptionSummaryModel() {
    }
    
    public List<ColumnData> getColumnData() {
        return columnData;
    }
    
    static public class ModelRow {
        public String customername;
        public Double interruptHoursContract;
        public Double interruptHoursRemaining;
        public Double interruptHoursUsed;
        public Double cil;
        public Integer noticeMinutes;
        public Double advancedElectionPricePerkW;
        public Double advancedElectionkW;
        public Double cfd;
    }
    
    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }
    
    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }
    
    public String getTitle() {
        return "Curtailment Interruption Summary";
    }

    public int getRowCount() {
        return data.size();
    }

    public void loadData() {
        // get all of the customers
        Validate.notNull(fromDate, "Start date must not be null");
        Validate.notNull(toDate, "End date must not be null");
        List<CICustomerStub> customersForEC = customerStubDao.getCustomersForEC(energyCompanyId);
        data = new ArrayList<ModelRow>(customersForEC.size());
        
        for (CICustomerStub customerStub : customersForEC) {
            ModelRow row = new ModelRow();
            try {
                row.customername = customerStub.getCompanyName();
                row.interruptHoursContract = isocCommonStrategy.getAllowedHours(customerStub);
                row.interruptHoursUsed = isocCommonStrategy.getTotalEventHours(customerStub, fromDate, toDate);
                row.interruptHoursRemaining = row.interruptHoursContract - row.interruptHoursUsed;
                
                row.cil = customerPointTypeHelper.getPointValue(customerStub, CICustomerPointType.ContractIntLoad);
                row.noticeMinutes = (int) customerPointTypeHelper.getPointValue(customerStub, CICustomerPointType.MinimumNotice);
                row.advancedElectionPricePerkW = customerPointTypeHelper.getPointValue(customerStub, CICustomerPointType.AdvBuyThrough$);
                row.advancedElectionkW = customerPointTypeHelper.getPointValue(customerStub, CICustomerPointType.AdvBuyThroughKw);
                row.cfd = customerPointTypeHelper.getPointValue(customerStub, CICustomerPointType.ContractFrmDmd);
                data.add(row);
            } catch (Exception e) {
                // not sure what to do here???
                CTILogger.error("Unable to generate row of report for " + customerStub, e);
            }
            
        }
    }

    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }
    
    public void setStartDate(Date startDate) {
        this.fromDate = startDate;
    }
    
    public void setStopDate(Date stopDate) {
        this.toDate = stopDate;
    }

    public CustomerPointTypeHelper getCustomerPointTypeHelper() {
        return customerPointTypeHelper;
    }

    public void setCustomerPointTypeHelper(CustomerPointTypeHelper customerPointTypeHelper) {
        this.customerPointTypeHelper = customerPointTypeHelper;
    }

}
