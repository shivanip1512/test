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
    
    ULONG getCustomerId() const;
    ULONG getOfferId() const;
    ULONG getRevisionNumber() const;
    ULONG getHour() const;
    DOUBLE getAmountCommitted() const;

    CtiLMEnergyExchangeHourlyCustomer& setCustomerId(ULONG custid);
    CtiLMEnergyExchangeHourlyCustomer& setOfferId(ULONG offid);
    CtiLMEnergyExchangeHourlyCustomer& setRevisionNumber(ULONG revnumber);
    CtiLMEnergyExchangeHourlyCustomer& setHour(ULONG hour);
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

    ULONG _customerid;
    ULONG _offerid;
    ULONG _revisionnumber;
    ULONG _hour;
    DOUBLE _amountcommitted;
};
#endif

