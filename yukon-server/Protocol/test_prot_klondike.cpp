#include <boost/test/unit_test.hpp>

#include "prot_klondike.h"

#include "boost_test_helpers.h"

using namespace std;

using Cti::Protocols::KlondikeProtocol;

using Cti::Test::byte_str;

BOOST_AUTO_TEST_SUITE( test_prot_klondike )

class Test_Wrap : public Cti::Protocols::Wrap
{
public:

    KlondikeProtocol::byte_buffer_t sent, received;

    virtual bool send( const std::vector<unsigned char> &buf )
    {
        sent.assign(buf.begin(), buf.end());
        return true;
    };

    virtual bool recv( void )  {  sent.clear();  received.clear();  return true;  };
    virtual bool init( void )  {  sent.clear();  received.clear();  return true;  };

    virtual YukonError_t generate(CtiXfer &xfer)
    {
        return ClientErrors::None;
    };

    virtual YukonError_t decode(CtiXfer &xfer, YukonError_t status)
    {
        return ClientErrors::None;
    };

    virtual void getInboundData( std::vector<unsigned char> &buf )
    {
        buf.assign(received.begin(), received.end());
    };

    virtual unsigned getMaximumPayload() const
    {
        return 255;
    };
};

struct Test_Klondike : public KlondikeProtocol
{
    unsigned long time;

    using KlondikeProtocol::setWrap;

    long currentTime()
    {
        return time;
    };
};


void do_xfer(Test_Klondike &tk, Test_Wrap &tw, CtiXfer &xfer, const byte_str &outbound, const byte_str &inbound)
{
    //  first do the send...
    BOOST_CHECK_EQUAL(tk.generate(xfer), ClientErrors::None);

    // check what was assigned into our Test_Wrap object
    BOOST_CHECK_EQUAL_RANGES(tw.sent, outbound);

    BOOST_CHECK_EQUAL(tk.decode(xfer, ClientErrors::None), ClientErrors::None);
    BOOST_CHECK(!tk.errorCondition());

    tw.sent.clear();
    tw.received.clear();

    //  then do the receive
    BOOST_CHECK_EQUAL(tk.generate(xfer), ClientErrors::None);

    BOOST_CHECK_EQUAL(tw.sent.size(), 0);

    //  assign our inbound data into the Test_Wrap object
    tw.received.insert(
        tw.received.end(),
        inbound.begin(),
        inbound.end());

    BOOST_CHECK_EQUAL(tk.decode(xfer, ClientErrors::None), ClientErrors::None);
    BOOST_CHECK(!tk.errorCondition());

    tw.sent.clear();
    tw.received.clear();
}


BOOST_AUTO_TEST_CASE(test_prot_klondike_timesync_and_queue_loading)
{
    Test_Klondike  test_klondike;
    Test_Wrap test_wrap;
    CtiXfer xfer;

    test_klondike.setWrap(&test_wrap);

    cout.setf(ios::hex, ios::basefield);
    cout.width(2);

    //  load up a timesync command
    BOOST_CHECK_EQUAL(test_klondike.setCommand(KlondikeProtocol::Command_TimeSync), ClientErrors::None);

    test_klondike.time = 0x456789ab;

    do_xfer(test_klondike, test_wrap, xfer, byte_str("21 ab 89 67 45"),
                                            byte_str("80 21 00 00"));

    //  then queue up a queued command
    Test_Klondike::byte_buffer_t timesync_standin;

    timesync_standin.push_back(0x12);
    timesync_standin.push_back(0x34);
    timesync_standin.push_back(0x56);

    test_klondike.addQueuedWork(this,
                                timesync_standin,
                                MAXPRIORITY,
                                KlondikeProtocol::DLCParms_BroadcastFlag,
                                0);

    //  and set the command
    BOOST_CHECK_EQUAL(test_klondike.setCommand(KlondikeProtocol::Command_LoadQueue), ClientErrors::None);

    //  verify it grabs the status from the CCU first
    do_xfer(test_klondike, test_wrap, xfer, byte_str("11"),
                                            byte_str("81 11 00 00 04 37 00"));

    //  then loads the queued request
    do_xfer(test_klondike, test_wrap, xfer, byte_str("13 37 00 01 0f 10 00 03 12 34 56"),
                                            byte_str("81 13 08 00 01 03"));
}


BOOST_AUTO_TEST_CASE(test_prot_klondike_route_loading)
{
    Test_Klondike  test_klondike;
    Test_Wrap test_wrap;
    CtiXfer xfer;

    test_klondike.setWrap(&test_wrap);

    cout.setf(ios::hex, ios::basefield);
    cout.width(2);

    BOOST_CHECK_EQUAL(test_klondike.setCommand(KlondikeProtocol::Command_LoadRoutes), ClientErrors::None);

    test_klondike.addRoute(1, 27, 3, 6);

    do_xfer(test_klondike, test_wrap, xfer, byte_str("34 00"),
                                            byte_str("80 21 00 00"));

    //  fixed, variable, stages, bus

    do_xfer(test_klondike, test_wrap, xfer, byte_str("31 01 00 db 31"),
                                            byte_str("80 21 00 00"));

    BOOST_CHECK_EQUAL(test_klondike.setCommand(KlondikeProtocol::Command_LoadRoutes), ClientErrors::None);

    test_klondike.addRoute(2, 16, 5, 2);

    do_xfer(test_klondike, test_wrap, xfer, byte_str("34 00"),
                                            byte_str("80 21 00 00"));

    do_xfer(test_klondike, test_wrap, xfer, byte_str("31 02 00 db 31 01 85 12"),
                                            byte_str("80 21 00 00"));
}


BOOST_AUTO_TEST_SUITE_END()
