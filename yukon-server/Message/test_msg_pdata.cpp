#include <boost/test/unit_test.hpp>

#include "msg_pdata.h"

#include "utility.h"

BOOST_AUTO_TEST_SUITE( test_msg_pdata )

BOOST_AUTO_TEST_CASE(test_tags)
{
    CtiPointDataMsg msg;

    BOOST_CHECK_EQUAL(false, msg.isOldTimestamp());
    BOOST_CHECK_EQUAL(false, msg.isRegistrationUpload());

    msg.setTags(TAG_POINT_DATA_UNSOLICITED);

    BOOST_CHECK_EQUAL(false, msg.isOldTimestamp());
    BOOST_CHECK_EQUAL(false, msg.isRegistrationUpload());

    msg.setTags(TAG_POINT_OLD_TIMESTAMP);

    BOOST_CHECK_EQUAL(true,  msg.isOldTimestamp());
    BOOST_CHECK_EQUAL(false, msg.isRegistrationUpload());

    msg.setTags(TAG_POINT_REG_UPLOAD);

    BOOST_CHECK_EQUAL(true, msg.isOldTimestamp());
    BOOST_CHECK_EQUAL(true, msg.isRegistrationUpload());

    msg.resetTags(0xffffffff);

    BOOST_CHECK_EQUAL(false, msg.isOldTimestamp());
    BOOST_CHECK_EQUAL(false, msg.isRegistrationUpload());

    msg.setTags(0xffffffff);

    BOOST_CHECK_EQUAL(true, msg.isOldTimestamp());
    BOOST_CHECK_EQUAL(true, msg.isRegistrationUpload());
}

BOOST_AUTO_TEST_SUITE_END()
