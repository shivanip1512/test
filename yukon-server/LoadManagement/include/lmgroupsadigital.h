/*---------------------------------------------------------------------------
        Filename:  lmgroupsadigital.h
        
        Programmer:  Aaron Lauinger
        
        Description:    Header file for CtiLMGroupSADigital

        Initial Date:  03/05/2004
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2004
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMGROUPSADIGITAL_H
#define CTILMGROUPSADIGITAL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>

#include "lmgroupbase.h"
                
class CtiLMGroupSADigital : public CtiLMGroupBase
{
public:

RWDECLARE_COLLECTABLE( CtiLMGroupSADigital )

    CtiLMGroupSADigital();
    CtiLMGroupSADigital(RWDBReader& rdr);
    CtiLMGroupSADigital(const CtiLMGroupSADigital& groupexp);

    virtual ~CtiLMGroupSADigital();

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupSADigital& operator=(const CtiLMGroupSADigital& right);

    int operator==(const CtiLMGroupSADigital& right) const;
    int operator!=(const CtiLMGroupSADigital& right) const;

    /* Static Members */

private:

    void restore(RWDBReader& rdr);
};
#endif

