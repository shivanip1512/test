#include "precompiled.h"

#include "GroupControlInterface.h"
#include "ItronCycleGear.h"
#include "ItronControlInterface.h"
#include "std_helper.h"


namespace Cti::LoadManagement {

ItronCycleGear::ItronCycleGear( Cti::RowReader & rdr )
    :   CtiLMProgramDirectGear( rdr )
{
    // empty
}


CtiLMProgramDirectGear * ItronCycleGear::replicate() const
{
    return new ItronCycleGear( *this );
}


bool ItronCycleGear::attemptControl( CtiLMGroupPtr  currentLMGroup,
                                     long           controlSeconds,
                                     double       & expectedLoadReduced )
{
    if ( ItronControlInterfacePtr itronGroup = boost::dynamic_pointer_cast<ItronControlInterface>( currentLMGroup ) )
    {
        const double loadScalar = ( ( getPercentReduction() == 0 ) ? 100.0 : getPercentReduction() ) / 100.0;

        expectedLoadReduced += ( currentLMGroup->getKWCapacity() * loadScalar );

        return itronGroup->sendCycleControl( controlSeconds );
    }

    CTILOG_WARN( dout, "Group does not implement the Itron control interface: " << currentLMGroup->getPAOName() );

    return false;
}


bool ItronCycleGear::stopControl( CtiLMGroupPtr currentLMGroup )
{
    if ( GroupControlInterfacePtr itronGroup = boost::dynamic_pointer_cast<GroupControlInterface>( currentLMGroup ) )
    {
        return itronGroup->sendStopControl( true );
    }

    CTILOG_WARN( dout, "Group does not implement the basic control interface: " << currentLMGroup->getPAOName() );

    return false;
}


unsigned long ItronCycleGear::estimateOffTime( long controlSeconds )
{
    return controlSeconds;
}

}

