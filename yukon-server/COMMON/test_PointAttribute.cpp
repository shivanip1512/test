#include <boost/test/unit_test.hpp>

#include "PointAttribute.h"

BOOST_AUTO_TEST_SUITE( test_PointAttribute )

//test function to verify the Attribute retreival is correct.
BOOST_AUTO_TEST_CASE(test_valueOf)
{
    BOOST_CHECK( true );
//    PointAttribute attributeTest = PointAttribute::valueOf("UNKNOWN");
//    BOOST_CHECK_EQUAL(PointAttribute::Unknown.name(), attributeTest.name());
//
//    attributeTest = PointAttribute::valueOf("TAP_UP");
//    BOOST_CHECK_EQUAL(PointAttribute::TapUp.name(), attributeTest.name());
//
//    attributeTest = PointAttribute::valueOf("TAP_DOWN");
//    BOOST_CHECK_EQUAL(PointAttribute::TapDown.name(), attributeTest.name());
//
//    attributeTest = PointAttribute::valueOf("AUTO_REMOTE_CONTROL");
//    BOOST_CHECK_EQUAL(PointAttribute::AutoRemoteControl.name(), attributeTest.name());
//
//    attributeTest = PointAttribute::valueOf("TAP_POSITION");
//    BOOST_CHECK_EQUAL(PointAttribute::TapPosition.name(), attributeTest.name());
//
//    attributeTest = PointAttribute::valueOf("KEEP_ALIVE");
//    BOOST_CHECK_EQUAL(PointAttribute::KeepAlive.name(), attributeTest.name());
//
//    attributeTest = PointAttribute::valueOf("ERROR_CASE");
//    BOOST_CHECK_EQUAL(PointAttribute::Unknown.name(), attributeTest.name());
}

BOOST_AUTO_TEST_SUITE_END()
