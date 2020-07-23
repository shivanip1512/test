package com.cannontech.amr.archivedValueExporter.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportAttributeDao;
import com.cannontech.amr.archivedValueExporter.model.DataSelection;
import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class ArchiveValuesExportAttributeDaoImpl implements ArchiveValuesExportAttributeDao{
    
    public static final String TABLE_NAME = "ArchiveValuesExportAttribute";
        
    private final YukonRowMapper<ExportAttribute> rowMapper = createRowMapper();
    @Autowired  private NextValueHelper nextValueHelper;
    @Autowired  private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired AttributeService attributeService;

    @Override
    public ExportAttribute create(ExportAttribute attribute) {
        attribute.setAttributeId(nextValueHelper.getNextValue(TABLE_NAME));
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto(TABLE_NAME);
        sink.addValue("AttributeID", attribute.getAttributeId());
        sink.addValue("FormatID", attribute.getFormatId());
        if (attribute.getAttribute() instanceof CustomAttribute) {
            sink.addValue("AttributeName", ((CustomAttribute) attribute.getAttribute()).getCustomAttributeId());
        } else if (attribute.getAttribute() instanceof BuiltInAttribute) {
            sink.addValue("AttributeName", ((BuiltInAttribute) attribute.getAttribute()).name());
        }
        sink.addValue("DataSelection", attribute.getDataSelection().name());
        sink.addValue("DaysPrevious",  attribute.getDaysPrevious());
        yukonJdbcTemplate.update(sql);
        return attribute;
    }
    
    @Override
    public boolean deleteByFormatId(int formatId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM");
        sql.append(TABLE_NAME);
        sql.append("WHERE FormatID").eq(formatId);
        int rowsAffected = yukonJdbcTemplate.update(sql);
        return rowsAffected == 1;
    }
        
    @Override
    public List<ExportAttribute> getByFormatId(int formatId) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT AttributeID, FormatID, AttributeName, DataSelection, DaysPrevious");
            sql.append("FROM");
            sql.append(TABLE_NAME);
            sql.append("WHERE FormatID").eq(formatId);
            return yukonJdbcTemplate.query(sql, rowMapper);
        } catch (EmptyResultDataAccessException ex){
            throw new NotFoundException("The format id supplied does not exist.");
        }
    }
    
    private YukonRowMapper<ExportAttribute> createRowMapper() {
        final YukonRowMapper<ExportAttribute> mapper = new YukonRowMapper<ExportAttribute>() {
            @Override
            public ExportAttribute mapRow(YukonResultSet rs) throws SQLException {
                final ExportAttribute attribute = new ExportAttribute();
                attribute.setFormatId(rs.getInt("FormatID"));
                attribute.setAttributeId(rs.getInt("AttributeID"));
                attribute.setAttribute(attributeService.parseAttribute(rs.getString("AttributeName")));
                attribute.setDataSelection(rs.getEnum("DataSelection", DataSelection.class));
                attribute.setDaysPrevious(rs.getInt("DaysPrevious"));
                return attribute;
            }
        };
        return mapper;
    }   
}