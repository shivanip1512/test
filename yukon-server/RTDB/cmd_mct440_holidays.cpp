#include "precompiled.h"

#include "cmd_mct440_holidays.h"

#include "ctidate.h"

#include <sstream>

namespace {

    const CtiDate HolidayEpoch = CtiDate(1, 1, 2010);
}

namespace Cti {
namespace Devices {
namespace Commands {

Mct440HolidaysCommand::Mct440HolidaysCommand(const CtiTime Now, const std::set<CtiDate> &holidays) :
    _executionState(&Mct440HolidaysCommand::write),
    _now(Now)
{
    const CtiDate Today(_now.date());
    const CtiDate NextYear(Today.day(), Today.year() + 1);

    if( holidays.size() > 15 )
    {
        throw CommandException(BADPARAM, "Maximum 15 holidays supported (" + CtiNumStr(holidays.size()) + " provided)");
    }

    for each( const CtiDate &holiday in holidays )
    {
        if( holiday <= Today ||
            holiday >= NextYear )
        {
            throw CommandException(
               BADPARAM,
               "Invalid holiday (" + holiday.asStringUSFormat() + "),"
                   " must be after " + Today.asStringUSFormat() +
                   " and before " + NextYear.asStringUSFormat());
        }

        _holidays.insert(holiday);
    }
}

Mct440HolidaysCommand::Mct440HolidaysCommand() :
    _executionState(&Mct440HolidaysCommand::read1)
{
}

DlcCommand::request_ptr Mct440HolidaysCommand::execute(const CtiTime now)
{
    return doCommand();
}

DlcCommand::request_ptr Mct440HolidaysCommand::decode(const CtiTime now, const unsigned function, const boost::optional<Bytes> &payload, std::string &description, std::vector<point_data> &points)
{
    if( payload )
    {
        std::ostringstream ss;

        ss << "Holiday schedule:\n";

        unsigned offset = (function - 0xd0) * 6;

        for( unsigned index = 0; index < payload->size() / 2; ++index )
        {
            CtiDate holiday_date(HolidayEpoch);

            unsigned days = 0;
            days |= (*payload)[index * 2];
            days <<= 8;
            days |= (*payload)[index * 2 + 1];

            ss << "Holiday " << (offset + index + 1) << ": ";

            if( days != 0xffff )
            {
                holiday_date += days;

                ss << holiday_date.asStringUSFormat() + "\n";
            }
            else
            {
                ss << "(unused)\n";
            }
        }

        description += ss.str();
    }

    return doCommand();
}

//  throws CommandException
DlcCommand::request_ptr Mct440HolidaysCommand::error(const CtiTime now, const int error_code, std::string &description)
{
    //  This should probably be the default for all commands unless otherwise specified.
    throw CommandException(error_code, GetError(error_code));
}

DlcCommand::request_ptr Mct440HolidaysCommand::doCommand()
{
    //  call the current state's member function
    return _executionState(this);
}

DlcCommand::request_ptr Mct440HolidaysCommand::write()
{
    std::vector<unsigned char> payload;

    const CtiDate TodayGmt(_now.dateGMT());

    unsigned index = 0;

    setBits(payload, index, 1, TodayGmt.month() % 2);
    index += 1;

    const CtiDate Today(_now.date());
    const CtiDate StartOfMonth(1, Today.month(), Today.year());

    unsigned offset = StartOfMonth.daysFrom1970();

    for each( const CtiDate &holiday in _holidays )
    {
        unsigned delta = holiday.daysFrom1970() - offset - 1;

        if( delta > 252 )
        {
            setBits(payload, index, 7, 0x7f);
            index += 7;
            delta -= 252;
        }
        if( delta > 126 )
        {
            setBits(payload, index, 7, 0x7e);
            index += 7;
            delta -= 126;
        }

        setBits(payload, index, 7, delta);
        index += 7;

        offset = holiday.daysFrom1970();
    }

    _executionState = &Mct440HolidaysCommand::read1;

    return request_ptr(new write_request_t(0xd0, payload));
}

DlcCommand::request_ptr Mct440HolidaysCommand::read1()
{
    _executionState = &Mct440HolidaysCommand::read2;

    return request_ptr(new read_request_t(0xd0, 12));
}

DlcCommand::request_ptr Mct440HolidaysCommand::read2()
{
    _executionState = &Mct440HolidaysCommand::read3;

    return request_ptr(new read_request_t(0xd1, 12));
}

DlcCommand::request_ptr Mct440HolidaysCommand::read3()
{
    _executionState = &Mct440HolidaysCommand::done;

    return request_ptr(new read_request_t(0xd2, 6));
}

DlcCommand::request_ptr Mct440HolidaysCommand::done()
{
    return request_ptr();
}

}
}
}
