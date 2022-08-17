//#define BOOST_TEST_MAIN

#define WIN32_LEAN_AND_MEAN

#include <boost/test/auto_unit_test.hpp>

using namespace std;

#define private public
#define protected public

#include "message.h"
#include "msg_cmd.h"
#include "msg_dbchg.h"
#include "msg_lmcontrolhistory.h"
#include "msg_multi.h"
#include "msg_notif_alarm.h"
#include "msg_notif_email.h"
#include "msg_notif_lmcontrol.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "msg_queuedata.h"
#include "Msg_reg.h"
#include "msg_requestcancel.h"
#include "msg_server_req.h"
#include "msg_server_resp.h"
#include "msg_signal.h"
#include "msg_tag.h"
#include "msg_trace.h"

#undef private
#undef protected

#include "test_serialization.h"

BOOST_AUTO_TEST_SUITE( test_serialization )

BOOST_AUTO_TEST_CASE( test_CtiMessage )
{
    TestSequence<TestCase<CtiMessage>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiCommandMsg )
{
    TestSequence<TestCase<CtiCommandMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiDBChangeMsg )
{
    TestSequence<TestCase<CtiDBChangeMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiLMControlHistoryMsg )
{
    TestSequence<TestCase<CtiLMControlHistoryMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiMultiMsg )
{
    TestSequence<TestCase<CtiMultiMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiNotifAlarmMsg )
{
    TestSequence<TestCase<CtiNotifAlarmMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiNotifEmailMsg )
{
    TestSequence<TestCase<CtiNotifEmailMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiCustomerNotifEmailMsg )
{
    TestSequence<TestCase<CtiCustomerNotifEmailMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiNotifLMControlMsg )
{
    TestSequence<TestCase<CtiNotifLMControlMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiRequestMsg )
{
    TestSequence<TestCase<CtiRequestMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiReturnMsg )
{
    TestSequence<TestCase<CtiReturnMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiPointDataMsg )
{
    TestSequence<TestCase<CtiPointDataMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiPointRegistrationMsg )
{
    TestSequence<TestCase<CtiPointRegistrationMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiQueueDataMsg )
{
    TestSequence<TestCase<CtiQueueDataMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiRegistrationMsg )
{
    TestSequence<TestCase<CtiRegistrationMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiRequestCancelMsg )
{
    TestSequence<TestCase<CtiRequestCancelMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiServerRequestMsg )
{
    TestSequence<TestCase<CtiServerRequestMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiServerResponseMsg )
{
    TestSequence<TestCase<CtiServerResponseMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiSignalMsg )
{
    TestSequence<TestCase<CtiSignalMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiTagMsg )
{
    TestSequence<TestCase<CtiTagMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiTraceMsg )
{
    TestSequence<TestCase<CtiTraceMsg>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_SUITE_END()
