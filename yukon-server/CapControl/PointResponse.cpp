
#include "yukon.h"
#include "PointResponse.h"
#include "logger.h"
#include "ccid.h"

#include <math.h>

extern unsigned long _CC_DEBUG;

namespace Cti {
namespace CapControl {

PointResponse::PointResponse() : _pointId(0),
                                 _bankId(0),
                                 _preOpValue(0.0),
                                 _delta(0.0)
{

}

PointResponse::PointResponse(long pointId, long bankId, double preOpValue, double delta) : _pointId(pointId),
                                                                                           _bankId(bankId),
                                                                                           _preOpValue(preOpValue),
                                                                                           _delta(delta)
{
}

bool PointResponse::operator<(const PointResponse& right) const
{
    return _bankId < right._bankId || _pointId < right._pointId;
}

bool PointResponse::operator==(const PointResponse& right) const
{
    return _bankId == right._bankId && _pointId == right._pointId;
}

long PointResponse::getPointId()
{
    return _pointId;
}

void PointResponse::setPointId(long pointId)
{
    _pointId = pointId;
}

long PointResponse::getBankId()
{
    return _bankId;
}

void PointResponse::setBankId(long bankId)
{
    _bankId = bankId;
}

double PointResponse::getPreOpValue()
{
    return _preOpValue;
}

void PointResponse::setPreOpValue(double preOpValue)
{
    _preOpValue = preOpValue;
}

double PointResponse::getDelta()
{
    return _delta;
}

void PointResponse::setDelta(double delta)
{
    _delta = delta;
}

void PointResponse::updateDelta(long nInAvg, double value)
{
    if (_CC_DEBUG & (CC_DEBUG_MULTIVOLT | CC_DEBUG_IVVC))
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " Point Delta: Bank ID: " << _bankId <<" Point ID: " << _pointId << " preOpValue: " << _preOpValue << " currentValue: " << value << endl;
    }

    double nInAvg = nInAvg != 0 ? nInAvg:1;
    double fabsy = fabs(_preOpValue - value);
    double delta = ((_delta*(nInAvg - 1.0 )) + fabsy) / nInAvg;

    if (_CC_DEBUG & (CC_DEBUG_MULTIVOLT | CC_DEBUG_IVVC))
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " Point Delta: Bank ID: " << _bankId <<" Point ID: " << _pointId << " fabs: " << fabsy << " delta: " << delta << endl;
    }

    _delta = delta;

}
}
}
