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

extern ULONG _LM_DEBUG;

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
CtiRequestMsg* CtiLMGroupExpresscom::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    RWCString controlString = RWCString("control xcom shed ");
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
CtiRequestMsg* CtiLMGroupExpresscom::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const
{
    char tempchar[64];
    RWCString controlString = RWCString("control xcom cycle ");
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
CtiRequestMsg* CtiLMGroupExpresscom::createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const
{
    char tempchar[64];
    RWCString controlString = RWCString("control xcom cycle ");
    _ltoa(percent,tempchar,10);
    controlString += tempchar;
    controlString += " count ";
    _ltoa(defaultCount,tempchar,10);
    controlString += tempchar;
    controlString += " period ";
    controlString += buildPeriodString(period);
    controlString += " truecycle";

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
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
CtiRequestMsg* CtiLMGroupExpresscom::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    RWCString controlString = RWCString("control xcom shed ");
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
CtiRequestMsg* CtiLMGroupExpresscom::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{ 
    RWCString controlString = RWCString("control xcom shed ");
    controlString += buildShedString(offTime-60);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending master cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
    createThermoStatRequestMsg

    Creates a new CtiRequestMsg pointer for a thermostat program gear with
    all the appropriate settings.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupExpresscom::createSetPointRequestMsg(RWCString settings, LONG minValue, LONG maxValue,
                                                              LONG valueB, LONG valueD, LONG valueF, LONG random,
                                                              LONG valueTA, LONG valueTB, LONG valueTC, LONG valueTD,
                                                              LONG valueTE, LONG valueTF, int priority) const
{
    RWCString controlString = RWCString("control xcom setpoint ");

    settings.toUpper();
    if( settings.length() > 0 && settings[(size_t)0]=='D' )
    {
        controlString += "delta ";
    }
    if( settings.length() > 1 && settings[(size_t)1]=='C' )
    {
        controlString += "celsius ";
    }
    if( settings.length() > 3 && settings[(size_t)2]=='H' && settings[(size_t)3]=='I' )
    {
        controlString += "mode both ";
    }
    if( settings.length() > 2 && settings[(size_t)2]=='H' )
    {
        controlString += "mode heat ";
    }
    if( settings.length() > 3 && settings[(size_t)3]=='I' )
    {
        controlString += "mode cool ";
    }

    if( minValue != 0 )
    {
        controlString += "min ";
        char tempchar[64];
        _ltoa(minValue,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( maxValue != 0 )
    {
        controlString += "max ";
        char tempchar[64];
        _ltoa(maxValue,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( random != 0 )
    {
        controlString += "tr ";
        char tempchar[64];
        _ltoa(random,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( valueTA != 0 )
    {
        controlString += "ta ";
        char tempchar[64];
        _ltoa(valueTA,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( valueTB != 0 )
    {
        controlString += "tb ";
        char tempchar[64];
        _ltoa(valueTB,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( valueB != 0 )
    {
        controlString += "dsb ";
        char tempchar[64];
        _ltoa(valueB,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( valueTC != 0 )
    {
        controlString += "tc ";
        char tempchar[64];
        _ltoa(valueTC,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( valueTD != 0 )
    {
        controlString += "td ";
        char tempchar[64];
        _ltoa(valueTD,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( valueD != 0 )
    {
        controlString += "dsd ";
        char tempchar[64];
        _ltoa(valueD,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( valueTE != 0 )
    {
        controlString += "te ";
        char tempchar[64];
        _ltoa(valueTE,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( valueTF != 0 )
    {
        controlString += "tf ";
        char tempchar[64];
        _ltoa(valueTF,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }
    if( valueF != 0 )
    {
        controlString += "dsf ";
        char tempchar[64];
        _ltoa(valueF,tempchar,10);
        controlString += tempchar;
        controlString += " ";
    }

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending set point command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
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

