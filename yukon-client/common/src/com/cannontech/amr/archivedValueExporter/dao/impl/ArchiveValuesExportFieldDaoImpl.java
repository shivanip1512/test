package com.cannontech.amr.archivedValueExporter.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportFieldDao;
import com.cannontech.amr.archivedValueExporter.model.AttributeField;
import com.cannontech.amr.archivedValueExporter.model.DataSelection;
import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportField;
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
    public List<ExportField> create(List<ExportField> fields) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO ");
        sql.append(TABLE_NAME);
        sql.append(" (FieldID,FormatID,FieldType,AttributeID,AttributeField,Pattern,MaxLength,PadChar,PadSide,RoundingMode,MissingAttribute,MissingAttributeValue) ");
        sql.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?) ");
        List<Object[]> batchArgs = Lists.newArrayList();
        for (ExportField field : fields) {
            int fieldId = nextValueHelper.getNextValue(TABLE_NAME);
            int formatId = field.getFormatId();
            String fieldType = SqlUtils.convertStringToDbValue(field.getFieldType().name());
            Integer attributeID = null;
            if (field.getFieldType().equals(FieldType.ATTRIBUTE)) {
                attributeID = field.getAttribute().getAttributeId();
            }
            String attributeField = "";
            if (field.getAttributeField() != null) {
                attributeField = field.getAttributeField().name();
            }
            attributeField = SqlUtils.convertStringToDbValue(attributeField);
            String pattern = SqlUtils.convertStringToDbValue(field.getPattern());
            int maxLength = field.getMaxLength();
            String padChar = SqlUtils.convertStringToDbValue(field.getPadChar());
            String padSide = SqlUtils.convertStringToDbValue("");
            if(field.getPadSide() != null){
                padSide = field.getPadSide().name();
            }
            String roundingMode = SqlUtils.convertStringToDbValue("");
            if(field.getRoundingMode() != null){
                roundingMode = field.getRoundingMode().name();
            }
            String missingAttribute =  SqlUtils.convertStringToDbValue("");
            if(field.getMissingAttribute() != null){
                missingAttribute  = field.getMissingAttribute().name();
            }
            String missingAttributeValue = SqlUtils.convertStringToDbValue(field.getMissingAttributeValue());
            batchArgs.add(new Object[] { fieldId, formatId, fieldType, attributeID, attributeField,
                    pattern, maxLength, padChar, padSide, roundingMode, missingAttribute,
                    missingAttributeValue });
        }
        yukonJdbcTemplate.batchUpdate(sql.getSql(), batchArgs);
        return fields;
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
            return yukonJdbcTemplate.query(sql, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("The format id supplied does not exist.");
        }
    }

    private YukonRowMapper<ExportField> createRowMapper() {
        final YukonRowMapper<ExportField> mapper =
            new YukonRowMapper<ExportField>() {
                @Override
                public ExportField mapRow(YukonResultSet rs) throws SQLException {
                    final ExportField field = new ExportField();
                    field.setFieldId(rs.getInt("FieldID"));
                    field.setFormatId(rs.getInt("FormatID"));
                    field.setFieldType(rs.getEnum("FieldType", FieldType.class));
                    if (StringUtils.isNotBlank(SqlUtils.convertDbValueToString(rs.getString("AttributeID")))) {
                        final ExportAttribute attribute = new ExportAttribute();
                        attribute.setFormatId(rs.getInt("FormatID"));
                        attribute.setAttributeId(rs.getInt("AttributeID"));
                        attribute.setAttribute(rs.getEnum("AttributeName", BuiltInAttribute.class));
                        attribute.setDataSelection(rs.getEnum("DataSelection", DataSelection.class));
                        attribute.setDaysPrevious(rs.getInt("DaysPrevious"));
                        field.setAttribute(attribute);
                        field.setAttributeField(rs.getEnum("AttributeField", AttributeField.class));
                    }
                    field.setPattern(SqlUtils.convertDbValueToString(rs.getString("Pattern")));
                    field.setMaxLength(rs.getInt("MaxLength"));
                    field.setPadChar(SqlUtils.convertDbValueToString(rs.getString("PadChar")));
                    if(!StringUtils.isEmpty(SqlUtils.convertDbValueToString(rs.getString("PadSide")))){
                        field.setPadSide(rs.getEnum("PadSide", PadSide.class));
                    }
                    if(!StringUtils.isEmpty(SqlUtils.convertDbValueToString(rs.getString("RoundingMode")))){
                        field.setRoundingMode(rs.getEnum("RoundingMode", YukonRoundingMode.class));
                    };
                    field.setMissingAttributeValue(SqlUtils.convertDbValueToString(rs.getString("MissingAttributeValue")));
                    if (!StringUtils.isEmpty(SqlUtils.convertDbValueToString(rs.getString("MissingAttribute")))) {
                        field.setMissingAttribute(rs.getEnum("MissingAttribute",MissingAttribute.class));
                    }
                    return field;
                }
            };
        return mapper;
    }
}
