#include <boost/test/unit_test.hpp>

#include "mgr_rfn_request.h"
#include "mgr_device.h"
#include "cmd_rfn_ChannelConfiguration.h"
#include "cmd_rfn_ConfigNotification.h"
#include "cmd_rfn_LoadProfile.h"

#include "rtdb_test_helpers.h"

#include "boost_test_helpers.h"

BOOST_AUTO_TEST_SUITE( test_mgr_rfn_request )

using Cti::Messaging::Rfn::E2eDataRequestMsg;

struct test_E2eMessenger : Cti::Messaging::Rfn::E2eMessenger
{
    std::vector<Request> messages;

    Indication::Callback indicationHandler;

    void setE2eDtHandler(Indication::Callback callback) override
    {
        indicationHandler = callback;
    }

    void serializeAndQueue(const Request &req, const Cti::Messaging::Rfn::ApplicationServiceIdentifiers asid, Confirm::Callback callback, TimeoutCallback timeout) override
    {
        messages.push_back(req);

        Confirm ack;
        ack.rfnIdentifier = req.rfnIdentifier;
        ack.error = ClientErrors::None;

        callback(ack);  //  call the confirm callback immediately
    }
};

struct test_RfnRequestManager : Cti::Pil::RfnRequestManager
{
    Cti::Test::test_DeviceManager devMgr;

    test_RfnRequestManager() 
        :   RfnRequestManager { devMgr }
    {
    }

    struct test_E2eDataTransferProtocol : Cti::Protocols::E2eDataTransferProtocol
    {
        unsigned short id;

        unsigned short getOutboundId() override
        {
            return id++;
        }

    } e2e;

    EndpointMessage handleE2eDtIndication(const std::vector<unsigned char> &payload, const Cti::RfnIdentifier endpointId) override
    {
        return e2e.handleIndication(payload, endpointId);
    }
    Bytes sendE2eDtRequest(const std::vector<unsigned char> &payload, const Cti::RfnIdentifier endpointId, const unsigned long token) override
    {
        return e2e.sendRequest(payload, endpointId, token);
    }
    Bytes sendE2eDtBlockContinuation(const BlockSize blockSize, const int blockNum, const Cti::RfnIdentifier endpointId, const unsigned long token) override
    {
        return e2e.sendBlockContinuation(blockSize, blockNum, endpointId, token);
    }
    Bytes sendE2eDtPost(const std::vector<unsigned char> &payload, const Cti::RfnIdentifier endpointId, const unsigned long token) override
    {
        return e2e.sendPost(payload, endpointId, token);
    }
};

BOOST_AUTO_TEST_CASE( test_cmd_rfn_successful )
{
    //  a handle for our reference
    test_E2eMessenger *e2e = new test_E2eMessenger;

    Cti::Messaging::Rfn::gE2eMessenger.reset(e2e);

    test_RfnRequestManager mgr;

    mgr.e2e.id = 0x7301;

    mgr.start();

    std::vector<Cti::Pil::RfnDeviceRequest> requests;

    const Cti::RfnIdentifier rfnId { "MANUFACTURER", "MODEL", "SERIAL" };

    {
        Cti::Pil::RfnDeviceRequest::Parameters parameters;
        parameters.rfnIdentifier   = rfnId;
        parameters.deviceId        = 11235;
        parameters.commandString   = "Command string.";
        parameters.priority        = 11;
        parameters.userMessageId   = 0;
        parameters.groupMessageId  = 0;

        requests.emplace_back(parameters, 0x5ad6, std::make_unique<Cti::Devices::Commands::RfnGetChannelSelectionFullDescriptionCommand>());
    }

    mgr.submitRequests(std::move(requests));

    mgr.tick();

    {
        BOOST_REQUIRE_EQUAL(e2e->messages.size(), 1);

        const auto &request = e2e->messages.front();

        Cti::Test::byte_str expectedPayload = "42 01 02 73 5a d6 ff 78 02 00";

        BOOST_CHECK_EQUAL_RANGES(request.payload, expectedPayload);
    }

    mgr.tick();

    {
        Cti::Messaging::Rfn::E2eMessenger::Indication indication;

        Cti::Test::byte_str inboundPayload =
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

        indication.rfnIdentifier = rfnId;
        indication.payload.assign(inboundPayload.begin(), inboundPayload.end());

        e2e->indicationHandler(indication);
    }

    mgr.tick();

    auto results = mgr.getResults(99);

    {
        BOOST_REQUIRE_EQUAL(results.size(), 1);

        const auto & commandResults = results.front().commandResults;

        BOOST_REQUIRE_EQUAL(commandResults.size(), 1);

        const auto & commandResult = commandResults.front();

        const std::string expectedDescription =
            "Channel Selection Request:"
            "\nStatus: Success (0)"
            "\nChannel Registration Full Description:"
            "\nMetric(s) descriptors:"
            "\nWatt hour received (2): Scaling Factor: 1"
            "\nWatt hour total/sum (3): Scaling Factor: 1"
            "\nWatt hour net (4): Scaling Factor: 1"
            "\nWatts delivered, current demand (5): Scaling Factor: 1"
            "\nWatts delivered, peak demand (7): Scaling Factor: 1"
            "\nTime in Seconds (256): Coincident Value 1, Scaling Factor: 1"
            "\nWatts delivered, peak demand (Frozen) (9): Scaling Factor: 1"
            "\nTime in Seconds (256): Coincident Value 1, Scaling Factor: 1"
            "\nTemperature in Centigrade (257): Scaling Factor: 1"
            "\nWatt hour delivered, Rate A (1001): Scaling Factor: 1"
            "\nWatt hour received, Rate A (1002): Scaling Factor: 1"
            "\nWatt hour total/sum, Rate A (1003): Scaling Factor: 1"
            "\nWatt hour net, Rate A (1004): Scaling Factor: 1"
            "\nWatts delivered, peak demand, Rate A (1007): Scaling Factor: 1"
            "\nTime in Seconds, Rate A (1256): Coincident Value 1, Scaling Factor: 1"
            "\nWatts delivered, peak demand (Frozen), Rate A (1009): Scaling Factor: 1"
            "\nTime in Seconds, Rate A (1256): Coincident Value 1, Scaling Factor: 1"
            "\nWatt hour delivered, Rate B (2001): Scaling Factor: 1"
            "\nWatt hour received, Rate B (2002): Scaling Factor: 1"
            "\nWatt hour total/sum, Rate B (2003): Scaling Factor: 1"
            "\nWatt hour net, Rate B (2004): Scaling Factor: 1"
            "\nWatts delivered, peak demand, Rate B (2007): Scaling Factor: 1"
            "\nTime in Seconds, Rate B (2256): Coincident Value 1, Scaling Factor: 1"
            "\nWatts delivered, peak demand (Frozen), Rate B (2009): Scaling Factor: 1"
            "\nTime in Seconds, Rate B (2256): Coincident Value 1, Scaling Factor: 1"
            "\nWatt hour delivered, Rate C (3001): Scaling Factor: 1"
            "\nWatt hour received, Rate C (3002): Scaling Factor: 1"
            "\nWatt hour total/sum, Rate C (3003): Scaling Factor: 1"
            "\nWatt hour net, Rate C (3004): Scaling Factor: 1"
            "\nWatts delivered, peak demand, Rate C (3007): Scaling Factor: 1"
            "\nTime in Seconds, Rate C (3256): Coincident Value 1, Scaling Factor: 1"
            "\nWatts delivered, peak demand (Frozen), Rate C (3009): Scaling Factor: 1"
            "\nTime in Seconds, Rate C (3256): Coincident Value 1, Scaling Factor: 1"
            "\nWatt hour delivered, Rate D (4001): Scaling Factor: 1"
            "\nWatt hour received, Rate D (4002): Scaling Factor: 1"
            "\nWatt hour total/sum, Rate D (4003): Scaling Factor: 1"
            "\nWatt hour net, Rate D (4004): Scaling Factor: 1"
            "\nWatts delivered, peak demand, Rate D (4007): Scaling Factor: 1"
            "\nTime in Seconds, Rate D (4256): Coincident Value 1, Scaling Factor: 1"
            "\nWatts delivered, peak demand (Frozen), Rate D (4009): Scaling Factor: 1"
            "\nTime in Seconds, Rate D (4256): Coincident Value 1, Scaling Factor: 1"
            "\n";

        BOOST_CHECK_EQUAL(commandResult.description, expectedDescription);
        BOOST_CHECK(commandResult.points.empty());
        BOOST_CHECK_EQUAL(commandResult.status, ClientErrors::None);
    }
}

BOOST_AUTO_TEST_CASE( test_cmd_rfn_badRequest )
{
    //  a handle for our reference
    test_E2eMessenger *e2e = new test_E2eMessenger;

    Cti::Messaging::Rfn::gE2eMessenger.reset(e2e);

    test_RfnRequestManager mgr;

    mgr.e2e.id = 0x7301;

    mgr.start();

    std::vector<Cti::Pil::RfnDeviceRequest> requests;

    const Cti::RfnIdentifier rfnId = { "MANUFACTURER", "MODEL", "SERIAL" };

    {
        Cti::Pil::RfnDeviceRequest::Parameters parameters;
        parameters.rfnIdentifier   = rfnId;
        parameters.deviceId        = 11235;
        parameters.commandString   = "Command string.";
        parameters.priority        = 11;
        parameters.userMessageId   = 0;
        parameters.groupMessageId  = 0;

        requests.emplace_back(parameters, 0x5ad6, std::make_unique<Cti::Devices::Commands::RfnGetChannelSelectionFullDescriptionCommand>());
    }

    mgr.submitRequests(std::move(requests));

    mgr.tick();

    {
        BOOST_REQUIRE_EQUAL(e2e->messages.size(), 1);

        const auto &request = e2e->messages.front();

        Cti::Test::byte_str expectedPayload = "42 01 02 73 5a d6 ff 78 02 00";

        BOOST_CHECK_EQUAL_RANGES(request.payload, expectedPayload);
    }

    mgr.tick();

    {
        Cti::Messaging::Rfn::E2eMessenger::Indication indication;

        Cti::Test::byte_str inboundPayload =
            "62 "  //  ack response, token length 2
            "86 "  //  response code 406  (code / 100 << 6 | code % 100)
            "02 73 "  //  e2e unique ID
            "5a d6 "  //  app token
            "ff";

        indication.rfnIdentifier = rfnId;
        indication.payload.assign(inboundPayload.begin(), inboundPayload.end());

        e2e->indicationHandler(indication);
    }

    mgr.tick();

    auto results = mgr.getResults(99);

    {
        BOOST_REQUIRE_EQUAL(results.size(), 1);

        const auto & commandResults = results.front().commandResults;

        BOOST_REQUIRE_EQUAL(commandResults.size(), 1);

        const auto & commandResult = commandResults.front();

        BOOST_CHECK_EQUAL(commandResult.description, "Channel Selection Request:"
                                                     "\nEndpoint indicated request not acceptable.");
        BOOST_CHECK(commandResult.points.empty());
        BOOST_CHECK_EQUAL(commandResult.status, ClientErrors::E2eRequestNotAcceptable);
    }
}

BOOST_AUTO_TEST_CASE(test_cmd_rfn_blockContinuation)
{
    Cti::Test::set_to_central_timezone();

    //  a handle for our reference
    test_E2eMessenger *e2e = new test_E2eMessenger;

    Cti::Messaging::Rfn::gE2eMessenger.reset(e2e);

    test_RfnRequestManager mgr;

    const CtiTime now   { CtiDate(25, 7, 2018), 10, 17 };
    const CtiTime begin { CtiDate(18, 7, 2018),  0,  0 };
    const CtiTime end   { CtiDate(25, 7, 2018),  9, 59 };

    mgr.e2e.id = 0x2b0a;

    mgr.start();

    std::vector<Cti::Pil::RfnDeviceRequest> requests;

    const Cti::RfnIdentifier rfnId{ "MANUFACTURER", "MODEL", "SERIAL" };

    {
        Cti::Pil::RfnDeviceRequest::Parameters parameters;
        parameters.rfnIdentifier = rfnId;
        parameters.deviceId = 11235;
        parameters.commandString = "getvalue voltage profile 07/18/2018 00:00 07/25/2018 09:59";
        parameters.priority = 11;
        parameters.userMessageId = 0;
        parameters.groupMessageId = 0;

        requests.emplace_back(parameters, 0xa01e, std::make_unique<Cti::Devices::Commands::RfnLoadProfileReadPointsCommand>(now, begin, end));
    }

    mgr.submitRequests(std::move(requests));

    mgr.tick();

    {
        BOOST_REQUIRE_EQUAL(e2e->messages.size(), 1);

        const auto &request = e2e->messages.front();

        Cti::Test::byte_str expectedPayload = "42 01 0b 2b a0 1e ff 68 05 01 04 00 08 5b 4e c9 50 5b 58 90 34";

        BOOST_CHECK_EQUAL_RANGES(request.payload, expectedPayload);
    }

    mgr.tick();

    {
        Cti::Messaging::Rfn::E2eMessenger::Indication indication;

        Cti::Test::byte_str inboundPayload =
            "62 45 0b 2b a0 1e d1 0a 0e 52 08 d9 ff 69 05 00 01 03 08 d2 11 10 80 00 01 40 0f 13 5b 4e c9 50 03 28 09 f0 00 09 f4 00 09 f5 00 09 f7 00 09 f9 00 09 "
            "f8 00 09 ed 00 09 eb 00 09 ec 00 09 ea 00 09 eb 00 09 ef 00 09 ec 00 09 ee 00 09 ec 00 09 ee 00 09 e1 00 09 d8 00 09 ce 00 09 d6 00 09 cc 00 09 d7 00 "
            "09 e0 00 09 d6 00 09 d2 00 09 c4 00 09 c5 00 09 b8 00 09 b8 00 09 af 00 09 a7 00 09 a5 00 09 a2 00 09 a5 00 09 b5 00 09 bc 00 09 b8 00 09 b2 00 09 ad "
            "00 09 bc 00 5b 4f 55 f0 03 28 09 b2 00 09 b6 00 09 af 00 09 a9 00 09 a9 00 09 a6 00 09 b1 00 09 a2 00 09 b7 00 09 b1 00 09 ad 00 09 a7 00 09 a7 00 09 "
            "a5 00 09 a9 00 09 a9 00 09 a4 00 09 aa 00 09 a6 00 09 a7 00 09 ad 00 09 b3 00 09 b7 00 09 bb 00 09 ba 00 09 bb 00 09 ba 00 09 c1 00 09 cb 00 09 ce 00 "
            "09 cf 00 09 d3 00 09 d7 00 09 db 00 09 d8 00 09 e2 00 09 f8 00 09 fe 00 09 fb 00 09 fb 00 5b 4f e2 90 03 28 0a 00 00 0a 01 00 09 f9 00 09 ff 00 09 f2 "
            "00 09 f4 00 09 f5 00 09 fa 00 09 fa 00 0a 00 00 09 fe 00 09 fb 00 09 fc 00 09 f3 00 09 f0 00 09 f6 00 09 e9 00 09 ef 00 09 f4 00 09 e0 00 09 e4 00 09 "
            "e8 00 09 ec 00 09 ee 00 09 ef 00 09 f0 00 09 f1 00 09 f3 00 09 f3 00 09 f2 00 09 f0 00 09 ef 00 09 e0 00 09 d9 00 09 cc 00 09 d0 00 09 d5 00 09 c9 00 "
            "09 c8 00 09 c3 00 5b 50 6f 30 03 28 09 c0 00 09 b5 00 09 ac 00 09 b8 00 09 b5 00 09 aa 00 09 aa 00 09 a1 00 09 a1 00 09 b0 00 09 b1 00 09 aa 00 09 a4 "
            "00 09 a9 00 09 9f 00 09 a2 00 09 a6 00 09 a4 00 09 a7 00 09 a4 00 09 bf 00 09 bf 00 09 c1 00 09 c1 00 09 c6 00 09 cd 00 09 c4 00 09 bf 00 09 bd 00 09 "
            "bf 00 09 bb 00 09 bb 00 09 c0 00 09 c6 00 09 c8 00 09 cf 00 09 d2 00 09 d6 00 09 e0 00 09 d8 00 5b 50 fb d0 03 28 09 dd 00 09 d3 00 09 da 00 09 de 00 "
            "09 df 00 09 e8 00 09 ed 00 09 e2 00 09 ec 00 09 f1 00 09 e8 00 09 ed 00 09 f9 00 09 e6 00 09 e5 00 09 e3 00 09 e5 00 09 e7 00 09 e1 00 09 dd 00 09 d7 "
            "00 09 df 00 09 df 00 09 df 00 09 e2 00 09 ed 00 09 e3 00 09 e9 00 09 ed 00 09 e9 00 09 ea 00 09 eb 00 09 f3 00 09 f1 00 09 f0 00 09 ed 00 09 ee 00 09 "
            "ef 00 09 f6 00 09 f8 00 5b 51 88 70 03 28 09 fa 00 09 f9 00 09 f9 00 09 fb 00 09 fc 00 09 ee 00 09 e6 00 09 e6 00 09 e6 00 09 db 00 09 d5 00 09 d6 00 "
            "09 d3 00 09 cb 00 09 cd 00 09 c8 00 09 c0 00 09 c0 00 09 bf 00 09 bd 00 09 ba 00 09 ad 00 09 b3 00 09 b5 00 09 b2 00 09 b0 00 09 b0 00 09 ad 00 09 a7 "
            "00 09 be 00 09 c2 00 09 bc 00 09 ba 00 09 b9 00 09 bd 00 09 bb 00 09 bd 00 09 bc 00 09 be 00 09 ba 00 5b 52 15 10 03 28 09 bc 00 09 ba 00 09 b8 00 09 "
            "b3 00 09 ad 00 09 b9 00 09 ba 00 09 bb 00 09 b9 00 09 b9 00 09 b6 00 09 b0 00 09 b8 00 09 bd 00 09 c5 00 09 c3 00 09 c7 00 09 c8 00 09 ca 00 09 cb 00 "
            "09 c7 00 09 ce 00 09 d2 00 09 d9 00 09 e6 00 09 e5 00 09 ea 00 09 ee 00 09 ff 00 0a 06 00 09 e7 00 09 e9 00 09 ec 00 09 f0 00 09 ef 00 09 ef 00 09 ef "
            "00 09 f1 00 09 f1 00 09 f3 00 5b 52 a1 b0 03 28 09 f8 00 09 ec 00 09 ef 00 09 f3 00 09 f8 00 09 f9 00 09 f6 00 09 fa 00 09 fc 00 09 fe 00 09 f9 00 0a "
            "00 00 09 f6 00 09 f5 00 09 f7 00 09 f8 00 09 f7 00 09 f9 00 09 f8 00 09 f9 00 09 fd 00 09 fd 00 09 fc 00 09 fc 00 09 fc 00 09 fd 00 09 fa 00 09 fc 00 "
            "09 fe 00 09 fd 00 09 fc 00 09 fe 00 09 fb 00 09 f2 00 09 f2 00 09 dd 00 09 db 00 09 e0 00 09 dd 00 09 da 00 5b";

        indication.rfnIdentifier = rfnId;
        indication.payload.assign(inboundPayload.begin(), inboundPayload.end());

        e2e->indicationHandler(indication);
    }

    mgr.tick();

    BOOST_CHECK(mgr.getResults(99).empty());

    {
        Cti::Messaging::Rfn::E2eMessenger::Indication indication;

        Cti::Test::byte_str inboundPayload =
            "62 45 0c 2b a0 1e d1 0a 1e ff 53 2e 50 03 28 09 d8 00 09 cf 00 09 cc 00 09 c8 00 09 c8 00 09 c3 00 09 c2 00 09 cd 00 09 cd 00 09 ca 00 09 ca 00 09 c8 "
            "00 09 c8 00 09 c4 00 09 c3 00 09 c1 00 09 c1 00 09 bb 00 09 c1 00 09 c2 00 09 cf 00 09 cf 00 09 d1 00 09 d1 00 09 d0 00 09 cf 00 09 ce 00 09 d3 00 09 "
            "d1 00 09 d1 00 09 d3 00 09 d1 00 09 d1 00 09 d9 00 09 d8 00 09 d3 00 09 d6 00 09 d9 00 09 da 00 09 db 00 5b 53 ba f0 03 28 09 de 00 09 dc 00 09 e0 00 "
            "09 df 00 09 df 00 09 e5 00 09 e6 00 09 e8 00 09 e8 00 09 ef 00 09 f1 00 09 ef 00 09 f4 00 09 f5 00 09 f5 00 09 f7 00 09 f6 00 09 f0 00 09 eb 00 09 f2 "
            "00 09 f1 00 09 f0 00 09 f3 00 09 f7 00 09 f8 00 09 ff 00 09 f8 00 09 fc 00 09 fb 00 09 fe 00 09 fe 00 09 fb 00 09 fb 00 09 f5 00 09 f6 00 09 f8 00 09 "
            "f8 00 09 fa 00 09 f9 00 09 fa 00 5b 54 47 90 03 28 09 fa 00 09 fd 00 09 fc 00 09 fa 00 09 f9 00 09 fc 00 09 fb 00 09 fb 00 09 fe 00 09 fb 00 09 fc 00 "
            "09 ef 00 09 e9 00 09 e4 00 09 e7 00 09 e9 00 09 e8 00 09 e4 00 09 e3 00 09 e4 00 09 e0 00 09 dc 00 09 d8 00 09 d5 00 09 d7 00 09 d1 00 09 e0 00 09 da "
            "00 09 d9 00 09 d6 00 09 d6 00 09 df 00 09 e0 00 09 e4 00 09 dd 00 09 da 00 09 db 00 09 d9 00 09 d3 00 09 d4 00 5b 54 d4 30 03 28 09 d1 00 09 dc 00 09 "
            "d9 00 09 da 00 09 dd 00 09 da 00 09 d9 00 09 d5 00 09 d2 00 09 d0 00 09 d1 00 09 d0 00 09 d1 00 09 e0 00 09 e1 00 09 da 00 09 e0 00 09 e0 00 09 df 00 "
            "09 e4 00 09 e3 00 09 e3 00 09 e7 00 09 ec 00 09 ef 00 09 f1 00 09 f5 00 09 f5 00 09 fb 00 09 fc 00 09 fc 00 09 f2 00 09 f9 00 09 ed 00 09 f3 00 09 f2 "
            "00 09 f4 00 09 fb 00 09 fb 00 09 ff 00 5b 55 60 d0 03 28 09 f9 00 09 fc 00 09 fb 00 09 fc 00 09 fb 00 09 f4 00 09 e4 00 09 e2 00 09 d7 00 09 d1 00 09 "
            "c5 00 09 c5 00 09 c2 00 09 c0 00 09 c4 00 09 c3 00 09 c7 00 09 c3 00 09 c5 00 09 bf 00 09 bb 00 09 be 00 09 bc 00 09 b6 00 09 ad 00 09 b5 00 09 b1 00 "
            "09 b1 00 09 c1 00 09 b1 00 09 af 00 09 a8 00 09 a9 00 09 a4 00 09 9f 00 09 ab 00 09 ac 00 09 91 00 09 a8 00 09 b3 00 5b 55 ed 70 03 28 09 b9 00 09 ba "
            "00 09 b6 00 09 b9 00 09 ad 00 09 ad 00 09 ad 00 09 a5 00 09 a4 00 09 a8 00 09 9e 00 09 a3 00 09 9c 00 09 a3 00 09 a6 00 09 a3 00 09 aa 00 09 a9 00 09 "
            "ab 00 09 a3 00 09 a8 00 09 a7 00 09 ac 00 09 ae 00 09 a9 00 09 a5 00 09 a7 00 09 a9 00 09 af 00 09 b7 00 09 bc 00 09 c5 00 09 c8 00 09 d4 00 09 d3 00 "
            "09 e0 00 09 fa 00 09 f9 00 09 fb 00 09 fe 00 5b 56 7a 10 03 28 0a 06 00 09 fa 00 09 f4 00 09 f1 00 09 f1 00 09 f4 00 09 f9 00 09 fa 00 0a 05 00 0a 00 "
            "00 09 e9 00 09 ef 00 09 e9 00 09 e9 00 09 f2 00 09 f3 00 09 f4 00 09 fa 00 09 fe 00 0a 02 00 0a 08 00 09 fe 00 09 e8 00 09 e0 00 09 e2 00 09 e4 00 09 "
            "e6 00 09 e3 00 09 e6 00 09 e6 00 09 e7 00 09 e8 00 09 de 00 09 d5 00 09 c9 00 09 cf 00 09 d3 00 09 c8 00 09 c9 00 09 c3 00 5b 57 06 b0 03 12 09 bc 00 "
            "09 be 00 09 ba 00 09 b9 00 09 ae 00 09 b3 00 09 b2 00 09 a7 00 09 ae 00 09 af 00 09 a6 00 09 ae 00 09 b7 00 09 b1 00 09 ba 00 09 d7 00 09 cd 00 09 cc "
            "00 5b 57 49 7c 03 28 09 c6 00 09 c9 00 09 c1 00 09 c1 00 09 be 00 09 c0 00 09 b6 00 09 b8 00 09 aa 00 09 b3 00 09 af 00 09 b1 00 09 ae 00 09 ac 00 09 "
            "b0 00 09 a9 00 09 b6 00 09 b7 00 09 b5 00 09 c1 00 09 bb 00 09 ba 00 09 be 00 09 be 00 09 c8 00 09 c5";

        indication.rfnIdentifier = rfnId;
        indication.payload.assign(inboundPayload.begin(), inboundPayload.end());

        e2e->indicationHandler(indication);
    }

    mgr.tick();
    
    BOOST_CHECK(mgr.getResults(99).empty());

    {
        Cti::Messaging::Rfn::E2eMessenger::Indication indication;

        Cti::Test::byte_str inboundPayload =
            "62 45 0d 2b a0 1e d1 0a 26 ff 00 09 e0 00 09 e3 00 09 e0 00 09 e8 00 09 eb 00 09 e3 00 09 e9 00 0a 03 00 0a 03 00 0a 0a 00 0a 07 00 0a 07 00 09 e8 00 "
            "09 eb 00 5b 57 d6 1c 03 28 09 ee 00 09 f1 00 09 f5 00 09 f7 00 09 ff 00 09 f1 00 09 f9 00 09 f5 00 09 f5 00 09 fc 00 09 f0 00 09 e5 00 09 ee 00 09 f4 "
            "00 09 d7 00 09 d7 00 09 dd 00 09 dd 00 09 e5 00 09 e6 00 09 e6 00 09 ec 00 09 ec 00 09 ea 00 09 ec 00 09 ec 00 09 eb 00 09 ea 00 09 ee 00 09 e2 00 09 "
            "d6 00 09 c9 00 09 cd 00 09 d2 00 09 c7 00 09 c4 00 09 b9 00 09 c9 00 09 bd 00 09 bf 00 5b 58 62 bc 03 0e 09 b2 00 09 c0 00 09 b8 00 09 af 00 09 b3 00 "
            "09 b6 00 09 b5 00 09 c5 00 09 c9 00 09 c2 00 09 be 00 09 bc 00 09 c0 00 09 bb 00 ";

        indication.rfnIdentifier = rfnId;
        indication.payload.assign(inboundPayload.begin(), inboundPayload.end());

        e2e->indicationHandler(indication);
    }

    mgr.tick();
    
    {
        auto results = mgr.getResults(99);

        BOOST_REQUIRE_EQUAL(results.size(), 1);

        const auto & commandResults = results.front().commandResults;

        BOOST_REQUIRE_EQUAL(commandResults.size(), 1);

        const auto & commandResult = commandResults.front();

        const std::string expectedDescription =
            "Load Profile Read Points Request:"
            "\nStatus: Success (0)";

        BOOST_CHECK_EQUAL(commandResult.description, expectedDescription);
        BOOST_CHECK_EQUAL(commandResult.status, ClientErrors::None);
        BOOST_REQUIRE_EQUAL(commandResult.points.size(), 712);

        const auto point0 = commandResult.points[0];

        BOOST_CHECK(point0.description.empty());
        BOOST_CHECK_EQUAL(point0.name, "Voltage");
        BOOST_CHECK_EQUAL(point0.offset, 214);
        BOOST_CHECK_EQUAL(point0.quality, 5);
        BOOST_CHECK_EQUAL(point0.tags, 0x8000);
        BOOST_CHECK_EQUAL(point0.time, begin);
        BOOST_CHECK_EQUAL(point0.type, 1);
        BOOST_CHECK_CLOSE(point0.value, 254.4, 0.0001);

        for( const auto point : commandResult.points )
        {
            BOOST_CHECK_EQUAL(point.name,    point0.name);
            BOOST_CHECK_EQUAL(point.offset,  point0.offset);
            BOOST_CHECK_EQUAL(point.quality, point0.quality);
            BOOST_CHECK_EQUAL(point.tags,    point0.tags);
            BOOST_CHECK_EQUAL(point.type,    point0.type);
        }
    }
}

BOOST_AUTO_TEST_SUITE_END()

