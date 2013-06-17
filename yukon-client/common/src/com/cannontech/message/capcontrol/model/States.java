package com.cannontech.message.capcontrol.model;

import java.util.Vector;

import com.cannontech.database.db.state.State;

public class States extends CapControlMessage {
	
	private Vector<State> states;
	
    public States() {
    	super();
    }
    
    public int getNumberOfStates() {
    	return states.size();
    }
    
    public State getState(int index) {
    	return states.get(index);
    }
    
    public Vector<State> getStates() {
    	return states;
    }
    
    public void setStates(Vector<State> allStates) {
    	states = allStates;
    }

}