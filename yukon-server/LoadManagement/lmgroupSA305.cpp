/*---------------------------------------------------------------------------
  Filename:  lmgroupsa305.cpp

  Programmer:  Aaron Lauinger
        
  Description:    Source file for CtiLMGroupSA305.
  CtiLMGroupSA305 maintains the state and handles
  the persistence of sa 305 groups in Load Management.

  Initial Date:  3/6/2004
         
  COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2004
  ---------------------------------------------------------------------------*/
#include "yukon.h"

#include "lmgroupsa305.h"
#include "lmid.h"
#include "logger.h"
#include "dbaccess.h"

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMGroupSA305, CTILMGROUPSA305_ID )

/*---------------------------------------------------------------------------
  Constructors
  ---------------------------------------------------------------------------*/
    CtiLMGroupSA305::CtiLMGroupSA305()
{   
}

CtiLMGroupSA305::CtiLMGroupSA305(RWDBReader& rdr)
{
    restore(rdr);   
}

CtiLMGroupSA305::CtiLMGroupSA305(const CtiLMGroupSA305& groupexp)
{
    operator=(groupexp);
}

/*---------------------------------------------------------------------------
  Destructor
  ---------------------------------------------------------------------------*/
CtiLMGroupSA305::~CtiLMGroupSA305()
{
}

/*-------------------------------------------------------------------------
  createTimeRefreshRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of time refresh with the appropriate refresh rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA305::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    RWCString controlString = RWCString("control sa305 shed ");
    controlString += buildShedString(shedTime);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending time refresh command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
  createSmartCycleRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of smart cycle with the appropriate cycle percent, period length
  in minutes, and the default count of periods.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA305::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const
{
    char tempchar[64];
    RWCString controlString = RWCString("control sa305 cycle ");
    _ltoa(percent,tempchar,10);
    controlString += tempchar;
    controlString += " count ";
    _ltoa(defaultCount,tempchar,10);
    controlString += tempchar;
    controlString += " period ";
    controlString += buildPeriodString(period);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending smart cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
  createTrueCycleRequestMsg

  Creates true cycle request msg which is exactly like a smart cycle but
  with the "truecycle" string at the end of the control string.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA305::createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " - createTrueCycleRequestMsg() not implemented for SA305 LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;
}

/*-------------------------------------------------------------------------
  createRotationRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of rotation with the appropriate send rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA305::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    RWCString controlString = RWCString("control sa305 shed ");
    controlString += buildShedString(shedTime);
    
    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending rotation command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
  createMasterCycleRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of master cycle with the appropriate off time, period length.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA305::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    RWCString controlString = RWCString("control sa305 shed ");
    controlString += buildShedString(offTime);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending master cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
  restoreGuts
    
  Restore self's state from the given stream
  --------------------------------------------------------------------------*/
void CtiLMGroupSA305::restoreGuts(RWvistream& istrm)
{
    CtiLMGroupBase::restoreGuts( istrm );
}

/*---------------------------------------------------------------------------
  saveGuts
    
  Save self's state onto the given stream
  ---------------------------------------------------------------------------*/
void CtiLMGroupSA305::saveGuts(RWvostream& ostrm ) const  
{
    CtiLMGroupBase::saveGuts( ostrm );
}

/*---------------------------------------------------------------------------
  operator=
  ---------------------------------------------------------------------------*/
CtiLMGroupSA305& CtiLMGroupSA305::operator=(const CtiLMGroupSA305& right)
{
    if( this != &right )
    {
        CtiLMGroupBase::operator=(right);
    }
    return *this;
}

/*---------------------------------------------------------------------------
  operator==
  ---------------------------------------------------------------------------*/
int CtiLMGroupSA305::operator==(const CtiLMGroupSA305& right) const
{
    return CtiLMGroupBase::operator==(right);
}

/*---------------------------------------------------------------------------
  operator!=
  ---------------------------------------------------------------------------*/
int CtiLMGroupSA305::operator!=(const CtiLMGroupSA305& right) const
{
    return CtiLMGroupBase::operator!=(right);
}

/*---------------------------------------------------------------------------
  replicate
    
  Restores self's operation fields
  ---------------------------------------------------------------------------*/
CtiLMGroupBase* CtiLMGroupSA305::replicate() const
{
    return (new CtiLMGroupSA305(*this));
}

/*---------------------------------------------------------------------------
  restore
    
  Restores given a RWDBReader
  ---------------------------------------------------------------------------*/
void CtiLMGroupSA305::restore(RWDBReader& rdr)
{
    CtiLMGroupBase::restore(rdr);
}

