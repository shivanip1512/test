/*---------------------------------------------------------------------------
        Filename:  lmgroupversacom.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMGroupVersacom
                        CtiLMGroupVersacom

        Initial Date:  2/5/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMGROUPVERSACOMIMPL_H
#define CTILMGROUPVERSACOMIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
#include "lmgroupbase.h"
                
class CtiLMGroupVersacom : public CtiLMGroupBase
{

public:

RWDECLARE_COLLECTABLE( CtiLMGroupVersacom )

    CtiLMGroupVersacom();
    CtiLMGroupVersacom(RWDBReader& rdr);
    CtiLMGroupVersacom(const CtiLMGroupVersacom& groupversa);

    virtual ~CtiLMGroupVersacom();

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiPILRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiPILRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiPILRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiPILRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupVersacom& operator=(const CtiLMGroupVersacom& right);

    int operator==(const CtiLMGroupVersacom& right) const;
    int operator!=(const CtiLMGroupVersacom& right) const;

    /* Static Members */

private:

    void restore(RWDBReader& rdr);
};
#endif

