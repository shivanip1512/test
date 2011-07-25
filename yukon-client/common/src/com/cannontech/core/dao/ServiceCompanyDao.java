package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.common.model.ServiceCompanyDto;

public interface ServiceCompanyDao {

    /**
     * Returns a ServiceCompanyDto for the given service company id.
     * @param serviceCompanyId
     * @return ServiceCompanyDto
     */
    public ServiceCompanyDto getCompanyById(int serviceCompanyId);

    /**
     * Returns a list of all the service companies.
     */
    public List<ServiceCompanyDto> getAllServiceCompanies();
    
    /**
     * Returns a list of all the relevant service companies wrt to a specific energy company
     * @param energyCompanyId
     * @return list of service company dtos
     */
    public List<ServiceCompanyDto> getAllServiceCompaniesForEnergyCompany(int energyCompanyId);
    
    public void create(ServiceCompanyDto serviceCompany, int energyCompanyId);
    
    public void update(ServiceCompanyDto serviceCompany);
    
    public void delete(int serviceCompanyId);

    /**
     * This method takes all the inventory that is attached to one service company and 
     * moves them over to another service company.
     */
    public void moveInventory(int fromServiceCompanyId, int toServiceCompanyId);

    /**
     * This method returns the total number of inventory that are attached to a given service company
     */
    public int getInventoryCountForServiceCompany(int serviceCompanyId);
}
