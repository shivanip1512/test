/*---------------------------------------------------------------------------
        Filename:  lmgroupmacro.h
        
        Programmer:  Aaron Lauinger
        
        Description:    Header file for CtiLMGroupMacro
                        CtiLMGroupMacro

        Initial Date:  6/24/2004
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001-2004
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef __LMGROUPMACRO_H__
#define __LMGROUPMACRO_H__

#include <vector>

#include <rw/db/db.h>
#include <rw/thr/recursiv.h> 

#include "lmgroupbase.h"

using namespace std;

class CtiLMGroupMacro : public CtiLMGroupBase
{
public:

    CtiLMGroupMacro();
    CtiLMGroupMacro(RWDBReader& rdr);

    virtual ~CtiLMGroupMacro();

    const vector< CtiLMGroupBase* > getChildren() const;
    void setChildren(const vector<CtiLMGroupBase*>& children);
    
    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const { return 0; }
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const { return 0; }
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const { return 0;}
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const { return 0; }

private:

    vector< CtiLMGroupBase* > _children;

protected:
    
    void restore(RWDBReader& rdr);
};
#endif
