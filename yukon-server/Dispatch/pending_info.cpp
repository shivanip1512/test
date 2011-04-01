#include "yukon.h"

#include "logger.h"
#include "pending_info.h"

using namespace std;

/*-----------------------------------------------------------------------------*
*
* File:   pending_info
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/pending_info.cpp-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2006/06/16 20:05:17 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


CtiPendingPointOperations::CtiPendingPointOperations(LONG id, INT type) :
_pid(id),
_pendingType(type),
_signal(NULL),
_pdata(NULL),
_limitBeingTimed(-1),
_limitDuration(0),
_time(YUKONEOT),
_controlState( noControl ),
_controlTimeout(300),
_controlCompleteValue(-1.0),
_controlCompleteDeadband(0),
_excludeFromHistory(false),
_associationKey(0)
{
}

CtiPendingPointOperations::CtiPendingPointOperations(const CtiPendingPointOperations& aRef) :
_pendingType(pendingInvalid),
_controlCompleteValue(-1.0),
_pdata(NULL),
_signal(NULL)
{
    *this = aRef;
}


void CtiPendingPointOperations::dump() const
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << " Point ID                  " << getPointID() << endl;
        dout << " PPO Time                  " << getTime() << endl;

        switch(getType())
        {
        case pendingLimit:
        case pendingLimit + 1:
        case pendingLimit + 2:
        case pendingLimit + 3:
        case pendingLimit + 4:
        case pendingLimit + 5:
        case pendingLimit + 6:
        case pendingLimit + 7:
        case pendingLimit + 8:
        case pendingLimit + 9:
            {
                dout << " PPO Type                  pendingLimit " << getType() - pendingLimit + 1 << endl;
                dout << " PPO Limit Timed           " << getLimitBeingTimed() << endl;
                break;
            }
        case pendingControl:
            {
                dout << " PPO Type                  pendingControl" << endl;
                dout << " PPO Control State         " << getControlState() << endl;
                getControl().dump();
                break;
            }
        case pendingPointData:
            {
                dout << " PPO Type                  pendingPointData" << endl;
                break;
            }
        }
    }
}

LONG CtiPendingPointOperations::getPointID() const
{
    return _pid;
}
INT CtiPendingPointOperations::getType() const
{
    return _pendingType;
}
INT CtiPendingPointOperations::getLimitBeingTimed() const
{
    return _limitBeingTimed;
}
CtiTime CtiPendingPointOperations::getTime() const
{
    return _time;
}
CtiTime& CtiPendingPointOperations::getTime()
{
    return _time;
}
UINT CtiPendingPointOperations::getLimitDuration() const
{
    return _limitDuration;
}

INT CtiPendingPointOperations::getControlState() const
{
    return _controlState;
}

UINT CtiPendingPointOperations::getControlTimeout() const
{
    return _controlTimeout;
}

CtiPendingPointOperations& CtiPendingPointOperations::setPointID( LONG id )
{
    _pid = id;
    return *this;
}
CtiPendingPointOperations& CtiPendingPointOperations::setType( INT type )
{
    _pendingType = type;
    return *this;
}
CtiPendingPointOperations& CtiPendingPointOperations::setLimitBeingTimed( INT lim )
{
    _limitBeingTimed = lim;
    return *this;
}
CtiPendingPointOperations& CtiPendingPointOperations::setTime(const CtiTime& rt )
{
    _time = rt;
    return *this;
}
CtiPendingPointOperations& CtiPendingPointOperations::setLimitDuration( UINT ld )
{
    _limitDuration = ld;
    return *this;
}
CtiPendingPointOperations& CtiPendingPointOperations::setControlState(INT cs)
{
    _controlState = cs;
    return *this;
}
CtiPendingPointOperations& CtiPendingPointOperations::setControlTimeout( UINT ld )
{
    _controlTimeout = ld;
    return *this;
}

CtiSignalMsg* CtiPendingPointOperations::getSignal()
{
    return _signal;
}

const CtiSignalMsg* CtiPendingPointOperations::getSignal() const
{
    return _signal;
}

CtiPendingPointOperations& CtiPendingPointOperations::setSignal( CtiSignalMsg *pSig )
{
    if(_signal != NULL) delete _signal;
    _signal = pSig;

    return *this;
}

CtiPointDataMsg* CtiPendingPointOperations::getPointData()
{
    return _pdata;
}

const CtiPointDataMsg* CtiPendingPointOperations::getPointData() const
{
    return _pdata;
}

CtiPendingPointOperations& CtiPendingPointOperations::setPointData( CtiPointDataMsg *pDat )
{
    if(_pdata != NULL) delete _pdata;
    _pdata = pDat;

    return *this;
}

DOUBLE CtiPendingPointOperations::getControlCompleteValue() const
{
    return _controlCompleteValue;
}
DOUBLE CtiPendingPointOperations::getControlCompleteDeadband() const
{
    return _controlCompleteDeadband;
}
bool CtiPendingPointOperations::getExcludeFromHistory() const
{
    return _excludeFromHistory;
}
int CtiPendingPointOperations::getAssociationKey() const
{
    return _associationKey;
}
CtiPendingPointOperations& CtiPendingPointOperations::setControlCompleteValue( const DOUBLE &aDbl )
{
    _controlCompleteValue = aDbl;
    return *this;
}
CtiPendingPointOperations& CtiPendingPointOperations::setControlCompleteDeadband( const DOUBLE &aDbl )
{
    _controlCompleteDeadband = aDbl;
    return *this;
}
CtiPendingPointOperations& CtiPendingPointOperations::setExcludeFromHistory( const bool exclude )
{
    _excludeFromHistory = exclude;
    return *this;
}

CtiPendingPointOperations& CtiPendingPointOperations::setAssociationKey( const int key )
{
    _associationKey = key;
    return *this;
}

CtiPendingPointOperations::~CtiPendingPointOperations()
{
    if(_signal != NULL)
    {
        delete _signal;
        _signal = NULL;
    }
    if(_pdata != NULL)
    {
        delete _pdata;
        _pdata = NULL;
    }
}

CtiPendingPointOperations& CtiPendingPointOperations::operator=(const CtiPendingPointOperations& aRef)
{
    if(this != &aRef)
    {
        _pid = aRef.getPointID();
        _pendingType = aRef.getType();
        _time = aRef.getTime();
        _limitBeingTimed = aRef.getLimitBeingTimed();
        _limitDuration = aRef.getLimitDuration();
        _controlState = aRef.getControlState();
        _controlTimeout = aRef.getControlTimeout();
        _controlCompleteValue = aRef.getControlCompleteValue();
        _controlCompleteDeadband = aRef.getControlCompleteDeadband();
        _control = aRef.getControl();
        _excludeFromHistory = aRef.getExcludeFromHistory();
        _associationKey = aRef.getAssociationKey();

        _opidMap = aRef.getOffsetMap();

        if(_signal != NULL)
        {
            delete _signal;
            _signal = NULL;
        }

        if(_pdata != NULL)
        {
            delete _pdata;
            _pdata = NULL;
        }

        if(aRef.getSignal() != NULL)
        {
            _signal = (CtiSignalMsg*)(aRef.getSignal()->replicateMessage());
        }

        if(aRef.getPointData() != NULL)
        {
            _pdata = (CtiPointDataMsg*)(aRef.getPointData()->replicateMessage());
        }
    }
    return *this;
}

bool CtiPendingPointOperations::operator==(const CtiPendingPointOperations& y) const
{
    return(getPointID() == y.getPointID());
}

bool CtiPendingPointOperations::operator()(const CtiPendingPointOperations& y) const
{
    return operator<(y);    // *this < y
}

bool CtiPendingPointOperations::operator<(const CtiPendingPointOperations& y) const
{
    CtiTime xt = getTime();
    CtiTime yt = y.getTime();

    bool isLess = false;

    if(getPointID() < y.getPointID())
    {
        isLess = true;
    }
    else if(getPointID() == y.getPointID() && getType() < y.getType())
    {
        isLess = true;
    }
    else if(getPointID() == y.getPointID() && getType() == y.getType())
    {
#if 1
        isLess = false;
#else
        if(getType() == CtiPendingPointOperations::pendingLimit)
        {
            CtiTime maxTime(YUKONEOT);
            xt = getTime() + getLimitDuration();
            yt = y.getTime() + y.getLimitDuration();
            isLess = xt < yt;
        }
        else if( (getType() == CtiPendingPointOperations::pendingControl) ||
                 (getType() == CtiPendingPointOperations::pendingPointData) )
        {
            isLess = false;
        }
#endif
    }

/*
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << " XPID " << getPointID() << " Type " << getType() << " Time " << xt << endl;
        dout << " YPID " << y.getPointID() << " Type " << y.getType() << " Time " << yt << endl;
    }
*/
    return( isLess );
}

const CtiTableLMControlHistory& CtiPendingPointOperations::getControl() const
{
    return _control;
}
CtiTableLMControlHistory& CtiPendingPointOperations::getControl()
{
    return _control;
}
CtiPendingPointOperations& CtiPendingPointOperations::setControl(const CtiTableLMControlHistory& ref)
{
    _control = ref;
    return *this;
}

LONG CtiPendingPointOperations::getOffsetsPointID(int offset)
{
    LONG pid = 0;
    CtiOffsetPIDMap_t::iterator itr = _opidMap.find(offset);

    if(itr != _opidMap.end())
    {
        pid = (*itr).second;
    }

    return pid;
}

CtiPendingPointOperations::CtiOffsetPIDMap_t CtiPendingPointOperations::getOffsetMap() const
{
    return _opidMap;
}

CtiPendingPointOperations& CtiPendingPointOperations::setOffsetMap(CtiPendingPointOperations::CtiOffsetPIDMap_t &aMap)
{
    _opidMap = aMap;
    return *this;
}


/*
 *  This map attempts to keep track of the
 */
void CtiPendingPointOperations::addOffset(int offset, long pid)
{
    pair< CtiOffsetPIDMap_t::iterator, bool > insertpair  = _opidMap.insert( CtiOffsetPIDMap_t::value_type(make_pair(offset, pid)) );

    if(insertpair.second == false)      // This is a collision!
    {
        CtiOffsetPIDMap_t::iterator &itr = insertpair.first;
        long &opid = (*itr).second;
        opid = pid;
    }

    return;
}

void CtiPendingPointOperations::setLastHistoryPost(CtiTime rwt)
{
    _lastHistoryPost = rwt;
}

CtiTime CtiPendingPointOperations::getLastHistoryPost() const
{
    return _lastHistoryPost;
}

bool CtiPendingPointOperations::isInControlCompleteState( DOUBLE &value )
{
    bool retVal = false;

    if( (_controlCompleteDeadband + _controlCompleteValue) >= value && (_controlCompleteValue - _controlCompleteDeadband) <= value )
    {
        retVal = true;
    }
    return retVal;
}
