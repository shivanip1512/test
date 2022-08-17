#include "precompiled.h"

#include "cmd_mct440_holidays.h"

#include "ctidate.h"
#include "std_helper.h"

#include <boost/assign/list_of.hpp>

#include <sstream>

namespace {

    const CtiDate HolidayEpoch = CtiDate(1, 1, 2010);

    enum
    {
        Holiday_Read1_Function = 0xd0,
        Holiday_Read2_Function = 0xd1,
        Holiday_Read3_Function = 0xd2
    };
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
        throw CommandException(ClientErrors::BadParameter, "Maximum 15 holidays supported (" + CtiNumStr(holidays.size()) + " provided)");
    }

    for each( const CtiDate &holiday in holidays )
    {
        if( holiday <= Today ||
            holiday >= NextYear )
        {
            throw CommandException(
               ClientErrors::BadParameter,
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

DlcCommand::emetcon_request_ptr Mct440HolidaysCommand::executeCommand(const CtiTime now)
{
    return doCommand();
}

DlcCommand::request_ptr Mct440HolidaysCommand::decodeCommand(const CtiTime now, const unsigned function, const boost::optional<Bytes> &payload, std::string &description, std::vector<point_data> &points)
{
    if( payload )
    {
        typedef std::pair<unsigned, unsigned> HolidayReadDescriptor;

        const std::map<unsigned, HolidayReadDescriptor> functionDescriptors = boost::assign::map_list_of
            (Holiday_Read1_Function, std::make_pair( 1u, 6u))
            (Holiday_Read2_Function, std::make_pair( 7u, 6u))
            (Holiday_Read3_Function, std::make_pair(13u, 3u));

        if( const boost::optional<HolidayReadDescriptor> rd = mapFind(functionDescriptors, function) )
        {
            const unsigned holidayOffset = rd->first;
            const unsigned holidayCount  = rd->second;

            if( payload->size() < holidayCount * 2 )
            {
                throw CommandException(
                    ClientErrors::DataMissing,
                    "Payload too small ("
                        + CtiNumStr(payload->size())  + " received, "
                        + CtiNumStr(holidayCount * 2) + " required)");
            }

            std::ostringstream ss;

            ss << "Holiday schedule:\n";

            for( unsigned index = 0; index < holidayCount; ++index )
            {
                const unsigned days =
                    (*payload)[index * 2] << 8 |
                    (*payload)[index * 2 + 1];

                ss << "Holiday " << (holidayOffset + index) << ": ";

                if( days == 0xffff )
                {
                    ss << "(unused)\n";
                }
                else
                {
                    CtiDate holiday_date(HolidayEpoch);

                    holiday_date += days;

                    ss << holiday_date.asStringUSFormat() + "\n";
                }
            }

            description += ss.str();
        }
    }

    return doCommand();
}

//  throws CommandException
DlcCommand::request_ptr Mct440HolidaysCommand::error(const CtiTime now, const YukonError_t error_code, std::string &description)
{
    //  This should probably be the default for all commands unless otherwise specified.
    throw CommandException(error_code, CtiError::GetErrorString(error_code));
}

DlcCommand::emetcon_request_ptr Mct440HolidaysCommand::doCommand()
{
    //  call the current state's member function
    return (this->*_executionState)();
}

DlcCommand::emetcon_request_ptr Mct440HolidaysCommand::write()
{
    std::vector<unsigned char> payload;

    const CtiDate TodayGmt(_now.dateGMT());

    unsigned index = 0;

    setBits_lEndian(payload, index, 1, TodayGmt.month() % 2);
    index += 1;

    const CtiDate Today(_now.date());
    const CtiDate StartOfMonth(1, Today.month(), Today.year());

    unsigned offset = StartOfMonth.daysFrom1970();

    for each( const CtiDate &holiday in _holidays )
    {
        unsigned delta = holiday.daysFrom1970() - offset - 1;

        if( delta > 252 )
        {
            setBits_lEndian(payload, index, 7, 0x7f);
            index += 7;
            delta -= 252;
        }
        if( delta > 126 )
        {
            setBits_lEndian(payload, index, 7, 0x7e);
            index += 7;
            delta -= 126;
        }

        setBits_lEndian(payload, index, 7, delta);
        index += 7;

        offset = holiday.daysFrom1970();
    }

    _executionState = &Mct440HolidaysCommand::read1;

    return std::make_unique<write_request_t>(0xd0, payload);
}

DlcCommand::emetcon_request_ptr Mct440HolidaysCommand::read1()
{
    _executionState = &Mct440HolidaysCommand::read2;

    return std::make_unique<read_request_t>(Holiday_Read1_Function, 12);
}

DlcCommand::emetcon_request_ptr Mct440HolidaysCommand::read2()
{
    _executionState = &Mct440HolidaysCommand::read3;

    return std::make_unique<read_request_t>(Holiday_Read2_Function, 12);
}

DlcCommand::emetcon_request_ptr Mct440HolidaysCommand::read3()
{
    _executionState = &Mct440HolidaysCommand::done;

    return std::make_unique<read_request_t>(Holiday_Read3_Function, 6);
}

DlcCommand::emetcon_request_ptr Mct440HolidaysCommand::done()
{
    return nullptr;
}

}
}
}
