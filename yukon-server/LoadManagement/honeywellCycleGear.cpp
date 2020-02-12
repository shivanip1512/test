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
    if ( auto controllableGroup = dynamic_cast<HoneywellControlInterface*>( currentLMGroup.get() ) )
    {
        expectedLoadReduced += calculateLoadReduction( currentLMGroup->getKWCapacity() );

        return controllableGroup->sendCycleControl( getProgramPAOId(),
                                                    getDutyCycle(),
                                                    controlSeconds,
                                                    isMandatory(),
                                                    isRampInOut() );
    }

    CTILOG_WARN( dout, "Group does not implement honeywell control interface: " << currentLMGroup->getPAOName() );

    return false;
}

bool HoneywellCycleGear::stopControl( CtiLMGroupPtr currentLMGroup )
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

    CTILOG_WARN( dout, "Group does not implement basic control interface: " << currentLMGroup->getPAOName() );

    return false;
}

unsigned long HoneywellCycleGear::estimateOffTime( long controlSeconds )
{
    return controlSeconds * ( getMethodRate() / 100.0 );
}

double HoneywellCycleGear::calculateLoadReduction( double groupCapacity ) const
{
    const double loadScalar = ( getPercentReduction() == 0 ) ? 1.0 : getPercentReduction() / 100.0;

    return loadScalar * groupCapacity;
}

bool HoneywellCycleGear::isRampInOut() const
{
    return ciStringEqual( getFrontRampOption(), CtiLMProgramDirectGear::RandomizeRandomOptionType );
}

bool HoneywellCycleGear::isMandatory() const
{
    return ciStringEqual( getMethodOptionType(), "Mandatory" );
}

int HoneywellCycleGear::getDutyCycle() const
{
    return getMethodRate();
}
