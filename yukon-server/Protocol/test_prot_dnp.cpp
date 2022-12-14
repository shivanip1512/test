#include <boost/test/unit_test.hpp>

#include "prot_dnp.h"
#include "utility.h"

#include "boost_test_helpers.h"

using Cti::Test::byte_str;
using namespace Cti::Protocols;
typedef Interface::pointlist_t  pointlist_t;
typedef Interface::stringlist_t stringlist_t;

BOOST_AUTO_TEST_SUITE( test_prot_dnp )

std::vector<int> getOutput(const CtiXfer& xfer)
{
    return { xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount() };
}

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
            "05 64 05 C9 04 00 03 00 B6 20");

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

BOOST_AUTO_TEST_CASE(test_prot_dnp_loopback_unsupported)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(4, 3);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_Loopback);

    CtiXfer xfer;

    dnp.setConfigData(2, DNP::TimeOffset::Utc, false, false, false, false, false, false);

    for( int i = 0; i < 12; ++i )
    {
        {
            BOOST_CHECK_EQUAL(ClientErrors::None, dnp.generate(xfer));

            BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

            BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

            const byte_str expected(
                "05 64 05 C9 04 00 03 00 B6 20");

            BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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
                    "05 64 05 0F 03 00 04 00 67 EA");

                //  make sure we don't copy more than they expect
                std::copy(response.begin(), response.end(),
                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

                xfer.setInCountActual(response.size());
            }

            if( (i + 1) % 4 )
            {
                BOOST_CHECK_EQUAL(ClientErrors::None, dnp.decode(xfer, ClientErrors::None));
            }
            else  //  every 4rd loop is an error
            {
                BOOST_CHECK_EQUAL(ClientErrors::Abnormal, dnp.decode(xfer, ClientErrors::None));
            }

            if( i < 11 )
            {
                BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
            }
            else  //  final iteration
            {
                BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());
            }
        }
    }
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
            "05 64 05 C9 04 00 03 00 B6 20");

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
            "05 64 05 C9 04 00 03 00 B6 20");

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        const auto string_list = dnp.getInboundStrings();

        BOOST_REQUIRE_EQUAL(5, string_list.size());

        auto string_itr = string_list.cbegin();

        BOOST_CHECK_EQUAL(*string_itr++,
            "Successfully read internal indications");
        BOOST_CHECK_EQUAL(*string_itr++,
            "Internal indications:\n"
            "Time synchronization needed\n"
            "Device restart\n");
        BOOST_CHECK_EQUAL(*string_itr++,
            "Attempting to clear Device Restart bit");
        BOOST_CHECK_EQUAL(*string_itr++,
            "Internal indications:\n"
            "Time synchronization needed\n");
        BOOST_CHECK_EQUAL(*string_itr++,
            "Reset Device Restart Bit completed successfully");

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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        const auto string_list = dnp.getInboundStrings();

        BOOST_REQUIRE_EQUAL(8, string_list.size());

        auto string_itr = string_list.cbegin();

        BOOST_CHECK_EQUAL(*string_itr++,
            "Successfully read internal indications");
        BOOST_CHECK_EQUAL(*string_itr++,
            "Internal indications:\n"
            "Time synchronization needed\n"
            "Device restart\n");
        BOOST_CHECK_EQUAL(*string_itr++,
            "Attempting to clear Device Restart bit");
        BOOST_CHECK_EQUAL(*string_itr++,
            "Internal indications:\n"
            "Time synchronization needed\n");
        BOOST_CHECK_EQUAL(*string_itr++,
            "Reset Device Restart Bit completed successfully");
        BOOST_CHECK_EQUAL(*string_itr++,
            "Unsolicited reporting enabled");
        BOOST_CHECK_EQUAL(*string_itr++,
            "Internal indications:\n"
            "Time synchronization needed\n");
        BOOST_CHECK_EQUAL(*string_itr++,
            "Unsolicited Enable completed successfully");

        delete_container(point_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_restart_bit_with_unsolicited_unsupported)
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(4, 3);
    dnp.setName("Test DNP device");
    dnp.setCommand(DnpProtocol::Command_Class1230Read);

    CtiXfer xfer;

    dnp.setConfigData(2, DNP::TimeOffset::Utc, false, false, true, false, false, false);

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
            "05 64 14 C4 04 00 03 00 c7 17 "
            "c0 c1 01 3c 02 06 3c 03 06 3c 04 06 3c 01 06 7a 6f");

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
                "05 64 27 44 03 00 04 00 AB 3D");

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
                "c0 ca 81 9f 00 1e 01 18 01 00 03 00 3f 01 00 00 DB A8 "
                "01 02 18 01 00 01 00 14 01 18 01 00 00 00 13 00 95 4f "
                "00 00 ff ff");


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
                "C3 C3 81 10 01 A1 6E");

            //  make sure we don't copy more than they expect
            std::copy(response.begin(), response.end(),
                stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_REQUIRE_EQUAL(6, point_list.size());

        auto pd_itr = point_list.cbegin();

        {
            auto pd = *pd_itr++;

            BOOST_CHECK_EQUAL(pd->getValue(), 319);
            BOOST_CHECK_EQUAL(pd->getType(), AnalogPointType);
            BOOST_CHECK_EQUAL(pd->getId(), 4);
        }
        {
            auto pd = *pd_itr++;

            BOOST_CHECK_EQUAL(pd->getValue(), 0);
            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);
            BOOST_CHECK_EQUAL(pd->getId(), 2);
        }
        {
            auto pd = *pd_itr++;

            BOOST_CHECK_EQUAL(pd->getValue(), 19);
            BOOST_CHECK_EQUAL(pd->getType(), PulseAccumulatorPointType);
            BOOST_CHECK_EQUAL(pd->getId(), 1);
        }
        {
            auto pd = *pd_itr++;

            BOOST_CHECK_EQUAL(pd->getValue(), 1);
            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);
            BOOST_CHECK_EQUAL(pd->getId(), 9999);
        }
        {
            auto pd = *pd_itr++;

            BOOST_CHECK_EQUAL(pd->getValue(), 0);
            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);
            BOOST_CHECK_EQUAL(pd->getId(), 9999);
        }
        {
            auto pd = *pd_itr++;

            BOOST_CHECK_EQUAL(pd->getValue(), 0);
            BOOST_CHECK_EQUAL(pd->getType(), StatusPointType);
            BOOST_CHECK_EQUAL(pd->getId(), 9999);
        }

        const auto string_list = dnp.getInboundStrings();

        BOOST_REQUIRE_EQUAL(7, string_list.size());

        auto str_itr = string_list.cbegin();

        BOOST_CHECK_EQUAL(*str_itr++,
            "Internal indications:\n"
            "Broadcast message received\n"
            "Class 1 data available\n"
            "Class 2 data available\n"
            "Class 3 data available\n"
            "Time synchronization needed\n"
            "Device restart\n");
        BOOST_CHECK_EQUAL(*str_itr++,
            "Point data report:"
            "\nAI:     1; AO:     0; DI:     1; DO:     0; Counters:     1; "
            "\nFirst/Last 5 points of each type returned:"
            "\nAnalog inputs:"
            "\n[4:319]"
            "\nBinary inputs:"
            "\n[2:0]"
            "\nCounters:"
            "\n[1:19]"
            "\n");
        BOOST_CHECK_EQUAL(*str_itr++,
            "Attempting to clear Device Restart bit");
        BOOST_CHECK_EQUAL(*str_itr++,
            "Internal indications:\n"
            "Time synchronization needed\n");
        BOOST_CHECK_EQUAL(*str_itr++,
            "Reset Device Restart Bit completed successfully");
        BOOST_CHECK_EQUAL(*str_itr++,
            "Internal indications:\n"
            "Time synchronization needed\n"
            "Function code not implemented\n");
        BOOST_CHECK_EQUAL(*str_itr++,
            "Unsolicited Enable completed with status 304 - Function code not supported.");

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

    const auto string_list = dnp.getInboundStrings();

    BOOST_REQUIRE_EQUAL(5, string_list.size());

    auto string_itr = string_list.cbegin();

    BOOST_CHECK_EQUAL(*string_itr++,
        "Successfully read internal indications");
    BOOST_CHECK_EQUAL(*string_itr++,
        "Internal indications:\n"
        "Time synchronization needed\n"
        "Device restart\n"
        "Parameter error\n");
    BOOST_CHECK_EQUAL(*string_itr++,
        "Attempting to clear Device Restart bit");
    BOOST_CHECK_EQUAL(*string_itr++,
        "Internal indications:\n"
        "Time synchronization needed\n");
    BOOST_CHECK_EQUAL(*string_itr++,
        "Reset Device Restart Bit completed successfully");
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

    const auto string_list = dnp.getInboundStrings();

    BOOST_REQUIRE_EQUAL(5, string_list.size());

    auto string_itr = string_list.cbegin();

    BOOST_CHECK_EQUAL(*string_itr++,
        "Successfully read internal indications");
    BOOST_CHECK_EQUAL(*string_itr++,
        "Internal indications:\n"
        "Time synchronization needed\n"
        "Device restart\n"
        "Function code not implemented\n");
    BOOST_CHECK_EQUAL(*string_itr++,
        "Attempting to clear Device Restart bit");
    BOOST_CHECK_EQUAL(*string_itr++,
        "Internal indications:\n"
        "Time synchronization needed\n");
    BOOST_CHECK_EQUAL(*string_itr++,
        "Reset Device Restart Bit completed successfully");
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

    const auto string_list = dnp.getInboundStrings();

    BOOST_REQUIRE_EQUAL(5, string_list.size());

    auto string_itr = string_list.cbegin();

    BOOST_CHECK_EQUAL(*string_itr++,
        "Successfully read internal indications");
    BOOST_CHECK_EQUAL(*string_itr++,
        "Internal indications:\n"
        "Time synchronization needed\n"
        "Device restart\n"
        "Requested objects unknown\n");
    BOOST_CHECK_EQUAL(*string_itr++,
        "Attempting to clear Device Restart bit");
    BOOST_CHECK_EQUAL(*string_itr++,
        "Internal indications:\n"
        "Time synchronization needed\n");
    BOOST_CHECK_EQUAL(*string_itr++,
        "Reset Device Restart Bit completed successfully");
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

    const auto string_list = dnp.getInboundStrings();

    BOOST_REQUIRE_EQUAL(5, string_list.size());

    auto string_itr = string_list.cbegin();

    BOOST_CHECK_EQUAL(*string_itr++,
        "Successfully read internal indications");
    BOOST_CHECK_EQUAL(*string_itr++,
        "Internal indications:\n"
        "Time synchronization needed\n"
        "Device restart\n"
        "Request already executing\n");
    BOOST_CHECK_EQUAL(*string_itr++,
        "Attempting to clear Device Restart bit");
    BOOST_CHECK_EQUAL(*string_itr++,
        "Internal indications:\n"
        "Time synchronization needed\n");
    BOOST_CHECK_EQUAL(*string_itr++,
        "Reset Device Restart Bit completed successfully");
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_with_time)
{
    const auto tz_override = Cti::Test::set_to_central_timezone();

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

        const byte_str expected(
                "05 64 17 c4 d2 04 01 00 85 40 "
                "c0 c1 01 32 01 06 3c 02 06 3c "
                "03 06 3c 04 06 3c fe e0 01 06 "
                "75 e1");

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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
    const auto tz_override = Cti::Test::set_to_central_timezone();

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

        const byte_str expected(
                "05 64 17 c4 d2 04 01 00 85 40 "
                "c0 c1 01 32 01 06 3c 02 06 3c "
                "03 06 3c 04 06 3c fe e0 01 06 "
                "75 e1");

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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
    const auto tz_override = Cti::Test::set_to_central_timezone();

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

        const byte_str expected(
            "05 64 17 c4 d2 04 01 00 85 40 "
            "c0 c1 01 32 01 06 3c 02 06 3c "
            "03 06 3c 04 06 3c fe e0 01 06 "
            "75 e1");

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        const byte_str expected(
                "05 64 17 c4 d2 04 01 00 85 40 "
                "c0 c1 01 32 01 06 3c 02 06 3c "
                "03 06 3c 04 06 3c fe e0 01 06 "
                "75 e1");

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_timeout_reset, *boost::unit_test::disabled())
{
    DnpProtocol dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1024, 10);
    dnp.setName("OSI RTU");

    dnp.setConfigData(1, DNP::TimeOffset::Utc, true, false, false, false, false, false);

    CtiXfer xfer;

    struct trx {
        byte_str out;
        std::vector<byte_str> ins;
    };

    /*
    2020-03-03 05:15:01.292  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) OUT:
    05 64 14 c4 00 04 0a 00 d6 69 c0 c1 01 3c 02 06 3c 03 06 3c 04 06 3c 01 06 7a
    6f
    2020-03-03 05:15:01.299  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) IN:
    05 64 ff 53 0a 00 00 04 bd fa

    2020-03-03 05:15:01.401  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) IN:
    40 c1 81 00 00 1e 01 00 00 77 01 00 00 00 00 01 9b 8b a7 fe ff ff 01 69 ff ff
    ff 01 d7 ff ff ff 01 8f 82 14 ff ff ff 01 77 ff ff ff 01 ad fe ff ff 01 8c ff
    b7 48 ff ff 01 c6 ff ff ff 01 4c fa ff ff 01 62 fc ff 63 7c ff 01 df fe ff ff
    01 35 fe ff ff 01 ae ff ff ff a8 39 01 f5 fb ff ff 01 11 ff ff ff 01 e7 ff ff
    ff 01 99 26 48 fe ff ff 01 d2 fe ff ff 01 69 fe ff ff 01 2f ce e8 00 00 00 01
    06 00 00 00 01 32 00 00 00 01 0f fe a1 24 ff ff 01 c6 fd ff ff 01 f3 ff ff ff
    01 00 00 00 09 87 00 01 f3 ff ff ff 01 9a fd ff ff 01 b4 fc ff ff ff ca 01 85
    ff ff ff 01 11 00 00 00 01 a0 fd ff ff 01 e0 25 7e ff ff ff 01 c0 fa ff ff 01
    11 fe ff ff 01 3b 6e cf ff ff ff 01 ae ff ff ff 01 52 ff ff ff 01 20 ff e8 81
    ff ff 01 43 00 00 00 01 fd ff ff ff 01 6b fe ff a6 3e ff 01 92 ff ff ff 01 75
    ff ff ff 01 83 ff ff ff 87 a6 01 56 ff ff ff 01 bd fc ff ff bd 7e

    2020-03-03 05:15:01.497  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) OUT:
    05 64 05 80 00 04 0a 00 52 18

    2020-03-03 05:15:01.504  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) IN:
    05 64 ff 73 0a 00 00 04 e0 e2

    2020-03-03 05:15:01.606  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) IN:
    01 01 d0 ff ff ff 01 ee ff ff ff 01 b6 fd ff ff a5 3e 01 6f ff ff ff 01 94 fc
    ff ff 01 43 fe ff ff 01 eb 73 6d 00 00 00 01 39 fd ff ff 01 26 00 00 00 01 f2
    0f da ff ff ff 01 0c 00 00 00 01 66 00 00 00 01 a2 ff b3 70 ff ff 01 6c 00 00
    00 01 50 ff ff ff 01 aa ff ff 72 ae ff 01 a7 ff ff ff 01 08 fe ff ff 01 e8 fd
    ff ff 3c 49 01 d6 ff ff ff 01 83 ff ff ff 01 19 00 00 00 01 50 3e b1 00 00 00
    01 fe fe ff ff 01 c2 02 00 00 01 17 d6 33 ff ff ff 01 fc fe ff ff 01 9e fe ff
    ff 01 49 00 26 33 00 00 01 73 fc ff ff 01 05 00 00 00 01 94 fe ff e8 fa ff 01
    d2 fe ff ff 01 10 ff ff ff 01 15 ff ff ff 03 24 01 e4 ff ff ff 01 4e fc ff ff
    01 e9 ff ff ff 01 03 f1 63 fb ff ff 01 5a ff ff ff 01 ee fd ff ff 01 1a fc 96
    fe ff ff 01 15 00 00 00 01 78 ff ff ff 01 b2 ff 0a 97 ff ff 01 c3 ff ff ff 01
    8d ff ff ff 01 06 fe ff 78 e4 ff 01 6e fe ff ff 01 00 00 00 ae 2a

    2020-03-03 05:15:01.703  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) OUT:
    05 64 05 80 00 04 0a 00 52 18

    2020-03-03 05:15:01.709  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) IN:
    05 64 75 53 0a 00 00 04 55 57

    2020-03-03 05:15:01.811  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) IN:
    82 00 01 5f fe ff ff 01 63 fb ff ff 01 86 fb ff 32 35 ff 01 de ff ff ff 01 6f
    fe ff ff 01 34 fe ff ff cc b8 01 7e fe ff ff 01 8f fd ff ff 01 d5 ff ff ff 01
    10 73 36 ff ff ff 01 41 fe ff ff 01 d7 f8 ff ff 01 b5 84 7d ff ff ff 01 88 fd
    ff ff 01 cb fc ff ff 01 55 ff 92 22 ff ff 01 bf 21 02 00 01 09 07 00 00 01 d4
    bd ff 8d 69 ff 01 48 fd ff ff 01 e4 00 00 00 01 74 fd ff ff 0b ed

    2020-03-03 05:15:01.907  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) OUT:
    05 64 05 80 00 04 0a 00 52 18

    2020-03-03 05:15:30.516  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) OUT:
    05 64 14 c4 00 04 0a 00 d6 69 c0 c2 01 3c 02 06 3c 03 06 3c 04 06 3c 01 06 6a
    2c

    2020-03-03 05:15:30.522  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) IN:
    05 64 ff 73 0a 00 00 04 e0 e2

    2020-03-03 05:15:30.624  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) IN:
    40 c2 81 00 00 1e 01 00 00 77 01 00 00 00 00 01 b3 39 8c fe ff ff 01 65 ff ff
    ff 01 d6 ff ff ff 01 8f e8 62 ff ff ff 01 7a ff ff ff 01 ac fe ff ff 01 8b ff
    6a d0 ff ff 01 d0 ff ff ff 01 4c fa ff ff 01 61 fc ff 1c 1f ff 01 db fe ff ff
    01 36 fe ff ff 01 b1 ff ff ff 30 68 01 f4 fb ff ff 01 12 ff ff ff 01 e7 ff ff
    ff 01 5e 40 45 fe ff ff 01 cd fe ff ff 01 69 fe ff ff 01 2b a2 01 00 00 00 01
    00 00 00 00 01 28 00 00 00 01 0d fe 06 d3 ff ff 01 b9 fd ff ff 01 f3 ff ff ff
    01 00 00 00 a5 fd 00 01 f1 ff ff ff 01 97 fd ff ff 01 d8 fc ff ff 6b fd 01 93
    ff ff ff 01 1b 00 00 00 01 ac fd ff ff 01 b5 e4 7e ff ff ff 01 c1 fa ff ff 01
    18 fe ff ff 01 39 67 9a ff ff ff 01 b4 ff ff ff 01 50 ff ff ff 01 13 ff 3a 22
    ff ff 01 4d 00 00 00 01 00 00 00 00 01 6b fe ff ca 76 ff 01 8d ff ff ff 01 77
    ff ff ff 01 88 ff ff ff b0 93 01 5b ff ff ff 01 bd fc ff ff 94 18

    2020-03-03 05:15:30.721  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) OUT:
    05 64 05 80 00 04 0a 00 52 18

    2020-03-03 05:15:30.728  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) IN:
    05 64 ff 53 0a 00 00 04 bd fa

    2020-03-03 05:15:30.829  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) IN:
    01 01 d5 ff ff ff 01 e9 ff ff ff 01 b6 fd ff ff 20 79 01 70 ff ff ff 01 94 fc
    ff ff 01 3b fe ff ff 01 42 47 76 00 00 00 01 36 fd ff ff 01 24 00 00 00 01 f1
    db f3 ff ff ff 01 0c 00 00 00 01 68 00 00 00 01 ab ff be 5b ff ff 01 6c 00 00
    00 01 52 ff ff ff 01 a9 ff ff 9e a8 ff 01 a9 ff ff ff 01 08 fe ff ff 01 ea fd
    ff ff 4c bd 01 d6 ff ff ff 01 85 ff ff ff 01 19 00 00 00 01 ee 2f ba 00 00 00
    01 0b ff ff ff 01 b2 02 00 00 01 16 1a 14 ff ff ff 01 0a ff ff ff 01 a9 fe ff
    ff 01 3f 00 21 24 00 00 01 61 fc ff ff 01 04 00 00 00 01 97 fe ff 10 be ff 01
    db fe ff ff 01 13 ff ff ff 01 0d ff ff ff 38 dc 01 e0 ff ff ff 01 49 fc ff ff
    01 ec ff ff ff 01 d8 79 6c fb ff ff 01 5a ff ff ff 01 ee fd ff ff 01 1a b3 cd
    fe ff ff 01 12 00 00 00 01 6b ff ff ff 01 b2 ff 2f a9 ff ff 01 c3 ff ff ff 01
    93 ff ff ff 01 fb fd ff 54 e1 ff 01 73 fe ff ff 01 00 00 00 3d f8

    2020-03-03 05:15:31.033  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) OUT:
    05 64 05 80 00 04 0a 00 52 18

    2020-03-03 05:15:42.047  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) IN:   1
    Not Normal (Unsuccessful) Return

    2020-03-03 05:15:42.143  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) OUT:
    05 64 14 c4 00 04 0a 00 d6 69 c0 c2 01 3c 02 06 3c 03 06 3c 04 06 3c 01 06 6a
    2c

    2020-03-03 05:15:42.150  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) IN:
    05 64 05 40 0a 00 00 04 f3 bf
    2020-03-03 05:15:42.150  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) IN:
    05 64 05 40 0a 00 00 04 f3 bf

    2020-03-03 05:15:53.060  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) IN:   1
    Not Normal (Unsuccessful) Return
    2020-03-03 05:15:53.060  P: 216993 / TCP port TCP PORT CBC  D: 236108 / OSI RTU (192.168.10.12:20010) IN:   1
    Not Normal (Unsuccessful) Return

    */
    std::vector<trx> 
        success {
            {
                "05 64 14 c4 00 04 0a 00 d6 69 c0 c1 01 3c 02 06 3c 03 06 3c 04 06 3c 01 06 7a 6f",
                {   "05 64 ff 53 0a 00 00 04 bd fa",
                    "40 c1 81 00 00 1e 01 00 00 77 01 00 00 00 00 01 9b 8b a7 fe ff ff 01 69 ff ff "
                    "ff 01 d7 ff ff ff 01 8f 82 14 ff ff ff 01 77 ff ff ff 01 ad fe ff ff 01 8c ff "
                    "b7 48 ff ff 01 c6 ff ff ff 01 4c fa ff ff 01 62 fc ff 63 7c ff 01 df fe ff ff "
                    "01 35 fe ff ff 01 ae ff ff ff a8 39 01 f5 fb ff ff 01 11 ff ff ff 01 e7 ff ff "
                    "ff 01 99 26 48 fe ff ff 01 d2 fe ff ff 01 69 fe ff ff 01 2f ce e8 00 00 00 01 "
                    "06 00 00 00 01 32 00 00 00 01 0f fe a1 24 ff ff 01 c6 fd ff ff 01 f3 ff ff ff "
                    "01 00 00 00 09 87 00 01 f3 ff ff ff 01 9a fd ff ff 01 b4 fc ff ff ff ca 01 85 "
                    "ff ff ff 01 11 00 00 00 01 a0 fd ff ff 01 e0 25 7e ff ff ff 01 c0 fa ff ff 01 "
                    "11 fe ff ff 01 3b 6e cf ff ff ff 01 ae ff ff ff 01 52 ff ff ff 01 20 ff e8 81 "
                    "ff ff 01 43 00 00 00 01 fd ff ff ff 01 6b fe ff a6 3e ff 01 92 ff ff ff 01 75 "
                    "ff ff ff 01 83 ff ff ff 87 a6 01 56 ff ff ff 01 bd fc ff ff bd 7e" } },
            {
                "05 64 05 80 00 04 0a 00 52 18",
                {   "05 64 ff 73 0a 00 00 04 e0 e2",
                    "01 01 d0 ff ff ff 01 ee ff ff ff 01 b6 fd ff ff a5 3e 01 6f ff ff ff 01 94 fc "
                    "ff ff 01 43 fe ff ff 01 eb 73 6d 00 00 00 01 39 fd ff ff 01 26 00 00 00 01 f2 "
                    "0f da ff ff ff 01 0c 00 00 00 01 66 00 00 00 01 a2 ff b3 70 ff ff 01 6c 00 00 "
                    "00 01 50 ff ff ff 01 aa ff ff 72 ae ff 01 a7 ff ff ff 01 08 fe ff ff 01 e8 fd "
                    "ff ff 3c 49 01 d6 ff ff ff 01 83 ff ff ff 01 19 00 00 00 01 50 3e b1 00 00 00 "
                    "01 fe fe ff ff 01 c2 02 00 00 01 17 d6 33 ff ff ff 01 fc fe ff ff 01 9e fe ff "
                    "ff 01 49 00 26 33 00 00 01 73 fc ff ff 01 05 00 00 00 01 94 fe ff e8 fa ff 01 "
                    "d2 fe ff ff 01 10 ff ff ff 01 15 ff ff ff 03 24 01 e4 ff ff ff 01 4e fc ff ff "
                    "01 e9 ff ff ff 01 03 f1 63 fb ff ff 01 5a ff ff ff 01 ee fd ff ff 01 1a fc 96 "
                    "fe ff ff 01 15 00 00 00 01 78 ff ff ff 01 b2 ff 0a 97 ff ff 01 c3 ff ff ff 01 "
                    "8d ff ff ff 01 06 fe ff 78 e4 ff 01 6e fe ff ff 01 00 00 00 ae 2a" } },
            {
                "05 64 05 80 00 04 0a 00 52 18",
                {   "05 64 75 53 0a 00 00 04 55 57",
                    "82 00 01 5f fe ff ff 01 63 fb ff ff 01 86 fb ff 32 35 ff 01 de ff ff ff 01 6f "
                    "fe ff ff 01 34 fe ff ff cc b8 01 7e fe ff ff 01 8f fd ff ff 01 d5 ff ff ff 01 "
                    "10 73 36 ff ff ff 01 41 fe ff ff 01 d7 f8 ff ff 01 b5 84 7d ff ff ff 01 88 fd "
                    "ff ff 01 cb fc ff ff 01 55 ff 92 22 ff ff 01 bf 21 02 00 01 09 07 00 00 01 d4 "
                    "bd ff 8d 69 ff 01 48 fd ff ff 01 e4 00 00 00 01 74 fd ff ff 0b ed" } },
            {
                "05 64 05 80 00 04 0a 00 52 18", 
                { } } },
        failure {
            {
                "05 64 14 c4 00 04 0a 00 d6 69 c0 c2 01 3c 02 06 3c 03 06 3c 04 06 3c 01 06 6a 2c",
                {   "05 64 ff 73 0a 00 00 04 e0 e2",
                    "40 c2 81 00 00 1e 01 00 00 77 01 00 00 00 00 01 b3 39 8c fe ff ff 01 65 ff ff "
                    "ff 01 d6 ff ff ff 01 8f e8 62 ff ff ff 01 7a ff ff ff 01 ac fe ff ff 01 8b ff "
                    "6a d0 ff ff 01 d0 ff ff ff 01 4c fa ff ff 01 61 fc ff 1c 1f ff 01 db fe ff ff "
                    "01 36 fe ff ff 01 b1 ff ff ff 30 68 01 f4 fb ff ff 01 12 ff ff ff 01 e7 ff ff "
                    "ff 01 5e 40 45 fe ff ff 01 cd fe ff ff 01 69 fe ff ff 01 2b a2 01 00 00 00 01 "
                    "00 00 00 00 01 28 00 00 00 01 0d fe 06 d3 ff ff 01 b9 fd ff ff 01 f3 ff ff ff "
                    "01 00 00 00 a5 fd 00 01 f1 ff ff ff 01 97 fd ff ff 01 d8 fc ff ff 6b fd 01 93 "
                    "ff ff ff 01 1b 00 00 00 01 ac fd ff ff 01 b5 e4 7e ff ff ff 01 c1 fa ff ff 01 "
                    "18 fe ff ff 01 39 67 9a ff ff ff 01 b4 ff ff ff 01 50 ff ff ff 01 13 ff 3a 22 "
                    "ff ff 01 4d 00 00 00 01 00 00 00 00 01 6b fe ff ca 76 ff 01 8d ff ff ff 01 77 "
                    "ff ff ff 01 88 ff ff ff b0 93 01 5b ff ff ff 01 bd fc ff ff 94 18" } },
            {
                "05 64 05 80 00 04 0a 00 52 18",
                {   "05 64 ff 53 0a 00 00 04 bd fa",
                    "01 01 d5 ff ff ff 01 e9 ff ff ff 01 b6 fd ff ff 20 79 01 70 ff ff ff 01 94 fc "
                    "ff ff 01 3b fe ff ff 01 42 47 76 00 00 00 01 36 fd ff ff 01 24 00 00 00 01 f1 "
                    "db f3 ff ff ff 01 0c 00 00 00 01 68 00 00 00 01 ab ff be 5b ff ff 01 6c 00 00 "
                    "00 01 52 ff ff ff 01 a9 ff ff 9e a8 ff 01 a9 ff ff ff 01 08 fe ff ff 01 ea fd "
                    "ff ff 4c bd 01 d6 ff ff ff 01 85 ff ff ff 01 19 00 00 00 01 ee 2f ba 00 00 00 "
                    "01 0b ff ff ff 01 b2 02 00 00 01 16 1a 14 ff ff ff 01 0a ff ff ff 01 a9 fe ff "
                    "ff 01 3f 00 21 24 00 00 01 61 fc ff ff 01 04 00 00 00 01 97 fe ff 10 be ff 01 "
                    "db fe ff ff 01 13 ff ff ff 01 0d ff ff ff 38 dc 01 e0 ff ff ff 01 49 fc ff ff "
                    "01 ec ff ff ff 01 d8 79 6c fb ff ff 01 5a ff ff ff 01 ee fd ff ff 01 1a b3 cd "
                    "fe ff ff 01 12 00 00 00 01 6b ff ff ff 01 b2 ff 2f a9 ff ff 01 c3 ff ff ff 01 "
                    "93 ff ff ff 01 fb fd ff 54 e1 ff 01 73 fe ff ff 01 00 00 00 3d f8" } },
            {
                "05 64 05 80 00 04 0a 00 52 18",
                { "" } } },
        resets {
            {
                "05 64 14 c4 00 04 0a 00 d6 69 c0 c2 01 3c 02 06 3c 03 06 3c 04 06 3c 01 06 6a 2c",
                {   "05 64 05 40 0a 00 00 04 f3 bf",
                    "05 64 05 40 0a 00 00 04 f3 bf" } } };

    unsigned scan_num = 0;

    for( const auto scan : { success, failure, resets } )
    {
        dnp.setCommand(DnpProtocol::Command_Class1230Read);

        BOOST_TEST_CONTEXT("scan number " << ++scan_num)
        {
            unsigned packet_num = 0;

            for( const auto comms : scan )
            {
                BOOST_TEST_CONTEXT("transaction number " << ++packet_num)
                {
                    BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

                    BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

                    BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

                    BOOST_CHECK_EQUAL_RANGES(comms.out, getOutput(xfer));

                    BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

                    BOOST_CHECK_EQUAL(comms.ins.empty(), dnp.isTransactionComplete());

                    unsigned inbound_num = 0;

                    BOOST_TEST_CONTEXT("inbound number " << ++inbound_num)
                    {
                        for( const auto inbound : comms.ins )
                        {
                            BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

                            BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

                            if( inbound.size() < xfer.getInCountExpected() )
                            {
                                //  make sure we don't copy more than they expect
                                std::copy(inbound.begin(), inbound.end(),
                                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

                                xfer.setInCountActual(inbound.size());

                                BOOST_CHECK_EQUAL(1, dnp.decode(xfer, ClientErrors::ReadTimeout));

                                BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
                            }
                            else
                            {
                                //  make sure we don't copy more than they expect
                                std::copy(inbound.begin(), inbound.end(),
                                    stdext::make_checked_array_iterator(xfer.getInBuffer(), xfer.getInCountExpected()));

                                xfer.setInCountActual(inbound.size());

                                BOOST_CHECK_EQUAL(0, dnp.decode(xfer, ClientErrors::None));

                                BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
                            }
                        }
                    }
                }
            }
        }
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        const auto string_list = dnp.getInboundStrings();

        BOOST_REQUIRE_EQUAL(8, string_list.size());

        auto string_itr = string_list.cbegin();

        BOOST_CHECK_EQUAL(*string_itr++,
            "Successfully read internal indications");
        BOOST_CHECK_EQUAL(*string_itr++,
            "Internal indications:\n"
            "Time synchronization needed\n"
            "Device restart\n");
        BOOST_CHECK_EQUAL(*string_itr++,
            "Attempting to clear Device Restart bit");
        BOOST_CHECK_EQUAL(*string_itr++,
            "Internal indications:\n"
            "Time synchronization needed\n");
        BOOST_CHECK_EQUAL(*string_itr++,
            "Reset Device Restart Bit completed successfully");
        BOOST_CHECK_EQUAL(*string_itr++,
            "Time sync sent");
        BOOST_CHECK_EQUAL(*string_itr++,
            ""); // no internal indication
        BOOST_CHECK_EQUAL(*string_itr++,
            "Write Time completed successfully");

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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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

        BOOST_CHECK_EQUAL_RANGES(expected, getOutput(xfer));
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
