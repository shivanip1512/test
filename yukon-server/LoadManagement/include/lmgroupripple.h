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
    
    LONG getShedTime() const;

    CtiLMGroupRipple& setShedTime(LONG shed);

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    virtual BOOL doesMasterCycleNeedToBeUpdated(ULONG secondsFrom1901, ULONG groupControlDone, ULONG offTime);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupRipple& operator=(const CtiLMGroupRipple& right);

    int operator==(const CtiLMGroupRipple& right) const;
    int operator!=(const CtiLMGroupRipple& right) const;

    /* Static Members */

private:

    LONG _shedtime;

    BOOL _refreshsent;

    void restore(RWDBReader& rdr);
};
#endif

