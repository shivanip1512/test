/*---------------------------------------------------------------------------
        Filename:  lmgroupsa305.h
        
        Programmer:  Aaron Lauinger
        
        Description:    Header file for CtiLMGroupSA305

        Initial Date:  03/05/2004
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2004
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMGROUPSA305_H
#define CTILMGROUPSA305_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>

#include "lmgroupbase.h"
                
class CtiLMGroupSA305 : public CtiLMGroupBase
{
public:

RWDECLARE_COLLECTABLE( CtiLMGroupSA305 )

    CtiLMGroupSA305();
    CtiLMGroupSA305(RWDBReader& rdr);
    CtiLMGroupSA305(const CtiLMGroupSA305& groupexp);

    virtual ~CtiLMGroupSA305();

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupSA305& operator=(const CtiLMGroupSA305& right);

    int operator==(const CtiLMGroupSA305& right) const;
    int operator!=(const CtiLMGroupSA305& right) const;

    /* Static Members */

private:

    void restore(RWDBReader& rdr);
};
#endif

