package com.cannontech.web.admin.energyCompany.serviceCompany.service;

import java.util.List;

import com.cannontech.common.model.ServiceCompanyDto;
import com.cannontech.stars.database.db.report.ServiceCompanyDesignationCode;

/**
 * Service for manipulating Service Companies
 */
public interface ServiceCompanyService {
    
    /**
     * Get all of the Designation Codes for a particular Service Company
     * @param serviceCompanyDto     The Service Company we want designation codes for
     * @return                      List of Designation Codes in the ServiceCompanyDesignationCodeDto format
     */
    public List<ServiceCompanyDesignationCode> getDesignationCodesForServiceCompany(ServiceCompanyDto serviceCompanyDto);
    
    /**
     * Get a particular Service Company
     * @param serviceCompanyId  ID of the Service Company we want
     * @return  null|ServiceCompanyDto  The Service Company in a the ServiceCompanyDto format
     */
    public ServiceCompanyDto getServiceCompany(int serviceCompanyId);
    
    /**
     * Create the Service Company, Address, Contact and Designation Code(s) specified in the Dto.  Setting
     * Designation Codes to null will skip their processing.
     * @param serviceCompanyDto     Contains Service Company, Address, Contact, and Designation Code information
     * @param energyCompanyId       ID of the Energy Company this Service Company will belong to
     */
    public void createServiceCompany(ServiceCompanyDto serviceCompanyDto, int energyCompanyId);
    
    /**
     * Delete the Service Company, and associated Contact, Address & Designation Code(s). Designation
     * Codes are deleted atomically
     * @param serviceCompanyId  ID of the Service Company we want to delete
     */
    public void deleteServiceCompany(int serviceCompanyId);
    
    /**
     * Delete the Service Company, and associated Contact, Address & Designation Code(s).  Designation
     * Codes are deleted atomically
     * @param serviceCompanyDto  Dto representation of the Service Company we are deleting
     */
    public void deleteServiceCompany(ServiceCompanyDto serviceCompanyDto);

    /**
     * Update the Service Company and associated Contact, Address & Designation Code(s).  Designation
     * Codes are updated individually if defined.  Setting the Designation Codes to null will skip their update.
     * @param serviceCompany    Dto representation of the Service Company we are updating
     */
    void updateServiceCompany(ServiceCompanyDto serviceCompany);
}
