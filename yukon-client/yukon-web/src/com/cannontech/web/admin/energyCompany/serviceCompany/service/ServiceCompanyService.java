package com.cannontech.web.admin.energyCompany.serviceCompany.service;

import com.cannontech.common.model.ServiceCompanyDto;

/**
 * Service for manipulating Service Companies
 */
public interface ServiceCompanyService {
    /**
     * Get a particular Service Company
     *
     * @param serviceCompanyId ID of the Service Company we want
     * @return null|ServiceCompanyDto The Service Company in a the ServiceCompanyDto format
     */
    ServiceCompanyDto getServiceCompany(int serviceCompanyId);

    /**
     * Create the Service Company, Address, Contact and Designation Code(s) specified in the Dto. Setting
     * Designation Codes to null will skip their processing.
     *
     * @param serviceCompanyDto Contains Service Company, Address, Contact, and Designation Code information
     * @param energyCompanyId ID of the Energy Company this Service Company will belong to
     */
    void createServiceCompany(ServiceCompanyDto serviceCompanyDto, int energyCompanyId);

    /**
     * Delete the Service Company, and associated Contact, Address & Designation Code(s). Designation
     * Codes are deleted atomically
     *
     * @param serviceCompanyDto Dto representation of the Service Company we are deleting
     */
    void deleteServiceCompany(ServiceCompanyDto serviceCompanyDto);

    /**
     * Update the Service Company and associated Contact, Address & Designation Code(s). Designation
     * Codes are updated individually if defined. Setting the Designation Codes to null will skip their
     * update.
     *
     * @param serviceCompany Dto representation of the Service Company we are updating
     */
    void updateServiceCompany(ServiceCompanyDto serviceCompany);
}
