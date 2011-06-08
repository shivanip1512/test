
#include "yukon.h"
#include "PointResponse.h"
#include "logger.h"
#include "ccid.h"

#include <math.h>

using std::endl;

extern unsigned long _CC_DEBUG;

namespace Cti {
namespace CapControl {

PointResponse::PointResponse(long pointId, long bankId, double preOpValue, double delta, bool staticDelta) : _pointId(pointId),
                                                                                                            _bankId(bankId),
                                                                                                            _preOpValue(preOpValue),
                                                                                                            _delta(delta),
                                                                                                            _staticDelta(staticDelta)
{
}

long PointResponse::getPointId() const
{
    return _pointId;
}


long PointResponse::getBankId() const
{
    return _bankId;
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
    _delta = delta;
}

bool PointResponse::getStaticDelta() const
{
    return _staticDelta;
}

void PointResponse::setStaticDelta(bool staticDelta)
{
    _staticDelta = staticDelta;
}

void PointResponse::updateDelta(long nInAvg, double value)
{
    if ( ! _staticDelta)
    {
        if (_CC_DEBUG & (CC_DEBUG_MULTIVOLT | CC_DEBUG_IVVC))
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " Point Delta: Bank ID: " << _bankId <<" Point ID: " << _pointId << " preOpValue: " << _preOpValue << " currentValue: " << value << endl;
        }

        double new_nInAvg = nInAvg != 0 ? nInAvg:1;
        double fabsy = fabs(_preOpValue - value);
        double delta = ((_delta*(new_nInAvg - 1.0 )) + fabsy) / new_nInAvg;

        if (_CC_DEBUG & (CC_DEBUG_MULTIVOLT | CC_DEBUG_IVVC))
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " Point Delta: Bank ID: " << _bankId <<" Point ID: " << _pointId << " fabs: " << fabsy << " delta: " << delta << endl;
        }

        _delta = delta;
    }
}

void PointResponse::updatePreOpValue(double preOpValue)
{
    _preOpValue = preOpValue;
}

}
}
