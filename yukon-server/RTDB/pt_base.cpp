#include "precompiled.h"

#include "pt_base.h"
#include "tbl_pt_alarm.h"
#include "logger.h"

using std::string;

bool CtiPointBase::isNumeric() const
{
    switch(getType())
    {
        case AnalogPointType:
        case PulseAccumulatorPointType:
        case DemandAccumulatorPointType:
        case CalculatedPointType:
        case AnalogOutputPointType:
        {
            return true;
        }
    }

    return false;
}

bool CtiPointBase::isStatus() const
{
    switch(getType())
    {
        case StatusPointType:
        case StatusOutputPointType:
        case CalculatedStatusPointType:
        {
            return true;
        }
    }

    return false;
}

CtiPointBase::CtiPointBase(LONG pid) :
    _pointBase(pid)
{}

string CtiPointBase::getSQLCoreStatement()
{
    return CtiTablePointBase::getSQLCoreStatement();
}

void CtiPointBase::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    _pointBase.DecodeDatabaseReader(rdr);

    setUpdatedFlag();
    setValid();
}


INT               CtiPointBase::getArchiveInterval() const       { return _pointBase.getArchiveInterval();}
INT               CtiPointBase::getArchiveType() const           { return _pointBase.getArchiveType();}
INT               CtiPointBase::getPointOffset() const           { return _pointBase.getPointOffset();}

LONG              CtiPointBase::getPointID() const               { return _pointBase.getPointID();}
LONG              CtiPointBase::getID() const                    { return _pointBase.getPointID();}

string            CtiPointBase::getName() const                  { return _pointBase.getName();}
LONG              CtiPointBase::getDeviceID() const              { return _pointBase.getPAObjectID();}


LONG              CtiPointBase::getStateGroupID() const          { return _pointBase.getStateGroupID();}

BOOL              CtiPointBase::isAlarmDisabled() const          { return _pointBase.getAlarmDisableTag();}

BOOL              CtiPointBase::isPseudoPoint() const            { return _pointBase.getPseudoTag();}

CtiPointType_t    CtiPointBase::getType() const                  { return _pointBase.getType();}

void CtiPointBase::setType(CtiPointType_t type)
{
    _pointBase.setType(type);
}

UINT CtiPointBase::adjustStaticTags(UINT &tag) const
{
    return _pointBase.adjustStaticTags(tag);
}

double CtiPointBase::getDefaultValue( ) const
{
    return 0.0;
}

