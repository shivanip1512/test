package com.cannontech.debug.gui;

import java.awt.Dialog;
import java.util.Collection;
import java.util.Vector;

import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;

/**
 * A dialog that provides some environment information , and optionally, some 
 * application specific information.
 * 
 */
public class AboutDialog extends com.cannontech.debug.gui.AbstractListDataViewer {
    /* The parent class doesn't honor the constructors title
     * which is why it is explicitly in all the constructors
     */
    private Vector<String> _listData;
    private static final String DEFAULT_TITLE = "About";

    {   // Initializer
        init();
    }

    /**
     * ScheduleDebugViewer constructor comment.
     */
    public AboutDialog() {
        super((Dialog)null, DEFAULT_TITLE);
        setTitle(DEFAULT_TITLE);
    }

    /**
     * @param owner java.awt.Dialog
     */
    public AboutDialog(java.awt.Dialog owner) {
        super(owner, DEFAULT_TITLE);
        setTitle(DEFAULT_TITLE);
    }

    /**
     * @param owner java.awt.Dialog
     * @param title java.lang.String
     */
    public AboutDialog(java.awt.Dialog owner, String title) {
        super(owner, title);
        setTitle(title);
    }

    /**
     * @param owner java.awt.Dialog
     * @param title java.lang.String
     * @param modal boolean
     */
    public AboutDialog(java.awt.Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
        setTitle(title);
    }

    /**
     * @param owner java.awt.Dialog
     * @param modal boolean
     */
    public AboutDialog(java.awt.Dialog owner, boolean modal) {
        super(owner, DEFAULT_TITLE, modal);
    }

    /**
     * @param owner java.awt.Frame
     */
    public AboutDialog(java.awt.Frame owner) {
        super(owner, DEFAULT_TITLE);
    }

    /**
     * @param owner java.awt.Frame
     * @param title java.lang.String
     */
    public AboutDialog(java.awt.Frame owner, String title) {
        super(owner, title);
        setTitle(title);
    }

    /**
     * @param owner java.awt.Frame
     * @param title java.lang.String
     * @param modal boolean
     */
    public AboutDialog(java.awt.Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        setTitle(title);
    }

    /**
     * @param owner java.awt.Frame
     * @param modal boolean
     */
    public AboutDialog(java.awt.Frame owner, boolean modal) {
        super(owner, DEFAULT_TITLE, modal);
        setTitle(DEFAULT_TITLE);
    }

    /**
     * Don't call this.
     */
    @Override
    public Object getValue() {
        throw new Error("Don't call this");
    }

    /**
     * Adds a Collection of Objects to the list to be displayed.
     */
    @Override
    public void setValue(Object obj) {
        if(obj instanceof Collection) {
            _listData.addAll((Collection<String>) obj);
        }
    }

    private void init() {
        _listData = new Vector<String>();

        try {
            // Be sure to not include sensitive information here including DB user/pass credentials
            _listData.addElement(CtiUtilities.COPYRIGHT);
            _listData.addElement("Version         : " + com.cannontech.common.version.VersionTools.getYUKON_VERSION());
            _listData.addElement("Version Details : " + com.cannontech.common.version.VersionTools.getYukonDetails());

            _listData.addElement("JRE Version     : " + System.getProperty("java.version"));

            /* ALWAYS leave this as the last thing */
            com.cannontech.database.db.version.CTIDatabase db = com.cannontech.common.version.VersionTools.getDatabaseVersion();
            _listData.addElement("DB Version      : " + db.getVersion() + "  Build:  " + db.getBuild());
            _listData.addElement("User            : " + ClientSession.getInstance().getUser().getUsername());

        } catch(Throwable t) {  // Catch ALL things and just print them out
            com.cannontech.clientutils.CTILogger.info("*** Throwable caught in + " + this.getClass() + " : " + t.getMessage());
            _listData.addElement("(Exception occurred while getting values from database)");
        }
        getJListInfo().setListData(_listData);
    }
}
