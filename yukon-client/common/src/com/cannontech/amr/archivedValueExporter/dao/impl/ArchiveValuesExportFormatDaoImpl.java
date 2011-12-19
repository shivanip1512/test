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
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;


public class ArchiveValuesExportFormatDaoImpl implements ArchiveValuesExportFormatDao{

    public static final String TABLE_NAME = "ArchiveValuesExportFormat";
   
    private final ParameterizedRowMapper<ExportFormat> rowMapper = createRowMapper();
    private final ParameterizedRowMapper<ExportFormat> formatIdAndFormatNameRowMapper = createFormatIdAndFormatNameRowMapper();
   
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private ArchiveValuesExportAttributeDao archiveValuesExportAttributeDao;
    @Autowired private ArchiveValuesExportFieldDao archiveValuesExportFieldDao;

   
    @Override
    @Transactional
    public ExportFormat create(ExportFormat format) {
        format.setFormatId(nextValueHelper.getNextValue(TABLE_NAME));
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto(TABLE_NAME);
        sink.addValue("FormatID", format.getFormatId());
        sink.addValue("FormatName", format.getFormatName());
        sink.addValue("Delimiter", format.getDelimiter());
        sink.addValue("Header",  format.getHeader());
        sink.addValue("Footer",  format.getFooter());
        yukonJdbcTemplate.update(sql);
        createAttributesAndFields(format);
        return format;
    }
    
   
    @Override
    @Transactional
    public ExportFormat update(ExportFormat format) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.update(TABLE_NAME);
        sink.addValue("FormatID", format.getFormatId());
        sink.addValue("FormatName", format.getFormatName());
        sink.addValue("Delimiter", format.getDelimiter());
        sink.addValue("Header",  format.getHeader());
        sink.addValue("Footer",  format.getFooter());
        sql.append("WHERE FormatID").eq(format.getFormatId());
        yukonJdbcTemplate.update(sql);
        createAttributesAndFields(format);
        return format;
    }
    
    @Override
    @Transactional
    public void delete(int formatId) {
        archiveValuesExportFieldDao.deleteByFormatId(formatId);
        archiveValuesExportAttributeDao.deleteByFormatId(formatId);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM");
        sql.append(TABLE_NAME);
        sql.append("WHERE FormatID").eq(formatId);
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public ExportFormat getByFormatId(int formatId) {
        ExportFormat format = null;
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT FormatID, FormatName, Delimiter, Header, Footer");
            sql.append("FROM");
            sql.append(TABLE_NAME);
            sql.append("WHERE FormatID").eq(formatId);
            List <ExportFormat> formats = yukonJdbcTemplate.query(sql, rowMapper);
            if(!formats.isEmpty()){
                format  = formats.get(0);
            }
        } catch (EmptyResultDataAccessException ex){
            throw new NotFoundException("The format id supplied does not exist.");
        }
        return format;
    }
    
    @Override
    public ExportFormat getByFormatName(String formatName) {
        ExportFormat format = null;
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT FormatID, FormatName, Delimiter, Header, Footer");
            sql.append("FROM");
            sql.append(TABLE_NAME);
            sql.append("WHERE FormatName").eq(formatName);
            List <ExportFormat> formats = yukonJdbcTemplate.query(sql, rowMapper);
            if(!formats.isEmpty()){
                format  = formats.get(0);
            }
        } catch (EmptyResultDataAccessException ex) {
        }
        return format;
    }
    
    @Override
    public List<ExportFormat> getAllFormats() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT FormatID, FormatName");
        sql.append("FROM");
        sql.append(TABLE_NAME);
        return yukonJdbcTemplate.query(sql,  formatIdAndFormatNameRowMapper);
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
        archiveValuesExportFieldDao.deleteByFormatId(format.getFormatId());
        archiveValuesExportAttributeDao.deleteByFormatId(format.getFormatId());
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

}
