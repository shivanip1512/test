/*-----------------------------------------------------------------------------*
*
* File:   pttrigger
*
* Date:   5/16/2006
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/INCLUDE/pttrigger.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2008/09/15 17:59:18 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PTTRIGGER_H__
#define __PTTRIGGER_H__

#include "yukon.h"
#include <boost/shared_ptr.hpp>
#include "boostutil.h"
//using boost::shared_ptr;

#include "logger.h"
#include "mgr_point.h"
#include "tbl_pt_trigger.h"

typedef struct 
{
    CtiTablePointTrigger dbTriggerData;
    DOUBLE lastTriggerValue;
} PtVerifyTrigger;
typedef shared_ptr< PtVerifyTrigger > PtVerifyTriggerSPtr;

class IM_EX_PNTDB CtiPointTriggerManager
{
public:

    typedef CtiLockGuard<CtiMutex>           LockGuard;
    typedef map< long, PtVerifyTriggerSPtr > coll_type;              // This is the collection type!
    typedef map< long, coll_type    >        trig_coll_type;
    typedef coll_type::iterator              spiterator;

private:
    coll_type _verificationIDMap;
    coll_type _pointIDMap;
    trig_coll_type _triggerIDMap;
    mutable CtiMutex _mapMux;
    void refreshTriggerData(long pointID, RWDBReader& rdr, CtiPointManager &pointMgr);
public:

    CtiPointTriggerManager();
    ~CtiPointTriggerManager();
    CtiPointTriggerManager& operator=(const CtiPointTriggerManager &aRef);

    void refreshList(long ptID, CtiPointManager &pointMgr);
    void orphan(long ptID, CtiPointManager &pointMgr);
    coll_type*          getPointIteratorFromTrigger(long triggerID);
    PtVerifyTriggerSPtr getPointTriggerFromPoint(long pointID);
    PtVerifyTriggerSPtr getPointTriggerFromVerificationID(long pointID);

    CtiMutex& getMux();
};

#endif // __PTTRIGGER_H__
