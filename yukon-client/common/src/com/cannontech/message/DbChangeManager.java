package com.cannontech.message;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;

public interface DbChangeManager {

    /**
     * Process a DbChange. Creates a DBChangeMsg and sends it to dispatch with the information
     * passed in by parameter.
     * @param id - the id of the object the dbChange is being processed for.
     * @param database - the DbChangeMsg database category
     * @param category - the PaoCategory
     * @param objectType - string representation of the object type.
     * @param dbChangeType - the type of dbchange being processed (none, add, update, delete)
     */
    public void processDbChange(int id, int database, String category, String objectType, DbChangeType dbChangeType);
    
    /**
     * Process a DbChange. Creates a DBChangeMsg and sends it to dispatch with the information
     * passed in by parameter. Constructs the DbChangeMsg without an objectType (which will be
     * defaulted to CtiUtilities.STRING_NONE.)
     * @param id - the id of the object the dbChange is being processed for.
     * @param database - the DbChangeMsg database category
     * @param category - the PaoCategory
     * @param dbChangeType - the type of dbchange being processed (none, add, update, delete)
     */
    public void processDbChange(int id, int database, String category, DbChangeType dbChangeType);

    /**
     * Process a DbChangeMessage for a specific YukonPao. Creates a DBChangeMsg and sends it 
     * to dispatch with the information from the YukonPao and the change type provided.
     * @param pao
     * @param changeType
     */
    public void processPaoDbChange(YukonPao pao, DbChangeType changeType);
    
    /**
     * The general case for processing a DbChange. Sends the DBChangeMsg passed as a parameter
     * to dispatch. While this method isn't deprecated,<b> you should use one of the 
     * other processDbChange methods.</b> This method is only for use within the DbChangeManager
     * class and in instances where DBChangeMsg objects have already been created.
     * @param dbChange
     */
    public void processDbChange(DBChangeMsg dbChange);

    /**
     * This sends a simpler form of a DB Change that can be used in new code where the 
     * {@link com.cannontech.core.dynamic.AsyncDynamicDataSource#addDatabaseChangeEventListener(com.cannontech.core.dynamic.DatabaseChangeEventListener)}
     * will be used on the receiving end.
     * @param type
     * @param category
     * @param primaryKey
     */
    public void processDbChange(DbChangeType type, DbChangeCategory category, int primaryKey);
}