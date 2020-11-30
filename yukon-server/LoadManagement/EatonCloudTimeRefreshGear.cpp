#include "precompiled.h"

#include "GroupControlInterface.h"
#include "EatonCloudTimeRefreshGear.h"
#include "EatonCloudControlInterface.h"
#include "std_helper.h"


namespace Cti::LoadManagement {

EatonCloudTimeRefreshGear::EatonCloudTimeRefreshGear( Cti::RowReader & rdr )
    :   CtiLMProgramDirectGear( rdr )
{
    // empty
}

CtiLMProgramDirectGear * EatonCloudTimeRefreshGear::replicate() const
{
    return new EatonCloudTimeRefreshGear( *this );
}

bool EatonCloudTimeRefreshGear::attemptControl( CtiLMGroupPtr  currentLMGroup,
                                                long           controlSeconds,
                                                double       & expectedLoadReduced )
{
    if ( auto eatonCloudGroup = boost::dynamic_pointer_cast<EatonCloudControlInterface>( currentLMGroup ) )
    {
        expectedLoadReduced += calculateLoadReduction( currentLMGroup->getKWCapacity() );

        return eatonCloudGroup->sendTimeRefreshControl( controlSeconds );
    }

    CTILOG_WARN( dout, "Group does not implement the EatonCloud control interface: " << currentLMGroup->getPAOName() );

    return false;
}

bool EatonCloudTimeRefreshGear::stopControl( CtiLMGroupPtr currentLMGroup )
{
    if ( auto eatonCloudGroup = boost::dynamic_pointer_cast<GroupControlInterface>( currentLMGroup ) )
    {
        return eatonCloudGroup->sendStopControl( true );
    }

    CTILOG_WARN( dout, "Group does not implement the basic control interface: " << currentLMGroup->getPAOName() );

    return false;
}

unsigned long EatonCloudTimeRefreshGear::estimateOffTime( long controlSeconds )
{
    return controlSeconds;
}

double EatonCloudTimeRefreshGear::calculateLoadReduction( double groupCapacity ) const
{
    const double loadScalar = ( getPercentReduction() == 0 ) ? 1.0 : getPercentReduction() / 100.0;

    return loadScalar * groupCapacity;
}

}

