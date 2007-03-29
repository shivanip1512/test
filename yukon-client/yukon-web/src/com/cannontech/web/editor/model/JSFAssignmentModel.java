package com.cannontech.web.editor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

public abstract class JSFAssignmentModel {
    private static final String ID = "id";
    List<Integer> assignedSubs;
    List<Integer> unassignedSubs;
    JSFAssignmentModel assignmentModel;
    //exists for testing purposes
    private Integer settableID;

    public JSFAssignmentModel() {
        super();
        getAssigned();
        getUnassigned();
        getContext();

    }

    abstract FacesContext getContext();

    public List<Integer> getUnassigned() {
        if (unassignedSubs == null) {
            unassignedSubs = new ArrayList<Integer>();
            populateUnassigned();
            unassignedSubs.removeAll(getAssigned());
        }
        return unassignedSubs;
    }

    abstract void populateUnassigned();

    public List<Integer> getAssigned() {
        if (assignedSubs == null) {
            assignedSubs = new ArrayList<Integer>();
            populateAssigned();
        }
        return assignedSubs;
    }

    abstract void populateAssigned();

    public void add() {
        getAssigned().add(getSelectedID());
        getUnassigned().remove(getSelectedID());
    }

    public void remove() {
        getAssigned().remove(getSelectedID());
        getUnassigned().add(getSelectedID());

    }

    public Integer getSelectedID() {
        Integer elemID = getSelectedThroughJSFContext();
        if (elemID == null)
        {
            elemID = getSettableID();
        }
        return elemID;
    }

    public Integer getSettableID() {
        return settableID;
    }

    private Integer getSelectedThroughJSFContext() {
        Integer elemID = null;
        if (getContext() != null)
        {
            Map paramMap = getContext().getExternalContext()
                                       .getRequestParameterMap();
            elemID = new Integer((String) paramMap.get(ID)).intValue();
        }
        return elemID;
    }

    public void reset() {
        unassignedSubs = null;
        assignedSubs = null;
        getUnassigned();
        getAssigned();
    }

    public void setSettableID(Integer settableID) {
        this.settableID = settableID;
    }
}
