package com.cannontech.amr.archivedValueExporter.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportFieldDao;
import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.amr.archivedValueExporter.model.DataSelection;
import com.cannontech.amr.archivedValueExporter.model.AttributeField;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Lists;

public class ArchiveValuesExportFieldDaoImpl implements ArchiveValuesExportFieldDao {
    
    public static final String TABLE_NAME = "ArchiveValuesExportField";
    
    private static String insertSql = getInsertSql();
    private static String removeByFormatIdSql = getRemoveByFormatIdSql();
    
    private final ParameterizedRowMapper<ExportField> rowMapper = createRowMapper();
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    
    private static String getInsertSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(TABLE_NAME);
        sb.append(" (FieldID,FormatID,FieldType,AttributeID,AttributeField,Pattern,MaxLength,PadChar,PadSide,RoundingMode,MissingAttributeValue,MultiplierRemovedFlag) ");
        sb.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?) ");
        return sb.toString();
    }

    private static String getRemoveByFormatIdSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(TABLE_NAME);
        sb.append(" WHERE FormatID = ? ");
        return sb.toString();
    }
    @Override
    @Transactional
    public List<ExportField> create(List<ExportField> fields) {
        List<Object[]> batchArgs = Lists.newArrayList();
        for (ExportField field : fields) {
            int fieldId = nextValueHelper.getNextValue(TABLE_NAME);
            int formatId = field.getFormatId();
            String fieldType = SqlUtils.convertStringToDbValue(field.getFieldType().name());
            Integer attributeID = null;
            if(field.getFieldType().equals(FieldType.ATTRIBUTE)){
                attributeID = field.getAttribute().getAttributeId();
            }
            String attributeField = " ";
            if(field.getAttributeField() != null){
                attributeField = SqlUtils.convertStringToDbValue(field.getAttributeField().name());
            }
            String pattern = SqlUtils.convertStringToDbValue(field.getPattern());
            int maxLength = field.getMaxLength();
            String padChar = SqlUtils.convertStringToDbValue(field.getPadChar());
            String padSide = SqlUtils.convertStringToDbValue(field.getPadSide());
            String roundingMode = SqlUtils.convertStringToDbValue(field.getRoundingMode());
            String missingAttributeValue = SqlUtils.convertStringToDbValue(field.getMissingAttributeValue());
            //String multiplierRemovedFlag =YNBoolean.valueOf(field.isMultiplierRemovedFlag()).getDatabaseRepresentation().toString();
            DatabaseRepresentationSource ynBoolean = (DatabaseRepresentationSource)YNBoolean.valueOf(field.isMultiplierRemovedFlag());
            String multiplierRemovedFlag = ynBoolean.getDatabaseRepresentation().toString();
            batchArgs.add(new Object[] {fieldId,formatId,fieldType,attributeID, attributeField, pattern, maxLength, padChar, padSide, roundingMode, missingAttributeValue, multiplierRemovedFlag });
        }
        yukonJdbcTemplate.batchUpdate(insertSql, batchArgs);
        return fields;
    }
    
    @Override
    @Transactional
    public boolean removeByFormatId(int formatId) {
        int rowsAffected =  yukonJdbcTemplate.update(removeByFormatIdSql, formatId);
        return rowsAffected == 1;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ExportField> getByFormatId(int formatId) {
        List<ExportField> fields = null;
        try {
            SqlStatementBuilder selectByFormatIdSql = getSelectByFormatIdSql().eq(formatId);
            fields = yukonJdbcTemplate.query(selectByFormatIdSql, rowMapper);
        } catch (EmptyResultDataAccessException ex){
            throw new NotFoundException("The format id supplied does not exist.");
        }
        return fields;
    }
    
    private SqlStatementBuilder getSelectByFormatIdSql() {
        SqlStatementBuilder selectByFormatIdSql = new SqlStatementBuilder();
        selectByFormatIdSql.append("SELECT A.AttributeName, A.DataSelection, A.DaysPrevious, F.FieldID, F.FormatID, F.FieldType, F.AttributeID, F.AttributeField, F.Pattern, F.MaxLength, F.PadChar, F.PadSide, F.RoundingMode, F.MissingAttributeValue, F.MultiplierRemovedFlag ");
        selectByFormatIdSql.append("FROM ");
        selectByFormatIdSql.append(TABLE_NAME);
        selectByFormatIdSql.append(" AS F LEFT JOIN ArchiveValuesExportAttribute A ON F.FormatID=A.FormatID AND F.AttributeID=A.AttributeID WHERE F.FormatID");
        return selectByFormatIdSql;
    }
    
    private ParameterizedRowMapper<ExportField> createRowMapper() {
        final ParameterizedRowMapper<ExportField> mapper =
            new ParameterizedRowMapper<ExportField>() {
                @Override
                public ExportField mapRow(ResultSet rs, int rowNum) throws SQLException {
                    final ExportField field = new ExportField();
                    field.setFieldId(rs.getInt("FieldID"));
                    field.setFormatId(rs.getInt("FormatID"));
                    field.setFieldType(FieldType.valueOf(SqlUtils.convertDbValueToString(rs, "FieldType")));
                    if (!StringUtils.isBlank(rs.getString("AttributeID"))) {
                        final ExportAttribute attribute = new ExportAttribute();
                        attribute.setFormatId(rs.getInt("FormatID"));
                        attribute.setAttributeId(rs.getInt("AttributeID"));
                        attribute.setAttribute(BuiltInAttribute.valueOf(rs
                            .getString("AttributeName")));
                        attribute.setDataSelection(DataSelection.valueOf(rs
                            .getString("DataSelection")));
                        attribute.setDaysPrevious(rs.getInt("DaysPrevious"));
                        field.setAttribute(attribute);
                        field.setAttributeField(AttributeField.valueOf(SqlUtils.convertDbValueToString(rs, "AttributeField")));
                    }
                    field.setPattern(SqlUtils.convertDbValueToString(rs, "Pattern"));
                    field.setMaxLength(rs.getInt("MaxLength"));
                    field.setPadChar(SqlUtils.convertDbValueToString(rs, "PadChar"));
                    field.setPadSide(SqlUtils.convertDbValueToString(rs, "PadSide"));
                    field.setRoundingMode(SqlUtils.convertDbValueToString(rs, "RoundingMode"));
                    field.setMissingAttributeValue(SqlUtils.convertDbValueToString(rs, "MissingAttributeValue"));
                    if(YNBoolean.YES.getDatabaseRepresentation().equals(SqlUtils.convertDbValueToString(rs, "MultiplierRemovedFlag"))){
                        field.setMultiplierRemovedFlag(true);
                    }
                    return field;
                }
            };
        return mapper;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
}
