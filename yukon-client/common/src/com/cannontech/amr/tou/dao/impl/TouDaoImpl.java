package com.cannontech.amr.tou.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.amr.tou.dao.TouDao;
import com.cannontech.amr.tou.model.TouAttributeMapping;

public class TouDaoImpl implements TouDao {
    private SimpleJdbcOperations simpleJdbcTemplate;
    private TouRowMapper touRowMapper = new TouRowMapper();

    String retrieveTouSql = "select * from TouAttributeMapping TAM ";

    String getAllTouMappingsSql = retrieveTouSql + "order by TAM.touID asc ";

    public List<TouAttributeMapping> getTouMappings() {
        List<TouAttributeMapping> results = simpleJdbcTemplate.query(getAllTouMappingsSql,
                                                     touRowMapper);

        return results;
    }

    @Required
    public void setSimpleJdbcTemplate(SimpleJdbcOperations simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

}
