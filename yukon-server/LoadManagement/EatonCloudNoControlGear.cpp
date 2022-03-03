#include "precompiled.h"

#include "GroupControlInterface.h"
#include "EatonCloudNoControlGear.h"
#include "EatonCloudControlInterface.h"
#include "std_helper.h"


namespace Cti::LoadManagement {

EatonCloudNoControlGear::EatonCloudNoControlGear( Cti::RowReader & rdr )
    :   CtiLMProgramDirectGear( rdr )
{
    // empty
}

CtiLMProgramDirectGear * EatonCloudNoControlGear::replicate() const
{
    return new EatonCloudNoControlGear( *this );
}

bool EatonCloudNoControlGear::attemptControl( CtiLMGroupPtr  currentLMGroup,
                                              long           controlSeconds,
                                              double       & expectedLoadReduced )
{
    if ( auto eatonCloudGroup = boost::dynamic_pointer_cast<EatonCloudControlInterface>( currentLMGroup ) )
    {
        expectedLoadReduced += calculateLoadReduction( currentLMGroup->getKWCapacity() );

        const bool doRestore = ciStringEqual( getMethodStopType(), CtiLMProgramDirectGear::RestoreStopType );

        return eatonCloudGroup->sendNoControl( doRestore );
    }

    CTILOG_WARN( dout, "Group does not implement the EatonCloud control interface: " << currentLMGroup->getPAOName() );

    return false;
}

bool EatonCloudNoControlGear::stopControl( CtiLMGroupPtr currentLMGroup )
{
    //const bool restoreStop = ciStringEqual( getMethodStopType(), CtiLMProgramDirectGear::RestoreStopType );

    if ( auto eatonCloudGroup = boost::dynamic_pointer_cast<GroupControlInterface>( currentLMGroup ) )
    {
        //return eatonCloudGroup->sendStopControl( restoreStop );
        return false;
    }

    CTILOG_WARN( dout, "Group does not implement the basic control interface: " << currentLMGroup->getPAOName() );

    return false;
}

unsigned long EatonCloudNoControlGear::estimateOffTime( long controlSeconds )
{
    return controlSeconds;
}

double EatonCloudNoControlGear::calculateLoadReduction( double groupCapacity ) const
{
    return 0;
}

}

