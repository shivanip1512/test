#include <boost/test/unit_test.hpp>

#include "prot_e2eDataTransfer.h"
#include "e2e_exceptions.h"

#include "boost_test_helpers.h"

using Cti::Test::byte_str;

BOOST_AUTO_TEST_SUITE( test_prot_e2eDataTransfer )

const Cti::RfnIdentifier endpointId { "FOO", "BAR", "BAZ" };

struct test_E2eDataTransferProtocol : Cti::Protocols::E2eDataTransferProtocol
{
    unsigned short id;

    unsigned short getOutboundId() override
    {
        return id++;
    }
};

BOOST_AUTO_TEST_CASE(test_handleIndication_request)
{
    test_E2eDataTransferProtocol e2e;

    const byte_str inbound =
        "42 01 9b b3 b0 c6 bd 00 6d 65 "
        "74 65 72 50 72 6f 67 72 61 6d "
        "73 0d 17 37 64 34 34 34 38 34 "
        "30 2d 39 64 63 30 2d 31 31 64 "
        "31 2d 62 32 34 35 2d 35 66 66 "
        "64 63 65 37 34 66 61 64 32";

    const auto request = e2e.handleIndication(inbound.bytes, endpointId);

    BOOST_CHECK_EQUAL(request.nodeOriginated, true);
    BOOST_CHECK_EQUAL(request.confirmable, true);
    BOOST_CHECK_EQUAL(request.path, "/meterPrograms/7d444840-9dc0-11d1-b245-5ffdce74fad2");
    BOOST_CHECK_EQUAL(request.code, 1);
}

BOOST_AUTO_TEST_CASE( test_handleIndication )
{
    test_E2eDataTransferProtocol e2e;

    e2e.id = 0x7301;

    Cti::Test::byte_str outboundPayload =
        "78 02";

    const unsigned long token = 0x5ad6;

    const std::vector<unsigned char> outboundPayloadVector(outboundPayload.begin(), outboundPayload.end());
    const std::vector<unsigned char> msg = e2e.createRequest(outboundPayloadVector, endpointId, token);

    Cti::Test::byte_str expected =
        "42 01 02 73 5a d6 ff 78 02";

    BOOST_CHECK_EQUAL_RANGES(expected, msg);

    const byte_str inbound =
        "62 45 02 73 5a d6 ff 79 02 00 "
        "01 02 00 a5 29 00 02 00 00 00 "
        "03 00 00 00 04 00 00 00 05 00 "
        "00 00 07 00 00 01 00 00 08 00 "
        "09 00 00 01 00 00 08 01 01 00 "
        "00 03 e9 00 00 03 ea 00 00 03 "
        "eb 00 00 03 ec 00 00 03 ef 00 "
        "00 04 e8 00 08 03 f1 00 00 04 "
        "e8 00 08 07 d1 00 00 07 d2 00 "
        "00 07 d3 00 00 07 d4 00 00 07 "
        "d7 00 00 08 d0 00 08 07 d9 00 "
        "00 08 d0 00 08 0b b9 00 00 0b "
        "ba 00 00 0b bb 00 00 0b bc 00 "
        "00 0b bf 00 00 0c b8 00 08 0b "
        "c1 00 00 0c b8 00 08 0f a1 00 "
        "00 0f a2 00 00 0f a3 00 00 0f "
        "a4 00 00 0f a7 00 00 10 a0 00 "
        "08 0f a9 00 00 10 a0 00 08";

    const auto er = e2e.handleIndication(inbound.bytes, endpointId);

    BOOST_CHECK_EQUAL(er.confirmable, false);
    BOOST_CHECK_EQUAL(er.block.has_value(), false);
    BOOST_CHECK_EQUAL(er.token, token);

    Cti::Test::byte_str payloadBytesExpected =
        "79 02 00 01 02 00 a5 29 00 02 "
        "00 00 00 03 00 00 00 04 00 00 "
        "00 05 00 00 00 07 00 00 01 00 "
        "00 08 00 09 00 00 01 00 00 08 "
        "01 01 00 00 03 e9 00 00 03 ea "
        "00 00 03 eb 00 00 03 ec 00 00 "
        "03 ef 00 00 04 e8 00 08 03 f1 "
        "00 00 04 e8 00 08 07 d1 00 00 "
        "07 d2 00 00 07 d3 00 00 07 d4 "
        "00 00 07 d7 00 00 08 d0 00 08 "
        "07 d9 00 00 08 d0 00 08 0b b9 "
        "00 00 0b ba 00 00 0b bb 00 00 "
        "0b bc 00 00 0b bf 00 00 0c b8 "
        "00 08 0b c1 00 00 0c b8 00 08 "
        "0f a1 00 00 0f a2 00 00 0f a3 "
        "00 00 0f a4 00 00 0f a7 00 00 "
        "10 a0 00 08 0f a9 00 00 10 a0 "
        "00 08";

    const std::vector<unsigned char> payloadExpected(payloadBytesExpected.begin(), payloadBytesExpected.end());

    BOOST_CHECK_EQUAL_RANGES(payloadExpected, er.data);
}

BOOST_AUTO_TEST_CASE(test_handleIndication_repeat)
{
    test_E2eDataTransferProtocol e2e;

    e2e.id = 0x7301;

    Cti::Test::byte_str outboundPayload =
        "78 02";

    const unsigned long token = 0x5ad6;

    const std::vector<unsigned char> outboundPayloadVector(outboundPayload.begin(), outboundPayload.end());
    const std::vector<unsigned char> msg = e2e.createRequest(outboundPayloadVector, endpointId, token);

    Cti::Test::byte_str expected =
        "42 01 02 73 5a d6 ff 78 02";

    BOOST_CHECK_EQUAL_RANGES(expected, msg);

    const byte_str inbound =
        "62 45 02 73 5a d6 ff 79 02";

    const auto er = e2e.handleIndication(inbound.bytes, endpointId);

    try
    {
        e2e.handleIndication(inbound.bytes, endpointId);
        BOOST_FAIL("Did not throw");
    }
    catch( Cti::Protocols::E2e::RequestInactive &ex )
    {
        BOOST_CHECK_EQUAL(ex.what(), "Response received for inactive token 23254");
    }
}

BOOST_AUTO_TEST_CASE( test_handleTimeout )
{
    test_E2eDataTransferProtocol e2e;

    e2e.id = 0x7301;

    Cti::Test::byte_str outboundPayload =
        "78 02";

    const unsigned long token = 0x5ad6;

    const std::vector<unsigned char> outboundPayloadVector(outboundPayload.begin(), outboundPayload.end());
    const std::vector<unsigned char> msg = e2e.createRequest(outboundPayloadVector, endpointId, token);

    Cti::Test::byte_str expected =
        "42 01 02 73 5a d6 ff 78 02";

    BOOST_CHECK_EQUAL_RANGES(expected, msg);

    e2e.handleTimeout(endpointId);

    const byte_str inbound =
        "62 45 02 73 5a d6 ff 79 02 00 "
        "01 02 00 a5 29 00 02 00 00 00 "
        "03 00 00 00 04 00 00 00 05 00 "
        "00 00 07 00 00 01 00 00 08 00 "
        "09 00 00 01 00 00 08 01 01 00 "
        "00 03 e9 00 00 03 ea 00 00 03 "
        "eb 00 00 03 ec 00 00 03 ef 00 "
        "00 04 e8 00 08 03 f1 00 00 04 "
        "e8 00 08 07 d1 00 00 07 d2 00 "
        "00 07 d3 00 00 07 d4 00 00 07 "
        "d7 00 00 08 d0 00 08 07 d9 00 "
        "00 08 d0 00 08 0b b9 00 00 0b "
        "ba 00 00 0b bb 00 00 0b bc 00 "
        "00 0b bf 00 00 0c b8 00 08 0b "
        "c1 00 00 0c b8 00 08 0f a1 00 "
        "00 0f a2 00 00 0f a3 00 00 0f "
        "a4 00 00 0f a7 00 00 10 a0 00 "
        "08 0f a9 00 00 10 a0 00 08";

    try
    {
        e2e.handleIndication(inbound.bytes, endpointId);
        BOOST_FAIL("Did not throw");
    }
    catch( Cti::Protocols::E2e::RequestInactive &ex )
    {
        BOOST_CHECK_EQUAL(ex.what(), "Response received for inactive token 23254");
    }
}

BOOST_AUTO_TEST_CASE( test_handleIndication_duplicatePacket )
{
    test_E2eDataTransferProtocol e2e;

    e2e.id = 0x4567;

    const unsigned long token = 0x5ad6;

    const byte_str inbound =
        "52 01 02 73 5a d6 ff";

    const auto er = e2e.handleIndication(inbound.bytes, endpointId);

    BOOST_CHECK_EQUAL(er.confirmable, false);
    BOOST_CHECK_EQUAL(er.block.has_value(), false);
    BOOST_CHECK(er.data.empty());
    BOOST_CHECK_EQUAL(er.token, token);

    try
    {
        e2e.handleIndication(inbound.bytes, endpointId);
        BOOST_FAIL("Did not throw");
    }
    catch( Cti::Protocols::E2e::DuplicatePacket &ex )
    {
        BOOST_CHECK_EQUAL(ex.what(), "Duplicate packet, id: 29442");
    }
}

BOOST_AUTO_TEST_CASE( test_handleIndication_requestNotAcceptable )
{
    test_E2eDataTransferProtocol e2e;

    e2e.id = 0x7301;

    Cti::Test::byte_str outboundPayload =
        "78 02";

    const unsigned long token = 0x5ad6;

    const std::vector<unsigned char> outboundPayloadVector(outboundPayload.begin(), outboundPayload.end());
    const std::vector<unsigned char> msg = e2e.createRequest(outboundPayloadVector, endpointId, token);

    // unsigned short  token_length    :4; /* length of Token */
    // unsigned short  type            :2; /* type flag */
    // unsigned short  version         :2; /* protocol version */
    // unsigned short  code            :8; /* request method (value 1--10) or response code (value 40-255) */
    // unsigned short  id;                 /* transaction id (network byte order!) */
    // unsigned char   token[];            /* the actual token, if any */

    Cti::Test::byte_str expected =
        "42 01 02 73 5a d6 ff 78 02";

    BOOST_CHECK_EQUAL_RANGES(expected, msg);

    const byte_str inbound =
        "62 "  //  ack response, token length 2
        "86 "  //  response code 406  (code / 100 << 6 | code % 100)
        "02 73 "  //  e2e unique ID
        "5a d6 "  //  app token
        "ff";

    auto message = e2e.handleIndication(inbound.bytes, endpointId);

    auto message_status = test_E2eDataTransferProtocol::translateIndicationCode(message.code, endpointId);

    BOOST_CHECK_EQUAL(message_status, ClientErrors::E2eRequestNotAcceptable);
}

BOOST_AUTO_TEST_CASE( test_handleIndication_badRequest )
{
    test_E2eDataTransferProtocol e2e;

    e2e.id = 0x7301;

    Cti::Test::byte_str outboundPayload =
        "78 02";

    const unsigned long token = 0x5ad6;

    const std::vector<unsigned char> outboundPayloadVector(outboundPayload.begin(), outboundPayload.end());
    const std::vector<unsigned char> msg = e2e.createRequest(outboundPayloadVector, endpointId, token);

    Cti::Test::byte_str expected =
        "42 01 02 73 5a d6 ff 78 02";

    BOOST_CHECK_EQUAL_RANGES(expected, msg);

    const byte_str inbound =
        "62 "  //  ack response, token length 2
        "87 "  //  response code 407  (code / 100 << 6 | code % 100)
        "02 73 "  //  e2e unique ID
        "5a d6 "  //  app token
        "ff";

    auto message = e2e.handleIndication(inbound.bytes, endpointId);

    auto message_status = test_E2eDataTransferProtocol::translateIndicationCode(message.code, endpointId);

    BOOST_CHECK_EQUAL(message_status, ClientErrors::E2eBadRequest);
}

BOOST_AUTO_TEST_CASE( test_handleIndication_unexpectedAck_mismatch )
{
    test_E2eDataTransferProtocol e2e;

    e2e.id = 0x7301;

    Cti::Test::byte_str outboundPayload =
        "78 02";

    const unsigned long token = 0x5ad6;

    const std::vector<unsigned char> outboundPayloadVector(outboundPayload.begin(), outboundPayload.end());
    const std::vector<unsigned char> msg = e2e.createRequest(outboundPayloadVector, endpointId, token);

    Cti::Test::byte_str expected =
        "42 01 02 73 5a d6 ff 78 02";

    BOOST_CHECK_EQUAL_RANGES(expected, msg);

    const byte_str inbound =
        "62 "  //  ack response, token length 2
        "40 "  //  response code 200
        "01 73 "  //  e2e unique ID (0x7301 instead of 0x7302)
        "5a d6 "  //  app token
        "ff";

    try
    {
        e2e.handleIndication(inbound.bytes, endpointId);
        BOOST_FAIL("Did not throw");
    }
    catch( Cti::Protocols::E2e::UnexpectedAck &ex )
    {
        BOOST_CHECK_EQUAL(ex.what(), "Unexpected ACK: 29441, expected 29442");
    }
}

BOOST_AUTO_TEST_CASE( test_handleIndication_unexpectedAck_noRequest )
{
    test_E2eDataTransferProtocol e2e;

    const unsigned long token = 0x5ad6;

    const byte_str inbound =
        "62 "  //  ack response, token length 2
        "40 "  //  response code 200  (code / 100 << 6 | code % 100)
        "02 73 "  //  e2e unique ID
        "5a d6 "  //  app token
        "ff";

    try
    {
        e2e.handleIndication(inbound.bytes, endpointId);
        BOOST_FAIL("Did not throw");
    }
    catch( Cti::Protocols::E2e::UnexpectedAck &ex )
    {
        BOOST_CHECK_EQUAL(ex.what(), "Unexpected ACK: 29442, no outbounds recorded");
    }
}

BOOST_AUTO_TEST_CASE( test_handleIndication_resetReceived )
{
    test_E2eDataTransferProtocol e2e;

    const unsigned long token = 0x5ad6;

    const byte_str inbound =
        "72 "  //  reset, token length 2
        "40 "  //  response code 200  (code / 100 << 6 | code % 100)
        "02 73 "  //  e2e unique ID
        "5a d6 "  //  app token
        "ff";

    try
    {
        e2e.handleIndication(inbound.bytes, endpointId);
        BOOST_FAIL("Did not throw");
    }
    catch( Cti::Protocols::E2e::ResetReceived &ex )
    {
        BOOST_CHECK_EQUAL(ex.what(), "Reset packet received");
    }
}

BOOST_AUTO_TEST_SUITE_END()
