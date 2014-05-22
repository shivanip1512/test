package com.cannontech.common.gui.util;

import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.editor.EditorInputValidationException;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;

public abstract class DataInputPanel extends JPanel {
    private List<DataInputPanelListener> listeners = new Vector<DataInputPanelListener>();

    // set this string to the message of an error when isInputValid() returns false,
    // and it will print to the screen.
    private String errorString = " ";

    /**
     * Override this to set the cursor or focus on a specific element of the UI.
     */
    public void setFirstFocus() {
        // Default empty implementation.
    }

    public void addDataInputPanelListener(DataInputPanelListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void fireInputUpdate() {
        for (int i = listeners.size() - 1; i >= 0; i--) {
            listeners.get(i).inputUpdate(new PropertyPanelEvent(this));
        }
    }

    public void fireInputDataPanelEvent(PropertyPanelEvent ev) {
        for (int i = listeners.size() - 1; i >= 0; i--) {
            listeners.get(i).inputUpdate(ev);
        }
    }

    public String getErrorString() {
        return errorString;
    }

    public abstract Object getValue(Object o) throws EditorInputValidationException;

    /**
     * This method must be implemented if a notion of data validity needs to be supported.
     * 
     * This method should be overridden all the time.
     * IN THE FUTURE THIS METHOD WILL BE MADE abstract.
     */
    public boolean isInputValid() {
        return true;
    }

    public void removeDataInputPanelListener(DataInputPanelListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public void setErrorString(String newErrorString) {
        errorString = newErrorString;
    }

    public abstract void setValue(Object o);

    public void disposeValue() {
        // nothing by default, used to clean up large pieces of memory
    }

    /**
     * @param dbPersistent The dbPersistent that was just saved.
     */
    public void postSave(DBPersistent dbPersistent) {
        // default nothing
    }

    /**
     * Helper method to determine if pao is unique.
     * If thisPaobjectId is the same as a paobjectId, then still considered unique.
     */
    protected boolean isUniquePao(String paoName, PaoType paoType, int thisPaobjectId) {
        LiteYukonPAObject liteYukonPAObject =
            YukonSpringHook.getBean(PaoDao.class).findUnique(paoName, paoType);

        // if one is found, compare it to deviceBase to see if its this.
        if (liteYukonPAObject != null) {
            return liteYukonPAObject.getYukonID() == thisPaobjectId;
        }

        // liteYukonPaobject will be null if matching object was not found.
        return (liteYukonPAObject == null);
    }

    protected boolean isUniquePao(String paoName, PaoType paoType) {
        return isUniquePao(paoName, paoType, -1);
    }

    public final static Integer getIntervalSecondsValue(String selectedString) {
        Integer generic = null;
        Integer retVal = null;
        int multiplier = 1;

        if (selectedString == null) {
            retVal = 0; // we have no idea, just use zero
        } else if (selectedString.toLowerCase().compareTo("daily") == 0) {
            generic = 86400;
            return generic;
        } else if (selectedString.toLowerCase().compareTo("weekly") == 0) {
            generic = 604800;
            return generic;
        } else if (selectedString.toLowerCase().compareTo("monthly") == 0) {
            generic = 2592000;
            return generic;
        } else if (selectedString.toLowerCase().indexOf("second") != -1) {
            multiplier = 1;
        } else if (selectedString.toLowerCase().indexOf("minute") != -1) {
            multiplier = 60;
        } else if (selectedString.toLowerCase().indexOf("hour") != -1) {
            multiplier = 3600;
        } else if (selectedString.toLowerCase().indexOf("day") != -1) {
            multiplier = 86400;
        } else {
            multiplier = 0; // we have no idea, just use zero
        }

        try {
            int loc = selectedString.toLowerCase().indexOf(" ");

            retVal = multiplier * Integer.parseInt(selectedString.toLowerCase().substring(0, loc));
        } catch (Exception e) {
            CTILogger.error("Unable to parse combo box text string into seconds, using ZERO", e);
            retVal = 0;
        }

        return retVal;
    }
}
