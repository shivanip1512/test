#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
#include "lmenergyexchangeofferrevision.h"
#include "row_reader.h"
#include "rwutil.h"

class CtiLMEnergyExchangeOffer : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiLMEnergyExchangeOffer )

    CtiLMEnergyExchangeOffer();
    CtiLMEnergyExchangeOffer(Cti::RowReader &rdr);
    CtiLMEnergyExchangeOffer(const CtiLMEnergyExchangeOffer& energyexchangeoffer);

    virtual ~CtiLMEnergyExchangeOffer();

    LONG getPAOId() const;
    LONG getOfferId() const;
    const std::string& getRunStatus() const;
    const CtiTime& getOfferDate() const;
    std::vector<CtiLMEnergyExchangeOfferRevision*>& getLMEnergyExchangeOfferRevisions();

    CtiLMEnergyExchangeOffer& setPAOId(LONG paoid);
    CtiLMEnergyExchangeOffer& setOfferId(LONG offid);
    CtiLMEnergyExchangeOffer& setRunStatus(const std::string& runstat);
    CtiLMEnergyExchangeOffer& setOfferDate(const CtiTime& offdate);

    void addLMEnergyExchangeProgramOfferTable();
    void updateLMEnergyExchangeProgramOfferTable(CtiTime& currentDateTime);
    void deleteLMEnergyExchangeProgramOfferTable();
    void restoreDynamicData(Cti::RowReader &rdr);
    void dumpDynamicData();
    void dumpDynamicData(CtiTime& currentDateTime);
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
    static const std::string NullRunStatus;
    static const std::string ScheduledRunStatus;
    static const std::string OpenRunStatus;
    static const std::string ClosingRunStatus;
    static const std::string CurtailmentPendingRunStatus;
    static const std::string CurtailmentActiveRunStatus;
    static const std::string CompletedRunStatus;
    static const std::string CanceledRunStatus;

private:

    LONG _paoid;
    LONG _offerid;
    std::string _runstatus;
    CtiTime _offerdate;

    std::vector<CtiLMEnergyExchangeOfferRevision*> _lmenergyexchangeofferrevisions;

    void restore(Cti::RowReader &rdr);
};

