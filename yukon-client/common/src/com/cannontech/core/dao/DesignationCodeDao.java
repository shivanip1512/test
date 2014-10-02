package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.common.model.DesignationCodeDto;

public interface DesignationCodeDao {

    /**
     * Fetch all of the Designation Codes serviced by a specific Service Company
     *
     * @param serviceCompanyId ID of the Service Company we are searching by
     * @return List of DesignationCodeDtos
     */
    public List<DesignationCodeDto> getDesignationCodesByServiceCompanyId(int serviceCompanyId);

    /**
     * Insert a collection of designation codes into the DB. Used in conjunction with creating/editing
     * a Service Company.
     *
     * @param designationCodes List of DesignationCodeDtos
     */
    public void bulkAdd(List<DesignationCodeDto> designationCodes);

    /**
     * Delete a specific Designation Code
     *
     * @param designationCode The designationCodeDto we want to delete
     */
    public void delete(DesignationCodeDto designationCode);

    /**
     * Delete a collection of designation codes. Used in conjucntion with updating/deleting a service company
     *
     * @param designationCodes List of DesignationCodeDtos
     */
    public void bulkDelete(List<DesignationCodeDto> designationCodes);
}
