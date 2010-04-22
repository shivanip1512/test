#include "yukon.h"
#include "LoadTapChanger.h"
#include "ccid.h"

extern ULONG _IVVC_MIN_TAP_PERIOD_MINUTES;

RWDEFINE_COLLECTABLE( LoadTapChanger, CTILTC_ID )

LoadTapChanger::LoadTapChanger() : CapControlPao(),
                                   _updated(true),
                                   _manualLocalMode(false),
                                   _lowerTap(false),
                                   _raiseTap(false),
                                   _autoRemote(false)
{

}

LoadTapChanger::LoadTapChanger(RWDBReader& rdr) : CapControlPao(rdr),
                                                  _updated(true),
                                                  _manualLocalMode(false),
                                                  _lowerTap(false),
                                                  _raiseTap(false),
                                                  _autoRemote(false)
{

}

LoadTapChanger::LoadTapChanger(const LoadTapChanger& ltc) : CapControlPao(),
                                                            _updated(true),
                                                            _manualLocalMode(false),
                                                            _lowerTap(false),
                                                            _raiseTap(false),
                                                            _autoRemote(false)
{
    operator=(ltc);
}

PointValueHolder& LoadTapChanger::getPointValueHolder()
{
    return _pointValues;
}

void LoadTapChanger::handlePointData(CtiPointDataMsg* message)
{
    setUpdated(true);
    _pointValues.updatePointValue(message);
}

bool LoadTapChanger::isUpdated()
{
    return _updated;
}

void LoadTapChanger::setUpdated(bool updated)
{
    _updated = updated;
}

bool LoadTapChanger::isManualLocalMode()
{
    return _manualLocalMode;
}

void LoadTapChanger::setManualLocalMode(bool manualLocalMode)
{
    _manualLocalMode = manualLocalMode;
}

void LoadTapChanger::setLtcVoltagePoint(const LitePoint& point)
{
    _ltcVoltagePoint = point;
}

const LitePoint& LoadTapChanger::getLtcVoltagePoint()
{
    return _ltcVoltagePoint;
}

void LoadTapChanger::setLowerTapPoint(const LitePoint& point)
{
    _lowerTapPoint = point;
}

const LitePoint& LoadTapChanger::getLowerTapPoint()
{
    return _lowerTapPoint;
}

void LoadTapChanger::setRaiseTapPoint(const LitePoint& point)
{
    _raiseTapPoint = point;
}

const LitePoint& LoadTapChanger::getRaiseTapPoint()
{
    return _raiseTapPoint;
}

void LoadTapChanger::setAutoRemotePoint(const LitePoint& point)
{
    _autoRemotePoint = point;
}

const LitePoint& LoadTapChanger::getAutoRemotePoint()
{
    return _autoRemotePoint;
}

void LoadTapChanger::setTapPosition(const LitePoint& point)
{
    _tapPositionPoint = point;
}

const LitePoint& LoadTapChanger::getTapPosition()
{
    return _tapPositionPoint;
}

bool LoadTapChanger::getPointValue(int pointId, double& value)
{
    if (_pointValues.getPointValue(pointId,value))
    {
        return true;
    }
    return false;
}

void LoadTapChanger::getRegistrationPoints(std::set<long>& regPoints)
{
    if (_ltcVoltagePoint.getPointType() != InvalidPointType)
    {
        regPoints.insert(_ltcVoltagePoint.getPointId());
    }

    if (_lowerTapPoint.getPointType() != InvalidPointType)
    {
        regPoints.insert(_lowerTapPoint.getPointId());
    }

    if (_raiseTapPoint.getPointType() != InvalidPointType)
    {
        regPoints.insert(_raiseTapPoint.getPointId());
    }

    if (_autoRemotePoint.getPointType() != InvalidPointType)
    {
        regPoints.insert(_autoRemotePoint.getPointId());
    }

    if (_tapPositionPoint.getPointType() != InvalidPointType)
    {
        regPoints.insert(_tapPositionPoint.getPointId());
    }
}

void LoadTapChanger::saveGuts(RWvostream& ostrm) const
{
    RWCollectable::saveGuts(ostrm);
    CapControlPao::saveGuts(ostrm);

    ostrm << 0 //Parent Id.
          << _lowerTap
          << _raiseTap
          << _autoRemote
          << _manualLocalMode;
}

LoadTapChanger& LoadTapChanger::operator=(const LoadTapChanger& right)
{
    CapControlPao::operator =(right);

    _ltcVoltagePoint = right._ltcVoltagePoint;
    _lowerTapPoint = right._lowerTapPoint;
    _raiseTapPoint = right._raiseTapPoint;
    _autoRemotePoint = right._autoRemotePoint;
    _tapPositionPoint = right._tapPositionPoint;

    _updated = right._updated;
    _lowerTap = right._lowerTap;
    _raiseTap = right._raiseTap;
    _autoRemote = right._autoRemote;
    _manualLocalMode = right._manualLocalMode;

    _pointValues = right._pointValues;

    return *this;
}

void LoadTapChanger::updateFlags()
{
    static int timeToShowOperation = (int)(_IVVC_MIN_TAP_PERIOD_MINUTES * 60) / 2;

    bool updated = false;

    bool lowerTap = false;
    bool raiseTap = false;
    bool autoRemote = false;

    CtiTime now;
    CtiTime pointTime;
    double pointValue = 0.0;

    bool ret = _pointValues.getPointTime(_lowerTapPoint.getPointId(),pointTime);
    if (ret)
    {
        if ((pointTime + timeToShowOperation) > now)
        {
            lowerTap = true;
        }
    }
    if (_lowerTap != lowerTap)
    {
        _lowerTap = lowerTap;
        setUpdated(true);
    }

    ret = _pointValues.getPointTime(_raiseTapPoint.getPointId(),pointTime);
    if (ret)
    {
        if ((pointTime + timeToShowOperation) > now)
        {
            raiseTap = true;
        }
    }
    if (_raiseTap != raiseTap)
    {
        _raiseTap = raiseTap;
        setUpdated(true);
    }

    ret = _pointValues.getPointValue(_autoRemotePoint.getPointId(),pointValue);
    if (ret)
    {
        autoRemote = (pointValue == 0.0);
    }
    if (_autoRemote != autoRemote)
    {
        _autoRemote = autoRemote;
        setUpdated(true);
    }
}
