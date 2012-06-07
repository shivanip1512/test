package com.cannontech.stars.web.bean;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.stars.database.db.report.ServiceCompanyDesignationCode;

public class ServiceCompanyBean 
{
    private List<ServiceCompanyDesignationCode> designationCodes;    
    private Integer serviceCompanyID;
    
    public List<ServiceCompanyDesignationCode> getDesignationCodes()
    {
        if(serviceCompanyID != null)
        {
            designationCodes = ServiceCompanyDesignationCode.getServiceCompanyDesignationCodes(serviceCompanyID.intValue());
            return designationCodes;
        }
        
        return new ArrayList<ServiceCompanyDesignationCode>(0);
    }
    
    public void setDesignationCodes(final List<ServiceCompanyDesignationCode> setCodes)
    {
        designationCodes = setCodes;
    }
    
    public Integer getServiceCompanyID()
    {
        return serviceCompanyID;
    }
    
    public void setServiceCompanyID(Integer newID)
    {
        serviceCompanyID = newID;
    }
    
}
