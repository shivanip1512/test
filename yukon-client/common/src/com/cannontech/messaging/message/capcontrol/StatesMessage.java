package com.cannontech.messaging.message.capcontrol;

import java.util.Vector;

import com.cannontech.database.db.state.State;

public class StatesMessage extends CapControlMessage {

    private Vector<State> states;

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
