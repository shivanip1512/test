#include "precompiled.h"

#include "PaoStatisticsRecord.h"
#include "ctidate.h"
#include "error.h"

namespace Cti {
namespace Porter {
namespace Statistics {

static const CtiDate LifetimeIntervalStart = CtiDate(1, 1, 2000);

PaoStatisticsRecord::PaoStatisticsRecord(long pao_id, StatisticTypes type, const CtiTime record_time) :
    _pao_id(pao_id),
    _type(type),
    _interval_start(calcIntervalStart(type, record_time)),
    _requests(0),
    _attempts(0),
    _completions(0),
    _comm_errors(0),
    _protocol_errors(0),
    _system_errors(0)
{
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
        case Lifetime:  return LifetimeIntervalStart;
    }
}

CtiTime PaoStatisticsRecord::hourStart(const CtiTime t)
{
    return CtiTime(t.date(), t.hour(), 0, 0);
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


bool PaoStatisticsRecord::isStale(const CtiTime timeNow) const
{
    switch( _type )
    {
        case Hourly:
        {
            return timeNow >= (_interval_start + 3600);
        }
        case Daily:
        {
            return timeNow >= (_interval_start.date() + 1);
        }
        case Monthly:
        {
            CtiDate d = _interval_start.date();

            return timeNow >= (d + CtiDate::daysInMonthYear(d.month(), d.year()));
        }

        default:
        case Lifetime:  return false;
    }
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
}

void PaoStatisticsRecord::incrementAttempts(const YukonError_t attempt_status)
{
    ++_attempts;

    if( attempt_status )
    {
        switch( CtiError::GetErrorType(attempt_status) )
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
}

void PaoStatisticsRecord::incrementCompletions()
{
    ++_completions;
}

std::unique_ptr<CtiTableDynamicPaoStatistics> PaoStatisticsRecord::makeTableEntry()
{
    return
        std::make_unique<CtiTableDynamicPaoStatistics>(
            _pao_id,
            getStatisticTypeString( _type ),
            _interval_start,
            _requests,
            _attempts,
            _completions,
            _comm_errors,
            _protocol_errors,
            _system_errors );
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

