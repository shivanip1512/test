
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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2004/06/03 16:32:04 $
* HISTORY      :
* $Log: dev_exclusion.h,v $
* Revision 1.6  2004/06/03 16:32:04  mfisher
* added a fake typedef for smart pointers wrapped by #define VSLICK_TAG_WORKAROUND so Slick will treat Cti*SPtr as a true pointer
*
* Revision 1.5  2004/05/20 22:42:30  cplender
* Various exclusion changes
*
* Revision 1.4  2004/05/19 14:48:53  cplender
* Exclusion changes
*
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

#if VSLICK_TAG_WORKAROUND
typedef CtiDeviceExclusion * CtiExclusionSPtr;
#else
typedef shared_ptr< CtiDeviceExclusion > CtiExclusionSPtr;
#endif

class IM_EX_DEVDB CtiDeviceExclusion
{
public:

    typedef vector< CtiTablePaoExclusion > exclusions;
    typedef vector< pair< unsigned long, RWTime > > prohibitions;

protected:

    LONG _deviceId;

    CtiTablePaoExclusion _cycleTimeExclusion;          // Used if this device has a time exclusion?

    mutable CtiMutex    _exclusionMux;            // Used when processing the exclusion logic
    exclusions          _exclusionRecords;        // This is the list of database records identifying the exclusions on or against this pao.
    prohibitions        _executionProhibited;     // Device is currently prohibited from executing because of this list of devids.

    RWTime              _evalNext;                // Device should be looked at again at this time for exclusion purposes
    RWTime              _executingUntil;          // Device is currently executing until...
    RWTime              _executeGrantExpires;     // Device is may execute until...
    RWTime              _executionGrant;      // This is the last time this device was granted execution priviledges.

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
    RWTime getExecutionGrantExpires() const;
    void setExecutionGrantExpires(RWTime set);
    RWTime getExecutionGrant() const;
    void setExecutionGrant(RWTime set);

    bool isTimeExclusionOpen() const;          // This device has no time slot, or no is in the timeslot.
    bool proximityExcludes(LONG id) const;

    RWTime getTimeSlotOpen() const;
    RWTime getNextTimeSlotOpen() const;
    RWTime getTimeSlotClose() const;
    CtiTablePaoExclusion getCycleTimeExclusion() const;

};
#endif // #ifndef __DEV_EXCLUSION_H__
