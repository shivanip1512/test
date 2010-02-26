package com.cannontech.core.dao.impl;

import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.model.Substation;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.SubstationDao;
import com.cannontech.database.incrementer.NextValueHelper;

public class SubstationDaoImpl implements SubstationDao {
    private static final ParameterizedRowMapper<Substation> rowMapper;
    private static final SqlStatementBuilder insertSql;
    private static final SqlStatementBuilder updateSql;
    private static final SqlStatementBuilder deleteSql;
    private static final SqlStatementBuilder selectAllSql;
    private static final SqlStatementBuilder selectByIdSql;
    private static final SqlStatementBuilder selectByNameSql;
    private SimpleJdbcTemplate template;
    private NextValueHelper nextValueHelper;
    
    static {
        rowMapper = new SubstationRowMapper();

        insertSql = new SqlStatementBuilder();
        insertSql.append("INSERT INTO Substation (SubstationID,SubstationName,LMRouteID)");
        insertSql.append("VALUES(?,?,?)");
        
        updateSql = new SqlStatementBuilder();
        updateSql.append("UPDATE Substation");
        updateSql.append("SET SubstationName = ?, LMRouteID = ?");
        updateSql.append("WHERE SubstationID = ?");
        
        deleteSql = new SqlStatementBuilder();
        deleteSql.append("DELETE FROM Substation");
        deleteSql.append("WHERE SubstationID = ?");
        
        selectAllSql = new SqlStatementBuilder();
        selectAllSql.append("SELECT SubstationID, SubstationName, LMRouteID");
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
        
        int result = template.update(insertSql.toString(), 
                                     substation.getId(),
                                     substation.getName(), 
                                     substation.getRouteId());
        return (result == 1);
    }

    public boolean remove(final Substation substation) {
        int result = template.update(deleteSql.toString(), substation.getId());
        return (result == 1);
    }

    public boolean update(final Substation substation) {
        int result = template.update(updateSql.toString(), 
                                     substation.getName(),
                                     substation.getRouteId(),
                                     substation.getId());
        return (result == 1);
    }
    
    public Substation getByName(final String name) {
    	try {
    		return template.queryForObject(selectByNameSql.toString(), rowMapper, name);
    	} catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A Substation with name " + name + " cannot be found.");
        }
    }
    
    public Substation getById(final int id) {
    	try {
    		return template.queryForObject(selectByIdSql.toString(), rowMapper, id);
    	} catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("A Substation with id " + id + " cannot be found.");
        }
    }

    public List<Substation> getAll() {
        return template.query(selectAllSql.toString(), rowMapper);
    }

    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate template) {
        this.template = template;
    }
    
    public void setNextValueHelper(final NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

}
