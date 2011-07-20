#pragma once

#include "cmd_mct410.h"

#include "dev_single.h"
#include "ctidate.h"

#include <boost/optional.hpp>

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB Mct410HourlyReadCommand : public Mct410Command
{
    //  const parameters that define this request
    const CtiDate _date_begin;
    const CtiDate _date_end;

    const unsigned _channel;

    struct request_pointer
    {
        CtiDate date;

        bool reading_day_end;

        bool next(const CtiDate end)
        {
            if( ! reading_day_end )
            {
                if( ! end.is_special() && date < end )
                {
                    date += 1;
                }
                else
                {
                    return false;
                }
            }

            reading_day_end = !reading_day_end;

            return true;
        }

    } _request;

    unsigned _retries;

    boost::optional<unsigned> _midday_reading;

    request_ptr makeRequest(const CtiTime now);

    CtiDeviceSingle::point_info        extractMidnightKwh(const payload_t &payload) const;
    static point_data                  extractBlinkCount (const payload_t &payload);
    static std::vector<unsigned>       extractDeltas     (const payload_t &payload, const request_pointer &rp);

    static std::vector<point_data> processDeltas(point_data base_kwh, const std::vector<unsigned> &deltas);

    static CtiTime make_midday_time(const CtiTime request_midnight);
    static CtiDate make_yesterday(const CtiTime t);

protected:

    static void validateDate(const CtiDate &d, const CtiDate &Yesterday);
    static void validateRead(const request_pointer &rp, const unsigned function, const CtiTime &decode_time);

    static read_request_t requestDayBegin(const CtiDate &date_begin, const unsigned channel, const CtiDate &Yesterday);
    static read_request_t requestDayEnd  (const CtiDate &date_begin, const unsigned channel, const CtiDate &Yesterday);

    static int convertDelta(unsigned delta);

    virtual CtiDeviceSingle::point_info getAccumulatorData(const unsigned char *buf, const unsigned len) const;

public:

    Mct410HourlyReadCommand(CtiDate date_begin, CtiDate date_end, const unsigned channel);

    virtual request_ptr execute(const CtiTime now);
    virtual request_ptr decode (const CtiTime now, const unsigned function, const payload_t &payload, std::string &description, std::vector<point_data> &points);
    virtual request_ptr error  (const CtiTime now, const int error_code, std::string &description);
};

}
}
}

