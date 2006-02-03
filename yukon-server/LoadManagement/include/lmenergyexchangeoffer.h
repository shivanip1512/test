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

    LONG getPAOId() const;
    LONG getOfferId() const;
    const string& getRunStatus() const;
    const CtiTime& getOfferDate() const;
    std::vector<CtiLMEnergyExchangeOfferRevision*>& getLMEnergyExchangeOfferRevisions();

    CtiLMEnergyExchangeOffer& setPAOId(LONG paoid);
    CtiLMEnergyExchangeOffer& setOfferId(LONG offid);
    CtiLMEnergyExchangeOffer& setRunStatus(const string& runstat);
    CtiLMEnergyExchangeOffer& setOfferDate(const CtiTime& offdate);

    void addLMEnergyExchangeProgramOfferTable();
    void updateLMEnergyExchangeProgramOfferTable(RWDBConnection& conn, CtiTime& currentDateTime);
    void deleteLMEnergyExchangeProgramOfferTable();
    void restoreDynamicData(RWDBReader& rdr);
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime);
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
    static const string NullRunStatus;
    static const string ScheduledRunStatus;
    static const string OpenRunStatus;
    static const string ClosingRunStatus;
    static const string CurtailmentPendingRunStatus;
    static const string CurtailmentActiveRunStatus;
    static const string CompletedRunStatus;
    static const string CanceledRunStatus;
    //static const string ActiveRunStatus;
    //static const string StoppedEarlyRunStatus;
    //static const string CompletedRunStatus;

private:

    LONG _paoid;
    LONG _offerid;
    string _runstatus;
    CtiTime _offerdate;

    std::vector<CtiLMEnergyExchangeOfferRevision*> _lmenergyexchangeofferrevisions;

    void restore(RWDBReader& rdr);
};
#endif

