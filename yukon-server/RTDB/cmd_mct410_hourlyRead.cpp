#include "precompiled.h"

#include "cmd_mct410_hourlyRead.h"
#include "dev_mct410.h"

#include "ctidate.h"
#include "error.h"

using std::vector;

namespace Cti {
namespace Devices {
namespace Commands {

Mct410HourlyReadCommand::Mct410HourlyReadCommand(CtiDate date_begin, CtiDate date_end, const unsigned channel) :
    _date_begin(date_begin),
    _date_end  (date_end),
    _channel   (channel),
    _retries(2)
{
}


//  throws CommandException
DlcCommand::emetcon_request_ptr Mct410HourlyReadCommand::executeCommand(CtiTime now)
{
    const CtiDate Yesterday(make_yesterday(now));

    // If the date is not specified, we use yesterday (last full day)
    _request.date = _date_begin.is_special() ? Yesterday : _date_begin;
    _request.reading_day_end = true;

    if( ! _date_end.is_special() )
    {
        validateDate(_date_end, Yesterday);

        if( _date_end < _request.date )
        {
            throw CommandException(ClientErrors::BadParameter, "Invalid end date (" + _date_end.asStringMDY() + ") for hourly read request; must be after begin date (" + _request.date.asStringMDY() + ")");
        }
    }

    return makeRequest(now);
}


//  throws CommandException
DlcCommand::read_request_t Mct410HourlyReadCommand::requestDayEnd(const CtiDate &date_begin, const unsigned channel, const CtiDate &Yesterday)
{
    return requestDay(date_begin, channel, Yesterday, DayEnd);
}


//  throws CommandException
DlcCommand::read_request_t Mct410HourlyReadCommand::requestDayBegin(const CtiDate &date_begin, const unsigned channel, const CtiDate &Yesterday)
{
    return requestDay(date_begin, channel, Yesterday, DayBegin);
}


//  throws CommandException
DlcCommand::read_request_t Mct410HourlyReadCommand::requestDay(const CtiDate &date_begin, const unsigned channel, const CtiDate &Yesterday, DayPart meridiem)
{
    if( channel == 0 || channel > 2 )
    {
        throw CommandException(ClientErrors::BadParameter, "Invalid channel (" + CtiNumStr(channel) + ") for hourly read request; must be 1 or 2");
    }

    validateDate(date_begin, Yesterday);

    unsigned days_back = Yesterday.daysFrom1970() - date_begin.daysFrom1970();

    if( channel == 2 )
    {
        return read_request_t(Read_HourlyReadChannel2BasePos + days_back * 2 + meridiem, Read_HourlyReadLen);
    }
    else
    {
        return read_request_t(Read_HourlyReadChannel1BasePos + days_back * 2 + meridiem, Read_HourlyReadLen);
    }
}


//  throws CommandException
void Mct410HourlyReadCommand::validateDate(const CtiDate &d, const CtiDate &Yesterday)
{
    //  Yesterday and up to 6 days before yesterday
    const unsigned DaysBack = 6;

    if( d > Yesterday )
    {
        throw CommandException(ClientErrors::BadParameter, "Invalid date (" + d.asStringMDY() + ") for hourly read request; must be before today (" + (Yesterday + 1).asStringMDY() + ")");
    }

    if( d < Yesterday - DaysBack )
    {
        throw CommandException(ClientErrors::BadParameter, "Invalid date (" + d.asStringMDY() + ") for hourly read request; must be no more than 7 days ago (" + (Yesterday - DaysBack).asStringMDY() + ")");
    }
}


//  throws CommandException
DlcCommand::point_data Mct410HourlyReadCommand::extractBlinkCount(const Bytes &payload)
{
    point_data blink;

    blink.name    = "Blink Counter";
    blink.offset  = 20;
    blink.type    = PulseAccumulatorPointType;
    blink.quality = NormalQuality;
    blink.value   = getValueFromBits_bEndian(payload, 3, 10);


    //  This is the only error case - it's a catch-all for any error value or overflow.
    if( blink.value == 1023 )
    {
        blink.quality = OverflowQuality;
    }

    return blink;
}


//  throws CommandException
Mct410Device::point_info Mct410HourlyReadCommand::extractMidnightKwh(const Bytes &payload) const
{
    //  we have to manually check this here because we're using Mct4xxDevice::getData(), which only knows pointers
    if( payload.size() < 13 )
    {
        throw CommandException(ClientErrors::Abnormal, "Payload too small");
    }

    return getAccumulatorData(&payload.front() + 10, 3);
}


Mct410Device::point_info Mct410HourlyReadCommand::getAccumulatorData(const unsigned char *buf, const unsigned len) const
{
    return Mct410Device::decodePulseAccumulator(buf, len, 0);
}


//  throws CommandException
vector<unsigned> Mct410HourlyReadCommand::extractDeltas(const Bytes &payload, const request_pointer &rp)
{
    CtiTime day_begin(rp.date), day_end(rp.date + 1);

    if( rp.reading_day_end )
    {
        if( day_end.isDST() )
        {
            //  If it's DST, the last delta (Hour 24) is from 24:00 to 25:00, which we don't need
            return getValueVectorFromBits_bEndian(payload, 3, 7, 10);
        }
        else
        {
            return getValueVectorFromBits_bEndian(payload, 3, 7, 11);
        }
    }
    else
    {
        if( day_begin.isDST() )
        {
            return getValueVectorFromBits_bEndian(payload, 13, 7, 13);
        }
        else
        {
            //  If it's not DST, the first delta (Hour 00) is from 00:00 to 01:00, which we don't need
            return getValueVectorFromBits_bEndian(payload, 20, 7, 12);
        }
    }
}


vector<DlcCommand::point_data> Mct410HourlyReadCommand::processDeltas(point_data base_kwh, const vector<unsigned> &deltas)
{
    vector<point_data> hourly_readings;

    //  gotta go through the vector of deltas backward
    vector<unsigned>::const_reverse_iterator delta = deltas.rbegin();

    while( delta != deltas.rend() )
    {
        int converted_delta = convertDelta(*delta++);

        if( converted_delta < 0 )
        {
            //  Stop right there - we don't have a way to continue
            break;
        }

        base_kwh.value -= converted_delta;
        base_kwh.time  -= 3600;

        hourly_readings.push_back(base_kwh);
    }

    return hourly_readings;
}


//  throws CommandException
DlcCommand::emetcon_request_ptr Mct410HourlyReadCommand::makeRequest(const CtiTime now)
{
    if( _request.reading_day_end )
    {
        _midday_reading.reset();

        return std::make_unique<read_request_t>(requestDayEnd(_request.date, _channel, make_yesterday(now)));
    }
    else
    {
        auto request = std::make_unique<read_request_t>(requestDayBegin(_request.date, _channel, make_yesterday(now)));

        if( ! _midday_reading )
        {
            //  the kWh readings are toast - just read the day of week and blink count
            request->read_length = 2;
        }

        return std::move(request);
    }
}


//  throws CommandException
DlcCommand::request_ptr Mct410HourlyReadCommand::decodeCommand(CtiTime now, const unsigned function, const boost::optional<Bytes> &payload, std::string &description, vector<point_data> &points)
try
{
    const unsigned weekday = getValueFromBits_bEndian(*payload, 0, 3);

    if( weekday != _request.date.weekDay() )
    {
        description = "Day of week does not match (" + CtiNumStr(weekday) + " != " + CtiNumStr(_request.date.weekDay()) + ")";

        throw CommandException(ClientErrors::InvalidTimestamp, description);
    }

    validateRead(_request, function, now);

    const CtiTime Midnight(_request.date + 1);

    point_data kwh;

    kwh.name    = "kWh";
    kwh.offset  = _channel;
    kwh.type    = PulseAccumulatorPointType;
    kwh.quality = InvalidQuality;  //  will be reset if there is a valid kWh reading for this decode pass
    kwh.time    = Midnight;  //  end of the current day

    if( _request.reading_day_end )
    {
        kwh = extractMidnightKwh(*payload);

        points.push_back(kwh);
    }
    else
    {
        point_data blink_count = extractBlinkCount(*payload);

        blink_count.time = Midnight;

        points.push_back(blink_count);

        if( _midday_reading )
        {
            kwh.value   = *_midday_reading;
            kwh.quality = NormalQuality;
            kwh.time    = make_midday_time(Midnight);
        }
    }

    if( kwh.quality == NormalQuality )
    {
        vector<unsigned> deltas = extractDeltas(*payload, _request);

        vector<point_data> hourly_reads = processDeltas(kwh, deltas);

        //  If we successfully decoded all of the delta-based points offset from midnight,
        //    store off the mid-day kWh value for the beginning-of-day read
        if( _request.reading_day_end && hourly_reads.size() == deltas.size() )
        {
            _midday_reading = hourly_reads.back().value;
        }

        points.insert(points.end(), hourly_reads.begin(), hourly_reads.end());
    }

    if( _request.next(_date_end) )
    {
        return makeRequest(now);
    }
    else
    {
        description = "Hourly read complete";

        return request_ptr();
    }
}
catch( DlcCommand::CommandException &ex )
{
    return error(now, ex.error_code, description = ex.error_description);
}


//  throws CommandException
void Mct410HourlyReadCommand::validateRead(const request_pointer &rp, const unsigned function, const CtiTime &decode_time)
{
    if( !!(function % 2) != rp.reading_day_end )
    {
        throw CommandException(ClientErrors::InvalidTimestamp, "Wrong read performed (" + CtiNumStr(function).xhex() + ")");
    }

    validateDate(rp.date, make_yesterday(decode_time));
}


int Mct410HourlyReadCommand::convertDelta(unsigned delta)
{
    if( delta >= 0x7f )
    {
        return -1;
    }

    if( delta > 60 )
    {
        return (delta - 54) * 10;
    }

    return delta;
}


//  throws CommandException
DlcCommand::request_ptr Mct410HourlyReadCommand::error(const CtiTime now, const YukonError_t error_code, std::string &description)
{
    if( description.empty() )
    {
        description = CtiError::GetErrorString(error_code);
    }

    description += "\n";

    if( _retries > 0 )
    {
        _retries--;

        description += "Retrying (" + CtiNumStr(_retries) + " remaining)";

        return makeRequest(now);
    }
    else
    {
        throw CommandException(error_code, description + "Retries exhausted");
    }
}


void Mct410HourlyReadCommand::cancel()
{
    _retries = 0;
}


CtiTime Mct410HourlyReadCommand::make_midday_time(const CtiTime request_midnight)
{
    if( request_midnight.isDST() )
    {
        return request_midnight - 10 * 3600;
    }
    else
    {
        return request_midnight - 11 * 3600;
    }
}


CtiDate Mct410HourlyReadCommand::make_yesterday(const CtiTime t)
{
    return CtiDate(t) - 1;
}


}
}
}
