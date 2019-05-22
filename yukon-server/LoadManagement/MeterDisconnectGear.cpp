#include "precompiled.h"

#include "GroupControlInterface.h"
#include "MeterDisconnectGear.h"
#include "MeterDisconnectControlInterface.h"


namespace Cti::LoadManagement {

MeterDisconnectGear::MeterDisconnectGear( Cti::RowReader & rdr )
    :   CtiLMProgramDirectGear( rdr )
{
    // empty
}

CtiLMProgramDirectGear * MeterDisconnectGear::replicate() const
{
    return new MeterDisconnectGear( *this );
}

bool MeterDisconnectGear::attemptControl( CtiLMGroupPtr  currentLMGroup,
                                          long           controlSeconds,
                                          double       & expectedLoadReduced )
{
    if ( MeterDisconnectControlInterfacePtr mdGroup = boost::dynamic_pointer_cast<MeterDisconnectControlInterface>( currentLMGroup ) )
    {
//        expectedLoadReduced += calculateLoadReduction( currentLMGroup->getKWCapacity() );

        return mdGroup->sendControl( controlSeconds );
    }

    CTILOG_WARN( dout, "Group does not implement the MeterDisconnect control interface: " << currentLMGroup->getPAOName() );

    return false;
}

bool MeterDisconnectGear::stopControl( CtiLMGroupPtr currentLMGroup )
{
    if ( GroupControlInterfacePtr mdGroup = boost::dynamic_pointer_cast<GroupControlInterface>( currentLMGroup ) )
    {
        return mdGroup->sendStopControl( true );
    }

    CTILOG_WARN( dout, "Group does not implement the basic control interface: " << currentLMGroup->getPAOName() );

    return false;
}

unsigned long MeterDisconnectGear::estimateOffTime( long controlSeconds )
{
    return controlSeconds;
}

}

