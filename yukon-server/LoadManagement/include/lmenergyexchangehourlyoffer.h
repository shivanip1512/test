/*---------------------------------------------------------------------------
        Filename:  lmenergyexchangehourlyoffer.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMEnergyExchangeHourlyOffer

        Initial Date:  5/14/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMENERGYEXCHANGEHOURLYOFFERIMPL_H
#define CTILMENERGYEXCHANGEHOURLYOFFERIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
                
class CtiLMEnergyExchangeHourlyOffer : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiLMEnergyExchangeHourlyOffer )

    CtiLMEnergyExchangeHourlyOffer();
    CtiLMEnergyExchangeHourlyOffer(RWDBReader& rdr);
    CtiLMEnergyExchangeHourlyOffer(const CtiLMEnergyExchangeHourlyOffer& customer);

    virtual ~CtiLMEnergyExchangeHourlyOffer();
    
    LONG getOfferId() const;
    LONG getRevisionNumber() const;
    LONG getHour() const;
    DOUBLE getPrice() const;
    DOUBLE getAmountRequested() const;

    CtiLMEnergyExchangeHourlyOffer& setOfferId(LONG offid);
    CtiLMEnergyExchangeHourlyOffer& setRevisionNumber(LONG revnum);
    CtiLMEnergyExchangeHourlyOffer& setHour(LONG hour);
    CtiLMEnergyExchangeHourlyOffer& setPrice(DOUBLE price);
    CtiLMEnergyExchangeHourlyOffer& setAmountRequested(DOUBLE amtrequested);

    CtiLMEnergyExchangeHourlyOffer* replicate() const;

    void addLMEnergyExchangeHourlyOfferTable();
    void updateLMEnergyExchangeHourlyOfferTable();
    void restoreDynamicData(RWDBReader& rdr);
    void dumpDynamicData();
    
    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMEnergyExchangeHourlyOffer& operator=(const CtiLMEnergyExchangeHourlyOffer& right);

    int operator==(const CtiLMEnergyExchangeHourlyOffer& right) const;
    int operator!=(const CtiLMEnergyExchangeHourlyOffer& right) const;

    // Static Members

    // Possible  statuses

protected:

    void restore(RWDBReader& rdr);

private:

    LONG _offerid;
    LONG _revisionnumber;
    LONG _hour;
    DOUBLE _price;
    DOUBLE _amountrequested;
};
#endif

