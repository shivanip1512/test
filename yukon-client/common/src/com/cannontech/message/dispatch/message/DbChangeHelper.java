package com.cannontech.message.dispatch.message;

import java.util.Set;

import org.apache.commons.lang.Validate;

public class DbChangeHelper {
    public static DBChangeMsg newDbChange(DbChangeType type, DbChangeCategory category, int primaryKey) {
        DBChangeMsg msg = new DBChangeMsg(primaryKey, DBChangeMsg.CHANGE_DO_NOT_CARE_DB, category.getStringRepresentation(), type);
        return msg;
    }

    public static DatabaseChangeEvent findMatchingEvent(final DBChangeMsg originalMsg, Set<DbChangeType> types, final DbChangeCategory category) {
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
    
    public static DatabaseChangeEvent convertToEvent(final DBChangeMsg originalMsg) {
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
