package com.cannontech.database.data.capcontrol;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.capcontrol.CapControlStrategy;

public final class CCYukonPAOFactory {
/**
 * This method was created in VisualAge.
 */
public final static YukonPAObject createCapControlPAO( int type ) 
{
	YukonPAObject retBase = null;

	switch( type )
	{
	    case CapControlTypes.CAP_CONTROL_SPECIAL_AREA:
	    	retBase = new CapControlSpecialArea();
	    	((CapControlYukonPAOBase)retBase).setCapControlType( CapControlTypes.STRING_CAPCONTROL_SPECIAL_AREA );
	    	break;
        case CapControlTypes.CAP_CONTROL_AREA:
            retBase = new CapControlArea();
            ((CapControlYukonPAOBase)retBase).setCapControlType( CapControlTypes.STRING_CAPCONTROL_AREA );
            break;
        
        case CapControlTypes.CAP_CONTROL_SUBSTATION:
			retBase = new CapControlSubstation();
			((CapControlYukonPAOBase)retBase).setCapControlType( CapControlTypes.STRING_CAPCONTROL_SUBSTATION );
			break;        
        case CapControlTypes.CAP_CONTROL_SUBBUS:
			retBase = new CapControlSubBus();
			((CapControlYukonPAOBase)retBase).setCapControlType( CapControlTypes.STRING_CAPCONTROL_SUBBUS );
			break;

		case CapControlTypes.CAP_CONTROL_FEEDER:
			retBase = new CapControlFeeder();
			((CapControlYukonPAOBase)retBase).setCapControlType( CapControlTypes.STRING_CAPCONTROL_FEEDER );			
			break;

		default:
			retBase = DeviceFactory.createDevice( type );
	}

	
	return retBase;
}

/**
 * Creates a default DB CapControlStrategy
 */
public final static CapControlStrategy createCapControlStrategy() 
{
	CapControlStrategy retCCStrategy = new CapControlStrategy();
	
	return retCCStrategy;
}

}