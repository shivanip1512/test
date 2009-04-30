package com.cannontech.stars.xml.serialize;

import java.util.Vector;

/*
 * NOTE: this is used to not just show enrolled programs, but all programs off of an appliance category
 * So why the #$%& did he call it Stars"Enr"LMProgram?
 */
public class StarsEnrLMProgram {
    private int _programID;
    private boolean _has_programID;
    private int _deviceID;
    private boolean _has_deviceID;
    private java.lang.String _yukonName;
    private StarsWebConfig _starsWebConfig;
    private java.util.Vector _addressingGroupList;
    private ChanceOfControl _chanceOfControl;
    private int programOrder = 0;

    public StarsEnrLMProgram() {
        _addressingGroupList = new Vector();
    }

    public void addAddressingGroup(AddressingGroup vAddressingGroup)
        throws java.lang.IndexOutOfBoundsException
    {
        _addressingGroupList.addElement(vAddressingGroup);
    } 

    public void addAddressingGroup(int index, AddressingGroup vAddressingGroup)
        throws java.lang.IndexOutOfBoundsException
    {
        _addressingGroupList.insertElementAt(vAddressingGroup, index);
    }

    public void deleteDeviceID()
    {
        this._has_deviceID= false;
    } 

    public void deleteProgramID()
    {
        this._has_programID= false;
    }

    public java.util.Enumeration enumerateAddressingGroup()
    {
        return _addressingGroupList.elements();
    } 

    public AddressingGroup getAddressingGroup(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _addressingGroupList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (AddressingGroup) _addressingGroupList.elementAt(index);
    }

    public AddressingGroup[] getAddressingGroup()
    {
        int size = _addressingGroupList.size();
        AddressingGroup[] mArray = new AddressingGroup[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (AddressingGroup) _addressingGroupList.elementAt(index);
        }
        return mArray;
    } 

    public int getAddressingGroupCount()
    {
        return _addressingGroupList.size();
    }

    public ChanceOfControl getChanceOfControl()
    {
        return this._chanceOfControl;
    } 

    public int getDeviceID()
    {
        return this._deviceID;
    }

    public int getProgramID()
    {
        return this._programID;
    } 

    public StarsWebConfig getStarsWebConfig()
    {
        return this._starsWebConfig;
    }

    public java.lang.String getYukonName()
    {
        return this._yukonName;
    } 

    public boolean hasDeviceID()
    {
        return this._has_deviceID;
    } 

    public boolean hasProgramID()
    {
        return this._has_programID;
    } 

    public AddressingGroup removeAddressingGroup(int index)
    {
        java.lang.Object obj = _addressingGroupList.elementAt(index);
        _addressingGroupList.removeElementAt(index);
        return (AddressingGroup) obj;
    } 

    public void removeAllAddressingGroup()
    {
        _addressingGroupList.removeAllElements();
    } 

    public void setAddressingGroup(int index, AddressingGroup vAddressingGroup)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _addressingGroupList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _addressingGroupList.setElementAt(vAddressingGroup, index);
    } 
    
    public void setAddressingGroup(AddressingGroup[] addressingGroupArray)
    {
        //-- copy array
        _addressingGroupList.removeAllElements();
        for (int i = 0; i < addressingGroupArray.length; i++) {
            _addressingGroupList.addElement(addressingGroupArray[i]);
        }
    }

    public void setChanceOfControl(ChanceOfControl chanceOfControl)
    {
        this._chanceOfControl = chanceOfControl;
    } 

    public void setDeviceID(int deviceID)
    {
        this._deviceID = deviceID;
        this._has_deviceID = true;
    }
    
    public void setProgramID(int programID)
    {
        this._programID = programID;
        this._has_programID = true;
    }

    public void setStarsWebConfig(StarsWebConfig starsWebConfig)
    {
        this._starsWebConfig = starsWebConfig;
    } 

    public void setYukonName(java.lang.String yukonName)
    {
        this._yukonName = yukonName;
    }
    
    public int getProgramOrder() {
        return programOrder;
    }

    public void setProgramOrder(int programOrder) {
        this.programOrder = programOrder;
    }    
}
