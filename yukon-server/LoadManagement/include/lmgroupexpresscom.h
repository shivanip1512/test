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
    virtual CtiPILRequestMsg* createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiPILRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiPILRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiPILRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiPILRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;
    virtual CtiPILRequestMsg* createSetPointRequestMsg(RWCString settings, LONG minValue, LONG maxValue,
                                                    LONG valueB, LONG valueD, LONG valueF, LONG random,
                                                    LONG valueTA, LONG valueTB, LONG valueTC, LONG valueTD,
                                                    LONG valueTE, LONG valueTF, int priority) const;

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

