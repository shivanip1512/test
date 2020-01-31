#include "precompiled.h"

#include "GroupControlInterface.h"
#include "HoneywellControlInterface.h"
#include "HoneywellSetpointGear.h"
#include "logger.h"

#include <algorithm>

using namespace Cti::LoadManagement;


HoneywellSetpointGear::HoneywellSetpointGear( Cti::RowReader & rdr )
    :   CtiLMProgramThermostatGear( rdr )
{
}


CtiLMProgramDirectGear * HoneywellSetpointGear::replicate() const
{
    return new HoneywellSetpointGear( *this );
}


bool HoneywellSetpointGear::attemptControl( CtiLMGroupPtr currentLMGroup,
                                            long          controlSeconds,
                                            DOUBLE      & expectedLoadReduced )
{

    if ( auto controllableGroup = dynamic_cast<HoneywellControlInterface*>( currentLMGroup.get() ) )
    {
        expectedLoadReduced += calculateLoadReduction( currentLMGroup->getKWCapacity() );

        return controllableGroup->sendSetpointControl( getProgramPAOId(),
                                                       isHeatControl(),
                                                       isMandatory(),
                                                       getSetpointOffset(),
                                                       getSetpointPreOffset(),
                                                       controlSeconds );
        return true;
    }

    CTILOG_WARN(dout, "Group does not implement the Honeywell control interface: " << currentLMGroup->getPAOName());

    return false;
}


bool HoneywellSetpointGear::stopControl( CtiLMGroupPtr currentLMGroup )
{
    if ( auto controllableGroup = dynamic_cast<GroupControlInterface*>( currentLMGroup.get() ) )
    {
        if ( ciStringEqual( getMethodStopType(), CtiLMProgramDirectGear::RestoreStopType ) )
        {
            return controllableGroup->sendStopControl( true );
        }
        // else don't send a message

        return true;
    }

    CTILOG_WARN(dout, "Group does not implement the basic control interface: " << currentLMGroup->getPAOName());

    return false;
}


unsigned long HoneywellSetpointGear::estimateOffTime( long controlSeconds )
{
    return controlSeconds * ( getMethodRate() / 100.0 );
}

// Custom Gear settings

bool HoneywellSetpointGear::isHeatControl() const
{
    return ciStringEqual( getSettings(), "--H-" );
}

bool HoneywellSetpointGear::isMandatory() const
{
    return ciStringEqual( getMethodOptionType(), "Mandatory" );
}

namespace
{
    int validateOffset( const std::string & name, long minimum, long maximum, long value )
    {
        if ( value > maximum || value < minimum )
        {
            CTILOG_WARN( dout, name << " Setpoint Offset of: " << value << " is outside the valid range of [" 
                        << minimum << ", " << maximum << "]. Clamping to nearest value." );
        }

        return std::clamp( value, minimum, maximum );
    }
}

int HoneywellSetpointGear::getSetpointOffset() const
{
    return validateOffset( isHeatControl() ? "HEAT" : "COOL",
                           -10L, 10L,
                           getProfileSettings().maxValue );
}

int HoneywellSetpointGear::getSetpointPreOffset() const
{
    return validateOffset( isHeatControl() ? "pre-HEAT" : "pre-COOL",
                           -10L, 10L,
                           getProfileSettings().valueB );
}

double HoneywellSetpointGear::calculateLoadReduction( double groupCapacity ) const
{
    const double loadScalar = ( getPercentReduction() == 0 ) ? 1.0 : getPercentReduction() / 100.0;

    return loadScalar * groupCapacity;
}

