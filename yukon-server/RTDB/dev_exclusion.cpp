/*-----------------------------------------------------------------------------*
*
* File:   dev_exclusion
*
* Date:   2/27/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.17 $
* DATE         :  $Date: 2008/10/15 19:54:04 $
*
* HISTORY      :
* $Log: dev_exclusion.cpp,v $
* Revision 1.17  2008/10/15 19:54:04  jotteson
* YUK-6588 Porter's memory use needs to be trimmed
* Removed unused table.
* Changed exclusion to be a pointer when unused.
*
* Revision 1.16  2006/04/24 20:47:30  tspar
* Rogue Wave: more odds and ends in the RWreplacement
*
* Revision 1.15  2006/03/31 18:24:43  cplender
* Additional (take 2) exclusion logic tweaks to accomodate East River.
*
* Revision 1.14  2006/03/24 15:58:19  cplender
* Work on exclusion logic to unify the ripple work and make EREPC work right.  90% there.
*
* Revision 1.13  2006/03/03 18:35:31  cplender
* Altered exclusion logic for Ripple groups and LCU processing
*
* Revision 1.12  2006/01/30 20:29:27  jotteson
* Fixed exclusion bug comparing a time and a device id. Removed un-necessary if loop.
*
* Revision 1.11  2005/12/20 17:20:21  tspar
* Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
*
* Revision 1.10  2005/07/07 20:06:25  jotteson
* Fixed bug with exceptions in exclusion and mgr_device.
*
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
#include "dlldefs.h"
#include "dllbase.h"

#include "cparms.h"
#include "dev_exclusion.h"
#include "guard.h"
#include "logger.h"
#include "utility.h"

using std::make_pair;
using std::endl;

CtiDeviceExclusion::CtiDeviceExclusion(LONG id) :
_deviceId(id),
_minTimeInSec(0),
_cycleTimeExclusion(NULL)
{
}

CtiDeviceExclusion::CtiDeviceExclusion(const CtiDeviceExclusion& aRef)
{
    *this = aRef;
}

CtiDeviceExclusion::~CtiDeviceExclusion()
{
    if(_cycleTimeExclusion != NULL)
    {
        delete _cycleTimeExclusion;
        _cycleTimeExclusion = NULL;
    }
}

CtiDeviceExclusion& CtiDeviceExclusion::operator=(const CtiDeviceExclusion& aRef)
{
    if(this != &aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            bstatus = _exclusionRecords.size() != 0 || (_cycleTimeExclusion != NULL);
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << "  " << getId() << " unable to acquire exclusion mutex: hasExclusions()" << endl;
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                if(_cycleTimeExclusion != NULL)
                {
                    delete _cycleTimeExclusion;
                    _cycleTimeExclusion = NULL;
                }
                _cycleTimeExclusion = CTIDBG_new CtiTablePaoExclusion(paox); // Store this in a special slot to make it quicker to discover.  Only one of these.
            }
            else
            {
                _exclusionRecords.push_back(paox);
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << getId() << " unable to acquire exclusion mutex: addExclusion()" << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            if(_cycleTimeExclusion != NULL)
            {
                delete _cycleTimeExclusion;
                _cycleTimeExclusion = NULL;
            }
            _exclusionRecords.clear();
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << getId() << " unable to acquire exclusion mutex: clearExclusions()" << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            dout << CtiTime() << " " << getId() << " unable to acquire exclusion mutex: isDeviceExcluded()" << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCLUSION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return bstatus;
}

bool CtiDeviceExclusion::isExecuting() const
{
    return(_executingUntil > _executingUntil.now());
}

void CtiDeviceExclusion::setExecuting(bool executing, CtiTime when)
{
    if(executing)
        _executingUntil = when;
    else
        _executingUntil = CtiTime(PASTDATE);

    return;
}

CtiTime CtiDeviceExclusion::getExecutingUntil() const
{
    return _executingUntil;
}

/*
 *  Determines if this device is exclusion prohibited by any other device.  The passed in time is used to remove expired time exclusions
 *  The passed in device id if non-zero is used to determine if the indicated id is prohibiting the execution (true return).
 *  If did = zero, any exclusion returns bool true.
 */
bool CtiDeviceExclusion::isExecutionProhibited(const CtiTime &now, LONG did)
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
                        if(!did || (did && (*itr).first == did))
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
                    dout << CtiTime() << " " << getId() << " unable to acquire exclusion mutex: isExecutionProhibited()" << endl;
                }
                prohibited = true;
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
    return prohibited;
}

size_t CtiDeviceExclusion::setExecutionProhibited(unsigned long id, CtiTime& releaseTime)
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
                dout << CtiTime() << " " << getId() << " unable to acquire exclusion mutex: setExecutionProhibited()" << endl;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCLUSION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
    CtiTime eot(YUKONEOT);
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
                        CtiTime now;

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
                    dout << CtiTime() << " " << getId() << " unable to acquire exclusion mutex: removeInfiniteProhibit()" << endl;
                }
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return(removed && !pass);
}

/*
 *  This method removes all exclusions against this device from device "id".
 *  A time based exclusion will not be removed.
 */
bool CtiDeviceExclusion::removeProhibit(unsigned long id)
{
    bool pass = false;          // If this is set to true it means a non-infinite and valid time exclusion exists for id.
    bool removed = false;
    CtiTime eot(YUKONEOT);
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
                        itr = _executionProhibited.erase(itr);
                        removed = true;
                    }
                    else
                    {
                        pass = true;        // There _still_ exists an exclusion against this device.
                        itr++;
                    }
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << getId() << " unable to acquire exclusion mutex: removeInfiniteProhibit()" << endl;
                }
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return(removed && !pass);
}

/*
 *  This method displays all exclusions against this device from device "id".  If not id is specified, all are displayed.
 */
void CtiDeviceExclusion::dumpProhibits(unsigned long id)
{
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
                    if(!id || (*itr).first == id)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "   id " << (*itr).first << " blocks until " << (*itr).second << endl;
                        }
                    }
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << getId() << " unable to acquire exclusion mutex: removeInfiniteProhibit()" << endl;
                }
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return;
}

bool CtiDeviceExclusion::hasTimeExclusion() const
{
    bool b = false;

    if(_cycleTimeExclusion != NULL && _cycleTimeExclusion->getCycleTime() > 0)
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

CtiTime CtiDeviceExclusion::getEvaluateNextAt() const
{
    return _evalNext;
}

void CtiDeviceExclusion::setEvaluateNextAt(CtiTime t)
{
    _evalNext = t;
    return;
}

CtiTime CtiDeviceExclusion::getExecutionGrantExpires() const
{
    return _executeGrantExpires;
}
void CtiDeviceExclusion::setExecutionGrantExpires(CtiTime t)
{
    _executeGrantExpires = t;
    return;
}

CtiTime CtiDeviceExclusion::getExecutionGrant() const
{
    return _executionGrant;
}
void CtiDeviceExclusion::setExecutionGrant(CtiTime t)
{
    _executionGrant = t;
    return;
}


bool CtiDeviceExclusion::isTimeExclusionOpen() const          // This device has no time slot, or no is in the timeslot.
{
    bool bstatus = false;

    if( _cycleTimeExclusion != NULL && _cycleTimeExclusion->getCycleTime() > 0)
    {
        if(_cycleTimeExclusion->getFunctionId() == (CtiTablePaoExclusion::ExFunctionCycleTime))
        {
            CtiTime now;
            CtiTime nextOpen = nextScheduledTimeAlignedOnRate( now, _cycleTimeExclusion->getCycleTime() );

            CtiTime open = nextOpen - _cycleTimeExclusion->getCycleTime() + _cycleTimeExclusion->getCycleOffset();               // Back up one position.
            CtiTime close = open + _cycleTimeExclusion->getTransmitTime() - getMinTimeInSec();

            bstatus = (open <= now && now < close);
        }
    }

    return bstatus;
}

CtiTime CtiDeviceExclusion::getTimeSlotOpen() const
{
    CtiTime t;

    if( _cycleTimeExclusion != NULL )
    {
        CtiTime now;
        CtiTime nextOpen = nextScheduledTimeAlignedOnRate( now, _cycleTimeExclusion->getCycleTime() );
        CtiTime open = nextOpen - _cycleTimeExclusion->getCycleTime() + _cycleTimeExclusion->getCycleOffset();
        t = open;
    }

    return t;
}

CtiTime CtiDeviceExclusion::getNextTimeSlotOpen() const
{
    CtiTime t;

    if( _cycleTimeExclusion != NULL && _cycleTimeExclusion->getCycleTime() > 0 )
    {
        CtiTime now;
        CtiTime nextOpen = nextScheduledTimeAlignedOnRate( now, _cycleTimeExclusion->getCycleTime() );
        t = nextOpen + _cycleTimeExclusion->getCycleOffset();
    }
    else
    {
        t = CtiTime() + gConfigParms.getValueAsInt("PORTER_SA_REPEAT_DELAY", 300);
    }

    return t;
}

CtiTime CtiDeviceExclusion::getTimeSlotClose() const
{
    CtiTime t;

    if( _cycleTimeExclusion != NULL )
    {
        CtiTime now;
        CtiTime nextOpen = nextScheduledTimeAlignedOnRate( now, _cycleTimeExclusion->getCycleTime() );
        CtiTime open = nextOpen - _cycleTimeExclusion->getCycleTime() + _cycleTimeExclusion->getCycleOffset();
        CtiTime close = open + _cycleTimeExclusion->getTransmitTime();
        t = close;
    }

    return t;
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
    if(_cycleTimeExclusion != NULL)
    {
        return *_cycleTimeExclusion;
    }
    else
    {
        return CtiTablePaoExclusion();
    }

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


