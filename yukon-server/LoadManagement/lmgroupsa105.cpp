/*---------------------------------------------------------------------------
  Filename:  lmgroupsa105.cpp

  Programmer:  Aaron Lauinger
        
  Description:    Source file for CtiLMGroupSA105.
  CtiLMGroupSA105 maintains the state and handles
  the persistence of sa 305 groups in Load Management.

  Initial Date:  3/6/2004
         
  COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2004
  ---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "lmgroupsa105.h"
#include "lmid.h"
#include "logger.h"
#include "dbaccess.h"

RWDEFINE_COLLECTABLE( CtiLMGroupSA105, CTILMGROUPSA105_ID )

/*---------------------------------------------------------------------------
  Constructors
  ---------------------------------------------------------------------------*/
    CtiLMGroupSA105::CtiLMGroupSA105()
{   
}

CtiLMGroupSA105::CtiLMGroupSA105(RWDBReader& rdr)
{
    restore(rdr);   
}

CtiLMGroupSA105::CtiLMGroupSA105(const CtiLMGroupSA105& groupexp)
{
    operator=(groupexp);
}

/*---------------------------------------------------------------------------
  Destructor
  ---------------------------------------------------------------------------*/
CtiLMGroupSA105::~CtiLMGroupSA105()
{
}

/*-------------------------------------------------------------------------
  createTimeRefreshRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of time refresh with the appropriate refresh rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA105::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " - createTimeRefreshRequestMsg() not implemented for sa105 LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;
}

/*-------------------------------------------------------------------------
  createSmartCycleRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of smart cycle with the appropriate cycle percent, period length
  in minutes, and the default count of periods.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA105::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " - createSmartCycleRequestMsg() not implemented for sa105 LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;
}

/*-------------------------------------------------------------------------
  createTrueCycleRequestMsg

  Creates true cycle request msg which is exactly like a smart cycle but
  with the "truecycle" string at the end of the control string.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA105::createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " - createTrueCycleRequestMsg() not implemented for sa105 LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;
}

/*-------------------------------------------------------------------------
  createRotationRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of rotation with the appropriate send rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA105::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " - createRotationRequestMsg() not implemented for sa105 LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;
}

/*-------------------------------------------------------------------------
  createMasterCycleRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of master cycle with the appropriate off time, period length.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA105::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " - createMasterCycleRequestMsg() not implemented for sa105 LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;

}

/*-------------------------------------------------------------------------
  restoreGuts
    
  Restore self's state from the given stream
  --------------------------------------------------------------------------*/
void CtiLMGroupSA105::restoreGuts(RWvistream& istrm)
{
    CtiLMGroupBase::restoreGuts( istrm );
}

/*---------------------------------------------------------------------------
  saveGuts
    
  Save self's state onto the given stream
  ---------------------------------------------------------------------------*/
void CtiLMGroupSA105::saveGuts(RWvostream& ostrm ) const  
{
    CtiLMGroupBase::saveGuts( ostrm );
}

/*---------------------------------------------------------------------------
  operator=
  ---------------------------------------------------------------------------*/
CtiLMGroupSA105& CtiLMGroupSA105::operator=(const CtiLMGroupSA105& right)
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
int CtiLMGroupSA105::operator==(const CtiLMGroupSA105& right) const
{
    return CtiLMGroupBase::operator==(right);
}

/*---------------------------------------------------------------------------
  operator!=
  ---------------------------------------------------------------------------*/
int CtiLMGroupSA105::operator!=(const CtiLMGroupSA105& right) const
{
    return CtiLMGroupBase::operator!=(right);
}

/*---------------------------------------------------------------------------
  replicate
    
  Restores self's operation fields
  ---------------------------------------------------------------------------*/
CtiLMGroupBase* CtiLMGroupSA105::replicate() const
{
    return (new CtiLMGroupSA105(*this));
}

/*---------------------------------------------------------------------------
  restore
    
  Restores given a RWDBReader
  ---------------------------------------------------------------------------*/
void CtiLMGroupSA105::restore(RWDBReader& rdr)
{
    CtiLMGroupBase::restore(rdr);
}

