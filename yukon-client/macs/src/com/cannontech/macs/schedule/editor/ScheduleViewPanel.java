package com.cannontech.macs.schedule.editor;

import com.cannontech.clientutils.CTILogger;

public class ScheduleViewPanel extends ScheduleEditorPanel {

    public ScheduleViewPanel() {
        super();
        initialize();
    }

    private void initialize() {
        try {
            setName("ScheduleViewPanel");
            getPropertyButtonPanel().getOkJButton().setVisible(false);
            getPropertyButtonPanel().getApplyJButton().setVisible(false);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    
    public String toString() {
        return "View Schedule";
    }
    
    private void handleException(java.lang.Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        CTILogger.error(exception.getMessage(), exception);
        ;
    }
}
