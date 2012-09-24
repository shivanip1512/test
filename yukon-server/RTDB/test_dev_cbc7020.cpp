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

    // and a firmware point -- value = 0x030D == 781

    points.push_back( new CtiPointDataMsg( TestCbc7020Device::PointOffset_FirmwareRevision,
                                           0x030D,
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
        Resulting firmware version should be 'M.3'

        chars                   ==  'M',    '.',    '3'
        hex                     ==  4d,     2e,     33
        encoded (hex - 0x20)    ==  2d,     0e,     13
        6-bit binary            ==  101101, 001110, 010011
        4-bit regroup (rhs)     ==  10 1101 0011 1001 0011
        as hex #                ==  2d393
        as decimal #            ==  185235
    */

    // normally we'd use BOOST_REQUIRE_CLOSE(...) on this but we are looking to exactly match the mantissa
    BOOST_REQUIRE_EQUAL( points[1]->getValue(),     185235.0 );

    delete_container(points);
}


BOOST_AUTO_TEST_CASE(test_test_dev_cbc7020_firmware_points_point_present_low_limit)
{
    Cti::Protocol::Interface::pointlist_t   points;

    // and a firmware point -- value = 0x0001 == 1

    points.push_back( new CtiPointDataMsg( TestCbc7020Device::PointOffset_FirmwareRevision,
                                           0x0001,
                                           NormalQuality, AnalogPointType ) );

    TestCbc7020Device::processFirmwarePoint( points );

    BOOST_REQUIRE_EQUAL( points.size(),             1 );

    /*
        Resulting firmware version should be 'A.0'

        chars                   ==  'A',    '.',    '0'
        hex                     ==  41,     2e,     30
        encoded (hex - 0x20)    ==  21,     0e,     10
        6-bit binary            ==  100001, 001110, 010000
        4-bit regroup (rhs)     ==  10 0001 0011 1001 0000
        as hex #                ==  21390
        as decimal #            ==  136080
    */

    // normally we'd use BOOST_REQUIRE_CLOSE(...) on this but we are looking to exactly match the mantissa
    BOOST_REQUIRE_EQUAL( points[0]->getValue(),     136080.0 );

    delete_container(points);
}


BOOST_AUTO_TEST_CASE(test_test_dev_cbc7020_firmware_points_point_present_high_limit)
{
    Cti::Protocol::Interface::pointlist_t   points;

    // and a firmware point -- value = 0xFF1A == 65306

    points.push_back( new CtiPointDataMsg( TestCbc7020Device::PointOffset_FirmwareRevision,
                                           0xff1a,
                                           NormalQuality, AnalogPointType ) );

    TestCbc7020Device::processFirmwarePoint( points );

    BOOST_REQUIRE_EQUAL( points.size(),             1 );

    /*
        Resulting firmware version should be 'Z.255'

        chars                   ==  'Z',    '.',    '2',    '5',    '5'
        hex                     ==  5a,     2e,     32,     35,     35
        encoded (hex - 0x20)    ==  3a,     0e,     12,     15,     15
        6-bit binary            ==  111010, 001110, 010010, 010101, 010101
        4-bit regroup (rhs)     ==  11 1010 0011 1001 0010 0101 0101 0101
        as hex #                ==  3a392555
        as decimal #            ==  976823637
    */

    // normally we'd use BOOST_REQUIRE_CLOSE(...) on this but we are looking to exactly match the mantissa
    BOOST_REQUIRE_EQUAL( points[0]->getValue(),     976823637.0 );

    delete_container(points);
}


BOOST_AUTO_TEST_CASE(test_test_dev_cbc7020_firmware_points_point_present_major_version_underflow)
{
    Cti::Protocol::Interface::pointlist_t   points;

    // and a firmware point -- value = 0x1200 == 4608 --> major revision == 0

    points.push_back( new CtiPointDataMsg( TestCbc7020Device::PointOffset_FirmwareRevision,
                                           0x1200,
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

    // and a firmware point -- value = 0x1220 == 4640 --> major revision == 32

    points.push_back( new CtiPointDataMsg( TestCbc7020Device::PointOffset_FirmwareRevision,
                                           0x1220,
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
