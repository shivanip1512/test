package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.Validate;

import com.cannontech.cc.dao.BaseEventDao;
import com.cannontech.cc.dao.CustomerStubDao;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.service.IsocCommonStrategy;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.support.CustomerPointTypeHelper;

public class CurtailmentEventSummaryModel extends BareDatedReportModelBase<CurtailmentEventSummaryModel.ModelRow> implements EnergyCompanyModelAttributes {
    private int energyCompanyId;
    
    private CustomerStubDao customerStubDao = (CustomerStubDao) YukonSpringHook.getBean("customerStubDao");
    private BaseEventDao baseEventDao = (BaseEventDao) YukonSpringHook.getBean("baseEventDao");
    private CustomerPointTypeHelper customerPointTypeHelper = (CustomerPointTypeHelper) YukonSpringHook.getBean("customerPointTypeHelper");
    private IsocCommonStrategy isocCommonStrategy = (IsocCommonStrategy) YukonSpringHook.getBean("isocCommonStrategy");
    
    private List<ModelRow> data = Collections.emptyList();
    
    public CurtailmentEventSummaryModel() {
    }
    
    static public class ModelRow {
        public String customerName;
        public String eventNumber;
        public Date startDate;
        public Date notificationDate;
        public Date stopDate;
        public Double durationHours;
        public String type;
        public String state;
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
        return "Curtailment Event Summary";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        // get all of the customers
        Validate.notNull(getStartDate(), "Start date must not be null");
        Validate.notNull(getStopDate(), "End date must not be null");
        
        List<CICustomerStub> customersForEC = customerStubDao.getCustomersForEC(energyCompanyId);
        data = new ArrayList<ModelRow>(customersForEC.size());
        
        for (CICustomerStub customerStub : customersForEC) {
            try {
            	//Allow events with an earlier start(from) date to be included
                List<BaseEvent> allEvents = baseEventDao.getAllForCustomerStopsWithin(customerStub, getStartDate(), getStopDate());
                for (BaseEvent event : allEvents) {
                    ModelRow row = new ModelRow();
                    row.customerName = customerStub.getCompanyName();
                    row.eventNumber = event.getDisplayName();
                    row.startDate = event.getStartTime();
                    row.stopDate = event.getStopTime();
                    row.notificationDate = event.getNotificationTime();
                    boolean doesContribute = isocCommonStrategy.doesEventContributeToAllowedHours(event);
                    row.durationHours = doesContribute ? (double)event.getDuration() / 60 : 0;
                    row.type = event.getProgram().getProgramType().getName() + " - " + event.getProgram().getName();
                    row.state = event.getStateDescription();
                    data.add(row);
                }
            } catch (Exception e) {
                // not sure what to do here???
                CTILogger.error("Unable to generate row of report for " + customerStub, e);
            }
            
        }
    }

    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }
    
    public CustomerPointTypeHelper getCustomerPointTypeHelper() {
        return customerPointTypeHelper;
    }

    public void setCustomerPointTypeHelper(CustomerPointTypeHelper customerPointTypeHelper) {
        this.customerPointTypeHelper = customerPointTypeHelper;
    }

}
