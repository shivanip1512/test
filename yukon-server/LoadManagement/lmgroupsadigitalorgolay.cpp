/*---------------------------------------------------------------------------
  Filename:  lmgroupsadigitalorgolay.cpp

  Programmer:  Aaron Lauinger
        
  Description:    Source file for CtiLMGroupSADigitalORGolay.
  CtiLMGroupSADigitalORGolay maintains the state and handles
  the persistence of sa 305 groups in Load Management.

  Initial Date:  3/6/2004
         
  COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2004
  ---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "lmgroupsadigitalorgolay.h"
#include "lmid.h"
#include "logger.h"
#include "dbaccess.h"

RWDEFINE_COLLECTABLE( CtiLMGroupSADigitalORGolay, CTILMGROUPSADIGITALORGOLAY_ID )

/*---------------------------------------------------------------------------
  Constructors
  ---------------------------------------------------------------------------*/
    CtiLMGroupSADigitalORGolay::CtiLMGroupSADigitalORGolay()
{   
}

CtiLMGroupSADigitalORGolay::CtiLMGroupSADigitalORGolay(RWDBReader& rdr)
{
    restore(rdr);   
}

CtiLMGroupSADigitalORGolay::CtiLMGroupSADigitalORGolay(const CtiLMGroupSADigitalORGolay& groupexp)
{
    operator=(groupexp);
}

/*---------------------------------------------------------------------------
  Destructor
  ---------------------------------------------------------------------------*/
CtiLMGroupSADigitalORGolay::~CtiLMGroupSADigitalORGolay()
{
}

/*-------------------------------------------------------------------------
  createTimeRefreshRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of time refresh with the appropriate refresh rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSADigitalORGolay::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " - createTimeRefreshRequestMsg() not implemented for SA Digital or Golay LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;
}

/*-------------------------------------------------------------------------
  createSmartCycleRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of smart cycle with the appropriate cycle percent, period length
  in minutes, and the default count of periods.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSADigitalORGolay::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " - createSmartCycleRequestMsg() not implemented for SA Digital or Golay LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;
}

/*-------------------------------------------------------------------------
  createTrueCycleRequestMsg

  Creates true cycle request msg which is exactly like a smart cycle but
  with the "truecycle" string at the end of the control string.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSADigitalORGolay::createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " - createTrueCycleRequestMsg() not implemented for SA Digital or Golay LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;
}

/*-------------------------------------------------------------------------
  createRotationRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of rotation with the appropriate send rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSADigitalORGolay::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " - createRotationRequestMsg() not implemented for SA Digital or Golay LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;
}

/*-------------------------------------------------------------------------
  createMasterCycleRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of master cycle with the appropriate off time, period length.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSADigitalORGolay::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " - createMasterCycleRequestMsg() not implemented for SA Digital or Golay LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;

}

/*-------------------------------------------------------------------------
  restoreGuts
    
  Restore self's state from the given stream
  --------------------------------------------------------------------------*/
void CtiLMGroupSADigitalORGolay::restoreGuts(RWvistream& istrm)
{
    CtiLMGroupBase::restoreGuts( istrm );
}

/*---------------------------------------------------------------------------
  saveGuts
    
  Save self's state onto the given stream
  ---------------------------------------------------------------------------*/
void CtiLMGroupSADigitalORGolay::saveGuts(RWvostream& ostrm ) const  
{
    CtiLMGroupBase::saveGuts( ostrm );
}

/*---------------------------------------------------------------------------
  operator=
  ---------------------------------------------------------------------------*/
CtiLMGroupSADigitalORGolay& CtiLMGroupSADigitalORGolay::operator=(const CtiLMGroupSADigitalORGolay& right)
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
int CtiLMGroupSADigitalORGolay::operator==(const CtiLMGroupSADigitalORGolay& right) const
{
    return CtiLMGroupBase::operator==(right);
}

/*---------------------------------------------------------------------------
  operator!=
  ---------------------------------------------------------------------------*/
int CtiLMGroupSADigitalORGolay::operator!=(const CtiLMGroupSADigitalORGolay& right) const
{
    return CtiLMGroupBase::operator!=(right);
}

/*---------------------------------------------------------------------------
  replicate
    
  Restores self's operation fields
  ---------------------------------------------------------------------------*/
CtiLMGroupBase* CtiLMGroupSADigitalORGolay::replicate() const
{
    return (new CtiLMGroupSADigitalORGolay(*this));
}

/*---------------------------------------------------------------------------
  restore
    
  Restores given a RWDBReader
  ---------------------------------------------------------------------------*/
void CtiLMGroupSADigitalORGolay::restore(RWDBReader& rdr)
{
    CtiLMGroupBase::restore(rdr);
}

