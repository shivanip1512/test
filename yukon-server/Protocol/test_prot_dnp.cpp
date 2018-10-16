#include <boost/test/auto_unit_test.hpp>

#include "prot_dnp.h"
#include "utility.h"

#include "boost_test_helpers.h"

using Cti::Test::byte_str;
using namespace Cti::Protocols;
typedef Interface::pointlist_t  pointlist_t;
typedef Interface::stringlist_t stringlist_t;

BOOST_AUTO_TEST_SUITE( test_prot_dnp )

BOOST_AUTO_TEST_CASE(test_prot_dnp_loopback)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(4, 3);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_Loopback);

    CtiXfer xfer;

    dnp.setConfigData(2, DNP::TimeOffset::Utc, false, false, false, false, false, false);

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
            "05 64 05 D9 04 00 03 00 24 8A");

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
                "05 64 05 0B 03 00 04 00 7F 66");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(),
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());
    }

    pointlist_t point_list;
    
    dnp.getInboundPoints(point_list);

    BOOST_CHECK(point_list.empty());

    auto string_list = dnp.getInboundStrings();

    BOOST_REQUIRE_EQUAL(1, string_list.size());

    BOOST_CHECK_EQUAL(string_list[0],
        "Loopback successful");
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_loopback_timeout_retry)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(4, 3);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_Loopback);

    CtiXfer xfer;

    dnp.setConfigData(2, DNP::TimeOffset::Utc, false, false, false, false, false, false);

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
            "05 64 05 D9 04 00 03 00 24 8A");

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
        xfer.setInCountActual(0UL);

        BOOST_CHECK_EQUAL(ClientErrors::Abnormal, dnp.decode(xfer, ClientErrors::ReadTimeout));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
            "05 64 05 D9 04 00 03 00 24 8A");

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
                "05 64 05 0B 03 00 04 00 7F 66");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(),
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(ClientErrors::None, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());
    }

    pointlist_t point_list;
    
    dnp.getInboundPoints(point_list);

    BOOST_CHECK(point_list.empty());

    auto string_list = dnp.getInboundStrings();

    BOOST_REQUIRE_EQUAL(1, string_list.size());

    BOOST_CHECK_EQUAL(string_list[0],
        "Loopback successful");
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_restart_bit)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(4, 3);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_ReadInternalIndications);

    CtiXfer xfer;

    dnp.setConfigData( 2, DNP::TimeOffset::Utc, false, false, false, false, false, false );

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 08 C4 04 00 03 00 B4 B8 "
                "C0 C1 17 8C 0C");

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
                    "C1 C1 81 90 00 75 CD");

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

            BOOST_CHECK_EQUAL(pd->getId(), 9999);
        }

        {
            CtiPointDataMsg *pd = point_list[1];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 9999);
        }

        auto string_list = dnp.getInboundStrings();

        BOOST_REQUIRE_EQUAL(4, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Successfully read internal indications");
        BOOST_CHECK_EQUAL(string_list[1],
            "Internal indications:\n"
            "Time synchronization needed\n"
            "Device restart\n");
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
    dnp.setCommand(DnpProtocol::Command_ReadInternalIndications);

    CtiXfer xfer;

    dnp.setConfigData( 2, DNP::TimeOffset::Utc, false, false, true, false, false, false );

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 08 C4 04 00 03 00 B4 B8 "
                "C0 C1 17 8C 0C");

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
                    "C1 C1 81 90 00 75 CD");

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

            BOOST_CHECK_EQUAL(pd->getId(), 9999);
        }

        {
            CtiPointDataMsg *pd = point_list[1];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 9999);
        }

        {
            CtiPointDataMsg *pd = point_list[2];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 9999);
        }

        auto string_list = dnp.getInboundStrings();

        BOOST_REQUIRE_EQUAL(6, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Successfully read internal indications");
        BOOST_CHECK_EQUAL(string_list[1],
            "Internal indications:\n"
            "Time synchronization needed\n"
            "Device restart\n");
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

// the following tests are identical to the one at the top except they have a IIN error
//  bit set - some of the testing boilerplate has been removed and only relevant details
//  remain.
BOOST_AUTO_TEST_CASE(test_prot_dnp_restart_bit_generate_parameter_error)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(4, 3);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_ReadInternalIndications);

    CtiXfer xfer;

    dnp.setConfigData( 2, DNP::TimeOffset::Utc, false, false, false, false, false, false );

    dnp.generate(xfer);
    dnp.decode(xfer, ClientErrors::None);
    dnp.generate(xfer);

    {
        const byte_str response(
                "05 64 0A 44 03 00 04 00 7C AE");

        //  make sure we don't copy more than they expect
        std::copy(response.begin(), response.end(), 
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

        xfer.setInCountActual(response.size());
    }

    dnp.decode(xfer, ClientErrors::None);
    dnp.generate(xfer);

    {
        const byte_str response(
                "C1 C1 81 90 04 0D 14");

        //  make sure we don't copy more than they expect
        std::copy(response.begin(), response.end(), 
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

        xfer.setInCountActual(response.size());
    }

    BOOST_CHECK_EQUAL(ClientErrors::ParameterError, dnp.decode(xfer, ClientErrors::None)); 

    dnp.generate(xfer);
    dnp.decode(xfer, ClientErrors::None);
    dnp.generate(xfer);

    {
        const byte_str response(
                "05 64 0A 44 03 00 04 00 7C AE");

        //  make sure we don't copy more than they expect
        std::copy(response.begin(), response.end(), 
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

        xfer.setInCountActual(response.size());
    }

    dnp.decode(xfer, ClientErrors::None);
    dnp.generate(xfer);

    {
        const byte_str response(
                "C2 C2 81 10 00 11 b9");

        //  make sure we don't copy more than they expect
        std::copy(response.begin(), response.end(), 
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

        xfer.setInCountActual(response.size());
    }

    dnp.decode(xfer, ClientErrors::None);

    auto string_list = dnp.getInboundStrings();

    BOOST_REQUIRE_EQUAL(4, string_list.size());

    BOOST_CHECK_EQUAL(string_list[0],
        "Successfully read internal indications");
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
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_restart_bit_generate_invalid_function_code)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(4, 3);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_ReadInternalIndications);

    CtiXfer xfer;

    dnp.setConfigData( 2, DNP::TimeOffset::Utc, false, false, false, false, false, false );

    dnp.generate(xfer);
    dnp.decode(xfer, ClientErrors::None);
    dnp.generate(xfer);

    {
        const byte_str response(
                "05 64 0A 44 03 00 04 00 7C AE");

        //  make sure we don't copy more than they expect
        std::copy(response.begin(), response.end(), 
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

        xfer.setInCountActual(response.size());
    }

    dnp.decode(xfer, ClientErrors::None);
    dnp.generate(xfer);

    {
        const byte_str response(
                "C1 C1 81 90 01 2B FB");

        //  make sure we don't copy more than they expect
        std::copy(response.begin(), response.end(), 
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

        xfer.setInCountActual(response.size());
    }

    BOOST_CHECK_EQUAL(ClientErrors::FunctionCodeNotImplemented, dnp.decode(xfer, ClientErrors::None)); 

    dnp.generate(xfer);
    dnp.decode(xfer, ClientErrors::None);
    dnp.generate(xfer);

    {
        const byte_str response(
                "05 64 0A 44 03 00 04 00 7C AE");

        //  make sure we don't copy more than they expect
        std::copy(response.begin(), response.end(), 
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

        xfer.setInCountActual(response.size());
    }

    dnp.decode(xfer, ClientErrors::None);
    dnp.generate(xfer);

    {
        const byte_str response(
                "C2 C2 81 10 00 11 b9");

        //  make sure we don't copy more than they expect
        std::copy(response.begin(), response.end(), 
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

        xfer.setInCountActual(response.size());
    }

    dnp.decode(xfer, ClientErrors::None);

    auto string_list = dnp.getInboundStrings();

    BOOST_REQUIRE_EQUAL(4, string_list.size());

    BOOST_CHECK_EQUAL(string_list[0],
        "Successfully read internal indications");
    BOOST_CHECK_EQUAL(string_list[1],
        "Internal indications:\n"
        "Time synchronization needed\n"
        "Device restart\n"
        "Function code not implemented\n");
    BOOST_CHECK_EQUAL(string_list[2],
        "Attempting to clear Device Restart bit");
    BOOST_CHECK_EQUAL(string_list[3],
        "Internal indications:\n"
        "Time synchronization needed\n");
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_restart_bit_generate_unknown_object)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(4, 3);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_ReadInternalIndications);

    CtiXfer xfer;

    dnp.setConfigData( 2, DNP::TimeOffset::Utc, false, false, false, false, false, false );

    dnp.generate(xfer);
    dnp.decode(xfer, ClientErrors::None);
    dnp.generate(xfer);

    {
        const byte_str response(
                "05 64 0A 44 03 00 04 00 7C AE");

        //  make sure we don't copy more than they expect
        std::copy(response.begin(), response.end(), 
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

        xfer.setInCountActual(response.size());
    }

    dnp.decode(xfer, ClientErrors::None);
    dnp.generate(xfer);

    {
        const byte_str response(
                "C1 C1 81 90 02 C9 A1");

        //  make sure we don't copy more than they expect
        std::copy(response.begin(), response.end(), 
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

        xfer.setInCountActual(response.size());
    }

    BOOST_CHECK_EQUAL(ClientErrors::UnknownObject, dnp.decode(xfer, ClientErrors::None)); 

    dnp.generate(xfer);
    dnp.decode(xfer, ClientErrors::None);
    dnp.generate(xfer);

    {
        const byte_str response(
                "05 64 0A 44 03 00 04 00 7C AE");

        //  make sure we don't copy more than they expect
        std::copy(response.begin(), response.end(), 
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

        xfer.setInCountActual(response.size());
    }

    dnp.decode(xfer, ClientErrors::None);
    dnp.generate(xfer);

    {
        const byte_str response(
                "C2 C2 81 10 00 11 b9");

        //  make sure we don't copy more than they expect
        std::copy(response.begin(), response.end(), 
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

        xfer.setInCountActual(response.size());
    }

    dnp.decode(xfer, ClientErrors::None);

    auto string_list = dnp.getInboundStrings();

    BOOST_REQUIRE_EQUAL(4, string_list.size());

    BOOST_CHECK_EQUAL(string_list[0],
        "Successfully read internal indications");
    BOOST_CHECK_EQUAL(string_list[1],
        "Internal indications:\n"
        "Time synchronization needed\n"
        "Device restart\n"
        "Requested objects unknown\n");
    BOOST_CHECK_EQUAL(string_list[2],
        "Attempting to clear Device Restart bit");
    BOOST_CHECK_EQUAL(string_list[3],
        "Internal indications:\n"
        "Time synchronization needed\n");
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_restart_bit_generate_operation_already_executing)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(4, 3);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_ReadInternalIndications);

    CtiXfer xfer;

    dnp.setConfigData( 2, DNP::TimeOffset::Utc, false, false, false, false, false, false );

    dnp.generate(xfer);
    dnp.decode(xfer, ClientErrors::None);
    dnp.generate(xfer);

    {
        const byte_str response(
                "05 64 0A 44 03 00 04 00 7C AE");

        //  make sure we don't copy more than they expect
        std::copy(response.begin(), response.end(), 
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

        xfer.setInCountActual(response.size());
    }

    dnp.decode(xfer, ClientErrors::None);
    dnp.generate(xfer);

    {
        const byte_str response(
                "C1 C1 81 90 10 1E 7F");

        //  make sure we don't copy more than they expect
        std::copy(response.begin(), response.end(), 
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

        xfer.setInCountActual(response.size());
    }

    BOOST_CHECK_EQUAL(ClientErrors::OperationAlreadyExecuting, dnp.decode(xfer, ClientErrors::None)); 

    dnp.generate(xfer);
    dnp.decode(xfer, ClientErrors::None);
    dnp.generate(xfer);

    {
        const byte_str response(
                "05 64 0A 44 03 00 04 00 7C AE");

        //  make sure we don't copy more than they expect
        std::copy(response.begin(), response.end(), 
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

        xfer.setInCountActual(response.size());
    }

    dnp.decode(xfer, ClientErrors::None);
    dnp.generate(xfer);

    {
        const byte_str response(
                "C2 C2 81 10 00 11 b9");

        //  make sure we don't copy more than they expect
        std::copy(response.begin(), response.end(), 
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

        xfer.setInCountActual(response.size());
    }

    dnp.decode(xfer, ClientErrors::None);

    auto string_list = dnp.getInboundStrings();

    BOOST_REQUIRE_EQUAL(4, string_list.size());

    BOOST_CHECK_EQUAL(string_list[0],
        "Successfully read internal indications");
    BOOST_CHECK_EQUAL(string_list[1],
        "Internal indications:\n"
        "Time synchronization needed\n"
        "Device restart\n"
        "Request already executing\n");
    BOOST_CHECK_EQUAL(string_list[2],
        "Attempting to clear Device Restart bit");
    BOOST_CHECK_EQUAL(string_list[3],
        "Internal indications:\n"
        "Time synchronization needed\n");
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_with_time)
{
    Cti::Test::set_to_central_timezone();

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

            BOOST_CHECK_EQUAL(pd->getId(), 9999);

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

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_with_empty_time_block)
{
    Cti::Test::set_to_central_timezone();

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
                    "05 64 2b 44 01 00 d2 04 cb b8");

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

        BOOST_CHECK_EQUAL(44, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                    "c0 ca 81 0f 00 32 01 07 00 1e 01 18 01 00 03 00 e5 aa "
                    "3f 01 00 00 01 02 18 01 00 01 00 14 01 18 01 00 83 35 "
                    "00 00 13 00 00 00 99 52");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(), 
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(ClientErrors::Abnormal, dnp.decode(xfer, ClientErrors::None));

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
            BOOST_CHECK_EQUAL(pd->getId(), 9999);
            BOOST_CHECK_EQUAL(pd->getTags(), 0);
        }

        auto string_list = dnp.getInboundStrings();

        BOOST_CHECK_EQUAL(3, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Device returned an empty time object block");
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

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_with_invalid_time_object)
{
    Cti::Test::set_to_central_timezone();

    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_Class1230Read_WithTime);

    dnp.setConfigData(2, DNP::TimeOffset::Utc, false, false, false, false, false, false);

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
                "05 64 12 44 01 00 d2 04 b3 1e");

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

        BOOST_CHECK_EQUAL(15, xfer.getInCountExpected());
    }
    {
        {
            const byte_str response(
                "c0 ca 81 0f 00 32 01 07 01 de ad be ef 64 2c");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(),
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(ClientErrors::Abnormal, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_REQUIRE_EQUAL(1, point_list.size());

        {
            CtiPointDataMsg *pd = point_list[0];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);
            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);
            BOOST_CHECK_EQUAL(pd->getId(), 9999);
            BOOST_CHECK_EQUAL(pd->getTags(), 0);
        }

        auto string_list = dnp.getInboundStrings();

        BOOST_REQUIRE_EQUAL(3, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Device returned an empty time object block");
        BOOST_CHECK_EQUAL(string_list[1],
            "Internal indications:\n"
            "Broadcast message received\n"
            "Class 1 data available\n"
            "Class 2 data available\n"
            "Class 3 data available\n");
        BOOST_CHECK_EQUAL(string_list[2],
            "Point data report:\n"
            "AI:     0; AO:     0; DI:     0; DO:     0; Counters:     0; \n"
            "(No points returned)\n");

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

            BOOST_CHECK_EQUAL(pd->getId(), 9999);
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

            BOOST_CHECK_EQUAL(pd->getId(), 9999);
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

            BOOST_CHECK_EQUAL(pd->getId(), 9999);
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

            BOOST_CHECK_EQUAL(pd->getId(), 9999);
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

            BOOST_CHECK_EQUAL(pd->getId(), 9999);
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

            BOOST_CHECK_EQUAL(pd->getId(), 9999);
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

            BOOST_CHECK_EQUAL(pd->getId(), 9999);
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

            BOOST_CHECK_EQUAL(pd->getId(), 9999);
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

            BOOST_CHECK_EQUAL(pd->getId(), 9999);
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
    dnp.setCommand(DnpProtocol::Command_ReadInternalIndications);

    CtiXfer xfer;

    // Enable DNP timesync
    dnp.setConfigData( 1, DNP::TimeOffset::Utc, true, false, false, false, false, false );

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 08 C4 04 00 03 00 B4 B8 "
                "C0 C1 17 8C 0C");

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
                    "C1 C1 81 90 00 75 CD");

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

            BOOST_CHECK_EQUAL(pd->getId(), 9999);
        }

        {
            CtiPointDataMsg *pd = point_list[1];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 9999);
        }

        {
            CtiPointDataMsg *pd = point_list[2];

            BOOST_CHECK_EQUAL(pd->getValue(), 0);

            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);

            BOOST_CHECK_EQUAL(pd->getId(), 9999);
        }

        auto string_list = dnp.getInboundStrings();

        BOOST_REQUIRE_EQUAL(6, string_list.size());

        BOOST_CHECK_EQUAL(string_list[0],
            "Successfully read internal indications");
        BOOST_CHECK_EQUAL(string_list[1],
            "Internal indications:\n"
            "Time synchronization needed\n"
            "Device restart\n");
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

void simulateUnsolicited(DnpProtocol &dnp, const byte_str &response, bool transactionComplete)
{
    CtiXfer xfer;
    auto responseIterator = response.begin();

    //  First cycle.  generate gives us a buffer big enough for the header.
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));
        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());

        //  make sure we don't copy more than they expect
        std::copy(responseIterator, responseIterator + xfer.getInCountExpected(),
            stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));
        xfer.setInCountActual(xfer.getInCountExpected());
        responseIterator += xfer.getInCountExpected();

        // now process that header
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));
        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  Now we read in the remaining data for the packet
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));
        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
        int remaining = response.end() - responseIterator;
        BOOST_CHECK_EQUAL(remaining, xfer.getInCountExpected());

        //  make sure we don't copy more than they expect
        std::copy(responseIterator, response.end(),
            stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));
        xfer.setInCountActual(remaining);

        // And process it
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));
        // it isn't over till the fat lady sings.
        BOOST_CHECK_EQUAL(transactionComplete, dnp.isTransactionComplete());
    }
}

/**
  * Test a message with an absolute time after all the point data to make sure the time gets propagated to the points.
  * Message was captured from OpenDNP3 with a patch to send the Group50Var1 Absolute Time after the points.
  */
BOOST_AUTO_TEST_CASE(test_prot_dnp_late_absolute_time)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(2, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_UnsolicitedInbound);
    //  Set to UTC so timestamp interpretation doesn't wiggle over DST changes
    dnp.setConfigData(2, DNP::TimeOffset::Utc, false, false, true, false, false, false);

    // Actual data from OpenDNP simulating a CBC-8024
    const byte_str response(
        "05 64 ff 44 01 00 02 00 ce 84 "
        "44 cb 81 00 01 01 02 01 01 00 03 00 02 02 02 01 73 91 02 01 05 00 05 00 02 01 "
        "01 01 0d 00 0e 00 00 01 95 ae 02 01 43 00 45 00 02 02 02 01 02 01 47 00 47 00 "
        "d3 31 02 01 02 01 55 00 55 00 02 01 02 01 58 00 58 00 e0 25 02 01 02 01 7d 00 "
        "7e 00 02 02 01 02 01 d0 07 d0 16 8c 07 02 14 01 00 01 03 02 00 00 00 00 02 00 "
        "00 00 61 34 00 02 00 00 00 00 1e 01 01 06 00 07 00 02 00 00 0d e8 00 00 02 00 "
        "00 00 00 1e 01 01 0b 00 0b 00 01 9c 38 c4 04 00 00 1e 01 01 14 00 14 00 02 00 "
        "00 00 00 1e 46 9b 01 01 71 00 71 00 02 00 00 00 00 1e 01 01 0e 27 68 e0 0e 27 "
        "02 00 00 00 00 1e 01 01 10 27 10 27 02 00 71 64 00 00 00 1e 01 01 14 27 14 27 "
        "02 00 00 00 00 1e 80 28 01 01 16 27 18 27 02 00 00 00 00 02 00 00 00 00 eb c1 "
        "02 00 00 00 00 1e 01 01 1a 27 1c 27 02 00 00 00 57 a2 00 02 00 00 00 00 02 00 "
        "00 00 00 1e 01 01 1f 27 05 9b 1f 27 02 00 00 00 00 1e 01 01 6f 47 "
    );
    simulateUnsolicited(dnp, response, false);

    // This is a 2 packet message
    const byte_str response2(
        "05 64 31 44 01 00 02 00 35 ca "
        "85 23 27 23 27 02 00 00 00 00 1e 01 01 4d 28 4d 63 ed 28 02 00 00 00 00 1e 01 "
        "01 20 4e 20 4e 02 00 00 6c 1e 00 00 32 01 07 01 e8 be fc b1 58 01 2b 12 "
    );
    simulateUnsolicited(dnp, response2, true);

    pointlist_t point_list;
    dnp.getInboundPoints(point_list);
    BOOST_CHECK_EQUAL(37, point_list.size());   // we have a lot of points

    {
        // Make some spot checks
        for (auto pd : point_list)
        {
            switch (pd->getId())
            {
            case 12:
                BOOST_CHECK_EQUAL(pd->getValue(), 1180.0000000000000);
                BOOST_CHECK_EQUAL(pd->getType(), AnalogPointType);
                BOOST_CHECK_EQUAL(pd->getTime(), CtiTime(1480454881));
                break;
            }
        }
    }
}

/**
* Test a message with an absolute time before all the point data to make sure the time gets propagated to the points.
* Message was captured from OpenDNP3 with a patch to send the Group50Var1 Absolute Time before the points.
*/
BOOST_AUTO_TEST_CASE(test_prot_dnp_early_absolute_time)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(2, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_UnsolicitedInbound);
    //  Set to UTC so timestamp interpretation doesn't wiggle over DST changes
    dnp.setConfigData(2, DNP::TimeOffset::Utc, false, false, true, false, false, false);

    // Actual data from OpenDNP simulating a CBC-8024
    const byte_str response(
        "05 64 ff 44 01 00 02 00 ce 84 "
        "4f c9 81 00 01 32 01 07 01 f0 7c 4a b7 58 01 01 97 a3 02 01 01 00 03 00 02 02 "
        "02 01 02 01 05 00 05 00 a9 83 02 01 01 01 0d 00 0e 00 00 01 02 01 43 00 45 00 "
        "78 a2 02 02 02 01 02 01 47 00 47 00 02 01 02 01 55 00 3a 41 55 00 02 01 02 01 "
        "58 00 58 00 02 01 02 01 7d 00 22 7d 7e 00 02 02 01 02 01 d0 07 d0 07 02 14 01 "
        "00 01 62 57 03 02 00 00 00 00 02 00 00 00 00 02 00 00 00 00 ea 78 1e 01 01 06 "
        "00 07 00 02 00 00 00 00 02 00 00 00 92 cf 00 1e 01 01 0b 00 0b 00 01 0c 04 00 "
        "00 1e 01 01 b1 8b 14 00 14 00 02 00 00 00 00 1e 01 01 71 00 71 00 17 85 02 00 "
        "00 00 00 1e 01 01 0e 27 0e 27 02 00 00 00 30 88 00 1e 01 01 10 27 10 27 02 00 "
        "00 00 00 1e 01 01 37 77 14 27 14 27 02 00 00 00 00 1e 01 01 16 27 18 27 8c 9d "
        "02 00 00 00 00 02 00 00 00 00 02 00 00 00 00 1e 6a 97 01 01 1a 27 1c 27 02 00 "
        "00 00 00 02 00 00 00 00 c5 e4 02 00 00 00 00 1e 01 01 1f 27 35 47 "
    );
    simulateUnsolicited(dnp, response, false);

    // This is a 2 packet message
    const byte_str response2(
        "05 64 31 44 01 00 02 00 35 ca "
        "90 1f 27 02 00 00 00 00 1e 01 01 23 27 23 27 02 16 84 00 00 00 00 1e 01 01 4d "
        "28 4d 28 02 00 00 00 00 33 19 1e 01 01 20 4e 20 4e 02 00 00 00 00 39 38 "
    );
    simulateUnsolicited(dnp, response2, true);

    pointlist_t point_list;
    dnp.getInboundPoints(point_list);
    BOOST_CHECK_EQUAL(37, point_list.size());   // we have a lot of points

    {
        // Make some spot checks
        for (auto pd : point_list)
        {
            switch (pd->getId())
            {
            case 12:
                BOOST_CHECK_EQUAL(pd->getValue(), 1036.0000000000000);
                BOOST_CHECK_EQUAL(pd->getType(), AnalogPointType);
                BOOST_CHECK_EQUAL(pd->getTime(), CtiTime(1480543862));
                break;
            }
        }
    }
}

BOOST_AUTO_TEST_SUITE_END()
