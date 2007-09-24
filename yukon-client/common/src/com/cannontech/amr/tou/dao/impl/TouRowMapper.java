package com.cannontech.amr.tou.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.amr.tou.model.TouAttributeMapper;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;

public class TouRowMapper implements ParameterizedRowMapper<TouAttributeMapper> {

    public TouAttributeMapper mapRow(ResultSet rs, int rowNum) throws SQLException {
        TouAttributeMapper tou = new TouAttributeMapper();

        int touId = rs.getInt("touId");
        tou.setTouId(touId);

        String displayName = rs.getString("displayName");
        tou.setDisplayName(displayName);

        String usageAttribute = rs.getString("usageAttribute");
        try {
            Attribute usage = BuiltInAttribute.valueOf(usageAttribute);
            tou.setUsage(usage);
        } catch (Exception e) {
            tou.setUsage(null);
        }

        String peakAttribute = rs.getString("peakAttribute");
        try {
            Attribute peak = BuiltInAttribute.valueOf(peakAttribute);
            tou.setPeak(peak);
        } catch (Exception e) {
            tou.setPeak(null);
        }

        return tou;
    }

}