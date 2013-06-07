package com.cannontech.core.roleproperties.dao.impl;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.dispatch.DbChangeType;
import com.cannontech.messaging.message.dispatch.DBChangeMessage;

public class RolePropertyChangeHelper {
    
    private AsyncDynamicDataSource asyncDynamicDataSource;
    private RolePropertyDaoImpl rolePropertyDaoImpl;
    private Logger log = YukonLogManager.getLogger(RolePropertyChangeHelper.class);
    
    @PostConstruct
    public void setup() {
        asyncDynamicDataSource.addDBChangeListener(new DBChangeListener() {

            @Override
            public void dbChangeReceived(DBChangeMessage dbChange) {
                 if (dbChange.getDatabase() == DBChangeMessage.CHANGE_YUKON_USER_DB && (dbChange.getDbChangeType() == DbChangeType.UPDATE || 
                                                                                    dbChange.getDbChangeType() == DbChangeType.DELETE)) {
                     if (log.isDebugEnabled()) {
                         log.debug("sending clearCache to RolePropertyDao because: " + dbChange);
                     }
                     rolePropertyDaoImpl.clearCache();
                 }
            }
        });
        
        // now that we're registered, clear out the cache of anything accumulated until now
        rolePropertyDaoImpl.clearCache();
    }
    
    @Autowired
    public void setAsyncDynamicDataSource(
            AsyncDynamicDataSource asyncDynamicDataSource) {
        this.asyncDynamicDataSource = asyncDynamicDataSource;
    }
    
    @Autowired
    public void setRolePropertyDaoImpl(RolePropertyDaoImpl rolePropertyDaoImpl) {
        this.rolePropertyDaoImpl = rolePropertyDaoImpl;
    }

}
