#include <boost/test/auto_unit_test.hpp>

#include "prot_dnp.h"
#include "utility.h"

#include "boost_test_helpers.h"

using Cti::Test::byte_str;
using namespace Cti::Protocols;
typedef Interface::pointlist_t  pointlist_t;
typedef Interface::stringlist_t stringlist_t;

BOOST_AUTO_TEST_SUITE( test_prot_dnp )

BOOST_AUTO_TEST_CASE(test_prot_dnp_restart_bit)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(4, 3);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_Loopback);

    CtiXfer xfer;

    dnp.setConfigData( 2, DNP::TimeOffset::Utc, false, false, false, false, false, false );

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 08 C4 04 00 03 00 B4 B8 "
                "C0 C1 01 23 0B");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }

    {
        {
            const byte_str response(
                    "05 64 0A 44 03 00 04 00 7C AE");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(7, xfer.getInCountExpected());
    }

    {
        {
            const byte_str response(
                    "C1 C1 81 90 04 0D 14");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 0E C4 04 00 03 00 6D D3 "
                "C0 C2 02 50 01 00 07 07 00 08 "
                "65");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }

    {
        {
            const byte_str response(
                    "05 64 0A 44 03 00 04 00 7C AE");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(7, xfer.getInCountExpected());
    }

    {
        {
            const byte_str response(
                    "C2 C2 81 10 00 11 b9");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_CHECK_EQUAL(2, point_list.size());

        {
            CtiPointDataMsg *pd = point_list[0];

            BOOST_CHECK_EQUAL(pd->getValue(), 1);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2001);
        }

        {
            CtiPointDataMsg *pd = point_list[1];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2001);
        }

        auto string_list = dnp.getInboundStrings();

        BOOST_REQUIRE_EQUAL(4, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Loopback successful");
        BOOST_CHECK_EQUAL(string_list[1],
            "Internal indications:\n"
            "Time synchronization needed\n"
            "Device restart\n"
            "Parameter error\n");
        BOOST_CHECK_EQUAL(string_list[2],
            "Attempting to clear Device Restart bit");
        BOOST_CHECK_EQUAL(string_list[3],
            "Internal indications:\n"
            "Time synchronization needed\n");

        delete_container(point_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_restart_bit_with_unsolicited_enable)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(4, 3);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_Loopback);

    CtiXfer xfer;

    dnp.setConfigData( 2, DNP::TimeOffset::Utc, false, false, true, false, false, false );

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 08 C4 04 00 03 00 B4 B8 "
                "C0 C1 01 23 0B");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }

    {
        {
            const byte_str response(
                    "05 64 0A 44 03 00 04 00 7C AE");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(7, xfer.getInCountExpected());
    }

    {
        {
            const byte_str response(
                    "C1 C1 81 90 04 0D 14");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 0E C4 04 00 03 00 6D D3 "
                "C0 C2 02 50 01 00 07 07 00 08 "
                "65");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }

    {
        {
            const byte_str response(
                    "05 64 0A 44 03 00 04 00 7C AE");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(7, xfer.getInCountExpected());
    }

    {
        {
            const byte_str response(
                    "C2 C2 81 10 00 11 b9");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
            "05 64 0B C4 04 00 03 00 E4 2B "
            "C0 C3 14 3C 02 06 BA 3C");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }

    {
        {
            const byte_str response(
                "05 64 0A 44 03 00 04 00 7C AE");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(7, xfer.getInCountExpected());
    }

    {
        {
            const byte_str response(
                "C3 C3 81 10 00 FF 58");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_CHECK_EQUAL(3, point_list.size());

        {
            CtiPointDataMsg *pd = point_list[0];

            BOOST_CHECK_EQUAL(pd->getValue(), 1);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2001);
        }

        {
            CtiPointDataMsg *pd = point_list[1];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2001);
        }

        {
            CtiPointDataMsg *pd = point_list[2];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2001);
        }

        auto string_list = dnp.getInboundStrings();

        BOOST_REQUIRE_EQUAL(6, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Loopback successful");
        BOOST_CHECK_EQUAL(string_list[1],
            "Internal indications:\n"
            "Time synchronization needed\n"
            "Device restart\n"
            "Parameter error\n");
        BOOST_CHECK_EQUAL(string_list[2],
            "Attempting to clear Device Restart bit");
        BOOST_CHECK_EQUAL(string_list[3],
            "Internal indications:\n"
            "Time synchronization needed\n");
        BOOST_CHECK_EQUAL(string_list[4],
            "Unsolicited reporting enabled");
        BOOST_CHECK_EQUAL(string_list[5],
            "Internal indications:\n"
            "Time synchronization needed\n");

        delete_container(point_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_with_time)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_Class1230Read_WithTime);

    dnp.setConfigData( 2, DNP::TimeOffset::Utc, false, false, false, false, false, false );

    CtiXfer xfer;

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str request(
                "05 64 17 c4 d2 04 01 00 85 40 "
                "c0 c1 01 32 01 06 3c 02 06 3c "
                "03 06 3c 04 06 3c fe e0 01 06 "
                "75 e1");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());
        const std::vector<int> expected(request.begin(), request.end());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "05 64 31 44 01 00 d2 04 61 7c");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(50, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "c0 ca 81 0f 00 32 01 07 01 b8 ee 7d 3e 46 01 1e 72 34 "
                    "01 18 01 00 03 00 3f 01 00 00 01 02 18 01 00 01 e0 a6 "
                    "00 14 01 18 01 00 00 00 13 00 00 00 e1 c4");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_REQUIRE_EQUAL(4, point_list.size());

        {
            CtiPointDataMsg *pd = point_list[0];

            BOOST_CHECK_EQUAL(pd->getValue(), 319);

            BOOST_CHECK_EQUAL(pd->getType(), AnalogPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 4);

            BOOST_CHECK_EQUAL(pd->getTags(), 0);
        }

        {
            CtiPointDataMsg *pd = point_list[1];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2);

            BOOST_CHECK_EQUAL(pd->getTags(), 0);
        }

        {
            CtiPointDataMsg *pd = point_list[2];

            BOOST_CHECK_EQUAL(pd->getValue(), 19);

            BOOST_CHECK_EQUAL(pd->getType(), PulseAccumulatorPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 1);

            BOOST_CHECK_EQUAL(pd->getTags(), 0);
        }

        {
            CtiPointDataMsg *pd = point_list[3];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2001);

            BOOST_CHECK_EQUAL(pd->getTags(), 0);
        }

        auto string_list = dnp.getInboundStrings();

        BOOST_CHECK_EQUAL(3, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Device time: 05/27/2014 11:22:59.000");
        BOOST_CHECK_EQUAL(string_list[1],
            "Internal indications:\n"
            "Broadcast message received\n"
            "Class 1 data available\n"
            "Class 2 data available\n"
            "Class 3 data available\n");
        BOOST_CHECK_EQUAL(string_list[2],
            "Point data report:\n"
            "AI:     1; AO:     0; DI:     1; DO:     0; Counters:     1; \n"
            "First/Last 5 points of each type returned:\n"
            "Analog inputs:\n"
            "[4:319]\n"
            "Binary inputs:\n"
            "[2:0]\n"
            "Counters:\n"
            "[1:19]\n");

        delete_container(point_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_with_time_no_ack_required)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_Class1230Read_WithTime);

    CtiXfer xfer;

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 17 c4 d2 04 01 00 85 40 "
                "c0 c1 01 32 01 06 3c 02 06 3c "
                "03 06 3c 04 06 3c fe e0 01 06 "
                "75 e1");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "05 64 27 44 01 00 d2 04 79 6f");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(40, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "c0 ca 81 0f 00 1e 01 18 01 00 "
                    "03 00 3f 01 00 00 40 be 01 02 "
                    "18 01 00 01 00 14 01 18 01 00 "
                    "00 00 13 00 95 4f 00 00 ff ff");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_REQUIRE_EQUAL(4, point_list.size());

        {
            CtiPointDataMsg *pd = point_list[0];

            BOOST_CHECK_EQUAL(pd->getValue(), 319);

            BOOST_CHECK_EQUAL(pd->getType(), AnalogPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 4);
        }

        {
            CtiPointDataMsg *pd = point_list[1];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2);
        }

        {
            CtiPointDataMsg *pd = point_list[2];

            BOOST_CHECK_EQUAL(pd->getValue(), 19);

            BOOST_CHECK_EQUAL(pd->getType(), PulseAccumulatorPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 1);
        }

        {
            CtiPointDataMsg *pd = point_list[3];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2001);
        }

        auto string_list = dnp.getInboundStrings();

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Internal indications:\n"
            "Broadcast message received\n"
            "Class 1 data available\n"
            "Class 2 data available\n"
            "Class 3 data available\n");
        BOOST_CHECK_EQUAL(string_list[1],
            "Point data report:\n"
            "AI:     1; AO:     0; DI:     1; DO:     0; Counters:     1; \n"
            "First/Last 5 points of each type returned:\n"
            "Analog inputs:\n"
            "[4:319]\n"
            "Binary inputs:\n"
            "[2:0]\n"
            "Counters:\n"
            "[1:19]\n");

        delete_container(point_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_with_time_ack_required)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_Class1230Read_WithTime);

    CtiXfer xfer;

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str request(
                "05 64 17 c4 d2 04 01 00 85 40 "
                "c0 c1 01 32 01 06 3c 02 06 3c "
                "03 06 3c 04 06 3c fe e0 01 06 "
                "75 e1");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());
        const std::vector<int> expected(request.begin(), request.end());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "05 64 27 44 01 00 d2 04 79 6f");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(40, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "c0 ea 81 0f 00 1e 01 18 01 00 "
                    "03 00 3f 01 00 00 9e b9 01 02 "
                    "18 01 00 01 00 14 01 18 01 00 "
                    "00 00 13 00 95 4f 00 00 ff ff");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 08 c4 d2 04 01 00 a6 7c "
                "c0 ca 00 42 e2");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_REQUIRE_EQUAL(4, point_list.size());

        {
            CtiPointDataMsg *pd = point_list[0];

            BOOST_CHECK_EQUAL(pd->getValue(), 319);

            BOOST_CHECK_EQUAL(pd->getType(), AnalogPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 4);
        }

        {
            CtiPointDataMsg *pd = point_list[1];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2);
        }

        {
            CtiPointDataMsg *pd = point_list[2];

            BOOST_CHECK_EQUAL(pd->getValue(), 19);

            BOOST_CHECK_EQUAL(pd->getType(), PulseAccumulatorPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 1);
        }

        {
            CtiPointDataMsg *pd = point_list[3];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2001);
        }

        auto string_list = dnp.getInboundStrings();

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Internal indications:\n"
            "Broadcast message received\n"
            "Class 1 data available\n"
            "Class 2 data available\n"
            "Class 3 data available\n");
        BOOST_CHECK_EQUAL(string_list[1],
            "Point data report:\n"
            "AI:     1; AO:     0; DI:     1; DO:     0; Counters:     1; \n"
            "First/Last 5 points of each type returned:\n"
            "Analog inputs:\n"
            "[4:319]\n"
            "Binary inputs:\n"
            "[2:0]\n"
            "Counters:\n"
            "[1:19]\n");

        delete_container(point_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_with_time_interrupting_unsolicited)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_Class1230Read_WithTime);

    CtiXfer xfer;

    //  Initial scan request
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 17 c4 d2 04 01 00 85 40 "
                "c0 c1 01 32 01 06 3c 02 06 3c "
                "03 06 3c 04 06 3c fe e0 01 06 "
                "75 e1");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  Grab the first header, but it's an unsolicited
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "05 64 27 44 01 00 d2 04 79 6f");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  body of the unsolicited
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(40, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "c0 f5 82 0f 00 1e 01 18 01 00 "
                    "04 00 3f 01 00 00 25 c7 01 02 "
                    "18 01 00 02 00 14 01 18 01 00 "
                    "10 00 13 00 20 d3 00 00 ff ff");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  Ack the unsolicited
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 08 c4 d2 04 01 00 a6 7c "
                "c0 d5 00 9f d5");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  Grab the header of the scan response
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "05 64 27 44 01 00 d2 04 79 6f");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  the body of the scan response
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(40, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "c0 ea 81 0f 00 1e 01 18 01 00 "
                    "03 00 3f 01 00 00 9e b9 01 02 "
                    "18 01 00 01 00 14 01 18 01 00 "
                    "00 00 13 00 95 4f 00 00 ff ff");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  send the ack to the scan response, and we're done
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 08 c4 d2 04 01 00 a6 7c "
                "c0 ca 00 42 e2");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_CHECK_EQUAL(7, point_list.size());

        {
            CtiPointDataMsg *pd = point_list[0];

            BOOST_CHECK_EQUAL(pd->getValue(), 319);

            BOOST_CHECK_EQUAL(pd->getType(), AnalogPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 5);
        }

        {
            CtiPointDataMsg *pd = point_list[1];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 3);
        }

        {
            CtiPointDataMsg *pd = point_list[2];

            BOOST_CHECK_EQUAL(pd->getValue(), 19);

            BOOST_CHECK_EQUAL(pd->getType(), PulseAccumulatorPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 17);
        }

        {
            CtiPointDataMsg *pd = point_list[3];

            BOOST_CHECK_EQUAL(pd->getValue(), 319);

            BOOST_CHECK_EQUAL(pd->getType(), AnalogPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 4);
        }

        {
            CtiPointDataMsg *pd = point_list[4];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2);
        }

        {
            CtiPointDataMsg *pd = point_list[5];

            BOOST_CHECK_EQUAL(pd->getValue(), 19);

            BOOST_CHECK_EQUAL(pd->getType(), PulseAccumulatorPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 1);
        }

        {
            CtiPointDataMsg *pd = point_list[6];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2001);
        }

        auto string_list = dnp.getInboundStrings();

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Internal indications:\n"
            "Broadcast message received\n"
            "Class 1 data available\n"
            "Class 2 data available\n"
            "Class 3 data available\n");
        BOOST_CHECK_EQUAL(string_list[1],
            "Point data report:\n"
            "AI:     2; AO:     0; DI:     2; DO:     0; Counters:     2; \n"
            "First/Last 5 points of each type returned:\n"
            "Analog inputs:\n"
            "[4:319, 5:319]\n"
            "Binary inputs:\n"
            "[2:0, 3:0]\n"
            "Counters:\n"
            "[1:19, 17:19]\n");

        delete_container(point_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_Class1230Read);

    CtiXfer xfer;

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 14 c4 d2 04 01 00 d5 d3 "
                "c0 c1 01 3c 02 06 3c 03 06 3c "
                "04 06 3c 01 06 7a 6f");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "05 64 27 44 01 00 d2 04 79 6f");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(40, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "c0 ca 81 0f 00 1e 01 18 01 00 "
                    "03 00 3f 01 00 00 40 be 01 02 "
                    "18 01 00 01 00 14 01 18 01 00 "
                    "00 00 13 00 95 4f 00 00 ff ff");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_REQUIRE_EQUAL(4, point_list.size());

        {
            CtiPointDataMsg *pd = point_list[0];

            BOOST_CHECK_EQUAL(pd->getValue(), 319);

            BOOST_CHECK_EQUAL(pd->getType(), AnalogPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 4);
        }

        {
            CtiPointDataMsg *pd = point_list[1];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2);
        }

        {
            CtiPointDataMsg *pd = point_list[2];

            BOOST_CHECK_EQUAL(pd->getValue(), 19);

            BOOST_CHECK_EQUAL(pd->getType(), PulseAccumulatorPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 1);
        }

        {
            CtiPointDataMsg *pd = point_list[3];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2001);
        }

        auto string_list = dnp.getInboundStrings();

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Internal indications:\n"
            "Broadcast message received\n"
            "Class 1 data available\n"
            "Class 2 data available\n"
            "Class 3 data available\n");
        BOOST_CHECK_EQUAL(string_list[1],
            "Point data report:\n"
            "AI:     1; AO:     0; DI:     1; DO:     0; Counters:     1; \n"
            "First/Last 5 points of each type returned:\n"
            "Analog inputs:\n"
            "[4:319]\n"
            "Binary inputs:\n"
            "[2:0]\n"
            "Counters:\n"
            "[1:19]\n");

        delete_container(point_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_no_ack_required)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_Class1230Read);

    CtiXfer xfer;

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 14 c4 d2 04 01 00 d5 d3 "
                "c0 c1 01 3c 02 06 3c 03 06 3c "
                "04 06 3c 01 06 7a 6f");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "05 64 27 44 01 00 d2 04 79 6f");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(40, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "c0 ca 81 0f 00 1e 01 18 01 00 "
                    "03 00 3f 01 00 00 40 be 01 02 "
                    "18 01 00 01 00 14 01 18 01 00 "
                    "00 00 13 00 95 4f 00 00 ff ff");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_REQUIRE_EQUAL(4, point_list.size());

        {
            CtiPointDataMsg *pd = point_list[0];

            BOOST_CHECK_EQUAL(pd->getValue(), 319);

            BOOST_CHECK_EQUAL(pd->getType(), AnalogPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 4);
        }

        {
            CtiPointDataMsg *pd = point_list[1];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2);
        }

        {
            CtiPointDataMsg *pd = point_list[2];

            BOOST_CHECK_EQUAL(pd->getValue(), 19);

            BOOST_CHECK_EQUAL(pd->getType(), PulseAccumulatorPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 1);
        }

        {
            CtiPointDataMsg *pd = point_list[3];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2001);
        }

        auto string_list = dnp.getInboundStrings();

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Internal indications:\n"
            "Broadcast message received\n"
            "Class 1 data available\n"
            "Class 2 data available\n"
            "Class 3 data available\n");
        BOOST_CHECK_EQUAL(string_list[1],
            "Point data report:\n"
            "AI:     1; AO:     0; DI:     1; DO:     0; Counters:     1; \n"
            "First/Last 5 points of each type returned:\n"
            "Analog inputs:\n"
            "[4:319]\n"
            "Binary inputs:\n"
            "[2:0]\n"
            "Counters:\n"
            "[1:19]\n");

        delete_container(point_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_ack_required)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_Class1230Read);

    CtiXfer xfer;

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 14 c4 d2 04 01 00 d5 d3 "
                "c0 c1 01 3c 02 06 3c 03 06 3c "
                "04 06 3c 01 06 7a 6f");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "05 64 27 44 01 00 d2 04 79 6f");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(40, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "c0 ea 81 0f 00 1e 01 18 01 00 "
                    "03 00 3f 01 00 00 9e b9 01 02 "
                    "18 01 00 01 00 14 01 18 01 00 "
                    "00 00 13 00 95 4f 00 00 ff ff");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 08 c4 d2 04 01 00 a6 7c "
                "c0 ca 00 42 e2");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_REQUIRE_EQUAL(4, point_list.size());

        {
            CtiPointDataMsg *pd = point_list[0];

            BOOST_CHECK_EQUAL(pd->getValue(), 319);

            BOOST_CHECK_EQUAL(pd->getType(), AnalogPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 4);
        }

        {
            CtiPointDataMsg *pd = point_list[1];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2);
        }

        {
            CtiPointDataMsg *pd = point_list[2];

            BOOST_CHECK_EQUAL(pd->getValue(), 19);

            BOOST_CHECK_EQUAL(pd->getType(), PulseAccumulatorPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 1);
        }

        {
            CtiPointDataMsg *pd = point_list[3];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2001);
        }

        auto string_list = dnp.getInboundStrings();

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Internal indications:\n"
            "Broadcast message received\n"
            "Class 1 data available\n"
            "Class 2 data available\n"
            "Class 3 data available\n");
        BOOST_CHECK_EQUAL(string_list[1],
            "Point data report:\n"
            "AI:     1; AO:     0; DI:     1; DO:     0; Counters:     1; \n"
            "First/Last 5 points of each type returned:\n"
            "Analog inputs:\n"
            "[4:319]\n"
            "Binary inputs:\n"
            "[2:0]\n"
            "Counters:\n"
            "[1:19]\n");

        delete_container(point_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_interrupting_unsolicited)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_Class1230Read);

    CtiXfer xfer;

    //  Initial scan request
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 14 c4 d2 04 01 00 d5 d3 "
                "c0 c1 01 3c 02 06 3c 03 06 3c "
                "04 06 3c 01 06 7a 6f");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  Grab the first header, but it's an unsolicited
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "05 64 27 44 01 00 d2 04 79 6f");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  body of the unsolicited
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(40, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "c0 f5 82 0f 00 1e 01 18 01 00 "
                    "04 00 3f 01 00 00 25 c7 01 02 "
                    "18 01 00 02 00 14 01 18 01 00 "
                    "10 00 13 00 20 d3 00 00 ff ff");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  Ack the unsolicited
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 08 c4 d2 04 01 00 a6 7c "
                "c0 d5 00 9f d5");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  Grab the header of the scan response
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "05 64 27 44 01 00 d2 04 79 6f");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  the body of the scan response
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(40, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "c0 ea 81 0f 00 1e 01 18 01 00 "
                    "03 00 3f 01 00 00 9e b9 01 02 "
                    "18 01 00 01 00 14 01 18 01 00 "
                    "00 00 13 00 95 4f 00 00 ff ff");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  send the ack to the scan response, and we're done
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 08 c4 d2 04 01 00 a6 7c "
                "c0 ca 00 42 e2");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_CHECK_EQUAL(7, point_list.size());

        {
            CtiPointDataMsg *pd = point_list[0];

            BOOST_CHECK_EQUAL(pd->getValue(), 319);

            BOOST_CHECK_EQUAL(pd->getType(), AnalogPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 5);
        }

        {
            CtiPointDataMsg *pd = point_list[1];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 3);
        }

        {
            CtiPointDataMsg *pd = point_list[2];

            BOOST_CHECK_EQUAL(pd->getValue(), 19);

            BOOST_CHECK_EQUAL(pd->getType(), PulseAccumulatorPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 17);
        }

        {
            CtiPointDataMsg *pd = point_list[3];

            BOOST_CHECK_EQUAL(pd->getValue(), 319);

            BOOST_CHECK_EQUAL(pd->getType(), AnalogPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 4);
        }

        {
            CtiPointDataMsg *pd = point_list[4];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2);
        }

        {
            CtiPointDataMsg *pd = point_list[5];

            BOOST_CHECK_EQUAL(pd->getValue(), 19);

            BOOST_CHECK_EQUAL(pd->getType(), PulseAccumulatorPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 1);
        }

        {
            CtiPointDataMsg *pd = point_list[6];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2001);
        }

        auto string_list = dnp.getInboundStrings();

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Internal indications:\n"
            "Broadcast message received\n"
            "Class 1 data available\n"
            "Class 2 data available\n"
            "Class 3 data available\n");
        BOOST_CHECK_EQUAL(string_list[1],
            "Point data report:\n"
            "AI:     2; AO:     0; DI:     2; DO:     0; Counters:     2; \n"
            "First/Last 5 points of each type returned:\n"
            "Analog inputs:\n"
            "[4:319, 5:319]\n"
            "Binary inputs:\n"
            "[2:0, 3:0]\n"
            "Counters:\n"
            "[1:19, 17:19]\n");

        delete_container(point_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_reversed_start_stop)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_Class1230Read);

    CtiXfer xfer;

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 14 c4 d2 04 01 00 d5 d3 "
                "c0 c1 01 3c 02 06 3c 03 06 3c "
                "04 06 3c 01 06 7a 6f");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "05 64 27 44 01 00 d2 04 79 6f");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(40, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "c0 ca 81 0f 00 1e 01 00 ff 00 03 00 3f 01 00 00 2b ac "
                    "01 02 18 01 00 01 00 14 01 18 01 00 00 00 13 00 95 4f "
                    "00 00 ff ff");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_REQUIRE_EQUAL(1, point_list.size());

        {
            CtiPointDataMsg *pd = point_list[0];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2001);
        }

        auto string_list = dnp.getInboundStrings();

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Internal indications:\n"
            "Broadcast message received\n"
            "Class 1 data available\n"
            "Class 2 data available\n"
            "Class 3 data available\n");
        BOOST_CHECK_EQUAL(string_list[1],
            "Point data report:\n"
            "AI:     0; AO:     0; DI:     0; DO:     0; Counters:     0; \n"
            "(No points returned)\n");

        delete_container(point_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_unsolicited)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_UnsolicitedInbound);

    CtiXfer xfer;

    //  Grab the unsolicited header
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "05 64 27 44 01 00 d2 04 79 6f");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  body of the unsolicited
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(40, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "c0 f5 82 0f 00 1e 01 18 01 00 "
                    "04 00 3f 01 00 00 25 c7 01 02 "
                    "18 01 00 02 00 14 01 18 01 00 "
                    "10 00 13 00 20 d3 00 00 ff ff");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  Ack the unsolicited
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 08 c4 d2 04 01 00 a6 7c "
                "c0 d5 00 9f d5");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_REQUIRE_EQUAL(4, point_list.size());

        {
            CtiPointDataMsg *pd = point_list[0];

            BOOST_CHECK_EQUAL(pd->getValue(), 319);

            BOOST_CHECK_EQUAL(pd->getType(), AnalogPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 5);
        }

        {
            CtiPointDataMsg *pd = point_list[1];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 3);
        }

        {
            CtiPointDataMsg *pd = point_list[2];

            BOOST_CHECK_EQUAL(pd->getValue(), 19);

            BOOST_CHECK_EQUAL(pd->getType(), PulseAccumulatorPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 17);
        }

        {
            CtiPointDataMsg *pd = point_list[3];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2001);
        }

        auto string_list = dnp.getInboundStrings();

        BOOST_CHECK_EQUAL(0, string_list.size());

        delete_container(point_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_needtime)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(4, 3);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_Loopback);

    CtiXfer xfer;

    // Enable DNP timesync
    dnp.setConfigData( 1, DNP::TimeOffset::Utc, true, false, false, false, false, false );

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 08 C4 04 00 03 00 B4 B8 "
                "C0 C1 01 23 0B");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());

        {
            const byte_str response(
                    "05 64 0A 44 03 00 04 00 7C AE");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(7, xfer.getInCountExpected());

        {
            // NEEDTIME and RESTART are enable in this response,
            // RESTART has priority however NEEDTIME is expected to be scheduled
            const byte_str response(
                    "C1 C1 81 90 04 0D 14");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 0E C4 04 00 03 00 6D D3 "
                "C0 C2 02 50 01 00 07 07 00 08 "
                "65");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());

    {
            const byte_str response(
                    "05 64 0A 44 03 00 04 00 7C AE");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(7, xfer.getInCountExpected());

        {
            // NEEDTIME is set again in this response
            const byte_str response(
                    "C2 C2 81 10 00 11 b9");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        // Override the current time to Tue, 27 May 2014 16:22:59 GMT
        Cti::Test::Override_CtiTime_Now override_ctiTime_now(1401207779);

        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        // Millis timestamp on 48-bits
        // 1401207779 * 1000 = 1401207779000 => 0x01463E7DEEB8 => 0xB8, 0xEE, 0x7D, 0x3E, 0x46, 0x01

        const byte_str expected(
                "05 64 12 c4 04 00 03 00 1E 7C "
                "C0 C3 02 32 01 07 01 B8 EE 7D 3E 46 01 57 3B");

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());

        {
            // Header block, expecting 10-bytes, (all user data without the CRC)
            // src address 0x0003
            // dst address 0x0004
            const byte_str response(
                    "05 64 0A 44 03 00 04 00 7C AE");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(7, xfer.getInCountExpected());

        {
            // this time leave internal indication to zero
            const byte_str response(
                    "C0 C3 81 00 00 DD E2");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());
    }

    {
        pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_CHECK_EQUAL(3, point_list.size());

        {
            CtiPointDataMsg *pd = point_list[0];

            BOOST_CHECK_EQUAL(pd->getValue(), 1);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2001);
        }

        {
            CtiPointDataMsg *pd = point_list[1];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2001);
        }

        {
            CtiPointDataMsg *pd = point_list[2];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 2001);
        }

        auto string_list = dnp.getInboundStrings();

        BOOST_CHECK_EQUAL(6, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Loopback successful");
        BOOST_CHECK_EQUAL(string_list[1],
            "Internal indications:\n"
            "Time synchronization needed\n"
            "Device restart\n"
            "Parameter error\n");
        BOOST_CHECK_EQUAL(string_list[2],
            "Attempting to clear Device Restart bit");
        BOOST_CHECK_EQUAL(string_list[3],
            "Internal indications:\n"
            "Time synchronization needed\n");
        BOOST_CHECK_EQUAL(string_list[4],
            "Time sync sent");
        BOOST_CHECK_EQUAL(string_list[5],
            ""); // no internal indication

        delete_container(point_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_control_inhibited_by_local_automation)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(502, 1000);
    dnp.setName("Test DNP device");

    DnpProtocol::output_point op;

    op.dout.control    = DNP::BinaryOutputControl::PulseOn;
    op.dout.trip_close = DNP::BinaryOutputControl::Close;
    op.dout.on_time = 0;
    op.dout.off_time = 0;
    op.dout.queue = false;
    op.dout.clear = false;
    op.dout.count = 1;

    op.control_offset = 0;
    op.type = DnpProtocol::DigitalOutputPointType;
    op.expiration = ~0;

    dnp.setCommand(DnpProtocol::Command_SetDigitalOut_Direct, op);

    CtiXfer xfer;

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 18 C4 F6 01 E8 03 36 79 "
                "C0 C1 05 0C 01 17 01 00 41 01 00 00 00 00 00 00 84 A9 "
                "00 00 00 FF FF");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "05 64 1A 44 E8 03 F6 01 20 BB");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
        BOOST_CHECK_EQUAL(25, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "DE C3 81 00 00 0C 01 17 01 00 41 01 00 00 00 00 09 D9 "
                    "00 00 00 00 0A CA 6C");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        auto string_list = dnp.getInboundStrings();

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Control result (10): Request not accepted because it was inhibited by a local automation process.");
        BOOST_CHECK_EQUAL(string_list[1],
            "");  //  no internal indications
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_control_not_supported)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(502, 1000);
    dnp.setName("Test DNP device");

    DnpProtocol::output_point op;

    op.dout.control    = DNP::BinaryOutputControl::PulseOn;
    op.dout.trip_close = DNP::BinaryOutputControl::Close;
    op.dout.on_time = 0;
    op.dout.off_time = 0;
    op.dout.queue = false;
    op.dout.clear = false;
    op.dout.count = 1;

    op.control_offset = 0;
    op.type = DnpProtocol::DigitalOutputPointType;
    op.expiration = ~0;

    dnp.setCommand(DnpProtocol::Command_SetDigitalOut_Direct, op);

    CtiXfer xfer;

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 18 C4 F6 01 E8 03 36 79 "
                "C0 C1 05 0C 01 17 01 00 41 01 00 00 00 00 00 00 84 A9 "
                "00 00 00 FF FF");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "05 64 1A 44 E8 03 F6 01 20 BB");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
        BOOST_CHECK_EQUAL(25, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "DE C3 81 00 00 0C 01 17 01 00 41 01 00 00 00 00 09 D9 "
                    "00 00 00 00 04 87 26");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        auto string_list = dnp.getInboundStrings();

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Control result (4): Request not accepted because a control operation is not supported for this point.");
        BOOST_CHECK_EQUAL(string_list[1],
            "");  //  no internal indications
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_control_sbo)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(502, 1000);
    dnp.setName("Test DNP device");

    DnpProtocol::output_point op;

    op.dout.control    = DNP::BinaryOutputControl::PulseOn;
    op.dout.trip_close = DNP::BinaryOutputControl::Close;
    op.dout.on_time = 0;
    op.dout.off_time = 0;
    op.dout.queue = false;
    op.dout.clear = false;
    op.dout.count = 1;

    op.control_offset = 0;
    op.type = DnpProtocol::DigitalOutputPointType;
    op.expiration = ~0;

    dnp.setCommand(DnpProtocol::Command_SetDigitalOut_SBO_Select, op);

    CtiXfer xfer;

    //  Select
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 18 C4 F6 01 E8 03 36 79 "
                "C0 C1 03 0C 01 17 01 00 41 01 00 00 00 00 00 00 A4 2F "
                "00 00 00 FF FF");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "05 64 1A 44 E8 03 F6 01 20 BB");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
        BOOST_CHECK_EQUAL(25, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "DE C3 81 00 00 0C 01 17 01 00 41 01 00 00 00 00 09 D9 "
                    "00 00 00 00 00 FF FF");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        auto string_list = dnp.getInboundStrings();

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Select successful, sending operate");
        BOOST_CHECK_EQUAL(string_list[1],
            "Control result (0): Request accepted, initiated, or queued.");
    }
    //  Operate
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 18 C4 F6 01 E8 03 36 79 "
                "C0 C2 04 0C 01 17 01 00 41 01 00 00 00 00 00 00 5C 25 "
                "00 00 00 FF FF");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "05 64 1A 44 E8 03 F6 01 20 BB");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
        BOOST_CHECK_EQUAL(25, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "DF C4 81 00 00 0C 01 17 01 00 41 01 00 00 00 00 B4 D3 "
                    "00 00 00 00 00 FF FF");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        auto string_list = dnp.getInboundStrings();

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Control result (0): Request accepted, initiated, or queued.");
        BOOST_CHECK_EQUAL(string_list[1],
            "");
    }
}

BOOST_AUTO_TEST_SUITE_END()
