/*---------------------------------------------------------------------------
        Filename:  lmgroupsadigitalorgolay.h
        
        Programmer:  Aaron Lauinger
        
        Description:    Header file for CtiLMGroupSADigitalORGolay

        Initial Date:  03/05/2004
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2004
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMGROUPSADIGITALORGOLAY_H
#define CTILMGROUPSADIGITALORGOLAY_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>

#include "lmgroupbase.h"
                
class CtiLMGroupSADigitalORGolay : public CtiLMGroupBase
{
public:

RWDECLARE_COLLECTABLE( CtiLMGroupSADigitalORGolay )

    CtiLMGroupSADigitalORGolay();
    CtiLMGroupSADigitalORGolay(RWDBReader& rdr);
    CtiLMGroupSADigitalORGolay(const CtiLMGroupSADigitalORGolay& groupexp);

    virtual ~CtiLMGroupSADigitalORGolay();

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiPILRequestMsg* createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiPILRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiPILRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const;
    virtual CtiPILRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiPILRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupSADigitalORGolay& operator=(const CtiLMGroupSADigitalORGolay& right);

    int operator==(const CtiLMGroupSADigitalORGolay& right) const;
    int operator!=(const CtiLMGroupSADigitalORGolay& right) const;

    /* Static Members */

private:

    void restore(RWDBReader& rdr);
};
#endif

