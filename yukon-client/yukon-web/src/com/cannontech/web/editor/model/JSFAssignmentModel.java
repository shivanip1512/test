package com.cannontech.web.editor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

public abstract class JSFAssignmentModel {
    private static final String ID = "ID";
    List<Integer> assignedSubs;
    List<Integer> unassignedSubs;
    JSFAssignmentModel assignmentModel;

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
        assignedSubs.add(getSelectedID());
        unassignedSubs.remove(getSelectedID());
    }

    public void remove() {
        assignedSubs.remove(getSelectedID());
        unassignedSubs.add(getSelectedID());

    }

    public int getSelectedID() {
        Map paramMap = getContext().getExternalContext()
                                   .getRequestParameterMap();
        int elemID = new Integer((String) paramMap.get(ID)).intValue();
        return elemID;
    }

    public void reset() {
        unassignedSubs = null;
        assignedSubs = null;
        getUnassigned();
        getAssigned();
    }
}
