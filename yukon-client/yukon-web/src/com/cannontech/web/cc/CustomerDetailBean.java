package com.cannontech.web.cc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.service.CustomerPointService;
import com.cannontech.database.db.customer.CICustomerPointType;

public class CustomerDetailBean {
    private CICustomerStub customer;
    private CommercialCurtailmentBean commercialCurtailmentBean;
    private CustomerPointService customerService;
    private DataModel pointsModel;
    private Map<CICustomerPointType, BigDecimal> pointValueCache = new TreeMap<CICustomerPointType, BigDecimal>();
    private List<CICustomerPointType> pointTypeList;

    public CustomerDetailBean() {
        super();
    }
    
    public CICustomerStub getCustomer() {
        return customer;
    }

    public void setCustomer(CICustomerStub customer) {
        this.customer = customer;
        initializePointCache();
    }

    private void initializePointCache() {
        pointValueCache = customerService.getPointValueCache(getCustomer());
        pointTypeList = customerService.getPointTypeList(getCustomer());
        pointsModel = new ListDataModel(new ArrayList<CICustomerPointType>(pointTypeList));
    }

    public String showCustomer() {
        ExternalContext externalContext = 
            FacesContext.getCurrentInstance().getExternalContext();
        String customerIdStr = 
            (String) externalContext.getRequestParameterMap().get("customerId");
        int customerId = Integer.parseInt(customerIdStr);
        setCustomer(customerService.getCustomer(customerId));
        
        return "customerDetail";
    }
    
    public String createPoint() {
        CICustomerPointType pointType = (CICustomerPointType) pointsModel.getRowData();
        customerService.createPoint(getCustomer(), pointType);
        pointValueCache.put(pointType, BigDecimal.ZERO);
        return null;
    }
    
    public String apply() {
        savePoints();
        return null;
    }
    
    public String cancel() {
        return "customerList";
    }
    
    public String save() {
        savePoints();
        return "customerList";
    }
    
    public void savePoints() {
        customerService.savePointValues(getCustomer(), getPointValueCache());
    }
    
    public DataModel getPointsModel() {
        return pointsModel;
    }
    
    public boolean getRowPointExists() {
        CICustomerPointType pointType = (CICustomerPointType) pointsModel.getRowData();
        return getCustomer().getPointData().containsKey(pointType);
    }
    
    public String getSatisfiedPointGroups() {
        List<String> satisfiedPointGroups = customerService.getSatisfiedPointGroups(getCustomer());
        if (satisfiedPointGroups.isEmpty()) {
            return "none";
        } else {
            return StringUtils.join(satisfiedPointGroups.iterator(), ", ");
        }
    }
    
    public void setCommercialCurtailmentBean(CommercialCurtailmentBean commercialCurtailmentBean) {
        this.commercialCurtailmentBean = commercialCurtailmentBean;
    }
    
    public CommercialCurtailmentBean getCommercialCurtailmentBean() {
        return commercialCurtailmentBean;
    }
    
    public void setCustomerService(CustomerPointService customerService) {
        this.customerService = customerService;
    }
    
    public CustomerPointService getCustomerService() {
        return customerService;
    }

    public Map<CICustomerPointType, BigDecimal> getPointValueCache() {
        return pointValueCache;
    }

    public void setPointValueCache(Map<CICustomerPointType, BigDecimal> pointValueCache) {
        this.pointValueCache = pointValueCache;
    }

}
