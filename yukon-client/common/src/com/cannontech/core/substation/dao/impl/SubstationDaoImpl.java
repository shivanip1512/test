package com.cannontech.core.substation.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.common.model.Substation;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.substation.dao.SubstationDao;
import com.cannontech.core.substation.model.SubstationRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;

public class SubstationDaoImpl implements SubstationDao {
    private static final RowMapper<Substation> rowMapper;
    private static final SqlStatementBuilder insertSql;
    private static final SqlStatementBuilder updateSql;
    private static final SqlStatementBuilder deleteSql;
    private static final SqlStatementBuilder selectAllSql;
    private static final SqlStatementBuilder selectByIdSql;
    private static final SqlStatementBuilder selectByNameSql;
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    
    static {
        rowMapper = new SubstationRowMapper();

        insertSql = new SqlStatementBuilder();
        insertSql.append("INSERT INTO Substation (SubstationID,SubstationName)");
        insertSql.append("VALUES(?,?)");
        
        updateSql = new SqlStatementBuilder();
        updateSql.append("UPDATE Substation");
        updateSql.append("SET SubstationName = ?");
        updateSql.append("WHERE SubstationID = ?");
        
        deleteSql = new SqlStatementBuilder();
        deleteSql.append("DELETE FROM Substation");
        deleteSql.append("WHERE SubstationID = ?");
        
        selectAllSql = new SqlStatementBuilder();
        selectAllSql.append("SELECT SubstationID, SubstationName");
        selectAllSql.append("FROM Substation");
        selectAllSql.append("WHERE SubstationID != 0");
        
        selectByIdSql = new SqlStatementBuilder();
        selectByIdSql.append(selectAllSql.toString());
        selectByIdSql.append("AND SubstationID = ?");

        selectByNameSql = new SqlStatementBuilder();
        selectByNameSql.append(selectAllSql.toString());
        selectByNameSql.append("AND SubstationName = ?");
    }
    
    public SubstationDaoImpl() {
        
    }
    
    public boolean add(final Substation substation) {
        final int id = nextValueHelper.getNextValue("Substation");
        substation.setId(id);
        
        int result = yukonJdbcTemplate.update(insertSql.toString(), 
                                     substation.getId(),
                                     substation.getName());
        return (result == 1);
    }

    public boolean remove(final Substation substation) {
        int result = yukonJdbcTemplate.update(deleteSql.toString(), substation.getId());
        return (result == 1);
    }

    public boolean update(final Substation substation) {
        int result = yukonJdbcTemplate.update(updateSql.toString(), 
                                     substation.getName(),
                                     substation.getId());
        return (result == 1);
    }
    
    public Substation getByName(final String name) {
    	try {
    		return yukonJdbcTemplate.queryForObject(selectByNameSql.toString(), rowMapper, name);
    	} catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A Substation with name " + name + " cannot be found.");
        }
    }
    
    public Substation getById(final int id) {
    	try {
    		return yukonJdbcTemplate.queryForObject(selectByIdSql.toString(), rowMapper, id);
    	} catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A Substation with id " + id + " cannot be found.");
        }
    }
    
    @Override
    public List<Substation> getAllSubstationsByEnergyCompanyId(int energyCompanyId) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT S.*");
    	sql.append("FROM ECToSubstationMapping ECTSM");
    	sql.append("  JOIN Substation S ON ECTSM.SubstationId = S.SubstationId");
    	sql.append("WHERE ECTSM.EnergyCompanyId").eq(energyCompanyId);
    	
    	return yukonJdbcTemplate.query(sql, rowMapper);
    }

    public List<Substation> getAll() {
        return yukonJdbcTemplate.query(selectAllSql.toString(), rowMapper);
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
    
    @Autowired
    public void setNextValueHelper(final NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

}
