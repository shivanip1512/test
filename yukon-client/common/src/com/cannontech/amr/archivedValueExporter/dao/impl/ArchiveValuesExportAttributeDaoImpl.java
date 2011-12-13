package com.cannontech.amr.archivedValueExporter.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportAttributeDao;
import com.cannontech.amr.archivedValueExporter.model.DataSelection;
import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Lists;

public class ArchiveValuesExportAttributeDaoImpl implements ArchiveValuesExportAttributeDao{
    
    public static final String TABLE_NAME = "ArchiveValuesExportAttribute";
    
    private static String insertSql =  getInsertSql() ;
    private static String updateSql =  getUpdateSql();
    private static String removeByFormatIdSql = getRemoveByFormatIdSql();
    
    private final ParameterizedRowMapper<ExportAttribute> rowMapper = createRowMapper();
    private NextValueHelper nextValueHelper;
    private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    @Transactional
    public ExportAttribute create(ExportAttribute attribute) {
        attribute.setAttributeId(nextValueHelper.getNextValue(TABLE_NAME));
        int attributeId = attribute.getAttributeId();
        int formatId = attribute.getFormatId();
        String attributeName = SqlUtils.convertStringToDbValue(attribute.getAttribute().getKey());
        String dataSelection = SqlUtils.convertStringToDbValue(attribute.getDataSelection().name());
        int daysPrevious = attribute.getDaysPrevious();
        yukonJdbcTemplate.update(insertSql,attributeId,formatId,attributeName,dataSelection, daysPrevious);   
        return attribute;
    }
    
    @Override
    @Transactional
    public List<ExportAttribute> create(List<ExportAttribute> attributes) {
        List<Object[]> batchArgs = Lists.newArrayList();
        for (ExportAttribute attribute : attributes) {
            attribute.setAttributeId(nextValueHelper.getNextValue(TABLE_NAME));
            int attributeId = attribute.getAttributeId();
            int formatId = attribute.getFormatId();
            String attributeName = SqlUtils.convertStringToDbValue(attribute.getAttribute().getKey());
            String dataSelection = SqlUtils.convertStringToDbValue(attribute.getDataSelection().name());
            int daysPrevious = attribute.getDaysPrevious();
            batchArgs.add(new Object[] {attributeId,formatId,attributeName,dataSelection, daysPrevious});
        }
        yukonJdbcTemplate.batchUpdate(insertSql, batchArgs);
        return attributes;
    }
        
    @Override
    @Transactional
    public ExportAttribute update(ExportAttribute attribute) {
        int attributeId = attribute.getAttributeId();
        int formatId = attribute.getFormatId();
        String attributeName =  SqlUtils.convertStringToDbValue(attribute.getAttribute().getDescription());
        String dataSelection =  SqlUtils.convertStringToDbValue(attribute.getDataSelection().name());
        int daysPrevious = attribute.getDaysPrevious();
        yukonJdbcTemplate.update(updateSql,attributeId,attributeName, formatId,dataSelection, daysPrevious, attributeId );   
        return attribute;
    }
    
    @Override
    @Transactional
    public boolean removeByFormatId(int formatId) {
        int rowsAffected =  yukonJdbcTemplate.update(removeByFormatIdSql, formatId);
        return rowsAffected == 1;
    }
        
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ExportAttribute> getByFormatId(int formatId) {
        List<ExportAttribute> attributes = null;
        try {
            SqlStatementBuilder selectByFormatIdSql = getSelectByFormatIdSql().eq(formatId);
            attributes = yukonJdbcTemplate.query(selectByFormatIdSql, rowMapper);
        } catch (EmptyResultDataAccessException ex){
            throw new NotFoundException("The format id supplied does not exist.");
        }
        return attributes;
    }
    
    private SqlStatementBuilder getSelectByFormatIdSql() {
        SqlStatementBuilder selectByFormatIdSql = new SqlStatementBuilder();
        selectByFormatIdSql.append("SELECT AttributeID, FormatID, AttributeName, DataSelection, DaysPrevious ");
        selectByFormatIdSql.append("FROM ");
        selectByFormatIdSql.append(TABLE_NAME);
        selectByFormatIdSql.append(" WHERE FormatID");
        return selectByFormatIdSql;
    }

    private static String getUpdateSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ArchiveValuesExportAttribute ");
        sb.append(TABLE_NAME);
        sb.append(" SET AttributeID = ?, FormatID = ?, AttributeName = ?, DataSelection = ?, DaysPrevious = ? ");
        sb.append("WHERE AttributeID = ?");
        return sb.toString();
    }
    
    private static String getInsertSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(TABLE_NAME);
        sb.append(" (AttributeID,FormatID,AttributeName,DataSelection,DaysPrevious) ");
        sb.append("VALUES (?,?,?,?,?) ");
        return sb.toString();
    }

    private static String getRemoveByAttributeIdSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(TABLE_NAME);
        sb.append(" WHERE AttributeID = ?");
        return sb.toString();
    }
    
    private static String getRemoveByFormatIdSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(TABLE_NAME);
        sb.append(" WHERE FormatID = ?");
        return sb.toString();
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    private ParameterizedRowMapper<ExportAttribute> createRowMapper() {
        final ParameterizedRowMapper<ExportAttribute> mapper = new ParameterizedRowMapper<ExportAttribute>() {
            @Override
            public ExportAttribute mapRow(ResultSet rs, int rowNum) throws SQLException {
                final ExportAttribute attribute = new ExportAttribute();
                attribute.setFormatId(rs.getInt("FormatID"));
                attribute.setAttributeId(rs.getInt("AttributeID"));
                attribute.setAttribute(BuiltInAttribute.valueOf(rs.getString("AttributeName")));
                attribute.setDataSelection(DataSelection.valueOf(rs.getString("DataSelection")));
                attribute.setDaysPrevious(rs.getInt("DaysPrevious"));
                return attribute;
            }
        };
        return mapper;
    }
    
    
}
