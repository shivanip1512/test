package com.cannontech.web.cc;

import java.util.List;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.service.CustomerPointService;

public class CustomerListBean {
    private CommercialCurtailmentBean commercialCurtailmentBean;
    private CustomerPointService customerService;

    public CustomerListBean() {
        super();
    }
    
    public List<CICustomerStub> getCustomerList() {
        return customerService.getCustomers(commercialCurtailmentBean.getEnergyCompany());
    }

    public CommercialCurtailmentBean getCommercialCurtailmentBean() {
        return commercialCurtailmentBean;
    }

    public void setCommercialCurtailmentBean(CommercialCurtailmentBean commercialCurtailmentBean) {
        this.commercialCurtailmentBean = commercialCurtailmentBean;
    }

    public CustomerPointService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(CustomerPointService customerService) {
        this.customerService = customerService;
    }


}
