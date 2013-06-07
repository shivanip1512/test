package com.cannontech.dispatch;

import java.util.Set;

import org.apache.commons.lang.Validate;

import com.cannontech.messaging.message.dispatch.DBChangeMessage;

public class DbChangeHelper {
    public static DBChangeMessage newDbChange(DbChangeType type, DbChangeCategory category, int primaryKey) {
    	// see DbChangeIdentifier, for that code to work, we need to create a unique "database" for each category of change
    	// otherwise, the value doesn't matter
    	int fakeDatabaseNum = DBChangeMessage.USES_NEW_CATEGORY_ENUM - category.ordinal();
    	DBChangeMessage msg = new DBChangeMessage(primaryKey, fakeDatabaseNum, category.getStringRepresentation(), type);
        return msg;
    }

    public static DatabaseChangeEvent findMatchingEvent(final DBChangeMessage originalMsg, Set<DbChangeType> types, final DbChangeCategory category) {
        Validate.notNull(category);
        Validate.notNull(types);
        Validate.notNull(originalMsg);
        final DbChangeType dbChangeType = originalMsg.getDbChangeType();
        if (!types.contains(dbChangeType)) return null;
        
        String originalCategory = originalMsg.getCategory();
        if (!category.getStringRepresentation().equals(originalCategory)) return null;
        
        return new DatabaseChangeEvent() {
            
            @Override
            public int getPrimaryKey() {
                return originalMsg.getId();
            }
            
            @Override
            public DbChangeType getChangeType() {
                return dbChangeType;
            }
            
            @Override
            public DbChangeCategory getChangeCategory() {
                return category;
            }
        };
    }
    
    public static DatabaseChangeEvent convertToEvent(final DBChangeMessage originalMsg) {
        final DbChangeCategory changeCategory = DbChangeCategory.findForStringRepresentation(originalMsg.getCategory());
        if (changeCategory == null) return null;
        
        return new DatabaseChangeEvent() {
            
            @Override
            public int getPrimaryKey() {
                return originalMsg.getId();
            }
            
            @Override
            public DbChangeType getChangeType() {
                return originalMsg.getDbChangeType();
            }
            
            @Override
            public DbChangeCategory getChangeCategory() {
                return changeCategory;
            }
        };
    }
}
