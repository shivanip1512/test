/*-----------------------------------------------------------------------------*
*
* File:   dev_exclusion
*
* Date:   2/27/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2005/02/10 23:23:59 $
*
* HISTORY      :
* $Log: dev_exclusion.cpp,v $
* Revision 1.9  2005/02/10 23:23:59  alauinger
* Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build
*
* Revision 1.8  2004/12/08 21:21:39  cplender
* Make certain the CycleTime is > 0 on the "next" calculation
*
* Revision 1.7  2004/08/18 22:05:43  cplender
* Changed hasTimeExclusion to be correct fro commented out funcparams.
*
* Revision 1.6  2004/07/08 23:15:37  cplender
* Added get/setMinTimeInSec()
*
* Revision 1.5  2004/05/20 22:42:30  cplender
* Various exclusion changes
*
* Revision 1.4  2004/05/19 14:48:53  cplender
* Exclusion changes
*
* Revision 1.3  2004/05/10 21:35:50  cplender
* Exclusions a'la GRE are a bit closer here.  The proximity exclusions should work ok now.
*
* Revision 1.2  2004/04/29 20:22:38  cplender
* IR
*
* Revision 1.1.2.1  2004/04/15 23:30:19  cplender
* IR
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "cparms.h"
#include "dev_exclusion.h"
#include "guard.h"
#include "logger.h"
#include "utility.h"


CtiDeviceExclusion::CtiDeviceExclusion(LONG id) :
_deviceId(id),
_minTimeInSec(0)
{
}

CtiDeviceExclusion::CtiDeviceExclusion(const CtiDeviceExclusion& aRef)
{
    *this = aRef;
}

CtiDeviceExclusion::~CtiDeviceExclusion()
{
}

CtiDeviceExclusion& CtiDeviceExclusion::operator=(const CtiDeviceExclusion& aRef)
{
    if(this != &aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    return *this;
}

CtiDeviceExclusion& CtiDeviceExclusion::setId(LONG id)
{
    _deviceId = id;
    return *this;
}

LONG CtiDeviceExclusion::getId() const
{
    return _deviceId;
}

bool CtiDeviceExclusion::hasExclusions() const
{
    bool bstatus = false;

    try
    {
        CtiLockGuard<CtiMutex> ex_guard(_exclusionMux, 5000);

        if(ex_guard.isAcquired())
        {
            bstatus = _exclusionRecords.size() != 0 || (_cycleTimeExclusion.getFunctionId() == CtiTablePaoExclusion::ExFunctionCycleTime);
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << "  " << getId() << " unable to acquire exclusion mutex: hasExclusions()" << endl;
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return bstatus;
}

CtiDeviceExclusion::exclusions CtiDeviceExclusion::getExclusions() const
{
    return _exclusionRecords;
}
void CtiDeviceExclusion::addExclusion(CtiTablePaoExclusion &paox)
{
    try
    {
        CtiLockGuard<CtiMutex> guard(_exclusionMux, 30000);

        if(guard.isAcquired())
        {
            if(paox.getFunctionId() == CtiTablePaoExclusion::ExFunctionCycleTime)
            {
                _cycleTimeExclusion = paox;                     // Store this in a special slot to make it quicker to discover.  Only one of these.
            }
            else
            {
                _exclusionRecords.push_back(paox);
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << getId() << " unable to acquire exclusion mutex: addExclusion()" << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return;
}

void CtiDeviceExclusion::clearExclusions()
{
    try
    {
        CtiLockGuard<CtiMutex> guard(_exclusionMux, 15000);

        if(guard.isAcquired())
        {
            _cycleTimeExclusion.setFunctionId( CtiTablePaoExclusion::ExFunctionInvalid );
            _exclusionRecords.clear();
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << getId() << " unable to acquire exclusion mutex: clearExclusions()" << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return;
}



/*
 *  Check if the passed id is in the exclusion list?
 */
bool CtiDeviceExclusion::isDeviceExcluded(long id) const
{
    bool bstatus = false;

    try
    {
        CtiLockGuard<CtiMutex> guard(_exclusionMux, 5000);

        if(guard.isAcquired())
        {
            if(hasExclusions())
            {
                exclusions::const_iterator itr;

                for(itr = _exclusionRecords.begin(); itr != _exclusionRecords.end(); itr++)
                {
                    const CtiTablePaoExclusion &paox = *itr;

                    if(paox.getExcludedPaoId() == id)
                    {
                        bstatus = true;
                        break;
                    }
                }
            }
        }
        else
        {
            bstatus = true;

            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << getId() << " unable to acquire exclusion mutex: isDeviceExcluded()" << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCLUSION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return bstatus;
}

bool CtiDeviceExclusion::isExecuting() const
{
    return(_executingUntil > _executingUntil.now());
}

void CtiDeviceExclusion::setExecuting(bool set)
{
    if(set)
        _executingUntil = RWTime(YUKONEOT);
    else
    {
        if(_executingUntil == RWTime(YUKONEOT))
        {
            _executingUntil = RWTime(rwEpoch);
        }
    }

    return;
}

RWTime CtiDeviceExclusion::getExecutingUntil() const
{
    return _executingUntil;
}

void CtiDeviceExclusion::setExecutingUntil(RWTime set)
{
    #if 0
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << " Executing until " << set << endl;
    }
    #endif

    _executingUntil = set;
    return;
}

bool CtiDeviceExclusion::isExecutionProhibited(const RWTime &now, LONG did)
{
    bool prohibited = false;

    if(_executionProhibited.size() != 0)
    {
        try
        {
            CtiDeviceExclusion::prohibitions::iterator itr;
            CtiLockGuard<CtiMutex> guard(_exclusionMux, 5000);
            if(guard.isAcquired())
            {
                for(itr = _executionProhibited.begin(); itr != _executionProhibited.end(); )
                {
                    if((*itr).second > now)
                    {
                        if(!did || (did && (*itr).second == did))
                        {
                            prohibited = true;
                        }
                        itr++;
                    }
                    else
                    {
                        itr = _executionProhibited.erase(itr);      // Removes any time exclusions which have expired.
                    }
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << getId() << " unable to acquire exclusion mutex: isExecutionProhibited()" << endl;
                }
                prohibited = true;
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
    return prohibited;
}

size_t CtiDeviceExclusion::setExecutionProhibited(unsigned long id, RWTime& releaseTime)
{
    size_t cnt = 0;

    try
    {
        CtiLockGuard<CtiMutex> guard(_exclusionMux, 5000);
        if(guard.isAcquired())
        {
            _executionProhibited.push_back( make_pair(id, releaseTime) );
            cnt = _executionProhibited.size();
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << getId() << " unable to acquire exclusion mutex: setExecutionProhibited()" << endl;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCLUSION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return cnt;
}

/*
 *  This method removes all _infinite_ exclusions against this device from device "id".
 *  A time based exclusion will not be removed.
 */
bool CtiDeviceExclusion::removeInfiniteProhibit(unsigned long id)
{
    bool pass = false;          // If this is set to true it means a non-infinite and valid time exclusion exists for id.
    bool removed = false;
    RWTime eot(YUKONEOT);
    CtiDeviceExclusion::prohibitions::iterator itr;

    try
    {
        if( _executionProhibited.size() > 0 )
        {
            CtiLockGuard<CtiMutex> guard(_exclusionMux, 5000);
            if(guard.isAcquired())
            {
                for(itr = _executionProhibited.begin(); itr != _executionProhibited.end(); )
                {
                    if((*itr).first == id)      // Remove the non infinite times
                    {
                        RWTime now;

                        if(eot == (*itr).second || (now > (*itr).second))   // infinite time, or now is greater than the exclusion time.
                        {
                            itr = _executionProhibited.erase(itr);
                            removed = true;
                        }
                        else
                        {
                            pass = true;        // There _still_ exists a valid time exclusion against this device (but no infinite time exclusions)
                            itr++;
                        }
                    }
                    else
                    {
                        itr++;
                    }
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << getId() << " unable to acquire exclusion mutex: removeInfiniteProhibit()" << endl;
                }
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return(removed && !pass);
}

bool CtiDeviceExclusion::hasTimeExclusion() const
{
    bool b = false;

    if(_cycleTimeExclusion.getFunctionId() == CtiTablePaoExclusion::ExFunctionCycleTime && _cycleTimeExclusion.getCycleTime() > 0)
    {
        b = true;
    }

    return b;
}

void CtiDeviceExclusion::Dump(void) const
{
    if(hasExclusions())
    {
        exclusions::const_iterator itr;

        for(itr = _exclusionRecords.begin(); itr != _exclusionRecords.end(); itr++)
        {
            const CtiTablePaoExclusion &paox = *itr;

            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << paox.getPaoId() << " is excluded against " << paox.getExcludedPaoId() << "." << endl;
                }
            }
        }
    }
}

RWTime CtiDeviceExclusion::getEvaluateNextAt() const
{
    return _evalNext;
}

void CtiDeviceExclusion::setEvaluateNextAt(RWTime set)
{
    _evalNext = set;
    return;
}

RWTime CtiDeviceExclusion::getExecutionGrantExpires() const
{
    return _executeGrantExpires;
}
void CtiDeviceExclusion::setExecutionGrantExpires(RWTime set)
{
    _executeGrantExpires = set;
    return;
}

RWTime CtiDeviceExclusion::getExecutionGrant() const
{
    return _executionGrant;
}
void CtiDeviceExclusion::setExecutionGrant(RWTime set)
{
    _executionGrant = set;
    return;
}


bool CtiDeviceExclusion::isTimeExclusionOpen() const          // This device has no time slot, or no is in the timeslot.
{
    bool bstatus = false;

    if( _cycleTimeExclusion.getFunctionId() == CtiTablePaoExclusion::ExFunctionCycleTime && _cycleTimeExclusion.getCycleTime() > 0)
    {
        if(_cycleTimeExclusion.getFunctionId() == (CtiTablePaoExclusion::ExFunctionCycleTime))
        {
            RWTime now;
            RWTime nextOpen = nextScheduledTimeAlignedOnRate( now, _cycleTimeExclusion.getCycleTime() );

            RWTime open = nextOpen - _cycleTimeExclusion.getCycleTime() + _cycleTimeExclusion.getCycleOffset();               // Back up one position.
            RWTime close = open + _cycleTimeExclusion.getTransmitTime() - getMinTimeInSec();

            bstatus = (open <= now && now < close);
        }
    }

    return bstatus;
}

RWTime CtiDeviceExclusion::getTimeSlotOpen() const
{
    RWTime tm;

    if( _cycleTimeExclusion.getFunctionId() == CtiTablePaoExclusion::ExFunctionCycleTime )
    {
        RWTime now;
        RWTime nextOpen = nextScheduledTimeAlignedOnRate( now, _cycleTimeExclusion.getCycleTime() );
        RWTime open = nextOpen - _cycleTimeExclusion.getCycleTime() + _cycleTimeExclusion.getCycleOffset();
        tm = open;
    }

    return tm;
}

RWTime CtiDeviceExclusion::getNextTimeSlotOpen() const
{
    RWTime tm;

    if( _cycleTimeExclusion.getFunctionId() == CtiTablePaoExclusion::ExFunctionCycleTime && _cycleTimeExclusion.getCycleTime() > 0 )
    {
        RWTime now;
        RWTime nextOpen = nextScheduledTimeAlignedOnRate( now, _cycleTimeExclusion.getCycleTime() );
        tm = nextOpen + _cycleTimeExclusion.getCycleOffset();
    }
    else
    {
        tm = RWTime() + gConfigParms.getValueAsInt("PORTER_SA_REPEAT_DELAY", 300);
    }

    return tm;
}

RWTime CtiDeviceExclusion::getTimeSlotClose() const
{
    RWTime tm;

    if( _cycleTimeExclusion.getFunctionId() == CtiTablePaoExclusion::ExFunctionCycleTime )
    {
        RWTime now;
        RWTime nextOpen = nextScheduledTimeAlignedOnRate( now, _cycleTimeExclusion.getCycleTime() );
        RWTime open = nextOpen - _cycleTimeExclusion.getCycleTime() + _cycleTimeExclusion.getCycleOffset();
        RWTime close = open + _cycleTimeExclusion.getTransmitTime();
        tm = close;
    }

    return tm;
}

bool CtiDeviceExclusion::proximityExcludes(LONG id) const
{
    bool b = false;

    exclusions::const_iterator itr;

    for(itr = _exclusionRecords.begin(); itr != _exclusionRecords.end(); itr++)
    {
        const CtiTablePaoExclusion &paox = *itr;

        switch(paox.getFunctionId())
        {
        case (CtiTablePaoExclusion::ExFunctionIdExclusion):
            {
                if(paox.getExcludedPaoId() == id)
                    b = true;

                break;
            }
        }
    }

    return b;
}

CtiTablePaoExclusion CtiDeviceExclusion::getCycleTimeExclusion() const
{
    return _cycleTimeExclusion;
}

unsigned int CtiDeviceExclusion::getMinTimeInSec() const
{
    return _minTimeInSec;
}

void CtiDeviceExclusion::setMinTimeInSec(unsigned int sec)
{
    _minTimeInSec = sec;
    return;
}


