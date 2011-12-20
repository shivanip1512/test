package com.cannontech.stars.xml.serialize;


import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class StarsLMControlHistory {

    private boolean _beingControlled;

    /**
     * keeps track of state for field: _beingControlled
    **/
    private boolean _has_beingControlled;

    private Vector<ControlHistoryEntry> _controlHistoryList;

    private ControlSummary _controlSummary;

    public StarsLMControlHistory() {
        super();
        _controlHistoryList = new Vector<ControlHistoryEntry>();
    }

    public void addControlHistory(ControlHistoryEntry vControlHistory)
        throws java.lang.IndexOutOfBoundsException
    {
        _controlHistoryList.addElement(vControlHistory);
    } 

    public void addControlHistory(int index, ControlHistoryEntry vControlHistory)
        throws java.lang.IndexOutOfBoundsException
    {
        _controlHistoryList.insertElementAt(vControlHistory, index);
    }

    public void deleteBeingControlled()
    {
        this._has_beingControlled= false;
    }

    public Enumeration<ControlHistoryEntry> enumerateControlHistory()
    {
        return _controlHistoryList.elements();
    }

    /**
     * Returns the value of field 'beingControlled'.
     * 
     * @return the value of field 'beingControlled'.
    **/
    public boolean getBeingControlled()
    {
        return this._beingControlled;
    }

    public ControlHistoryEntry getControlHistory(int index)
        throws IndexOutOfBoundsException
    {
        if ((index < 0) || (index > _controlHistoryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (ControlHistoryEntry) _controlHistoryList.elementAt(index);
    } 

    public ControlHistoryEntry[] getControlHistory()
    {
        int size = _controlHistoryList.size();
        ControlHistoryEntry[] mArray = new ControlHistoryEntry[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (ControlHistoryEntry) _controlHistoryList.elementAt(index);
        }
        return mArray;
    }
    
    public List<ControlHistoryEntry> getControlHistoryList() {
        return _controlHistoryList;
    }

    public int getControlHistoryCount()
    {
        return _controlHistoryList.size();
    } 

    public ControlSummary getControlSummary()
    {
        return this._controlSummary;
    } 

    public boolean hasBeingControlled()
    {
        return this._has_beingControlled;
    }

    public void removeAllControlHistory()
    {
        _controlHistoryList.removeAllElements();
    } 

    public ControlHistoryEntry removeControlHistory(int index)
    {
        Object obj = _controlHistoryList.elementAt(index);
        _controlHistoryList.removeElementAt(index);
        return (ControlHistoryEntry) obj;
    } 

    public ControlHistoryEntry removeControlHistory(ControlHistoryEntry controlHistory)
    {
        _controlHistoryList.remove(controlHistory);
        if (_controlSummary != null) {
            _controlSummary.subtractDuration(controlHistory.getControlDuration());
        }
        return controlHistory;
    } 

    
    public void setBeingControlled(boolean beingControlled)
    {
        this._beingControlled = beingControlled;
        this._has_beingControlled = true;
    } 

    public void setControlHistory(int index, ControlHistoryEntry vControlHistory)
        throws IndexOutOfBoundsException
    {
        if ((index < 0) || (index > _controlHistoryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _controlHistoryList.setElementAt(vControlHistory, index);
    }

    public void setControlHistory(ControlHistoryEntry[] controlHistoryArray)
    {
        _controlHistoryList.removeAllElements();
        for (int i = 0; i < controlHistoryArray.length; i++) {
            _controlHistoryList.addElement(controlHistoryArray[i]);
        }
    } 

    public void setControlSummary(ControlSummary controlSummary)
    {
        this._controlSummary = controlSummary;
    } 

}
