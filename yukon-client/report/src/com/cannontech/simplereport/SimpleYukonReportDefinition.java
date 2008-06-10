package com.cannontech.simplereport;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.simplereport.reportlayoutdata.ReportLayoutData;
import com.cannontech.web.input.InputRoot;

public class SimpleYukonReportDefinition<T extends BareReportModel> implements YukonReportDefinition<T>, BeanFactoryAware, BeanNameAware {
    private String reportModelName;
    private String name;
    private BeanFactory beanFactory;
    private InputRoot inputs;
    private ReportLayoutData reportLayoutData;
    
    public String getReportModelName() {
        return reportModelName;
    }
    public void setReportModelName(String reportModelName) {
        this.reportModelName = reportModelName;
    }

    @SuppressWarnings("unchecked")
    public T createBean() {
        return (T) beanFactory.getBean(getReportModelName());
    }
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
    public InputRoot getInputs() {
        return inputs;
    }
    public void setInputs(InputRoot inputs) {
        this.inputs = inputs;
    }
    public String getName() {
        return name;
    }
    public void setBeanName(String name) {
        this.name = name;
    }
    public ReportLayoutData getReportLayoutData() {
        return reportLayoutData;
    }
    public void setReportLayoutData(ReportLayoutData reportLayoutData) {
        this.reportLayoutData = reportLayoutData;
    }

}
