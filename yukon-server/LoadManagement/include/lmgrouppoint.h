/*---------------------------------------------------------------------------
        Filename:  lmgrouppoint.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMGroupPoint
                        CtiLMGroupPoint

        Initial Date:  3/13/2002
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2002
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMGROUPPOINTIMPL_H
#define CTILMGROUPPOINTIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "lmgroupbase.h"
#include "msg_cmd.h"
#include "observe.h"
                
class CtiLMGroupPoint : public CtiLMGroupBase
{

public:

RWDECLARE_COLLECTABLE( CtiLMGroupPoint )

    CtiLMGroupPoint();
    CtiLMGroupPoint(RWDBReader& rdr);
    CtiLMGroupPoint(const CtiLMGroupPoint& grouppoint);

    virtual ~CtiLMGroupPoint();
    
    LONG getDeviceIdUsage() const;
    LONG getPointIdUsage() const;
    LONG getStartControlRawState() const;

    CtiLMGroupPoint& setDeviceIdUsage(LONG deviduse);
    CtiLMGroupPoint& setPointIdUsage(LONG pointiduse);
    CtiLMGroupPoint& setStartControlRawState(LONG startcontrolstate);

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;
    virtual CtiCommandMsg* createLatchingRequestMsg(LONG rawState, int priority) const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupPoint& operator=(const CtiLMGroupPoint& right);

    int operator==(const CtiLMGroupPoint& right) const;
    int operator!=(const CtiLMGroupPoint& right) const;

    /* Static Members */

private:

    LONG _deviceidusage;
    LONG _pointidusage;
    LONG _startcontrolrawstate;

    void restore(RWDBReader& rdr);
};
#endif

