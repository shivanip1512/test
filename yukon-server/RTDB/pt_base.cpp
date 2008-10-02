/*-----------------------------------------------------------------------------*
*
* File:   pt_base
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/pt_base.cpp-arc  $
* REVISION     :  $Revision: 1.22 $
* DATE         :  $Date: 2008/10/02 18:27:29 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "pt_base.h"
#include "tbl_pt_alarm.h"
#include "logger.h"
#include "rwutil.h"


CtiPointBase::~CtiPointBase()
{
}

bool CtiPointBase::isNumeric() const
{
    bool bNumeric = false;

    switch(getType())
    {
    case AnalogPointType:
    case PulseAccumulatorPointType:
    case DemandAccumulatorPointType:
    case CalculatedPointType:
    case AnalogOutputPointType:
        {
            bNumeric = true;
            break;
        }
    case SystemPointType:
    case StatusOutputPointType:
    case StatusPointType:
    case CalculatedStatusPointType:
    case InvalidPointType:               // Place Holder - allows point type looping.
    default:
        {
            break;
        }
    }

    return bNumeric;
}

bool CtiPointBase::isStatus() const
{
    bool bStatus = false;

    switch(getType())
    {
    case AnalogPointType:
    case PulseAccumulatorPointType:
    case DemandAccumulatorPointType:
    case CalculatedPointType:
    case AnalogOutputPointType:
        {
            break;
        }
    case SystemPointType:
    case StatusOutputPointType:
    case StatusPointType:
    case CalculatedStatusPointType:
    case InvalidPointType:               // Place Holder - allows point type looping.
    default:
        {
            bStatus = true;
            break;
        }
    }

    return bStatus;
}

CtiPointBase::CtiPointBase(LONG pid) :
    _pointBase(pid)
{}

// Copy constructor.....
CtiPointBase::CtiPointBase(const CtiPointBase& aRef) :
    _pointBase(-1)
{
    *this = aRef;
}


CtiPointBase& CtiPointBase::operator=(const CtiPointBase& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        {
            _pointBase = aRef._pointBase;
        }
    }

    return *this;
}

void CtiPointBase::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    CtiTablePointBase::getSQL(db, keyTable, selector);
}

void CtiPointBase::DecodeDatabaseReader(RWDBReader &rdr)
{
    _pointBase.DecodeDatabaseReader(rdr);

    setUpdatedFlag();
    setValid();
}

void CtiPointBase::DumpData()
{
    _pointBase.dump();
}


INT               CtiPointBase::getArchiveInterval() const       { return _pointBase.getArchiveInterval();}
INT               CtiPointBase::getArchiveType() const           { return _pointBase.getArchiveType();}
INT               CtiPointBase::getPointOffset() const           { return _pointBase.getPointOffset();}

LONG              CtiPointBase::getPointID() const               { return _pointBase.getPointID();}
LONG              CtiPointBase::getID() const                    { return _pointBase.getPointID();}

string            CtiPointBase::getName() const                  { return _pointBase.getName();}
LONG              CtiPointBase::getDeviceID() const              { return _pointBase.getPAObjectID();}


//string            CtiPointBase::getLogicalGroup() const          { return _pointBase.getLogicalGroup();}
LONG              CtiPointBase::getStateGroupID() const          { return _pointBase.getStateGroupID();}

BOOL              CtiPointBase::getDisableTag() const            { return _pointBase.getDisableTag();}
BOOL              CtiPointBase::isInService() const              { return !(_pointBase.getDisableTag());}
BOOL              CtiPointBase::isOutOfService() const           { return _pointBase.getDisableTag();}

BOOL              CtiPointBase::getAlarmDisableTag() const       { return _pointBase.getAlarmDisableTag();}
BOOL              CtiPointBase::isAlarmDisabled() const          { return _pointBase.getAlarmDisableTag();}

BOOL              CtiPointBase::getPseudoTag() const             { return _pointBase.getPseudoTag();}
BOOL              CtiPointBase::isPseudoPoint() const            { return _pointBase.getPseudoTag();}

BOOL              CtiPointBase::getArchivePending() const        { return _pointBase.getArchivePending();}
BOOL              CtiPointBase::isArchivePending() const         { return _pointBase.getArchivePending();}

CtiPointType_t    CtiPointBase::getType() const                  { return _pointBase.getType();}
CtiPointType_t    CtiPointBase::isA() const                      { return _pointBase.getType();}


void CtiPointBase::setType(CtiPointType_t type)
{
    _pointBase.setType(type);
}

void CtiPointBase::setArchivePending(BOOL b)
{
    _pointBase.setArchivePending(b);
}
void CtiPointBase::resetArchivePending(BOOL b)
{
    _pointBase.setArchivePending(b);
}

UINT CtiPointBase::adjustStaticTags(UINT &tag) const
{
    return _pointBase.adjustStaticTags(tag);
}

UINT CtiPointBase::getStaticTags()
{
    return _pointBase.getStaticTags();
}

bool CtiPointBase::isAbnormal( double value )
{
    return false;
}

double CtiPointBase::getDefaultValue( ) const
{
    return 0.0;
}

double CtiPointBase::getInitialValue( ) const
{
    return 0.0;
}

int CtiPointBase::getControlExpirationTime() const
{
    return 300;
}

/*
 * This method makes certain this object should be able to decode with this reader.
 */
bool CtiPointBase::isA(RWDBReader &rdr) const
{
    bool I_is = false;
    int pttype;
    string rwsType = "(none)";

    rdr["pointtype"]  >> rwsType;
    pttype = resolvePointType(rwsType);
    if(pttype == getType()) I_is = true;

    return I_is;
}

