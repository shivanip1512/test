#include "precompiled.h"

#include "GroupControlInterface.h"
#include "EcobeeControlInterface.h"
#include "EcobeeSetpointGear.h"
#include "logger.h"

#include <algorithm>

using namespace Cti::LoadManagement;


EcobeeSetpointGear::EcobeeSetpointGear( Cti::RowReader & rdr )
    :   CtiLMProgramThermostatGear( rdr )
{
}


CtiLMProgramDirectGear * EcobeeSetpointGear::replicate() const
{
    return new EcobeeSetpointGear( *this );
}


bool EcobeeSetpointGear::attemptControl( CtiLMGroupPtr currentLMGroup,
                                         long          controlSeconds,
                                         DOUBLE      & expectedLoadReduced )
{

    if ( EcobeeControlInterfacePtr controllableGroup =
            boost::dynamic_pointer_cast<EcobeeControlInterface>( currentLMGroup ) )
    {
        const double loadScalar = ( ( getPercentReduction() == 0 ) ? 100.0 : getPercentReduction() ) / 100.0;

        expectedLoadReduced += ( currentLMGroup->getKWCapacity() * loadScalar );

        return controllableGroup->sendSetpointControl( controlSeconds,
                                                       isHeatControl(),
                                                       isMandatory(),
                                                       getSetpointOffset() );
        return true;
    }

    CTILOG_WARN(dout, "Group does not implement ecobee control interface: " << currentLMGroup->getPAOName());

    return false;
}


bool EcobeeSetpointGear::stopControl( CtiLMGroupPtr currentLMGroup )
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


unsigned long EcobeeSetpointGear::estimateOffTime( long controlSeconds )
{
    return controlSeconds * ( getMethodRate() / 100.0 );
}

// Custom Gear settings

bool EcobeeSetpointGear::isHeatControl() const
{
    return ciStringEqual( getSettings(), "--H-" );
}

bool EcobeeSetpointGear::isMandatory() const
{
    return ciStringEqual( getMethodOptionType(), "Mandatory" );
}

int EcobeeSetpointGear::getSetpointOffset() const
{
    constexpr auto  Minimum = -10L,
                    Maximum =  10L;

    if ( getProfileSettings().maxValue > Maximum || getProfileSettings().maxValue < Minimum )
    {
        CTILOG_WARN( dout, "Setpoint Offset of: " << getProfileSettings().maxValue << " is outside the valid range of [" 
                    << Minimum << ", " << Maximum << "]. Clamping to nearest value." );
    }

    return std::clamp( getProfileSettings().maxValue, Minimum, Maximum );
}

