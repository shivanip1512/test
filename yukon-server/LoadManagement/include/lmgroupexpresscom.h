/*---------------------------------------------------------------------------
        Filename:  lmgroupexpresscom.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMGroupExpresscom
                        CtiLMGroupExpresscom

        Initial Date:  10/24/2002
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2002
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMGROUPEXPRESSCOMIMPL_H
#define CTILMGROUPEXPRESSCOMIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/recursiv.h> 

#include "lmgroupbase.h"
#include "observe.h"
                
class CtiLMGroupExpresscom : public CtiLMGroupBase
{

public:

RWDECLARE_COLLECTABLE( CtiLMGroupExpresscom )

    CtiLMGroupExpresscom();
    CtiLMGroupExpresscom(RWDBReader& rdr);
    CtiLMGroupExpresscom(const CtiLMGroupExpresscom& groupexp);

    virtual ~CtiLMGroupExpresscom();

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTrueCycleRequestMsg(ULONG percent, ULONG period, ULONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(ULONG refreshRate, ULONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(ULONG percent, ULONG period, ULONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(ULONG sendRate, ULONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(ULONG offTime, ULONG period, int priority) const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupExpresscom& operator=(const CtiLMGroupExpresscom& right);

    int operator==(const CtiLMGroupExpresscom& right) const;
    int operator!=(const CtiLMGroupExpresscom& right) const;

    /* Static Members */

private:

    void restore(RWDBReader& rdr);
};
#endif

