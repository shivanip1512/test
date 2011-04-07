#pragma once

#include "PaoStatisticsRecord.h"

namespace Cti {
    class RowReader;

namespace Database {
    class DatabaseReader;
}
}

namespace Cti {
namespace Porter {
namespace Statistics {

class PaoStatistics
{
    long _pao_id;

    struct record_elements
    {
        long row_id;
        unsigned requests;
        unsigned attempts;
        unsigned completions;
        unsigned protocol_errors;
        unsigned comm_errors;
        unsigned system_errors;
    };

    static record_elements extractRecordElements(RowReader &rdr);
    static PaoStatisticsRecord *makeRecord(const CtiTime record_time, long pao_id, PaoStatisticsRecord::StatisticTypes type, record_elements re);

    typedef boost::shared_ptr<PaoStatisticsRecord> PaoStatisticsRecordSPtr;

    PaoStatisticsRecordSPtr _hourly;
    PaoStatisticsRecordSPtr _daily;
    PaoStatisticsRecordSPtr _monthly;
    PaoStatisticsRecordSPtr _lifetime;

    void freshenRecords(const CtiTime time);

    bool freshenRecord(const CtiTime rotate_time, PaoStatisticsRecordSPtr &statistics_record, PaoStatisticsRecord::StatisticTypes type);

    std::vector<PaoStatisticsRecordSPtr> _stale_records;

public:

    /**
     * Restoring from a record
     *
     * @param rdr DatabaseReader
     */
    PaoStatistics(CtiTime reader_time, Database::DatabaseReader &rdr);

    /**
     * Creating a new record that has never been in the database
     * before
     *
     * @param pao_id Pao ID of the new statistics record
     */
    PaoStatistics(long pao_id);

    long getPaoId() const;

    static void buildDatabaseReader(Database::DatabaseReader &rdr, const CtiTime reader_time, std::set<long>::const_iterator id_begin, std::set<long>::const_iterator id_end);

    void incrementRequests  (const CtiTime request_time);
    void incrementAttempts  (const CtiTime attempt_time,    int attempt_status);
    void incrementCompletion(const CtiTime completion_time, int completion_status);

    bool isDirty() const;

    unsigned writeRecords(Database::DatabaseWriter &writer);
};

}
}
}

