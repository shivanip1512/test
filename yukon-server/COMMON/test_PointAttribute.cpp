#include "yukon.h"

#include "PointAttribute.h"

#define BOOST_AUTO_TEST_MAIN "Test Point Attribute"

#include <boost/test/unit_test.hpp>

using boost::unit_test_framework::test_suite;


BOOST_AUTO_TEST_CASE(test_valueOf)
{
    PointAttribute attributeTest = PointAttribute::valueOf("UNKNOWN");
    BOOST_CHECK_EQUAL(PointAttribute::Unknown.name(), attributeTest.name());

    attributeTest = PointAttribute::valueOf("RAISE_TAP");
    BOOST_CHECK_EQUAL(PointAttribute::RaiseTap.name(), attributeTest.name());

    attributeTest = PointAttribute::valueOf("LOWER_TAP");
    BOOST_CHECK_EQUAL(PointAttribute::LowerTap.name(), attributeTest.name());

    attributeTest = PointAttribute::valueOf("LTC_VOLTAGE");
    BOOST_CHECK_EQUAL(PointAttribute::LtcVoltage.name(), attributeTest.name());

    attributeTest = PointAttribute::valueOf("AUTO_REMOTE_CONTROL");
    BOOST_CHECK_EQUAL(PointAttribute::AutoRemoteControl.name(), attributeTest.name());

    attributeTest = PointAttribute::valueOf("UPPER_VOLT_LIMIT");
    BOOST_CHECK_EQUAL(PointAttribute::UpperVoltLimit.name(), attributeTest.name());

    attributeTest = PointAttribute::valueOf("LOWER_VOLT_LIMIT");
    BOOST_CHECK_EQUAL(PointAttribute::LowerVoltLimit.name(), attributeTest.name());

    attributeTest = PointAttribute::valueOf("KEEP_ALIVE");
    BOOST_CHECK_EQUAL(PointAttribute::KeepAlive.name(), attributeTest.name());

    attributeTest = PointAttribute::valueOf("ERROR_CASE");
    BOOST_CHECK_EQUAL(PointAttribute::Unknown.name(), attributeTest.name());


}

