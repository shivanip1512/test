package com.cannontech.stars.web.action;

import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.StarsMsgUtils;
import com.cannontech.stars.xml.serialize.StarsUpdateLogin;

public class LoginAction {
    
    private static final AuthenticationService authenticationService = (AuthenticationService) YukonSpringHook.getBean("authenticationService");

    /* from updateLoginAction.java */
    public static LiteYukonUser createLogin(StarsUpdateLogin login,
                                            LiteContact liteContact,
                                            LiteStarsEnergyCompany energyCompany,
                                            boolean authTypeChange)
            throws TransactionException {
        RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
        LiteYukonUser user = energyCompany.getUser();
        AuthType defaultAuthType = rolePropertyDao.getPropertyEnumValue(
                                                                        YukonRoleProperty.DEFAULT_AUTH_TYPE,
                                                                        AuthType.class,
                                                                        user);
    
        com.cannontech.database.data.user.YukonUser dataUser = new com.cannontech.database.data.user.YukonUser();
        com.cannontech.database.db.user.YukonUser dbUser = dataUser.getYukonUser();
    
        if (login.hasGroupID()) {
            com.cannontech.database.db.user.YukonGroup dbGroup = new com.cannontech.database.db.user.YukonGroup();
            dbGroup.setGroupID(new Integer(login.getGroupID()));
            dataUser.getYukonGroups().addElement(dbGroup);
        }
    
        dbUser.setUsername(login.getUsername());
        if (authTypeChange) {
            dbUser.setAuthType(AuthType.NONE);
        } else {
            dbUser.setAuthType(defaultAuthType);
        }
    
        if (login.getStatus() != null) {
            dbUser.setLoginStatus(StarsMsgUtils.getUserStatus(login.getStatus()));
        } else {
            dbUser.setLoginStatus(LoginStatusEnum.ENABLED);
        }
    
        dataUser = Transaction.createTransaction(Transaction.INSERT, dataUser).execute();
        LiteYukonUser liteUser = new LiteYukonUser(dbUser.getUserID().intValue(), dbUser.getUsername(), dbUser.getLoginStatus());
        liteUser.setAuthType(dbUser.getAuthType());
    
        ServerUtils.handleDBChange(liteUser, DbChangeType.ADD);
    
        if (authenticationService.supportsPasswordSet(defaultAuthType) && !authTypeChange) {
            authenticationService.setPassword(liteUser, login.getPassword());
        }
    
        if (liteContact != null) {
            liteContact.setLoginID(liteUser.getUserID());
            com.cannontech.database.data.customer.Contact contact = (com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent(liteContact);
            Transaction.createTransaction(Transaction.UPDATE, contact.getContact()).execute();
            ServerUtils.handleDBChange(liteContact, DbChangeType.UPDATE);
        }
    
        return liteUser;
    }

    /* from UpdateLoginAction */
    public static void deleteLogin(int userID) throws TransactionException {
        LiteContact liteContact = DaoFactory.getYukonUserDao().getLiteContact(userID);
        if (liteContact != null) {
            liteContact.setLoginID(com.cannontech.user.UserUtils.USER_DEFAULT_ID);
            com.cannontech.database.data.customer.Contact contact = (com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent(liteContact);
            Transaction.createTransaction(Transaction.UPDATE, contact.getContact()).execute();
            ServerUtils.handleDBChange(liteContact, DbChangeType.UPDATE);
        }
    
        com.cannontech.database.data.user.YukonUser yukonUser = new com.cannontech.database.data.user.YukonUser();
        LiteYukonUser liteUser = DaoFactory.getYukonUserDao().getLiteYukonUser(userID);
        yukonUser.setUserID(new Integer(userID));
        Transaction.createTransaction(Transaction.DELETE, yukonUser).execute();
    
        StarsDatabaseCache.getInstance().deleteStarsYukonUser(userID);
        ServerUtils.handleDBChange(liteUser, DbChangeType.DELETE);
    }

}
