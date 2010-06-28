/*
 * test Cti::Protocol::Klondike
 *
 */

#include <boost/test/floating_point_comparison.hpp>

#define BOOST_TEST_MAIN "Test fifo_multiset"
#include <boost/test/unit_test.hpp>


#include "boostutil.h"

using boost::unit_test_framework::test_suite;

#include <iostream>
#include "prot_klondike.h"
#include "dev_ccu721.h"  //  for timesync writes

#define BOOST_AUTO_TEST_MAIN "Test Klondike Protocol"
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

    nice_buffer &operator, (unsigned char c) {  v.push_back(c);  return *this;  };
    nice_buffer &operator<<(unsigned char c) {  v.push_back(c);  return *this;  };
    operator string()  {  return string(v.begin(), v.end());  }
};


void do_xfer(Test_Klondike &tk, Test_Wrap &tw, CtiXfer &xfer, string outbound, string inbound)
{
    //  first do the send...
    BOOST_CHECK_EQUAL(tk.generate(xfer), NoError);

    // check what was assigned into our Test_Wrap object
    BOOST_CHECK_EQUAL(tw.sent.size(), outbound.size());
    BOOST_CHECK(!memcmp( &*(tw.sent.begin()), &*outbound.begin(), outbound.size()));

    BOOST_CHECK_EQUAL(tk.decode(xfer, 0), NoError);
    BOOST_CHECK(!tk.errorCondition());

    tw.sent.clear();
    tw.received.clear();

    //  then do the receive
    BOOST_CHECK_EQUAL(tk.generate(xfer), NoError);

    BOOST_CHECK_EQUAL(tw.sent.size(), 0);

    //  assign our inbound data into the Test_Wrap object
    inbound.c_str();

    string::const_iterator iter = inbound.begin();
    for( ;iter != inbound.end(); iter++ )
    {
        tw.received.push_back(*iter);
    }

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
    BOOST_CHECK_EQUAL(test_klondike.setCommand(Klondike::Command_TimeSync), NoError);

    test_klondike.time = 0x456789ab;

    do_xfer(test_klondike, test_wrap, xfer, (nice_buffer() << 0x21, 0xab, 0x89, 0x67, 0x45),
                                            (nice_buffer() << 0x80, 0x21, 0x00, 0x00));
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
    do_xfer(test_klondike, test_wrap, xfer, (nice_buffer() << 0x11),
                                            (nice_buffer() << 0x81, 0x11, 0x00, 0x00, 0x04, 0x37, 0x00));
    cout << "Transaction " << ++transactions << endl;

    //  then loads the queued request
    do_xfer(test_klondike, test_wrap, xfer, (nice_buffer() << 0x13, 0x37, 0x00, 0x01, 0x0f, 0x10, 0x00, 0x03, 0x12, 0x34, 0x56),
                                            (nice_buffer() << 0x81, 0x13, 0x08, 0x00, 0x01, 0x03));
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

    BOOST_CHECK_EQUAL(test_klondike.setCommand(Klondike::Command_LoadRoutes), NoError);

    test_klondike.addRoute(1, 27, 3, 6);

    do_xfer(test_klondike, test_wrap, xfer, (nice_buffer() << 0x34, 0x00),
                                            (nice_buffer() << 0x80, 0x21, 0x00, 0x00));
    cout << "Transaction " << ++transactions << endl;

    //  fixed, variable, stages, bus

    do_xfer(test_klondike, test_wrap, xfer, (nice_buffer() << 0x31, 0x01, 0x00, 0xdb, 0x31),
                                            (nice_buffer() << 0x80, 0x21, 0x00, 0x00));
    cout << "Transaction " << ++transactions << endl;

    BOOST_CHECK_EQUAL(test_klondike.setCommand(Klondike::Command_LoadRoutes), NoError);

    test_klondike.addRoute(2, 16, 5, 2);

    do_xfer(test_klondike, test_wrap, xfer, (nice_buffer() << 0x34, 0x00),
                                            (nice_buffer() << 0x80, 0x21, 0x00, 0x00));
    cout << "Transaction " << ++transactions << endl;

    do_xfer(test_klondike, test_wrap, xfer, (nice_buffer() << 0x31, 0x02, 0x00, 0xdb, 0x31, 0x01, 0x85, 0x12),
                                            (nice_buffer() << 0x80, 0x21, 0x00, 0x00));
    cout << "Transaction " << ++transactions << endl;
}


bool findAllMessages(void *, void *)
{
    return true;
}


bool findRequestId(void *request_id, void *om)
{
    return om && (unsigned long)request_id == ((OUTMESS *)om)->Request.GrpMsgID;
}


BOOST_AUTO_TEST_CASE(test_prot_klondike_queue_handler_find_all)
{
    Test_Klondike test_klondike;

    OUTMESS om;

    om.Request.GrpMsgID = 112358;

    //  verify the queues start out empty
    BOOST_CHECK_EQUAL(test_klondike.getQueueCount(0), 0);
    BOOST_CHECK_EQUAL(test_klondike.getQueueCount(112358), 0);

    BOOST_CHECK(!test_klondike.hasWaitingWork());
    BOOST_CHECK(!test_klondike.hasQueuedWork());
    BOOST_CHECK(!test_klondike.hasRemoteWork());

    //  add the outmessage, ignoring the unimportant pieces
    test_klondike.addQueuedWork(&om, Klondike::byte_buffer_t(), 0, 0, 0);

    //  verify it got in there with the correct requestid
    BOOST_CHECK_EQUAL(test_klondike.getQueueCount(0), 0);
    BOOST_CHECK_EQUAL(test_klondike.getQueueCount(112358), 1);

    BOOST_CHECK(test_klondike.hasWaitingWork());
    BOOST_CHECK(test_klondike.hasQueuedWork());
    BOOST_CHECK(!test_klondike.hasRemoteWork());

    std::list<void *> entries;

    //  pull out all OMs
    test_klondike.retrieveQueueEntries(findAllMessages, NULL, entries);

    //  verify the OM is no longer in there
    BOOST_CHECK_EQUAL(test_klondike.getQueueCount(0), 0);
    BOOST_CHECK_EQUAL(test_klondike.getQueueCount(112358), 0);

    BOOST_CHECK(!test_klondike.hasWaitingWork());
    BOOST_CHECK(!test_klondike.hasQueuedWork());
    BOOST_CHECK(!test_klondike.hasRemoteWork());

    //  and that we got the right OM back
    BOOST_CHECK_EQUAL(entries.size(), 1);
    BOOST_CHECK_EQUAL(entries.front(), &om);
}


BOOST_AUTO_TEST_CASE(test_prot_klondike_queue_handler_find_requestid)
{
    Test_Klondike test_klondike;

    OUTMESS om;

    om.Request.GrpMsgID = 112358;

    //  verify the queues start out empty
    BOOST_CHECK_EQUAL(test_klondike.getQueueCount(0), 0);
    BOOST_CHECK_EQUAL(test_klondike.getQueueCount(112358), 0);

    BOOST_CHECK(!test_klondike.hasWaitingWork());
    BOOST_CHECK(!test_klondike.hasQueuedWork());
    BOOST_CHECK(!test_klondike.hasRemoteWork());

    //  add the outmessage, ignoring the unimportant pieces
    test_klondike.addQueuedWork(&om, Klondike::byte_buffer_t(), 0, 0, 0);

    //  verify it got in there with the correct requestid
    BOOST_CHECK_EQUAL(test_klondike.getQueueCount(0), 0);
    BOOST_CHECK_EQUAL(test_klondike.getQueueCount(112358), 1);

    BOOST_CHECK(test_klondike.hasWaitingWork());
    BOOST_CHECK(test_klondike.hasQueuedWork());
    BOOST_CHECK(!test_klondike.hasRemoteWork());

    std::list<void *> entries;

    //  try to grab a bogus requestID
    test_klondike.retrieveQueueEntries(findRequestId, (void *)111111, entries);

    //  verify our OM is still in there
    BOOST_CHECK_EQUAL(test_klondike.getQueueCount(0), 0);
    BOOST_CHECK_EQUAL(test_klondike.getQueueCount(112358), 1);

    BOOST_CHECK(test_klondike.hasWaitingWork());
    BOOST_CHECK(test_klondike.hasQueuedWork());
    BOOST_CHECK(!test_klondike.hasRemoteWork());

    BOOST_CHECK_EQUAL(entries.size(), 0);

    //  grab the right requestID
    test_klondike.retrieveQueueEntries(findRequestId, (void *)112358, entries);

    //  verify the OM is no longer in there
    BOOST_CHECK_EQUAL(test_klondike.getQueueCount(0), 0);
    BOOST_CHECK_EQUAL(test_klondike.getQueueCount(112358), 0);

    BOOST_CHECK(!test_klondike.hasWaitingWork());
    BOOST_CHECK(!test_klondike.hasQueuedWork());
    BOOST_CHECK(!test_klondike.hasRemoteWork());

    //  and that we got the right OM back
    BOOST_CHECK_EQUAL(entries.size(), 1);
    BOOST_CHECK_EQUAL(entries.front(), &om);
}
