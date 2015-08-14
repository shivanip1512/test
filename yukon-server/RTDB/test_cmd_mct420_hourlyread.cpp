#include <boost/test/unit_test.hpp>

#include "cmd_mct420_hourlyread.h"

using Cti::Devices::Commands::Mct420HourlyReadCommand;
using Cti::Devices::Commands::DlcCommand;
using std::string;

struct expected_pointdata
{
    CtiTime time;
    double value;
    PointQuality_t quality;
    CtiPointType_t type;
    unsigned offset;
};

static bool operator!=(const DlcCommand::point_data &pd, const expected_pointdata &ep)
{
    return pd.time    != ep.time
        || pd.value   != ep.value
        || pd.quality != ep.quality
        || pd.type    != ep.type
        || pd.offset  != ep.offset;
}

static std::ostream &operator<<(std::ostream &os, const expected_pointdata &ep)
{
    os << "(" << ep.time.asString()
       << "," << ep.value
       << "," << ep.quality
       << "," << ep.type
       << "," << ep.offset
       << ")";

    return os;
}

static std::ostream &operator<<(std::ostream &os, const DlcCommand::point_data &pd)
{
    os << "(" << pd.time.asString()
       << "," << pd.value
       << "," << pd.quality
       << "," << pd.type
       << "," << pd.offset
       << ")";

    return os;
}

BOOST_AUTO_TEST_SUITE( test_cmd_mct420_hourlyread )

BOOST_AUTO_TEST_CASE(test_command_single_day_read_dst_active)
{
    const CtiDate start(11, 8, 2010);
    const CtiTime execute_time(CtiDate(17, 8, 2010), 10);

    Mct420HourlyReadCommand hourlyRead(start, CtiDate::neg_infin, 1);

    {
        auto r = hourlyRead.executeCommand(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function(), 0xCB);
        BOOST_CHECK_EQUAL(r->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),   13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x62);  //  Day of week - 3, or Wednesday
        payload.push_back(0xc5);
        payload.push_back(0x09);  //  Deltas are increasing from 0.1 at hour 24 to 1.1 at hour 14
        payload.push_back(0x10);
        payload.push_back(0x1c);
        payload.push_back(0x30);
        payload.push_back(0x50);
        payload.push_back(0x80);
        payload.push_back(0xc1);
        payload.push_back(0x01);

        payload.push_back(0x01);  //  total kWh at midnight - 7456.5 kWh
        payload.push_back(0x23);
        payload.push_back(0x45);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xCB, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        auto er = dynamic_cast<DlcCommand::emetcon_request_t *>(r.get());

        BOOST_REQUIRE(er);

        BOOST_CHECK_EQUAL(er->function(), 0xCA);
        BOOST_CHECK_EQUAL(er->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(er->length(),   13);
        BOOST_CHECK_EQUAL(er->payload().size(), 0);

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(12, 8, 2010),  0), 74565, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 23), 74563, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 22), 74560, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 21), 74556, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 20), 74551, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 19), 74545, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 18), 74538, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 17), 74530, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 16), 74521, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 15), 74511, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 14), 74500, NormalQuality, PulseAccumulatorPointType, 1 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x6a);  //  Day of week - still 3, or Wednesday
        payload.push_back(0x08);  //  Blink count = 321, or 0x141
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x1f);  //  72.0 = 126 = 7e
        payload.push_back(0x9f);  //  8.0  =  62 = 3e
        payload.push_back(0x3c);  //  6.0  =  60 = 3c

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xCA, payload, description, points);

        //  make sure it's null, we're done with today
        BOOST_CHECK( ! r.get());

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(12, 8, 2010),  0), 321.0, NormalQuality, PulseAccumulatorPointType, 20 },

            { CtiTime(CtiDate(11, 8, 2010), 13), 74440, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 12), 74360, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 11), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 10), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010),  9), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010),  8), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010),  7), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010),  6), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010),  5), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010),  4), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010),  3), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010),  2), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010),  1), 73640, NormalQuality, PulseAccumulatorPointType, 1 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }
}

BOOST_AUTO_TEST_CASE(test_command_single_day_read_dst_inactive)
{
    const CtiDate start(11, 2, 2010);
    const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

    Mct420HourlyReadCommand hourlyRead(start, CtiDate::neg_infin, 1);

    {
        auto r = hourlyRead.executeCommand(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function(), 0xCB);
        BOOST_CHECK_EQUAL(r->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),   13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x82);  //  Day of week - 4, or Thursday
        payload.push_back(0xc5);
        payload.push_back(0x09);  //  Deltas are increasing from 0.1 at hour 24 to 1.1 at hour 14
        payload.push_back(0x10);
        payload.push_back(0x1c);
        payload.push_back(0x30);
        payload.push_back(0x50);
        payload.push_back(0x80);
        payload.push_back(0xc1);
        payload.push_back(0x01);

        payload.push_back(0x01);  //  total kWh at midnight - 7456.5 kWh
        payload.push_back(0x23);
        payload.push_back(0x45);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xCB, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        auto er = dynamic_cast<DlcCommand::emetcon_request_t *>(r.get());

        BOOST_REQUIRE(er);

        BOOST_CHECK_EQUAL(er->function(), 0xCA);
        BOOST_CHECK_EQUAL(er->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(er->length(),   13);
        BOOST_CHECK_EQUAL(er->payload().size(), 0);

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(12, 2, 2010),  0), 74565, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 2, 2010), 23), 74564, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 2, 2010), 22), 74562, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 2, 2010), 21), 74559, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 2, 2010), 20), 74555, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 2, 2010), 19), 74550, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 2, 2010), 18), 74544, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 2, 2010), 17), 74537, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 2, 2010), 16), 74529, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 2, 2010), 15), 74520, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 2, 2010), 14), 74510, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 2, 2010), 13), 74499, NormalQuality, PulseAccumulatorPointType, 1 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x8a);  //  Day of week - still 4, or Thursday
        payload.push_back(0x08);  //  Blink count = 321, or 0x141
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x1f);  //  72.0 = 126 = 7e
        payload.push_back(0x9f);  //  8.0  =  62 = 3e
        payload.push_back(0x3c);  //  6.0  =  60 = 3c

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xCA, payload, description, points);

        //  make sure it's null, we're done with today
        BOOST_CHECK( ! r.get());

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(12, 2, 2010),  0), 321.0, NormalQuality, PulseAccumulatorPointType, 20 },

            { CtiTime(CtiDate(11, 2, 2010), 12), 74439, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 2, 2010), 11), 74359, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 2, 2010), 10), 73639, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 2, 2010),  9), 73639, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 2, 2010),  8), 73639, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 2, 2010),  7), 73639, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 2, 2010),  6), 73639, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 2, 2010),  5), 73639, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 2, 2010),  4), 73639, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 2, 2010),  3), 73639, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 2, 2010),  2), 73639, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 2, 2010),  1), 73639, NormalQuality, PulseAccumulatorPointType,  1 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }
}

BOOST_AUTO_TEST_CASE(test_command_single_day_read_dst_fall_2010)
{
    const CtiDate start(7, 11, 2010);
    const CtiTime execute_time(CtiDate(8, 11, 2010), 10);

    Mct420HourlyReadCommand hourlyRead(start, CtiDate::neg_infin, 1);

    {
        auto r = hourlyRead.executeCommand(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function(), 0xC1);
        BOOST_CHECK_EQUAL(r->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),   13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x02);  //  Day of week - 0, or Sunday
        payload.push_back(0xc5);
        payload.push_back(0x09);  //  Deltas are increasing from 0.1 at hour 24 to 1.1 at hour 14
        payload.push_back(0x10);
        payload.push_back(0x1c);
        payload.push_back(0x30);
        payload.push_back(0x50);
        payload.push_back(0x80);
        payload.push_back(0xc1);
        payload.push_back(0x01);

        payload.push_back(0x01);  //  total kWh at midnight - 7456.5 kWh
        payload.push_back(0x23);
        payload.push_back(0x45);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xC1, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        auto er = dynamic_cast<DlcCommand::emetcon_request_t *>(r.get());

        BOOST_REQUIRE(er);

        BOOST_CHECK_EQUAL(er->function(), 0xC0);
        BOOST_CHECK_EQUAL(er->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(er->length(),   13);
        BOOST_CHECK_EQUAL(er->payload().size(), 0);

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(8, 11, 2010),  0), 74565, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(7, 11, 2010), 23), 74564, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(7, 11, 2010), 22), 74562, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(7, 11, 2010), 21), 74559, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(7, 11, 2010), 20), 74555, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(7, 11, 2010), 19), 74550, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(7, 11, 2010), 18), 74544, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(7, 11, 2010), 17), 74537, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(7, 11, 2010), 16), 74529, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(7, 11, 2010), 15), 74520, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(7, 11, 2010), 14), 74510, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(7, 11, 2010), 13), 74499, NormalQuality, PulseAccumulatorPointType, 1 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x0a);  //  Day of week - still 0, or Sunday
        payload.push_back(0x08);  //  Blink count = 321, or 0x141
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x1f);  //  72.0 = 126 = 7e
        payload.push_back(0x9f);  //  8.0  =  62 = 3e
        payload.push_back(0x3c);  //  6.0  =  60 = 3c

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xC0, payload, description, points);

        //  make sure it's null, we're done with today
        BOOST_CHECK( ! r.get());

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(8, 11, 2010),  0), 321.0, NormalQuality, PulseAccumulatorPointType, 20 },

            { CtiTime(CtiDate(7, 11, 2010), 12), 74439, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(7, 11, 2010), 11), 74359, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(7, 11, 2010), 10), 73639, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(7, 11, 2010),  9), 73639, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(7, 11, 2010),  8), 73639, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(7, 11, 2010),  7), 73639, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(7, 11, 2010),  6), 73639, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(7, 11, 2010),  5), 73639, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(7, 11, 2010),  4), 73639, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(7, 11, 2010),  3), 73639, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(7, 11, 2010),  2), 73639, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(7, 11, 2010),  1), 73639, NormalQuality, PulseAccumulatorPointType,  1 },
            //  the only way to create this time (1:00 AM CDT) on the fall-back DST day
            { CtiTime(CtiDate(7, 11, 2010),  0) + 3600, 73639, NormalQuality, PulseAccumulatorPointType,  1 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }
}

BOOST_AUTO_TEST_CASE(test_command_single_day_read_dst_spring_2011)
{
    const CtiDate start(13, 3, 2011);
    const CtiTime execute_time(CtiDate(14, 3, 2011), 10);

    Mct420HourlyReadCommand hourlyRead(start, CtiDate::neg_infin, 1);

    {
        auto r = hourlyRead.executeCommand(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function(), 0xC1);
        BOOST_CHECK_EQUAL(r->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),   13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x02);  //  Day of week - 0, or Sunday
        payload.push_back(0xc5);
        payload.push_back(0x09);  //  Deltas are increasing from 0.1 at hour 24 to 1.1 at hour 14
        payload.push_back(0x10);
        payload.push_back(0x1c);
        payload.push_back(0x30);
        payload.push_back(0x50);
        payload.push_back(0x80);
        payload.push_back(0xc1);
        payload.push_back(0x01);

        payload.push_back(0x01);  //  total kWh at midnight - 7456.5 kWh
        payload.push_back(0x23);
        payload.push_back(0x45);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xC1, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        auto er = dynamic_cast<DlcCommand::emetcon_request_t *>(r.get());

        BOOST_REQUIRE(er);

        BOOST_CHECK_EQUAL(er->function(), 0xC0);
        BOOST_CHECK_EQUAL(er->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(er->length(),   13);
        BOOST_CHECK_EQUAL(er->payload().size(), 0);

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(14, 3, 2011),  0), 74565, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(13, 3, 2011), 23), 74563, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(13, 3, 2011), 22), 74560, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(13, 3, 2011), 21), 74556, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(13, 3, 2011), 20), 74551, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(13, 3, 2011), 19), 74545, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(13, 3, 2011), 18), 74538, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(13, 3, 2011), 17), 74530, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(13, 3, 2011), 16), 74521, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(13, 3, 2011), 15), 74511, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(13, 3, 2011), 14), 74500, NormalQuality, PulseAccumulatorPointType, 1 },
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x0a);  //  Day of week - still 0, or Sunday
        payload.push_back(0x08);  //  Blink count = 321, or 0x141
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x1f);  //  72.0 = 126 = 7e
        payload.push_back(0x9f);  //  8.0  =  62 = 3e
        payload.push_back(0x3c);  //  6.0  =  60 = 3c

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xC0, payload, description, points);

        //  make sure it's null, we're done with today
        BOOST_CHECK( ! r.get());

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(14, 3, 2011),  0), 321.0, NormalQuality, PulseAccumulatorPointType, 20 },

            { CtiTime(CtiDate(13, 3, 2011), 13), 74440, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(13, 3, 2011), 12), 74360, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(13, 3, 2011), 11), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(13, 3, 2011), 10), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(13, 3, 2011),  9), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(13, 3, 2011),  8), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(13, 3, 2011),  7), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(13, 3, 2011),  6), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(13, 3, 2011),  5), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(13, 3, 2011),  4), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(13, 3, 2011),  3), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            //  The Time That Shall Not Be Created
            //{ CtiTime(CtiDate(13, 3, 2011),  2), 73638, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(13, 3, 2011),  1), 73640, NormalQuality, PulseAccumulatorPointType,  1 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }
}

BOOST_AUTO_TEST_CASE(test_command_single_day_read_channel_2)
{
    const CtiDate start(11, 8, 2010);
    const CtiTime execute_time(CtiDate(17, 8, 2010), 10);

    Mct420HourlyReadCommand hourlyRead(start, CtiDate::neg_infin, 2);

    {
        auto r = hourlyRead.executeCommand(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function(), 0xDB);
        BOOST_CHECK_EQUAL(r->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),   13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x62);  //  Day of week - 3, or Wednesday
        payload.push_back(0xc5);
        payload.push_back(0x09);  //  Deltas are increasing from 0.1 at hour 24 to 1.1 at hour 14
        payload.push_back(0x10);
        payload.push_back(0x1c);
        payload.push_back(0x30);
        payload.push_back(0x50);
        payload.push_back(0x80);
        payload.push_back(0xc1);
        payload.push_back(0x01);

        payload.push_back(0x01);  //  total kWh at midnight - 7456.5 kWh
        payload.push_back(0x23);
        payload.push_back(0x45);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xDB, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        auto er = dynamic_cast<DlcCommand::emetcon_request_t *>(r.get());

        BOOST_REQUIRE(er);

        BOOST_CHECK_EQUAL(er->function(), 0xDA);
        BOOST_CHECK_EQUAL(er->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(er->length(),   13);
        BOOST_CHECK_EQUAL(er->payload().size(), 0);

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(12, 8, 2010),  0), 74565, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010), 23), 74563, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010), 22), 74560, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010), 21), 74556, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010), 20), 74551, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010), 19), 74545, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010), 18), 74538, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010), 17), 74530, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010), 16), 74521, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010), 15), 74511, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010), 14), 74500, NormalQuality, PulseAccumulatorPointType, 2 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x6a);  //  Day of week - still 3, or Wednesday
        payload.push_back(0x08);  //  Blink count = 321, or 0x141
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x1f);  //  72.0 = 126 = 7e
        payload.push_back(0x9f);  //  8.0  =  62 = 3e
        payload.push_back(0x3c);  //  6.0  =  60 = 3c

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xDA, payload, description, points);

        //  make sure it's null, we're done with today
        BOOST_CHECK( ! r.get());

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(12, 8, 2010),  0), 321.0, NormalQuality, PulseAccumulatorPointType, 20 },

            { CtiTime(CtiDate(11, 8, 2010), 13), 74440, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010), 12), 74360, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010), 11), 73640, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010), 10), 73640, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010),  9), 73640, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010),  8), 73640, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010),  7), 73640, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010),  6), 73640, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010),  5), 73640, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010),  4), 73640, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010),  3), 73640, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010),  2), 73640, NormalQuality, PulseAccumulatorPointType, 2 },
            { CtiTime(CtiDate(11, 8, 2010),  1), 73640, NormalQuality, PulseAccumulatorPointType, 2 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }
}

BOOST_AUTO_TEST_CASE(test_command_single_day_read_yesterday)
{
    const CtiTime execute_time(CtiDate(17, 8, 2010), 10);

    Mct420HourlyReadCommand hourlyRead(CtiDate::neg_infin, CtiDate::neg_infin, 1);

    {
        auto r = hourlyRead.executeCommand(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function(), 0xC1);
        BOOST_CHECK_EQUAL(r->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),   13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x22);  //  Day of week - 1, or Monday
        payload.push_back(0xc5);
        payload.push_back(0x09);  //  Deltas are increasing from 0.1 at hour 24 to 1.1 at hour 14
        payload.push_back(0x10);
        payload.push_back(0x1c);
        payload.push_back(0x30);
        payload.push_back(0x50);
        payload.push_back(0x80);
        payload.push_back(0xc1);
        payload.push_back(0x01);

        payload.push_back(0x01);  //  total kWh at midnight - 7456.5 kWh
        payload.push_back(0x23);
        payload.push_back(0x45);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xC1, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        auto er = dynamic_cast<DlcCommand::emetcon_request_t *>(r.get());

        BOOST_REQUIRE(er);

        BOOST_CHECK_EQUAL(er->function(), 0xC0);
        BOOST_CHECK_EQUAL(er->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(er->length(),   13);
        BOOST_CHECK_EQUAL(er->payload().size(), 0);

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(17, 8, 2010),  0), 74565, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010), 23), 74563, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010), 22), 74560, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010), 21), 74556, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010), 20), 74551, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010), 19), 74545, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010), 18), 74538, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010), 17), 74530, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010), 16), 74521, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010), 15), 74511, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010), 14), 74500, NormalQuality, PulseAccumulatorPointType, 1 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x2a);  //  Day of week - still 1, or Monday
        payload.push_back(0x08);  //  Blink count = 321, or 0x141
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x1f);  //  72.0 = 126 = 7e
        payload.push_back(0x9f);  //  8.0  =  62 = 3e
        payload.push_back(0x3c);  //  6.0  =  60 = 3c

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xC0, payload, description, points);

        //  make sure it's null, we're done with today
        BOOST_CHECK( ! r.get());

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(17, 8, 2010),  0), 321.0, NormalQuality, PulseAccumulatorPointType, 20 },

            { CtiTime(CtiDate(16, 8, 2010), 13), 74440, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010), 12), 74360, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010), 11), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010), 10), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010),  9), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010),  8), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010),  7), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010),  6), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010),  5), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010),  4), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010),  3), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010),  2), 73640, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(16, 8, 2010),  1), 73640, NormalQuality, PulseAccumulatorPointType, 1 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }
}

BOOST_AUTO_TEST_CASE(test_command_multi_day_read_dst_active)
{
    const CtiDate start(11, 8, 2010);
    const CtiDate end  (12, 8, 2010);
    const CtiTime execute_time(CtiDate(14, 8, 2010), 10);

    Mct420HourlyReadCommand hourlyRead(start, end, 1);

    {
        auto r = hourlyRead.executeCommand(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function(), 0xC5);
        BOOST_CHECK_EQUAL(r->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),   13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x62);  //  Day of week - 3, or Wednesday
        payload.push_back(0xc5);
        payload.push_back(0x09);  //  Deltas are increasing from 0.1 at hour 24 to 1.1 at hour 14
        payload.push_back(0x10);
        payload.push_back(0x1c);
        payload.push_back(0x30);
        payload.push_back(0x50);
        payload.push_back(0x80);
        payload.push_back(0xc1);
        payload.push_back(0x01);

        payload.push_back(0x01);  //  total kWh at midnight - 7456.5 kWh
        payload.push_back(0x23);
        payload.push_back(0x45);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xC7, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        auto er = dynamic_cast<DlcCommand::emetcon_request_t *>(r.get());

        BOOST_REQUIRE(er);

        BOOST_CHECK_EQUAL(er->function(), 0xC4);
        BOOST_CHECK_EQUAL(er->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(er->length(),   13);
        BOOST_CHECK_EQUAL(er->payload().size(), 0);

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(12, 8, 2010),  0), 74565, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 23), 74563, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 22), 74560, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 21), 74556, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 20), 74551, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 19), 74545, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 18), 74538, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 17), 74530, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 16), 74521, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 15), 74511, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 14), 74500, NormalQuality, PulseAccumulatorPointType, 1 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x6a);  //  Day of week - still 3, or Wednesday
        payload.push_back(0x08);  //  Blink count = 321, or 0x141
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x1f);  //  72.0 = 126 = 7e
        payload.push_back(0x9f);  //  8.0  =  62 = 3e
        payload.push_back(0x3c);  //  6.0  =  60 = 3c

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xC4, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        auto er = dynamic_cast<DlcCommand::emetcon_request_t *>(r.get());

        BOOST_REQUIRE(er);

        BOOST_CHECK_EQUAL(er->function(), 0xC3);
        BOOST_CHECK_EQUAL(er->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(er->length(),   13);
        BOOST_CHECK_EQUAL(er->payload().size(), 0);

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(12, 8, 2010),  0), 321.0, NormalQuality, PulseAccumulatorPointType, 20 },

            { CtiTime(CtiDate(11, 8, 2010), 13), 74440, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 8, 2010), 12), 74360, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 8, 2010), 11), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 8, 2010), 10), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 8, 2010),  9), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 8, 2010),  8), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 8, 2010),  7), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 8, 2010),  6), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 8, 2010),  5), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 8, 2010),  4), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 8, 2010),  3), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 8, 2010),  2), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(11, 8, 2010),  1), 73640, NormalQuality, PulseAccumulatorPointType,  1 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x82);  //  Day of week - 4, or Thursday
        payload.push_back(0xc5);
        payload.push_back(0x09);  //  Deltas are increasing from 0.1 at hour 24 to 1.1 at hour 14
        payload.push_back(0x10);
        payload.push_back(0x1c);
        payload.push_back(0x30);
        payload.push_back(0x50);
        payload.push_back(0x80);
        payload.push_back(0xc1);
        payload.push_back(0x01);

        payload.push_back(0x01);  //  total kWh at midnight - 7456.5 kWh
        payload.push_back(0x23);
        payload.push_back(0x45);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xC3, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        auto er = dynamic_cast<DlcCommand::emetcon_request_t *>(r.get());

        BOOST_REQUIRE(er);

        BOOST_CHECK_EQUAL(er->function(), 0xC2);
        BOOST_CHECK_EQUAL(er->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(er->length(),   13);
        BOOST_CHECK_EQUAL(er->payload().size(), 0);

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(13, 8, 2010),  0), 74565, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(12, 8, 2010), 23), 74563, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(12, 8, 2010), 22), 74560, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(12, 8, 2010), 21), 74556, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(12, 8, 2010), 20), 74551, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(12, 8, 2010), 19), 74545, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(12, 8, 2010), 18), 74538, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(12, 8, 2010), 17), 74530, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(12, 8, 2010), 16), 74521, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(12, 8, 2010), 15), 74511, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(12, 8, 2010), 14), 74500, NormalQuality, PulseAccumulatorPointType, 1 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x8a);  //  Day of week - still 4, or Thursday
        payload.push_back(0x08);  //  Blink count = 321, or 0x141
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x1f);  //  72.0 = 126 = 7e
        payload.push_back(0x9f);  //  8.0  =  62 = 3e
        payload.push_back(0x3c);  //  6.0  =  60 = 3c

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xC2, payload, description, points);

        //  make sure it's null, we're done with the multi-day read
        BOOST_CHECK( ! r.get());

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(13, 8, 2010),  0), 321.0, NormalQuality, PulseAccumulatorPointType, 20 },

            { CtiTime(CtiDate(12, 8, 2010), 13), 74440, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(12, 8, 2010), 12), 74360, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(12, 8, 2010), 11), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(12, 8, 2010), 10), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(12, 8, 2010),  9), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(12, 8, 2010),  8), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(12, 8, 2010),  7), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(12, 8, 2010),  6), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(12, 8, 2010),  5), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(12, 8, 2010),  4), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(12, 8, 2010),  3), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(12, 8, 2010),  2), 73640, NormalQuality, PulseAccumulatorPointType,  1 },
            { CtiTime(CtiDate(12, 8, 2010),  1), 73640, NormalQuality, PulseAccumulatorPointType,  1 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }
}

BOOST_AUTO_TEST_CASE(test_execute_bad_parameters)
{
    const CtiTime execute_time(CtiDate(17, 8, 2010), 10);

    {
        const CtiDate start(16, 8, 2010);

        Mct420HourlyReadCommand hourlyRead(start, start, 0);

        try
        {
            hourlyRead.executeCommand(execute_time);

            BOOST_FAIL("Mct420HourlyReadCommand::executeCommand() did not throw");
        }
        catch( DlcCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
            BOOST_CHECK_EQUAL(ex.error_description, "Invalid channel (0) for hourly read request; must be 1 or 2");
        }
    }

    {
        const CtiDate start(16, 8, 2010);

        Mct420HourlyReadCommand hourlyRead(start, start, 3);

        try
        {
            hourlyRead.executeCommand(execute_time);

            BOOST_FAIL("Mct420HourlyReadCommand::executeCommand() did not throw");
        }
        catch( DlcCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
            BOOST_CHECK_EQUAL(ex.error_description, "Invalid channel (3) for hourly read request; must be 1 or 2");
        }
    }

    {
        const CtiDate start(17, 8, 2010);

        Mct420HourlyReadCommand hourlyRead(start, start, 1);

        try
        {
            hourlyRead.executeCommand(execute_time);

            BOOST_FAIL("Mct420HourlyReadCommand::executeCommand() did not throw");
        }
        catch( DlcCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
            BOOST_CHECK_EQUAL(ex.error_description, "Invalid date (08/17/2010) for hourly read request; must be before today (08/17/2010)");
        }
    }

    {
        const CtiDate start(18, 8, 2010);

        Mct420HourlyReadCommand hourlyRead(start, start, 1);

        try
        {
            hourlyRead.executeCommand(execute_time);

            BOOST_FAIL("Mct420HourlyReadCommand::executeCommand() did not throw");
        }
        catch( DlcCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
            BOOST_CHECK_EQUAL(ex.error_description, "Invalid date (08/18/2010) for hourly read request; must be before today (08/17/2010)");
        }
    }

    {
        const CtiDate start(9, 8, 2010);

        Mct420HourlyReadCommand hourlyRead(start, start, 1);

        try
        {
            hourlyRead.executeCommand(execute_time);

            BOOST_FAIL("Mct420HourlyReadCommand::executeCommand() did not throw");
        }
        catch( DlcCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
            BOOST_CHECK_EQUAL(ex.error_description, "Invalid date (08/09/2010) for hourly read request; must be no more than 7 days ago (08/10/2010)");
        }
    }

    {
        const CtiDate start(16, 8, 2010);
        const CtiDate end  (15, 8, 2010);

        Mct420HourlyReadCommand hourlyRead(start, end, 1);

        try
        {
            hourlyRead.executeCommand(execute_time);

            BOOST_FAIL("Mct420HourlyReadCommand::executeCommand() did not throw");
        }
        catch( DlcCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
            BOOST_CHECK_EQUAL(ex.error_description, "Invalid end date (08/15/2010) for hourly read request; must be after begin date (08/16/2010)");
        }
    }

    {
        const CtiDate start(16, 8, 2010);
        const CtiDate end  (17, 8, 2010);

        Mct420HourlyReadCommand hourlyRead(start, end, 1);

        try
        {
            hourlyRead.executeCommand(execute_time);

            BOOST_FAIL("Mct420HourlyReadCommand::executeCommand() did not throw");
        }
        catch( DlcCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
            BOOST_CHECK_EQUAL(ex.error_description, "Invalid date (08/17/2010) for hourly read request; must be before today (08/17/2010)");
        }
    }

    {
        const CtiDate end(15, 8, 2010);

        Mct420HourlyReadCommand hourlyRead(CtiDate::neg_infin, end, 1);

        try
        {
            hourlyRead.executeCommand(execute_time);

            BOOST_FAIL("Mct420HourlyReadCommand::executeCommand() did not throw");
        }
        catch( DlcCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
            BOOST_CHECK_EQUAL(ex.error_description, "Invalid end date (08/15/2010) for hourly read request; must be after begin date (08/16/2010)");
        }
    }
}


BOOST_AUTO_TEST_CASE(test_decode_invalid_midnight_kwh_reading)
{
    const CtiDate start(11, 8, 2010);
    const CtiTime execute_time(CtiDate(17, 8, 2010), 10);

    Mct420HourlyReadCommand hourlyRead(start, start, 1);

    {
        auto r = hourlyRead.executeCommand(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function(), 0xCB);
        BOOST_CHECK_EQUAL(r->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),   13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x62);  //  Day of week - 3, or Wednesday
        payload.push_back(0xc5);
        payload.push_back(0x09);  //  Deltas are increasing from 0.1 at hour 24 to 1.1 at hour 14
        payload.push_back(0x10);
        payload.push_back(0x1c);
        payload.push_back(0x30);
        payload.push_back(0x50);
        payload.push_back(0x80);
        payload.push_back(0xc1);
        payload.push_back(0x01);

        payload.push_back(0xff);  //  total kWh at midnight - invalid/FFFC, or Not Yet Available.
        payload.push_back(0xff);
        payload.push_back(0xfc);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xCB, payload, description, points);

        //  make sure it's not null, we're going to try for the blink count
        BOOST_CHECK(r.get());

        auto er = dynamic_cast<DlcCommand::emetcon_request_t *>(r.get());

        BOOST_REQUIRE(er);

        BOOST_CHECK_EQUAL(er->function(), 0xCA);
        BOOST_CHECK_EQUAL(er->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(er->length(),   2);
        BOOST_CHECK_EQUAL(er->payload().size(), 0);

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(12, 8, 2010),  0), 0, InvalidQuality, PulseAccumulatorPointType, 1 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x6a);  //  Day of week - still 3, or Wednesday
        payload.push_back(0x08);  //  Blink count = 321, or 0x141
        //  deltas are omitted

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xCA, payload, description, points);

        //  make sure it's null, we're done with today
        BOOST_CHECK( ! r.get());

        //  just the blink count, since the kWh was toasted
        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(12, 8, 2010),  0), 321.0, NormalQuality, PulseAccumulatorPointType, 20 },
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }
}

BOOST_AUTO_TEST_CASE(test_decode_one_bad_delta_on_first_read)
{
    const CtiDate start(11, 8, 2010);
    const CtiTime execute_time(CtiDate(17, 8, 2010), 10);

    Mct420HourlyReadCommand hourlyRead(start, start, 1);

    {
        auto r = hourlyRead.executeCommand(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function(), 0xCB);
        BOOST_CHECK_EQUAL(r->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),   13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x62);  //  Day of week - 3, or Wednesday
        payload.push_back(0xc5);
        payload.push_back(0x09);  //  Deltas are increasing from 0.1 at hour 24 to 1.1 at hour 14
        payload.push_back(0x10);
        payload.push_back(0x1c);
        payload.push_back(0x30);
        payload.push_back(0x50);
        payload.push_back(0x9f);  //  delta 3 (22-23) is invalid
        payload.push_back(0xc1);  //  delta 2 (23-00) is okay
        payload.push_back(0x01);  //  delta 1 (00-01) is okay

        payload.push_back(0x01);  //  total kWh at midnight - 7456.5 kWh
        payload.push_back(0x23);
        payload.push_back(0x45);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xCB, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        auto er = dynamic_cast<DlcCommand::emetcon_request_t *>(r.get());

        BOOST_REQUIRE(er);

        BOOST_CHECK_EQUAL(er->function(), 0xCA);
        BOOST_CHECK_EQUAL(er->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(er->length(),   2);
        BOOST_CHECK_EQUAL(er->payload().size(), 0);

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(12, 8, 2010),  0), 74565, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 23), 74563, NormalQuality, PulseAccumulatorPointType, 1 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x6a);  //  Day of week - still 3, or Wednesday
        payload.push_back(0x08);  //  Blink count = 321, or 0x141
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x1f);  //  72.0 = 126 = 7e
        payload.push_back(0x9f);  //  8.0  =  62 = 3e
        payload.push_back(0x3c);  //  6.0  =  60 = 3c

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xCA, payload, description, points);

        //  make sure it's null, we're done with today
        BOOST_CHECK( ! r.get());

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(12, 8, 2010),  0), 321.0, NormalQuality, PulseAccumulatorPointType, 20 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }
}


BOOST_AUTO_TEST_CASE(test_decode_all_bad_deltas_on_first_read)
{
    const CtiDate start(11, 8, 2010);
    const CtiTime execute_time(CtiDate(17, 8, 2010), 10);

    Mct420HourlyReadCommand hourlyRead(start, start, 1);

    {
        auto r = hourlyRead.executeCommand(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function(), 0xCB);
        BOOST_CHECK_EQUAL(r->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),   13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x7f);  //  Day of week - 3, or Wednesday
        payload.push_back(0xff);
        payload.push_back(0xff);  //  all deltas are 0x7f, or invalid
        payload.push_back(0xff);
        payload.push_back(0xff);
        payload.push_back(0xff);
        payload.push_back(0xff);
        payload.push_back(0xff);
        payload.push_back(0xff);
        payload.push_back(0xff);

        payload.push_back(0x01);  //  total kWh at midnight - 7456.5 kWh
        payload.push_back(0x23);
        payload.push_back(0x45);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xCB, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        auto er = dynamic_cast<DlcCommand::emetcon_request_t *>(r.get());

        BOOST_REQUIRE(er);

        BOOST_CHECK_EQUAL(er->function(), 0xCA);
        BOOST_CHECK_EQUAL(er->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(er->length(),   2);
        BOOST_CHECK_EQUAL(er->payload().size(), 0);

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(12, 8, 2010),  0), 74565, NormalQuality, PulseAccumulatorPointType, 1 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }
}

BOOST_AUTO_TEST_CASE(test_decode_payload_too_small_once)
{
    const CtiDate start(11, 8, 2010);
    const CtiTime execute_time(CtiDate(17, 8, 2010), 10);

    Mct420HourlyReadCommand hourlyRead(start, start, 1);

    {
        auto r = hourlyRead.executeCommand(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function(), 0xCB);
        BOOST_CHECK_EQUAL(r->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),   13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x62);  //  Day of week - 3, or Wednesday
        payload.push_back(0xc5);
        payload.push_back(0x09);  //  Deltas are increasing from 0.1 at hour 24 to 1.1 at hour 14
        payload.push_back(0x10);
        payload.push_back(0x1c);
        payload.push_back(0x30);
        payload.push_back(0x50);
        payload.push_back(0x80);
        payload.push_back(0xc1);
        payload.push_back(0x01);

        payload.push_back(0x01);  //  total kWh at midnight - 7456.5 kWh
        payload.push_back(0x23);
        //payload.push_back(0x45);  //  missing last byte

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xCB, payload, description, points);

        //  make sure it's not null, we're retrying
        BOOST_CHECK(r.get());

        auto er = dynamic_cast<DlcCommand::emetcon_request_t *>(r.get());

        BOOST_REQUIRE(er);

        BOOST_CHECK_EQUAL(er->function(), 0xCB);
        BOOST_CHECK_EQUAL(er->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(er->length(),   13);
        BOOST_CHECK_EQUAL(er->payload().size(), 0);

        BOOST_CHECK(points.empty());
        BOOST_CHECK_EQUAL(description, "Payload too small\nRetrying (1 remaining)");
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x62);  //  Day of week - 3, or Wednesday
        payload.push_back(0xc5);
        payload.push_back(0x09);  //  Deltas are increasing from 0.1 at hour 24 to 1.1 at hour 14
        payload.push_back(0x10);
        payload.push_back(0x1c);
        payload.push_back(0x30);
        payload.push_back(0x50);
        payload.push_back(0x80);
        payload.push_back(0xc1);
        payload.push_back(0x01);

        payload.push_back(0x01);  //  total kWh at midnight - 7456.5 kWh
        payload.push_back(0x23);
        payload.push_back(0x45);

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xCB, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        auto er = dynamic_cast<DlcCommand::emetcon_request_t *>(r.get());

        BOOST_REQUIRE(er);

        BOOST_CHECK_EQUAL(er->function(), 0xCA);
        BOOST_CHECK_EQUAL(er->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(er->length(),   13);
        BOOST_CHECK_EQUAL(er->payload().size(), 0);

        expected_pointdata expected[] =
        {
            { CtiTime(CtiDate(12, 8, 2010),  0), 74565, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 23), 74563, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 22), 74560, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 21), 74556, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 20), 74551, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 19), 74545, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 18), 74538, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 17), 74530, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 16), 74521, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 15), 74511, NormalQuality, PulseAccumulatorPointType, 1 },
            { CtiTime(CtiDate(11, 8, 2010), 14), 74500, NormalQuality, PulseAccumulatorPointType, 1 }
        };

        unsigned expected_size = sizeof(expected) / sizeof(expected_pointdata);

        BOOST_CHECK_EQUAL_COLLECTIONS(points.begin(), points.end(), expected, expected + expected_size);
    }
}

BOOST_AUTO_TEST_CASE(test_decode_bad_weekday)
{
    const CtiDate start(11, 8, 2010);
    const CtiTime execute_time(CtiDate(17, 8, 2010), 10);

    Mct420HourlyReadCommand hourlyRead(start, start, 1);

    {
        auto r = hourlyRead.executeCommand(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function(), 0xCB);
        BOOST_CHECK_EQUAL(r->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),   13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x42);  //  Day of week - 2, or Tuesday
        payload.push_back(0xc5);
        payload.push_back(0x09);  //  Deltas are increasing from 0.1 at hour 24 to 1.1 at hour 14
        payload.push_back(0x10);
        payload.push_back(0x1c);
        payload.push_back(0x30);
        payload.push_back(0x50);
        payload.push_back(0x80);
        payload.push_back(0xc1);
        payload.push_back(0x01);

        payload.push_back(0x01);  //  total kWh at midnight - 7456.5 kWh
        payload.push_back(0x23);
        payload.push_back(0x45);

        string description;
        std::vector<DlcCommand::point_data> points;

        //  first retry
        {
            auto r = hourlyRead.decodeCommand(execute_time, 0xCB, payload, description, points);

            //  verify retry
            BOOST_CHECK_EQUAL(description, "Day of week does not match (2 != 3)\nRetrying (1 remaining)");
            BOOST_CHECK_EQUAL(points.size(), 0);

            auto er = dynamic_cast<DlcCommand::emetcon_request_t *>(r.get());

            BOOST_REQUIRE(er);

            BOOST_CHECK_EQUAL(er->function(), 0xCB);
            BOOST_CHECK_EQUAL(er->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL(er->length(),   13);
            BOOST_CHECK_EQUAL(er->payload().size(), 0);
        }

        //  second retry
        {
            auto r = hourlyRead.decodeCommand(execute_time, 0xCB, payload, description, points);

            //  verify retry
            BOOST_CHECK_EQUAL(description, "Day of week does not match (2 != 3)\nRetrying (0 remaining)");
            BOOST_CHECK_EQUAL(points.size(), 0);

            auto er = dynamic_cast<DlcCommand::emetcon_request_t *>(r.get());

            BOOST_REQUIRE(er);

            BOOST_CHECK_EQUAL(er->function(), 0xCB);
            BOOST_CHECK_EQUAL(er->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL(er->length(),   13);
            BOOST_CHECK_EQUAL(er->payload().size(), 0);
        }

        //  failure
        try
        {
            auto r = hourlyRead.decodeCommand(execute_time, 0xCB, payload, description, points);

            BOOST_FAIL("Mct420HourlyReadCommand::decodeCommand() did not throw");
        }
        catch( DlcCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::InvalidTimestamp);
            BOOST_CHECK_EQUAL(ex.error_description, "Day of week does not match (2 != 3)\nRetries exhausted");
        }
    }
}


BOOST_AUTO_TEST_CASE(test_decode_wrong_read)
{
    const CtiDate start(11, 8, 2010);
    const CtiTime execute_time(CtiDate(17, 8, 2010), 10);

    Mct420HourlyReadCommand hourlyRead(start, start, 1);

    {
        auto r = hourlyRead.executeCommand(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function(), 0xCB);
        BOOST_CHECK_EQUAL(r->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),   13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x6a);  //  Day of week - 3, or Wednesday
        payload.push_back(0x08);  //  Blink count = 321, or 0x141
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x1f);  //  72.0 = 126 = 7e
        payload.push_back(0x9f);  //  8.0  =  62 = 3e
        payload.push_back(0x3c);  //  6.0  =  60 = 3c

        string description;
        std::vector<DlcCommand::point_data> points;

        auto r = hourlyRead.decodeCommand(execute_time, 0xCA, payload, description, points);

        BOOST_CHECK(points.empty());
        BOOST_CHECK_EQUAL(description, "Wrong read performed (0xca)\nRetrying (1 remaining)");

        //  make sure it's not null
        BOOST_CHECK(r.get());

        auto er = dynamic_cast<DlcCommand::emetcon_request_t *>(r.get());

        BOOST_REQUIRE(er);

        BOOST_CHECK_EQUAL(er->function(), 0xCB);
        BOOST_CHECK_EQUAL(er->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(er->length(),   13);
        BOOST_CHECK_EQUAL(er->payload().size(), 0);
    }
}


BOOST_AUTO_TEST_CASE(test_decode_expired)
{
    const CtiDate start(11, 8, 2010);
    const CtiTime execute_time(CtiDate(17, 8, 2010), 10);

    Mct420HourlyReadCommand hourlyRead(start, start, 1);

    {
        auto r = hourlyRead.executeCommand(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function(), 0xCB);
        BOOST_CHECK_EQUAL(r->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),   13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::Bytes payload;

        payload.push_back(0x6a);  //  Day of week - 3, or Wednesday
        payload.push_back(0x08);  //  Blink count = 321, or 0x141
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x00);
        payload.push_back(0x1f);  //  72.0 = 126 = 7e
        payload.push_back(0x9f);  //  8.0  =  62 = 3e
        payload.push_back(0x3c);  //  6.0  =  60 = 3c

        string description;
        std::vector<DlcCommand::point_data> points;

        const CtiTime decode_time(CtiDate(24, 8, 2010), 10);

        try
        {
            auto r = hourlyRead.decodeCommand(decode_time, 0xCA, payload, description, points);

            BOOST_FAIL("Mct420HourlyReadCommand::decodeCommand() did not throw");
        }
        catch( DlcCommand::CommandException &ex )
        {
            BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
            BOOST_CHECK_EQUAL(ex.error_description, "Invalid date (08/11/2010) for hourly read request; must be no more than 7 days ago (08/17/2010)");
        }
    }
}


BOOST_AUTO_TEST_CASE(test_decode_read_timeout)
{
    const CtiDate start(11, 8, 2010);
    const CtiTime execute_time(CtiDate(17, 8, 2010), 10);

    Mct420HourlyReadCommand hourlyRead(start, start, 1);

    {
        auto r = hourlyRead.executeCommand(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function(), 0xCB);
        BOOST_CHECK_EQUAL(r->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),   13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        string description;

        auto r = hourlyRead.error(execute_time, ClientErrors::ReadTimeout, description);

        BOOST_CHECK_EQUAL(description, "Timeout Reading from Port\nRetrying (1 remaining)");

        //  make sure it's not null
        BOOST_CHECK(r.get());

        auto er = dynamic_cast<DlcCommand::emetcon_request_t *>(r.get());

        BOOST_REQUIRE(er);

        BOOST_CHECK_EQUAL(er->function(), 0xCB);
        BOOST_CHECK_EQUAL(er->io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(er->length(),   13);
        BOOST_CHECK_EQUAL(er->payload().size(), 0);
    }
}


BOOST_AUTO_TEST_CASE(test_request_day_begin)
{
    struct test : Mct420HourlyReadCommand
    {
        using Mct420HourlyReadCommand::requestDayBegin;
    };

    const CtiDate yesterday(16, 8, 2010);

    {
        const CtiDate start(11, 8, 2010);

        DlcCommand::read_request_t r = test::requestDayBegin(start, 1, yesterday);

        BOOST_CHECK_EQUAL(r.function(), 0xCA);
        BOOST_CHECK_EQUAL(r.io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r.length(),   13);
        BOOST_CHECK_EQUAL(r.payload().size(), 0);
    }

    {
        const CtiDate start(12, 8, 2010);

        DlcCommand::read_request_t r = test::requestDayBegin(start, 1, yesterday);

        BOOST_CHECK_EQUAL(r.function(), 0xC8);
        BOOST_CHECK_EQUAL(r.io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r.length(),   13);
        BOOST_CHECK_EQUAL(r.payload().size(), 0);
    }

    {
        const CtiDate start(16, 8, 2010);

        DlcCommand::read_request_t r = test::requestDayBegin(start, 1, yesterday);

        BOOST_CHECK_EQUAL(r.function(), 0xC0);
        BOOST_CHECK_EQUAL(r.io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r.length(),   13);
        BOOST_CHECK_EQUAL(r.payload().size(), 0);
    }

    try
    {
        const CtiDate start(17, 8, 2010);

        DlcCommand::read_request_t r = test::requestDayBegin(start, 1, yesterday);

        BOOST_FAIL("Mct420HourlyReadCommand::requestDayBegin() did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid date (08/17/2010) for hourly read request; must be before today (08/17/2010)");
    }

    try
    {
        const CtiDate start(9, 8, 2010);

        DlcCommand::read_request_t r = test::requestDayBegin(start, 1, yesterday);

        BOOST_FAIL("Mct420HourlyReadCommand::requestDayBegin() did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid date (08/09/2010) for hourly read request; must be no more than 7 days ago (08/10/2010)");
    }
}


BOOST_AUTO_TEST_CASE(test_request_day_end)
{
    struct test : Mct420HourlyReadCommand
    {
        using Mct420HourlyReadCommand::requestDayEnd;
    };

    const CtiDate yesterday(16, 8, 2010);

    {
        const CtiDate start(11, 8, 2010);

        DlcCommand::read_request_t r = test::requestDayEnd(start, 1, yesterday);

        BOOST_CHECK_EQUAL(r.function(), 0xCB);
        BOOST_CHECK_EQUAL(r.io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r.length(),   13);
        BOOST_CHECK_EQUAL(r.payload().size(), 0);
    }

    {
        const CtiDate start(12, 8, 2010);

        DlcCommand::read_request_t r = test::requestDayEnd(start, 1, yesterday);

        BOOST_CHECK_EQUAL(r.function(), 0xC9);
        BOOST_CHECK_EQUAL(r.io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r.length(),   13);
        BOOST_CHECK_EQUAL(r.payload().size(), 0);
    }

    {
        const CtiDate start(16, 8, 2010);

        DlcCommand::read_request_t r = test::requestDayEnd(start, 1, yesterday);

        BOOST_CHECK_EQUAL(r.function(), 0xC1);
        BOOST_CHECK_EQUAL(r.io(),       Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r.length(),   13);
        BOOST_CHECK_EQUAL(r.payload().size(), 0);
    }

    try
    {
        const CtiDate start(17, 8, 2010);

        DlcCommand::read_request_t r = test::requestDayEnd(start, 1, yesterday);

        BOOST_FAIL("Mct420HourlyReadCommand::requestDayEnd() did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid date (08/17/2010) for hourly read request; must be before today (08/17/2010)");
    }

    try
    {
        const CtiDate start(9, 8, 2010);

        DlcCommand::read_request_t r = test::requestDayEnd(start, 1, yesterday);

        BOOST_FAIL("Mct420HourlyReadCommand::requestDayEnd() did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        ClientErrors::BadParameter);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid date (08/09/2010) for hourly read request; must be no more than 7 days ago (08/10/2010)");
    }
}


BOOST_AUTO_TEST_CASE(test_validate_date)
{
    struct test : Mct420HourlyReadCommand
    {
        using Mct420HourlyReadCommand::validateDate;
    };

    {
        const CtiDate yesterday(23, 8, 2010);

        BOOST_CHECK_THROW(test::validateDate(CtiDate(13, 8, 2010), yesterday), DlcCommand::CommandException);
        BOOST_CHECK_THROW(test::validateDate(CtiDate(14, 8, 2010), yesterday), DlcCommand::CommandException);
        BOOST_CHECK_THROW(test::validateDate(CtiDate(15, 8, 2010), yesterday), DlcCommand::CommandException);
        BOOST_CHECK_THROW(test::validateDate(CtiDate(16, 8, 2010), yesterday), DlcCommand::CommandException);

        BOOST_CHECK_NO_THROW(test::validateDate(CtiDate(17, 8, 2010), yesterday));
        BOOST_CHECK_NO_THROW(test::validateDate(CtiDate(18, 8, 2010), yesterday));
        BOOST_CHECK_NO_THROW(test::validateDate(CtiDate(19, 8, 2010), yesterday));
        BOOST_CHECK_NO_THROW(test::validateDate(CtiDate(20, 8, 2010), yesterday));
        BOOST_CHECK_NO_THROW(test::validateDate(CtiDate(21, 8, 2010), yesterday));
        BOOST_CHECK_NO_THROW(test::validateDate(CtiDate(22, 8, 2010), yesterday));
        BOOST_CHECK_NO_THROW(test::validateDate(CtiDate(23, 8, 2010), yesterday));

        BOOST_CHECK_THROW(test::validateDate(CtiDate(24, 8, 2010), yesterday), DlcCommand::CommandException);
        BOOST_CHECK_THROW(test::validateDate(CtiDate(25, 8, 2010), yesterday), DlcCommand::CommandException);
        BOOST_CHECK_THROW(test::validateDate(CtiDate(26, 8, 2010), yesterday), DlcCommand::CommandException);
        BOOST_CHECK_THROW(test::validateDate(CtiDate(27, 8, 2010), yesterday), DlcCommand::CommandException);
    }

    {
        const CtiDate yesterday(1, 1, 2012);

        BOOST_CHECK_THROW(test::validateDate(CtiDate(22, 12, 2011), yesterday), DlcCommand::CommandException);
        BOOST_CHECK_THROW(test::validateDate(CtiDate(23, 12, 2011), yesterday), DlcCommand::CommandException);
        BOOST_CHECK_THROW(test::validateDate(CtiDate(24, 12, 2011), yesterday), DlcCommand::CommandException);
        BOOST_CHECK_THROW(test::validateDate(CtiDate(25, 12, 2011), yesterday), DlcCommand::CommandException);

        BOOST_CHECK_NO_THROW(test::validateDate(CtiDate(26, 12, 2011), yesterday));
        BOOST_CHECK_NO_THROW(test::validateDate(CtiDate(27, 12, 2011), yesterday));
        BOOST_CHECK_NO_THROW(test::validateDate(CtiDate(28, 12, 2011), yesterday));
        BOOST_CHECK_NO_THROW(test::validateDate(CtiDate(29, 12, 2011), yesterday));
        BOOST_CHECK_NO_THROW(test::validateDate(CtiDate(30, 12, 2011), yesterday));
        BOOST_CHECK_NO_THROW(test::validateDate(CtiDate(31, 12, 2011), yesterday));
        BOOST_CHECK_NO_THROW(test::validateDate(CtiDate( 1,  1, 2012), yesterday));

        BOOST_CHECK_THROW(test::validateDate(CtiDate( 2, 1, 2012), yesterday), DlcCommand::CommandException);
        BOOST_CHECK_THROW(test::validateDate(CtiDate( 3, 1, 2012), yesterday), DlcCommand::CommandException);
        BOOST_CHECK_THROW(test::validateDate(CtiDate( 4, 1, 2012), yesterday), DlcCommand::CommandException);
        BOOST_CHECK_THROW(test::validateDate(CtiDate( 5, 1, 2012), yesterday), DlcCommand::CommandException);
    }
}


BOOST_AUTO_TEST_CASE(test_convert_delta)
{
    struct test : Mct420HourlyReadCommand
    {
        using Mct420HourlyReadCommand::convertDelta;
    };

    //  the range 0-60 should give 0-60
    {
        BOOST_CHECK_EQUAL(test::convertDelta( 0),  0);
        BOOST_CHECK_EQUAL(test::convertDelta( 1),  1);
        BOOST_CHECK_EQUAL(test::convertDelta( 2),  2);
        BOOST_CHECK_EQUAL(test::convertDelta( 3),  3);
        BOOST_CHECK_EQUAL(test::convertDelta( 4),  4);
        BOOST_CHECK_EQUAL(test::convertDelta( 5),  5);
        BOOST_CHECK_EQUAL(test::convertDelta( 6),  6);
        BOOST_CHECK_EQUAL(test::convertDelta( 7),  7);
        BOOST_CHECK_EQUAL(test::convertDelta( 8),  8);
        BOOST_CHECK_EQUAL(test::convertDelta( 9),  9);
        BOOST_CHECK_EQUAL(test::convertDelta(10), 10);
        BOOST_CHECK_EQUAL(test::convertDelta(11), 11);
        BOOST_CHECK_EQUAL(test::convertDelta(12), 12);
        BOOST_CHECK_EQUAL(test::convertDelta(13), 13);
        BOOST_CHECK_EQUAL(test::convertDelta(14), 14);
        BOOST_CHECK_EQUAL(test::convertDelta(15), 15);
        BOOST_CHECK_EQUAL(test::convertDelta(16), 16);
        BOOST_CHECK_EQUAL(test::convertDelta(17), 17);
        BOOST_CHECK_EQUAL(test::convertDelta(18), 18);
        BOOST_CHECK_EQUAL(test::convertDelta(19), 19);
        BOOST_CHECK_EQUAL(test::convertDelta(20), 20);
        BOOST_CHECK_EQUAL(test::convertDelta(21), 21);
        BOOST_CHECK_EQUAL(test::convertDelta(22), 22);
        BOOST_CHECK_EQUAL(test::convertDelta(23), 23);
        BOOST_CHECK_EQUAL(test::convertDelta(24), 24);
        BOOST_CHECK_EQUAL(test::convertDelta(25), 25);
        BOOST_CHECK_EQUAL(test::convertDelta(26), 26);
        BOOST_CHECK_EQUAL(test::convertDelta(27), 27);
        BOOST_CHECK_EQUAL(test::convertDelta(28), 28);
        BOOST_CHECK_EQUAL(test::convertDelta(29), 29);
        BOOST_CHECK_EQUAL(test::convertDelta(30), 30);
        BOOST_CHECK_EQUAL(test::convertDelta(31), 31);
        BOOST_CHECK_EQUAL(test::convertDelta(32), 32);
        BOOST_CHECK_EQUAL(test::convertDelta(33), 33);
        BOOST_CHECK_EQUAL(test::convertDelta(34), 34);
        BOOST_CHECK_EQUAL(test::convertDelta(35), 35);
        BOOST_CHECK_EQUAL(test::convertDelta(36), 36);
        BOOST_CHECK_EQUAL(test::convertDelta(37), 37);
        BOOST_CHECK_EQUAL(test::convertDelta(38), 38);
        BOOST_CHECK_EQUAL(test::convertDelta(39), 39);
        BOOST_CHECK_EQUAL(test::convertDelta(40), 40);
        BOOST_CHECK_EQUAL(test::convertDelta(41), 41);
        BOOST_CHECK_EQUAL(test::convertDelta(42), 42);
        BOOST_CHECK_EQUAL(test::convertDelta(43), 43);
        BOOST_CHECK_EQUAL(test::convertDelta(44), 44);
        BOOST_CHECK_EQUAL(test::convertDelta(45), 45);
        BOOST_CHECK_EQUAL(test::convertDelta(46), 46);
        BOOST_CHECK_EQUAL(test::convertDelta(47), 47);
        BOOST_CHECK_EQUAL(test::convertDelta(48), 48);
        BOOST_CHECK_EQUAL(test::convertDelta(49), 49);
        BOOST_CHECK_EQUAL(test::convertDelta(50), 50);
        BOOST_CHECK_EQUAL(test::convertDelta(51), 51);
        BOOST_CHECK_EQUAL(test::convertDelta(52), 52);
        BOOST_CHECK_EQUAL(test::convertDelta(53), 53);
        BOOST_CHECK_EQUAL(test::convertDelta(54), 54);
        BOOST_CHECK_EQUAL(test::convertDelta(55), 55);
        BOOST_CHECK_EQUAL(test::convertDelta(56), 56);
        BOOST_CHECK_EQUAL(test::convertDelta(57), 57);
        BOOST_CHECK_EQUAL(test::convertDelta(58), 58);
        BOOST_CHECK_EQUAL(test::convertDelta(59), 59);
        BOOST_CHECK_EQUAL(test::convertDelta(60), 60);
    }

    //  the range 61-126 should give 70-720
    {
        BOOST_CHECK_EQUAL(test::convertDelta( 61),  70);
        BOOST_CHECK_EQUAL(test::convertDelta( 62),  80);
        BOOST_CHECK_EQUAL(test::convertDelta( 63),  90);
        BOOST_CHECK_EQUAL(test::convertDelta( 64), 100);
        BOOST_CHECK_EQUAL(test::convertDelta( 65), 110);
        BOOST_CHECK_EQUAL(test::convertDelta( 66), 120);
        BOOST_CHECK_EQUAL(test::convertDelta( 67), 130);
        BOOST_CHECK_EQUAL(test::convertDelta( 68), 140);
        BOOST_CHECK_EQUAL(test::convertDelta( 69), 150);
        BOOST_CHECK_EQUAL(test::convertDelta( 70), 160);
        BOOST_CHECK_EQUAL(test::convertDelta( 71), 170);
        BOOST_CHECK_EQUAL(test::convertDelta( 72), 180);
        BOOST_CHECK_EQUAL(test::convertDelta( 73), 190);
        BOOST_CHECK_EQUAL(test::convertDelta( 74), 200);
        BOOST_CHECK_EQUAL(test::convertDelta( 75), 210);
        BOOST_CHECK_EQUAL(test::convertDelta( 76), 220);
        BOOST_CHECK_EQUAL(test::convertDelta( 77), 230);
        BOOST_CHECK_EQUAL(test::convertDelta( 78), 240);
        BOOST_CHECK_EQUAL(test::convertDelta( 79), 250);
        BOOST_CHECK_EQUAL(test::convertDelta( 80), 260);
        BOOST_CHECK_EQUAL(test::convertDelta( 81), 270);
        BOOST_CHECK_EQUAL(test::convertDelta( 82), 280);
        BOOST_CHECK_EQUAL(test::convertDelta( 83), 290);
        BOOST_CHECK_EQUAL(test::convertDelta( 84), 300);
        BOOST_CHECK_EQUAL(test::convertDelta( 85), 310);
        BOOST_CHECK_EQUAL(test::convertDelta( 86), 320);
        BOOST_CHECK_EQUAL(test::convertDelta( 87), 330);
        BOOST_CHECK_EQUAL(test::convertDelta( 88), 340);
        BOOST_CHECK_EQUAL(test::convertDelta( 89), 350);
        BOOST_CHECK_EQUAL(test::convertDelta( 90), 360);
        BOOST_CHECK_EQUAL(test::convertDelta( 91), 370);
        BOOST_CHECK_EQUAL(test::convertDelta( 92), 380);
        BOOST_CHECK_EQUAL(test::convertDelta( 93), 390);
        BOOST_CHECK_EQUAL(test::convertDelta( 94), 400);
        BOOST_CHECK_EQUAL(test::convertDelta( 95), 410);
        BOOST_CHECK_EQUAL(test::convertDelta( 96), 420);
        BOOST_CHECK_EQUAL(test::convertDelta( 97), 430);
        BOOST_CHECK_EQUAL(test::convertDelta( 98), 440);
        BOOST_CHECK_EQUAL(test::convertDelta( 99), 450);
        BOOST_CHECK_EQUAL(test::convertDelta(100), 460);
        BOOST_CHECK_EQUAL(test::convertDelta(101), 470);
        BOOST_CHECK_EQUAL(test::convertDelta(102), 480);
        BOOST_CHECK_EQUAL(test::convertDelta(103), 490);
        BOOST_CHECK_EQUAL(test::convertDelta(104), 500);
        BOOST_CHECK_EQUAL(test::convertDelta(105), 510);
        BOOST_CHECK_EQUAL(test::convertDelta(106), 520);
        BOOST_CHECK_EQUAL(test::convertDelta(107), 530);
        BOOST_CHECK_EQUAL(test::convertDelta(108), 540);
        BOOST_CHECK_EQUAL(test::convertDelta(109), 550);
        BOOST_CHECK_EQUAL(test::convertDelta(110), 560);
        BOOST_CHECK_EQUAL(test::convertDelta(111), 570);
        BOOST_CHECK_EQUAL(test::convertDelta(112), 580);
        BOOST_CHECK_EQUAL(test::convertDelta(113), 590);
        BOOST_CHECK_EQUAL(test::convertDelta(114), 600);
        BOOST_CHECK_EQUAL(test::convertDelta(115), 610);
        BOOST_CHECK_EQUAL(test::convertDelta(116), 620);
        BOOST_CHECK_EQUAL(test::convertDelta(117), 630);
        BOOST_CHECK_EQUAL(test::convertDelta(118), 640);
        BOOST_CHECK_EQUAL(test::convertDelta(119), 650);
        BOOST_CHECK_EQUAL(test::convertDelta(120), 660);
        BOOST_CHECK_EQUAL(test::convertDelta(121), 670);
        BOOST_CHECK_EQUAL(test::convertDelta(122), 680);
        BOOST_CHECK_EQUAL(test::convertDelta(123), 690);
        BOOST_CHECK_EQUAL(test::convertDelta(124), 700);
        BOOST_CHECK_EQUAL(test::convertDelta(125), 710);
        BOOST_CHECK_EQUAL(test::convertDelta(126), 720);
    }

    //  all values greater than 126 should give -1
    BOOST_CHECK_EQUAL(test::convertDelta(127), -1);
    BOOST_CHECK_EQUAL(test::convertDelta(32767), -1);
}

BOOST_AUTO_TEST_SUITE_END()
