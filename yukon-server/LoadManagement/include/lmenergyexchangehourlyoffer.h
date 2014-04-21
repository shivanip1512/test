#pragma once

#include "row_reader.h"
#include "collectable.h"

class CtiLMEnergyExchangeHourlyOffer
{

public:

DECLARE_COLLECTABLE( CtiLMEnergyExchangeHourlyOffer );

    CtiLMEnergyExchangeHourlyOffer();
    CtiLMEnergyExchangeHourlyOffer(Cti::RowReader &rdr);
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
    void restoreDynamicData();
    void dumpDynamicData();

    CtiLMEnergyExchangeHourlyOffer& operator=(const CtiLMEnergyExchangeHourlyOffer& right);

    int operator==(const CtiLMEnergyExchangeHourlyOffer& right) const;
    int operator!=(const CtiLMEnergyExchangeHourlyOffer& right) const;

    // Static Members

    // Possible  statuses

protected:

    void restore(Cti::RowReader &rdr);

private:

    LONG _offerid;
    LONG _revisionnumber;
    LONG _hour;
    DOUBLE _price;
    DOUBLE _amountrequested;
};


