#include "yukon.h"
#include "LoadTapChanger.h"

LoadTapChanger::LoadTapChanger() : Inherited()
{

}

LoadTapChanger::LoadTapChanger(RWDBReader& rdr) : Inherited(rdr)
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

void LoadTapChanger::setUpperVoltPoint(const LitePoint& point)
{
    _upperVoltPoint = point;
}

const LitePoint& LoadTapChanger::getUpperVoltPoint()
{
    return _upperVoltPoint;
}

void LoadTapChanger::setLowerVoltPoint(const LitePoint& point)
{
    _lowerVoltPoint = point;
}

const LitePoint& LoadTapChanger::getLowerVoltPoint()
{
    return _lowerVoltPoint;
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

    if (_upperVoltPoint.getPointType() != InvalidPointType)
    {
        regPoints.push_back(_upperVoltPoint.getPointId());
    }

    if (_lowerVoltPoint.getPointType() != InvalidPointType)
    {
        regPoints.push_back(_lowerVoltPoint.getPointId());
    }
}
