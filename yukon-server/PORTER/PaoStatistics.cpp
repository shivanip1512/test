#include "precompiled.h"

#include "PaoStatistics.h"
#include "logger.h"

namespace Cti {
namespace Porter {
namespace Statistics {

PaoStatistics::PaoStatistics(long pao_id) :
    _pao_id(pao_id)
{
}

void PaoStatistics::freshenRecords(const CtiTime freshen_time)
{
    if( freshenRecord(freshen_time, _hourly, PaoStatisticsRecord::Hourly) )
    {
        if( freshenRecord(freshen_time, _daily, PaoStatisticsRecord::Daily) )
        {
            if( freshenRecord(freshen_time, _monthly, PaoStatisticsRecord::Monthly) )
            {
                freshenRecord(freshen_time, _lifetime, PaoStatisticsRecord::Lifetime);
            }
        }
    }
}

bool PaoStatistics::freshenRecord(const CtiTime freshen_time, PaoStatisticsRecordSPtr &statistics_record, PaoStatisticsRecord::StatisticTypes type)
{
    if( statistics_record )
    {
        if( ! statistics_record->isStale(freshen_time) )
        {
            //  The existing record is not stale, so we don't need to freshen this record
            return false;
        }

        _stale_records.push_back(statistics_record);
    }

    statistics_record.reset(new PaoStatisticsRecord(_pao_id, type, freshen_time));

    return true;
}

void PaoStatistics::incrementRequests(const CtiTime request_time)
{
    freshenRecords(request_time);

    _hourly  ->incrementRequests();
    _daily   ->incrementRequests();
    _monthly ->incrementRequests();
    _lifetime->incrementRequests();
}

void PaoStatistics::incrementAttempts(const CtiTime attempt_time, YukonError_t attempt_status)
{
    freshenRecords(attempt_time);

    _hourly  ->incrementAttempts(attempt_status);
    _daily   ->incrementAttempts(attempt_status);
    _monthly ->incrementAttempts(attempt_status);
    _lifetime->incrementAttempts(attempt_status);
}

void PaoStatistics::incrementCompletion(const CtiTime completion_time, YukonError_t completion_status)
{
    incrementAttempts(completion_time, completion_status);

    if(completion_status == ClientErrors::None)
    {
        _hourly  ->incrementCompletions();
        _daily   ->incrementCompletions();
        _monthly ->incrementCompletions();
        _lifetime->incrementCompletions();
    }
}

void PaoStatistics::collectRecords( std::vector<std::unique_ptr<CtiTableDynamicPaoStatistics>> & collection )
{
    for ( const PaoStatisticsRecordSPtr &p : _stale_records )
    {
        collection.emplace_back( p->makeTableEntry() );
    }

    if ( _lifetime )
    {
        collection.emplace_back( _lifetime->makeTableEntry() );
    }

    if ( _monthly )
    {
        collection.emplace_back( _monthly->makeTableEntry() );
    }

    if ( _daily )
    {
        collection.emplace_back( _daily->makeTableEntry() );
    }

    if ( _hourly )
    {
        collection.emplace_back( _hourly->makeTableEntry() );
    }
}

long PaoStatistics::getPaoId() const
{
    return _pao_id;
}

}
}
}
