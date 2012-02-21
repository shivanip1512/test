#include <boost/test/unit_test.hpp>

#include "encryption_oneway_message.h"
#include "CtiDate.h"
#include "CtiTime.h"

BOOST_AUTO_TEST_SUITE( test_encryption_oneway_message )

BOOST_AUTO_TEST_CASE(test_one_way_message_sequence_generator)
{
    CtiTime timeNow(CtiDate(31, 12, 1999), 18, 0, 0);

    CtiTime  lastXmitTime;
    unsigned utcCounter;

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    BOOST_CHECK_EQUAL( lastXmitTime , timeNow   );
    BOOST_CHECK_EQUAL( utcCounter   , 0         );

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    BOOST_CHECK_EQUAL( lastXmitTime , timeNow   );
    BOOST_CHECK_EQUAL( utcCounter   , 1         );

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    BOOST_CHECK_EQUAL( lastXmitTime , timeNow   );
    BOOST_CHECK_EQUAL( utcCounter   , 2         );

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    BOOST_CHECK_EQUAL( lastXmitTime , timeNow   );
    BOOST_CHECK_EQUAL( utcCounter   , 3         );

    timeNow += 5;

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    BOOST_CHECK_EQUAL( lastXmitTime , timeNow   );
    BOOST_CHECK_EQUAL( utcCounter   , 0         );

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    BOOST_CHECK_EQUAL( lastXmitTime , timeNow   );
    BOOST_CHECK_EQUAL( utcCounter   , 1         );

    timeNow += 1;

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    BOOST_CHECK_EQUAL( lastXmitTime , timeNow   );
    BOOST_CHECK_EQUAL( utcCounter   , 0         );

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    BOOST_CHECK_EQUAL( lastXmitTime , timeNow   );
    BOOST_CHECK_EQUAL( utcCounter   , 1         );

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    BOOST_CHECK_EQUAL( lastXmitTime , timeNow   );
    BOOST_CHECK_EQUAL( utcCounter   , 2         );
}

BOOST_AUTO_TEST_SUITE_END()
