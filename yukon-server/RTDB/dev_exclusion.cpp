/*-----------------------------------------------------------------------------*
*
* File:   dev_exclusion
*
* Date:   2/27/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2004/04/29 20:22:38 $
*
* HISTORY      :
* $Log: dev_exclusion.cpp,v $
* Revision 1.2  2004/04/29 20:22:38  cplender
* IR
*
* Revision 1.1.2.1  2004/04/15 23:30:19  cplender
* IR
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#include "dev_exclusion.h"
#include "guard.h"
#include "logger.h"


CtiDeviceExclusion::CtiDeviceExclusion(LONG id) :
_deviceId(id)
{
}

CtiDeviceExclusion::CtiDeviceExclusion(const CtiDeviceExclusion& aRef)
{
    *this = aRef;
}

CtiDeviceExclusion::~CtiDeviceExclusion()
{}

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
            bstatus = _exclusionRecords.size() != 0;
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
            _exclusionRecords.push_back(paox);
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
    return (_executingUntil >= _executingUntil.now());
}

void CtiDeviceExclusion::setExecuting(bool set)
{
    if(set)
        _executingUntil = RWTime(YUKONEOT);
    else
        _executingUntil = RWTime(rwEpoch);

    return;
}

RWTime CtiDeviceExclusion::getExecutingUntil() const
{
    return _executingUntil;
}

void CtiDeviceExclusion::setExecutingUntil(RWTime set)
{
    _executingUntil = set;
    return;
}

bool CtiDeviceExclusion::isExecutionProhibited(const RWTime &now)
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
                        prohibited = true;
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

bool CtiDeviceExclusion::removeExecutionProhibited(unsigned long id)
{
    bool removed = false;
    CtiDeviceExclusion::prohibitions::iterator itr;

    try
    {
        CtiLockGuard<CtiMutex> guard(_exclusionMux, 5000);
        if(guard.isAcquired())
        {
            for(itr = _executionProhibited.begin(); itr != _executionProhibited.end(); )
            {
                if((*itr).first == id)
                {
                    itr = _executionProhibited.erase(itr);
                    removed = true;
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
                dout << RWTime() << " " << getId() << " unable to acquire exclusion mutex: removeExecutionProhibited()" << endl;
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

    return removed;
}

bool CtiDeviceExclusion::hasTimeExclusion() const
{
    return false;
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

