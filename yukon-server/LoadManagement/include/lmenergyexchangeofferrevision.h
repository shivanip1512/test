/*---------------------------------------------------------------------------
        Filename:  lmenergyexchangeofferrevision.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMEnergyExchangeOfferRevision

        Initial Date:  5/9/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMENERGYEXCHANGEOFFERREVISIONIMPL_H
#define CTILMENERGYEXCHANGEOFFERREVISIONIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
                
class CtiLMEnergyExchangeOfferRevision : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiLMEnergyExchangeOfferRevision )

    CtiLMEnergyExchangeOfferRevision();
    CtiLMEnergyExchangeOfferRevision(RWDBReader& rdr);
    CtiLMEnergyExchangeOfferRevision(const CtiLMEnergyExchangeOfferRevision& revision);

    virtual ~CtiLMEnergyExchangeOfferRevision();
    
    ULONG getOfferId() const;
    ULONG getRevisionNumber() const;
    const RWDBDateTime& getActionDateTime() const;
    const RWDBDateTime& getNotificationDateTime() const;
    const RWDBDateTime& getOfferExpirationDateTime() const;
    const RWCString& getAdditionalInfo() const;
    RWOrdered& getLMEnergyExchangeHourlyOffers();

    CtiLMEnergyExchangeOfferRevision& setOfferId(ULONG offid);
    CtiLMEnergyExchangeOfferRevision& setRevisionNumber(ULONG revnum);
    CtiLMEnergyExchangeOfferRevision& setActionDateTime(const RWDBDateTime& actiontime);
    CtiLMEnergyExchangeOfferRevision& setNotificationDateTime(const RWDBDateTime& notifytime);
    CtiLMEnergyExchangeOfferRevision& setOfferExpirationDateTime(const RWDBDateTime& expirationtime);
    CtiLMEnergyExchangeOfferRevision& setAdditionalInfo(const RWCString& additional);

    CtiLMEnergyExchangeOfferRevision* replicate() const;

    void addLMEnergyExchangeOfferRevisionTable();
    void updateLMEnergyExchangeOfferRevisionTable();
    ULONG getFirstCurtailHour() const;
    ULONG getLastCurtailHour() const;
    void restoreDynamicData(RWDBReader& rdr);
    void dumpDynamicData();
    
    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMEnergyExchangeOfferRevision& operator=(const CtiLMEnergyExchangeOfferRevision& right);

    int operator==(const CtiLMEnergyExchangeOfferRevision& right) const;
    int operator!=(const CtiLMEnergyExchangeOfferRevision& right) const;

    // Static Members

    // Possible  statuses

protected:

    void restore(RWDBReader& rdr);

private:

    ULONG _offerid;
    ULONG _revisionnumber;
    RWDBDateTime _actiondatetime;
    RWDBDateTime _notificationdatetime;
    RWDBDateTime _offerexpirationdatetime;
    RWCString _additionalinfo;

    RWOrdered _lmenergyexchangehourlyoffers;

    mutable RWRecursiveLock<RWMutexLock> _mutex;
};
#endif

