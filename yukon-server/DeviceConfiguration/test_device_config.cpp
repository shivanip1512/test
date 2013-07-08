#include <boost/test/unit_test.hpp>

#include "config_device.h"

struct test_DeviceConfig : public Cti::Config::DeviceConfig
{
    test_DeviceConfig() : DeviceConfig(-1, "", "") {}

    using DeviceConfig::insertValue;
};

BOOST_AUTO_TEST_SUITE( test_device_config )

BOOST_AUTO_TEST_CASE(test_findLongValueForKey)
{
    test_DeviceConfig dc;

    {
        boost::optional<long> value;

        value = dc.findLongValueForKey("foo");

        BOOST_CHECK( ! value );
    }

    dc.insertValue("foo", "17");

    {
        boost::optional<long> value;

        value = dc.findLongValueForKey("foo");

        BOOST_REQUIRE( value );
        BOOST_CHECK_EQUAL( *value, 17 );
    }
}

BOOST_AUTO_TEST_SUITE_END()

