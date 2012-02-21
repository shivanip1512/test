#include <boost/test/unit_test.hpp>

#include "cmd_mct420_LcdConfiguration.h"

#include "ctidate.h"

using Cti::Devices::Commands::DlcCommand;
using std::string;

BOOST_AUTO_TEST_SUITE( test_cmd_mct420_LcdConfiguration )

const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

BOOST_AUTO_TEST_CASE(test_empty_write)
{
    const std::vector<unsigned char> metrics;

    Cti::Devices::Commands::Mct420LcdConfigurationCommand lcdConfiguration(metrics, false);

    {
        DlcCommand::request_ptr r = lcdConfiguration.execute(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F6);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(r->length(),  0);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf6, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F6);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),  13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf6, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F7);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),  13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf7, payload, description, points);

        //  make sure it's null
        BOOST_CHECK( ! r.get());
    }
}

BOOST_AUTO_TEST_CASE(test_one_metric)
{
    const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

    {
        const std::vector<unsigned char> metrics(1, 17);

        Cti::Devices::Commands::Mct420LcdConfigurationCommand lcdConfiguration(metrics, false);

        {
            DlcCommand::request_ptr r = lcdConfiguration.execute(execute_time);

            //  make sure it's not null
            BOOST_CHECK(r.get());

            BOOST_CHECK_EQUAL(r->function,  0x1F6);
            BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Write);
            BOOST_CHECK_EQUAL(r->length(),  1);
            BOOST_CHECK_EQUAL(r->payload().size(), 1);
            BOOST_CHECK_EQUAL(r->payload()[0], 17);
        }

        {
            DlcCommand::payload_t payload;

            string description;
            std::vector<DlcCommand::point_data> points;

            DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf6, payload, description, points);

            //  make sure it's not null
            BOOST_CHECK(r.get());

            BOOST_CHECK_EQUAL(r->function,  0x1F6);
            BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL(r->length(),  13);
            BOOST_CHECK_EQUAL(r->payload().size(), 0);
        }

        {
            DlcCommand::payload_t payload;

            string description;
            std::vector<DlcCommand::point_data> points;

            DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf6, payload, description, points);

            //  make sure it's not null
            BOOST_CHECK(r.get());

            BOOST_CHECK_EQUAL(r->function,  0x1F7);
            BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL(r->length(),  13);
            BOOST_CHECK_EQUAL(r->payload().size(), 0);
        }

        {
            DlcCommand::payload_t payload;

            string description;
            std::vector<DlcCommand::point_data> points;

            DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf7, payload, description, points);

            //  make sure it's not null
            BOOST_CHECK( ! r.get());
        }
    }

    {
        const std::vector<unsigned char> metrics(1, 1);

        Cti::Devices::Commands::Mct420LcdConfigurationCommand lcdConfiguration(metrics, false);

        {
            DlcCommand::request_ptr r = lcdConfiguration.execute(execute_time);

            //  make sure it's not null
            BOOST_CHECK(r.get());

            BOOST_CHECK_EQUAL(r->function,  0x1F6);
            BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Write);
            BOOST_CHECK_EQUAL(r->length(),  1);
            BOOST_CHECK_EQUAL(r->payload().size(), 1);
            BOOST_CHECK_EQUAL(r->payload()[0], 1);
        }

        {
            DlcCommand::payload_t payload;

            string description;
            std::vector<DlcCommand::point_data> points;

            DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf6, payload, description, points);

            //  make sure it's not null
            BOOST_CHECK(r.get());

            BOOST_CHECK_EQUAL(r->function,  0x1F6);
            BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL(r->length(),  13);
            BOOST_CHECK_EQUAL(r->payload().size(), 0);
        }

        {
            DlcCommand::payload_t payload;

            string description;
            std::vector<DlcCommand::point_data> points;

            DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf6, payload, description, points);

            //  make sure it's not null
            BOOST_CHECK(r.get());

            BOOST_CHECK_EQUAL(r->function,  0x1F7);
            BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL(r->length(),  13);
            BOOST_CHECK_EQUAL(r->payload().size(), 0);
        }

        {
            DlcCommand::payload_t payload;

            string description;
            std::vector<DlcCommand::point_data> points;

            DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf7, payload, description, points);

            //  make sure it's null
            BOOST_CHECK( ! r.get());
        }
    }
}

BOOST_AUTO_TEST_CASE(test_two_metrics)
{
    const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

    std::vector<unsigned char> metrics;

    metrics.push_back(2);
    metrics.push_back(17);

    Cti::Devices::Commands::Mct420LcdConfigurationCommand lcdConfiguration(metrics, false);

    {
        DlcCommand::request_ptr r = lcdConfiguration.execute(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F6);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(r->length(),  2);
        BOOST_CHECK_EQUAL(r->payload().size(), 2);
        BOOST_CHECK_EQUAL(r->payload()[0], 2);
        BOOST_CHECK_EQUAL(r->payload()[1], 17);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf6, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F6);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),  13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf6, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F7);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),  13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf7, payload, description, points);

        //  make sure it's null
        BOOST_CHECK( ! r.get());
    }
}

BOOST_AUTO_TEST_CASE(test_twelve_metrics)
{
    std::vector<unsigned char> metrics;

    metrics.push_back(1);
    metrics.push_back(1);
    metrics.push_back(2);
    metrics.push_back(3);
    metrics.push_back(5);
    metrics.push_back(8);
    metrics.push_back(13);
    metrics.push_back(21);
    metrics.push_back(34);
    metrics.push_back(55);
    metrics.push_back(89);
    metrics.push_back(144);

    Cti::Devices::Commands::Mct420LcdConfigurationCommand lcdConfiguration(metrics, false);

    {
        DlcCommand::request_ptr r = lcdConfiguration.execute(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F6);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(r->length(),  12);
        BOOST_CHECK_EQUAL(r->payload().size(), 12);
        BOOST_CHECK_EQUAL(r->payload() [0],   1);
        BOOST_CHECK_EQUAL(r->payload() [1],   1);
        BOOST_CHECK_EQUAL(r->payload() [2],   2);
        BOOST_CHECK_EQUAL(r->payload() [3],   3);
        BOOST_CHECK_EQUAL(r->payload() [4],   5);
        BOOST_CHECK_EQUAL(r->payload() [5],   8);
        BOOST_CHECK_EQUAL(r->payload() [6],  13);
        BOOST_CHECK_EQUAL(r->payload() [7],  21);
        BOOST_CHECK_EQUAL(r->payload() [8],  34);
        BOOST_CHECK_EQUAL(r->payload() [9],  55);
        BOOST_CHECK_EQUAL(r->payload()[10],  89);
        BOOST_CHECK_EQUAL(r->payload()[11], 144);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf6, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F6);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),  13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf6, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F7);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),  13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf7, payload, description, points);

        //  make sure it's null
        BOOST_CHECK( ! r.get());
    }
}

BOOST_AUTO_TEST_CASE(test_thirteen_metrics)
{
    std::vector<unsigned char> metrics;

    metrics.push_back(1);
    metrics.push_back(1);
    metrics.push_back(2);
    metrics.push_back(3);
    metrics.push_back(5);
    metrics.push_back(8);
    metrics.push_back(13);
    metrics.push_back(21);
    metrics.push_back(34);
    metrics.push_back(55);
    metrics.push_back(89);
    metrics.push_back(144);
    metrics.push_back(233);

    Cti::Devices::Commands::Mct420LcdConfigurationCommand lcdConfiguration(metrics, false);

    {
        DlcCommand::request_ptr r = lcdConfiguration.execute(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F6);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(r->length(),  13);
        BOOST_CHECK_EQUAL(r->payload().size(), 13);
        BOOST_CHECK_EQUAL(r->payload() [0],   1);
        BOOST_CHECK_EQUAL(r->payload() [1],   1);
        BOOST_CHECK_EQUAL(r->payload() [2],   2);
        BOOST_CHECK_EQUAL(r->payload() [3],   3);
        BOOST_CHECK_EQUAL(r->payload() [4],   5);
        BOOST_CHECK_EQUAL(r->payload() [5],   8);
        BOOST_CHECK_EQUAL(r->payload() [6],  13);
        BOOST_CHECK_EQUAL(r->payload() [7],  21);
        BOOST_CHECK_EQUAL(r->payload() [8],  34);
        BOOST_CHECK_EQUAL(r->payload() [9],  55);
        BOOST_CHECK_EQUAL(r->payload()[10],  89);
        BOOST_CHECK_EQUAL(r->payload()[11], 144);
        BOOST_CHECK_EQUAL(r->payload()[12], 233);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf6, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F7);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(r->length(),  0);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf7, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F6);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),  13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf6, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F7);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),  13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf7, payload, description, points);

        //  make sure it's null
        BOOST_CHECK( ! r.get());
    }
}

BOOST_AUTO_TEST_CASE(test_fourteen_metrics)
{
    std::vector<unsigned char> metrics;

    metrics.push_back(1);
    metrics.push_back(1);
    metrics.push_back(2);
    metrics.push_back(3);
    metrics.push_back(5);
    metrics.push_back(8);
    metrics.push_back(13);
    metrics.push_back(21);
    metrics.push_back(34);
    metrics.push_back(55);
    metrics.push_back(89);
    metrics.push_back(144);
    metrics.push_back(233);
    metrics.push_back(212);

    Cti::Devices::Commands::Mct420LcdConfigurationCommand lcdConfiguration(metrics, false);

    {
        DlcCommand::request_ptr r = lcdConfiguration.execute(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F6);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(r->length(),  13);
        BOOST_CHECK_EQUAL(r->payload().size(), 13);
        BOOST_CHECK_EQUAL(r->payload() [0],   1);
        BOOST_CHECK_EQUAL(r->payload() [1],   1);
        BOOST_CHECK_EQUAL(r->payload() [2],   2);
        BOOST_CHECK_EQUAL(r->payload() [3],   3);
        BOOST_CHECK_EQUAL(r->payload() [4],   5);
        BOOST_CHECK_EQUAL(r->payload() [5],   8);
        BOOST_CHECK_EQUAL(r->payload() [6],  13);
        BOOST_CHECK_EQUAL(r->payload() [7],  21);
        BOOST_CHECK_EQUAL(r->payload() [8],  34);
        BOOST_CHECK_EQUAL(r->payload() [9],  55);
        BOOST_CHECK_EQUAL(r->payload()[10],  89);
        BOOST_CHECK_EQUAL(r->payload()[11], 144);
        BOOST_CHECK_EQUAL(r->payload()[12], 233);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf6, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F7);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(r->length(),  1);
        BOOST_CHECK_EQUAL(r->payload().size(), 1);
        BOOST_CHECK_EQUAL(r->payload() [0], 212);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf7, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F6);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),  13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf6, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F7);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),  13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf7, payload, description, points);

        //  make sure it's null
        BOOST_CHECK( ! r.get());
    }
}

BOOST_AUTO_TEST_CASE(test_twenty_six_metrics)
{
    std::vector<unsigned char> metrics;

    metrics.push_back(1);
    metrics.push_back(1);
    metrics.push_back(2);
    metrics.push_back(3);
    metrics.push_back(5);
    metrics.push_back(8);
    metrics.push_back(13);
    metrics.push_back(21);
    metrics.push_back(34);
    metrics.push_back(55);
    metrics.push_back(89);
    metrics.push_back(144);
    metrics.push_back(233);
    metrics.push_back(232);
    metrics.push_back(231);
    metrics.push_back(230);
    metrics.push_back(229);
    metrics.push_back(228);
    metrics.push_back(227);
    metrics.push_back(226);
    metrics.push_back(225);
    metrics.push_back(224);
    metrics.push_back(223);
    metrics.push_back(222);
    metrics.push_back(221);
    metrics.push_back(220);

    Cti::Devices::Commands::Mct420LcdConfigurationCommand lcdConfiguration(metrics, false);

    {
        DlcCommand::request_ptr r = lcdConfiguration.execute(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F6);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(r->length(),  13);
        BOOST_CHECK_EQUAL(r->payload().size(), 13);
        BOOST_CHECK_EQUAL(r->payload() [0],   1);
        BOOST_CHECK_EQUAL(r->payload() [1],   1);
        BOOST_CHECK_EQUAL(r->payload() [2],   2);
        BOOST_CHECK_EQUAL(r->payload() [3],   3);
        BOOST_CHECK_EQUAL(r->payload() [4],   5);
        BOOST_CHECK_EQUAL(r->payload() [5],   8);
        BOOST_CHECK_EQUAL(r->payload() [6],  13);
        BOOST_CHECK_EQUAL(r->payload() [7],  21);
        BOOST_CHECK_EQUAL(r->payload() [8],  34);
        BOOST_CHECK_EQUAL(r->payload() [9],  55);
        BOOST_CHECK_EQUAL(r->payload()[10],  89);
        BOOST_CHECK_EQUAL(r->payload()[11], 144);
        BOOST_CHECK_EQUAL(r->payload()[12], 233);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf6, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F7);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(r->length(),  13);
        BOOST_CHECK_EQUAL(r->payload().size(), 13);
        BOOST_CHECK_EQUAL(r->payload() [0], 232);
        BOOST_CHECK_EQUAL(r->payload() [1], 231);
        BOOST_CHECK_EQUAL(r->payload() [2], 230);
        BOOST_CHECK_EQUAL(r->payload() [3], 229);
        BOOST_CHECK_EQUAL(r->payload() [4], 228);
        BOOST_CHECK_EQUAL(r->payload() [5], 227);
        BOOST_CHECK_EQUAL(r->payload() [6], 226);
        BOOST_CHECK_EQUAL(r->payload() [7], 225);
        BOOST_CHECK_EQUAL(r->payload() [8], 224);
        BOOST_CHECK_EQUAL(r->payload() [9], 223);
        BOOST_CHECK_EQUAL(r->payload()[10], 222);
        BOOST_CHECK_EQUAL(r->payload()[11], 221);
        BOOST_CHECK_EQUAL(r->payload()[12], 220);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf7, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F6);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),  13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf6, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F7);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),  13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf7, payload, description, points);

        //  make sure it's null
        BOOST_CHECK( ! r.get());
    }
}

BOOST_AUTO_TEST_CASE(test_too_many_metrics)
{
    const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

    const std::vector<unsigned char> metrics(27, 1);

    Cti::Devices::Commands::Mct420LcdConfigurationCommand lcdConfiguration(metrics, false);

    try
    {
        lcdConfiguration.execute(execute_time);

        BOOST_FAIL("Mct420LcdConfigurationCommand::execute() did not throw");
    }
    catch( DlcCommand::CommandException &ex )
    {
        BOOST_CHECK_EQUAL(ex.error_code,        BADPARAM);
        BOOST_CHECK_EQUAL(ex.error_description, "Invalid number of display metrics (27)");
    }
}

BOOST_AUTO_TEST_CASE(test_reads_only)
{
    const CtiTime execute_time(CtiDate(17, 2, 2010), 10);

    const std::vector<unsigned char> metrics(2, 2);

    Cti::Devices::Commands::Mct420LcdConfigurationCommand lcdConfiguration(metrics, true);

    {
        DlcCommand::request_ptr r = lcdConfiguration.execute(execute_time);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F6);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),  13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf6, payload, description, points);

        //  make sure it's not null
        BOOST_CHECK(r.get());

        BOOST_CHECK_EQUAL(r->function,  0x1F7);
        BOOST_CHECK_EQUAL(r->io(),      Cti::Protocols::EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(r->length(),  13);
        BOOST_CHECK_EQUAL(r->payload().size(), 0);
    }

    {
        DlcCommand::payload_t payload;

        string description;
        std::vector<DlcCommand::point_data> points;

        DlcCommand::request_ptr r = lcdConfiguration.decode(execute_time, 0xf7, payload, description, points);

        //  make sure it's null
        BOOST_CHECK( ! r.get());
    }
}

BOOST_AUTO_TEST_SUITE_END()
