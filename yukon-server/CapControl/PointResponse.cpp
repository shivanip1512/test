
#include "precompiled.h"
#include "PointResponse.h"
#include "logger.h"
#include "ccid.h"
#include "ccutil.h"

#include <cmath>

extern unsigned long _CC_DEBUG;

namespace Cti {
namespace CapControl {

PointResponse::PointResponse(long pointId, long deviceId, double preOpValue, double delta, bool staticDelta, long busId,
                             const std::string & pointName, const std::string & deviceName)
    :   _pointId( pointId ),
        _deviceId( deviceId ),
        _preOpValue( preOpValue ),
        _delta( delta ),
        _staticDelta( staticDelta ),
        _busId( busId ),
        _isDirty( false ),
        _pointName(pointName),
        _deviceName(deviceName)
{
}

bool PointResponse::operator != (const PointResponse& right) const
{
    return ( _pointId != right._pointId || _deviceId != right._deviceId);
}

long PointResponse::getPointId() const
{
    return _pointId;
}

long PointResponse::getDeviceId() const
{
    return _deviceId;
}

long PointResponse::getSubBusId() const
{
    return _busId;
}

double PointResponse::getPreOpValue() const
{
    return _preOpValue;
}

double PointResponse::getDelta() const
{
    return _delta;
}

void PointResponse::setDelta(double delta)
{
    _isDirty |= setVariableIfDifferent( _delta, delta );
}

bool PointResponse::getStaticDelta() const
{
    return _staticDelta;
}

void PointResponse::setStaticDelta(bool staticDelta)
{
    _isDirty |= setVariableIfDifferent( _staticDelta, staticDelta );
}

void PointResponse::updateDelta(long nInAvg, double value, const std::string & identifier, double maxDelta)
{
    if ( ! _staticDelta)
    {
        if ( 110.0 < _preOpValue && _preOpValue < 130.0  )
        {
            CTILOG_DEBUG( dout, identifier << " to Point ID: " << _pointId << " -- preOpValue: " << _preOpValue );
        }
        else
        {
            CTILOG_WARN( dout, identifier << " to Point ID: " << _pointId << " -- preOpValue: " << _preOpValue
                                << " -- outside valid voltage limits. Aborting Delta voltage update." );
            return;
        }

        if ( 110.0 < value && value < 130.0  )
        {
            CTILOG_DEBUG( dout, identifier << " to Point ID: " << _pointId << " -- currentValue: " << value );
        }
        else
        {
            CTILOG_WARN( dout, identifier << " to Point ID: " << _pointId << " -- currentValue: " << value
                                << " -- outside valid voltage limits. Aborting Delta voltage update." );
            return;
        }

        double new_nInAvg = nInAvg != 0 ? nInAvg:1;
        double fabsy = std::fabs(_preOpValue - value);

        if ( fabsy <= maxDelta )
        {
            double delta = ((_delta*(new_nInAvg - 1.0 )) + fabsy) / new_nInAvg;

            CTILOG_INFO( dout, identifier << " to Point ID: " << _pointId << " -- delta voltage updated from: "
                                << _delta << " to " << delta << " with N: " << new_nInAvg );

            setDelta( delta );
        }
        else
        {
            CTILOG_WARN( dout, identifier << " to Point ID: " << _pointId << " -- instantaneous delta voltage: " << fabsy
                                <<  " exceeds maximum allowable: " << maxDelta << " -- Aborting Delta voltage update." );
        }
    }
}

void PointResponse::updatePreOpValue(double preOpValue)
{
    _isDirty |= setVariableIfDifferent( _preOpValue, preOpValue );
}

bool PointResponse::isDirty() const
{
    return _isDirty;
}

void PointResponse::setDirty( const bool flag )
{
    _isDirty = flag;
}

}
}
