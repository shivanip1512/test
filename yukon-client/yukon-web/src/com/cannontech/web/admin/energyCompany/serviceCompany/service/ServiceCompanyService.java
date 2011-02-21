package com.cannontech.web.admin.energyCompany.serviceCompany.service;

import java.util.List;

import com.cannontech.common.model.ServiceCompanyDto;
import com.cannontech.database.db.stars.report.ServiceCompanyDesignationCode;

public interface ServiceCompanyService {
    public List<ServiceCompanyDto> getAllServiceCompanies(int energyCompanyId);
    
    public List<ServiceCompanyDesignationCode> getDesignationCodesForServiceCompany(ServiceCompanyDto serviceCompanyDto);
    
    public ServiceCompanyDto getServiceCompany(int serviceCompanyId);
    
    public void createServiceCompany(ServiceCompanyDto serviceCompanyDto, int energyCompanyId);
    
    public void deleteServiceCompany(int serviceCompanyId);
    
    public void deleteServiceCompany(ServiceCompanyDto servicdCompanyDto);

    void updateServiceCompany(ServiceCompanyDto serviceCompany);
}
