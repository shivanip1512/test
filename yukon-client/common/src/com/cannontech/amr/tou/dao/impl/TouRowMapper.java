package com.cannontech.amr.tou.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.amr.tou.model.TouAttributeMapping;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;

public class TouRowMapper implements ParameterizedRowMapper<TouAttributeMapping> {

    public TouAttributeMapping mapRow(ResultSet rs, int rowNum) throws SQLException {
        TouAttributeMapping tou = new TouAttributeMapping();

        int touId = rs.getInt("touId");
        tou.setTouId(touId);

        String displayName = rs.getString("displayName");
        tou.setDisplayName(displayName);

        String usageAttribute = rs.getString("usageAttribute");
        Attribute usage = BuiltInAttribute.valueOf(usageAttribute);
        tou.setUsage(usage);
        
        String peakAttribute = rs.getString("peakAttribute");
        Attribute peak = BuiltInAttribute.valueOf(peakAttribute);
        tou.setPeak(peak);
        
        return tou;
    }

}