package com.cannontech.database.data.lite;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.config.MCTConfig;

public class LiteConfig extends LiteBase {
    private String configName;
    private Integer configType;

    public LiteConfig() {
        super();
        setLiteType(LiteTypes.CONFIG);
    }

    public LiteConfig(int configID) {
        super();
        setLiteID(configID);
        setLiteType(LiteTypes.CONFIG);
    }

    public LiteConfig(int configID, String conName_) {
        this(configID);
        setConfigName(conName_);
    }

    public LiteConfig(int configID, String conName_, Integer type) {
        this(configID);
        setConfigName(conName_);
        setConfigType(type);
    }

    public int getConfigID() {
        return getLiteID();
    }

    public String getConfigName() {
        return configName;
    }

    public Integer getConfigType() {
        return configType;
    }

    /**
     * retrieve method comment.
     */
    public void retrieve(String databaseAlias) {

        SqlStatement s = new SqlStatement("SELECT configID, configName, configType " + 
                                          "FROM " + MCTConfig.TABLE_NAME + 
                                          " where configID = " + getConfigID(),
                                          CtiUtilities.getDatabaseAlias());

        try {
            s.execute();

            if (s.getRowCount() <= 0)
                throw new IllegalStateException("Unable to find config with ID = " + getLiteID());

            setConfigID(new Integer(s.getRow(0)[0].toString()).intValue());
            setConfigName(s.getRow(0)[1].toString());
            setConfigType(new Integer(s.getRow(0)[2].toString()));
        } catch (Exception e) {
            com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
        }

    }

    public void setConfigID(int conID) {
        setLiteID(conID);
    }

    public void setConfigName(String name) {
        configName = name;
    }

    public void setConfigType(Integer type) {
        configType = type;
    }

    @Override
    public String toString() {
        return getConfigName();
    }
}