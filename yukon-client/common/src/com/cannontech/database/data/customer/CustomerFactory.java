package com.cannontech.database.data.customer;

/**
 * Insert the type's description here.
 * Creation date: (10/25/2001 12:31:55 PM)
 * @author: 
 */
import com.cannontech.database.data.pao.CustomerTypes;

public final class CustomerFactory {
/**
 * CustomerFactory constructor comment.
 */
public CustomerFactory() {
	super();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.data.device.DeviceBase
 * @param custType int
 */
public final static CustomerBase createCustomer(int custType) 
{
	CustomerBase returnCustomer = null;

	switch( custType )
	{
		case CustomerTypes.CI_CUSTOMER:
			returnCustomer = new CICustomerBase();
			returnCustomer.setCustomerType( CustomerTypes.STRING_CI_CUSTOMER );			
			break;

	}

	//Set a couple reasonable defaults
	returnCustomer.setPAOCategory( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_CUSTOMER );
	returnCustomer.setPAOClass( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_CUSTOMER );
	returnCustomer.setDisableFlag( new Character('N') );
	
		
	return returnCustomer;
}
}
