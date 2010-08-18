#include "yukon.h"

#include "cmd_mct410_hourlyRead.h"

#include "ctidate.h"
#include "dsm2err.h"

namespace Cti {
namespace Devices {
namespace Commands {

Mct410HourlyReadCommand::Mct410HourlyReadCommand(CtiDate date_begin, CtiDate date_end, const unsigned channel) :
    _date_begin(date_begin),
    _date_end  (date_end),
    _channel   (channel)
{
    _retries = 2;
}

Mct410HourlyReadCommand::request_ptr Mct410HourlyReadCommand::execute(CtiTime now)
{
    const CtiDate Today     = CtiDate(now),
                  Yesterday = Today - 1,
                  Earliest  = Yesterday - 7;

    if( _date_begin.is_neg_infinity() )
    {
        // If the date is not specified, we use yesterday (last full day)
        _date_begin = Yesterday;
    }

    if( _date_end.is_neg_infinity() )
    {
        //  default to a single day
        _date_end = _date_begin;
    }

    if( _channel == 0 || _channel > 2 )
    {
        throw CommandException(BADPARAM, "Invalid channel for hourly read request; must be 1 or 2 (" + CtiNumStr(_channel) + ")");
    }

    if( _date_begin > Yesterday )
    {
        throw CommandException(BADPARAM, "Invalid date for hourly read request; must be before today (" + _date_begin.asStringUSFormat() + ")");
    }

    if( _date_begin < Earliest )
    {
        throw CommandException(BADPARAM, "Invalid date for hourly read request; must be less than 7 days ago (" + _date_begin.asStringUSFormat() + ")");
    }

    if( _date_end < _date_begin )
    {
        throw CommandException(BADPARAM, "Invalid end date for multi-day hourly read request; must be after begin date (" + _date_end.asStringUSFormat() + ")");
    }

    if( _date_end > Yesterday )    //  must end on or before yesterday
    {
        throw CommandException(BADPARAM, "Invalid end date for multi-day hourly read request; must be before today (" + _date_end.asStringUSFormat() + ")");
    }

    //  Read the second half of the day first - it's anchored at midnight,
    //    but the first read can float due to DST shifts
    return request_ptr(new read_request_t(requestDayEnd(_date_begin, _channel, Yesterday)));
}


Mct410HourlyReadCommand::read_request_t Mct410HourlyReadCommand::requestDayEnd(CtiDate &date_begin, const unsigned channel, const CtiDate &Yesterday)
{
    read_request_t request = requestDayBegin(date_begin, channel, Yesterday);

    request.function += 1;

    return request;
}


Mct410HourlyReadCommand::read_request_t Mct410HourlyReadCommand::requestDayBegin(CtiDate &date_begin, const unsigned channel, const CtiDate &Yesterday)
{
    unsigned days_back = Yesterday.daysFrom1970() - date_begin.daysFrom1970();

    if( channel == 2 )
    {
        return read_request_t(Read_HourlyReadChannel2BasePos + days_back * 2, Read_HourlyReadLen);
    }
    else
    {
        return read_request_t(Read_HourlyReadChannel1BasePos + days_back * 2, Read_HourlyReadLen);
    }
}


Mct410HourlyReadCommand::request_ptr Mct410HourlyReadCommand::decode(CtiTime now, const unsigned function, const payload_t &payload, std::string &description, std::vector<point_data_t> &points)
{
    const CtiDate Today     = CtiDate(now),
                  Yesterday = Today - 1;

    if( payload.size() < 13 )
    {
        throw CommandException(NOTNORMAL, "Payload too small");
    }

    const unsigned weekday = getValueFromBits(payload, 0, 3);

    if( weekday != _date_begin.weekDay() )
    {
        string error_description = "Day of week does not match (" + CtiNumStr(weekday) + " != " + CtiNumStr(_date_begin.weekDay()) + ")";

        _retries--;

        if( !_retries )
        {
            throw CommandException(ErrorInvalidTimestamp, error_description);
        }

        description = error_description + ", retrying (" + CtiNumStr(_retries) + " remaining)";

        return request_ptr(new read_request_t(requestDayBegin(_date_begin, _channel, Yesterday)));
    }

    if( _midday_reading && function % 2 )
    {
        throw CommandException(NOTNORMAL, "Wrong read was performed");
    }

    point_data_t kwh;

    kwh.name = "kWh";
    kwh.offset = 1;
    kwh.type = PulseAccumulatorPointType;
    kwh.quality = NormalQuality;

    //  end of the current day
    kwh.time = CtiTime(_date_begin + 1);

    if( kwh.time.isDST() )
    {
        kwh.time += 3600;
    }

    std::vector<unsigned> deltas;

    if( _midday_reading )
    {
        deltas = getValueVectorFromBits(payload, 13, 7, 13);

        point_data_t blink;

        blink.value = getValueFromBits(payload, 3, 10);
        blink.name = "Blink Counter";
        blink.offset = 20;
        blink.type = PulseAccumulatorPointType;
        blink.quality = NormalQuality;
        blink.time = CtiTime(_date_begin + 1);

        if( blink.value == 1023 )
        {
            blink.quality = OverflowQuality;
        }

        kwh.value = *_midday_reading;
        kwh.time -= 11 * 3600;

        points.push_back(blink);
    }
    else
    {
        deltas = getValueVectorFromBits(payload, 3, 7, 11);

        const double midnight_kwh = getValueFromBits(payload, 80, 24) * 0.1;

        if( kwh.time.isDST() )
        {
            kwh.value = midnight_kwh + convertDelta(deltas.back());

            deltas.pop_back();

            points.push_back(kwh);

            kwh.time -= 3600;
        }

        kwh.value = midnight_kwh;

        points.push_back(kwh);
    }

    while( !deltas.empty() )
    {
        kwh.value -= convertDelta(deltas.back());
        kwh.time -= 3600;

        deltas.pop_back();
        points.push_back(kwh);
    }

    if( _midday_reading )
    {
        if( _date_begin < _date_end  )
        {
            _midday_reading.reset();

            _date_begin += 1;

            return request_ptr(new read_request_t(requestDayEnd(_date_begin, _channel, Yesterday)));
        }
        else
        {
            description = "Hourly read complete";

            return request_ptr();
        }
    }
    else
    {
        _midday_reading = kwh.value;

        return request_ptr(new read_request_t(requestDayBegin(_date_begin, _channel, Yesterday)));
    }
}


double Mct410HourlyReadCommand::convertDelta(unsigned delta)
{
    if( delta == 0x7f )
    {
        return -1.0;
    }

    if( delta < 60 )
    {
        return delta * 0.1;
    }

    return delta - 54;
}


Mct410HourlyReadCommand::request_ptr Mct410HourlyReadCommand::error(CtiTime now, const unsigned function, std::string &description)
{
    const CtiDate Today     = CtiDate(now),
                  Yesterday = Today - 1,
                  Earliest  = Yesterday - 7;

    if( _date_begin < Earliest )
    {
        description = "Request expired (" + _date_begin.asStringUSFormat() + " < " + Earliest.asStringUSFormat() + ")";
    }
    else if( _retries-- <= 0 )
    {
        description = "Retries exhausted";
    }
    else
    {
        if( _midday_reading )
        {
            //  we have the second half of the day, re-request the first half
            return request_ptr(new read_request_t(requestDayBegin(_date_begin, _channel, Yesterday)));
        }
        else
        {
            //  we don't have any of this day yet
            return request_ptr(new read_request_t(requestDayEnd(_date_begin, _channel, Yesterday)));
        }
    }

    return request_ptr();
}


}
}
}
