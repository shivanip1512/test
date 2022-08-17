#include "precompiled.h"

#include "GroupControlInterface.h"
#include "EcobeeControlInterface.h"
#include "ecobeeCycleGear.h"
#include "logger.h"

using namespace Cti::LoadManagement;


EcobeeCycleGear::EcobeeCycleGear( Cti::RowReader & rdr )
    :   CtiLMProgramDirectGear( rdr )
{
}


CtiLMProgramDirectGear * EcobeeCycleGear::replicate() const
{
    return new EcobeeCycleGear( *this );
}


bool EcobeeCycleGear::attemptControl( CtiLMGroupPtr currentLMGroup,
                                      long          controlSeconds,
                                      DOUBLE      & expectedLoadReduced )
{

    if ( EcobeeControlInterfacePtr controllableGroup =
            boost::dynamic_pointer_cast<EcobeeControlInterface>( currentLMGroup ) )
    {
        const double loadScalar = ( ( getPercentReduction() == 0 ) ? 100.0 : getPercentReduction() ) / 100.0;

        expectedLoadReduced += ( currentLMGroup->getKWCapacity() * loadScalar );

        const bool rampIn  = ciStringEqual( getFrontRampOption(), CtiLMProgramDirectGear::RandomizeRandomOptionType );
        const bool rampOut = ciStringEqual( getBackRampOption(),  CtiLMProgramDirectGear::RandomizeRandomOptionType );

        // Ecobee gears are repurposing the MethodObjectType column in LMProgramDirectGear for its "Mandatory" boolean
        const bool mandatory = ciStringEqual( getMethodOptionType(), "Mandatory" );

        return controllableGroup->sendCycleControl( getMethodRate(), controlSeconds, rampIn, rampOut, mandatory );
    }

    CTILOG_WARN(dout, "Group does not implement ecobee control interface: " << currentLMGroup->getPAOName());

    return false;
}


bool EcobeeCycleGear::stopControl( CtiLMGroupPtr currentLMGroup )
{
    if ( GroupControlInterfacePtr controllableGroup =
            boost::dynamic_pointer_cast<GroupControlInterface>( currentLMGroup ) )
    {
        if( ciStringEqual( getMethodStopType(), CtiLMProgramDirectGear::RestoreStopType ) )
        {
            return controllableGroup->sendStopControl( true );
        }
        // else don't send a message

        return true;
    }

    CTILOG_WARN(dout, "Group does not implement basic control interface: " << currentLMGroup->getPAOName());

    return false;
}


unsigned long EcobeeCycleGear::estimateOffTime( long controlSeconds )
{
    return controlSeconds * ( getMethodRate() / 100.0 );
}

