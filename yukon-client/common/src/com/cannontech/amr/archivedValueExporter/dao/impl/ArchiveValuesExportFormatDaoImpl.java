package com.cannontech.amr.archivedValueExporter.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportAttributeDao;
import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportFieldDao;
import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportFormatDao;
import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;


public class ArchiveValuesExportFormatDaoImpl implements ArchiveValuesExportFormatDao{

    public static final String TABLE_NAME = "ArchiveValuesExportFormat";

    private static String insertSql = getInsertSql();
    private static String updateSql = getUpdateSql();
    private static String removeSql = getRemoveSql();
    
    private ArchiveValuesExportAttributeDao archiveValuesExportAttributeDao;
    private ArchiveValuesExportFieldDao archiveValuesExportFieldDao;
    private final ParameterizedRowMapper<ExportFormat> rowMapper = createRowMapper();
    private final ParameterizedRowMapper<ExportFormat> formatIdAndFormatNameRowMapper = createFormatIdAndFormatNameRowMapper();
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;

    
    @Override
    @Transactional
    public ExportFormat create(ExportFormat format) {
        format.setFormatId(nextValueHelper.getNextValue(TABLE_NAME));
        int formatId = format.getFormatId();
        String delimiter =  SqlUtils.convertStringToDbValue(format.getDelimiter());
        String formatName =  SqlUtils.convertStringToDbValue(format.getFormatName());
        String header =  SqlUtils.convertStringToDbValue(format.getHeader());
        String footer =  SqlUtils.convertStringToDbValue(format.getFooter());
        yukonJdbcTemplate.update(insertSql, formatId,formatName, delimiter, header, footer);
        createAttributesAndFields(format);
        return format;
    }
    
   
    @Override
    @Transactional
    public ExportFormat update(ExportFormat format) {
        int formatId = format.getFormatId();
        String delimiter =  SqlUtils.convertStringToDbValue(format.getDelimiter());
        String formatName =  SqlUtils.convertStringToDbValue(format.getFormatName());
        String header =  SqlUtils.convertStringToDbValue(format.getHeader());
        String footer =  SqlUtils.convertStringToDbValue(format.getFooter());
        yukonJdbcTemplate.update( updateSql, formatId,formatName, delimiter, header, footer, formatId);  
        createAttributesAndFields(format);
        return format;
    }
    
    @Override
    @Transactional
    public void remove(int formatId) {
        archiveValuesExportFieldDao.removeByFormatId(formatId);
        archiveValuesExportAttributeDao.removeByFormatId(formatId);
        yukonJdbcTemplate.update(removeSql, formatId);
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ExportFormat getByFormatId(int formatId) {
        ExportFormat format = null;
        try {
            SqlStatementBuilder selectByFormatIdSql = getSelectByFormatIdSql().eq(formatId);
            format = yukonJdbcTemplate.queryForObject(selectByFormatIdSql, rowMapper);
        } catch (EmptyResultDataAccessException ex){
            throw new NotFoundException("The format id supplied does not exist.");
        }
        return format;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ExportFormat getByFormatName(String formatName) {
        ExportFormat format = null;
        try {
            SqlStatementBuilder selectByFormatNameSql = getSelectByFormatName();
            selectByFormatNameSql.eq(formatName);
            format = yukonJdbcTemplate.queryForObject(selectByFormatNameSql, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
        }
        return format;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ExportFormat> getAllFormats() {
        List<ExportFormat> formats = null;
        SqlStatementBuilder selectAllSql = getSelectFormatIdAndFormatNameSql();
        formats = yukonJdbcTemplate.query(selectAllSql, formatIdAndFormatNameRowMapper);
        return formats;
    }
    
    private SqlStatementBuilder getSelectByFormatIdSql() {
        SqlStatementBuilder selectByFormatIdSql = new SqlStatementBuilder();
        selectByFormatIdSql.append("SELECT FormatID, FormatName, Delimiter, Header, Footer ");
        selectByFormatIdSql.append("FROM ");
        selectByFormatIdSql.append(TABLE_NAME);
        selectByFormatIdSql.append(" WHERE FormatID");
        return selectByFormatIdSql;
    }

    private SqlStatementBuilder getSelectByFormatName() {
        SqlStatementBuilder selectByFormatNameSql = new SqlStatementBuilder();
        selectByFormatNameSql.append("SELECT FormatID, FormatName, Delimiter, Header, Footer ");
        selectByFormatNameSql.append("FROM ");
        selectByFormatNameSql.append(TABLE_NAME);
        selectByFormatNameSql.append(" WHERE FormatName");
        return selectByFormatNameSql;
    }
    
    private SqlStatementBuilder getSelectFormatIdAndFormatNameSql() {
        SqlStatementBuilder selectAllSql = new SqlStatementBuilder();
        selectAllSql.append("SELECT FormatID, FormatName ");
        selectAllSql.append("FROM ");
        selectAllSql.append(TABLE_NAME);
        return selectAllSql;
    }

    private static String getUpdateSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ");
        sb.append(TABLE_NAME);
        sb.append(" SET FormatID = ?, FormatName = ?, Delimiter = ?, Header = ?, Footer = ? ");
        sb.append("WHERE FormatID = ?");
        return sb.toString();
    }

    private static String getInsertSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(TABLE_NAME);
        sb.append(" (FormatID,FormatName,Delimiter,Header,Footer) ");
        sb.append("VALUES (?,?,?,?,?) ");
        return sb.toString();
    }

    private static String getRemoveSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(TABLE_NAME);
        sb.append(" WHERE FormatID = ? ");
        return sb.toString();
    }
    
    private ParameterizedRowMapper<ExportFormat> createRowMapper() {
        final ParameterizedRowMapper<ExportFormat> mapper = new ParameterizedRowMapper<ExportFormat>() {
            @Override
            public ExportFormat mapRow(ResultSet rs, int rowNum) throws SQLException {
                final ExportFormat format = new ExportFormat();
                format.setFormatId(rs.getInt("FormatID"));
                format.setFormatName(SqlUtils.convertDbValueToString(rs, "FormatName"));
                format.setDelimiter(SqlUtils.convertDbValueToString(rs, "Delimiter"));
                format.setHeader(SqlUtils.convertDbValueToString(rs, "Header"));
                format.setFooter(SqlUtils.convertDbValueToString(rs, "Footer"));
                format.setAttributes(archiveValuesExportAttributeDao.getByFormatId(format.getFormatId()));
                format.setFields(archiveValuesExportFieldDao.getByFormatId(format.getFormatId()));
                return format ;
            }
        };
        return mapper;
    }
    
    private ParameterizedRowMapper<ExportFormat> createFormatIdAndFormatNameRowMapper() {
        final ParameterizedRowMapper<ExportFormat> mapper = new ParameterizedRowMapper<ExportFormat>() {
            @Override
            public ExportFormat mapRow(ResultSet rs, int rowNum) throws SQLException {
                final ExportFormat format = new ExportFormat();
                format.setFormatId(rs.getInt("FormatID"));
                format.setFormatName(SqlUtils.convertDbValueToString(rs, "FormatName"));
                return format ;
            }
        };
        return mapper;
    }
        
    private void createAttributesAndFields(ExportFormat format){
        archiveValuesExportFieldDao.removeByFormatId(format.getFormatId());
        archiveValuesExportAttributeDao.removeByFormatId(format.getFormatId());
        updateAttributesWithFormatId(format);
        updateFieldsWithFormatId(format);
        for(ExportAttribute attribute:format.getAttributes()){
            List<ExportField> exportFields = getExportFieldsByAttributeId(format, attribute);
            ExportAttribute newAttribute = archiveValuesExportAttributeDao.create(attribute);
            updateFieldsWithAttributeId(exportFields,newAttribute);
        }
        archiveValuesExportFieldDao.create(format.getFields());
    }
    
    private List<ExportField> getExportFieldsByAttributeId(ExportFormat format, ExportAttribute attribute){
        List<ExportField> exportFields = new ArrayList<ExportField>();
        for(ExportField field:format.getFields()){
            if(field.getFieldType().equals(FieldType.ATTRIBUTE) && field.getAttribute().getAttributeId() == attribute.getAttributeId()){
                exportFields.add(field);
            }
        }
        return exportFields;
    }
    
    private void updateAttributesWithFormatId(ExportFormat format){
        if(!format.getAttributes().isEmpty()){
            for(ExportAttribute attribute: format.getAttributes()){
                attribute.setFormatId(format.getFormatId());
            }
        }
    }
    
    private void updateFieldsWithFormatId(ExportFormat format){
        for (ExportField field : format.getFields()) {
            field.setFormatId(format.getFormatId());
        }
    }
    
    private void updateFieldsWithAttributeId(List<ExportField> exportFields, ExportAttribute newAttribute){
        for (ExportField field : exportFields) {
            field.setAttribute(newAttribute);
        }
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Autowired
    public void setArchiveValuesExportAttributeDao(ArchiveValuesExportAttributeDao archiveValuesExportAttributeDao) {
        this.archiveValuesExportAttributeDao = archiveValuesExportAttributeDao;
    }
    
    @Autowired
    public void setArchiveValuesExportFieldDao(ArchiveValuesExportFieldDao archiveValuesExportFieldDao) {
        this.archiveValuesExportFieldDao = archiveValuesExportFieldDao;
    }
    
}
