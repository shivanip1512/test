/*---------------------------------------------------------------------------
        Filename:  lmgroupemetcon.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMGroupEmetcon
                        CtiLMGroupEmetcon

        Initial Date:  2/5/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMGROUPEMETCONIMPL_H
#define CTILMGROUPEMETCONIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "lmgroupbase.h"
#include "observe.h"
                
class CtiLMGroupEmetcon : public CtiLMGroupBase
{

public:

RWDECLARE_COLLECTABLE( CtiLMGroupEmetcon )

    CtiLMGroupEmetcon();
    CtiLMGroupEmetcon(RWDBReader& rdr);
    CtiLMGroupEmetcon(const CtiLMGroupEmetcon& groupemet);

    virtual ~CtiLMGroupEmetcon();
    
    ULONG getGoldAddress() const;
    ULONG getSilverAddress() const;
    const RWCString& getAddressUsage() const;
    const RWCString& getRelayUsage() const;
    ULONG getRouteId() const;

    CtiLMGroupEmetcon& setGoldAddress(ULONG goldadd);
    CtiLMGroupEmetcon& setSilverAddress(ULONG silveradd);
    CtiLMGroupEmetcon& setAddressUsage(const RWCString& adduse);
    CtiLMGroupEmetcon& setRelayUsage(const RWCString& relayuse);
    CtiLMGroupEmetcon& setRouteId(ULONG rteid);

    void restoreEmetconSpecificDatabaseEntries(RWDBReader& rdr);

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(ULONG refreshRate, ULONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(ULONG percent, ULONG period, ULONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(ULONG sendRate, ULONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(ULONG offTime, ULONG period, int priority) const;

    virtual BOOL doesMasterCycleNeedToBeUpdated(ULONG secondsFrom1901, ULONG groupControlDone, ULONG offTime);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupEmetcon& operator=(const CtiLMGroupEmetcon& right);

    int operator==(const CtiLMGroupEmetcon& right) const;
    int operator!=(const CtiLMGroupEmetcon& right) const;

    /* Static Members */

private:

    ULONG _goldaddress;
    ULONG _silveraddress;
    RWCString _addressusage;
    RWCString _relayusage;
    ULONG _routeid;

    BOOL _refreshsent;

    mutable RWRecursiveLock<RWMutexLock> _mutex;

    void restore(RWDBReader& rdr);
};
#endif

