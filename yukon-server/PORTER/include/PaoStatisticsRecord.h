#pragma once

#include "ctitime.h"

#include <string>
#include <vector>

namespace Cti {

namespace Database {
    class DatabaseWriter;
}

namespace Porter {
namespace Statistics {

class PaoStatisticsRecord
{
public:

    PaoStatisticsRecord *makeNewRecord(const CtiTime record_time);

    bool writeRecord(Database::DatabaseWriter &writer);

    CtiTime endTime() const;
    bool isDirty() const;

    void incrementRequests();
    void incrementAttempts(int status);
    void incrementCompletions();

    unsigned getCompletions() const;
    unsigned getRequests() const;

protected:

    enum StatisticTypes
    {
        Daily,
        Hourly,
        Monthly,
        Lifetime
    };

    //  Needs to be able to construct PaoStatisticRecords
    friend class PaoStatistics;

    PaoStatisticsRecord(long pao_id, StatisticTypes type, const CtiTime record_time);
    PaoStatisticsRecord(long pao_id, StatisticTypes type, const CtiTime record_time,
        long row_id,
        unsigned requests, unsigned attempts, unsigned completions,
        unsigned protocol_errors, unsigned comm_errors, unsigned system_errors);

    static CtiTime hourStart (const CtiTime t);
    static CtiTime dayStart  (const CtiTime t);
    static CtiTime monthStart(const CtiTime t);

private:

    static CtiTime calcIntervalStart(const StatisticTypes type, const CtiTime now);

    static const std::string &getStatisticTypeString(const StatisticTypes type);

    bool Insert(Database::DatabaseWriter &writer);
    bool Update(Database::DatabaseWriter &writer);

    long _row_id;

    long _pao_id;

    StatisticTypes _type;

    CtiTime _interval_start;

    int _requests;
    int _attempts;
    int _completions;

    int _system_errors;
    int _protocol_errors;
    int _comm_errors;

    bool _dirty;
};

}
}
}
