package com.cannontech.web.editor;

import java.sql.Connection;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.util.JSFParamUtil;
import com.cannontech.yukon.conns.ConnPool;

/**
 * Form for executing editor changes for DBPersitants
 */
public abstract class DBEditorForm {
    // data object that represents what is being edited
    private DBPersistent dbPersistent = null;

    // contains: Key:"TabName", Value:Boolean
    private Map<String, Boolean> visibleTabs = new HashMap<String, Boolean>(16);

    // title of our editor panel
    private String editorTitle = "Database";

    // dummy UI comp to be used for internal event firing only
    protected static final UIComponent DUMMY_UI = new UIData();

    /**
     * Resets this form with the original values from the database
     */
    public void resetForm() {/* Override this if needed */}

    /**
     * Updates a given DB object.
     */
    protected void updateDBObject(DBPersistent db, FacesMessage facesMsg)
            throws TransactionException {
        db.setDbConnection(null);
        if (facesMsg == null)
            facesMsg = new FacesMessage();

        try {
            Transaction<DBPersistent> t = Transaction.createTransaction(Transaction.UPDATE, db);
            t.execute();

            generateDBChangeMsg(db, DbChangeType.UPDATE);
        } catch (TransactionException e) {
            CTILogger.error(e.getMessage(), e);
            facesMsg.setDetail("Error updating the database, " + e.getMessage());
            if(e.getCause().getMessage().contains("duplicate")) {
                facesMsg.setSummary("There may already be a object with that name.");
            }
            facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw e; // chuck this thing up
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
            facesMsg.setDetail("Unable to update the database, " + e.getMessage());
            facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new TransactionException(e.getMessage(), e); // chuck this
                                                                // thing up
        }

    }

    /**
     * Add a given DB object.
     */
    protected void addDBObject(DBPersistent db, FacesMessage facesMsg)
            throws TransactionException {

        if (facesMsg == null)
            facesMsg = new FacesMessage();

        try {
            Transaction<DBPersistent> t = Transaction.createTransaction(Transaction.INSERT, db);
            t.execute();
        }

        catch (TransactionException e) {
            CTILogger.error(e.getMessage(), e);
            facesMsg.setDetail("Error insert into the database, " + e.getMessage());
            facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw e; // chuck this thing up
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
            facesMsg.setDetail("Unable to insert into the database, " + e.getMessage());
            facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new TransactionException(e.getMessage(), e); // chuck this
                                                                // thing up
        }

        try {
            generateDBChangeMsg(db, DbChangeType.ADD);
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
            facesMsg.setDetail("Error with Connection to Servers, " + e.getMessage());
            facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new TransactionException(e.getMessage(), e); // chuck this
                                                                // thing up
        }
    }

    /**
     * Delete a given DB object.
     */
    protected void deleteDBObject(DBPersistent db, FacesMessage facesMsg)
            throws TransactionException {

        if (db == null)
            return;
        if (facesMsg == null)
            facesMsg = new FacesMessage();

        try {
            Transaction<DBPersistent> t = Transaction.createTransaction(Transaction.DELETE, db);
            t.execute();

            generateDBChangeMsg(db, DbChangeType.DELETE);
        } catch (TransactionException e) {
            CTILogger.error(e.getMessage(), e);
            facesMsg.setDetail("Error deleting '" + db + "' from the database: " + e.getMessage());
            facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw e; // chuck this thing up
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
            facesMsg.setDetail("Unable to delete '" + db + "' from the database: " + e.getMessage());
            facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new TransactionException(e.getMessage(), e); // chuck this
                                                                // thing up
        }

    }

    /**
     * Insert the method's description here. Creation date: (10/9/2001 1:49:24
     * PM)
     */
    protected void generateDBChangeMsg(DBPersistent object, DbChangeType dbChangeType) {
        if (object instanceof CTIDbChange) {
            DBChangeMsg[] dbChange = DefaultDatabaseCache.getInstance()
                                                         .createDBChangeMessages((CTIDbChange) object,
                                                                                 dbChangeType);

            for (int i = 0; i < dbChange.length; i++) {
                // set the username for each dbchagne to be the current Yukon
                // User logged in
                if (JSFParamUtil.getYukonUser() != null)
                    dbChange[i].setUserName(JSFParamUtil.getYukonUser()
                                                        .getUsername());

                DefaultDatabaseCache.getInstance()
                                    .handleDBChangeMessage(dbChange[i]);
                ConnPool.getInstance().getDefDispatchConn().write(dbChange[i]);
            }
        } else {
            throw new IllegalArgumentException("Non " + CTIDbChange.class.getName() + " class tried to generate a " + DBChangeMsg.class.getName() + " its class was : " + object.getClass()
                                                                                                                                                                                .getName());
        }

    }

    /**
     * Retrieves the current DB object from the database. Returns the DB object
     * that was retrieved from the DB
     */
    protected DBPersistent retrieveDBPersistent() {

        Connection conn = null;
        if (getDbPersistent() == null)
            return null;

        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            getDbPersistent().setDbConnection(conn);
            getDbPersistent().retrieve();
        } catch (SQLException sql) {
            CTILogger.error("Unable to retrieve DB Object", sql);
            return null;
        } catch(IncorrectResultSizeDataAccessException e){
            return null;
        } finally {
            getDbPersistent().setDbConnection(null);
            try {
                if (conn != null)
                    conn.close();
            } catch (java.sql.SQLException e2) {}
        }

        return getDbPersistent();
    }

    /**
     * @return
     */
    public DBPersistent getDbPersistent() {
        return dbPersistent;
    }

    /**
     * @param persistent
     */
    protected void setDbPersistent(DBPersistent persistent) {
        dbPersistent = persistent;
    }

    public Map<String, Boolean> getVisibleTabs() {
        return visibleTabs;
    }

    /**
     * @return
     */
    public String getEditorTitle() {
        return editorTitle;
    }

    /**
     * @param string
     */
    public void setEditorTitle(String string) {
        editorTitle = string;
    }

    protected abstract void checkForErrors() throws Exception;

}
