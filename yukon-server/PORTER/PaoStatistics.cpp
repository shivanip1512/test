#include "precompiled.h"

#include "PaoStatistics.h"

#include "database_reader.h"
#include "InvalidReaderException.h"
#include "logger.h"

namespace Cti {
namespace Porter {
namespace Statistics {

PaoStatistics::PaoStatistics(long pao_id) :
    _pao_id(pao_id)
{
}


PaoStatistics::PaoStatistics(const CtiTime reader_time, Database::DatabaseReader &rdr)
{
    rdr[0] >> _pao_id;

    _lifetime.reset(makeRecord(reader_time, _pao_id, PaoStatisticsRecord::Lifetime, extractRecordElements(rdr[1])));

    if( ! rdr[8].isNull() )
    {
        _monthly.reset(makeRecord(reader_time, _pao_id, PaoStatisticsRecord::Monthly, extractRecordElements(rdr[8])));

        if( ! rdr[15].isNull() )
        {
            _daily.reset(makeRecord(reader_time, _pao_id, PaoStatisticsRecord::Daily, extractRecordElements(rdr[15])));

            if( ! rdr[22].isNull() )
            {
                _hourly.reset(makeRecord(reader_time, _pao_id, PaoStatisticsRecord::Hourly, extractRecordElements(rdr[22])));
            }
        }
    }
}


PaoStatistics::record_elements PaoStatistics::extractRecordElements(RowReader &rdr)
{
    record_elements re;

    rdr >> re.row_id;

    rdr >> re.requests;
    rdr >> re.attempts;
    rdr >> re.completions;

    rdr >> re.comm_errors;
    rdr >> re.protocol_errors;
    rdr >> re.system_errors;

    return re;
}


void PaoStatistics::buildDatabaseReader(Database::DatabaseReader &rdr, const CtiTime reader_time, std::set<long>::const_iterator id_begin, std::set<long>::const_iterator id_end)
{
    std::string sql =
        "SELECT "
            //  element 0
            "l.PAObjectID, "
            //  elements 1-7
            "l.DynamicPaoStatisticsId, l.Requests, l.Attempts, l.Completions, l.CommErrors, l.ProtocolErrors, l.SystemErrors, "
            //  elements 8-14
            "m.DynamicPaoStatisticsId, m.Requests, m.Attempts, m.Completions, m.CommErrors, m.ProtocolErrors, m.SystemErrors, "
            //  elements 15-21
            "d.DynamicPaoStatisticsId, d.Requests, d.Attempts, d.Completions, d.CommErrors, d.ProtocolErrors, d.SystemErrors, "
            //  elements 22-28
            "h.DynamicPaoStatisticsId, h.Requests, h.Attempts, h.Completions, h.CommErrors, h.ProtocolErrors, h.SystemErrors "
        "FROM "
            "DynamicPaoStatistics l "
            "LEFT OUTER JOIN DynamicPaoStatistics m ON (l.PAObjectID = m.PAObjectID AND m.StatisticType = 'Monthly' AND m.StartDateTime = ?) "
            "LEFT OUTER JOIN DynamicPaoStatistics d ON (m.PAObjectID = d.PAObjectID AND d.StatisticType = 'Daily'   AND d.StartDateTime = ?) "
            "LEFT OUTER JOIN DynamicPaoStatistics h ON (d.PAObjectID = h.PAObjectID AND h.StatisticType = 'Hourly'  AND h.StartDateTime = ?) "
        "WHERE "
            "l.StatisticType = 'Lifetime' AND "
            "l.PAObjectId IN ";

    std::ostringstream in_list;

    std::copy(id_begin, id_end, csv_output_iterator<long, std::ostringstream>(in_list));

    sql += "(";
    sql += in_list.str();
    sql += ")";

    rdr.setCommandText(sql);

    rdr << PaoStatisticsRecord::monthStart(reader_time);
    rdr << PaoStatisticsRecord::dayStart  (reader_time);
    rdr << PaoStatisticsRecord::hourStart (reader_time);
}


PaoStatisticsRecord *PaoStatistics::makeRecord(const CtiTime record_time, long pao_id, PaoStatisticsRecord::StatisticTypes type, record_elements re)
{
    return new PaoStatisticsRecord(
        pao_id, type, record_time,
        re.row_id,
        re.requests, re.attempts, re.completions,
        re.protocol_errors, re.comm_errors, re.system_errors);
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

        if( statistics_record->isDirty() )
        {
            _stale_records.push_back(statistics_record);
        }
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

void PaoStatistics::incrementAttempts(const CtiTime attempt_time, int attempt_status)
{
    freshenRecords(attempt_time);

    _hourly  ->incrementAttempts(attempt_status);
    _daily   ->incrementAttempts(attempt_status);
    _monthly ->incrementAttempts(attempt_status);
    _lifetime->incrementAttempts(attempt_status);
}

void PaoStatistics::incrementCompletion(const CtiTime completion_time, int completion_status)
{
    incrementAttempts(completion_time, completion_status);

    if(completion_status == NORMAL)
    {
        _hourly  ->incrementCompletions();
        _daily   ->incrementCompletions();
        _monthly ->incrementCompletions();
        _lifetime->incrementCompletions();

        if( _daily->getRequests() < _daily->getCompletions() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " PaoStatistics::incrementCompletion - daily requests < daily completions ";
            dout << "(" << _daily->getRequests() << " < " << _daily->getCompletions() << ") ";
            dout << "for ID " << _pao_id << "\n";
        }
    }
}


unsigned PaoStatistics::writeRecords(Database::DatabaseWriter &writer)
{
    unsigned rows_written = 0;

    for each(const PaoStatisticsRecordSPtr &p in _stale_records)
    {
        p->writeRecord(writer);
    }

    rows_written = _stale_records.size();

    _stale_records.clear();

    //  If lifetime is dirty, then all records exist and are dirty.
    if( _lifetime && _lifetime->isDirty() )
    {
        _lifetime->writeRecord(writer);
        _monthly ->writeRecord(writer);
        _daily   ->writeRecord(writer);
        _hourly  ->writeRecord(writer);

        rows_written += 4;
    }

    return rows_written;
}


long PaoStatistics::getPaoId() const
{
    return _pao_id;
}


bool PaoStatistics::isDirty() const
{
    return ! _stale_records.empty() || _lifetime && _lifetime->isDirty();
}


}
}
}
