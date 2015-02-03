#include "precompiled.h"
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

CtiDeviceExclusion::~CtiDeviceExclusion()
{
    if(_cycleTimeExclusion != NULL)
    {
        delete _cycleTimeExclusion;
        _cycleTimeExclusion = NULL;
    }
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
            CTILOG_ERROR(dout, "DeviceId "<< getId() <<" - Unable to acquire exclusion mutex");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
            CTILOG_ERROR(dout, "DeviceId "<< getId() <<" - Unable to acquire exclusion mutex");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
            CTILOG_ERROR(dout, "DeviceId "<< getId() <<" - Unable to acquire exclusion mutex");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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

            CTILOG_ERROR(dout, "DeviceId "<< getId() <<" - Unable to acquire exclusion mutex");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                CTILOG_ERROR(dout, "DeviceId "<< getId() <<" - Unable to acquire exclusion mutex");

                prohibited = true;
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
            CTILOG_ERROR(dout, "DeviceId "<< getId() <<" - Unable to acquire exclusion mutex");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                CTILOG_ERROR(dout, "DeviceId "<< getId() <<" - Unable to acquire exclusion mutex");
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                CTILOG_ERROR(dout, "DeviceId "<< getId() <<" - Unable to acquire exclusion mutex");
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                Cti::StreamBuffer output;

                for(itr = _executionProhibited.begin(); itr != _executionProhibited.end(); )
                {
                    if(!id || (*itr).first == id)
                    {
                        output << endl <<"id "<< (*itr).first <<" blocks until "<< (*itr).second;
                    }
                }

                CTILOG_INFO(dout, output);
            }
            else
            {
                CTILOG_ERROR(dout, "DeviceId "<< getId() <<" - Unable to acquire exclusion mutex");
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
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

std::string CtiDeviceExclusion::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiDeviceExclusion";
    if(hasExclusions())
    {
        exclusions::const_iterator itr;
        for(itr = _exclusionRecords.begin(); itr != _exclusionRecords.end(); itr++)
        {
            const CtiTablePaoExclusion &paox = *itr;
            itemList << paox.getPaoId() << " is excluded against " << paox.getExcludedPaoId();
        }
    }

    return itemList.toString();
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



