#include "precompiled.h"

#include "GroupControlInterface.h"
#include "EatonCloudCycleGear.h"
#include "EatonCloudControlInterface.h"
#include "std_helper.h"


namespace Cti::LoadManagement {

EatonCloudCycleGear::EatonCloudCycleGear( Cti::RowReader & rdr )
    :   CtiLMProgramDirectGear( rdr ),
        _cyclingOption( SmartGearCyclingOption::Unsupported )
{
    const std::string dbKey = rdr[ "CycleOption" ].as<std::string>();

    _cyclingOption = resolveCyclingOption( dbKey );

    if ( _cyclingOption == SmartGearCyclingOption::Unsupported )
    {
        CTILOG_ERROR( dout, "Gear: '" << getGearName() << "' has an unknown cycle option: '" << dbKey << "'. No cycling controls will be issued." );
    }
}

CtiLMProgramDirectGear * EatonCloudCycleGear::replicate() const
{
    return new EatonCloudCycleGear( *this );
}

bool EatonCloudCycleGear::attemptControl( CtiLMGroupPtr  currentLMGroup,
                                          long           controlSeconds,
                                          double       & expectedLoadReduced )
{
    if ( auto eatonCloudGroup = boost::dynamic_pointer_cast<EatonCloudControlInterface>( currentLMGroup ) )
    {
        expectedLoadReduced += calculateLoadReduction( currentLMGroup->getKWCapacity() );

        const EatonCloudControlInterface::CycleControlParameters params
        {
            controlSeconds,
            isRampIn(),
            isRampOut(),
            getCyclingOption(),
            getDutyCyclePercentage(),
            getDutyCyclePeriod(),
            getCriticality()
        };

        return eatonCloudGroup->sendCycleControl( params );
    }

    CTILOG_WARN( dout, "Group does not implement the EatonCloud control interface: " << currentLMGroup->getPAOName() );

    return false;
}

bool EatonCloudCycleGear::stopControl( CtiLMGroupPtr currentLMGroup )
{
    const bool restoreStop = ciStringEqual( getMethodStopType(), CtiLMProgramDirectGear::RestoreStopType );

    if ( auto eatonCloudGroup = boost::dynamic_pointer_cast<GroupControlInterface>( currentLMGroup ) )
    {
        return eatonCloudGroup->sendStopControl( restoreStop );
    }

    CTILOG_WARN( dout, "Group does not implement the basic control interface: " << currentLMGroup->getPAOName() );

    return false;
}

unsigned long EatonCloudCycleGear::estimateOffTime( long controlSeconds )
{
    return controlSeconds;
}

// Custom Gear settings

bool EatonCloudCycleGear::isRampIn() const
{
    return ciStringEqual( getFrontRampOption(), RandomizeRandomOptionType );
}

bool EatonCloudCycleGear::isRampOut() const
{
    return ciStringEqual( getBackRampOption(), RandomizeRandomOptionType );
}

long EatonCloudCycleGear::getDutyCyclePercentage() const
{
    return getMethodRate();
}

long EatonCloudCycleGear::getDutyCyclePeriod() const
{
    return getMethodPeriod();   // seconds
}

long EatonCloudCycleGear::getCriticality() const
{
    return std::stol( getMethodOptionType() );
}

long EatonCloudCycleGear::getGroupCapacityReduction() const
{
    return getPercentReduction();
}

double EatonCloudCycleGear::calculateLoadReduction( double groupCapacity ) const
{
    const double loadScalar = ( getPercentReduction() == 0 ) ? 1.0 : getPercentReduction() / 100.0;

    return loadScalar * groupCapacity;
}

SmartGearCyclingOption EatonCloudCycleGear::getCyclingOption() const
{
    return _cyclingOption;
}

}

