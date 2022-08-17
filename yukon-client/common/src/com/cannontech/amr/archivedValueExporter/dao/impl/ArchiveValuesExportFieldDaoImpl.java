package com.cannontech.amr.archivedValueExporter.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportFieldDao;
import com.cannontech.amr.archivedValueExporter.model.AttributeField;
import com.cannontech.amr.archivedValueExporter.model.DataSelection;
import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.Field;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.amr.archivedValueExporter.model.MissingAttribute;
import com.cannontech.amr.archivedValueExporter.model.PadSide;
import com.cannontech.amr.archivedValueExporter.model.YukonRoundingMode;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Lists;

public class ArchiveValuesExportFieldDaoImpl implements ArchiveValuesExportFieldDao {

    public static final String TABLE_NAME = "ArchiveValuesExportField";
    private final YukonRowMapper<ExportField> rowMapper = createRowMapper();

    @Autowired YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired NextValueHelper nextValueHelper;
    
    @Override
    @Transactional
    public List<ExportField> create(List<ExportField> exportFields) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO ");
        sql.append(TABLE_NAME);
        sql.append(" (FieldID,FormatID,FieldType,AttributeID,AttributeField,Pattern,MaxLength,PadChar,PadSide,RoundingMode,MissingAttribute,MissingAttributeValue) ");
        sql.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?) ");
        List<Object[]> batchArgs = Lists.newArrayList();
        
        for (ExportField exportField : exportFields) {
            
            int fieldId = nextValueHelper.getNextValue(TABLE_NAME);
            int formatId = exportField.getFormatId();
            FieldType fieldType = exportField.getField().getType();
            String fieldTypeString = SqlUtils.convertStringToDbValue(fieldType.name());
            Integer attributeID = null;
            
            if (fieldType == FieldType.ATTRIBUTE) {
                attributeID = exportField.getField().getAttribute().getAttributeId();
            }
            
            String attributeField = SqlUtils.convertStringToDbValue("");
            if (exportField.getField().isAttributeType()) {
                attributeField = exportField.getAttributeField().name();
            }
            
            String pattern = null;
            if (exportField.isTimestamp() || exportField.isValue() || exportField.getField().isPlainTextType()) {
                //Don't use 'SqlUtils.convertStringToDbValue' since we need to store a null in the db for an empty string.
                pattern = exportField.getPattern().equals("") ? null : exportField.getPattern();
            }
            
            int maxLength = exportField.getMaxLength();
            String padChar = SqlUtils.convertStringToDbValue(exportField.getPadChar());
            String padSide = SqlUtils.convertStringToDbValue("");
            if (exportField.getPadSide() != null) {
                padSide = exportField.getPadSide().name();
            }
            
            String roundingMode = SqlUtils.convertStringToDbValue("");
            if (exportField.isValue()) {
                roundingMode = exportField.getRoundingMode().name();
            }
            String missingAttribute =  SqlUtils.convertStringToDbValue("");
            if (exportField.getMissingAttribute() != null) {
                missingAttribute  = exportField.getMissingAttribute().name();
            }
            String missingAttributeValue = SqlUtils.convertStringToDbValue(exportField.getMissingAttributeValue());
            
            batchArgs.add(new Object[] {
                fieldId, formatId, fieldTypeString, attributeID, attributeField,
                pattern, maxLength, padChar, padSide, roundingMode, missingAttribute,
                missingAttributeValue
            });
        }
        yukonJdbcTemplate.batchUpdate(sql.getSql(), batchArgs);
        return exportFields;
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
    public List<ExportField> getByFormatId(int formatId) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT A.AttributeName, A.DataSelection, A.DaysPrevious, F.FieldID, F.FormatID, F.FieldType, F.AttributeID, F.AttributeField, F.Pattern, F.MaxLength, F.PadChar, F.PadSide, F.RoundingMode, F.MissingAttribute, F.MissingAttributeValue");
            sql.append("FROM");
            sql.append(TABLE_NAME);
            sql.append("F LEFT JOIN ArchiveValuesExportAttribute A ON F.FormatID=A.FormatID AND F.AttributeID=A.AttributeID WHERE F.FormatID")
                .eq(formatId);
            sql.append("ORDER BY F.FieldID");
            return yukonJdbcTemplate.query(sql, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("The format id supplied does not exist.");
        }
    }

    private YukonRowMapper<ExportField> createRowMapper() {
        final YukonRowMapper<ExportField> mapper = new YukonRowMapper<ExportField>() {
            @Override
            public ExportField mapRow(YukonResultSet rs) throws SQLException {
                
                final ExportField exportField = new ExportField();
                final Field field = new Field();
                
                exportField.setFieldId(rs.getInt("FieldID"));
                exportField.setFormatId(rs.getInt("FormatID"));
                field.setType(rs.getEnum("FieldType", FieldType.class));
                if (!StringUtils.isEmpty(SqlUtils.convertDbValueToString(rs.getString("AttributeID")))) {
                    final ExportAttribute attribute = new ExportAttribute();
                    attribute.setFormatId(rs.getInt("FormatID"));
                    attribute.setAttributeId(rs.getInt("AttributeID"));
                    attribute.setAttribute(rs.getEnum("AttributeName", BuiltInAttribute.class));
                    attribute.setDataSelection(rs.getEnum("DataSelection", DataSelection.class));
                    attribute.setDaysPrevious(rs.getInt("DaysPrevious"));
                    field.setAttribute(attribute);
                    exportField.setAttributeField(rs.getEnum("AttributeField", AttributeField.class));
                }
                // Don't use 'SqlUtils.convertDbValueToString' here since a null in the db needs to represent an empty string.
                exportField.setPattern((rs.getString("Pattern") == null) ? "" : rs.getString("Pattern"));
                exportField.setMaxLength(rs.getInt("MaxLength"));
                if(!StringUtils.isEmpty(SqlUtils.convertDbValueToString(rs.getString("PadSide")))){
                    exportField.setPadSide(rs.getEnum("PadSide", PadSide.class));
                }
                exportField.setPadChar(SqlUtils.convertDbValueToString(rs.getString("PadChar")));
                if (exportField.getPadChar().isEmpty() && exportField.getPadSide() != null 
                    && (exportField.getPadSide() == PadSide.LEFT || exportField.getPadSide() == PadSide.RIGHT)) {
                    exportField.setPadChar(" ");
                }
                if(!StringUtils.isEmpty(SqlUtils.convertDbValueToString(rs.getString("RoundingMode")))){
                    exportField.setRoundingMode(rs.getEnum("RoundingMode", YukonRoundingMode.class));
                }
                exportField.setMissingAttributeValue(SqlUtils.convertDbValueToString(rs.getString("MissingAttributeValue")));
                if (!StringUtils.isEmpty(SqlUtils.convertDbValueToString(rs.getString("MissingAttribute")))) {
                    exportField.setMissingAttribute(rs.getEnum("MissingAttribute",MissingAttribute.class));
                }
                exportField.setField(field);
                
                return exportField;
            }
        };
        
        return mapper;
    }
    
}