/*---------------------------------------------------------------------------
        Filename:  lmgroupripple.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMGroupRipple

        Initial Date:  1/10/2002
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2002
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMGROUPRIPPLEIMPL_H
#define CTILMGROUPRIPPLEIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "lmgroupbase.h"
#include "observe.h"
                
class CtiLMGroupRipple : public CtiLMGroupBase
{

public:

RWDECLARE_COLLECTABLE( CtiLMGroupRipple )

    CtiLMGroupRipple();
    CtiLMGroupRipple(RWDBReader& rdr);
    CtiLMGroupRipple(const CtiLMGroupRipple& groupripple);

    virtual ~CtiLMGroupRipple();
    
    ULONG getRouteId() const;
    ULONG getShedTime() const;
    const RWCString& getControlValue() const;
    const RWCString& getRestoreValue() const;

    CtiLMGroupRipple& setRouteId(ULONG rteid);
    CtiLMGroupRipple& setShedTime(ULONG shed);
    CtiLMGroupRipple& setControlValue(const RWCString& control);
    CtiLMGroupRipple& setRestoreValue(const RWCString& restore);

    void restoreRippleSpecificDatabaseEntries(RWDBReader& rdr);

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(ULONG refreshRate, ULONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(ULONG percent, ULONG period, ULONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(ULONG sendRate, ULONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(ULONG offTime, ULONG period, int priority) const;

    virtual BOOL doesMasterCycleNeedToBeUpdated(ULONG secondsFrom1901, ULONG groupControlDone, ULONG offTime);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupRipple& operator=(const CtiLMGroupRipple& right);

    int operator==(const CtiLMGroupRipple& right) const;
    int operator!=(const CtiLMGroupRipple& right) const;

    /* Static Members */

private:

    ULONG _routeid;
    ULONG _shedtime;
    RWCString _controlvalue;
    RWCString _restorevalue;

    BOOL _refreshsent;

    void restore(RWDBReader& rdr);
};
#endif

