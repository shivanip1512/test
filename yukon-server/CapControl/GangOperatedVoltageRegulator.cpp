

#include "precompiled.h"
#include "logger.h"
#include "GangOperatedVoltageRegulator.h"
#include "ccutil.h"
#include "capcontroller.h"
#include "ccmessage.h"

#include "CountdownKeepAlivePolicy.h"
#include "LoadOnlyScanPolicy.h"


namespace Cti           {
namespace CapControl    {

DEFINE_COLLECTABLE( GangOperatedVoltageRegulator, CTIVOLTAGEREGULATOR_ID )


GangOperatedVoltageRegulator::GangOperatedVoltageRegulator()
    : VoltageRegulator()
{

    _keepAlivePolicy = std::make_unique<CountdownKeepAlivePolicy>();
    _scanPolicy = std::make_unique<LoadOnlyScanPolicy>();

    // empty...
}


GangOperatedVoltageRegulator::GangOperatedVoltageRegulator(Cti::RowReader & rdr)
    : VoltageRegulator(rdr)
{

    _keepAlivePolicy = std::make_unique<CountdownKeepAlivePolicy>();
    _scanPolicy = std::make_unique<LoadOnlyScanPolicy>();

    // empty...
}


GangOperatedVoltageRegulator::GangOperatedVoltageRegulator(const GangOperatedVoltageRegulator & toCopy)
    : VoltageRegulator()
{
    operator=(toCopy);
}


GangOperatedVoltageRegulator & GangOperatedVoltageRegulator::operator=(const GangOperatedVoltageRegulator & rhs)
{
    if ( this != &rhs )
    {
        VoltageRegulator::operator=(rhs);
    }

    return *this;
}


void GangOperatedVoltageRegulator::loadAttributes(AttributeService * service)
{
    const std::vector<PointAttribute> attributes
    {
        PointAttribute::VoltageY//,  // <-- need him so     loadPointAttributes()   is called.......FIX ME
    };

    for each ( const PointAttribute attribute in attributes )
    {
        loadPointAttributes(service, attribute);
    }
}


void GangOperatedVoltageRegulator::updateFlags(const unsigned tapDelay)
{
    bool recentOperation = ( ( _lastControlOperationTime + 30 ) > CtiTime() );

    if (_recentTapOperation != recentOperation)
    {
        _recentTapOperation = recentOperation;
        setUpdated(true);
    }

    OperatingMode currentMode = getOperatingMode();

    if (_lastOperatingMode != currentMode)
    {
        _lastOperatingMode = currentMode;
        setUpdated(true);
    }
}


VoltageRegulator * GangOperatedVoltageRegulator::replicate() const
{
    return new GangOperatedVoltageRegulator(*this);
}


const VoltageRegulator::Type GangOperatedVoltageRegulator::getType() const
{
    return VoltageRegulator::LoadTapChangerType;
}


}
}

