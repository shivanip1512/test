#include "precompiled.h"

#include "GroupControlInterface.h"
#include "EcobeeControlInterface.h"
#include "EcobeePlusGear.h"
#include "logger.h"

#include <algorithm>

using namespace Cti::LoadManagement;

EcobeePlusGear::EcobeePlusGear( Cti::RowReader & rdr )
    : CtiLMProgramThermostatGear( rdr )
{
}

CtiLMProgramDirectGear * EcobeePlusGear::replicate() const
{
    return new EcobeePlusGear( *this );
}

bool EcobeePlusGear::attemptControl( CtiLMGroupPtr currentLMGroup,
                                     long          controlSeconds,
                                     DOUBLE      & expectedLoadReduced )
{
    if ( EcobeeControlInterfacePtr controllableGroup =
            boost::dynamic_pointer_cast<EcobeeControlInterface>( currentLMGroup ) )
    {
        const double loadScalar = ( ( getPercentReduction() == 0 ) ? 100.0 : getPercentReduction() ) / 100.0;

        expectedLoadReduced += ( currentLMGroup->getKWCapacity() * loadScalar );

        return controllableGroup->sendEcobeePlusControl( getProgramPAOId(),
                                                         controlSeconds,
                                                         isHeatControl(), 
                                                         getMethodRate() );
        return true;
    }

    CTILOG_WARN(dout, "Group does not implement ecobee control interface: " << currentLMGroup->getPAOName());

    return false;
}

bool EcobeePlusGear::stopControl( CtiLMGroupPtr currentLMGroup )
{
    if ( GroupControlInterfacePtr controllableGroup =
            boost::dynamic_pointer_cast<GroupControlInterface>( currentLMGroup ) )
    {
        if ( ciStringEqual(getMethodStopType(), CtiLMProgramDirectGear::RestoreStopType ) )
        {
            return controllableGroup->sendStopControl( true );
        }

        return true;
    }

    CTILOG_WARN(dout, "Group does not implement basic control interface: " << currentLMGroup->getPAOName());

    return false;
}

bool EcobeePlusGear::isHeatControl() const
{
    return ciStringEqual( getSettings(), "--H-" );
}

unsigned long EcobeePlusGear::estimateOffTime( long controlSeconds )
{
    return controlSeconds;
}

