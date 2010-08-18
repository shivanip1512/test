#include "cmd_mct410_hourlyread.h"

#define BOOST_TEST_MAIN "Testing Devices::Commands::Mct410HourlyReadCommand"

#include <boost/test/unit_test.hpp>

using namespace std;

BOOST_AUTO_TEST_CASE(test_command)
{
    {
        const CtiDate start(11, 8, 2010);
        const CtiTime execute_time(CtiDate(17, 8, 2010), 10);

        Cti::Devices::Commands::Mct410HourlyReadCommand hourlyRead(start, start, 1);

        {
            Cti::Devices::Commands::DlcCommand::request_ptr r = hourlyRead.execute(execute_time);

            //  make sure it's not null
            BOOST_CHECK(r.get());

            if( ! r.get() )
            {
                return;
            }

            BOOST_CHECK_EQUAL(r->function,  0x1CB);
            BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL(r->length(),  13);
            BOOST_CHECK_EQUAL(r->payload().size(), 0);
        }

        {
            Cti::Devices::Commands::DlcCommand::payload_t payload;

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
            std::vector<Cti::Devices::Commands::DlcCommand::point_data_t> points;

            Cti::Devices::Commands::DlcCommand::request_ptr r = hourlyRead.decode(execute_time, 0xCB, payload, description, points);

            //  make sure it's not null
            BOOST_CHECK(r.get());

            if( ! r.get() )
            {
                return;
            }

            BOOST_CHECK_EQUAL(r->function,  0x1CA);
            BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL(r->length(),  13);
            BOOST_CHECK_EQUAL(r->payload().size(), 0);

            BOOST_CHECK_EQUAL(points.size(), 11);

            BOOST_CHECK_EQUAL(points[ 0].time,  CtiTime(CtiDate(12, 8, 2010),  0));
            BOOST_CHECK_EQUAL(points[ 0].value, 7456.5);

            BOOST_CHECK_EQUAL(points[ 1].time,  CtiTime(CtiDate(11, 8, 2010), 23));
            BOOST_CHECK_EQUAL(points[ 1].value, 7456.3);

            BOOST_CHECK_EQUAL(points[ 2].time,  CtiTime(CtiDate(11, 8, 2010), 22));
            BOOST_CHECK_EQUAL(points[ 2].value, 7456.0);

            BOOST_CHECK_EQUAL(points[ 3].time,  CtiTime(CtiDate(11, 8, 2010), 21));
            BOOST_CHECK_EQUAL(points[ 3].value, 7455.6);

            BOOST_CHECK_EQUAL(points[ 4].time,  CtiTime(CtiDate(11, 8, 2010), 20));
            BOOST_CHECK_EQUAL(points[ 4].value, 7455.1);

            BOOST_CHECK_EQUAL(points[ 5].time,  CtiTime(CtiDate(11, 8, 2010), 19));
            BOOST_CHECK_EQUAL(points[ 5].value, 7454.5);

            BOOST_CHECK_EQUAL(points[ 6].time,  CtiTime(CtiDate(11, 8, 2010), 18));
            BOOST_CHECK_EQUAL(points[ 6].value, 7453.8);

            BOOST_CHECK_EQUAL(points[ 7].time,  CtiTime(CtiDate(11, 8, 2010), 17));
            BOOST_CHECK_EQUAL(points[ 7].value, 7453.0);

            BOOST_CHECK_EQUAL(points[ 8].time,  CtiTime(CtiDate(11, 8, 2010), 16));
            BOOST_CHECK_EQUAL(points[ 8].value, 7452.1);

            BOOST_CHECK_EQUAL(points[ 9].time,  CtiTime(CtiDate(11, 8, 2010), 15));
            BOOST_CHECK_EQUAL(points[ 9].value, 7451.1);

            BOOST_CHECK_EQUAL(points[10].time,  CtiTime(CtiDate(11, 8, 2010), 14));
            BOOST_CHECK_EQUAL(points[10].value, 7450.0);
        }

        {
            Cti::Devices::Commands::DlcCommand::payload_t payload;

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
            std::vector<Cti::Devices::Commands::DlcCommand::point_data_t> points;

            Cti::Devices::Commands::DlcCommand::request_ptr r = hourlyRead.decode(execute_time, 0xCA, payload, description, points);

            //  make sure it's null, we're done with today
            BOOST_CHECK( ! r.get());

            BOOST_CHECK_EQUAL(points.size(), 14);

            BOOST_CHECK_EQUAL(points[ 0].time,   CtiTime(CtiDate(12, 8, 2010),  0));
            BOOST_CHECK_EQUAL(points[ 0].value,  321.0);
            BOOST_CHECK_EQUAL(points[ 0].type,   PulseAccumulatorPointType);
            BOOST_CHECK_EQUAL(points[ 0].offset, 20);

            BOOST_CHECK_EQUAL(points[ 1].time,   CtiTime(CtiDate(11, 8, 2010), 13));
            BOOST_CHECK_EQUAL(points[ 1].value,  7444.0);

            BOOST_CHECK_EQUAL(points[ 2].time,   CtiTime(CtiDate(11, 8, 2010), 12));
            BOOST_CHECK_EQUAL(points[ 2].value,  7436.0);

            BOOST_CHECK_EQUAL(points[ 3].time,   CtiTime(CtiDate(11, 8, 2010), 11));
            BOOST_CHECK_EQUAL(points[ 3].value,  7364.0);

            BOOST_CHECK_EQUAL(points[ 4].time,   CtiTime(CtiDate(11, 8, 2010), 10));
            BOOST_CHECK_EQUAL(points[ 4].value,  7364.0);

            BOOST_CHECK_EQUAL(points[ 5].time,   CtiTime(CtiDate(11, 8, 2010),  9));
            BOOST_CHECK_EQUAL(points[ 5].value,  7364.0);

            BOOST_CHECK_EQUAL(points[ 6].time,   CtiTime(CtiDate(11, 8, 2010),  8));
            BOOST_CHECK_EQUAL(points[ 6].value,  7364.0);

            BOOST_CHECK_EQUAL(points[ 7].time,   CtiTime(CtiDate(11, 8, 2010),  7));
            BOOST_CHECK_EQUAL(points[ 7].value,  7364.0);

            BOOST_CHECK_EQUAL(points[ 8].time,   CtiTime(CtiDate(11, 8, 2010),  6));
            BOOST_CHECK_EQUAL(points[ 8].value,  7364.0);

            BOOST_CHECK_EQUAL(points[ 9].time,   CtiTime(CtiDate(11, 8, 2010),  5));
            BOOST_CHECK_EQUAL(points[ 9].value,  7364.0);

            BOOST_CHECK_EQUAL(points[10].time,   CtiTime(CtiDate(11, 8, 2010),  4));
            BOOST_CHECK_EQUAL(points[10].value,  7364.0);

            BOOST_CHECK_EQUAL(points[11].time,   CtiTime(CtiDate(11, 8, 2010),  3));
            BOOST_CHECK_EQUAL(points[11].value,  7364.0);

            BOOST_CHECK_EQUAL(points[12].time,   CtiTime(CtiDate(11, 8, 2010),  2));
            BOOST_CHECK_EQUAL(points[12].value,  7364.0);

            BOOST_CHECK_EQUAL(points[13].time,   CtiTime(CtiDate(11, 8, 2010),  1));
            BOOST_CHECK_EQUAL(points[13].value,  7364.0);
        }
    }

    {
        const CtiDate start(11, 2, 2010);
        const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

        Cti::Devices::Commands::Mct410HourlyReadCommand hourlyRead(start, start, 1);

        {
            Cti::Devices::Commands::DlcCommand::request_ptr r = hourlyRead.execute(execute_time);

            //  make sure it's not null
            BOOST_CHECK(r.get());

            if( ! r.get() )
            {
                return;
            }

            BOOST_CHECK_EQUAL(r->function,  0x1CB);
            BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL(r->length(),  13);
            BOOST_CHECK_EQUAL(r->payload().size(), 0);
        }

        {
            Cti::Devices::Commands::DlcCommand::payload_t payload;

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
            std::vector<Cti::Devices::Commands::DlcCommand::point_data_t> points;

            Cti::Devices::Commands::DlcCommand::request_ptr r = hourlyRead.decode(execute_time, 0xCB, payload, description, points);

            //  make sure it's not null
            BOOST_CHECK(r.get());

            if( ! r.get() )
            {
                return;
            }

            BOOST_CHECK_EQUAL(r->function,  0x1CA);
            BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL(r->length(),  13);
            BOOST_CHECK_EQUAL(r->payload().size(), 0);

            BOOST_CHECK_EQUAL(points.size(), 12);

            BOOST_CHECK_EQUAL(points[ 0].time,  CtiTime(CtiDate(12, 2, 2010),  0));
            BOOST_CHECK_EQUAL(points[ 0].value, 7456.5);

            BOOST_CHECK_EQUAL(points[ 1].time,  CtiTime(CtiDate(11, 2, 2010), 23));
            BOOST_CHECK_EQUAL(points[ 1].value, 7456.4);

            BOOST_CHECK_EQUAL(points[ 2].time,  CtiTime(CtiDate(11, 2, 2010), 22));
            BOOST_CHECK_EQUAL(points[ 2].value, 7456.2);

            BOOST_CHECK_EQUAL(points[ 3].time,  CtiTime(CtiDate(11, 2, 2010), 21));
            BOOST_CHECK_EQUAL(points[ 3].value, 7455.9);

            BOOST_CHECK_EQUAL(points[ 4].time,  CtiTime(CtiDate(11, 2, 2010), 20));
            BOOST_CHECK_EQUAL(points[ 4].value, 7455.5);

            BOOST_CHECK_EQUAL(points[ 5].time,  CtiTime(CtiDate(11, 2, 2010), 19));
            BOOST_CHECK_EQUAL(points[ 5].value, 7455.0);

            BOOST_CHECK_EQUAL(points[ 6].time,  CtiTime(CtiDate(11, 2, 2010), 18));
            BOOST_CHECK_EQUAL(points[ 6].value, 7454.4);

            BOOST_CHECK_EQUAL(points[ 7].time,  CtiTime(CtiDate(11, 2, 2010), 17));
            BOOST_CHECK_EQUAL(points[ 7].value, 7453.7);

            BOOST_CHECK_EQUAL(points[ 8].time,  CtiTime(CtiDate(11, 2, 2010), 16));
            BOOST_CHECK_EQUAL(points[ 8].value, 7452.9);

            BOOST_CHECK_EQUAL(points[ 9].time,  CtiTime(CtiDate(11, 2, 2010), 15));
            BOOST_CHECK_EQUAL(points[ 9].value, 7452.0);

            BOOST_CHECK_EQUAL(points[10].time,  CtiTime(CtiDate(11, 2, 2010), 14));
            BOOST_CHECK_EQUAL(points[10].value, 7451.0);

            BOOST_CHECK_EQUAL(points[11].time,  CtiTime(CtiDate(11, 2, 2010), 13));
            BOOST_CHECK_EQUAL(points[11].value, 7449.9);
        }

        {
            Cti::Devices::Commands::DlcCommand::payload_t payload;

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
            std::vector<Cti::Devices::Commands::DlcCommand::point_data_t> points;

            Cti::Devices::Commands::DlcCommand::request_ptr r = hourlyRead.decode(execute_time, 0xCA, payload, description, points);

            //  make sure it's null, we're done with today
            BOOST_CHECK( ! r.get());

            BOOST_CHECK_EQUAL(points.size(), 13);

            BOOST_CHECK_EQUAL(points[ 0].time,   CtiTime(CtiDate(12, 2, 2010),  0));
            BOOST_CHECK_EQUAL(points[ 0].value,  321.0);
            BOOST_CHECK_EQUAL(points[ 0].type,   PulseAccumulatorPointType);
            BOOST_CHECK_EQUAL(points[ 0].offset, 20);

            BOOST_CHECK_EQUAL(points[ 1].time,   CtiTime(CtiDate(11, 2, 2010), 12));
            BOOST_CHECK_EQUAL(points[ 1].value,  7443.9);

            BOOST_CHECK_EQUAL(points[ 2].time,   CtiTime(CtiDate(11, 2, 2010), 11));
            BOOST_CHECK_EQUAL(points[ 2].value,  7435.9);

            BOOST_CHECK_EQUAL(points[ 3].time,   CtiTime(CtiDate(11, 2, 2010), 10));
            BOOST_CHECK_EQUAL(points[ 3].value,  7363.9);

            BOOST_CHECK_EQUAL(points[ 4].time,   CtiTime(CtiDate(11, 2, 2010),  9));
            BOOST_CHECK_EQUAL(points[ 4].value,  7363.9);

            BOOST_CHECK_EQUAL(points[ 5].time,   CtiTime(CtiDate(11, 2, 2010),  8));
            BOOST_CHECK_EQUAL(points[ 5].value,  7363.9);

            BOOST_CHECK_EQUAL(points[ 6].time,   CtiTime(CtiDate(11, 2, 2010),  7));
            BOOST_CHECK_EQUAL(points[ 6].value,  7363.9);

            BOOST_CHECK_EQUAL(points[ 7].time,   CtiTime(CtiDate(11, 2, 2010),  6));
            BOOST_CHECK_EQUAL(points[ 7].value,  7363.9);

            BOOST_CHECK_EQUAL(points[ 8].time,   CtiTime(CtiDate(11, 2, 2010),  5));
            BOOST_CHECK_EQUAL(points[ 8].value,  7363.9);

            BOOST_CHECK_EQUAL(points[ 9].time,   CtiTime(CtiDate(11, 2, 2010),  4));
            BOOST_CHECK_EQUAL(points[ 9].value,  7363.9);

            BOOST_CHECK_EQUAL(points[10].time,   CtiTime(CtiDate(11, 2, 2010),  3));
            BOOST_CHECK_EQUAL(points[10].value,  7363.9);

            BOOST_CHECK_EQUAL(points[11].time,   CtiTime(CtiDate(11, 2, 2010),  2));
            BOOST_CHECK_EQUAL(points[11].value,  7363.9);

            BOOST_CHECK_EQUAL(points[12].time,   CtiTime(CtiDate(11, 2, 2010),  1));
            BOOST_CHECK_EQUAL(points[12].value,  7363.9);
        }
    }
}
