package com.cannontech.amr.archivedValueExporter.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportAttributeDao;
import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportFieldDao;
import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportFormatDao;
import com.cannontech.amr.archivedValueExporter.model.ArchivedValuesExportFormatType;
import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.TimeZoneFormat;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;


public class ArchiveValuesExportFormatDaoImpl implements ArchiveValuesExportFormatDao{

    public static final String TABLE_NAME = "ArchiveValuesExportFormat";
	@Autowired DbChangeManager dbChangeManager;
    private final YukonRowMapper<ExportFormat> rowMapper = new YukonRowMapper<ExportFormat>() {
        @Override
        public ExportFormat mapRow(YukonResultSet rs) throws SQLException {
            
            final ExportFormat format = new ExportFormat();
            format.setFormatId(rs.getInt("FormatID"));
            format.setFormatName(rs.getStringSafe("FormatName"));
            format.setDelimiter((rs.getString("Delimiter") == null) ? "" : rs.getString("Delimiter"));
            format.setHeader(SqlUtils.convertDbValueToString(rs.getString("Header")));
            format.setFooter(SqlUtils.convertDbValueToString(rs.getString("Footer")));
            format.setFormatType(rs.getEnum("FormatType", ArchivedValuesExportFormatType.class));
            format.setAttributes(archiveValuesExportAttributeDao.getByFormatId(format.getFormatId()));
            format.setFields(archiveValuesExportFieldDao.getByFormatId(format.getFormatId()));
            format.setDateTimeZoneFormat(rs.getEnum("TimeZoneFormat", TimeZoneFormat.class));
            format.setExcludeAbnormal(rs.getBoolean("ExcludeAbnormal"));
            
            return format;
        }
    };
    private final YukonRowMapper<ExportFormat> formatIdAndFormatNameRowMapper = new YukonRowMapper<ExportFormat>() {
        @Override
        public ExportFormat mapRow(YukonResultSet rs) throws SQLException {
            final ExportFormat format = new ExportFormat();
            format.setFormatId(rs.getInt("FormatID"));
            format.setFormatName(rs.getStringSafe("FormatName"));
            return format ;
        }
    };
   
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
        sink.addValue("Delimiter", format.getDelimiter().equals("") ? null : format.getDelimiter());
        sink.addValue("Header",  format.getHeader());
        sink.addValue("Footer",  format.getFooter());
        sink.addValue("FormatType", format.getFormatType());
        sink.addValue("TimeZoneFormat", format.getDateTimeZoneFormat());
        sink.addValue("ExcludeAbnormal", format.isExcludeAbnormal());
        
        yukonJdbcTemplate.update(sql);
        createAttributesAndFields(format);
        dbChangeManager.processDbChange(DbChangeType.ADD, DbChangeCategory.DATA_EXPORT_FORMAT, format.getFormatId());
        return format;
    }
    
   
    @Override
    @Transactional
    public ExportFormat update(ExportFormat format) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.update(TABLE_NAME);
        sink.addValue("FormatID", format.getFormatId());
        sink.addValue("FormatName", format.getFormatName());
        sink.addValue("Delimiter", format.getDelimiter().equals("") ? null : format.getDelimiter());
        sink.addValue("Header",  format.getHeader());
        sink.addValue("Footer",  format.getFooter());
        sink.addValue("FormatType", format.getFormatType());
        sink.addValue("TimeZoneFormat", format.getDateTimeZoneFormat());
        sink.addValue("ExcludeAbnormal", format.isExcludeAbnormal());
        sql.append("WHERE FormatID").eq(format.getFormatId());
        
        yukonJdbcTemplate.update(sql);
        createAttributesAndFields(format);
        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.DATA_EXPORT_FORMAT, format.getFormatId());
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
        dbChangeManager.processDbChange(DbChangeType.DELETE, DbChangeCategory.DATA_EXPORT_FORMAT, formatId);
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public ExportFormat getByFormatId(int formatId) {
        ExportFormat format = null;
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT FormatID, FormatName, Delimiter, Header, Footer, FormatType, TimeZoneFormat, ExcludeAbnormal");
            sql.append("FROM");
            sql.append(TABLE_NAME);
            sql.append("WHERE FormatID").eq(formatId);
            
            format = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        } catch (EmptyResultDataAccessException ex){
            throw new NotFoundException("The format id supplied does not exist.");
        }
        return format;
    }
    
    @Override
    public String getName(int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder("select FormatName from ArchiveValuesExportFormat where FormatId").eq(id);
        return yukonJdbcTemplate.queryForString(sql);
    }
    
    @Override
    public ExportFormat findByFormatName(String formatName) {
        ExportFormat format = null;
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT FormatID, FormatName, Delimiter, Header, Footer, FormatType, TimeZoneFormat, ExcludeAbnormal");
            sql.append("FROM");
            sql.append(TABLE_NAME);
            sql.append("WHERE upper(FormatName)").eq(formatName.toUpperCase());
            format = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        } catch (EmptyResultDataAccessException ex) {
            // returns null if the format was not found
        }
        return format;
    }
    
    @Override
    public List<ExportFormat> getAllFormats() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT FormatID, FormatName");
        sql.append("FROM");
        sql.append(TABLE_NAME);
        sql.append("ORDER BY FormatName");
        return yukonJdbcTemplate.query(sql,  formatIdAndFormatNameRowMapper);
    }
    
    /**
     * Creates attributes and fields.
     *
     * @param format
     */
    private void createAttributesAndFields(ExportFormat format) {
        
        archiveValuesExportFieldDao.deleteByFormatId(format.getFormatId());
        archiveValuesExportAttributeDao.deleteByFormatId(format.getFormatId());
        
        // set format id
        for (ExportAttribute attribute: format.getAttributes()) {
            attribute.setFormatId(format.getFormatId());
        }
        for (ExportField field : format.getFields()) {
            field.setFormatId(format.getFormatId());
        }
        
        for (ExportAttribute attribute : format.getAttributes()) {
            
            List<ExportField> exportFields = getExportFieldsForExportAttribute(format, attribute);
            ExportAttribute newAttribute = archiveValuesExportAttributeDao.create(attribute);
            
            // set attribute id
            for (ExportField field : exportFields) {
                field.getField().setAttribute(newAttribute);
            }
        }
        
        archiveValuesExportFieldDao.create(format.getFields());
    }
    
    /**
     * Gets all fields used by an export attribute
     */
    private List<ExportField> getExportFieldsForExportAttribute(ExportFormat format, ExportAttribute attribute){
        
        List<ExportField> exportFields = new ArrayList<ExportField>();
        
        for (ExportField field:format.getFields()) {
            
            if (field.getField().getType().equals(FieldType.ATTRIBUTE)
                && field.getField().getAttribute().getAttribute() == attribute.getAttribute()
                && field.getField().getAttribute().getDataSelection() == attribute.getDataSelection()
                && field.getField().getAttribute().getDaysPrevious().intValue() == attribute.getDaysPrevious().intValue()) {
                exportFields.add(field);
            }
        }
        return exportFields;
    }
    
}
