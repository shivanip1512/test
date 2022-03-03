#pragma once

#include "ctitime.h"
#include "lmenergyexchangehourlyoffer.h"
#include "row_reader.h"

class CtiLMEnergyExchangeOfferRevision
{

public:

DECLARE_COLLECTABLE( CtiLMEnergyExchangeOfferRevision );

    CtiLMEnergyExchangeOfferRevision();
    CtiLMEnergyExchangeOfferRevision(Cti::RowReader &rdr);
    CtiLMEnergyExchangeOfferRevision(const CtiLMEnergyExchangeOfferRevision& revision);

    virtual ~CtiLMEnergyExchangeOfferRevision();

    LONG getOfferId() const;
    LONG getRevisionNumber() const;
    const CtiTime& getActionDateTime() const;
    const CtiTime& getNotificationDateTime() const;
    const CtiTime& getOfferExpirationDateTime() const;
    const std::string& getAdditionalInfo() const;
    std::vector<CtiLMEnergyExchangeHourlyOffer*>& getLMEnergyExchangeHourlyOffers();
    const std::vector<CtiLMEnergyExchangeHourlyOffer*>& getLMEnergyExchangeHourlyOffers() const;

    CtiLMEnergyExchangeOfferRevision& setOfferId(LONG offid);
    CtiLMEnergyExchangeOfferRevision& setRevisionNumber(LONG revnum);
    CtiLMEnergyExchangeOfferRevision& setActionDateTime(const CtiTime& actiontime);
    CtiLMEnergyExchangeOfferRevision& setNotificationDateTime(const CtiTime& notifytime);
    CtiLMEnergyExchangeOfferRevision& setOfferExpirationDateTime(const CtiTime& expirationtime);
    CtiLMEnergyExchangeOfferRevision& setAdditionalInfo(const std::string& additional);

    CtiLMEnergyExchangeOfferRevision* replicate() const;

    void addLMEnergyExchangeOfferRevisionTable();
    void updateLMEnergyExchangeOfferRevisionTable();
    LONG getFirstCurtailHour() const;
    LONG getLastCurtailHour() const;
    void restoreDynamicData();
    void dumpDynamicData();

    std::size_t getMemoryConsumption() const;

    CtiLMEnergyExchangeOfferRevision& operator=(const CtiLMEnergyExchangeOfferRevision& right);

    int operator==(const CtiLMEnergyExchangeOfferRevision& right) const;
    int operator!=(const CtiLMEnergyExchangeOfferRevision& right) const;

    // Static Members

    // Possible  statuses

protected:

    void restore(Cti::RowReader &rdr);

private:

    LONG _offerid;
    LONG _revisionnumber;
    CtiTime _actiondatetime;
    CtiTime _notificationdatetime;
    CtiTime _offerexpirationdatetime;
    std::string _additionalinfo;

    std::vector<CtiLMEnergyExchangeHourlyOffer*> _lmenergyexchangehourlyoffers;
};
