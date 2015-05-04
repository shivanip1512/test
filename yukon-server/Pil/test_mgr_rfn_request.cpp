#include <boost/test/unit_test.hpp>

#include "mgr_rfn_request.h"
#include "cmd_rfn_ChannelConfiguration.h"

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

    void serializeAndQueue(const Request &req, Confirm::Callback callback, TimeoutCallback timeout, const Cti::Messaging::Rfn::ApplicationServiceIdentifiers asid) override
    {
        messages.push_back(req);

        Confirm ack;
        ack.rfnIdentifier = req.rfnIdentifier;

        callback(ack);  //  call the confirm callback immediately
    }
};

struct test_RfnRequestManager : Cti::Pil::RfnRequestManager
{
    struct test_E2eDataTransferProtocol : Cti::Protocols::E2eDataTransferProtocol
    {
        unsigned short id;

        unsigned short getOutboundId() override
        {
            return id++;
        }

    } e2e;

    Cti::Protocols::E2eDataTransferProtocol::EndpointResponse handleE2eDtIndication(const std::vector<unsigned char> &payload, const long endpointId) override
    {
        return e2e.handleIndication(payload, endpointId);
    }
    std::vector<unsigned char> sendE2eDtRequest(const std::vector<unsigned char> &payload, const long endpointId, const unsigned long token) override
    {
        return e2e.sendRequest(payload, endpointId, token);
    }
};

BOOST_AUTO_TEST_CASE( test_cmd_rfn_successful )
{
    //  a handle for our reference
    test_E2eMessenger *e2e = new test_E2eMessenger;

    Cti::Messaging::Rfn::gE2eMessenger.reset(e2e);

    test_RfnRequestManager mgr;

    mgr.e2e.id = 0x7301;

    std::vector<Cti::Pil::RfnDeviceRequest> requests;

    const Cti::RfnIdentifier rfnId = { "MANUFATURER", "MODEL", "SERIAL" };

    {
        Cti::Pil::RfnDeviceRequest request;
        request.command.reset(new Cti::Devices::Commands::RfnGetChannelSelectionFullDescriptionCommand);
        request.rfnIdentifier   = rfnId;
        request.deviceId        = 11235;
        //request.rfnRequestId = ...
        request.commandString   = "Command string.";
        request.priority        = 11;
        request.userMessageId   = 0;
        request.groupMessageId  = 0;
        request.connectionHandle = nullptr;

        requests.push_back(request);
    }

    mgr.submitRequests(requests, 0x5ad5);

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

        indication.rfnIdentifier = requests.front().rfnIdentifier;
        indication.payload.assign(inboundPayload.begin(), inboundPayload.end());

        e2e->indicationHandler(indication);
    }

    mgr.tick();

    boost::ptr_deque<Cti::Pil::RfnDeviceResult> results = mgr.getResults(99);

    {
        BOOST_REQUIRE_EQUAL(results.size(), 1);

        Cti::Pil::RfnDeviceResult &result = results.front();

        const std::string expectedDescription =
            "Status: Success (0)"
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

        BOOST_CHECK_EQUAL(result.commandResult.description, expectedDescription);
        BOOST_CHECK(result.commandResult.points.empty());
        BOOST_CHECK_EQUAL(result.status, ClientErrors::None);
    }
}

BOOST_AUTO_TEST_CASE( test_cmd_rfn_badRequest )
{
    //  a handle for our reference
    test_E2eMessenger *e2e = new test_E2eMessenger;

    Cti::Messaging::Rfn::gE2eMessenger.reset(e2e);

    test_RfnRequestManager mgr;

    mgr.e2e.id = 0x7301;

    std::vector<Cti::Pil::RfnDeviceRequest> requests;

    const Cti::RfnIdentifier rfnId = { "MANUFATURER", "MODEL", "SERIAL" };

    {
        Cti::Pil::RfnDeviceRequest request;
        request.command.reset(new Cti::Devices::Commands::RfnGetChannelSelectionFullDescriptionCommand);
        request.rfnIdentifier   = rfnId;
        request.deviceId        = 11235;
        //request.rfnRequestId = ...
        request.commandString   = "Command string.";
        request.priority        = 11;
        request.userMessageId   = 0;
        request.groupMessageId  = 0;
        request.connectionHandle = nullptr;

        requests.push_back(request);
    }

    mgr.submitRequests(requests, 0x5ad5);

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

        indication.rfnIdentifier = requests.front().rfnIdentifier;
        indication.payload.assign(inboundPayload.begin(), inboundPayload.end());

        e2e->indicationHandler(indication);
    }

    mgr.tick();

    boost::ptr_deque<Cti::Pil::RfnDeviceResult> results = mgr.getResults(99);

    {
        BOOST_REQUIRE_EQUAL(results.size(), 1);

        Cti::Pil::RfnDeviceResult &result = results.front();

        const std::string expectedDescription =
            "Request not acceptable";

        BOOST_CHECK_EQUAL(result.commandResult.description, expectedDescription);
        BOOST_CHECK(result.commandResult.points.empty());
        BOOST_CHECK_EQUAL(result.status, ClientErrors::E2eRequestNotAcceptable);
    }
}

BOOST_AUTO_TEST_SUITE_END()

