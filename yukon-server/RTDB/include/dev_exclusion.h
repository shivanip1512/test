
/*-----------------------------------------------------------------------------*
*
* File:   dev_exclusion
*
* Class:  CtiDeviceExclusion
* Date:   2/27/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2004/05/10 21:35:51 $
* HISTORY      :
* $Log: dev_exclusion.h,v $
* Revision 1.3  2004/05/10 21:35:51  cplender
* Exclusions a'la GRE are a bit closer here.  The proximity exclusions should work ok now.
*
* Revision 1.2  2004/04/29 20:22:38  cplender
* IR
*
* Revision 1.1.2.1  2004/04/15 23:30:19  cplender
* IR
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __DEV_EXCLUSION_H__
#define __DEV_EXCLUSION_H__

#include <windows.h>
#include <vector>
using namespace std;

#include "boost/shared_ptr.hpp"
using boost::shared_ptr;

#include "dlldefs.h"
#include "mutex.h"
#include "tbl_paoexclusion.h"
#include "yukon.h"

class CtiDeviceExclusion;

typedef shared_ptr< CtiDeviceExclusion > CtiExclusionSPtr;

class IM_EX_DEVDB CtiDeviceExclusion
{
public:

    typedef vector< CtiTablePaoExclusion > exclusions;
    typedef vector< pair< unsigned long, RWTime > > prohibitions;

protected:

    LONG _deviceId;

    mutable CtiMutex    _exclusionMux;            // Used when processing the exclusion logic
    exclusions          _exclusionRecords;        // This is the list of database records identifying the exclusions on or against this pao.
    prohibitions        _executionProhibited;     // Device is currently prohibited from executing because of this list of devids.

    RWTime              _evalNext;                // Device should be looked at again at this time for exclusion purposes
    RWTime              _executingUntil;          // Device is currently executing until...
    RWTime              _executeGrantExpires;     // Device is may execute until...
    RWTime              _mustCompleteBy;          // This is the allotted time if this device were allowed to execute now.
    RWTime              _lastExclusionGrant;      // This is the last time this device was granted execution priviledges.

private:

public:

    CtiDeviceExclusion(LONG id = 0);

    CtiDeviceExclusion(const CtiDeviceExclusion& aRef);

    virtual ~CtiDeviceExclusion();

    CtiDeviceExclusion& operator=(const CtiDeviceExclusion& aRef);


    LONG getId() const;
    CtiDeviceExclusion& setId(LONG id);

    CtiMutex& getExclusionMux();
    bool hasExclusions() const;
    exclusions getExclusions() const;
    void addExclusion(CtiTablePaoExclusion &paox);
    void clearExclusions();
    bool isDeviceExcluded(long id) const;
    bool isExecutionProhibited(const RWTime &now = RWTime(), LONG did = 0);

    size_t setExecutionProhibited(unsigned long id, RWTime& releaseTime = RWTime(YUKONEOT));
    bool removeInfiniteProhibit(unsigned long id);
    bool hasTimeExclusion() const;


    bool   isExecuting() const;
    void   setExecuting(bool set = true);
    RWTime getExecutingUntil() const;
    void   setExecutingUntil(RWTime set = RWTime(YUKONEOT));

    void Dump(void) const;

    RWTime getEvaluateNextAt() const;
    void setEvaluateNextAt(RWTime set);
    RWTime getMustCompleteBy() const;
    void setMustCompleteBy(RWTime set);
    RWTime getExecutionGrantExpires() const;
    void setExecutionGrantExpires(RWTime set);
    RWTime getLastExclusionGrant() const;
    void setLastExclusionGrant(RWTime set);

    bool isTimeExclusionOpen() const;          // This device has no time slot, or no is in the timeslot.
    bool proximityExcludes(LONG id) const;

    RWTime getNextTimeSlotOpen() const;
    RWTime getNextTimeSlotClose() const;
};
#endif // #ifndef __DEV_EXCLUSION_H__
