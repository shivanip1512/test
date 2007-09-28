/*-----------------------------------------------------------------------------*
*
* File:   pt_base
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/pt_base.cpp-arc  $
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2007/09/28 15:43:05 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "pt_base.h"
#include "tbl_pt_alarm.h"
#include "logger.h"
#include "rwutil.h"

void IM_EX_PNTDB DefDynamicFactory(const CtiPointBase& pt)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

CtiPointBase::~CtiPointBase()
{
    if(_dynamic != NULL)    // We need to get rid of any Dynamic Data which is associated with us.
    {
        delete _dynamic;
    }

    if(_alarming != NULL)
    {
        delete _alarming;
    }
}

CtiPointBase::DynamicFactory CtiPointBase::getDynamicFactory()
{
    return _fpDynFactory;
}

/*---------------------------------------------------------------------------*
 * This method resets the Dynamic point factory and returns a pointer to the
 * previous on if needed
 *---------------------------------------------------------------------------*/
CtiPointBase::DynamicFactory CtiPointBase::setDynamicFactory(DynamicFactory fpNew)
{
    DynamicFactory fpOld = _fpDynFactory;

    _fpDynFactory = fpNew;

    return fpOld;
}

void CtiPointBase::primeDynamicData()
{
    if(_fpDynFactory != NULL)
    {
        (*_fpDynFactory)(*this);
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** ERROR **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " No dynamic point factory has been associated with this point!" << endl;
        }
    }
}

CtiDynamicPointBase* CtiPointBase::replicateDynamicData() const
{
    CtiDynamicPointBase*  clone = NULL;

    if(_dynamic != NULL)
    {
        clone = _dynamic->replicate();
    }

    return clone;
}


bool CtiPointBase::hasAlarming() const
{
    return(_alarming != 0);
}

CtiTablePointAlarming& CtiPointBase::getAlarming(bool refresh)
{
    try
    {
        if(_alarming == NULL)
        {
            _alarming = CTIDBG_new CtiTablePointAlarming( getPointID() );
            refresh = true;
        }

        if(refresh)
        {
            _alarming->Restore();
        }
    }
    catch( ... )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - MEMORY in point \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return *_alarming;
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
_pointBase(pid),
_fpDynFactory(DefDynamicFactory),
_dynamic(NULL),
_alarming(NULL),
_triggerPoint(false),
_verificationPoint(false)
{}

// Copy constructor.....
CtiPointBase::CtiPointBase(const CtiPointBase& aRef) :
_pointBase(-1),
_fpDynFactory(DefDynamicFactory),
_dynamic(NULL),
_alarming(NULL),
_triggerPoint(false),
_verificationPoint(false)
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
            _dynamic   = aRef.replicateDynamicData();
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

void CtiPointBase::DecodeAlarmingDatabaseReader(RWDBReader &rdr)
{
    if(_alarming == NULL)
    {
        _alarming = CTIDBG_new CtiTablePointAlarming( getPointID() );
    }

    _alarming->DecodeDatabaseReader(rdr);
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

CtiDynamicPointBase* CtiPointBase::getDynamic()
{
    return _dynamic;
}

CtiPointBase& CtiPointBase::setDynamic(CtiDynamicPointBase *pDyn)
{
    _dynamic = pDyn;
    return *this;
}

bool CtiPointBase::limitStateCheck( const int limitOrState, double val, int &direction)
{
    return false;
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

void CtiPointBase::setIsTriggerPoint(bool isTrigPoint)
{
    _triggerPoint = isTrigPoint;
}

//Valid only in dispatch!!!! 5/19/2006
bool CtiPointBase::isATriggerPoint()
{
    return _triggerPoint;
}

void CtiPointBase::setIsVerificationPoint(bool isVerifyPoint)
{
    _verificationPoint = isVerifyPoint;
}

//Valid only in dispatch!!!! 5/19/2006
bool CtiPointBase::isAVerificationPoint()
{
    return _verificationPoint;
}
