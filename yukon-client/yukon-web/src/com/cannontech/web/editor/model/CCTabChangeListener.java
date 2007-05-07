package com.cannontech.web.editor.model;

import javax.faces.event.AbortProcessingException;

import org.apache.myfaces.custom.tabbedpane.TabChangeEvent;
import org.apache.myfaces.custom.tabbedpane.TabChangeListener;

import com.cannontech.web.editor.CapControlForm;
import com.cannontech.web.util.JSFParamUtil;
import com.cannontech.web.util.JSFUtil;

public class CCTabChangeListener implements TabChangeListener {

    public void processTabChange(TabChangeEvent tabChangeEvent)
            throws AbortProcessingException {
        CapControlForm form = (CapControlForm) JSFParamUtil.getJSFVar("capControlForm");
        form.clearfaces();
    }

}
