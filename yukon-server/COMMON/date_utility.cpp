#include "precompiled.h"

#include "date_utility.h"
#include "utility.h"

#include <boost/optional.hpp>
#include <boost/algorithm/string/split.hpp>
#include <boost/algorithm/string/classification.hpp>

#include <regex>

namespace Cti {

CtiDate parseDateString(std::string date_str)
{
    std::vector<std::string> dateParts;

    boost::split(dateParts, date_str, is_chars{'-','/'});

    if( dateParts.size() == 3 )
    {
        try
        {
            int month = boost::lexical_cast<unsigned>(dateParts[0]);
            int day   = boost::lexical_cast<unsigned>(dateParts[1]);
            int year  = boost::lexical_cast<unsigned>(dateParts[2]);

            if( year < 100 )
            {
                year += 2000;  //  this will need to change around 2050
            }

            //  naive date construction - no range checking, so we count
            //    on CtiDate() resetting itself to 1/1/1970
            return CtiDate(day, month, year);
        }
        catch( boost::bad_lexical_cast &/*ex*/ )
        {
        }
    }

    return CtiDate::neg_infin;
}

std::optional<TimeParts> parseTimeString(std::string time_str)
{
    std::vector<std::string> timeParts;
    boost::split(timeParts, time_str, is_char{':'});

    //  make sure none of the strings are empty
    if( ! std::count_if(timeParts.begin(), timeParts.end(), std::mem_fn(&std::string::empty)) )
    {
        try
        {
            TimeParts result = {0, 0, 0};

            switch( timeParts.size() )
            {
                case 3:
                {
                    result.second = boost::lexical_cast<unsigned>(timeParts[2]);
                }   //  fall through
                case 2:
                {
                    result.minute = boost::lexical_cast<unsigned>(timeParts[1]);
                    result.hour   = boost::lexical_cast<unsigned>(timeParts[0]);

                    if( result.second < 60 &&
                        result.minute < 60 &&
                        result.hour   < 24 )
                    {
                        return result;
                    }
                }
            }
        }
        catch( boost::bad_lexical_cast &/*ex*/ )
        {
        }
    }

    return std::nullopt;
}

std::optional<std::chrono::seconds> parseDurationString(const std::string duration_str)
{
    using namespace std::chrono;

    const std::regex period_regex { "\\d+(\\.\\d+)?[hms]" };

    if( std::regex_match(duration_str, period_regex) )
    {
        size_t end;

        const auto count = std::stod(duration_str, &end);

        if( end + 1 == duration_str.size() )
        {
            switch( duration_str.back() )
            {
                case 's':  return duration_cast<seconds>(duration<double, seconds::period> { count });
                case 'm':  return duration_cast<seconds>(duration<double, minutes::period> { count });
                case 'h':  return duration_cast<seconds>(duration<double, hours  ::period> { count });
            }
        }
    }

    return std::nullopt;
}

}
