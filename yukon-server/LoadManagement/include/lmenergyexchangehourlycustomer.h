/*---------------------------------------------------------------------------
        Filename:  lmenergyexchangehourlycustomer.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMEnergyExchangeHourlyCustomer

        Initial Date:  5/15/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMENERGYEXCHANGEHOURLYCUSTOMERIMPL_H
#define CTILMENERGYEXCHANGEHOURLYCUSTOMERIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
                
class CtiLMEnergyExchangeHourlyCustomer : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiLMEnergyExchangeHourlyCustomer )

    CtiLMEnergyExchangeHourlyCustomer();
    CtiLMEnergyExchangeHourlyCustomer(RWDBReader& rdr);
    CtiLMEnergyExchangeHourlyCustomer(const CtiLMEnergyExchangeHourlyCustomer& customer);

    virtual ~CtiLMEnergyExchangeHourlyCustomer();
    
    LONG getCustomerId() const;
    LONG getOfferId() const;
    LONG getRevisionNumber() const;
    LONG getHour() const;
    DOUBLE getAmountCommitted() const;

    CtiLMEnergyExchangeHourlyCustomer& setCustomerId(LONG custid);
    CtiLMEnergyExchangeHourlyCustomer& setOfferId(LONG offid);
    CtiLMEnergyExchangeHourlyCustomer& setRevisionNumber(LONG revnumber);
    CtiLMEnergyExchangeHourlyCustomer& setHour(LONG hour);
    CtiLMEnergyExchangeHourlyCustomer& setAmountCommitted(DOUBLE committed);

    CtiLMEnergyExchangeHourlyCustomer* replicate() const;

    void addLMEnergyExchangeHourlyCustomerTable();
    void updateLMEnergyExchangeHourlyCustomerTable();
    
    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMEnergyExchangeHourlyCustomer& operator=(const CtiLMEnergyExchangeHourlyCustomer& right);

    int operator==(const CtiLMEnergyExchangeHourlyCustomer& right) const;
    int operator!=(const CtiLMEnergyExchangeHourlyCustomer& right) const;

    // Static Members

    // Possible 

protected:

    void restore(RWDBReader& rdr);

private:

    LONG _customerid;
    LONG _offerid;
    LONG _revisionnumber;
    LONG _hour;
    DOUBLE _amountcommitted;
};
#endif

