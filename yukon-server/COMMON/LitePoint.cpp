#include "precompiled.h"

#include "LitePoint.h"
#include "row_reader.h"
#include "resolvers.h"


LitePoint::LitePoint() :
    _pointId(0),
    _pointType(InvalidPointType),
    _paoId(0),
    _pointOffset(0),
    _controlOffset(0),
    _controlType(ControlType_Invalid),
    _closeTime1(0),
    _closeTime2(0),
    _multiplier(0),
    _stateGroupId(0)
{

}

LitePoint::LitePoint( Cti::RowReader & rdr )
    :   _pointId( 0 ),
        _pointType( InvalidPointType ),
        _paoId( 0 ),
        _pointOffset( 0 ),
        _controlOffset( 0 ),
        _controlType( ControlType_Invalid ),
        _closeTime1( 0 ),
        _closeTime2( 0 ),
        _multiplier( 0 ),
        _stateGroupId( 0 )
{
    std::string input;

    rdr["PointId"]      >> _pointId;

    rdr["PointType"]    >> input;
    _pointType = resolvePointType( input );

    rdr["PointName"]    >> _pointName;
    rdr["PAOBjectId"]   >> _paoId;
    rdr["PointOffset"]  >> _pointOffset;
    rdr["StateGroupId"] >> _stateGroupId;

    if ( ! rdr["ControlOffset"].isNull() )
    {
        rdr["ControlOffset"] >> _controlOffset;
    }

    if ( ! rdr["MULTIPLIER"].isNull() )
    {
        rdr["MULTIPLIER"] >> _multiplier;
    }

    if ( ! rdr["ControlType"].isNull() )
    {
        rdr["ControlType"] >> input;
        _controlType = resolveControlType( input );

        rdr["StateZeroControl"] >> _stateZeroControl;
        rdr["StateOneControl"]  >> _stateOneControl;
        rdr["CloseTime1"]       >> _closeTime1;
        rdr["CloseTime2"]       >> _closeTime2;
    }
}

LitePoint::LitePoint( const int Id, const CtiPointType_t Type, const std::string & Name,
                      const int PaoId, const int Offset,
                      const std::string & stateZeroControl,
                      const std::string & stateOneControl,
                      const double Multiplier,
                      const int stateGroupId )
    : _pointId(Id),
    _pointType(Type),
    _pointName(Name),
    _paoId(PaoId),
    _pointOffset(Offset),
    _controlOffset(0),
    _controlType(ControlType_Invalid),
    _closeTime1(0),
    _closeTime2(0),
    _stateZeroControl(stateZeroControl),
    _stateOneControl(stateOneControl),
    _multiplier(Multiplier),
    _stateGroupId(stateGroupId)
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

void LitePoint::setControlType(CtiControlType_t controlType)
{
    _controlType = controlType;
}

CtiControlType_t LitePoint::getControlType() const
{
    return _controlType;
}

void LitePoint::setCloseTime1(int closeTime1)
{
    _closeTime1 = closeTime1;
}

int LitePoint::getCloseTime1() const
{
    return _closeTime1;
}

void LitePoint::setCloseTime2(int closeTime)
{
    _closeTime2 = closeTime;
}

int LitePoint::getCloseTime2() const
{
    return _closeTime2;
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

void LitePoint::setMultiplier(const double multiplier)
{
    _multiplier = multiplier;
}

double LitePoint::getMultiplier() const
{
    return _multiplier;
}

void LitePoint::setStateGroupId(const int stateGroupId)
{
    _stateGroupId = stateGroupId;
}

int LitePoint::getStateGroupId() const
{
    return _stateGroupId;
}

