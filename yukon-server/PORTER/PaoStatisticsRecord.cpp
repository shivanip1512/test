#include "yukon.h"

#include "PaoStatisticsRecord.h"

#include "database_writer.h"
#include "ctidate.h"

#include "dsm2err.h"  //  for GetErrorType, please fix me

namespace Cti {
namespace Porter {
namespace Statistics {

PaoStatisticsRecord::PaoStatisticsRecord(long pao_id, StatisticTypes type, const CtiTime record_time) :
    _pao_id(pao_id),
    _type(type),
    _row_id(0),
    _requests(0),
    _attempts(0),
    _completions(0),
    _comm_errors(0),
    _protocol_errors(0),
    _system_errors(0),
    _dirty(false)
{
    _interval_start = calcIntervalStart(type, record_time);
}


PaoStatisticsRecord::PaoStatisticsRecord(long pao_id, StatisticTypes type, const CtiTime record_time,
    long row_id,
    unsigned requests, unsigned attempts, unsigned completions,
    unsigned protocol_errors, unsigned comm_errors, unsigned system_errors) :
    _pao_id(pao_id),
    _type(type),
    _row_id(row_id),
    _requests(requests),
    _attempts(attempts),
    _completions(completions),
    _comm_errors(comm_errors),
    _protocol_errors(protocol_errors),
    _system_errors(system_errors),
    _dirty(false)
{
    _interval_start = calcIntervalStart(type, record_time);
}


CtiTime PaoStatisticsRecord::calcIntervalStart(StatisticTypes type, const CtiTime interval_time)
{
    switch( type )
    {
        case Hourly:    return hourStart (interval_time);
        case Daily:     return dayStart  (interval_time);
        case Monthly:   return monthStart(interval_time);

        //  default to time of creation, which is fine
        default:
        case Lifetime:  return interval_time;
    }
}

CtiTime PaoStatisticsRecord::hourStart(const CtiTime t)
{
    return CtiTime(t.hour(), 0, 0);
}

CtiTime PaoStatisticsRecord::dayStart(const CtiTime t)
{
    return t.date();
}

CtiTime PaoStatisticsRecord::monthStart(const CtiTime t)
{
    const CtiDate d = t.date();

    return CtiDate(1, d.month(), d.year());
}


CtiTime PaoStatisticsRecord::endTime() const
{
    switch( _type )
    {
        case Hourly:    return _interval_start + 3600;
        case Daily:     return _interval_start.date() + 1;
        case Monthly:
        {
            CtiDate d = _interval_start.date();

            if( d.month() == 12 )
            {
                return CtiDate(1, 1, d.year() + 1);
            }
            else
            {
                return CtiDate(1, d.month() + 1, d.year());
            }
        }

        default:
        case Lifetime:  return YUKONEOT;
    }
}


bool PaoStatisticsRecord::isDirty() const
{
    return _dirty;
}


PaoStatisticsRecord *PaoStatisticsRecord::makeNewRecord(const CtiTime record_time)
{
    return new PaoStatisticsRecord(_pao_id, _type, record_time);
}


unsigned PaoStatisticsRecord::getCompletions() const
{
    return _completions;
}

unsigned PaoStatisticsRecord::getRequests() const
{
    return _requests;
}


void PaoStatisticsRecord::incrementRequests()
{
    ++_requests;

    _dirty = true;
}

void PaoStatisticsRecord::incrementAttempts(int attempt_status)
{
    ++_attempts;

    if( attempt_status )
    {
        switch( GetErrorType(attempt_status) )
        {
            default:
            case ERRTYPESYSTEM:
            {
                ++_system_errors;
                break;
            }

            case ERRTYPECOMM:
            {
                ++_comm_errors;
                break;
            }

            case ERRTYPEPROTOCOL:
            {
                ++_protocol_errors;
                break;
            }
        }
    }

    _dirty = true;
}

void PaoStatisticsRecord::incrementCompletions()
{
    ++_completions;

    _dirty = true;
}


bool PaoStatisticsRecord::writeRecord(Database::DatabaseWriter &writer)
{
    if( _dirty )
    {
        if( _row_id )
        {
            _dirty = ! Update(writer);
        }
        else
        {
            _dirty = ! Insert(writer);
        }
    }

    return ! _dirty;
}


bool PaoStatisticsRecord::Insert(Database::DatabaseWriter &writer)
{
    static const std::string sql =
        "insert into DynamicPaoStatistics "
        "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    writer.setCommandText(sql);

    long tmp_row_id;

    try
    {
        tmp_row_id = DynamicPaoStatisticsIdGen();

        writer
            << tmp_row_id
            << _pao_id
            << getStatisticTypeString(_type)
            << _interval_start
            << _requests
            << _attempts
            << _completions
            << _comm_errors
            << _protocol_errors
            << _system_errors;
    }
    catch( std::runtime_error &e )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << e.what() << __FILE__ << " (" << __LINE__ << ")" << endl;

        return false;
    }

    if( ! writer.execute() )
    {
        std::string error_sql = writer.asString();

        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << "Statistics Insert Error " << endl << error_sql << endl;
        }

        return false;
    }

    _row_id = tmp_row_id;

    return true;
}


bool PaoStatisticsRecord::Update(Database::DatabaseWriter &writer)
{
    static const std::string sql =
        "update DynamicPaoStatistics "
        "set "
            "requests = ?, "
            "attempts = ?, "
            "completions = ?, "
            "commerrors = ?, "
            "protocolerrors = ?, "
            "systemerrors = ? "
        "where "
            "DynamicPaoStatisticsId = ?";

    writer.setCommandText(sql);

    // set
    writer
        << _requests
        << _attempts
        << _completions
        << _comm_errors
        << _protocol_errors
        << _system_errors;

    // where
    writer
        << _row_id;

    if( ! executeUpdater(writer) )
    {
        std::string error_sql = writer.asString();

        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << "Statistics Insert Error " << endl << error_sql << endl;
        }

        return false;
    }

    return true;
}

const std::string &PaoStatisticsRecord::getStatisticTypeString(const StatisticTypes type)
{
    static const std::string hourly   = "Hourly";
    static const std::string daily    = "Daily";
    static const std::string monthly  = "Monthly";
    static const std::string lifetime = "Lifetime";

    switch( type )
    {
        case Hourly:    return hourly;
        case Daily:     return daily;
        case Monthly:   return monthly;
        case Lifetime:  return lifetime;
        default:        throw std::runtime_error("StatisticType not found");
    }
}


}
}
}

