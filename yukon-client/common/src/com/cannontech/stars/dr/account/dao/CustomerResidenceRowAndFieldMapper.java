package com.cannontech.stars.dr.account.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.database.RowAndFieldMapper;
import com.cannontech.database.SqlUtils;
import com.cannontech.stars.dr.account.model.CustomerResidence;

public class CustomerResidenceRowAndFieldMapper implements RowAndFieldMapper<CustomerResidence> {

    public Number getPrimaryKey(CustomerResidence customerResidence) {
        return customerResidence.getAccountSiteId();
    }
    
    public void setPrimaryKey(CustomerResidence customerResidence, int value) {
    	customerResidence.setAccountSiteId(value);
    }
    
    public void extractValues(MapSqlParameterSource p, CustomerResidence customerResidence) {
        p.addValue("ResidenceTypeId", customerResidence.getResidenceTypeId());
        p.addValue("ConstructionMaterialId", customerResidence.getConstructionMaterialId());
        p.addValue("DecadeBuiltId", customerResidence.getDecadeBuiltId());
        p.addValue("SquareFeetId", customerResidence.getSquareFeetId());
        p.addValue("InsulationDepthId", customerResidence.getInsulationDepthId());
        p.addValue("GeneralConditionId", customerResidence.getGeneralConditionId());
        p.addValue("MainCoolingSystemId", customerResidence.getMainCoolingSystemId());
        p.addValue("MainHeatingSystemId", customerResidence.getMainHeatingSystemId());
        p.addValue("NumberOfOccupantsId", customerResidence.getNumberOfOccupantsId());
        p.addValue("OwnershipTypeId", customerResidence.getOwnershipTypeId());
        p.addValue("MainFuelTypeId", customerResidence.getMainFuelTypeId());
        p.addValue("Notes", SqlUtils.convertStringToDbValue(customerResidence.getNotes()));
    }
    
    public CustomerResidence mapRow(ResultSet rs, int rowNum) throws SQLException {
    	CustomerResidence customerResidence = new CustomerResidence();
    	customerResidence.setAccountSiteId(rs.getInt("AccountSiteId"));
    	customerResidence.setResidenceTypeId(rs.getInt("ResidenceTypeId"));
    	customerResidence.setConstructionMaterialId(rs.getInt("ConstructionMaterialId"));
    	customerResidence.setDecadeBuiltId(rs.getInt("DecadeBuiltId"));
    	customerResidence.setSquareFeetId(rs.getInt("SquareFeetId"));
    	customerResidence.setInsulationDepthId(rs.getInt("InsulationDepthId"));
    	customerResidence.setGeneralConditionId(rs.getInt("GeneralConditionId"));
    	customerResidence.setMainCoolingSystemId(rs.getInt("MainCoolingSystemId"));
    	customerResidence.setMainHeatingSystemId(rs.getInt("MainHeatingSystemId"));
    	customerResidence.setNumberOfOccupantsId(rs.getInt("NumberOfOccupantsId"));
    	customerResidence.setOwnershipTypeId(rs.getInt("OwnershipTypeId"));
    	customerResidence.setMainFuelTypeId(rs.getInt("MainFuelTypeId"));
    	customerResidence.setNotes(SqlUtils.convertDbValueToString(rs, "Notes"));
        return customerResidence;
    }
}
