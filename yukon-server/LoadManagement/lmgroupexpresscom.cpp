/*---------------------------------------------------------------------------
        Filename:  lmgroupexpress.cpp

        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiLMGroupExpresscom.
                        CtiLMGroupExpresscom maintains the state and handles
                        the persistence of expresscom groups in Load Management.

        Initial Date:  2/9/2001
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmgroupexpresscom.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"

extern BOOL _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMGroupExpresscom, CTILMGROUPEXPRESSCOM_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom::CtiLMGroupExpresscom()
{   
}

CtiLMGroupExpresscom::CtiLMGroupExpresscom(RWDBReader& rdr)
{
    restore(rdr);   
}

CtiLMGroupExpresscom::CtiLMGroupExpresscom(const CtiLMGroupExpresscom& groupexp)
{
    operator=(groupexp);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom::~CtiLMGroupExpresscom()
{
}


/*-------------------------------------------------------------------------
    createTimeRefreshRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of time refresh with the appropriate refresh rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupExpresscom::createTimeRefreshRequestMsg(ULONG refreshRate, ULONG shedTime, int priority) const
{
    RWCString controlString = RWCString("control xcom shed ");
    controlString += convertSecondsToEvenTimeString(shedTime);

    if( _LM_DEBUG )
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
CtiRequestMsg* CtiLMGroupExpresscom::createSmartCycleRequestMsg(ULONG percent, ULONG period, ULONG defaultCount, int priority) const
{
    char tempchar[64];
    RWCString controlString = RWCString("control xcom cycle ");
    _ultoa(percent,tempchar,10);
    controlString += tempchar;
    controlString += " count ";
    _ultoa(defaultCount,tempchar,10);
    controlString += tempchar;
    controlString += " period ";
    controlString += convertSecondsToEvenTimeString(period);

    if( _LM_DEBUG )
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
CtiRequestMsg* CtiLMGroupExpresscom::createTrueCycleRequestMsg(ULONG percent, ULONG period, ULONG defaultCount, int priority) const
{
    char tempchar[64];
    RWCString controlString = RWCString("control xcom cycle ");
    _ultoa(percent,tempchar,10);
    controlString += tempchar;
    controlString += " count ";
    _ultoa(defaultCount,tempchar,10);
    controlString += tempchar;
    controlString += " period ";
    controlString += convertSecondsToEvenTimeString(period);
    controlString += " truecycle";

    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending true cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
    createRotationRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of rotation with the appropriate send rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupExpresscom::createRotationRequestMsg(ULONG sendRate, ULONG shedTime, int priority) const
{
    RWCString controlString = RWCString("control xcom shed ");
    controlString += convertSecondsToEvenTimeString(shedTime);

    if( _LM_DEBUG )
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
CtiRequestMsg* CtiLMGroupExpresscom::createMasterCycleRequestMsg(ULONG offTime, ULONG period, int priority) const
{
    RWCString controlString = RWCString("control xcom shed ");
    ULONG shedTime = 450;
    if( offTime > 570 && offTime <= 1220 )
    {
        shedTime = 900;
    }
    else if( offTime > 1220 && offTime <= 1920 )
    {
        shedTime = 1800;
    }
    else if( offTime > 1920 )
    {
        shedTime = 3600;
    }

    controlString += convertSecondsToEvenTimeString(shedTime);

    if( _LM_DEBUG )
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
void CtiLMGroupExpresscom::restoreGuts(RWvistream& istrm)
{
    CtiLMGroupBase::restoreGuts( istrm );
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMGroupExpresscom::saveGuts(RWvostream& ostrm ) const  
{
    CtiLMGroupBase::saveGuts( ostrm );
    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom& CtiLMGroupExpresscom::operator=(const CtiLMGroupExpresscom& right)
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
int CtiLMGroupExpresscom::operator==(const CtiLMGroupExpresscom& right) const
{

    return CtiLMGroupBase::operator==(right);
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMGroupExpresscom::operator!=(const CtiLMGroupExpresscom& right) const
{

    return CtiLMGroupBase::operator!=(right);
}

/*---------------------------------------------------------------------------
    replicate
    
    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMGroupBase* CtiLMGroupExpresscom::replicate() const
{
    return (new CtiLMGroupExpresscom(*this));
}

/*---------------------------------------------------------------------------
    restore
    
    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMGroupExpresscom::restore(RWDBReader& rdr)
{
    CtiLMGroupBase::restore(rdr);
}

