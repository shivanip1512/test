package com.cannontech.core.dao.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.LMGroupDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.loadcontrol.loadgroup.model.SEPGroupAttributes;
import com.cannontech.stars.dr.hardware.model.ExpressComAddressView;

public class LMGroupDaoImpl implements LMGroupDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    
    @Override
    public SEPGroupAttributes getSEPAttributesGroupForSepGroup(int groupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UtilityEnrollmentGroup,RampIn,RampOut");
        sql.append("FROM LMGroupSEP");
        sql.append("WHERE DeviceId").eq(groupId);
        
        return yukonJdbcTemplate.queryForObject(sql, new YukonRowMapper<SEPGroupAttributes>() {

            @Override
            public SEPGroupAttributes mapRow(YukonResultSet rs) throws SQLException {
                
                byte enrollmentGroup = rs.getByte("UtilityEnrollmentGroup");
                int rampIn = rs.getInt("RampIn");
                int rampOut = rs.getInt("RampOut");
                
                SEPGroupAttributes attributes = new SEPGroupAttributes(enrollmentGroup, rampIn, rampOut);
                return attributes;
            }
        });
    }
    
    @Override
    public ExpressComAddressView getExpressComAddressing(int groupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT LMGroupId, RouteId, SerialNumber, ServiceAddress, GeoAddress,");
        sql.append("SubstationAddress, FeederAddress, ZipCodeAddress, UDAddress, ProgramAddress,");
        sql.append("SplinterAddress, AddressUsage, RelayUsage, ProtocolPriority");
        sql.append("FROM ExpressComAddress_View");
        sql.append("WHERE LMGroupId").eq(groupId);
        
        return yukonJdbcTemplate.queryForObject(sql, new YukonRowMapper<ExpressComAddressView>() {

            @Override
            public ExpressComAddressView mapRow(YukonResultSet rs) throws SQLException {
                ExpressComAddressView view = new ExpressComAddressView();
                view.setGroupId(rs.getInt("LMGroupId"));
                view.setRouteId(rs.getInt("RouteId"));
                view.setSerialNumber(rs.getString("SerialNumber"));
                view.setSpid(rs.getInt("ServiceAddress"));
                view.setGeo(rs.getInt("GeoAddress"));
                view.setSubstation(rs.getInt("SubstationAddress"));
                view.setFeeder(rs.getInt("FeederAddress"));
                view.setZip(rs.getInt("ZipCodeAddress"));
                view.setUser(rs.getInt("UDAddress"));
                view.setProgram(rs.getInt("ProgramAddress"));
                view.setSplinter(rs.getInt("SplinterAddress"));
                view.setUsage(rs.getString("AddressUsage"));
                view.setRelay(rs.getString("RelayUsage"));
                view.setPriority(rs.getInt("ProtocolPriority"));
                return view;
            }
        });
    }
    
}