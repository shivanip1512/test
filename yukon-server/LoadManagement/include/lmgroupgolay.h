/*---------------------------------------------------------------------------
        Filename:  lmgroupgolay.h
        
        Programmer:  Aaron Lauinger
        
        Description:    Header file for CtiLMGroupGolay

        Initial Date:  03/05/2004
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2004
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMGROUPGOLAY_H
#define CTILMGROUPGOLAY_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>

#include "lmgroupbase.h"
                
class CtiLMGroupGolay : public CtiLMGroupBase
{
public:

RWDECLARE_COLLECTABLE( CtiLMGroupGolay )

    CtiLMGroupGolay();
    CtiLMGroupGolay(RWDBReader& rdr);
    CtiLMGroupGolay(const CtiLMGroupGolay& groupexp);

    virtual ~CtiLMGroupGolay();

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    int getNominalTimeout() const;
    CtiLMGroupGolay& setNominalTimeout(int nominal_timeout);
      
    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupGolay& operator=(const CtiLMGroupGolay& right);

    int operator==(const CtiLMGroupGolay& right) const;
    int operator!=(const CtiLMGroupGolay& right) const;

    /* Static Members */

private:

    int _nominal_timeout;
    
    void restore(RWDBReader& rdr);
};
#endif

