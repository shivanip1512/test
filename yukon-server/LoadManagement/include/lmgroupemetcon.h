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
    
    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    virtual BOOL doesMasterCycleNeedToBeUpdated(ULONG secondsFrom1901, ULONG groupControlDone, ULONG offTime);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupEmetcon& operator=(const CtiLMGroupEmetcon& right);

    int operator==(const CtiLMGroupEmetcon& right) const;
    int operator!=(const CtiLMGroupEmetcon& right) const;

    /* Static Members */

private:

    BOOL _refreshsent;

protected:
    void restore(RWDBReader& rdr);
};
#endif
