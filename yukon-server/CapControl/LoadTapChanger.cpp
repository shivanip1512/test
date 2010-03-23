#include "yukon.h"
#include "LoadTapChanger.h"
#include "ccid.h"

RWDEFINE_COLLECTABLE( LoadTapChanger, CTILTC_ID )

LoadTapChanger::LoadTapChanger() : CapControlPao(),
                                   _updated(true)
{

}

LoadTapChanger::LoadTapChanger(RWDBReader& rdr) : CapControlPao(rdr),
                                                  _updated(true)
{

}

LoadTapChanger::LoadTapChanger(const LoadTapChanger& ltc)
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
    CapControlPao::saveGuts(ostrm);

    CtiTime lowerTime;
    CtiTime raiseTime;

    //bool lowerRet = _pointValues.getPointTime(_lowerTapPoint.getPointId(),lowerTime);
    //bool raiseRet = _pointValues.getPointTime(_raiseTapPoint.getPointId(),raiseTime);
    //bool autoRet = _pointValues.getPointValue(_autoRemotePoint.getPointId(),autoRemote);

    bool lowerTap = false;
    bool raiseTap = false;
    bool autoRemote = false;
    bool autoRemoteManual = false;

    ostrm << 0 //Parent Id.
          << lowerTap
          << raiseTap
          << autoRemote
          << autoRemoteManual;
}

LoadTapChanger& LoadTapChanger::operator=(const LoadTapChanger& right)
{

    _ltcVoltagePoint = right._ltcVoltagePoint;
    _lowerTapPoint = right._lowerTapPoint;
    _raiseTapPoint = right._raiseTapPoint;
    _autoRemotePoint = right._autoRemotePoint;
    _tapPositionPoint = right._tapPositionPoint;

    _pointValues = right._pointValues;

    return *this;
}
