/*---------------------------------------------------------------------------
        Filename:  lmgrouppoint.cpp

        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiLMGroupPoint.
                        CtiLMGroupPoint maintains the state and handles
                        the persistence of emetcon groups in Load Management.

        Initial Date:  3/12/2002
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmgrouppoint.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"

extern BOOL _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMGroupPoint, CTILMGROUPPOINT_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMGroupPoint::CtiLMGroupPoint()
{   
}

CtiLMGroupPoint::CtiLMGroupPoint(RWDBReader& rdr)
{
    restore(rdr);   
}

CtiLMGroupPoint::CtiLMGroupPoint(const CtiLMGroupPoint& grouppoint)
{
    operator=(grouppoint);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMGroupPoint::~CtiLMGroupPoint()
{
}

/*---------------------------------------------------------------------------
    getDeviceIdUsage

    Returns the Device Id Usage of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupPoint::getDeviceIdUsage() const
{

    return _deviceidusage;
}

/*---------------------------------------------------------------------------
    getPointIdUsage

    Returns the Point Id Usage of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupPoint::getPointIdUsage() const
{

    return _pointidusage;
}

/*---------------------------------------------------------------------------
    getStartControlRawState

    Returns the Start Control Raw State of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupPoint::getStartControlRawState() const
{

    return _startcontrolrawstate;
}

/*---------------------------------------------------------------------------
    setDeviceIdUsage

    Sets the Device Id Usage of the group
---------------------------------------------------------------------------*/
CtiLMGroupPoint& CtiLMGroupPoint::setDeviceIdUsage(ULONG deviduse)
{

    _deviceidusage = deviduse;
    return *this;
}

/*---------------------------------------------------------------------------
    setPointIdUsage

    Sets the Point Id Usage of the group
---------------------------------------------------------------------------*/
CtiLMGroupPoint& CtiLMGroupPoint::setPointIdUsage(ULONG pointiduse)
{

    _pointidusage = pointiduse;
    return *this;
}

/*---------------------------------------------------------------------------
    setStartControlRawState

    Sets the Start Control Raw State of the group
---------------------------------------------------------------------------*/
CtiLMGroupPoint& CtiLMGroupPoint::setStartControlRawState(ULONG startcontrolstate)
{

    _startcontrolrawstate = startcontrolstate;
    return *this;
}


/*-------------------------------------------------------------------------
    createTimeRefreshRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of time refresh with the appropriate refresh rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupPoint::createTimeRefreshRequestMsg(ULONG refreshRate, ULONG shedTime, int priority) const
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Can not do this to an Load Management Point Group, in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return NULL;
}

/*-------------------------------------------------------------------------
    createSmartCycleRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of smart cycle with the appropriate cycle percent, period length,
    and the default count of periods.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupPoint::createSmartCycleRequestMsg(ULONG percent, ULONG period, ULONG defaultCount, int priority) const
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Can not do this to an Load Management Point Group, in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return NULL;
}

/*-------------------------------------------------------------------------
    createRotationRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of rotation with the appropriate send rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupPoint::createRotationRequestMsg(ULONG sendRate, ULONG shedTime, int priority) const
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Can not do this to an Load Management Point Group, in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return NULL;
}

/*-------------------------------------------------------------------------
    createMasterCycleRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of master cycle with the appropriate off time, period length.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupPoint::createMasterCycleRequestMsg(ULONG offTime, ULONG period, int priority) const
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Can not do this to an Load Management Point Group, in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return NULL;
}

/*-------------------------------------------------------------------------
    createLatchingRequestMsg

    .
--------------------------------------------------------------------------*/
CtiCommandMsg* CtiLMGroupPoint::createLatchingRequestMsg(ULONG rawState, int priority) const
{
    CtiCommandMsg* returnCommandMsg = new CtiCommandMsg();
    returnCommandMsg->setOperation(CtiCommandMsg::ControlRequest);

    RWTValOrderedVector<RWInteger> opArgList;
    opArgList.insert(RWInteger(-1));
    opArgList.insert(RWInteger(getDeviceIdUsage()));
    opArgList.insert(RWInteger(getPointIdUsage()));
    opArgList.insert(RWInteger(rawState));

    returnCommandMsg->setOpArgList(opArgList);

    returnCommandMsg->setMessagePriority(priority);

    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending point latch command, LM Group: " << getPAOName() << ", raw state: " << rawState << ", priority: " << priority << endl;
    }
    return returnCommandMsg;
}

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMGroupPoint::restoreGuts(RWvistream& istrm)
{



    CtiLMGroupBase::restoreGuts( istrm );

    istrm >> _deviceidusage
          >> _pointidusage
          >> _startcontrolrawstate;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMGroupPoint::saveGuts(RWvostream& ostrm ) const  
{


        
    CtiLMGroupBase::saveGuts( ostrm );

    ostrm << _deviceidusage
          << _pointidusage
          << _startcontrolrawstate;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMGroupPoint& CtiLMGroupPoint::operator=(const CtiLMGroupPoint& right)
{


    if( this != &right )
    {
        CtiLMGroupBase::operator=(right);
        _deviceidusage = right._deviceidusage;
        _pointidusage = right._pointidusage;
        _startcontrolrawstate = right._startcontrolrawstate;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMGroupPoint::operator==(const CtiLMGroupPoint& right) const
{

    return CtiLMGroupBase::operator==(right);
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMGroupPoint::operator!=(const CtiLMGroupPoint& right) const
{

    return CtiLMGroupBase::operator!=(right);
}

/*---------------------------------------------------------------------------
    replicate
    
    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMGroupBase* CtiLMGroupPoint::replicate() const
{
    return (new CtiLMGroupPoint(*this));
}

/*---------------------------------------------------------------------------
    restore
    
    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMGroupPoint::restore(RWDBReader& rdr)
{


    CtiLMGroupBase::restore(rdr);
}

/*---------------------------------------------------------------------------
    restorePointSpecificDatabaseEntries
    
    Restores the database entries for a point group that are not contained
    in the base table.
---------------------------------------------------------------------------*/
void CtiLMGroupPoint::restorePointSpecificDatabaseEntries(RWDBReader& rdr)
{


    rdr["deviceidusage"] >> _deviceidusage;
    rdr["pointidusage"] >> _pointidusage;
    rdr["startcontrolrawstate"] >> _startcontrolrawstate;
}

