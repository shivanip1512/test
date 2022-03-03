//#define BOOST_TEST_MAIN

#define WIN32_LEAN_AND_MEAN

#include <boost/test/unit_test.hpp>

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

#include "mc_msg.h"
#include "mc_sched.h"
#include "mc_script.h"

#undef private
#undef protected

#include "test_mc_serialization.h"

BOOST_AUTO_TEST_SUITE( test_serialization )

BOOST_AUTO_TEST_CASE( test_CtiMCSchedule )
{
    TestSequence<TestCase<CtiMCSchedule>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiMCUpdateSchedule )
{
    TestSequence<TestCase<CtiMCUpdateSchedule>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiMCAddSchedule )
{
    TestSequence<TestCase<CtiMCAddSchedule>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiMCDeleteSchedule )
{
    TestSequence<TestCase<CtiMCDeleteSchedule>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiMCRetrieveSchedule )
{
    TestSequence<TestCase<CtiMCRetrieveSchedule>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiMCRetrieveScript )
{
    TestSequence<TestCase<CtiMCRetrieveScript>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiMCOverrideRequest )
{
    TestSequence<TestCase<CtiMCOverrideRequest>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiMCInfo )
{
    TestSequence<TestCase<CtiMCInfo>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_CASE( test_CtiMCScript )
{
    TestSequence<TestCase<CtiMCScript>> tc;
    BOOST_CHECK_EQUAL( 0, tc.Run() );
}

BOOST_AUTO_TEST_SUITE_END()
