/*-----------------------------------------------------------------------------*
*
* File:   pt_base
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/pt_base.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2004/10/12 20:14:18 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include "pt_base.h"
#include "tbl_pt_alarm.h"
#include "logger.h"

void IM_EX_PNTDB DefDynamicFactory(const CtiPointBase& pt)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        cout << "**** MEMORY **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
_alarming(NULL)
{}

// Copy constructor.....
CtiPointBase::CtiPointBase(const CtiPointBase& aRef) :
_pointBase(-1),
_fpDynFactory(DefDynamicFactory),
_dynamic(NULL),
_alarming(NULL)
{
    *this = aRef;
}


CtiPointBase& CtiPointBase::operator=(const CtiPointBase& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        {
            getPointBase()     = aRef.getPointBase();
            _dynamic           = aRef.replicateDynamicData();
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
    getPointBase().dump();
}

CtiTablePointBase  CtiPointBase::getPointBase() const            { return _pointBase;}
CtiTablePointBase& CtiPointBase::getPointBase()                  { return _pointBase;}


RWTime CtiPointBase::computeNextArchiveTime(const RWTime &Now) const
{
    RWTime nextTime;

    INT archiveInterval = getArchiveInterval();

    if( archiveInterval > 3600 )
    {
        RWTime hourstart = RWTime(Now.seconds() - (Now.seconds() % 3600)); // align to the hour.

        nextTime = RWTime(hourstart.seconds() - ((hourstart.hour() * 3600) % archiveInterval) + archiveInterval);
    }
    else
    {
        nextTime = RWTime(Now.seconds() - (Now.seconds() % archiveInterval) + archiveInterval);
    }

    while(nextTime <= Now)
    {
        nextTime += archiveInterval;
    }

    return nextTime;
}

INT               CtiPointBase::getArchiveInterval() const       { return getPointBase().getArchiveInterval();}
INT               CtiPointBase::getArchiveType() const           { return getPointBase().getArchiveType();}
INT               CtiPointBase::getPointOffset() const           { return getPointBase().getPointOffset();}

LONG              CtiPointBase::getPointID() const               { return getPointBase().getPointID();}
LONG              CtiPointBase::getID() const                    { return getPointBase().getPointID();}

RWCString         CtiPointBase::getName() const                  { return getPointBase().getName();}
LONG              CtiPointBase::getDeviceID() const              { return getPointBase().getPAObjectID();}


RWCString         CtiPointBase::getLogicalGroup() const          { return getPointBase().getLogicalGroup();}
LONG              CtiPointBase::getStateGroupID() const          { return getPointBase().getStateGroupID();}

BOOL              CtiPointBase::getDisableTag() const            { return getPointBase().getDisableTag();}
BOOL              CtiPointBase::isInService() const              { return !(getPointBase().getDisableTag());}
BOOL              CtiPointBase::isOutOfService() const           { return getPointBase().getDisableTag();}

BOOL              CtiPointBase::getAlarmDisableTag() const       { return getPointBase().getAlarmDisableTag();}
BOOL              CtiPointBase::isAlarmDisabled() const          { return getPointBase().getAlarmDisableTag();}

BOOL              CtiPointBase::getPseudoTag() const             { return getPointBase().getPseudoTag();}
BOOL              CtiPointBase::isPseudoPoint() const            { return getPointBase().getPseudoTag();}

BOOL              CtiPointBase::getArchivePending() const        { return getPointBase().getArchivePending();}
BOOL              CtiPointBase::isArchivePending() const         { return getPointBase().getArchivePending();}

CtiPointType_t    CtiPointBase::getType() const                  { return getPointBase().getType();}
CtiPointType_t    CtiPointBase::isA() const                      { return getPointBase().getType();}


void CtiPointBase::setArchivePending(BOOL b)
{
    getPointBase().setArchivePending(b);
}
void CtiPointBase::resetArchivePending(BOOL b)
{
    getPointBase().setArchivePending(b);
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

