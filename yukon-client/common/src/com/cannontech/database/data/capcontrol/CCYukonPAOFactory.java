package com.cannontech.database.data.capcontrol;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.data.pao.CapControlTypes;

public final class CCYukonPAOFactory {
/**
 * This method was created in VisualAge.
 */
public final static CapControlYukonPAOBase createCapControlPAO( int type ) 
{
	CapControlYukonPAOBase returnCCbase = null;

	switch( type )
	{
		case CapControlTypes.CAP_CONTROL_SUBBUS:
			returnCCbase = new CapControlSubBus();
			returnCCbase.setCapControlType( CapControlTypes.STRING_CAPCONTROL_SUBBUS );
			break;

		case CapControlTypes.CAP_CONTROL_FEEDER:
			returnCCbase = new CapControlFeeder();
			returnCCbase.setCapControlType( CapControlTypes.STRING_CAPCONTROL_FEEDER );			
			break;

		default:
			throw new Error("Unable to create CapControl PAObject type. (type = " + type );
	}

	//Set a couple reasonable defaults
	returnCCbase.setPAOCategory( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_CAPCONTROL );
	returnCCbase.setPAOClass( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_CAPCONTROL );
	returnCCbase.setDisableFlag( new Character('N') );

	
	return returnCCbase;
}
}
