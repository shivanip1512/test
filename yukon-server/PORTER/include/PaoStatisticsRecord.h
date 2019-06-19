#pragma once

#include "ctitime.h"

#include "yukon.h"

#include "tbl_dyn_paostatistics.h"

#include <string>
#include <vector>

namespace Cti {
namespace Porter {
namespace Statistics {

class PaoStatisticsRecord
{
public:

    enum StatisticTypes
    {
        Daily,
        Hourly,
        Monthly,
        Lifetime
    };

    PaoStatisticsRecord(long pao_id, StatisticTypes type, const CtiTime record_time);

    std::unique_ptr<CtiTableDynamicPaoStatistics> makeTableEntry();

    bool isStale(CtiTime timeNow) const;

    void incrementRequests();
    void incrementAttempts(const YukonError_t status);
    void incrementCompletions();

    unsigned getCompletions() const;
    unsigned getRequests() const;

    static CtiTime hourStart (const CtiTime t);
    static CtiTime dayStart  (const CtiTime t);
    static CtiTime monthStart(const CtiTime t);

protected:

private:

    static CtiTime calcIntervalStart(const StatisticTypes type, const CtiTime now);

    static const std::string &getStatisticTypeString(const StatisticTypes type);

    long _pao_id;

    const StatisticTypes _type;
    const CtiTime _interval_start;

    int _requests;
    int _attempts;
    int _completions;

    int _system_errors;
    int _protocol_errors;
    int _comm_errors;
};

}
}
}
