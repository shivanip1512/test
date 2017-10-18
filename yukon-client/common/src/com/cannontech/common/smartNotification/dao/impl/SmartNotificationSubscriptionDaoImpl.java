package com.cannontech.common.smartNotification.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationFrequency;
import com.cannontech.common.smartNotification.model.SmartNotificationMedia;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.common.smartNotification.model.SmartNotificationVerbosity;
import com.cannontech.common.stream.StreamUtils;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;

public class SmartNotificationSubscriptionDaoImpl implements SmartNotificationSubscriptionDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    
    private static final YukonRowMapper<SmartNotificationSubscription> subscriptionMapper = r -> {
        SmartNotificationSubscription subscription = new SmartNotificationSubscription();
        subscription.setId(r.getInt("SubscriptionId"));
        subscription.setUserId(r.getInt("UserId"));
        subscription.setType(r.getEnum("Type", SmartNotificationEventType.class));
        subscription.setMedia(r.getEnum("Media", SmartNotificationMedia.class));
        subscription.setFrequency(r.getEnum("Frequency", SmartNotificationFrequency.class));
        subscription.setVerbosity(r.getEnum("Verbosity", SmartNotificationVerbosity.class));
        subscription.setRecipient(r.getString("Recipient"));
        return subscription;
    };
    
    SqlStatementBuilder baseSubscriptionSql = new SqlStatementBuilder();
    {
        baseSubscriptionSql.append("SELECT s.SubscriptionId, s.UserId, s.Type, s.Media, s.Frequency, s.Recipient, s.Verbosity");
        baseSubscriptionSql.append("FROM SmartNotificationSub s");
    }
    
    @Transactional
    @Override
    public int saveSubscription(SmartNotificationSubscription subscription) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT SubscriptionId");
        sql.append("FROM SmartNotificationSub");
        sql.append("WHERE SubscriptionId").eq(subscription.getId());

        SqlStatementBuilder updateCreateSql = new SqlStatementBuilder();
        try {
            jdbcTemplate.queryForInt(sql);
            SqlParameterSink params = updateCreateSql.update("SmartNotificationSub");
            initParameterSink(subscription, params);
            updateCreateSql.append("WHERE SubscriptionId").eq(subscription.getId());
            deleteSubscriptionParameters(subscription.getId());
        } catch (EmptyResultDataAccessException e) {
            subscription.setId(nextValueHelper.getNextValue("SmartNotificationSub"));
            SqlParameterSink params = updateCreateSql.insertInto("SmartNotificationSub");
            initParameterSink(subscription, params);
        }

        jdbcTemplate.update(updateCreateSql);
        saveSubscriptionParameters(subscription.getId(), subscription.getParameters());

        return subscription.getId();
        
    }
    
    @Override
    public void deleteSubscription(int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM SmartNotificationSub");
        sql.append("WHERE SubscriptionId").eq(id);

        jdbcTemplate.update(sql);
    }

    @Override
    public SmartNotificationSubscription getSubscription(int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder(baseSubscriptionSql.getSql());
        sql.append("WHERE SubscriptionId").eq(id);
        SmartNotificationSubscription subscription = jdbcTemplate.queryForObject(sql, subscriptionMapper);
        addParameters(Lists.newArrayList(subscription));
        
        return subscription;
    }

    @Override
    public List<SmartNotificationSubscription> getAllSubscriptions() {
        SqlStatementBuilder sql = new SqlStatementBuilder(baseSubscriptionSql.getSql());
        List<SmartNotificationSubscription> subscriptions = jdbcTemplate.query(sql, subscriptionMapper);
        System.out.println(sql.getDebugSql());
        addParameters(subscriptions);
        
        return subscriptions;
    }
    
    @Override
    public List<SmartNotificationSubscription> getSubscriptions(SmartNotificationEventType type, SmartNotificationFrequency... frequency) {
        SqlStatementBuilder sql = new SqlStatementBuilder(baseSubscriptionSql.getSql());
        sql.append("WHERE Type").eq_k(type);
        sql.append("AND Frequency").in(Lists.newArrayList(frequency));
        List<SmartNotificationSubscription> subscriptions = jdbcTemplate.query(sql, subscriptionMapper);
        addParameters(subscriptions);
        
        return subscriptions;
    }
    
    @Override
    public List<SmartNotificationSubscription> getSubscriptions(SmartNotificationEventType type, String name,
            List<Object> values, SmartNotificationFrequency... frequency) {
        SqlStatementBuilder sql = new SqlStatementBuilder(baseSubscriptionSql.getSql());
        sql.append("JOIN SmartNotificationSubParam p");
        sql.append("ON s.SubscriptionId = p.SubscriptionId");
        sql.append("WHERE s.Type").eq_k(type);
        sql.append("AND s.Frequency").in(Lists.newArrayList(frequency));
        sql.append("AND p.Name").eq(name);
        sql.append("AND p.Value").in(values);
        List<SmartNotificationSubscription> subscriptions = jdbcTemplate.query(sql, subscriptionMapper);
        addParameters(subscriptions);
        
        return subscriptions;
    }
    
    @Override
    public List<SmartNotificationSubscription> getSubscriptions(int userId, SmartNotificationEventType type) {
        SqlStatementBuilder sql = new SqlStatementBuilder(baseSubscriptionSql.getSql());
        sql.append("WHERE UserId").eq(userId);
        sql.append("AND Type").eq_k(type);
        List<SmartNotificationSubscription> subscriptions = jdbcTemplate.query(sql, subscriptionMapper);
        addParameters(subscriptions);
        
        return subscriptions;
    }
    
    @Override
    public List<SmartNotificationSubscription> getSubscriptions(int userId) {
        SqlStatementBuilder sql = new SqlStatementBuilder(baseSubscriptionSql.getSql());
        sql.append("WHERE UserId").eq(userId);
        List<SmartNotificationSubscription> subscriptions = jdbcTemplate.query(sql, subscriptionMapper);
        addParameters(subscriptions);
        
        return subscriptions;
    }
    
    private void saveSubscriptionParameters(int id, Map<String, Object> params) {
        List<List<Object>> values = new ArrayList<>();
        params.forEach((k, v) -> {
            values.add(Lists.newArrayList(id, k, v));
        });
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.batchInsertInto("SmartNotificationSubParam")
           .columns("SubscriptionId", "Name", "Value")
           .values(values);
        jdbcTemplate.yukonBatchUpdate(sql);
    }
    
    private void initParameterSink(SmartNotificationSubscription subscription, SqlParameterSink params){
        params.addValue("SubscriptionId", subscription.getId());
        params.addValue("UserId", subscription.getUserId());
        params.addValue("Type", subscription.getType());
        params.addValue("Media", subscription.getMedia());
        params.addValue("Frequency", subscription.getFrequency());
        params.addValue("Recipient", subscription.getRecipient());
        params.addValue("Verbosity", subscription.getVerbosity());
    }
    
    private void deleteSubscriptionParameters(int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM SmartNotificationSubParam");
        sql.append("WHERE SubscriptionId").eq(id);

        jdbcTemplate.update(sql);
    }
    
    private void addParameters(List<SmartNotificationSubscription> subscriptions) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        Map<Integer, SmartNotificationSubscription> idMap =
            subscriptions.stream().collect(Collectors.toMap(SmartNotificationSubscription::getId, Function.identity()));
        template.query(e -> {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT SubscriptionId, Name, Value");
            sql.append("FROM SmartNotificationSubParam");
            sql.append("WHERE SubscriptionId").in(e);
            return sql;
        }, idMap.keySet(), new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                SmartNotificationSubscription subscription = idMap.get(rs.getInt("SubscriptionId"));
                subscription.addParameters(rs.getString("Name"), rs.getObject("Value"));
            }
        });
    }
    
    @Override
    public void deleteAllSubcriptions() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM SmartNotificationSub");
        jdbcTemplate.update(sql);
    }

    @Override
    public SetMultimap<SmartNotificationEventType, SmartNotificationSubscription> getDailyDigestGrouped(String runTimeInMinutes) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * FROM SmartNotificationSub sns");
        sql.append("JOIN UserPreference up ON sns.UserId = up.UserId");
        sql.append("    AND up.Name").eq_k(UserPreferenceName.SMART_NOTIFICATIONS_DAILY_TIME);
        sql.append("    AND up.Value").eq(runTimeInMinutes);
        sql.append("    AND sns.Frequency").eq_k(SmartNotificationFrequency.DAILY_DIGEST);
        
        return (SetMultimap<SmartNotificationEventType, SmartNotificationSubscription>) jdbcTemplate.query(sql, subscriptionMapper)
                .stream()
                .collect(StreamUtils.groupingBy(SmartNotificationSubscription::getType));
    }

    @Override
    public SetMultimap<SmartNotificationEventType, SmartNotificationSubscription> getDailyDigestUngrouped(String runTimeInMinutes) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * FROM SmartNotificationSub sns");
        sql.append("JOIN UserPreference up ON sns.UserId = up.UserId");
        sql.append("    AND ((up.Name").neq_k(UserPreferenceName.SMART_NOTIFICATIONS_DAILY_TIME);
        sql.append("    )");
        sql.append("        OR (up.Name").eq_k(UserPreferenceName.SMART_NOTIFICATIONS_DAILY_TIME);
        sql.append("         AND up.Value = ''");
        sql.append("         ))");
        sql.append("    AND sns.Frequency").eq_k(SmartNotificationFrequency.DAILY_DIGEST);
        sql.append("JOIN SmartNotificationSubParam snsp ON sns.SubscriptionId = snsp.SubscriptionId");
        sql.append("    AND snsp.Name = 'sendTime'");
        sql.append("    AND snsp.Value").eq(runTimeInMinutes);

        return (SetMultimap<SmartNotificationEventType, SmartNotificationSubscription>) jdbcTemplate.query(sql, subscriptionMapper)
                .stream()
                .collect(StreamUtils.groupingBy(SmartNotificationSubscription::getType));
    }
}
