package com.cannontech.cbc.capbankeditor;

/**
 * Insert the type's description here.
 * Creation date: (12/14/00 4:41:31 PM)
 * @author: 
 */
import com.cannontech.cbc.data.CapBankDevice;

public class ObservedCapBankChanged 
{
	CapBankDevice capBankDevice = null;
/**
 * ObservedCapBankChanged constructor comment.
 */
public ObservedCapBankChanged(com.cannontech.cbc.data.CapBankDevice newCapBankDevice) 
{
	capBankDevice = newCapBankDevice;
}
/**
 * Insert the method's description here.
 * Creation date: (12/14/00 4:43:07 PM)
 * @return com.cannontech.cbc.CapBankDevice
 */
public com.cannontech.cbc.data.CapBankDevice getCapBankDevice() {
	return capBankDevice;
}
/**
 * Insert the method's description here.
 * Creation date: (12/14/00 4:43:07 PM)
 * @param newCapBankDevice com.cannontech.cbc.CapBankDevice
 */
public void setCapBankDevice(com.cannontech.cbc.data.CapBankDevice newCapBankDevice) {
	capBankDevice = newCapBankDevice;
}
}
