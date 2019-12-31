package com.cannontech.services.systemDataPublisher.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.stereotype.Repository;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.NetworkManagerJdbcTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.services.systemDataPublisher.dao.SystemDataPublisherDao;
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;

@Repository
public class SystemDataPublisherDaoImpl implements SystemDataPublisherDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NetworkManagerJdbcTemplate networkManagerJdbcTemplate;

    private static final ColumnMapRowMapper columnMapRowMapper = new ColumnMapRowMapper();
    private static final Logger log = YukonLogManager.getLogger(SystemDataPublisherDaoImpl.class);

    @Override
    public List<Map<String, Object>> getSystemData(DictionariesField dictionariesField) {
        List<Map<String, Object>> systemData = null;
        try {
            systemData = jdbcTemplate.query(dictionariesField.getSource(), columnMapRowMapper);
        } catch (EmptyResultDataAccessException e) {
            log.debug("No result found for field = " + dictionariesField.getField());
        }
        return systemData;
    }

    @Override
    public List<Map<String, Object>> getNMSystemData(DictionariesField dictionariesField) {
        List<Map<String, Object>> nmSystemData = null;
        try {
            nmSystemData = networkManagerJdbcTemplate.query(dictionariesField.getSource(), columnMapRowMapper);
        } catch(EmptyResultDataAccessException e) {
            log.debug("No result found for NM field = " + dictionariesField.getField());
        }
        return nmSystemData;
    }

}
