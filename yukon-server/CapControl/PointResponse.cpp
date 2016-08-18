
#include "precompiled.h"
#include "PointResponse.h"
#include "logger.h"
#include "ccid.h"
#include "ccutil.h"

#include <cmath>

extern unsigned long _CC_DEBUG;

namespace Cti {
namespace CapControl {

PointResponse::PointResponse(long pointId, long deviceId, double preOpValue, double delta, bool staticDelta, long busId)
    :   _pointId( pointId ),
        _deviceId( deviceId ),
        _preOpValue( preOpValue ),
        _delta( delta ),
        _staticDelta( staticDelta ),
        _busId( busId ),
        _isDirty( false )
{
}


PointResponse::PointResponse(const PointResponse& pr)
{
   operator=(pr);
}


PointResponse& PointResponse::operator=(const PointResponse& right)
{
    if (this != &right)
    {
        _pointId      = right._pointId;
        _deviceId     = right._deviceId;
        _preOpValue   = right._preOpValue;
        _delta        = right._delta;
        _staticDelta  = right._staticDelta;
        _busId        = right._busId;
        _isDirty      = right._isDirty;
    }
    return *this;
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

void PointResponse::updateDelta(long nInAvg, double value)
{
    if ( ! _staticDelta)
    {
        if (_CC_DEBUG & (CC_DEBUG_MULTIVOLT | CC_DEBUG_IVVC))
        {
            CTILOG_DEBUG(dout, "Point Delta: Device ID: " << _deviceId <<" Point ID: " << _pointId << " preOpValue: " << _preOpValue << " currentValue: " << value);
        }

        double new_nInAvg = nInAvg != 0 ? nInAvg:1;
        double fabsy = std::fabs(_preOpValue - value);
        double delta = ((_delta*(new_nInAvg - 1.0 )) + fabsy) / new_nInAvg;

        if (_CC_DEBUG & (CC_DEBUG_MULTIVOLT | CC_DEBUG_IVVC))
        {
            CTILOG_DEBUG(dout, "Point Delta: Device ID: " << _deviceId <<" Point ID: " << _pointId << " fabs: " << fabsy << " delta: " << delta);
        }

        setDelta( delta );
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
