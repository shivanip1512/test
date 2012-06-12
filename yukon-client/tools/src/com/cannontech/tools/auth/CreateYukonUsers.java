package com.cannontech.tools.auth;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.customer.Contact;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.user.YukonGroup;

public class CreateYukonUsers {
    static private DBPersistentDao dbPersistentDao = DaoFactory.getDbPersistentDao();
    
    /**
     * This was written to "clean up" the ISOC database because Contacts kept getting created
     * with no associated YukonUser.
     * @param args
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        JdbcOperations template = JdbcTemplateHelper.getYukonTemplate();
        String sql = "select contactid from contact where loginid=-9999 and contactid > 1";
        List<Integer> list = template.queryForList(sql, Integer.class);
        CTILogger.info("Found " + list.size() + " contacts that need updating");
        for (Integer contactId : list) {
            Contact contact = new Contact(contactId);
            dbPersistentDao.performDBChange(contact, Transaction.RETRIEVE);
            YukonUser user = createYukonUser(contact);
            CTILogger.info("Created " + user + " for " + contact);
            contact.getContact().setLogInID(user.getUserID());
            dbPersistentDao.performDBChange(contact, Transaction.UPDATE);
            CTILogger.info("Updated contact " + contact.getContact().getContactID() + " with login "  + contact.getContact().getLogInID());
        }
    }
    
    private static YukonUser createYukonUser(Contact contact) {
        YukonUser user = new YukonUser();
        YukonGroup group2 = new YukonGroup( 3 ); // probably want to change this before using again
        
        user.getYukonGroups().add(group2);
        
        String firstName = contact.getContact().getContFirstName();
        String lastName = contact.getContact().getContLastName();
        String salt = RandomStringUtils.randomNumeric(5);
        String stupidUserName = firstName.toLowerCase().substring(0, 1) + lastName.toLowerCase() + salt;
        user.getYukonUser().setUsername(stupidUserName);
        user.getYukonUser().setAuthType(AuthType.NONE);
        user.getYukonUser().setLoginStatus(LoginStatusEnum.ENABLED);
        dbPersistentDao.performDBChange(user, Transaction.INSERT);
        return user;
    }
}
