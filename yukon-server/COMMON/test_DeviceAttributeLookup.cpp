#include <boost/test/unit_test.hpp>

#include "DeviceAttributeLookup.h"

BOOST_AUTO_TEST_SUITE( test_DeviceAttributeLookup )

BOOST_AUTO_TEST_CASE(test_unknownAttributes)
{
    const auto unknowns = Cti::DeviceAttributeLookup::GetUnknownDeviceAttributes();

    for( const auto& deviceAttributes : unknowns )
    {
        BOOST_ERROR( "Device type " + std::to_string(deviceAttributes.first) + " has unknown attribute " + deviceAttributes.second + 
                     " - attributes may have been added to BuiltInAttributes.java without being added to PointAttribute.cpp and PointAttribute.h" );
    }
}

BOOST_AUTO_TEST_CASE(test_Lookup_success)
{
    {
        auto point = Cti::DeviceAttributeLookup::Lookup(DeviceTypes::TYPE_RFN420CL, Attribute::DeliveredkWh);

        BOOST_REQUIRE(point);

        BOOST_CHECK_EQUAL(point->offset, 1);
        BOOST_CHECK_EQUAL(point->type, 1);
    }
    {
        auto point = Cti::DeviceAttributeLookup::Lookup(DeviceTypes::TYPE_RFN420CL, Attribute::ReceivedkWh);

        BOOST_REQUIRE(point);

        BOOST_CHECK_EQUAL(point->offset, 2);
        BOOST_CHECK_EQUAL(point->type, 1);
    }
    {
        auto point = Cti::DeviceAttributeLookup::Lookup(DeviceTypes::TYPE_RFN420CL, Attribute::Voltage);

        BOOST_REQUIRE(point);

        BOOST_CHECK_EQUAL(point->offset, 214);
        BOOST_CHECK_EQUAL(point->type, 1);
    }
}

BOOST_AUTO_TEST_CASE(test_Lookup_failure)
{
    auto point = Cti::DeviceAttributeLookup::Lookup(DeviceTypes::TYPE_RFN420CL, Attribute::CalcCpuUtilization);

    BOOST_CHECK_EQUAL( point.is_initialized(), false );
}

BOOST_AUTO_TEST_SUITE_END()
