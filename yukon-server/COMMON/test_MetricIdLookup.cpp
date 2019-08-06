#include <boost/test/unit_test.hpp>

#include "MetricIdLookup.h"

BOOST_AUTO_TEST_SUITE( test_MetricIdLookup )

BOOST_AUTO_TEST_CASE(test_unknownAttributes)
{
    const auto unknowns = Cti::MetricIdLookup::getUnknownAttributes();

    if( ! unknowns.empty() )
    {
        BOOST_ERROR( "Metric ID to Attribute mapping has unknown attributes; "
                     "attributes may have been added to BuiltInAttributes.java without being added to Attribute.cpp and Attribute.h" );

        for( const auto& attributeNotFound : unknowns )
        {
            BOOST_ERROR( attributeNotFound.desc );
        }
    }
}

BOOST_AUTO_TEST_CASE(test_overrideFailures)
{
    const auto unmapped = Cti::MetricIdLookup::getUnmappedAttributes();

    for( const auto& metricIdNotFound : unmapped )
    {
        BOOST_ERROR( "Attribute override mapping has unmapped attribute " + metricIdNotFound.attribute.getName() + 
            " - attributes must be added to metricMapping before they can be overridden in attributeOverrides" );
    }
}

BOOST_AUTO_TEST_CASE(test_GetMetricId_success)
{
    {
        const auto metricId = Cti::MetricIdLookup::GetMetricId(Attribute::DeliveredkWh, TYPE_RFN420CL);

        BOOST_CHECK_EQUAL(metricId, 1);
    }
    {
        const auto metricId = Cti::MetricIdLookup::GetMetricId(Attribute::DeliveredDemand, TYPE_RFN420CL);

        BOOST_CHECK_EQUAL(metricId, 5);
    }
    {
        const auto metricId = Cti::MetricIdLookup::GetMetricId(Attribute::InstantaneouskW, TYPE_RFN430SL1);

        BOOST_CHECK_EQUAL(metricId, 200);
    }
    {
        const auto metricId = Cti::MetricIdLookup::GetMetricId(Attribute::kVAr, TYPE_RFN430SL1);

        BOOST_CHECK_EQUAL(metricId, 201);
    }
    {
        const auto metricId = Cti::MetricIdLookup::GetMetricId(Attribute::kVA, TYPE_RFN430SL1);

        BOOST_CHECK_EQUAL(metricId, 202);
    }
    {
        const auto metricId = Cti::MetricIdLookup::GetMetricId(Attribute::PeakDemand, TYPE_RFN530S4EAX);

        BOOST_CHECK_EQUAL(metricId, 13);
    }
    {
        const auto metricId = Cti::MetricIdLookup::GetMetricId(Attribute::PeakDemand, TYPE_RFN530S4X);

        BOOST_CHECK_EQUAL(metricId, 7);
    }
}

BOOST_AUTO_TEST_CASE(test_GetForAttribute_failure)
{
    try
    {
        Cti::MetricIdLookup::GetMetricId(Attribute::CalcCpuUtilization, TYPE_RFN420CL);
        BOOST_FAIL("Did not throw");
    }
    catch( const Cti::MetricMappingNotFound& ex )
    {
        BOOST_CHECK_EQUAL( ex.detail, "Metric ID not found for attribute CALC_CPU_UTILIZATION" );
    }
}

BOOST_AUTO_TEST_CASE(test_getAttribute_success)
{
    const auto attrib = Cti::MetricIdLookup::GetAttribute(1, TYPE_RFN420CL);

    BOOST_CHECK(attrib == Attribute::DeliveredkWh);
    BOOST_CHECK_EQUAL(attrib.getName(), "DELIVERED_KWH");
}

BOOST_AUTO_TEST_CASE(test_getAttribute_override)
{
    {
        const auto attrib = Cti::MetricIdLookup::GetAttribute(5, TYPE_RFN420CL);

        BOOST_CHECK(attrib == Attribute::DeliveredDemand);
        BOOST_CHECK_EQUAL(attrib.getName(), "DELIVERED_DEMAND");
    }

    {
        const auto attrib = Cti::MetricIdLookup::GetAttribute(200, TYPE_RFN430SL1);

        BOOST_CHECK(attrib == Attribute::InstantaneouskW);
        BOOST_CHECK_EQUAL(attrib.getName(), "INSTANTANEOUS_KW");
    }

    {
        const auto attrib = Cti::MetricIdLookup::GetAttribute(13, TYPE_RFN530S4EAX);

        BOOST_CHECK(attrib == Attribute::PeakDemand);
        BOOST_CHECK_EQUAL(attrib.getName(), "PEAK_DEMAND");
    }

    {
        const auto attrib = Cti::MetricIdLookup::GetAttribute(13, TYPE_RFN530S4X);

        BOOST_CHECK(attrib == Attribute::SumPeakDemand);
        BOOST_CHECK_EQUAL(attrib.getName(), "SUM_PEAK_DEMAND");
    }
}

BOOST_AUTO_TEST_CASE(test_getAttribute_failure)
{
    try
    {
        Cti::MetricIdLookup::GetAttribute(-1, TYPE_RFN420CL);
    }
    catch( const Cti::AttributeMappingNotFound& ex )
    {
        BOOST_CHECK_EQUAL( ex.detail, "Attribute mapping not found for metric ID 65535" );
    }

    try
    {
        Cti::MetricIdLookup::GetAttribute(-1, TYPE_RFN430SL0);
    }
    catch( const Cti::AttributeMappingNotFound& ex )
    {
        BOOST_CHECK_EQUAL( ex.detail, "Attribute mapping not found for metric ID 65535" );
    }
}

BOOST_AUTO_TEST_CASE(test_GetAttributeDescription_success)
{
    {
        const auto attribDesc = Cti::MetricIdLookup::GetAttributeDescription(1, TYPE_RFN420CL);

        BOOST_CHECK(attribDesc.attrib == Attribute::DeliveredkWh);
        BOOST_CHECK_EQUAL(attribDesc.attrib.getName(), "DELIVERED_KWH");
        BOOST_CHECK_EQUAL(attribDesc.magnitude, 3);
    }
    {
        const auto attribDesc = Cti::MetricIdLookup::GetAttributeDescription(80, TYPE_RFN420CL);

        BOOST_CHECK(attribDesc.attrib == Attribute::PowerFactor);
        BOOST_CHECK_EQUAL(attribDesc.attrib.getName(), "POWER_FACTOR");
        BOOST_CHECK_EQUAL(attribDesc.magnitude, 3);
    }
}

BOOST_AUTO_TEST_CASE(test_GetAttributeDescription_failure)
{
    try
    {
        Cti::MetricIdLookup::GetAttributeDescription(-1, TYPE_RFN420CL);
    }
    catch( const Cti::AttributeMappingNotFound& ex )
    {
        BOOST_CHECK_EQUAL( ex.detail, "Attribute mapping not found for metric ID 65535" );
    }
}

BOOST_AUTO_TEST_SUITE_END()
