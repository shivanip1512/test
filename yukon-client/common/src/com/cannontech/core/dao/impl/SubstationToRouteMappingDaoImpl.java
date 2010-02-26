package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.model.Route;
import com.cannontech.common.model.Substation;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.SubstationToRouteMappingDao;
import com.cannontech.database.YukonJdbcTemplate;

public class SubstationToRouteMappingDaoImpl implements SubstationToRouteMappingDao {
    private static final SqlStatementBuilder insertSql;
    private static final SqlStatementBuilder deleteSql;
    private static final SqlStatementBuilder deleteBySubIdSql;
    private static final SqlStatementBuilder deleteByRouteIdSql;
    private static final SqlStatementBuilder selectAllSql;
    private static final SqlStatementBuilder selectBySubIdSql;
    private static final SqlStatementBuilder selectAvailableSql;
    private YukonJdbcTemplate template;

    static {
        insertSql = new SqlStatementBuilder();
        insertSql.append("INSERT INTO SubstationToRouteMapping (SubstationID,RouteID,Ordering)");
        insertSql.append("VALUES(?,?,?)");

        deleteSql = new SqlStatementBuilder();
        deleteSql.append("DELETE FROM SubstationToRouteMapping");
        deleteSql.append("WHERE SubstationID = ?");
        deleteSql.append("AND RouteID = ?");

        deleteBySubIdSql = new SqlStatementBuilder();
        deleteBySubIdSql.append("DELETE FROM SubstationToRouteMapping");
        deleteBySubIdSql.append("WHERE SubstationID = ?");

        deleteByRouteIdSql = new SqlStatementBuilder();
        deleteByRouteIdSql.append("DELETE FROM SubstationToRouteMapping");
        deleteByRouteIdSql.append("WHERE RouteID = ?");

        selectAllSql = new SqlStatementBuilder();
        selectAllSql.append("SELECT PAObjectID,PAOName, Type from YukonPAObject");
        selectAllSql.append("WHERE Category = 'ROUTE'");
        selectAllSql.append("AND PAOClass = 'ROUTE'");

        selectBySubIdSql = new SqlStatementBuilder();
        selectBySubIdSql.append("SELECT PAObjectID, PAOName, Type, Ordering");
        selectBySubIdSql.append("FROM YukonPAObject pao, SubstationToRouteMapping stm, Route");
        selectBySubIdSql.append("WHERE pao.PAObjectID = stm.RouteID");
        selectBySubIdSql.append("AND Route.RouteID = stm.RouteID AND stm.SubstationID = ?");
        selectBySubIdSql.append("ORDER BY stm.Ordering");

        selectAvailableSql = new SqlStatementBuilder();
        selectAvailableSql.append(selectAllSql.toString());
        selectAvailableSql.append("AND PAObjectID NOT IN");
        selectAvailableSql.append("(SELECT DISTINCT RouteID FROM SubstationToRouteMapping WHERE SubstationID = ?)");
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(final int substationId, final int routeId, final int ordering) {
        int result = template.update(insertSql.toString(), substationId, routeId, ordering);
        return (result == 1);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(final int substationId, final int routeId) {
        int result = template.update(deleteSql.toString(), substationId, routeId);
        return (result == 1);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean removeAllBySubstationId(final int substationId) {
        int result = template.update(deleteBySubIdSql.toString(), new Object[]{substationId});
        return (result >= 0);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean removeAllByRouteId(final int routeId) {
        int result = template.update(deleteByRouteIdSql.toString(), routeId);
        return (result >= 0);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(final int substationId, final List<Integer> routeIdList) {
        removeAllBySubstationId(substationId);
        int order = 1;
        for (Integer routeId : routeIdList) {
            boolean result = add(substationId, routeId, order);
            if (!result) return false;
            order++;
        }
        return true;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Route> getRoutesBySubstationId(final int substationId) {
        return template.query(selectBySubIdSql.toString(), new ParameterizedRowMapper<Route>() {
            public Route mapRow(ResultSet rs, int rowNum) throws SQLException {
                PaoType type = PaoType.getForDbString(rs.getString("Type"));
                return new Route(rs.getInt("PAObjectID"), rs.getString("PAOName"), rs.getInt("Ordering"), type);
            };
        }, substationId);
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Integer> getRouteIdsBySubstationId(final int substationId) {
        return template.query(selectBySubIdSql.toString(), new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("PAObjectID");
            };
        }, substationId);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Route> getAvailableRoutesBySubstationId(final int substationId) {
        return template.query(selectAvailableSql.toString(), new ParameterizedRowMapper<Route>() {
            public Route mapRow(ResultSet rs, int rowNum) throws SQLException {
                PaoType type = PaoType.getForDbString(rs.getString("Type"));
                return new Route(rs.getInt("PAObjectID"), rs.getString("PAOName"), -1, type);
            }    
        }, substationId);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Route> getAll() {
        return template.query(selectAllSql.toString(), new ParameterizedRowMapper<Route>() {
            public Route mapRow(ResultSet rs, int rowNum) throws SQLException {
                PaoType type = PaoType.getForDbString(rs.getString("Type"));
                return new Route(rs.getInt("PAObjectID"), rs.getString("PAOName"), -1, type);
            }    
        });
    }
    
    @Override
    public List<Substation> getSubstationsForDevice(YukonDevice device) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select distinct Substation.*");
        sql.append("from Substation");
        sql.append("  join SubstationToRouteMapping on SubstationToRouteMapping.SubstationID = Substation.SubstationID");
        sql.append("  join DeviceRoutes on DeviceRoutes.RouteID = SubstationToRouteMapping.RouteID");
        sql.append("where DeviceRoutes.DeviceID").eq(device.getPaoIdentifier().getPaoId());
        sql.append("  and Substation.SubstationId != 0");
        
        List<Substation> result = template.query(sql, new SubstationRowMapper());
        
        return result;
    }

    public void setSimpleJdbcTemplate(final YukonJdbcTemplate template) {
        this.template = template;
    }

}
