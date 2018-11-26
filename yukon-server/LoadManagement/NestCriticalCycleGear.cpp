#include "precompiled.h"

#include "GroupControlInterface.h"
#include "NestCriticalCycleGear.h"
#include "NestControlInterface.h"

using namespace Cti::LoadManagement;



NestCriticalCycleGear::NestCriticalCycleGear( Cti::RowReader & rdr )
    :   CtiLMProgramDirectGear( rdr )
{
    // empty
}


CtiLMProgramDirectGear * NestCriticalCycleGear::replicate() const
{
    return new NestCriticalCycleGear( *this );
}


bool NestCriticalCycleGear::attemptControl( CtiLMGroupPtr  currentLMGroup,
                                            long           controlSeconds,
                                            DOUBLE       & expectedLoadReduced )
{
    if ( NestControlInterfacePtr nestGroup = boost::dynamic_pointer_cast<NestControlInterface>( currentLMGroup ) )
    {
        const double loadScalar = ( ( getPercentReduction() == 0 ) ? 100.0 : getPercentReduction() ) / 100.0;

        expectedLoadReduced += ( currentLMGroup->getKWCapacity() * loadScalar );
 
        return nestGroup->sendCycleControl( controlSeconds );
    }

    CTILOG_WARN( dout, "Group does not implement the nest control interface: " << currentLMGroup->getPAOName() );

    return false;
}


bool NestCriticalCycleGear::stopControl( CtiLMGroupPtr currentLMGroup )
{
    if ( GroupControlInterfacePtr nestGroup = boost::dynamic_pointer_cast<GroupControlInterface>( currentLMGroup ) )
    {
        return nestGroup->sendStopControl( true );
    }

    CTILOG_WARN( dout, "Group does not implement the basic control interface: " << currentLMGroup->getPAOName() );

    return false;
}


unsigned long NestCriticalCycleGear::estimateOffTime( long controlSeconds )
{
    return controlSeconds;
}

