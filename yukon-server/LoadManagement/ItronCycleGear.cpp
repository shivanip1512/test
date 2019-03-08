#include "precompiled.h"

#include "GroupControlInterface.h"
#include "ItronCycleGear.h"
#include "ItronControlInterface.h"
#include "std_helper.h"


namespace Cti::LoadManagement {

namespace
{
    static const std::map<std::string, long>    cycleOptionResolver
    {
        { "STANDARD",       0 },
        { "TRUE_CYCLE",     1 },
        { "SMART_CYCLE",    2 }
    };
}

ItronCycleGear::ItronCycleGear( Cti::RowReader & rdr )
    :   CtiLMProgramDirectGear( rdr ),
        _cyclingOption( -1 )
{
    const std::string dbKey = rdr[ "CycleOption" ].as<std::string>();
    
    if ( auto result = Cti::mapFind( cycleOptionResolver, dbKey ) )
    {
        _cyclingOption = *result;
    }
    else
    {
        CTILOG_ERROR( dout, "Gear: '" << getGearName() << "' has an unknown cycle option: '" << dbKey << "'" );
    }
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
        expectedLoadReduced += calculateLoadReduction( currentLMGroup->getKWCapacity() );

        return itronGroup->sendCycleControl( controlSeconds,
                                             isRampIn(),
                                             isRampOut(),
                                             getCyclingOption(),
                                             getDutyCyclePercentage(),
                                             getDutyCyclePeriod(),
                                             getCriticality() );
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

// Custom Gear settings

bool ItronCycleGear::isRampIn() const
{
    return ciStringEqual( getFrontRampOption(), RandomizeRandomOptionType );
}

bool ItronCycleGear::isRampOut() const
{
    return ciStringEqual( getBackRampOption(), RandomizeRandomOptionType );
}

long ItronCycleGear::getDutyCyclePercentage() const
{
    return getMethodRate();
}

long ItronCycleGear::getDutyCyclePeriod() const
{
    return getMethodPeriod();   // seconds
}

long ItronCycleGear::getCriticality() const
{
    return std::stol( getMethodOptionType() );
}

long ItronCycleGear::getGroupCapacityReduction() const
{
    return getPercentReduction();
}

double ItronCycleGear::calculateLoadReduction( double groupCapacity ) const
{
    const double loadScalar = ( getPercentReduction() == 0 ) ? 1.0 : getPercentReduction() / 100.0;

    return loadScalar * groupCapacity;
}

long ItronCycleGear::getCyclingOption() const
{
    return _cyclingOption;
}


}

