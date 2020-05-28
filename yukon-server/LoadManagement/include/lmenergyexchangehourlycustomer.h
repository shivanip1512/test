#pragma once

#include "row_reader.h"
#include "collectable.h"

class CtiLMEnergyExchangeHourlyCustomer
{

public:

    DECLARE_COLLECTABLE( CtiLMEnergyExchangeHourlyCustomer );

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

    CtiLMEnergyExchangeHourlyCustomer& operator=(const CtiLMEnergyExchangeHourlyCustomer& right);

    int operator==(const CtiLMEnergyExchangeHourlyCustomer& right) const;
    int operator!=(const CtiLMEnergyExchangeHourlyCustomer& right) const;

    std::size_t getMemoryConsumption() const;

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
