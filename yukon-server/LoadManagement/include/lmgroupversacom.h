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
    
    ULONG getUtilityAddress() const;
    ULONG getSectionAddress() const;
    ULONG getClassAddress() const;
    ULONG getDivisionAddress() const;
    const RWCString& getAddressUsage() const;
    const RWCString& getRelayUsage() const;
    ULONG getRouteId() const;

    CtiLMGroupVersacom& setUtilityAddress(ULONG utiladd);
    CtiLMGroupVersacom& setSectionAddress(ULONG sectadd);
    CtiLMGroupVersacom& setClassAddress(ULONG classadd);
    CtiLMGroupVersacom& setDivisionAddress(ULONG divadd);
    CtiLMGroupVersacom& setAddressUsage(const RWCString& adduse);
    CtiLMGroupVersacom& setRelayUsage(const RWCString& relayuse);
    CtiLMGroupVersacom& setRouteId(ULONG rteid);

    void restoreVersacomSpecificDatabaseEntries(RWDBReader& rdr);

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(ULONG refreshRate, ULONG shedTime) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(ULONG percent, ULONG period, ULONG defaultCount) const;
    virtual CtiRequestMsg* createRotationRequestMsg(ULONG sendRate, ULONG shedTime) const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupVersacom& operator=(const CtiLMGroupVersacom& right);

    int operator==(const CtiLMGroupVersacom& right) const;
    int operator!=(const CtiLMGroupVersacom& right) const;

    /* Static Members */

private:

    ULONG _utilityaddress;
    ULONG _sectionaddress;
    ULONG _classaddress;
    ULONG _divisionaddress;
    RWCString _addressusage;
    RWCString _relayusage;
    ULONG _routeid;

    mutable RWRecursiveLock<RWMutexLock> _mutex;

    void restore(RWDBReader& rdr);
};
#endif

