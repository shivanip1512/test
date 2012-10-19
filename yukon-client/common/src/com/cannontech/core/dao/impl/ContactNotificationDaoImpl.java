package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcOperations;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Functions for the LiteContactNotification data in cache
 * 
 */
public final class ContactNotificationDaoImpl implements ContactNotificationDao, InitializingBean{
    private IDatabaseCache databaseCache;
    
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private YukonJdbcOperations yukonJdbcOperations;
    private ChunkingSqlTemplate chunkyJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private PhoneNumberFormattingService phoneNumberFormattingService;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    
	public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
	
	public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
	
	public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    /**
	 * ContactFuncs constructor comment.
	 */
	private ContactNotificationDaoImpl() 
	{
		super();
	}

	public LiteContactNotification getContactNotification( int contactNotifID ) 
	{
		synchronized( databaseCache )
		{
			return databaseCache.getAContactNotifByNotifID(contactNotifID);
		}
	}

	public List<LiteContactNotification> getAllContactNotifications() 
	{
		synchronized( databaseCache )		
		{
		    return new ArrayList<LiteContactNotification>(databaseCache.getAllContactNotifsMap().values());
        }
	}

	@Override
    @Transactional
    public void saveNotificationsForContact(int contactId,
            List<LiteContactNotification> notificationList) {

	    // Get a list of existing notification ids
        StringBuilder sql = new StringBuilder("SELECT ContactNotifId");
        sql.append(" FROM ContactNotification");
        sql.append(" WHERE ContactId = ?");

        List<Integer> previousNotificationIds = simpleJdbcTemplate.query(
                    sql.toString(),
                    new ParameterizedRowMapper<Integer>() {

                        @Override
                        public Integer mapRow(
                                ResultSet rs,
                                int rowNum)
                                throws SQLException {

                            int notificationId = rs.getInt("ContactNotifId");
                            return notificationId;
                        }
                    },
                    contactId);

        // Save new/updated notifications
        List<Integer> currentNotificationIds = new ArrayList<Integer>();
        for(LiteContactNotification currentNotification : notificationList) {
            int notificationId = currentNotification.getLiteID();
            currentNotificationIds.add(notificationId);
            currentNotification.setContactID(contactId);
            this.saveNotification(currentNotification);
        }

        // Remove any existing notifications that are not in the notificationList
        previousNotificationIds.removeAll(currentNotificationIds);
        for(Integer removeId : previousNotificationIds) {
            this.removeNotification(removeId);
        }
        
    }

    @Override
    @Transactional
    public void saveNotification(LiteContactNotification notification) {

        int notificationId = notification.getLiteID();
        int contactId = notification.getContactID();
        int order = notification.getOrder();
        
        StringBuilder sql = new StringBuilder();
        if(notificationId == -1) {
            // Insert if id is -1
            
            notificationId = nextValueHelper.getNextValue("ContactNotification");
            notification.setContactNotifID(notificationId);
            
            sql.append("INSERT INTO ContactNotification");
            sql.append(" (ContactID, NotificationCategoryID, DisableFlag, Notification, Ordering, ContactNotifID)");
            sql.append(" VALUES (?,?,?,?,?,?)");
            
            StringBuilder orderSql = new StringBuilder("SELECT MAX(Ordering) + 1 FROM ContactNotification WHERE ContactId = ?");
            order = simpleJdbcTemplate.queryForInt(orderSql.toString(), contactId);
            
        } else {
            // Update if id is not -1
            
            sql.append("UPDATE ContactNotification");
            sql.append(" SET ContactID = ?, NotificationCategoryID = ?, DisableFlag = ?, ");
            sql.append(" Notification = ?, Ordering = ?");
            sql.append(" WHERE ContactNotifID = ?");
        }

        int notificationCategoryId = notification.getNotificationCategoryID();
        
        String notificationText = notification.getNotification();
        ContactNotificationType contactNotificationType = ContactNotificationType.getTypeForNotificationCategoryId(notificationCategoryId);
        if (contactNotificationType.isPhoneType() || contactNotificationType.isFaxType()) {
        	notificationText = phoneNumberFormattingService.strip(notificationText);
        }
        
        String disableFlag = notification.getDisableFlag();
        notificationText = SqlUtils.convertStringToDbValue(notificationText);
        simpleJdbcTemplate.update(sql.toString(),
                                  contactId,
                                  notificationCategoryId,
                                  disableFlag,
                                  notificationText,
                                  order,
                                  notificationId);
    }

    @Override
    public List<LiteContactNotification> getNotificationsForContact(int contactId) {

        StringBuilder sql = new StringBuilder("SELECT *");
        sql.append(" FROM ContactNotification");
        sql.append(" WHERE ContactId = ?");
        sql.append(" ORDER BY Ordering");

        List<LiteContactNotification> notificationList = simpleJdbcTemplate.query(sql.toString(),
                                                                                  new LiteContactNotificationRowMapper(),
                                                                                  contactId);

        return notificationList;
    }
    
    @Override
    public LiteContactNotification getNotificationForContact(int notificationId) {

    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT cn.*");
    	sql.append("FROM ContactNotification cn");
    	sql.append("WHERE cn.ContactNotifId").eq(notificationId);

        LiteContactNotification notification = yukonJdbcOperations.queryForObject(sql, new LiteContactNotificationRowMapper());

        return notification;
    }
    
    @Override
    public List<LiteContactNotification> getNotificationsForContact(
            LiteContact liteContact) {
        return getNotificationsForContact(liteContact.getContactID());
    }
    
    @Override
    public List<LiteContactNotification> getNotificationsForContactByType(int contactId, ContactNotificationType contactNotificationType) {
    	
    	List<LiteContactNotification> result = new ArrayList<LiteContactNotification>();
        List<LiteContactNotification> notificationsForContact = getNotificationsForContact(contactId);
        for (LiteContactNotification liteNotif : notificationsForContact) {
            if (liteNotif.getNotificationCategoryID() == contactNotificationType.getDefinitionId()) {
                result.add(liteNotif);
            }
        }
        
        return result;
    }
    
    @Override
    public List<LiteContactNotification> getNotificationsForContactByType(LiteContact liteContact, ContactNotificationType contactNotificationType) {
        return getNotificationsForContactByType(liteContact.getContactID(), contactNotificationType);
    }
    
    @Override
    public List<LiteContactNotification> getNotificationsForNotificationByType(String notification,  ContactNotificationType contactNotificationType) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT cn.*");
        sql.append("FROM ContactNotification cn");
        sql.append("WHERE cn.NotificationCategoryID").eq(contactNotificationType.getDatabaseRepresentation());
        sql.append("AND UPPER(cn.notification)").eq(notification.toUpperCase());
        
        return yukonJdbcTemplate.query(sql,  new LiteContactNotificationRowMapper());
    }
    
    @Override
    public List<Integer> getNotificationIdsForContact(int contactId) {
        String sql = "SELECT ContactNotifId FROM ContactNotification WHERE ContactId = ?";
        List<Integer> notifs = simpleJdbcTemplate.query(sql, new IntegerRowMapper(), contactId);
        return notifs;
    }
    
    @Override
    public LiteContactNotification getFirstNotificationForContactByType(LiteContact liteContact, ContactNotificationType contactNotificationType) {
        List<LiteContactNotification> notificationsForContactByType = getNotificationsForContactByType(liteContact, contactNotificationType);
        return notificationsForContactByType.isEmpty() ? null : notificationsForContactByType.get(0);
    }
    
    @Override
    public LiteContactNotification getFirstNotificationForContactByType(int contactId, ContactNotificationType contactNotificationType) {
        List<LiteContactNotification> notificationsForContactByType = getNotificationsForContactByType(contactId, contactNotificationType);
        return notificationsForContactByType.isEmpty() ? null : notificationsForContactByType.get(0);
    }

    // REMOVING CONTACT NOTIFICATIONS
    // --------------------------------------------------------------------------------------
    
    @Override
    @Transactional
    public void removeNotification(int notificationId) {
        
    	removeNotifications(Collections.singletonList(notificationId));
    }
    
    @Override
    @Transactional
    public void removeNotifications(List<Integer> notificationIds) {
        
    	chunkyJdbcTemplate.update(new RemoveNotificationDestinationsSqlGenerator(), notificationIds);
    	chunkyJdbcTemplate.update(new RemoveNotificationSqlGenerator(), notificationIds);
    }
    
    // chunking cleanup sql
    private class RemoveNotificationSqlGenerator implements SqlFragmentGenerator<Integer> {
    	
    	@Override
    	public SqlFragmentSource generate(List<Integer> contactNotificationIds) {
    		
    		SqlStatementBuilder sql = new SqlStatementBuilder();
    		sql.append("DELETE FROM ContactNotification");
    		sql.append("WHERE ContactNotifId IN (");
    		sql.appendList(contactNotificationIds);
    		sql.append(")");
    		return sql;
    	}
    }

	private class RemoveNotificationDestinationsSqlGenerator implements SqlFragmentGenerator<Integer> {
    	
    	@Override
    	public SqlFragmentSource generate(List<Integer> contactNotificationIds) {
    		
    		SqlStatementBuilder sql = new SqlStatementBuilder();
    		sql.append("DELETE FROM NotificationDestination");
    		sql.append("WHERE RecipientId IN (");
    		sql.appendList(contactNotificationIds);
    		sql.append(")");
    		return sql;
    	}
    }

    /**
     * Helper class to map a result set into LiteContactNotifications
     */
    private class LiteContactNotificationRowMapper implements
            ParameterizedRowMapper<LiteContactNotification> {

        @Override
        public LiteContactNotification mapRow(ResultSet rs, int rowNum)
                throws SQLException {

            int id = rs.getInt("ContactNotifId");
            int contactId = rs.getInt("ContactId");
            int categoryId = rs.getInt("NotificationCategoryId");
            int order = rs.getInt("Ordering");
            String disableFlag = rs.getString("DisableFlag");
            String notificationString = SqlUtils.convertDbValueToString(rs.getString("Notification"));

            LiteContactNotification notification = new LiteContactNotification(id);
            notification.setContactID(contactId);
            notification.setNotificationCategoryID(categoryId);
            notification.setDisableFlag(disableFlag.intern());
            notification.setNotification(notificationString);
            notification.setOrder(order);

            return notification;
        }

    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        chunkyJdbcTemplate= new ChunkingSqlTemplate(simpleJdbcTemplate);
    }
    
    @Autowired
    public void setYukonJdbcOperations(YukonJdbcOperations yukonJdbcOperations) {
		this.yukonJdbcOperations = yukonJdbcOperations;
	}
    
    @Autowired
    public void setPhoneNumberFormattingService(PhoneNumberFormattingService phoneNumberFormattingService) {
		this.phoneNumberFormattingService = phoneNumberFormattingService;
	}
}