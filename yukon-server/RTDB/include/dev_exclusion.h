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
* REVISION     :  $Revision: 1.13.2.1 $
* DATE         :  $Date: 2008/11/13 17:23:39 $
* HISTORY      :
* $Log: dev_exclusion.h,v $
* Revision 1.13.2.1  2008/11/13 17:23:39  jmarks
* YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008
*
* Responded to reviewer comments again.
*
* I eliminated excess references to windows.h .
*
* This still left over 100 references to it where "yukon.h" or "precompiled.h" was not obviously included.  Some other chaining of references could still be going on, and of course it is potentially possible that not all the files in the project that include windows.h actually need it - I didn't check for that.
*
* None-the-less, I than added the NOMINMAX define right before each place where windows.h is still included.
* Special note:  std::min<LONG>(TimeOut, 500); is still required for compilation.
*
* In this process I occasionally deleted a few empty lines, and when creating the define, also added some.
*
* This may not have affected every file in the project, but while mega-editing it certainly seemed like it did.
*
* Revision 1.13  2008/10/15 19:54:04  jotteson
* YUK-6588 Porter's memory use needs to be trimmed
* Removed unused table.
* Changed exclusion to be a pointer when unused.
*
* Revision 1.12  2008/09/15 17:59:18  jotteson
* YUK-6456 Change Boost Assert behavior to print stack trace instead of asserting
* Added boostutil.h to define boost functions necessary to override boost_assert
* Added #define to override boost_assert
* Changed boost_assert to call print function to print stack trace.
*
* Revision 1.11  2007/02/09 20:56:14  mfisher
* removed VSLICK_TAG_WORKAROUND #define - tagging problem was fixed in Slick 11
*
* Revision 1.10  2006/09/21 21:31:37  mfisher
* privatized Inherited typedef
*
* Revision 1.9  2006/03/24 15:58:19  cplender
* Work on exclusion logic to unify the ripple work and make EREPC work right.  90% there.
*
* Revision 1.8  2005/12/20 17:20:29  tspar
* Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
*
* Revision 1.7  2004/07/08 23:15:37  cplender
* Added get/setMinTimeInSec()
*
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


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <vector>
#include <queue>
#include <boost/shared_ptr.hpp>
#include "boostutil.h"
#include "dlldefs.h"
#include "mutex.h"
#include "tbl_paoexclusion.h"
#include "yukon.h"
#include "ctitime.h"

class CtiDeviceExclusion;

typedef boost::shared_ptr< CtiDeviceExclusion > CtiExclusionSPtr;

class IM_EX_DEVDB CtiDeviceExclusion
{
public:

    typedef std::vector< CtiTablePaoExclusion > exclusions;
    typedef std::vector< std::pair< unsigned long, CtiTime > > prohibitions;

protected:

    LONG _deviceId;

    CtiTablePaoExclusion *_cycleTimeExclusion;          // Used if this device has a time exclusion?

    mutable CtiMutex    _exclusionMux;            // Used when processing the exclusion logic
    exclusions          _exclusionRecords;        // This is the list of database records identifying the exclusions on or against this pao.
    prohibitions        _executionProhibited;     // Device is currently prohibited from executing because of this list of devids.

    CtiTime              _evalNext;                // Device should be looked at again at this time for exclusion purposes
    CtiTime              _executingUntil;          // Device is currently executing until...
    CtiTime              _executeGrantExpires;     // Device is may execute until...
    CtiTime              _executionGrant;      // This is the last time this device was granted execution priviledges.

    unsigned int        _minTimeInSec;            // This is the minimum time to allow in isTimeSlotOpen;

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
    bool isExecutionProhibited(const CtiTime &now = CtiTime(), LONG did = 0);

    size_t setExecutionProhibited(unsigned long id, CtiTime& releaseTime = CtiTime(YUKONEOT));
    bool removeInfiniteProhibit(unsigned long id);
    bool removeProhibit(unsigned long id);
    void dumpProhibits(unsigned long id = 0);
    bool hasTimeExclusion() const;


    bool   isExecuting() const;
    void   setExecuting(bool set = true, CtiTime when = CtiTime(YUKONEOT));
    CtiTime getExecutingUntil() const;

    void Dump(void) const;

    CtiTime getEvaluateNextAt() const;
    void setEvaluateNextAt(CtiTime set);
    CtiTime getExecutionGrantExpires() const;
    void setExecutionGrantExpires(CtiTime set);
    CtiTime getExecutionGrant() const;
    void setExecutionGrant(CtiTime set);

    bool isTimeExclusionOpen() const;          // This device has no time slot, or no is in the timeslot.
    bool proximityExcludes(LONG id) const;

    CtiTime getTimeSlotOpen() const;
    CtiTime getNextTimeSlotOpen() const;
    CtiTime getTimeSlotClose() const;
    CtiTablePaoExclusion getCycleTimeExclusion() const;

    unsigned int getMinTimeInSec() const;
    void setMinTimeInSec(unsigned int sec);

};
#endif // #ifndef __DEV_EXCLUSION_H__
