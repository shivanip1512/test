#include <boost/test/unit_test.hpp>

#include "DeviceAttributeLookup.h"
#include "desolvers.h"

// pretty print Attribute error messages
std::ostream& operator<<( std::ostream & os, const Attribute & a )
{
    return os << a.getName();
}


BOOST_AUTO_TEST_SUITE( test_DeviceAttributeLookup )

BOOST_AUTO_TEST_CASE(test_unknownAttributes)
{
    const auto unknowns = Cti::DeviceAttributeLookup::GetUnknownDeviceAttributes();

    for( const auto& deviceAttributes : unknowns )
    {
        BOOST_ERROR( "Device type " + desolveDeviceType(deviceAttributes.first) + " has unknown attribute " + deviceAttributes.second + 
                     " - attributes may have been added to BuiltInAttributes.java without being added to Attribute.cpp and Attribute.h" );
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
    {
        auto point = Cti::DeviceAttributeLookup::Lookup(DeviceTypes::TYPE_RFN420CLW, Attribute::WiFiBssidChange);

        BOOST_REQUIRE(point);

        BOOST_CHECK_EQUAL(point->offset, 98);
        BOOST_CHECK_EQUAL(point->type, 0);
    }
    {
        auto point = Cti::DeviceAttributeLookup::Lookup(DeviceTypes::TYPE_RFN510FL, Attribute::Voltage);

        BOOST_REQUIRE(point);

        BOOST_CHECK_EQUAL(point->offset, 214);
        BOOST_CHECK_EQUAL(point->type, 1);
    }
    {
        auto point = Cti::DeviceAttributeLookup::Lookup(DeviceTypes::TYPE_RFN530S4EAX, Attribute::UsageReading);

        BOOST_REQUIRE(point);

        BOOST_CHECK_EQUAL(point->offset, 3);
        BOOST_CHECK_EQUAL(point->type, 1);
    }
    {
        auto point = Cti::DeviceAttributeLookup::Lookup(DeviceTypes::TYPE_RFN530S4EAX, Attribute::PeakDemand);

        BOOST_REQUIRE(point);

        BOOST_CHECK_EQUAL(point->offset, 265);
        BOOST_CHECK_EQUAL(point->type, 1);
    }
    {
        auto point = Cti::DeviceAttributeLookup::Lookup(DeviceTypes::TYPE_RFN530S4X, Attribute::UsageReading);

        BOOST_REQUIRE(point);

        BOOST_CHECK_EQUAL(point->offset, 1);
        BOOST_CHECK_EQUAL(point->type, 1);
    }
    {
        auto point = Cti::DeviceAttributeLookup::Lookup(DeviceTypes::TYPE_RFN530S4X, Attribute::PeakDemand);

        BOOST_REQUIRE(point);

        BOOST_CHECK_EQUAL(point->offset, 105);
        BOOST_CHECK_EQUAL(point->type, 1);
    }
    {
        auto point = Cti::DeviceAttributeLookup::Lookup(DeviceTypes::PortTypeLocalDirect, Attribute::PortQueueCount);

        BOOST_REQUIRE(point);

        BOOST_CHECK_EQUAL(point->offset, 1);
        BOOST_CHECK_EQUAL(point->type, 1);
    }
}

BOOST_AUTO_TEST_CASE(test_Lookup_failure)
{
    auto point = Cti::DeviceAttributeLookup::Lookup(DeviceTypes::TYPE_RFN420CL, Attribute::CalcCpuUtilization);

    BOOST_CHECK_EQUAL( point.is_initialized(), false );
}

BOOST_AUTO_TEST_CASE(test_AttributeLookup)
{
    {
        auto attributes = Cti::DeviceAttributeLookup::AttributeLookup( DeviceTypes::TYPE_RFN420CL, AnalogPointType, 1 );

        BOOST_CHECK_EQUAL( attributes.size(),   2 );

        BOOST_CHECK( std::find( attributes.begin(), attributes.end(), Attribute::UsageReading )
                        != attributes.end() );

        BOOST_CHECK( std::find( attributes.begin(), attributes.end(), Attribute::DeliveredkWh )
                        != attributes.end() );
    }
    {
        auto attributes = Cti::DeviceAttributeLookup::AttributeLookup( DeviceTypes::TYPE_RFN420CL, AnalogPointType, 2 );

        BOOST_CHECK_EQUAL( attributes.size(),   1 );

        BOOST_CHECK( std::find( attributes.begin(), attributes.end(), Attribute::ReceivedkWh )
                        != attributes.end() );
    }
    {
        auto attributes = Cti::DeviceAttributeLookup::AttributeLookup( DeviceTypes::TYPE_CBC8020, AnalogPointType, 1234 );

        BOOST_CHECK_EQUAL( attributes.size(),   0 );
    }
}

BOOST_AUTO_TEST_SUITE_END()
