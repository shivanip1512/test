#define BOOST_TEST_MAIN "Test Klondike protocol"

#include <boost/test/unit_test.hpp>

using boost::unit_test_framework::test_suite;

#include "boost_test_helpers.h"

#include <iostream>
#include "prot_klondike.h"
#include "dev_ccu721.h"  //  for timesync writes

using namespace std;

using Cti::Protocols::KlondikeProtocol;

using Cti::byte_buffer;

class Test_Wrap : public Cti::Protocol::Wrap
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

    virtual int generate(CtiXfer &xfer)
    {
        return NoError;
    };

    virtual int decode(CtiXfer &xfer, int status)
    {
        return NoError;
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


void do_xfer(Test_Klondike &tk, Test_Wrap &tw, CtiXfer &xfer, const byte_buffer &outbound, const byte_buffer &inbound)
{
    //  first do the send...
    BOOST_CHECK_EQUAL(tk.generate(xfer), NoError);

    // check what was assigned into our Test_Wrap object
    BOOST_CHECK_EQUAL_COLLECTIONS(
        tw.sent.begin(),
        tw.sent.end(),
        outbound.begin(),
        outbound.end());

    BOOST_CHECK_EQUAL(tk.decode(xfer, 0), NoError);
    BOOST_CHECK(!tk.errorCondition());

    tw.sent.clear();
    tw.received.clear();

    //  then do the receive
    BOOST_CHECK_EQUAL(tk.generate(xfer), NoError);

    BOOST_CHECK_EQUAL(tw.sent.size(), 0);

    //  assign our inbound data into the Test_Wrap object
    tw.received.insert(
        tw.received.end(),
        inbound.begin(),
        inbound.end());

    BOOST_CHECK_EQUAL(tk.decode(xfer, 0), NoError);
    BOOST_CHECK(!tk.errorCondition());

    tw.sent.clear();
    tw.received.clear();
}


BOOST_AUTO_TEST_CASE(test_prot_klondike_timesync_and_queue_loading)
{
    Test_Klondike  test_klondike;
    Test_Wrap test_wrap;
    CtiXfer xfer;
    int transactions = 0;

    test_klondike.setWrap(&test_wrap);

    cout.setf(ios::hex, ios::basefield);
    cout.width(2);

    //  load up a timesync command
    BOOST_CHECK_EQUAL(test_klondike.setCommand(KlondikeProtocol::Command_TimeSync), NoError);

    test_klondike.time = 0x456789ab;

    do_xfer(test_klondike, test_wrap, xfer, (byte_buffer() << 0x21, 0xab, 0x89, 0x67, 0x45),
                                            (byte_buffer() << 0x80, 0x21, 0x00, 0x00));
    cout << "Transaction " << ++transactions << endl;

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
    BOOST_CHECK_EQUAL(test_klondike.setCommand(KlondikeProtocol::Command_LoadQueue), NoError);

    //  verify it grabs the status from the CCU first
    do_xfer(test_klondike, test_wrap, xfer, (byte_buffer() << 0x11),
                                            (byte_buffer() << 0x81, 0x11, 0x00, 0x00, 0x04, 0x37, 0x00));
    cout << "Transaction " << ++transactions << endl;

    //  then loads the queued request
    do_xfer(test_klondike, test_wrap, xfer, (byte_buffer() << 0x13, 0x37, 0x00, 0x01, 0x0f, 0x10, 0x00, 0x03, 0x12, 0x34, 0x56),
                                            (byte_buffer() << 0x81, 0x13, 0x08, 0x00, 0x01, 0x03));
    cout << "Transaction " << ++transactions << endl;
}


BOOST_AUTO_TEST_CASE(test_prot_klondike_route_loading)
{
    Test_Klondike  test_klondike;
    Test_Wrap test_wrap;
    CtiXfer xfer;
    int transactions = 0;

    test_klondike.setWrap(&test_wrap);

    cout.setf(ios::hex, ios::basefield);
    cout.width(2);

    BOOST_CHECK_EQUAL(test_klondike.setCommand(KlondikeProtocol::Command_LoadRoutes), NoError);

    test_klondike.addRoute(1, 27, 3, 6);

    do_xfer(test_klondike, test_wrap, xfer, (byte_buffer() << 0x34, 0x00),
                                            (byte_buffer() << 0x80, 0x21, 0x00, 0x00));
    cout << "Transaction " << ++transactions << endl;

    //  fixed, variable, stages, bus

    do_xfer(test_klondike, test_wrap, xfer, (byte_buffer() << 0x31, 0x01, 0x00, 0xdb, 0x31),
                                            (byte_buffer() << 0x80, 0x21, 0x00, 0x00));
    cout << "Transaction " << ++transactions << endl;

    BOOST_CHECK_EQUAL(test_klondike.setCommand(KlondikeProtocol::Command_LoadRoutes), NoError);

    test_klondike.addRoute(2, 16, 5, 2);

    do_xfer(test_klondike, test_wrap, xfer, (byte_buffer() << 0x34, 0x00),
                                            (byte_buffer() << 0x80, 0x21, 0x00, 0x00));
    cout << "Transaction " << ++transactions << endl;

    do_xfer(test_klondike, test_wrap, xfer, (byte_buffer() << 0x31, 0x02, 0x00, 0xdb, 0x31, 0x01, 0x85, 0x12),
                                            (byte_buffer() << 0x80, 0x21, 0x00, 0x00));
    cout << "Transaction " << ++transactions << endl;
}

