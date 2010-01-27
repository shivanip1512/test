#include "yukon.h"
#include "LoadTapChanger.h"

LoadTapChanger::LoadTapChanger() : CapControlPao()
{

}

LoadTapChanger::LoadTapChanger(RWDBReader& rdr) : CapControlPao(rdr)
{

}

PointValueHolder& LoadTapChanger::getPointValueHolder()
{
    return _pointValues;
}

void LoadTapChanger::handlePointData(CtiPointDataMsg* message)
{
    _pointValues.updatePointValue(message);
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

void LoadTapChanger::getRegistrationPoints(std::list<int>& regPoints)
{
    if (_ltcVoltagePoint.getPointType() != InvalidPointType)
    {
        regPoints.push_back(_ltcVoltagePoint.getPointId());
    }

    if (_lowerTapPoint.getPointType() != InvalidPointType)
    {
        regPoints.push_back(_lowerTapPoint.getPointId());
    }

    if (_raiseTapPoint.getPointType() != InvalidPointType)
    {
        regPoints.push_back(_raiseTapPoint.getPointId());
    }

    if (_autoRemotePoint.getPointType() != InvalidPointType)
    {
        regPoints.push_back(_autoRemotePoint.getPointId());
    }

    if (_tapPositionPoint.getPointType() != InvalidPointType)
    {
        regPoints.push_back(_tapPositionPoint.getPointId());
    }
}
