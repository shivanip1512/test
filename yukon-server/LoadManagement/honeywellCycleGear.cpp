#include "precompiled.h"

#include "GroupControlInterface.h"
#include "HoneywellControlInterface.h"
#include "honeywellCycleGear.h"
#include "logger.h"

using namespace Cti::LoadManagement;


HoneywellCycleGear::HoneywellCycleGear( Cti::RowReader & rdr )
    : CtiLMProgramDirectGear( rdr )
{
}

CtiLMProgramDirectGear * HoneywellCycleGear::replicate() const
{
    return new HoneywellCycleGear( *this );
}

bool HoneywellCycleGear::attemptControl( CtiLMGroupPtr currentLMGroup,
                                         long          controlSeconds,
                                         DOUBLE      & expectedLoadReduced )
{

    if ( HoneywellControlInterfacePtr controllableGroup =
         boost::dynamic_pointer_cast<HoneywellControlInterface>( currentLMGroup ) )
    {
        const double loadScalar = ( ( getPercentReduction() == 0 ) ? 100.0 : getPercentReduction() ) / 100.0;

        expectedLoadReduced +=  (currentLMGroup->getKWCapacity() * loadScalar );

        const bool rampInOut = ciStringEqual( getFrontRampOption(), CtiLMProgramDirectGear::RandomizeRandomOptionType );

        return controllableGroup->sendCycleControl( getProgramPAOId(), getMethodRate(), controlSeconds, rampInOut );
    }

    CTILOG_WARN( dout, "Group does not implement honeywell control interface: " << currentLMGroup->getPAOName() );

    return false;
}

bool HoneywellCycleGear::stopControl( CtiLMGroupPtr currentLMGroup )
{
    if ( GroupControlInterfacePtr controllableGroup =
         boost::dynamic_pointer_cast<GroupControlInterface>( currentLMGroup ) )
    {
        if ( ciStringEqual( getMethodStopType(), CtiLMProgramDirectGear::RestoreStopType ) )
        {
            return controllableGroup->sendStopControl( true );
        }
        // else don't send a message

        return true;
    }

    CTILOG_WARN( dout, "Group does not implement basic control interface: " << currentLMGroup->getPAOName() );

    return false;
}

unsigned long HoneywellCycleGear::estimateOffTime( long controlSeconds )
{
    return controlSeconds * ( getMethodRate() / 100.0 );
}