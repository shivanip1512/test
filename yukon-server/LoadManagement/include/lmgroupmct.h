/*---------------------------------------------------------------------------
        Filename:  lmgroupmct.h

        Programmer:  Josh Wolberg

        Description:    Header file for CtiLMGroupMCT
                        CtiLMGroupMCT

        Initial Date:  7/23/2003

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2003
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMGROUPMCTIMPL_H
#define CTILMGROUPMCTIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "lmgroupemetcon.h"
                
class CtiLMGroupMCT : public CtiLMGroupEmetcon
{

public:

RWDECLARE_COLLECTABLE( CtiLMGroupMCT )

    typedef CtiLMGroupEmetcon Inherited;
    
    CtiLMGroupMCT();
    CtiLMGroupMCT(RWDBReader& rdr);
    CtiLMGroupMCT(const CtiLMGroupMCT& groupmct);

    virtual ~CtiLMGroupMCT();
    
    virtual CtiLMGroupBase* replicate() const;
    /*virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    virtual BOOL doesMasterCycleNeedToBeUpdated(ULONG secondsFrom1901, ULONG groupControlDone, ULONG offTime);*/

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupMCT& operator=(const CtiLMGroupMCT& right);

    int operator==(const CtiLMGroupMCT& right) const;
    int operator!=(const CtiLMGroupMCT& right) const;

    /* Static Members */
};
#endif
