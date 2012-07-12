#include <boost/test/unit_test.hpp>

#include "dev_cbc7020.h"



BOOST_AUTO_TEST_SUITE( test_dev_cbc7020 )


struct TestCbc7020Device : Cti::Devices::Cbc7020Device
{
    using Cbc7020Device::PointOffset_FirmwareRevision;
    using Cbc7020Device::processFirmwarePoint;
};


BOOST_AUTO_TEST_CASE(test_test_dev_cbc7020_firmware_points_no_points_present)
{
    Cti::Protocol::Interface::pointlist_t   points;

    TestCbc7020Device::processFirmwarePoint( points );

    // No points went in, we better come back with none!
    BOOST_REQUIRE_EQUAL( points.size(), 0 );
}


BOOST_AUTO_TEST_CASE(test_test_dev_cbc7020_firmware_points_nonfirmware_points_present)
{
    Cti::Protocol::Interface::pointlist_t   points;

    // push in a non-firmware point

    points.push_back( new CtiPointDataMsg( 42, 3.1415, NormalQuality, AnalogPointType ) );

    TestCbc7020Device::processFirmwarePoint( points );

    // non-firmware point should be unchanged

    BOOST_REQUIRE_EQUAL( points.size(),             1 );

    BOOST_REQUIRE_EQUAL( points[0]->getId(),        42 );
    BOOST_REQUIRE_EQUAL( points[0]->getType(),      AnalogPointType );
    BOOST_REQUIRE_EQUAL( points[0]->getQuality(),   NormalQuality );
    BOOST_REQUIRE_CLOSE( points[0]->getValue(),     3.1415,     1e-9 );

    delete_container(points);
}


BOOST_AUTO_TEST_CASE(test_test_dev_cbc7020_firmware_points_points_present)
{
    Cti::Protocol::Interface::pointlist_t   points;

    // push in a non-firmware point

    points.push_back( new CtiPointDataMsg( 42, 3.1415, NormalQuality, AnalogPointType ) );

    // and a firmware point -- value = 0x0805 == 2053

    points.push_back( new CtiPointDataMsg( TestCbc7020Device::PointOffset_FirmwareRevision,
                                           0x0805,
                                           NormalQuality, AnalogPointType ) );

    TestCbc7020Device::processFirmwarePoint( points );

    BOOST_REQUIRE_EQUAL( points.size(),             2 );

    // non-firmware point should be unchanged

    BOOST_REQUIRE_EQUAL( points[0]->getId(),        42 );
    BOOST_REQUIRE_EQUAL( points[0]->getType(),      AnalogPointType );
    BOOST_REQUIRE_EQUAL( points[0]->getQuality(),   NormalQuality );
    BOOST_REQUIRE_CLOSE( points[0]->getValue(),     3.1415,     1e-9 );

    // firmware point should be modified

    BOOST_REQUIRE_EQUAL( points[1]->getId(),        TestCbc7020Device::PointOffset_FirmwareRevision );
    BOOST_REQUIRE_EQUAL( points[1]->getType(),      AnalogPointType );
    BOOST_REQUIRE_EQUAL( points[1]->getQuality(),   NormalQuality );

    /*
        Resulting firmware version should be 'H.6'

        chars                   ==  'H',    '.',    '6'
        hex                     ==  48,     2e,     36
        encoded (hex - 0x20)    ==  28,     0e,     16
        6-bit binary            ==  101000, 001110, 010110
        4-bit regroup (rhs)     ==  10 1000 0011 1001 0110
        as hex #                ==  28396
        as decimal #            ==  164758
    */

    // normally we'd use BOOST_REQUIRE_CLOSE(...) on this but we are looking to exactly match the mantissa
    BOOST_REQUIRE_EQUAL( points[1]->getValue(),     164758.0 );

    delete_container(points);
}


BOOST_AUTO_TEST_CASE(test_test_dev_cbc7020_firmware_points_point_present_low_limit)
{
    Cti::Protocol::Interface::pointlist_t   points;

    // and a firmware point -- value = 0x0100 == 256

    points.push_back( new CtiPointDataMsg( TestCbc7020Device::PointOffset_FirmwareRevision,
                                           0x0100,
                                           NormalQuality, AnalogPointType ) );

    TestCbc7020Device::processFirmwarePoint( points );

    BOOST_REQUIRE_EQUAL( points.size(),             1 );

    /*
        Resulting firmware version should be 'A.1'

        chars                   ==  'A',    '.',    '1'
        hex                     ==  41,     2e,     31
        encoded (hex - 0x20)    ==  21,     0e,     11
        6-bit binary            ==  100001, 001110, 010001
        4-bit regroup (rhs)     ==  10 0001 0011 1001 0001
        as hex #                ==  21391
        as decimal #            ==  136081
    */

    // normally we'd use BOOST_REQUIRE_CLOSE(...) on this but we are looking to exactly match the mantissa
    BOOST_REQUIRE_EQUAL( points[0]->getValue(),     136081.0 );

    delete_container(points);
}


BOOST_AUTO_TEST_CASE(test_test_dev_cbc7020_firmware_points_point_present_high_limit)
{
    Cti::Protocol::Interface::pointlist_t   points;

    // and a firmware point -- value = 0x1AFF == 6911

    points.push_back( new CtiPointDataMsg( TestCbc7020Device::PointOffset_FirmwareRevision,
                                           0x1aff,
                                           NormalQuality, AnalogPointType ) );

    TestCbc7020Device::processFirmwarePoint( points );

    BOOST_REQUIRE_EQUAL( points.size(),             1 );

    /*
        Resulting firmware version should be 'Z.256'

        chars                   ==  'Z',    '.',    '2',    '5',    '6'
        hex                     ==  5a,     2e,     32,     35,     36
        encoded (hex - 0x20)    ==  3a,     0e,     12,     15,     16
        6-bit binary            ==  111010, 001110, 010010, 010101, 010110
        4-bit regroup (rhs)     ==  11 1010 0011 1001 0010 0101 0101 0110
        as hex #                ==  3a392556
        as decimal #            ==  976823638
    */

    // normally we'd use BOOST_REQUIRE_CLOSE(...) on this but we are looking to exactly match the mantissa
    BOOST_REQUIRE_EQUAL( points[0]->getValue(),     976823638.0 );

    delete_container(points);
}


BOOST_AUTO_TEST_CASE(test_test_dev_cbc7020_firmware_points_point_present_major_version_underflow)
{
    Cti::Protocol::Interface::pointlist_t   points;

    // and a firmware point -- value = 0x0012 == 18 --> major revision == 0

    points.push_back( new CtiPointDataMsg( TestCbc7020Device::PointOffset_FirmwareRevision,
                                           0x0012,
                                           NormalQuality, AnalogPointType ) );

    TestCbc7020Device::processFirmwarePoint( points );

    BOOST_REQUIRE_EQUAL( points.size(),             1 );

    /*
        Resulting firmware version should be '0.0'

        chars                   ==  '0',    '.',    '0'
        hex                     ==  30,     2e,     30
        encoded (hex - 0x20)    ==  10,     0e,     10
        6-bit binary            ==  010000, 001110, 010000
        4-bit regroup (rhs)     ==  01 0000 0011 1001 0000
        as hex #                ==  10390
        as decimal #            ==  66448
    */

    // normally we'd use BOOST_REQUIRE_CLOSE(...) on this but we are looking to exactly match the mantissa
    BOOST_REQUIRE_EQUAL( points[0]->getValue(),     66448.0 );

    delete_container(points);
}


BOOST_AUTO_TEST_CASE(test_test_dev_cbc7020_firmware_points_point_present_major_version_overflow)
{
    Cti::Protocol::Interface::pointlist_t   points;

    // and a firmware point -- value = 0x2012 == 8210 --> major revision == 32

    points.push_back( new CtiPointDataMsg( TestCbc7020Device::PointOffset_FirmwareRevision,
                                           0x2012,
                                           NormalQuality, AnalogPointType ) );

    TestCbc7020Device::processFirmwarePoint( points );

    BOOST_REQUIRE_EQUAL( points.size(),             1 );

    /*
        Resulting firmware version should be '0.0'
     
        chars                   ==  '0',    '.',    '0'
        hex                     ==  30,     2e,     30
        encoded (hex - 0x20)    ==  10,     0e,     10
        6-bit binary            ==  010000, 001110, 010000
        4-bit regroup (rhs)     ==  01 0000 0011 1001 0000
        as hex #                ==  10390
        as decimal #            ==  66448
    */

    // normally we'd use BOOST_REQUIRE_CLOSE(...) on this but we are looking to exactly match the mantissa
    BOOST_REQUIRE_EQUAL( points[0]->getValue(),     66448.0 );

    delete_container(points);
}


BOOST_AUTO_TEST_SUITE_END()
