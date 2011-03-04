package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.common.model.DesignationCodeDto;

public interface DesignationCodeDao {
    
    /**
     * Fetch a specific Designation Code
     * @param id    ID of the Designation Code we want
     * @return      A DesignationCodeDto
     */
    public DesignationCodeDto getServiceCompanyDesignationCode(int id);
    
    /**
     * Fetch all of the Designation Codes serviced by a specific Service Company
     * @param serviceCompanyId  ID of the Service Company we are searching by
     * @return                  List of DesignationCodeDtos
     */
    public List<DesignationCodeDto> getDesignationCodesByServiceCompanyId(int serviceCompanyId);
    
    /**
     * Insert 1 Designation Code into the DB
     * @param designationCode
     */
    public void add(DesignationCodeDto designationCode);

    /**
     * Insert a collection of designation codes into the DB.  Used in conjunction with creating/editing
     * a Service Company.
     * @param designationCodes  List of DesignationCodeDtos
     */
    public void bulkAdd(List<DesignationCodeDto> designationCodes);
    
    /**
     * Update an existing Designation Code
     * @param designationCode   An updated version of the designation code we are commiting to the db
     *                          assumes the designation code has been validated and contains an id
     */
    public void update(DesignationCodeDto designationCode);
    
    /**
     * Delete a specific Designation Code
     * @param designationCode   The designationCodeDto we want to delete
     */
    public void delete(DesignationCodeDto designationCode);
    
    /**
     * Delete a specific Designation Code by its id
     * @param id    The id of the designation code we want to delete
     */
    public void delete(int id);
    
    /**
     * Delete a collection of designation codes.  Used in conjucntion with updating/deleting a service company
     * @param designationCodes  List of DesignationCodeDtos
     */
    public void bulkDelete(List<DesignationCodeDto> designationCodes);
}
