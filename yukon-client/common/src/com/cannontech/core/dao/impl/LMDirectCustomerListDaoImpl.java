package com.cannontech.core.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.LMDirectCustomerListDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.RowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.web.LMDirectCustomerList;
import com.google.common.collect.Sets;

public class LMDirectCustomerListDaoImpl implements LMDirectCustomerListDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private TransactionTemplate transactionTemplate;
    @Autowired private PaoDao paoDao;
    
    @Override
    public Set<Integer> getLMProgramIdsForCustomer(Integer customerId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ProgramID");
        sql.append("FROM LMDirectCustomerList");
        sql.append("WHERE CustomerID").eq(customerId);
        
        Set<Integer> result = Sets.newHashSet(jdbcTemplate.query(sql, RowMapper.INTEGER));
        return result;
    }
    
    @Override
    public Set<LiteYukonPAObject> getLMProgramPaosForCustomer(Integer customerId) {
        Set<LiteYukonPAObject> resultSet = new HashSet<LiteYukonPAObject>();
        Set<Integer> programIdsForCustomer = getLMProgramIdsForCustomer(customerId);
        for (Integer integer : programIdsForCustomer) {
            resultSet.add(paoDao.getLiteYukonPAO(integer));
        }
        return resultSet;
    }

    @Override
    public void setLMProgramPaosForCustomer(Integer customerId, Collection<LiteYukonPAObject> lmProgramPaos) {
        HashSet<Integer> programIds = new HashSet<Integer>();
        for (LiteYukonPAObject pao : lmProgramPaos) {
            programIds.add(pao.getLiteID());
        }
        setLMProgramIdsForCustomer(customerId, programIds);
    }
    
    private void setLMProgramIdsForCustomer(final Integer customerId, final Set<Integer> lmProgramIds) {
        transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                Set<Integer> currentDbSet = getLMProgramIdsForCustomer(customerId);
                // find ids that are to be removed
                Set<Integer> toAdd = new HashSet<Integer>(lmProgramIds);
                toAdd.removeAll(currentDbSet);
                SqlStatementBuilder addSql = new SqlStatementBuilder();
                addSql.append("insert into", LMDirectCustomerList.tableName, "(ProgramId, CustomerId)");
                addSql.append("values (?, ?)");
                for (Integer programId : toAdd) {
                    final int finalProgramid = programId.intValue();
                    PreparedStatementSetter pss = new PreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps) throws SQLException {
                            ps.setInt(1, finalProgramid);
                            ps.setInt(2, customerId);
                        }
                    };
                    jdbcTemplate.update(addSql.getSql(), pss);
                }
                
                // find ids that are to be added
                Set<Integer> toDelete = new HashSet<Integer>(currentDbSet);
                toDelete.removeAll(lmProgramIds);
                SqlStatementBuilder deleteSql = new SqlStatementBuilder();
                deleteSql.append("delete from", LMDirectCustomerList.tableName);
                deleteSql.append("where ProgramId in (", toDelete, ")");
                deleteSql.append("and CustomerId = ", customerId);
                jdbcTemplate.update(deleteSql);
                
                return null;
            }
        });
    }
}
