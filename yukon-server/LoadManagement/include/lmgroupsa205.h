/*---------------------------------------------------------------------------
        Filename:  lmgroupsa205.h
        
        Programmer:  Aaron Lauinger
        
        Description:    Header file for CtiLMGroupSA205

        Initial Date:  03/05/2004
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2004
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMGROUPSA205_H
#define CTILMGROUPSA205_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>

#include "lmgroupbase.h"
                
class CtiLMGroupSA205 : public CtiLMGroupBase
{
public:

RWDECLARE_COLLECTABLE( CtiLMGroupSA205 )

    CtiLMGroupSA205();
    CtiLMGroupSA205(RWDBReader& rdr);
    CtiLMGroupSA205(const CtiLMGroupSA205& groupexp);

    virtual ~CtiLMGroupSA205();

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupSA205& operator=(const CtiLMGroupSA205& right);

    int operator==(const CtiLMGroupSA205& right) const;
    int operator!=(const CtiLMGroupSA205& right) const;

    /* Static Members */

private:

    void restore(RWDBReader& rdr);
};
#endif

