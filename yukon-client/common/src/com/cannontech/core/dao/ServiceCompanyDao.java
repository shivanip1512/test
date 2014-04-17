package com.cannontech.core.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.common.model.ServiceCompanyDto;

public interface ServiceCompanyDao {
    /**
     * Returns a ServiceCompanyDto for the given service company id.
     * 
     * @param serviceCompanyId
     * @return ServiceCompanyDto
     */
    ServiceCompanyDto getCompanyById(int serviceCompanyId);

    /**
     * Returns a list of all the service companies.
     */
    List<ServiceCompanyDto> getAllServiceCompanies();

    /**
     * Returns a list of all the relevant service companies wrt to a specific energy companies
     */
    List<ServiceCompanyDto> getAllServiceCompaniesForEnergyCompanies(Set<Integer> energyCompanyIds);

    void create(ServiceCompanyDto serviceCompany, int energyCompanyId);

    void update(ServiceCompanyDto serviceCompany);

    void delete(int serviceCompanyId);

    /**
     * This method takes all the inventory that is attached to one service company and
     * moves them over to another service company.
     */
    void moveInventory(int fromServiceCompanyId, int toServiceCompanyId);

    /**
     * This method returns the total number of inventory that are attached to a given service company
     */
    int getInventoryCountForServiceCompany(int serviceCompanyId);

    List<DisplayableServiceCompany> getAllServiceCompanies(Iterable<Integer> energyCompanyIds);

    final class DisplayableServiceCompany {
        private final int serviceCompanyId;
        private final String serviceCompanyName;

        public DisplayableServiceCompany(int serviceCompanyId, String serviceCompanyName) {
            this.serviceCompanyId = serviceCompanyId;
            this.serviceCompanyName = serviceCompanyName;
        }

        public int getServiceCompanyId() {
            return serviceCompanyId;
        }

        public String getServiceCompanyName() {
            return serviceCompanyName;
        }
    }
}
