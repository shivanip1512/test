package com.cannontech.common.userpage.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.userpage.dao.UserSubscriptionDao;
import com.cannontech.common.userpage.model.UserSubscription;
import com.cannontech.common.userpage.model.UserSubscription.SubscriptionType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.AdvancedFieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SqlParameterChildSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;

public class UserSubscriptionDaoImpl implements UserSubscriptionDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    private static SimpleTableAccessTemplate<UserSubscription> userSubscriptionTemplate;

    @Override
    public boolean contains(UserSubscription subscription) {
        Integer id = getId(subscription);
        return id != null;
    }

    @Override
    public UserSubscription save(UserSubscription subscription) {

        Integer userSubscriptionId = getId(subscription);

        if (userSubscriptionId == null) {
            userSubscriptionId = userSubscriptionTemplate.insert(subscription);

            subscription = subscription.updateId(userSubscriptionId);
        } else {
            subscription = subscription.updateId(userSubscriptionId);
            userSubscriptionTemplate.update(subscription);
        }
        return subscription;
    }

    @Override
    public void delete(UserSubscription subscription) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM UserSubscription");
        sql.appendFragment(getUniquenessCriterion(subscription));
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public List<UserSubscription> getSubscriptionsForUser(LiteYukonUser user) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UserSubscriptionId, UserId, SubscriptionType, RefId");
        sql.append("FROM UserSubscription");
        sql.append("WHERE UserId").eq(user.getUserID());

        List<UserSubscription> results = yukonJdbcTemplate.query(sql, userSubscriptionRowMapper);

        return results;
    }

    @Override
    public void deleteSubscriptionsForItem(SubscriptionType type, Integer refId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM UserSubscription");
        sql.append("WHERE SubscriptionType").eq(type);
        sql.append("AND RefId").eq(refId);

        yukonJdbcTemplate.update(sql);
    }

    private Integer getId(UserSubscription subscription) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UserSubscriptionId, UserId, SubscriptionType, RefId");
        sql.append("FROM UserSubscription");
        sql.appendFragment(getUniquenessCriterion(subscription));
        List<UserSubscription> subscriptions = yukonJdbcTemplate.query(sql, userSubscriptionRowMapper);

        switch(subscriptions.size()){
        case 0: return null;
        default: return subscriptions.get(0).getId();
        }
    }

   private static SqlFragmentSource getUniquenessCriterion(UserSubscription subscription) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("WHERE UserId").eq(subscription.getUserId());
        sql.append("AND RefId").eq(subscription.getRefId());
        sql.append("AND SubscriptionType").eq(subscription.getType());
        return sql;
    }

    private static YukonRowMapper<UserSubscription> userSubscriptionRowMapper = new YukonRowMapper<UserSubscription>() {

        public UserSubscription mapRow(YukonResultSet rs) throws SQLException {

            Integer id = rs.getInt("UserSubscriptionId");
            Integer userId = rs.getInt("UserId");
            SubscriptionType type = rs.getEnum("SubscriptionType", SubscriptionType.class);
            Integer monitorId = rs.getInt("RefId");
            UserSubscription monitor = new UserSubscription(userId, type, monitorId, id);

            return monitor;
        }
    };

    private static AdvancedFieldMapper<UserSubscription> userMonitorMapper = new AdvancedFieldMapper<UserSubscription>() {

        public void extractValues(SqlParameterChildSink p, UserSubscription monitor) {
            p.addValue("UserId", monitor.getUserId());
            p.addValue("SubscriptionType", monitor.getType());
            p.addValue("RefId", monitor.getRefId());
        }
        public Number getPrimaryKey(UserSubscription monitor) {
            return monitor.getId();
        }
        public void setPrimaryKey(UserSubscription monitor, int value) {
            //Immutable object
        }
    };

    @PostConstruct
    public void init() throws Exception {
        userSubscriptionTemplate = new SimpleTableAccessTemplate<UserSubscription>(yukonJdbcTemplate, nextValueHelper);
        userSubscriptionTemplate.setTableName("UserSubscription");
        userSubscriptionTemplate.setPrimaryKeyField("UserSubscriptionId");
        userSubscriptionTemplate.setAdvancedFieldMapper(userMonitorMapper);
    }
}