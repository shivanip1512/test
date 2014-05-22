package com.cannontech.dbeditor.wizard.copy.lm;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.wizard.WizardPanel;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.db.DBPersistent;

/* All Panels used in this WizardPanel MUST be able to handle MultiDBPersistent */
/* Objects in their getValue(Object o) method!!! */
public class LMProgramCopyWizardPanel extends WizardPanel {
    private LMProgramCopyNameSettingsPanel lmProgramCopyNameSettingsPanel;

    private DBPersistent copyObject = null;

    public LMProgramCopyWizardPanel(
            com.cannontech.database.db.DBPersistent objectToCopy) {
        super();
        setCopyObject(objectToCopy);
    }

    @Override
    public java.awt.Dimension getActualSize() {
        setPreferredSize(new java.awt.Dimension(410, 480));
        return getPreferredSize();
    }

    public DBPersistent getCopyObject() {
        return copyObject;
    }

    protected LMProgramCopyNameSettingsPanel getLMProgramCopyNameSettingsPanel() {
        if (lmProgramCopyNameSettingsPanel == null)
            lmProgramCopyNameSettingsPanel = new LMProgramCopyNameSettingsPanel();

        return lmProgramCopyNameSettingsPanel;
    }

    @Override
    protected String getHeaderText() {
        return "Copy LM Direct Program";
    }

    @Override
    public java.awt.Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    protected DataInputPanel getNextInputPanel(DataInputPanel currentInputPanel) {

        if (currentInputPanel == null) {
            return getLMProgramCopyNameSettingsPanel();
        } else
            throw new Error(getClass() + "::" + "getNextInputPanel() - Could not determine next DataInputPanel");
    }

    @Override
    public Object getValue(Object o) {
        return super.getValue(getCopyObject());
    }

    @Override
    protected boolean isLastInputPanel(DataInputPanel currentPanel) {
        return currentPanel == getLMProgramCopyNameSettingsPanel();
    }

    public void setCopyObject(DBPersistent newObject) {
        try {
            copyObject = newObject;

            Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, copyObject);

            copyObject = t.execute();
        } catch (TransactionException e) {
            CTILogger.error(e.getMessage(), e);
        }
    }
}
