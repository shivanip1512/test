/*---------------------------------------------------------------------------
        Filename:  lmenergyexchangeoffer.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMEnergyExchangeOffer

        Initial Date:  5/14/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMENERGYEXCHANGEOFFERIMPL_H
#define CTILMENERGYEXCHANGEOFFERIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
#include "lmenergyexchangeofferrevision.h"

class CtiLMEnergyExchangeOffer : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiLMEnergyExchangeOffer )

    CtiLMEnergyExchangeOffer();
    CtiLMEnergyExchangeOffer(RWDBReader& rdr);
    CtiLMEnergyExchangeOffer(const CtiLMEnergyExchangeOffer& energyexchangeoffer);

    virtual ~CtiLMEnergyExchangeOffer();

    ULONG getPAOId() const;
    ULONG getOfferId() const;
    const RWCString& getRunStatus() const;
    const RWDBDateTime& getOfferDate() const;
    RWOrdered& getLMEnergyExchangeOfferRevisions();

    CtiLMEnergyExchangeOffer& setPAOId(ULONG paoid);
    CtiLMEnergyExchangeOffer& setOfferId(ULONG offid);
    CtiLMEnergyExchangeOffer& setRunStatus(const RWCString& runstat);
    CtiLMEnergyExchangeOffer& setOfferDate(const RWDBDateTime& offdate);

    void addLMEnergyExchangeProgramOfferTable();
    void updateLMEnergyExchangeProgramOfferTable();
    void deleteLMEnergyExchangeProgramOfferTable();
    void restoreDynamicData(RWDBReader& rdr);
    void dumpDynamicData();
    CtiLMEnergyExchangeOfferRevision* getCurrentOfferRevision();

    CtiLMEnergyExchangeOffer* replicate() const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMEnergyExchangeOffer& operator=(const CtiLMEnergyExchangeOffer& right);

    int operator==(const CtiLMEnergyExchangeOffer& right) const;
    int operator!=(const CtiLMEnergyExchangeOffer& right) const;

    // Static Members

    // Possible run statuses
    static const RWCString NullRunStatus;
    static const RWCString ScheduledRunStatus;
    static const RWCString OpenRunStatus;
    static const RWCString ClosingRunStatus;
    static const RWCString CurtailmentPendingRunStatus;
    static const RWCString CurtailmentActiveRunStatus;
    static const RWCString CompletedRunStatus;
    static const RWCString CanceledRunStatus;
    //static const RWCString ActiveRunStatus;
    //static const RWCString StoppedEarlyRunStatus;
    //static const RWCString CompletedRunStatus;

private:

    ULONG _paoid;
    ULONG _offerid;
    RWCString _runstatus;
    RWDBDateTime _offerdate;

    RWOrdered _lmenergyexchangeofferrevisions;

    mutable RWRecursiveLock<RWMutexLock> _mutex;

    void restore(RWDBReader& rdr);
};
#endif

