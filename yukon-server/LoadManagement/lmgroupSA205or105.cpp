/*---------------------------------------------------------------------------
  Filename:  lmgroupsa205or105.cpp

  Programmer:  Aaron Lauinger
        
  Description:    Source file for CtiLMGroupSA205OR105.
  CtiLMGroupSA205OR105 maintains the state and handles
  the persistence of sa 305 groups in Load Management.

  Initial Date:  3/6/2004
         
  COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2004
  ---------------------------------------------------------------------------*/
#include "yukon.h"

#include "lmgroupSA205or105.h"
#include "lmid.h"
#include "logger.h"
#include "dbaccess.h"

RWDEFINE_COLLECTABLE( CtiLMGroupSA205OR105, CTILMGROUPSA205OR105_ID )

/*---------------------------------------------------------------------------
  Constructors
  ---------------------------------------------------------------------------*/
    CtiLMGroupSA205OR105::CtiLMGroupSA205OR105()
{   
}

CtiLMGroupSA205OR105::CtiLMGroupSA205OR105(RWDBReader& rdr)
{
    restore(rdr);   
}

CtiLMGroupSA205OR105::CtiLMGroupSA205OR105(const CtiLMGroupSA205OR105& groupexp)
{
    operator=(groupexp);
}

/*---------------------------------------------------------------------------
  Destructor
  ---------------------------------------------------------------------------*/
CtiLMGroupSA205OR105::~CtiLMGroupSA205OR105()
{
}

/*-------------------------------------------------------------------------
  createTimeRefreshRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of time refresh with the appropriate refresh rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA205OR105::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " - createTimeRefreshRequestMsg() not implemented for SA205/105 LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;
}

/*-------------------------------------------------------------------------
  createSmartCycleRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of smart cycle with the appropriate cycle percent, period length
  in minutes, and the default count of periods.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA205OR105::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " - createSmartCycleRequestMsg() not implemented for SA205/105 LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;
}

/*-------------------------------------------------------------------------
  createTrueCycleRequestMsg

  Creates true cycle request msg which is exactly like a smart cycle but
  with the "truecycle" string at the end of the control string.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA205OR105::createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " - createTrueCycleRequestMsg() not implemented for SA205/105 LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;
}

/*-------------------------------------------------------------------------
  createRotationRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of rotation with the appropriate send rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA205OR105::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " - createRotationRequestMsg() not implemented for SA205/105 LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;
}

/*-------------------------------------------------------------------------
  createMasterCycleRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of master cycle with the appropriate off time, period length.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA205OR105::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " - createMasterCycleRequestMsg() not implemented for SA205/105 LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;

}

/*-------------------------------------------------------------------------
  restoreGuts
    
  Restore self's state from the given stream
  --------------------------------------------------------------------------*/
void CtiLMGroupSA205OR105::restoreGuts(RWvistream& istrm)
{
    CtiLMGroupBase::restoreGuts( istrm );
}

/*---------------------------------------------------------------------------
  saveGuts
    
  Save self's state onto the given stream
  ---------------------------------------------------------------------------*/
void CtiLMGroupSA205OR105::saveGuts(RWvostream& ostrm ) const  
{
    CtiLMGroupBase::saveGuts( ostrm );
}

/*---------------------------------------------------------------------------
  operator=
  ---------------------------------------------------------------------------*/
CtiLMGroupSA205OR105& CtiLMGroupSA205OR105::operator=(const CtiLMGroupSA205OR105& right)
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
int CtiLMGroupSA205OR105::operator==(const CtiLMGroupSA205OR105& right) const
{
    return CtiLMGroupBase::operator==(right);
}

/*---------------------------------------------------------------------------
  operator!=
  ---------------------------------------------------------------------------*/
int CtiLMGroupSA205OR105::operator!=(const CtiLMGroupSA205OR105& right) const
{
    return CtiLMGroupBase::operator!=(right);
}

/*---------------------------------------------------------------------------
  replicate
    
  Restores self's operation fields
  ---------------------------------------------------------------------------*/
CtiLMGroupBase* CtiLMGroupSA205OR105::replicate() const
{
    return (new CtiLMGroupSA205OR105(*this));
}

/*---------------------------------------------------------------------------
  restore
    
  Restores given a RWDBReader
  ---------------------------------------------------------------------------*/
void CtiLMGroupSA205OR105::restore(RWDBReader& rdr)
{
    CtiLMGroupBase::restore(rdr);
}

