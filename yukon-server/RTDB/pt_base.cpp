#include "precompiled.h"

#include "pt_base.h"
#include "tbl_pt_alarm.h"
#include "logger.h"

using std::string;

bool CtiPointBase::isNumeric() const
{
    return isNumeric(getType());
}

bool CtiPointBase::isNumeric(const CtiPointType_t type)
{
    switch(type)
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
    return isStatus(getType());
}

bool CtiPointBase::isStatus(const CtiPointType_t type)
{
    switch(type)
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

bool              CtiPointBase::isAlarmDisabled() const          { return _pointBase.isAlarmDisabled();}
bool              CtiPointBase::isOutOfService() const           { return _pointBase.isOutOfService();}
bool              CtiPointBase::isPseudoPoint() const            { return _pointBase.isPseudoPoint();}

CtiPointType_t    CtiPointBase::getType() const                  { return _pointBase.getType();}

void CtiPointBase::setType(CtiPointType_t type)
{
    _pointBase.setType(type);
}

const unsigned CtiPointBase::TAG_MASK_BASE = 
        TAG_ATTRIB_PSEUDO
        | TAG_DISABLE_POINT_BY_POINT 
        | TAG_DISABLE_ALARM_BY_POINT;

unsigned CtiPointBase::makeStaticTags(const bool isPseudoPoint, const bool isOutOfService, const bool isAlarmDisabled)
{
    return
        TAG_ATTRIB_PSEUDO * isPseudoPoint
        | TAG_DISABLE_POINT_BY_POINT * isOutOfService
        | TAG_DISABLE_ALARM_BY_POINT * isAlarmDisabled;
}

const unsigned CtiPointBase::TAG_MASK_CONTROL = 
        TAG_ATTRIB_CONTROL_AVAILABLE | 
        TAG_DISABLE_CONTROL_BY_POINT;

unsigned CtiPointBase::makeStaticControlTags(const bool isControlAvailable, const bool isControlInhibited)
{
    return (TAG_ATTRIB_CONTROL_AVAILABLE * isControlAvailable) |
           (TAG_DISABLE_CONTROL_BY_POINT * isControlInhibited);
}

unsigned CtiPointBase::adjustStaticTags(unsigned& tag) const
{
    tag &= ~TAG_MASK_BASE;

    tag |= makeStaticTags(isPseudoPoint(), isOutOfService(), isAlarmDisabled());

    return tag;
}

double CtiPointBase::getDefaultValue( ) const
{
    return 0.0;
}

