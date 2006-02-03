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
#include "ctitime.h"
#include "lmenergyexchangehourlyoffer.h"
#include "observe.h"
                
class CtiLMEnergyExchangeOfferRevision : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiLMEnergyExchangeOfferRevision )

    CtiLMEnergyExchangeOfferRevision();
    CtiLMEnergyExchangeOfferRevision(RWDBReader& rdr);
    CtiLMEnergyExchangeOfferRevision(const CtiLMEnergyExchangeOfferRevision& revision);

    virtual ~CtiLMEnergyExchangeOfferRevision();
    
    LONG getOfferId() const;
    LONG getRevisionNumber() const;
    const CtiTime& getActionDateTime() const;
    const CtiTime& getNotificationDateTime() const;
    const CtiTime& getOfferExpirationDateTime() const;
    const string& getAdditionalInfo() const;
    vector<CtiLMEnergyExchangeHourlyOffer*>& getLMEnergyExchangeHourlyOffers();

    CtiLMEnergyExchangeOfferRevision& setOfferId(LONG offid);
    CtiLMEnergyExchangeOfferRevision& setRevisionNumber(LONG revnum);
    CtiLMEnergyExchangeOfferRevision& setActionDateTime(const CtiTime& actiontime);
    CtiLMEnergyExchangeOfferRevision& setNotificationDateTime(const CtiTime& notifytime);
    CtiLMEnergyExchangeOfferRevision& setOfferExpirationDateTime(const CtiTime& expirationtime);
    CtiLMEnergyExchangeOfferRevision& setAdditionalInfo(const string& additional);

    CtiLMEnergyExchangeOfferRevision* replicate() const;

    void addLMEnergyExchangeOfferRevisionTable();
    void updateLMEnergyExchangeOfferRevisionTable();
    LONG getFirstCurtailHour() const;
    LONG getLastCurtailHour() const;
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

    LONG _offerid;
    LONG _revisionnumber;
    CtiTime _actiondatetime;
    CtiTime _notificationdatetime;
    CtiTime _offerexpirationdatetime;
    string _additionalinfo;

    vector<CtiLMEnergyExchangeHourlyOffer*> _lmenergyexchangehourlyoffers;
};
#endif

