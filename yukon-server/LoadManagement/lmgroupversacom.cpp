/*---------------------------------------------------------------------------
        Filename:  lmgroupversacom.cpp

        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiLMGroupVersacom.
                        CtiLMGroupVersacom maintains the state and handles
                        the persistence of versacom groups in Load Management.

        Initial Date:  2/9/2001
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmgroupbase.h"
#include "lmgroupversacom.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMGroupVersacom, CTILMGROUPVERSACOM_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMGroupVersacom::CtiLMGroupVersacom()
{   
}

CtiLMGroupVersacom::CtiLMGroupVersacom(RWDBReader& rdr)
{
    restore(rdr);   
}

CtiLMGroupVersacom::CtiLMGroupVersacom(const CtiLMGroupVersacom& groupversa)
{
    operator=(groupversa);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMGroupVersacom::~CtiLMGroupVersacom()
{
}


/*-------------------------------------------------------------------------
    createTimeRefreshRequestMsg

    Creates a new CtiPILRequestMsg pointer for a program gear with a control
    method of time refresh with the appropriate refresh rate and shed time.
--------------------------------------------------------------------------*/
CtiPILRequestMsg* CtiLMGroupVersacom::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    RWCString controlString = RWCString("control shed ");
    controlString += convertSecondsToEvenTimeString(shedTime);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending time refresh command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return new CtiPILRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
    createSmartCycleRequestMsg

    Creates a new CtiPILRequestMsg pointer for a program gear with a control
    method of smart cycle with the appropriate cycle percent, period length
    in minutes, and the default count of periods.
--------------------------------------------------------------------------*/
CtiPILRequestMsg* CtiLMGroupVersacom::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const
{
    char tempchar[64];
    RWCString controlString = RWCString("control cycle ");
    _ltoa(percent,tempchar,10);
    controlString += tempchar;
    controlString += " count ";
    _ltoa(defaultCount,tempchar,10);
    controlString += tempchar;
    controlString += " period ";
    controlString += convertSecondsToEvenTimeString(period);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending smart cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return new CtiPILRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
    createRotationRequestMsg

    Creates a new CtiPILRequestMsg pointer for a program gear with a control
    method of rotation with the appropriate send rate and shed time.
--------------------------------------------------------------------------*/
CtiPILRequestMsg* CtiLMGroupVersacom::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    RWCString controlString = RWCString("control shed ");
    controlString += convertSecondsToEvenTimeString(shedTime);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending rotation command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return new CtiPILRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
    createMasterCycleRequestMsg

    Creates a new CtiPILRequestMsg pointer for a program gear with a control
    method of master cycle with the appropriate off time, period length.
--------------------------------------------------------------------------*/
CtiPILRequestMsg* CtiLMGroupVersacom::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    RWCString controlString = RWCString("control shed ");
    controlString += convertSecondsToEvenTimeString(offTime-60);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending master cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return new CtiPILRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMGroupVersacom::restoreGuts(RWvistream& istrm)
{
    CtiLMGroupBase::restoreGuts( istrm );
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMGroupVersacom::saveGuts(RWvostream& ostrm ) const  
{
    CtiLMGroupBase::saveGuts( ostrm );
    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMGroupVersacom& CtiLMGroupVersacom::operator=(const CtiLMGroupVersacom& right)
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
int CtiLMGroupVersacom::operator==(const CtiLMGroupVersacom& right) const
{

    return CtiLMGroupBase::operator==(right);
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMGroupVersacom::operator!=(const CtiLMGroupVersacom& right) const
{

    return CtiLMGroupBase::operator!=(right);
}

/*---------------------------------------------------------------------------
    replicate
    
    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMGroupBase* CtiLMGroupVersacom::replicate() const
{
    return (new CtiLMGroupVersacom(*this));
}

/*---------------------------------------------------------------------------
    restore
    
    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMGroupVersacom::restore(RWDBReader& rdr)
{
    CtiLMGroupBase::restore(rdr);
}

