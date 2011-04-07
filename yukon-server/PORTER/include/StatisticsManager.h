#pragma once

#include "PaoStatistics.h"
#include "ThreadStatusKeeper.h"

#include <map>
#include <vector>

namespace Cti {
namespace Porter {

class StatisticsManager
{
    struct statistics_event_t
    {
        enum EventType
        {
            Attempt,
            Completion,
            Request,
            Deletion
        };

        CtiTime time;
        long pao_id;
        int result;
        EventType action;
    };

    typedef std::map<long, Statistics::PaoStatistics *> id_statistics_map;

    id_statistics_map _pao_statistics;

    void loadPaoStatistics(const std::set<long> &pao_ids);
    Statistics::PaoStatistics &getPaoStatistics(long pao_id);
    void deletePaoStatistics(long pao_id);

    bool pruneDaily(Database::DatabaseConnection &conn);

    CtiCriticalSection _event_queue_lock;

    typedef std::vector<statistics_event_t> event_queue_t;

    event_queue_t  _event_queues[2];

    event_queue_t *_active_event_queue;
    event_queue_t *_inactive_event_queue;

    void enqueueEvent(statistics_event_t::EventType action, int result, long port_id, long device_id, long target_id);
    void processEvent(const statistics_event_t &evt);

    void runWriterThreads(unsigned num_threads, ThreadStatusKeeper &threadKeeper);
    static void writeRecordRange(unsigned thread_num, unsigned chunk_size, const id_statistics_map::const_iterator begin, const id_statistics_map::const_iterator end, ThreadStatusKeeper *threadKeeper);

public:

    StatisticsManager();
    ~StatisticsManager();

    void processEvents(ThreadStatusKeeper &threadKeeper);
    void writeRecords (ThreadStatusKeeper &threadKeeper);

    void newRequest   (long port_id, long device_id, long target_id,             unsigned &messageFlags);
    void newAttempt   (long port_id, long device_id, long target_id, int result, unsigned  messageFlags);
    void newCompletion(long port_id, long device_id, long target_id, int result, unsigned &messageFlags);

    void deleteRecord(long pao_id);
};

extern StatisticsManager PorterStatisticsManager;

}
}

