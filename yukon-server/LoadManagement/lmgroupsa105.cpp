/*---------------------------------------------------------------------------
  Filename:  lmgroupsa105.cpp

  Programmer:  Aaron Lauinger

  Description:    Source file for CtiLMGroupSA105.
  CtiLMGroupSA105 maintains the state and handles
  the persistence of sa 305 groups in Load Management.

  Initial Date:  3/6/2004

  COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2004
  ---------------------------------------------------------------------------*/
#include "precompiled.h"

#include "lmgroupsa105.h"
#include "lmid.h"
#include "logger.h"
#include "dbaccess.h"

using std::string;
using std::endl;

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMGroupSA105, CTILMGROUPSA105_ID )

/*---------------------------------------------------------------------------
  Constructors
  ---------------------------------------------------------------------------*/
    CtiLMGroupSA105::CtiLMGroupSA105()
{
}

CtiLMGroupSA105::CtiLMGroupSA105(Cti::RowReader &rdr)
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
    string controlString("control sa105 shed ");
    controlString += buildShedString(shedTime);

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
CtiRequestMsg* CtiLMGroupSA105::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    char tempchar[64];
    string controlString("control sa105 cycle ");
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
        dout << CtiTime() << " - Sending smart cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
  createTrueCycleRequestMsg

  Creates true cycle request msg which is exactly like a smart cycle but
  with the "truecycle" string at the end of the control string.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA105::createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << CtiTime() << " - createTrueCycleRequestMsg() not implemented for sa105 LM Groups " << __FILE__ << " at:" << __LINE__ << endl;
    return NULL;
}

/*-------------------------------------------------------------------------
  createRotationRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of rotation with the appropriate send rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA105::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    string controlString("control sa105 shed ");
    controlString += buildShedString(shedTime);

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
CtiRequestMsg* CtiLMGroupSA105::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    string controlString("control sa105 shed ");
    controlString += buildShedString(offTime);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending master cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
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

  Restores given a Reader
  ---------------------------------------------------------------------------*/
void CtiLMGroupSA105::restore(Cti::RowReader &rdr)
{
    CtiLMGroupBase::restore(rdr);
}

