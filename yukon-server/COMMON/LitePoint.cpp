#include "precompiled.h"

#include "LitePoint.h"

LitePoint::LitePoint() :
    _pointId(0),
    _pointType(InvalidPointType),
    _paoId(0),
    _pointOffset(0),
    _controlOffset(0)
{

}

LitePoint::LitePoint( const int Id, const CtiPointType_t Type, const std::string & Name,
                      const int PaoId, const int Offset,
                      const std::string & stateZeroControl,
                      const std::string & stateOneControl )
    : _pointId(Id),
    _pointType(Type),
    _pointName(Name),
    _paoId(PaoId),
    _pointOffset(Offset),
    _controlOffset(0),
    _stateZeroControl(stateZeroControl),
    _stateOneControl(stateOneControl)
{

}

void LitePoint::setPointId(int pointId)
{
    _pointId = pointId;
}

int LitePoint::getPointId() const
{
    return _pointId;
}

void LitePoint::setPointType(CtiPointType_t pointType)
{
    _pointType = pointType;
}

CtiPointType_t LitePoint::getPointType() const
{
    return _pointType;
}

void LitePoint::setPointName(const std::string& pointName)
{
    _pointName = pointName;
}

std::string LitePoint::getPointName()const
{
    return _pointName;
}

void LitePoint::setPaoId(int paoId)
{
    _paoId = paoId;
}

int LitePoint::getPaoId() const
{
    return _paoId;
}

void LitePoint::setPointOffset(int pointOffset)
{
    _pointOffset = pointOffset;
}

int LitePoint::getPointOffset() const
{
    return _pointOffset;
}

void LitePoint::setControlOffset(int controlOffset)
{
    _controlOffset = controlOffset;
}

int LitePoint::getControlOffset() const
{
    return _controlOffset;
}

void LitePoint::setStateZeroControl(const std::string & stateZeroControl)
{
    _stateZeroControl = stateZeroControl;
}

std::string LitePoint::getStateZeroControl() const
{
    return _stateZeroControl;
}

void LitePoint::setStateOneControl(const std::string & stateOneControl)
{
    _stateOneControl = stateOneControl;
}

std::string LitePoint::getStateOneControl() const
{
    return _stateOneControl;
}

