#include <boost/test/unit_test.hpp>

#include "DeviceConfigDescription.h"

BOOST_AUTO_TEST_SUITE( test_DeviceConfigDescription )

BOOST_AUTO_TEST_CASE(test_unknownDeviceTypes)
{
    const auto unknowns = Cti::DeviceConfigDescription::GetUnknownDeviceTypes();

    for( const auto deviceType : unknowns )
    {
        BOOST_ERROR("XML device type string \"" + deviceType + "\" did not map to a known DeviceType in resource_helper.cpp");
    }
}

BOOST_AUTO_TEST_SUITE_END()
