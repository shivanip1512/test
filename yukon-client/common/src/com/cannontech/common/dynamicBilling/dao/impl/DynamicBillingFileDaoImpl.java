package com.cannontech.common.dynamicBilling.dao.impl;

import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.dynamicBilling.Channel;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.dao.DynamicBillingFileDao;
import com.cannontech.common.dynamicBilling.model.DynamicBillingField;
import com.cannontech.common.dynamicBilling.model.DynamicFormat;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.incrementer.NextValueHelper;

/**
 * Implementation class for DynamicBillingFileDao
 */
public final class DynamicBillingFileDaoImpl implements DynamicBillingFileDao {

	private SimpleJdbcTemplate simpleJdbcTemplate = null;

	private NextValueHelper nextValueHelper = null;

	/**
	 * Method to delete a dynamic billing format
	 * 
	 * @param formatID - Id of format to delete
	 */
	@Transactional
	public void delete(int formatID) {
		String sql = " delete from DynamicBillingFormat where formatid = ? ";
		simpleJdbcTemplate.update(sql, formatID);

		sql = " delete from DynamicBillingField where formatid = ? ";
		simpleJdbcTemplate.update(sql, formatID);

		sql = " delete from BillingFileFormats where formatid = ? ";
		simpleJdbcTemplate.update(sql, formatID);
	}

	/**
	 * Method to load a dynamic billing format
	 * 
	 * @param formatId - Id of format to load
	 */
	public DynamicFormat retrieve(int formatId) {
		
		String sql = "SELECT " +
				"	dbf.formatId " +
				"	, dbf.header " +
				"	, dbf.footer " +
				"	, dbf.delimiter " +
				"	, bff.formatType " +
				"	, bff.systemFormat " +
				" FROM " +
				"	DynamicBillingFormat dbf" +
				"	, BillingFileFormats bff" +
				" WHERE " +
				"	bff.FormatID= dbf.formatId AND bff.formatid = ?";
		DynamicFormat format = simpleJdbcTemplate.queryForObject(sql,
				new DynamicFormatRowMapper(), formatId);

		sql = "SELECT * FROM DynamicBillingField WHERE FormatID = ? ORDER BY FieldOrder";
		List<DynamicBillingField> tempList = simpleJdbcTemplate.query(sql,
				new BillingFieldRowMapper(), formatId);
		format.setFieldList(tempList);

		return format;
	}

	/**
	 * Method to save a dynamic billing format
	 * 
	 * @param format - Format to be saved
	 */
	@Transactional
	public void save(DynamicFormat format) {

		String bffSql = null;
		String dbfSql = null;

		if (format.getFormatId() == -1) {
			// Format has no id already, we are doing an insert (this is a new format)

			format.setFormatId(nextValueHelper
					.getNextValue("BillingFileFormats"));
			
			// insert new into Billing File Formats table
			bffSql = "INSERT INTO BillingFileFormats "
					+ "(FormatType, FormatID, SystemFormat )" + "VALUES(?,?,0)";

			// insert new into dynamic billing format table
			dbfSql = "INSERT INTO DynamicBillingFormat "
					+ "(Delimiter, Header, Footer, FormatID) "
					+ "VALUES(?,?,?,?)";
		} else {
			// Format has an id already, we are doing an update (this is an existing format)

			// updates Billing File Formats table
			bffSql = "UPDATE BillingFileFormats "
					+ "SET FormatType = ? WHERE FormatID = ? ";

			// update dynamic billing format table
			dbfSql = "UPDATE DynamicBillingFormat "
					+ " SET  Delimiter = ?, Header = ?,  Footer = ? WHERE FormatID = ? ";

			// Delete any existing field info for this format
			String sql = "DELETE FROM DynamicBillingField WHERE FormatID = ?";
			simpleJdbcTemplate.update(sql, format.getFormatId());
		}

		// Execute the format update or insert
		simpleJdbcTemplate.update(bffSql, format.getName(), format.getFormatId());
		simpleJdbcTemplate.update(dbfSql, 
		                          SqlUtils.convertStringToDbValue(format.getDelim()),
		                          SqlUtils.convertStringToDbValue(format.getHeader()),
		                          SqlUtils.convertStringToDbValue(format.getFooter()),
		                                                          format.getFormatId());

		// Insert the format field data
		for (DynamicBillingField field : format.getFieldList()) {
			int currentId = nextValueHelper.getNextValue("DynamicBillingField");
			simpleJdbcTemplate.update(
					"INSERT INTO DynamicBillingField (id, FormatID, FieldName, FieldOrder, FieldFormat, MaxLength, PadChar, PadSide, ReadingType, RoundingMode, Channel) "
						+ "VALUES(?,?,?,?,?,?,?,?,?,?,?)", 
					currentId, 
					format.getFormatId(),
					field.getName(), 
					field.getOrder(), 
					SqlUtils.convertStringToDbValue(field.getFormat()),
                    field.getMaxLength(),
                    field.getPadChar(),
                    field.getPadSide(),
                    field.getReadingType().toString(),
                    field.getRoundingMode().toString(),
                    field.getChannel().toString());
		}
	}

	/**
	 * Method to retrieve all Dynamic billing formats.
	 */
	@SuppressWarnings("unchecked")
	public List<DynamicFormat> retrieveAll() {
		
		String sql = "SELECT c.formatid FROM "
				+ " DynamicBillingFormat a, BillingFileFormats c "
				+ " WHERE a.formatid = c.formatid ORDER BY FormatType";

		//get all the format id
		List<Integer> formatIdList = simpleJdbcTemplate.getJdbcOperations().queryForList(sql, Integer.class);
		
		List<DynamicFormat> allFormats = new ArrayList<DynamicFormat>();
		
		//query for dynamic format using each format id
		for(Integer id : formatIdList) {
			allFormats.add(retrieve(id));
		}
		return allFormats;
	}

	/**
	 * Helper class to process a result set row into a DynamicFormat. 
	 */
	private class DynamicFormatRowMapper implements
			ParameterizedRowMapper<DynamicFormat> {

		public DynamicFormat mapRow(ResultSet rs, int row) throws SQLException {
			DynamicFormat format = new DynamicFormat();
			format.setFormatId(rs.getInt("FormatID"));
			format.setDelim(SqlUtils.convertDbValueToString(rs.getString("Delimiter")));
			format.setHeader(SqlUtils.convertDbValueToString(rs.getString("Header")));
			format.setFooter(SqlUtils.convertDbValueToString(rs.getString("Footer")));
			format.setName(rs.getString("FormatType"));
			format.setIsSystem(rs.getBoolean("SystemFormat"));
			return format;
		}

	}

	/**
	 * Helper class to process a result set row into a DynamicBillingField. 
	 */
	private class BillingFieldRowMapper implements
			ParameterizedRowMapper<DynamicBillingField> {

		public DynamicBillingField mapRow(ResultSet rs, int row)
				throws SQLException {
			DynamicBillingField field = new DynamicBillingField();
			field.setId(rs.getInt("id"));
			field.setFormatId(rs.getInt("FormatID"));
			field.setName(rs.getString("FieldName"));
			field.setOrder(rs.getInt("FieldOrder"));
			if (field.getName().equals("Plain Text")) {  //take string value literally.  We accept the fact that "" is now " ".
			    field.setFormat(rs.getString("FieldFormat"));
			} else {
			    field.setFormat(SqlUtils.convertDbValueToString(rs.getString("FieldFormat")));
			}
			field.setMaxLength(rs.getInt("MaxLength"));
			field.setPadChar(rs.getString("PadChar"));
			field.setPadSide(rs.getString("PadSide"));
			field.setReadingType(ReadingType.valueOf(rs.getString("ReadingType")));
			field.setRoundingMode(RoundingMode.valueOf(rs.getString("RoundingMode")));
			field.setChannel(Channel.valueOf(rs.getString("Channel")));
			return field;
		}

	}

	/**
     * Check to see if the format name provided is unique.
     */
    public boolean isFormatNameUnique(DynamicFormat format) {
        
        String sql = "SELECT COUNT(*) "
                + " FROM BillingFileFormats BFF "
                + " WHERE BFF.formatType = ? "
                + " AND BFF.formatId != ? ";

        int resultCount = simpleJdbcTemplate.queryForInt(sql, format.getName(), format.getFormatId());
        
        if (resultCount == 0) {
            return true;
        }
        return false;
    }
	
	public SimpleJdbcTemplate getSimpleJdbcTemplate() {
		return simpleJdbcTemplate;
	}

	public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
		this.simpleJdbcTemplate = simpleJdbcTemplate;
	}

	public NextValueHelper getNextValueHelper() {
		return nextValueHelper;
	}

	public void setNextValueHelper(NextValueHelper nextValueHelper) {
		this.nextValueHelper = nextValueHelper;
	}
}
