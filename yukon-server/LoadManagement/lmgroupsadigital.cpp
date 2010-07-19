/*---------------------------------------------------------------------------
  Filename:  lmgroupsadigital.cpp

  Programmer:  Aaron Lauinger
        
  Description:    Source file for CtiLMGroupSADigital.
  CtiLMGroupSADigital maintains the state and handles
  the persistence of sa 305 groups in Load Management.

  Initial Date:  3/6/2004
         
  COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2004
  ---------------------------------------------------------------------------*/
#include "yukon.h"

#include "lmgroupsadigital.h"
#include "lmid.h"
#include "logger.h"
#include "dbaccess.h"

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMGroupSADigital, CTILMGROUPSADIGITAL_ID )

/*---------------------------------------------------------------------------
  Constructors
  ---------------------------------------------------------------------------*/
CtiLMGroupSADigital::CtiLMGroupSADigital() :
_nominal_timeout(0)
{   
}

CtiLMGroupSADigital::CtiLMGroupSADigital(Cti::RowReader &rdr)
{
    restore(rdr);   
}

CtiLMGroupSADigital::CtiLMGroupSADigital(const CtiLMGroupSADigital& groupexp)
{
    operator=(groupexp);
}

/*---------------------------------------------------------------------------
  Destructor
  ---------------------------------------------------------------------------*/
CtiLMGroupSADigital::~CtiLMGroupSADigital()
{
}

/*-------------------------------------------------------------------------
  createTimeRefreshRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of time refresh with the appropriate refresh rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSADigital::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    string controlString("control sadigital shed ");

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending time refresh command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
  createSmartCycleRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of smart cycle with the appropriate cycle percent, period length
  in minutes, and the default count of periods.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSADigital::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << CtiTime() << " - createSmartCycleRequestMsg() not implemented for saDigital LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;
}

/*-------------------------------------------------------------------------
  createTrueCycleRequestMsg

  Creates true cycle request msg which is exactly like a smart cycle but
  with the "truecycle" string at the end of the control string.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSADigital::createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << CtiTime() << " - createTrueCycleRequestMsg() not implemented for saDigital LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;
}

/*-------------------------------------------------------------------------
  createRotationRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of rotation with the appropriate send rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSADigital::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    string controlString("control sadigital shed ");
    
    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending rotation command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
  createMasterCycleRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of master cycle with the appropriate off time, period length.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSADigital::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    string controlString("control sadigital shed ");

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending master cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}


/*----------------------------------------------------------------------------
  getNominalTimeout

  Returns the groups nominal timeout
----------------------------------------------------------------------------*/
int CtiLMGroupSADigital::getNominalTimeout() const
{
    return _nominal_timeout;
}

/*----------------------------------------------------------------------------
  setNominalTimeout

  Sets this groups nominal timeout
----------------------------------------------------------------------------*/
CtiLMGroupSADigital& CtiLMGroupSADigital::setNominalTimeout(int nominal_timeout)
{
    _nominal_timeout = nominal_timeout;
    return *this;
}

/*-------------------------------------------------------------------------
  restoreGuts
    
  Restore self's state from the given stream
  --------------------------------------------------------------------------*/
void CtiLMGroupSADigital::restoreGuts(RWvistream& istrm)
{
    CtiLMGroupBase::restoreGuts( istrm );
}

/*---------------------------------------------------------------------------
  saveGuts
    
  Save self's state onto the given stream
  ---------------------------------------------------------------------------*/
void CtiLMGroupSADigital::saveGuts(RWvostream& ostrm ) const  
{
    CtiLMGroupBase::saveGuts( ostrm );
}

/*---------------------------------------------------------------------------
  operator=
  ---------------------------------------------------------------------------*/
CtiLMGroupSADigital& CtiLMGroupSADigital::operator=(const CtiLMGroupSADigital& right)
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
int CtiLMGroupSADigital::operator==(const CtiLMGroupSADigital& right) const
{
    return CtiLMGroupBase::operator==(right);
}

/*---------------------------------------------------------------------------
  operator!=
  ---------------------------------------------------------------------------*/
int CtiLMGroupSADigital::operator!=(const CtiLMGroupSADigital& right) const
{
    return CtiLMGroupBase::operator!=(right);
}

/*---------------------------------------------------------------------------
  replicate
    
  Restores self's operation fields
  ---------------------------------------------------------------------------*/
CtiLMGroupBase* CtiLMGroupSADigital::replicate() const
{
    return (new CtiLMGroupSADigital(*this));
}

/*---------------------------------------------------------------------------
  restore
    
  Restores given a Reader
  ---------------------------------------------------------------------------*/
void CtiLMGroupSADigital::restore(Cti::RowReader &rdr)
{
    CtiLMGroupBase::restore(rdr);
}

