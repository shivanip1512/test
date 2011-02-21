package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.common.model.DesignationCodeDto;

public interface DesignationCodeDao {
    public DesignationCodeDto getServiceCompanyDesignationCode(int id);
    
    public List<DesignationCodeDto> getDesignationCodesByServiceCompanyId(int serviceCompanyId);
    
    public void add(DesignationCodeDto designationCode);

    public void bulkAdd(List<DesignationCodeDto> designationCodes);
    
    public void update(DesignationCodeDto designationCode);
    
    public void delete(DesignationCodeDto designationCode);
    
    public void delete(int id);
    
    public void bulkDelete(List<DesignationCodeDto> designationCodes);
    
    
}
