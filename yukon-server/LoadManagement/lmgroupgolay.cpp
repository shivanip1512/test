/*---------------------------------------------------------------------------
  Filename:  lmgroupgolay.cpp

  Programmer:  Aaron Lauinger
        
  Description:    Source file for CtiLMGroupGolay.
  CtiLMGroupGolay maintains the state and handles
  the persistence of golay groups in Load Management.

  Initial Date:  3/6/2004
         
  COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2004
  ---------------------------------------------------------------------------*/
#include "yukon.h"

#include "lmgroupgolay.h"
#include "lmid.h"
#include "logger.h"
#include "dbaccess.h"

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMGroupGolay, CTILMGROUPGOLAY_ID )

/*---------------------------------------------------------------------------
  Constructors
  ---------------------------------------------------------------------------*/
    CtiLMGroupGolay::CtiLMGroupGolay()
{   
}

CtiLMGroupGolay::CtiLMGroupGolay(RWDBReader& rdr)
{
    restore(rdr);   
}

CtiLMGroupGolay::CtiLMGroupGolay(const CtiLMGroupGolay& groupexp)
{
    operator=(groupexp);
}

/*---------------------------------------------------------------------------
  Destructor
  ---------------------------------------------------------------------------*/
CtiLMGroupGolay::~CtiLMGroupGolay()
{
}

/*-------------------------------------------------------------------------
  createTimeRefreshRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of time refresh with the appropriate refresh rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupGolay::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    RWCString controlString = RWCString("control golay shed ");

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
CtiRequestMsg* CtiLMGroupGolay::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " - createSmartCycleRequestMsg() not implemented for golay LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;
}

/*-------------------------------------------------------------------------
  createTrueCycleRequestMsg

  Creates true cycle request msg which is exactly like a smart cycle but
  with the "truecycle" string at the end of the control string.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupGolay::createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " - createTrueCycleRequestMsg() not implemented for golay LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;
}

/*-------------------------------------------------------------------------
  createRotationRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of rotation with the appropriate send rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupGolay::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    RWCString controlString = RWCString("control golay shed ");
    
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
CtiRequestMsg* CtiLMGroupGolay::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    RWCString controlString = RWCString("control golay shed ");

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending master cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*----------------------------------------------------------------------------
  getNominalTimeout

  Returns the groups nominal timeout
----------------------------------------------------------------------------*/
int CtiLMGroupGolay::getNominalTimeout() const
{
    return _nominal_timeout;
}

/*----------------------------------------------------------------------------
  setNominalTimeout

  Sets this groups nominal timeout
----------------------------------------------------------------------------*/
CtiLMGroupGolay& CtiLMGroupGolay::setNominalTimeout(int nominal_timeout)
{
    _nominal_timeout = nominal_timeout;
    return *this;
}

/*-------------------------------------------------------------------------
  restoreGuts
    
  Restore self's state from the given stream
  --------------------------------------------------------------------------*/
void CtiLMGroupGolay::restoreGuts(RWvistream& istrm)
{
    CtiLMGroupBase::restoreGuts( istrm );
}

/*---------------------------------------------------------------------------
  saveGuts
    
  Save self's state onto the given stream
  ---------------------------------------------------------------------------*/
void CtiLMGroupGolay::saveGuts(RWvostream& ostrm ) const  
{
    CtiLMGroupBase::saveGuts( ostrm );
}

/*---------------------------------------------------------------------------
  operator=
  ---------------------------------------------------------------------------*/
CtiLMGroupGolay& CtiLMGroupGolay::operator=(const CtiLMGroupGolay& right)
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
int CtiLMGroupGolay::operator==(const CtiLMGroupGolay& right) const
{
    return CtiLMGroupBase::operator==(right);
}

/*---------------------------------------------------------------------------
  operator!=
  ---------------------------------------------------------------------------*/
int CtiLMGroupGolay::operator!=(const CtiLMGroupGolay& right) const
{
    return CtiLMGroupBase::operator!=(right);
}

/*---------------------------------------------------------------------------
  replicate
    
  Restores self's operation fields
  ---------------------------------------------------------------------------*/
CtiLMGroupBase* CtiLMGroupGolay::replicate() const
{
    return (new CtiLMGroupGolay(*this));
}

/*---------------------------------------------------------------------------
  restore
    
  Restores given a RWDBReader
  ---------------------------------------------------------------------------*/
void CtiLMGroupGolay::restore(RWDBReader& rdr)
{
    CtiLMGroupBase::restore(rdr);
#ifdef _THISNEEDSTOBEADDEDBACK_
    //NOTE: !
    RWDBNullIndicator isNull;
    rdr["sasimplenominaltimeout"] >> isNull;
    if(!isNull)
    {
	rdr["sasimplenominaltimeout"] >> _nominal_timeout;
    }
    else
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " **Checkpoint** " << " Golay LMGroup could not find a nominal timeout when restoring from the database" << __FILE__ << "(" << __LINE__ << ")" << endl;
    }
#endif    
}

