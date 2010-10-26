#include "yukon.h"

#include "LitePoint.h"

LitePoint::LitePoint() : _pointId(0),
                         _pointType(InvalidPointType),
                         _paoId(0),
                         _pointOffset(0)
{

}
LitePoint::LitePoint(const LitePoint& point)
{
    _pointId = point._pointId;
    _pointType = point._pointType;
    _pointName = point._pointName;
    _paoId = point._paoId;
    _pointOffset = point._pointOffset;
}

LitePoint::~LitePoint()
{

}

LitePoint::LitePoint( const int Id, const CtiPointType_t Type, const std::string & Name, const int PaoId, const int Offset)
    : _pointId(Id),
    _pointType(Type),
    _pointName(Name),
    _paoId(PaoId),
    _pointOffset(Offset)
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

LitePoint& LitePoint::operator=( const LitePoint & right)
{
    _pointId = right._pointId;
    _pointType = right._pointType;
    _pointName = right._pointName;
    _paoId = right._paoId;
    _pointOffset = right._pointOffset;

    return *this;
}
