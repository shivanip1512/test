package com.cannontech.stars.web.bean;

import java.util.ArrayList;
import com.cannontech.database.db.stars.report.ServiceCompanyDesignationCode;

public class ServiceCompanyBean 
{
    private ArrayList designationCodes;    
    private Integer serviceCompanyID;
    
    public ArrayList getDesignationCodes()
    {
        if(serviceCompanyID != null)
        {
            designationCodes = ServiceCompanyDesignationCode.getAllCodesForServiceCompany(serviceCompanyID.intValue());
            return designationCodes;
        }
        
        return new ArrayList(0);
    }
    
    public void setDesignationCodes(ArrayList setCodes)
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
