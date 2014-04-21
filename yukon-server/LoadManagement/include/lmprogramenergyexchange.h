#pragma once

#include "lmprogrambase.h"
#include "lmenergyexchangeoffer.h"
#include "lmenergyexchangecustomer.h"

class CtiLMProgramEnergyExchange : public CtiLMProgramBase
{

public:

DECLARE_COLLECTABLE( CtiLMProgramEnergyExchange );

    CtiLMProgramEnergyExchange();
    CtiLMProgramEnergyExchange(Cti::RowReader &rdr);
    CtiLMProgramEnergyExchange(const CtiLMProgramEnergyExchange& energyexchangeprog);

    virtual ~CtiLMProgramEnergyExchange();

    LONG getMinNotifyTime() const;
    const std::string& getHeading() const;
    const std::string& getMessageHeader() const;
    const std::string& getMessageFooter() const;
    const std::string& getCanceledMsg() const;
    const std::string& getStoppedEarlyMsg() const;
    std::vector<CtiLMEnergyExchangeOffer*>& getLMEnergyExchangeOffers();
    const std::vector<CtiLMEnergyExchangeOffer*>& getLMEnergyExchangeOffers() const;
    std::vector<CtiLMEnergyExchangeCustomer*>& getLMEnergyExchangeCustomers();
    const std::vector<CtiLMEnergyExchangeCustomer*>& getLMEnergyExchangeCustomers() const;

    CtiLMProgramEnergyExchange& setMinNotifyTime(LONG notifytime);
    CtiLMProgramEnergyExchange& setHeading(const std::string& head);
    CtiLMProgramEnergyExchange& setMessageHeader(const std::string& msgheader);
    CtiLMProgramEnergyExchange& setMessageFooter(const std::string& msgfooter);
    CtiLMProgramEnergyExchange& setCanceledMsg(const std::string& canceled);
    CtiLMProgramEnergyExchange& setStoppedEarlyMsg(const std::string& stoppedearly);

    BOOL isOfferWithId(LONG offerid);
    BOOL isOfferRevisionOpen(LONG offerID, LONG revisionNumber);
    CtiLMEnergyExchangeOffer* getOfferWithId(LONG offerid);
    //void restoreEnergyExchangeSpecificDatabaseEntries(Cti::RowReader &rdr);
    void notifyCustomers(CtiLMEnergyExchangeOffer* offer, CtiMultiMsg* multiNotifMsg);
    void notifyCustomersOfCancel(CtiLMEnergyExchangeOffer* offer, CtiMultiMsg* multiNotifMsg);
    void dumpDynamicData();
    void dumpDynamicData(CtiTime& currentDateTime);

    virtual CtiLMProgramBaseSPtr replicate() const;
    virtual DOUBLE reduceProgramLoad(DOUBLE loadReductionNeeded, LONG currentPriority, std::vector<CtiLMControlAreaTrigger*> controlAreaTriggers, LONG secondsFromBeginningOfDay, CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL isTriggerCheckNeeded);
    virtual BOOL hasControlHoursAvailable();
    virtual BOOL stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, CtiTime currentTime);
    virtual BOOL handleManualControl(CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg);

    CtiLMProgramEnergyExchange& operator=(const CtiLMProgramEnergyExchange& right);

    int operator==(const CtiLMProgramEnergyExchange& right) const;
    int operator!=(const CtiLMProgramEnergyExchange& right) const;

    // Static Members

    // Possible

private:

    LONG _minnotifytime;
    std::string _heading;
    std::string _messageheader;
    std::string _messagefooter;
    std::string _canceledmsg;
    std::string _stoppedearlymsg;

    std::vector<CtiLMEnergyExchangeOffer*>     _lmenergyexchangeoffers;
    std::vector<CtiLMEnergyExchangeCustomer*>  _lmenergyexchangecustomers;

    void restore(Cti::RowReader &rdr);
};

typedef boost::shared_ptr< CtiLMProgramEnergyExchange > CtiLMProgramEnergyExchangeSPtr;
