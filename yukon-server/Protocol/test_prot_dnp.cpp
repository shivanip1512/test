#include <boost/test/auto_unit_test.hpp>

#include "prot_dnp.h"

#include "boost_test_helpers.h"

using Cti::byte_buffer;

BOOST_AUTO_TEST_SUITE( test_prot_dnp )

BOOST_AUTO_TEST_CASE(test_prot_dnp_restart_bit)
{
    Cti::Protocol::DNPInterface dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(4, 3);
    dnp.setName("Test DNP device");
    dnp.setCommand(Cti::Protocol::DNPInterface::Command_Loopback);

    CtiXfer xfer;

    dnp.setConfigData( 2, false, false, false, false );

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        byte_buffer request;
        request << 0x05, 0x64, 0x08, 0xC4, 0x04, 0x00, 0x03, 0x00, 0xB4, 0xB8,
                   0xC0, 0xC1, 0x01, 0x23, 0x0B;

        //  copy them into int vectors so they display nicely
        std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());
        std::vector<int> expected(request.begin(), request.end());

        BOOST_CHECK_EQUAL_COLLECTIONS(
            output.begin(),
            output.end(),
            expected.begin(),
            expected.end());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }

    {
        {
            byte_buffer response;
            response << 0x05, 0x64, 0x0A, 0x44, 0x03, 0x00, 0x04, 0x00, 0x7C, 0xAE;

            std::copy(response.begin(), response.end(), xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(7, xfer.getInCountExpected());
    }

    {
        {
            byte_buffer response;
            response << 0xC1, 0xC1, 0x81, 0x90, 0x04, 0x0D, 0x14;

            std::copy(response.begin(), response.end(), xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        byte_buffer request;
        request << 0x05, 0x64, 0x0E, 0xC4, 0x04, 0x00, 0x03, 0x00, 0x6D, 0xD3,
                   0xC0, 0xC2, 0x02, 0x50, 0x01, 0x00, 0x07, 0x07, 0x00, 0x08,
                   0x65;

        //  copy them into int vectors so they display nicely
        std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());
        std::vector<int> expected(request.begin(), request.end());

        BOOST_CHECK_EQUAL_COLLECTIONS(
            output.begin(),
            output.end(),
            expected.begin(),
            expected.end());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }

    {
        {
            byte_buffer response;
            response << 0x05, 0x64, 0x0A, 0x44, 0x03, 0x00, 0x04, 0x00, 0x7C, 0xAE;

            std::copy(response.begin(), response.end(), xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(7, xfer.getInCountExpected());
    }

    {
        {
            byte_buffer response;
            response << 0xC2, 0xC2, 0x81, 0x10, 0x00, 0x11, 0xb9;

            std::copy(response.begin(), response.end(), xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        Cti::Protocol::Interface::pointlist_t point_list;

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

        Cti::Protocol::Interface::stringlist_t string_list;

        dnp.getInboundStrings(string_list);

        BOOST_CHECK_EQUAL(4, string_list.size());

        BOOST_CHECK_EQUAL(*string_list[0],
            "Loopback successful");
        BOOST_CHECK_EQUAL(*string_list[1],
            "Internal indications:\n"
            "Time synchronization needed\n"
            "Device restart\n"
            "Request parameters out of range\n");
        BOOST_CHECK_EQUAL(*string_list[2],
            "Attempting to clear Device Restart bit");
        BOOST_CHECK_EQUAL(*string_list[3],
            "Internal indications:\n"
            "Time synchronization needed\n");

        delete_container(point_list);
        delete_container(string_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_with_time)
{
    Cti::Protocol::DNPInterface dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(Cti::Protocol::DNPInterface::Command_Class1230Read_WithTime);

    CtiXfer xfer;

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        byte_buffer request;
        request << 0x05, 0x64, 0x17, 0xc4, 0xd2, 0x04, 0x01, 0x00, 0x85, 0x40,
                   0xc0, 0xc1, 0x01, 0x32, 0x01, 0x06, 0x3c, 0x02, 0x06, 0x3c,
                   0x03, 0x06, 0x3c, 0x04, 0x06, 0x3c, 0xfe, 0xe0, 0x01, 0x06,
                   0x75, 0xe1;

        //  copy them into int vectors so they display nicely
        std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());
        std::vector<int> expected(request.begin(), request.end());

        BOOST_CHECK_EQUAL_COLLECTIONS(
            output.begin(),
            output.end(),
            expected.begin(),
            expected.end());
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            byte_buffer response;
            response << 0x05, 0x64, 0x27, 0x44, 0x01, 0x00, 0xd2, 0x04, 0x79, 0x6f;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(40, xfer.getInCountExpected());
    }
    {
        {
            byte_buffer response;
            response << 0xc0, 0xca, 0x81, 0x0f, 0x00, 0x1e, 0x01, 0x18, 0x01, 0x00,
                        0x03, 0x00, 0x3f, 0x01, 0x00, 0x00, 0x40, 0xbe, 0x01, 0x02,
                        0x18, 0x01, 0x00, 0x01, 0x00, 0x14, 0x01, 0x18, 0x01, 0x00,
                        0x00, 0x00, 0x13, 0x00, 0x95, 0x4f, 0x00, 0x00, 0xff, 0xff;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        Cti::Protocol::Interface::pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_CHECK_EQUAL(4, point_list.size());

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

        Cti::Protocol::Interface::stringlist_t string_list;

        dnp.getInboundStrings(string_list);

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(*string_list[0],
            "Internal indications:\n"
            "Broadcast message received\n"
            "Class 1 data available\n"
            "Class 2 data available\n"
            "Class 3 data available\n");
        BOOST_CHECK_EQUAL(*string_list[1],
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
        delete_container(string_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_with_time_no_ack_required)
{
    Cti::Protocol::DNPInterface dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(Cti::Protocol::DNPInterface::Command_Class1230Read_WithTime);

    CtiXfer xfer;

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        byte_buffer request;
        request << 0x05, 0x64, 0x17, 0xc4, 0xd2, 0x04, 0x01, 0x00, 0x85, 0x40,
                   0xc0, 0xc1, 0x01, 0x32, 0x01, 0x06, 0x3c, 0x02, 0x06, 0x3c,
                   0x03, 0x06, 0x3c, 0x04, 0x06, 0x3c, 0xfe, 0xe0, 0x01, 0x06,
                   0x75, 0xe1;

        //  copy them into int vectors so they display nicely
        std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());
        std::vector<int> expected(request.begin(), request.end());

        BOOST_CHECK_EQUAL_COLLECTIONS(
            output.begin(),
            output.end(),
            expected.begin(),
            expected.end());
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            byte_buffer response;
            response << 0x05, 0x64, 0x27, 0x44, 0x01, 0x00, 0xd2, 0x04, 0x79, 0x6f;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(40, xfer.getInCountExpected());
    }
    {
        {
            byte_buffer response;
            response << 0xc0, 0xca, 0x81, 0x0f, 0x00, 0x1e, 0x01, 0x18, 0x01, 0x00,
                        0x03, 0x00, 0x3f, 0x01, 0x00, 0x00, 0x40, 0xbe, 0x01, 0x02,
                        0x18, 0x01, 0x00, 0x01, 0x00, 0x14, 0x01, 0x18, 0x01, 0x00,
                        0x00, 0x00, 0x13, 0x00, 0x95, 0x4f, 0x00, 0x00, 0xff, 0xff;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        Cti::Protocol::Interface::pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_CHECK_EQUAL(4, point_list.size());

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

        Cti::Protocol::Interface::stringlist_t string_list;

        dnp.getInboundStrings(string_list);

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(*string_list[0],
            "Internal indications:\n"
            "Broadcast message received\n"
            "Class 1 data available\n"
            "Class 2 data available\n"
            "Class 3 data available\n");
        BOOST_CHECK_EQUAL(*string_list[1],
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
        delete_container(string_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_with_time_ack_required)
{
    Cti::Protocol::DNPInterface dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(Cti::Protocol::DNPInterface::Command_Class1230Read_WithTime);

    CtiXfer xfer;

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        byte_buffer request;
        request << 0x05, 0x64, 0x17, 0xc4, 0xd2, 0x04, 0x01, 0x00, 0x85, 0x40,
                   0xc0, 0xc1, 0x01, 0x32, 0x01, 0x06, 0x3c, 0x02, 0x06, 0x3c,
                   0x03, 0x06, 0x3c, 0x04, 0x06, 0x3c, 0xfe, 0xe0, 0x01, 0x06,
                   0x75, 0xe1;

        //  copy them into int vectors so they display nicely
        std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());
        std::vector<int> expected(request.begin(), request.end());

        BOOST_CHECK_EQUAL_COLLECTIONS(
            output.begin(),
            output.end(),
            expected.begin(),
            expected.end());
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            byte_buffer response;
            response << 0x05, 0x64, 0x27, 0x44, 0x01, 0x00, 0xd2, 0x04, 0x79, 0x6f;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(40, xfer.getInCountExpected());
    }
    {
        {
            byte_buffer response;
            response << 0xc0, 0xea, 0x81, 0x0f, 0x00, 0x1e, 0x01, 0x18, 0x01, 0x00,
                        0x03, 0x00, 0x3f, 0x01, 0x00, 0x00, 0x9e, 0xb9, 0x01, 0x02,
                        0x18, 0x01, 0x00, 0x01, 0x00, 0x14, 0x01, 0x18, 0x01, 0x00,
                        0x00, 0x00, 0x13, 0x00, 0x95, 0x4f, 0x00, 0x00, 0xff, 0xff;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const unsigned char request[] = "\x05\x64\x08\xc4\xd2\x04\x01\x00\xa6\x7c"
                                        "\xc0\xca\x00\x42\xe2";

        const unsigned request_len = sizeof(request) / sizeof(*request) - 1;  //  trim off the implicit null

        //  copy them into int vectors so they display nicely
        std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());
        std::vector<int> expected(request, request + request_len);

        BOOST_CHECK_EQUAL_COLLECTIONS(
            output.begin(),
            output.end(),
            expected.begin(),
            expected.end());
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        Cti::Protocol::Interface::pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_CHECK_EQUAL(4, point_list.size());

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

        Cti::Protocol::Interface::stringlist_t string_list;

        dnp.getInboundStrings(string_list);

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(*string_list[0],
            "Internal indications:\n"
            "Broadcast message received\n"
            "Class 1 data available\n"
            "Class 2 data available\n"
            "Class 3 data available\n");
        BOOST_CHECK_EQUAL(*string_list[1],
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
        delete_container(string_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_with_time_interrupting_unsolicited)
{
    Cti::Protocol::DNPInterface dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(Cti::Protocol::DNPInterface::Command_Class1230Read_WithTime);

    CtiXfer xfer;

    //  Initial scan request
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        byte_buffer request;
        request << 0x05, 0x64, 0x17, 0xc4, 0xd2, 0x04, 0x01, 0x00, 0x85, 0x40,
                   0xc0, 0xc1, 0x01, 0x32, 0x01, 0x06, 0x3c, 0x02, 0x06, 0x3c,
                   0x03, 0x06, 0x3c, 0x04, 0x06, 0x3c, 0xfe, 0xe0, 0x01, 0x06,
                   0x75, 0xe1;

        //  copy them into int vectors so they display nicely
        std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());
        std::vector<int> expected(request.begin(), request.end());

        BOOST_CHECK_EQUAL_COLLECTIONS(
            output.begin(),
            output.end(),
            expected.begin(),
            expected.end());
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

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
            byte_buffer response;
            response << 0x05, 0x64, 0x27, 0x44, 0x01, 0x00, 0xd2, 0x04, 0x79, 0x6f;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

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
            byte_buffer response;
            response << 0xc0, 0xf5, 0x82, 0x0f, 0x00, 0x1e, 0x01, 0x18, 0x01, 0x00,
                        0x04, 0x00, 0x3f, 0x01, 0x00, 0x00, 0x25, 0xc7, 0x01, 0x02,
                        0x18, 0x01, 0x00, 0x02, 0x00, 0x14, 0x01, 0x18, 0x01, 0x00,
                        0x10, 0x00, 0x13, 0x00, 0x20, 0xd3, 0x00, 0x00, 0xff, 0xff;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  Ack the unsolicited
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        byte_buffer request;
        request << 0x05, 0x64, 0x08, 0xc4, 0xd2, 0x04, 0x01, 0x00, 0xa6, 0x7c,
                   0xc0, 0xd5, 0x00, 0x9f, 0xd5;

        //  copy them into int vectors so they display nicely
        std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());
        std::vector<int> expected(request.begin(), request.end());

        BOOST_CHECK_EQUAL_COLLECTIONS(
            output.begin(),
            output.end(),
            expected.begin(),
            expected.end());
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

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
            byte_buffer response;
            response << 0x05, 0x64, 0x27, 0x44, 0x01, 0x00, 0xd2, 0x04, 0x79, 0x6f;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

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
            byte_buffer response;
            response << 0xc0, 0xea, 0x81, 0x0f, 0x00, 0x1e, 0x01, 0x18, 0x01, 0x00,
                        0x03, 0x00, 0x3f, 0x01, 0x00, 0x00, 0x9e, 0xb9, 0x01, 0x02,
                        0x18, 0x01, 0x00, 0x01, 0x00, 0x14, 0x01, 0x18, 0x01, 0x00,
                        0x00, 0x00, 0x13, 0x00, 0x95, 0x4f, 0x00, 0x00, 0xff, 0xff;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  send the ack to the scan response, and we're done
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        byte_buffer request;
        request << 0x05, 0x64, 0x08, 0xc4, 0xd2, 0x04, 0x01, 0x00, 0xa6, 0x7c,
                   0xc0, 0xca, 0x00, 0x42, 0xe2;

        //  copy them into int vectors so they display nicely
        std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());
        std::vector<int> expected(request.begin(), request.end());

        BOOST_CHECK_EQUAL_COLLECTIONS(
            output.begin(),
            output.end(),
            expected.begin(),
            expected.end());
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        Cti::Protocol::Interface::pointlist_t point_list;

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

        Cti::Protocol::Interface::stringlist_t string_list;

        dnp.getInboundStrings(string_list);

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(*string_list[0],
            "Internal indications:\n"
            "Broadcast message received\n"
            "Class 1 data available\n"
            "Class 2 data available\n"
            "Class 3 data available\n");
        BOOST_CHECK_EQUAL(*string_list[1],
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
        delete_container(string_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan)
{
    Cti::Protocol::DNPInterface dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(Cti::Protocol::DNPInterface::Command_Class1230Read);

    CtiXfer xfer;

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        byte_buffer request;
        request << 0x05, 0x64, 0x14, 0xc4, 0xd2, 0x04, 0x01, 0x00, 0xd5, 0xd3,
                   0xc0, 0xc1, 0x01, 0x3c, 0x02, 0x06, 0x3c, 0x03, 0x06, 0x3c,
                   0x04, 0x06, 0x3c, 0x01, 0x06, 0x7a, 0x6f;

        //  copy them into int vectors so they display nicely
        std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());
        std::vector<int> expected(request.begin(), request.end());

        BOOST_CHECK_EQUAL_COLLECTIONS(
            output.begin(),
            output.end(),
            expected.begin(),
            expected.end());
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            byte_buffer response;
            response << 0x05, 0x64, 0x27, 0x44, 0x01, 0x00, 0xd2, 0x04, 0x79, 0x6f;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(40, xfer.getInCountExpected());
    }
    {
        {
            byte_buffer response;
            response << 0xc0, 0xca, 0x81, 0x0f, 0x00, 0x1e, 0x01, 0x18, 0x01, 0x00,
                        0x03, 0x00, 0x3f, 0x01, 0x00, 0x00, 0x40, 0xbe, 0x01, 0x02,
                        0x18, 0x01, 0x00, 0x01, 0x00, 0x14, 0x01, 0x18, 0x01, 0x00,
                        0x00, 0x00, 0x13, 0x00, 0x95, 0x4f, 0x00, 0x00, 0xff, 0xff;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        Cti::Protocol::Interface::pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_CHECK_EQUAL(4, point_list.size());

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

        Cti::Protocol::Interface::stringlist_t string_list;

        dnp.getInboundStrings(string_list);

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(*string_list[0],
            "Internal indications:\n"
            "Broadcast message received\n"
            "Class 1 data available\n"
            "Class 2 data available\n"
            "Class 3 data available\n");
        BOOST_CHECK_EQUAL(*string_list[1],
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
        delete_container(string_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_no_ack_required)
{
    Cti::Protocol::DNPInterface dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(Cti::Protocol::DNPInterface::Command_Class1230Read);

    CtiXfer xfer;

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        byte_buffer request;
        request << 0x05, 0x64, 0x14, 0xc4, 0xd2, 0x04, 0x01, 0x00, 0xd5, 0xd3,
                   0xc0, 0xc1, 0x01, 0x3c, 0x02, 0x06, 0x3c, 0x03, 0x06, 0x3c,
                   0x04, 0x06, 0x3c, 0x01, 0x06, 0x7a, 0x6f;

        //  copy them into int vectors so they display nicely
        std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());
        std::vector<int> expected(request.begin(), request.end());

        BOOST_CHECK_EQUAL_COLLECTIONS(
            output.begin(),
            output.end(),
            expected.begin(),
            expected.end());
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            byte_buffer response;
            response << 0x05, 0x64, 0x27, 0x44, 0x01, 0x00, 0xd2, 0x04, 0x79, 0x6f;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(40, xfer.getInCountExpected());
    }
    {
        {
            byte_buffer response;
            response << 0xc0, 0xca, 0x81, 0x0f, 0x00, 0x1e, 0x01, 0x18, 0x01, 0x00,
                        0x03, 0x00, 0x3f, 0x01, 0x00, 0x00, 0x40, 0xbe, 0x01, 0x02,
                        0x18, 0x01, 0x00, 0x01, 0x00, 0x14, 0x01, 0x18, 0x01, 0x00,
                        0x00, 0x00, 0x13, 0x00, 0x95, 0x4f, 0x00, 0x00, 0xff, 0xff;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        Cti::Protocol::Interface::pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_CHECK_EQUAL(4, point_list.size());

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

        Cti::Protocol::Interface::stringlist_t string_list;

        dnp.getInboundStrings(string_list);

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(*string_list[0],
            "Internal indications:\n"
            "Broadcast message received\n"
            "Class 1 data available\n"
            "Class 2 data available\n"
            "Class 3 data available\n");
        BOOST_CHECK_EQUAL(*string_list[1],
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
        delete_container(string_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_ack_required)
{
    Cti::Protocol::DNPInterface dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(Cti::Protocol::DNPInterface::Command_Class1230Read);

    CtiXfer xfer;

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        byte_buffer request;
        request << 0x05, 0x64, 0x14, 0xc4, 0xd2, 0x04, 0x01, 0x00, 0xd5, 0xd3,
                   0xc0, 0xc1, 0x01, 0x3c, 0x02, 0x06, 0x3c, 0x03, 0x06, 0x3c,
                   0x04, 0x06, 0x3c, 0x01, 0x06, 0x7a, 0x6f;

        //  copy them into int vectors so they display nicely
        std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());
        std::vector<int> expected(request.begin(), request.end());

        BOOST_CHECK_EQUAL_COLLECTIONS(
            output.begin(),
            output.end(),
            expected.begin(),
            expected.end());
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            byte_buffer response;
            response << 0x05, 0x64, 0x27, 0x44, 0x01, 0x00, 0xd2, 0x04, 0x79, 0x6f;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(40, xfer.getInCountExpected());
    }
    {
        {
            byte_buffer response;
            response << 0xc0, 0xea, 0x81, 0x0f, 0x00, 0x1e, 0x01, 0x18, 0x01, 0x00,
                        0x03, 0x00, 0x3f, 0x01, 0x00, 0x00, 0x9e, 0xb9, 0x01, 0x02,
                        0x18, 0x01, 0x00, 0x01, 0x00, 0x14, 0x01, 0x18, 0x01, 0x00,
                        0x00, 0x00, 0x13, 0x00, 0x95, 0x4f, 0x00, 0x00, 0xff, 0xff;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const unsigned char request[] = "\x05\x64\x08\xc4\xd2\x04\x01\x00\xa6\x7c"
                                        "\xc0\xca\x00\x42\xe2";

        const unsigned request_len = sizeof(request) / sizeof(*request) - 1;  //  trim off the implicit null

        //  copy them into int vectors so they display nicely
        std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());
        std::vector<int> expected(request, request + request_len);

        BOOST_CHECK_EQUAL_COLLECTIONS(
            output.begin(),
            output.end(),
            expected.begin(),
            expected.end());
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        Cti::Protocol::Interface::pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_CHECK_EQUAL(4, point_list.size());

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

        Cti::Protocol::Interface::stringlist_t string_list;

        dnp.getInboundStrings(string_list);

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(*string_list[0],
            "Internal indications:\n"
            "Broadcast message received\n"
            "Class 1 data available\n"
            "Class 2 data available\n"
            "Class 3 data available\n");
        BOOST_CHECK_EQUAL(*string_list[1],
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
        delete_container(string_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_integrity_scan_interrupting_unsolicited)
{
    Cti::Protocol::DNPInterface dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(Cti::Protocol::DNPInterface::Command_Class1230Read);

    CtiXfer xfer;

    //  Initial scan request
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        byte_buffer request;
        request << 0x05, 0x64, 0x14, 0xc4, 0xd2, 0x04, 0x01, 0x00, 0xd5, 0xd3,
                   0xc0, 0xc1, 0x01, 0x3c, 0x02, 0x06, 0x3c, 0x03, 0x06, 0x3c,
                   0x04, 0x06, 0x3c, 0x01, 0x06, 0x7a, 0x6f;

        //  copy them into int vectors so they display nicely
        std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());
        std::vector<int> expected(request.begin(), request.end());

        BOOST_CHECK_EQUAL_COLLECTIONS(
            output.begin(),
            output.end(),
            expected.begin(),
            expected.end());
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

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
            byte_buffer response;
            response << 0x05, 0x64, 0x27, 0x44, 0x01, 0x00, 0xd2, 0x04, 0x79, 0x6f;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

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
            byte_buffer response;
            response << 0xc0, 0xf5, 0x82, 0x0f, 0x00, 0x1e, 0x01, 0x18, 0x01, 0x00,
                        0x04, 0x00, 0x3f, 0x01, 0x00, 0x00, 0x25, 0xc7, 0x01, 0x02,
                        0x18, 0x01, 0x00, 0x02, 0x00, 0x14, 0x01, 0x18, 0x01, 0x00,
                        0x10, 0x00, 0x13, 0x00, 0x20, 0xd3, 0x00, 0x00, 0xff, 0xff;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  Ack the unsolicited
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        byte_buffer request;
        request << 0x05, 0x64, 0x08, 0xc4, 0xd2, 0x04, 0x01, 0x00, 0xa6, 0x7c,
                   0xc0, 0xd5, 0x00, 0x9f, 0xd5;

        //  copy them into int vectors so they display nicely
        std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());
        std::vector<int> expected(request.begin(), request.end());

        BOOST_CHECK_EQUAL_COLLECTIONS(
            output.begin(),
            output.end(),
            expected.begin(),
            expected.end());
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

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
            byte_buffer response;
            response << 0x05, 0x64, 0x27, 0x44, 0x01, 0x00, 0xd2, 0x04, 0x79, 0x6f;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

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
            byte_buffer response;
            response << 0xc0, 0xea, 0x81, 0x0f, 0x00, 0x1e, 0x01, 0x18, 0x01, 0x00,
                        0x03, 0x00, 0x3f, 0x01, 0x00, 0x00, 0x9e, 0xb9, 0x01, 0x02,
                        0x18, 0x01, 0x00, 0x01, 0x00, 0x14, 0x01, 0x18, 0x01, 0x00,
                        0x00, 0x00, 0x13, 0x00, 0x95, 0x4f, 0x00, 0x00, 0xff, 0xff;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  send the ack to the scan response, and we're done
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        byte_buffer request;
        request << 0x05, 0x64, 0x08, 0xc4, 0xd2, 0x04, 0x01, 0x00, 0xa6, 0x7c,
                   0xc0, 0xca, 0x00, 0x42, 0xe2;

        //  copy them into int vectors so they display nicely
        std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());
        std::vector<int> expected(request.begin(), request.end());

        BOOST_CHECK_EQUAL_COLLECTIONS(
            output.begin(),
            output.end(),
            expected.begin(),
            expected.end());
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        Cti::Protocol::Interface::pointlist_t point_list;

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

        Cti::Protocol::Interface::stringlist_t string_list;

        dnp.getInboundStrings(string_list);

        BOOST_CHECK_EQUAL(2, string_list.size());

        BOOST_CHECK_EQUAL(*string_list[0],
            "Internal indications:\n"
            "Broadcast message received\n"
            "Class 1 data available\n"
            "Class 2 data available\n"
            "Class 3 data available\n");
        BOOST_CHECK_EQUAL(*string_list[1],
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
        delete_container(string_list);
    }
}

BOOST_AUTO_TEST_CASE(test_prot_dnp_unsolicited)
{
    Cti::Protocol::DNPInterface dnp;

    BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

    dnp.setAddresses(1234, 1);
    dnp.setName("Test DNP device");
    dnp.setCommand(Cti::Protocol::DNPInterface::Command_UnsolicitedInbound);

    CtiXfer xfer;

    //  Grab the unsolicited header
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
    }
    {
        {
            byte_buffer response;
            response << 0x05, 0x64, 0x27, 0x44, 0x01, 0x00, 0xd2, 0x04, 0x79, 0x6f;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

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
            byte_buffer response;
            response << 0xc0, 0xf5, 0x82, 0x0f, 0x00, 0x1e, 0x01, 0x18, 0x01, 0x00,
                        0x04, 0x00, 0x3f, 0x01, 0x00, 0x00, 0x25, 0xc7, 0x01, 0x02,
                        0x18, 0x01, 0x00, 0x02, 0x00, 0x14, 0x01, 0x18, 0x01, 0x00,
                        0x10, 0x00, 0x13, 0x00, 0x20, 0xd3, 0x00, 0x00, 0xff, 0xff;

            response.copy_to(xfer.getInBuffer());

            xfer.setInCountActual(response.size());
        }

        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());
    }

    //  Ack the unsolicited
    {
        BOOST_CHECK_EQUAL(0, dnp.generate(xfer));

        BOOST_CHECK_EQUAL(false, dnp.isTransactionComplete());

        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        byte_buffer request;
        request << 0x05, 0x64, 0x08, 0xc4, 0xd2, 0x04, 0x01, 0x00, 0xa6, 0x7c,
                   0xc0, 0xd5, 0x00, 0x9f, 0xd5;

        //  copy them into int vectors so they display nicely
        std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());
        std::vector<int> expected(request.begin(), request.end());

        BOOST_CHECK_EQUAL_COLLECTIONS(
            output.begin(),
            output.end(),
            expected.begin(),
            expected.end());
    }
    {
        BOOST_CHECK_EQUAL(0, dnp.decode(xfer, 0));

        BOOST_CHECK_EQUAL(true, dnp.isTransactionComplete());

        Cti::Protocol::Interface::pointlist_t point_list;

        dnp.getInboundPoints(point_list);

        BOOST_CHECK_EQUAL(4, point_list.size());

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

        Cti::Protocol::Interface::stringlist_t string_list;

        dnp.getInboundStrings(string_list);

        BOOST_CHECK_EQUAL(0, string_list.size());

        delete_container(point_list);
        delete_container(string_list);
    }
}


BOOST_AUTO_TEST_SUITE_END()
