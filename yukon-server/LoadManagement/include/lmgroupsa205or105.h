/*---------------------------------------------------------------------------
        Filename:  lmgroupsa205or105.h
        
        Programmer:  Aaron Lauinger
        
        Description:    Header file for CtiLMGroupSA205OR105

        Initial Date:  03/05/2004
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2004
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMGROUPSA205OR105_H
#define CTILMGROUPSA205OR105_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>

#include "lmgroupbase.h"
                
class CtiLMGroupSA205OR105 : public CtiLMGroupBase
{
public:

RWDECLARE_COLLECTABLE( CtiLMGroupSA205OR105 )

    CtiLMGroupSA205OR105();
    CtiLMGroupSA205OR105(RWDBReader& rdr);
    CtiLMGroupSA205OR105(const CtiLMGroupSA205OR105& groupexp);

    virtual ~CtiLMGroupSA205OR105();

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupSA205OR105& operator=(const CtiLMGroupSA205OR105& right);

    int operator==(const CtiLMGroupSA205OR105& right) const;
    int operator!=(const CtiLMGroupSA205OR105& right) const;

    /* Static Members */

private:

    void restore(RWDBReader& rdr);
};
#endif

