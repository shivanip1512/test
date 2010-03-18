package com.cannontech.core.dao;

import com.cannontech.common.model.ServiceCompanyDto;

public interface ServiceCompanyDao {

    /**
     * Returns a ServiceCompanyDto for the given service company id.
     * @param serviceCompanyId
     * @return ServiceCompanyDto
     */
    public ServiceCompanyDto getCompanyById(int serviceCompanyId);

}
