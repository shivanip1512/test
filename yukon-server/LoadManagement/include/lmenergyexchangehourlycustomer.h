#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
#include "row_reader.h"
                
class CtiLMEnergyExchangeHourlyCustomer : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiLMEnergyExchangeHourlyCustomer )

    CtiLMEnergyExchangeHourlyCustomer();
    CtiLMEnergyExchangeHourlyCustomer(Cti::RowReader &rdr);
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

    void restore(Cti::RowReader &rdr);

private:

    LONG _customerid;
    LONG _offerid;
    LONG _revisionnumber;
    LONG _hour;
    DOUBLE _amountcommitted;
};
