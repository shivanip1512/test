/*
 * test Cti::Protocol::Klondike
 *
 */

#include <boost/test/unit_test.hpp>

#define _WIN32_WINNT 0x0400

#include <iostream>
#include "prot_klondike.h"
#include "dev_ccu721.h"  //  for timesync writes

#define BOOST_AUTO_TEST_MAIN "Test Klondike Protocol"
#include <boost/test/auto_unit_test.hpp>
using boost::unit_test_framework::test_suite;

using namespace std;

using namespace Cti::Protocol;


class Test_Wrap : public Wrap
{
public:

    Klondike::byte_buffer_t sent, received;

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

class Test_Klondike : public Klondike
{
public:

    unsigned long time;

    void setWrap(Wrap *wrap)
    {
        Klondike::setWrap(wrap);
    };

    long currentTime()
    {
        return time;
    };
};

struct nice_buffer
{
    vector<char> v;

    nice_buffer &operator,(unsigned char c) {  v.push_back(c);  return *this;  };
    operator string()  {  return string(v.begin(), v.end());  }
};


void do_xfer(Test_Klondike &tk, Test_Wrap &tw, CtiXfer &xfer, string outbound, string inbound)
{
    //  first do the send...
    BOOST_CHECK_EQUAL(tk.generate(xfer), NoError);

    // check what was assigned into our Test_Wrap object
    BOOST_CHECK_EQUAL(tw.sent.size(), outbound.size());
    BOOST_CHECK(!memcmp(tw.sent.begin(), outbound.begin(), outbound.size()));  //  the remaining 4 bytes are the time, which can vary

    BOOST_CHECK_EQUAL(tk.decode(xfer, 0), NoError);
    BOOST_CHECK(!tk.errorCondition());

    tw.sent.clear();
    tw.received.clear();

    //  then do the receive
    BOOST_CHECK_EQUAL(tk.generate(xfer), NoError);

    BOOST_CHECK_EQUAL(tw.sent.size(), 0);

    //  assign our inbound data into the Test_Wrap object
    tw.received.assign(reinterpret_cast<unsigned char *>(inbound.begin()),
                       reinterpret_cast<unsigned char *>(inbound.end()));

    BOOST_CHECK_EQUAL(tk.decode(xfer, 0), NoError);
    BOOST_CHECK(!tk.errorCondition());

    tw.sent.clear();
    tw.received.clear();
}


BOOST_AUTO_UNIT_TEST(test_prot_klondike)
{
    Test_Klondike  test_klondike;
    Test_Wrap test_wrap;
    CtiXfer xfer;
    int transactions = 0;

    test_klondike.setWrap(&test_wrap);

    cout.setf(ios::hex, ios::basefield);
    cout.width(2);

    //  load up a timesync command
    BOOST_CHECK_EQUAL(test_klondike.setCommand(Klondike::Command_TimeSync), NoError);

    test_klondike.time = 0x456789ab;

    do_xfer(test_klondike, test_wrap, xfer, (nice_buffer(), 0x21, 0xab, 0x89, 0x67, 0x45),
                                            (nice_buffer(), 0x80, 0x21, 0x00, 0x00));
    cout << "Transaction " << ++transactions << endl;

    //  then queue up a queued command
    Test_Klondike::byte_buffer_t timesync_standin;

    timesync_standin.push_back(0x12);
    timesync_standin.push_back(0x34);
    timesync_standin.push_back(0x56);

    test_klondike.addQueuedWork(0,
                                timesync_standin,
                                MAXPRIORITY,
                                Klondike::DLCParms_BroadcastFlag,
                                0);

    //  and set the command
    BOOST_CHECK_EQUAL(test_klondike.setCommand(Klondike::Command_LoadQueue), NoError);

    //  verify it grabs the status from the CCU first
    do_xfer(test_klondike, test_wrap, xfer, (nice_buffer(), 0x11),
                                            (nice_buffer(), 0x81, 0x11, 0x00, 0x00, 0x04, 0x37, 0x00));
    cout << "Transaction " << ++transactions << endl;

    //  then loads the queued request
    do_xfer(test_klondike, test_wrap, xfer, (nice_buffer(), 0x13, 0x37, 0x00, 0x01, 0x0f, 0x10, 0x00, 0x03, 0x12, 0x34, 0x56),
                                            (nice_buffer(), 0x81, 0x13, 0x08, 0x00, 0x01, 0x03));
    cout << "Transaction " << ++transactions << endl;
}

