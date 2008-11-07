package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.SqlUtils;
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
    private ChunkingSqlTemplate<Integer> chunkyJdbcTemplate;
    private NextValueHelper nextValueHelper;
    
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

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactNotificationDao#getContactNotification(int)
     */
	public LiteContactNotification getContactNotification( int contactNotifID ) 
	{
		synchronized( databaseCache )
		{
			return databaseCache.getAContactNotifByNotifID(contactNotifID);
		}
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactNotificationDao#getAllContactNotifications()
     */
	public List<LiteContactNotification> getAllContactNotifications() 
	{
		synchronized( databaseCache )		
		{
		    return new ArrayList<LiteContactNotification>(databaseCache.getAllContactNotifsMap().values());
        }
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactNotificationDao#getContactNotificationsParent(int)
     */
	public LiteContact getContactNotificationsParent( int notifCatID ) 
	{
		synchronized( databaseCache )
		{
			List<LiteContact> cstCnts = databaseCache.getAllContacts();
			
			for( int i = 0; i < cstCnts.size(); i++ )
			{
				LiteContact ltCntact = cstCnts.get(i);
				for( int j = 0; j < ltCntact.getLiteContactNotifications().size(); j++ )
				{
					LiteContactNotification ltNotif = 
						ltCntact.getLiteContactNotifications().get(j);

					if( notifCatID == ltNotif.getContactNotifID() )
						return cstCnts.get(i);
				}
			}
		}
	
		return null;
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
            
            this.saveNotification(currentNotification);
        }

        // Remove any existing notifications that are not in the notificationList
        previousNotificationIds.removeAll(currentNotificationIds);
        for(Integer removeId : previousNotificationIds) {
            this.removeNotification(removeId);
        }
        
    }

    @Override
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
        String disableFlag = notification.getDisableFlag();
        String notificationText = SqlUtils.convertStringToDbValue(notification.getNotification());
        simpleJdbcTemplate.update(sql.toString(),
                                  contactId,
                                  notificationCategoryId,
                                  disableFlag,
                                  notificationText,
                                  order,
                                  notificationId);
    }

    @Override
    public List<LiteContactNotification> getNotificationsForContact(
            int contactId) {

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
    public List<Integer> getNotificationIdsForContact(int contactId) {
        String sql = "SELECT ContactNotifId FROM ContactNotification WHERE ContactId = ?";
        List<Integer> notifs = simpleJdbcTemplate.query(sql, new IntegerRowMapper(), contactId);
        return notifs;
    }
    
    @Override
    public List<Integer> getAllNotificationIdsForContactIds(List<Integer> contactIds){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ContactNotifId FROM ContactNotification WHERE ContactId IN (");
        sql.append(contactIds);
        sql.append(")");
        List<Integer> notifIds = simpleJdbcTemplate.query(sql.toString(), new IntegerRowMapper());
        return notifIds;
    }
    
    @Override
    public LiteContactNotification getNotificationForContactByType(int contactId, int type) {
        List<LiteContactNotification> notifs = getNotificationsForContact(contactId);
        for(LiteContactNotification notif : notifs) {
            if(notif.getNotificationCategoryID() == type) {
                return notif;
            }
        }
        return null;
    }

    @Override
    public void removeNotification(int notificationID) {
        
        StringBuilder sql = new StringBuilder("DELETE ");
        sql.append(" FROM ContactNotification");
        sql.append(" WHERE ContactNotifId =  ?");
        
        simpleJdbcTemplate.update(sql.toString(), notificationID);
        
    }
    
    @Override
    public void removeNotificationsForContactIds(List<Integer> contactIds) {
        StringBuilder sql = new StringBuilder("DELETE ");
        sql.append(" FROM ContactNotification");
        sql.append(" WHERE ContactId =  ?");
        
        simpleJdbcTemplate.update(sql.toString(), contactIds);
        
    }

    @Override
    public void removeNotificationsForContact(int contactId) {

        StringBuilder sql = new StringBuilder("DELETE ");
        sql.append(" FROM ContactNotification");
        sql.append(" WHERE ContactId =  ?");
        
        simpleJdbcTemplate.update(sql.toString(), contactId);
        
    }
    
    @Override
    public void removeContactNotifDestinationsForNotifs(List<Integer> contactNotifIds) {
        chunkyJdbcTemplate.update(new NotifDestinationSqlGenerator(), contactNotifIds);
    }
    
    @Override
    public void removeContactNotifsForContactIds(List<Integer> contactIds) {
        chunkyJdbcTemplate.update(new ContactNotifSqlGenerator(), contactIds);
    }
    
    @Override
    public void removeContactNotifMapEntriesForContactIds(List<Integer> contactIds) {
        chunkyJdbcTemplate.update(new ContactNotifMapSqlGenerator(), contactIds);
    }
    
    
    /**
     * Sql generator for deleting in NotificationDestination, useful for bulk deleteing
     * with chunking sql template.
     * @author asolberg
     *
     */
    private class NotifDestinationSqlGenerator implements SqlGenerator<Integer> {

        @Override
        public String generate(List<Integer> contactNotifIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM NotificationDestination WHERE RecipientId IN (");
            sql.append(contactNotifIds);
            sql.append(")");
            return sql.toString();
        }
    }
    
    /**
     * Sql generator for deleting in ContactNotification, useful for bulk deleteing
     * with chunking sql template.
     * @author asolberg
     *
     */
    private class ContactNotifSqlGenerator implements SqlGenerator<Integer> {

        @Override
        public String generate(List<Integer> contactNotifIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM ContactNotification WHERE ContactId IN (");
            sql.append(contactNotifIds);
            sql.append(")");
            return sql.toString();
        }
    }
    
    /**
     * Sql generator for deleting in ContactNotification, useful for bulk deleteing
     * with chunking sql template.
     * @author asolberg
     *
     */
    private class ContactNotifMapSqlGenerator implements SqlGenerator<Integer> {

        @Override
        public String generate(List<Integer> contactNotifIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM ContactNotifGroupMap WHERE ContactId IN (");
            sql.append(contactNotifIds);
            sql.append(")");
            return sql.toString();
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
        chunkyJdbcTemplate= new ChunkingSqlTemplate<Integer>(simpleJdbcTemplate);
    }

}