#pragma once

#include "PaoStatisticsRecord.h"

namespace Cti {
namespace Porter {
namespace Statistics {

class PaoStatistics
{
    long _pao_id;

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
     * Creating a new record that has never been in the database
     * before
     *
     * @param pao_id Pao ID of the new statistics record
     */
    PaoStatistics(long pao_id);

    long getPaoId() const;

    void incrementRequests  (const CtiTime request_time);
    void incrementAttempts  (const CtiTime attempt_time,    YukonError_t attempt_status);
    void incrementCompletion(const CtiTime completion_time, YukonError_t completion_status);

    void collectRecords( std::vector<std::unique_ptr<CtiTableDynamicPaoStatistics>> & collection );
};

}
}
}

