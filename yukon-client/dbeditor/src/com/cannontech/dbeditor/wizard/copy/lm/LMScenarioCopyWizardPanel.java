package com.cannontech.dbeditor.wizard.copy.lm;

import java.awt.Dimension;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.wizard.WizardPanel;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.db.DBPersistent;

/**
 * All Panels used in this WizardPanel MUST be able to handle MultiDBPersistent
 * objects in their getValue(Object o) method!!!
 */
public class LMScenarioCopyWizardPanel extends WizardPanel {
    private LMScenarioCopyNameSettingsPanel lmScenarioCopyNameSettingsPanel;

    private DBPersistent copyObject = null;

    public LMScenarioCopyWizardPanel(DBPersistent objectToCopy) {
        setCopyObject(objectToCopy);
    }

    @Override
    public Dimension getActualSize() {
        setPreferredSize(new Dimension(410, 480));

        return getPreferredSize();
    }

    public DBPersistent getCopyObject() {
        return copyObject;
    }

    protected LMScenarioCopyNameSettingsPanel getLMScenarioCopyNameSettingsPanel() {
        if (lmScenarioCopyNameSettingsPanel == null) {
            lmScenarioCopyNameSettingsPanel = new LMScenarioCopyNameSettingsPanel();
        }

        return lmScenarioCopyNameSettingsPanel;
    }

    @Override
    protected String getHeaderText() {
        return "Copy LM Scenario";
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    protected DataInputPanel getNextInputPanel(DataInputPanel currentInputPanel) {
        if (currentInputPanel == null) {
            return getLMScenarioCopyNameSettingsPanel();
        }

        throw new Error(getClass() + "::" + "getNextInputPanel() - Could not determine next DataInputPanel");
    }

    @Override
    public Object getValue(Object o) {
        return super.getValue(getCopyObject());
    }

    @Override
    protected boolean isLastInputPanel(DataInputPanel currentPanel) {
        return currentPanel == getLMScenarioCopyNameSettingsPanel();
    }

    public void setCopyObject(DBPersistent newObject) {
        try {
            copyObject = newObject;

            Transaction t = Transaction.createTransaction(Transaction.RETRIEVE,
                                                          copyObject);

            copyObject = t.execute();
        } catch (TransactionException e) {
            CTILogger.error(e.getMessage(), e);
        }
    }
}
