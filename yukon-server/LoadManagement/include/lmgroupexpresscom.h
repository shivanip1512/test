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
    
    ULONG getRouteId() const;
    const RWCString& getSerialNumber() const;
    ULONG getServiceAddress() const;
    ULONG getGeoAddress() const;
    ULONG getSubstationAddress() const;
    ULONG getFeederAddress() const;
    ULONG getZipCodeAddress() const;
    ULONG getUDAddress() const;
    ULONG getProgramAddress() const;
    ULONG getSplinterAddress() const;
    const RWCString& getAddressUsage() const;
    const RWCString& getRelayUsage() const;

    CtiLMGroupExpresscom& setRouteId(ULONG rteid);
    CtiLMGroupExpresscom& setSerialNumber(const RWCString& sn);
    CtiLMGroupExpresscom& setServiceAddress(ULONG add);
    CtiLMGroupExpresscom& setGeoAddress(ULONG add);
    CtiLMGroupExpresscom& setSubstationAddress(ULONG add);
    CtiLMGroupExpresscom& setFeederAddress(ULONG add);
    CtiLMGroupExpresscom& setZipCodeAddress(ULONG add);
    CtiLMGroupExpresscom& setUDAddress(ULONG add);
    CtiLMGroupExpresscom& setProgramAddress(ULONG add);
    CtiLMGroupExpresscom& setSplinterAddress(ULONG add);
    CtiLMGroupExpresscom& setAddressUsage(const RWCString& adduse);
    CtiLMGroupExpresscom& setRelayUsage(const RWCString& relayuse);

    void restoreExpresscomSpecificDatabaseEntries(RWDBReader& rdr);

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(ULONG refreshRate, ULONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(ULONG percent, ULONG period, ULONG defaultCount, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(ULONG sendRate, ULONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(ULONG offTime, ULONG period, int priority) const;

    virtual BOOL doesMasterCycleNeedToBeUpdated(ULONG secondsFrom1901, ULONG groupControlDone, ULONG offTime);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupExpresscom& operator=(const CtiLMGroupExpresscom& right);

    int operator==(const CtiLMGroupExpresscom& right) const;
    int operator!=(const CtiLMGroupExpresscom& right) const;

    /* Static Members */

private:

    ULONG       _routeid;
    RWCString   _serialnumber;
    ULONG       _serviceaddress;
    ULONG       _geoaddress;
    ULONG       _substationaddress;
    ULONG       _feederaddress;
    ULONG       _zipcodeaddress;
    ULONG       _udaddress;
    ULONG       _programaddress;
    ULONG       _splinteraddress;
    RWCString   _addressusage;
    RWCString   _relayusage;

    BOOL _refreshsent;

    void restore(RWDBReader& rdr);
};
#endif

