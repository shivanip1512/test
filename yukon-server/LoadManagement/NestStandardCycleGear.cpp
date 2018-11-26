#include "precompiled.h"

#include "GroupControlInterface.h"
#include "NestStandardCycleGear.h"
#include "NestControlInterface.h"
#include "std_helper.h"

using namespace Cti::LoadManagement;



NestStandardCycleGear::NestStandardCycleGear( Cti::RowReader & rdr )
    :   CtiLMProgramDirectGear( rdr )
{
    // empty
}


CtiLMProgramDirectGear * NestStandardCycleGear::replicate() const
{
    return new NestStandardCycleGear( *this );
}


bool NestStandardCycleGear::attemptControl( CtiLMGroupPtr  currentLMGroup,
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


bool NestStandardCycleGear::stopControl( CtiLMGroupPtr currentLMGroup )
{
    if ( GroupControlInterfacePtr nestGroup = boost::dynamic_pointer_cast<GroupControlInterface>( currentLMGroup ) )
    {
        return nestGroup->sendStopControl( true );
    }

    CTILOG_WARN( dout, "Group does not implement the basic control interface: " << currentLMGroup->getPAOName() );

    return false;
}


unsigned long NestStandardCycleGear::estimateOffTime( long controlSeconds )
{
    return controlSeconds;
}

