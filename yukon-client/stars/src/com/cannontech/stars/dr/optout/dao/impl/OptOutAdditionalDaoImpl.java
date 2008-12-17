package com.cannontech.stars.dr.optout.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.dr.optout.dao.OptOutAdditionalDao;

/**
 * Implementation class for OptOutAdditionalDao
 */
@Repository("optOutAdditionalDao")
public class OptOutAdditionalDaoImpl implements OptOutAdditionalDao {

	private SimpleJdbcTemplate simpleJdbcTemplate;
	

	@Override
	public int getAdditionalOptOuts(int inventoryId, int customerAccountId) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ExtraOptOutCount");
		sql.append("FROM OptOutAdditional");
		sql.append("WHERE InventoryId = ?");
		sql.append("	AND CustomerAccountId = ?");
		
		try {
			int additionalOptOuts = simpleJdbcTemplate.queryForInt(sql.toString(), 
					inventoryId, 
					customerAccountId);

			return additionalOptOuts;
		} catch (EmptyResultDataAccessException e) {
			// No additional opt outs for this inventory/account
			return 0;
		}
		
	}
	
	@Override
	@Transactional
	public void addAdditonalOptOuts(int inventoryId, int customerAccountId,
			int additionalOptOuts) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ExtraOptOutCount");
		sql.append("FROM OptOutAdditional");
		sql.append("WHERE InventoryId = ?");
		sql.append("	AND CustomerAccountId = ?");
		
		try {
			simpleJdbcTemplate.queryForInt(sql.toString(), 
					inventoryId, 
					customerAccountId);

			// There are additional opt outs for this inventory/account
			sql = new SqlStatementBuilder();
			sql.append("UPDATE OptOutAdditional");
			sql.append("SET ExtraOptOutCount = (ExtraOptOutCount + ?)");
			sql.append("WHERE InventoryId = ?");
			sql.append("	AND CustomerAccountId = ?");
		
		} catch (EmptyResultDataAccessException e) {
			
			// No additional opt outs for this inventory/account
			sql = new SqlStatementBuilder();
			sql.append("INSERT INTO OptOutAdditional");
			sql.append("(ExtraOptOutCount, InventoryId, CustomerAccountId)");
			sql.append("VALUES (?,?,?)");
			
		}
		
		simpleJdbcTemplate.update(sql.toString(), 
				additionalOptOuts, 
				inventoryId, 
				customerAccountId);
		
	}

	@Autowired
	public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
		this.simpleJdbcTemplate = simpleJdbcTemplate;
	}
	
}
