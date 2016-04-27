package com.cannontech.stars.dr.optout.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.stars.dr.optout.dao.OptOutAdditionalDao;

/**
 * Implementation class for OptOutAdditionalDao
 */
public class OptOutAdditionalDaoImpl implements OptOutAdditionalDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
	

    @Override
    public int getAdditionalOptOuts(int inventoryId, int customerAccountId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ExtraOptOutCount");
        sql.append("FROM OptOutAdditional");
        sql.append("WHERE InventoryId").eq(inventoryId);
        sql.append("  AND CustomerAccountId").eq(customerAccountId);
        try {
            int additionalOptOuts = jdbcTemplate.queryForInt(sql);

            return additionalOptOuts;
        } catch (EmptyResultDataAccessException e) {
            // No additional opt outs for this inventory/account
            return 0;
        }

    }
	
    @Override
    @Transactional
    public void addAdditonalOptOuts(int inventoryId, int customerAccountId, int additionalOptOuts) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM OptOutAdditional");
        sql.append("WHERE InventoryId").eq(inventoryId);
        sql.append("  AND CustomerAccountId").eq(customerAccountId);

        int rowCount = jdbcTemplate.queryForInt(sql);

        if (rowCount > 0) {
            // There are already additional opt outs for this inventory/account
            sql = new SqlStatementBuilder();
            sql.append("UPDATE OptOutAdditional");
            sql.append("SET ExtraOptOutCount = (ExtraOptOutCount + ?)");
            sql.append("WHERE InventoryId = ?");
            sql.append("	AND CustomerAccountId = ?");

        } else {
            // No additional opt outs exist for this inventory/account
            sql = new SqlStatementBuilder();
            sql.append("INSERT INTO OptOutAdditional");
            sql.append("(ExtraOptOutCount, InventoryId, CustomerAccountId)");
            sql.append("VALUES (?,?,?)");

        }

        jdbcTemplate.update(sql.toString(), additionalOptOuts, inventoryId, customerAccountId);

    }

}
