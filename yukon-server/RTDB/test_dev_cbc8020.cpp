#include <boost/test/unit_test.hpp>

#include "dev_cbc8020.h"
#include "ctidate.h"

BOOST_AUTO_TEST_SUITE( test_dev_cbc8020 )

struct TestCbc8020Device : Cti::Devices::Cbc8020Device
{
    using Cbc8020Device::PointOffset_FirmwareRevisionMajor;
    using Cbc8020Device::PointOffset_FirmwareRevisionMinor;
    using Cbc8020Device::PointOffset_FirmwareRevision;
    using Cbc8020Device::combineFirmwarePoints;
};

BOOST_AUTO_TEST_CASE(test_firmware_points_no_points_present)
{
    Cti::Protocol::Interface::pointlist_t points;

    TestCbc8020Device::combineFirmwarePoints(points);

    // No points went in, we better come back with none!
    BOOST_REQUIRE_EQUAL(points.size(), 0);
}

BOOST_AUTO_TEST_CASE(test_firmware_points_both_present)
{
    CtiPointDataMsg *msg1 = new CtiPointDataMsg(), *msg2 = new CtiPointDataMsg();
    Cti::Protocol::Interface::pointlist_t points;

    msg1->setValue(4);
    msg1->setType(AnalogPointType);
    msg1->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    msg2->setValue(1);
    msg2->setType(AnalogPointType);
    msg2->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    points.push_back(msg1);
    points.push_back(msg2);

    /*
        Resulting firmware version should be '0.4.1'
     
        chars                   ==  '0',    '.',    '4',    '.',    '1'
        hex                     ==  30,     2e,     34,     2e,     31
        encoded (hex - 0x20)    ==  10,     0e,     14,     0e,     11
        6-bit binary            ==  010000, 001110, 010100, 001110, 010001
        4-bit regroup (rhs)     ==   01 0000 0011 1001 0100 0011 1001 0001
        as hex #                ==  10394391
        as decimal #            ==  272188305
    */

    TestCbc8020Device::combineFirmwarePoints(points);

    BOOST_REQUIRE_EQUAL(points.size(), 3);

    BOOST_REQUIRE_EQUAL(points[0]->getValue(), 4);
    BOOST_REQUIRE_EQUAL(points[0]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[0]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    BOOST_REQUIRE_EQUAL(points[1]->getValue(), 1);
    BOOST_REQUIRE_EQUAL(points[1]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[1]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    BOOST_REQUIRE_EQUAL(points[2]->getValue(), 272188305);
    BOOST_REQUIRE_EQUAL(points[2]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[2]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevision);

    delete_container(points);
}

BOOST_AUTO_TEST_CASE(test_firmware_points_major_present_only)
{
    CtiPointDataMsg *msg1 = new CtiPointDataMsg();
    Cti::Protocol::Interface::pointlist_t points;

    msg1->setValue(8);
    msg1->setType(AnalogPointType);
    msg1->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    points.push_back(msg1);

    TestCbc8020Device::combineFirmwarePoints(points);

    // It went in without it's partner in crime, it should be in there alone still!
    BOOST_REQUIRE_EQUAL(points.size(), 1);

    BOOST_REQUIRE_EQUAL(points[0]->getValue(), 8);
    BOOST_REQUIRE_EQUAL(points[0]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[0]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    delete_container(points);
}

BOOST_AUTO_TEST_CASE(test_firmware_points_minor_present_only)
{
    CtiPointDataMsg *msg1 = new CtiPointDataMsg();
    Cti::Protocol::Interface::pointlist_t points;

    msg1->setValue(15);
    msg1->setType(AnalogPointType);
    msg1->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    points.push_back(msg1);

    TestCbc8020Device::combineFirmwarePoints(points);

    // It went in without it's partner in crime, it should be in there alone still!
    BOOST_REQUIRE_EQUAL(points.size(), 1);

    BOOST_REQUIRE_EQUAL(points[0]->getValue(), 15);
    BOOST_REQUIRE_EQUAL(points[0]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[0]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    delete_container(points);
}

BOOST_AUTO_TEST_CASE(test_firmware_points_major_minor_extra)
{
    CtiPointDataMsg *msg1 = new CtiPointDataMsg(), *msg2 = new CtiPointDataMsg(), *msg3 = new CtiPointDataMsg();
    Cti::Protocol::Interface::pointlist_t points;

    msg1->setValue(16);
    msg1->setType(AnalogPointType);
    msg1->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    msg2->setValue(23);
    msg2->setType(AnalogPointType);
    msg2->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    msg3->setValue(42);
    msg3->setType(AnalogPointType);
    msg3->setId(19);

    points.push_back(msg1);
    points.push_back(msg2);
    points.push_back(msg3);

    /*
        Resulting firmware version should be '1.0.23'

        chars                   ==  '1',    '.',    '0',    '.',    '2',    '3'
        hex                     ==  31,     2e,     30,     2e,     32,     33
        encoded (hex - 0x20)    ==  11,     0e,     10,     0e,     12,     13
        6-bit binary            ==  010001, 001110, 010000, 001110, 010010, 010011
        4-bit regroup (rhs)     ==  0100 0100 1110 0100 0000 1110 0100 1001 0011
        as hex #                ==  44e40e493
        as decimal #            ==  18492744851
    */

    TestCbc8020Device::combineFirmwarePoints(points);

    BOOST_REQUIRE_EQUAL(points.size(), 4);

    BOOST_REQUIRE_EQUAL(points[0]->getValue(), 16);
    BOOST_REQUIRE_EQUAL(points[0]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[0]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    BOOST_REQUIRE_EQUAL(points[1]->getValue(), 23);
    BOOST_REQUIRE_EQUAL(points[1]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[1]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    BOOST_REQUIRE_EQUAL(points[2]->getValue(), 42);
    BOOST_REQUIRE_EQUAL(points[2]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[2]->getId(),    19);

    BOOST_REQUIRE_EQUAL(points[3]->getValue(), 18492744851);
    BOOST_REQUIRE_EQUAL(points[3]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[3]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevision);

    delete_container(points);
}

/**
 * This unit test is used to define the expected behavior of the
 * CBC 8000's <code>combineFirmwarePoints()</code> function in
 * the unexpected case that we receive multiple major or minor
 * revision points in a message. In this case, we would expect
 * to keep the value of the major and minor revision points
 * whose positions were closest to the end of the vector passed
 * into the function.
 */
BOOST_AUTO_TEST_CASE(test_firmware_points_major_major_minor_minor)
{
    CtiPointDataMsg *msg1 = new CtiPointDataMsg(),
                    *msg2 = new CtiPointDataMsg(),
                    *msg3 = new CtiPointDataMsg(),
                    *msg4 = new CtiPointDataMsg();
    Cti::Protocol::Interface::pointlist_t points;

    msg1->setValue(7);
    msg1->setType(AnalogPointType);
    msg1->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    msg2->setValue(4);
    msg2->setType(AnalogPointType);
    msg2->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    msg3->setValue(28);
    msg3->setType(AnalogPointType);
    msg3->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    msg4->setValue(58);
    msg4->setType(AnalogPointType);
    msg4->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    points.push_back(msg1);
    points.push_back(msg2);
    points.push_back(msg3);
    points.push_back(msg4);

    /*
        Resulting firmware version should be '0.4.28'

        chars                   ==  '0',    '.',    '4',    '.',    '2',    '8'
        hex                     ==  30,     2e,     34,     2e,     32,     28
        encoded (hex - 0x20)    ==  10,     0e,     14,     0e,     12,     18
        6-bit binary            ==  010000, 001110, 010100, 001110, 010010, 011000
     
        4-bit regroup (rhs)     ==  0100 0000 1110 0101 0000 1110 0100 1001 1000
        as hex #                ==  40e50e498
        as decimal #            ==  17420051608
    */

    TestCbc8020Device::combineFirmwarePoints(points);

    // Three go in, two are combined, and two come back!
    BOOST_REQUIRE_EQUAL(points.size(), 5);

    BOOST_REQUIRE_EQUAL(points[0]->getValue(), 7);
    BOOST_REQUIRE_EQUAL(points[0]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[0]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    BOOST_REQUIRE_EQUAL(points[1]->getValue(), 4);
    BOOST_REQUIRE_EQUAL(points[1]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[1]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    BOOST_REQUIRE_EQUAL(points[2]->getValue(), 28);
    BOOST_REQUIRE_EQUAL(points[2]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[2]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    BOOST_REQUIRE_EQUAL(points[3]->getValue(), 58);
    BOOST_REQUIRE_EQUAL(points[3]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[3]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    BOOST_REQUIRE_EQUAL(points[4]->getValue(), 17420051608);
    BOOST_REQUIRE_EQUAL(points[4]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[4]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevision);

    delete_container(points);
}

BOOST_AUTO_TEST_CASE(test_firmware_points_both_present_max_length_output)
{
    CtiPointDataMsg *msg1 = new CtiPointDataMsg(), *msg2 = new CtiPointDataMsg();
    Cti::Protocol::Interface::pointlist_t points;

    msg1->setValue(205);    // 0xCD == 12.13
    msg1->setType(AnalogPointType);
    msg1->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    msg2->setValue(14);
    msg2->setType(AnalogPointType);
    msg2->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    points.push_back(msg1);
    points.push_back(msg2);

    /*
        Resulting firmware version should be '12.13.14'
     
        chars                   ==  '1',    '2',    '.',    '1',    '3',    '.',    '1',    '4'
        hex                     ==  31,     32,     2e,     31,     33,     2e,     31,     34
        encoded (hex - 0x20)    ==  11,     12,     0e,     11,     13,     0e,     11,     14
        6-bit binary            ==  010001, 010010, 001110, 010001, 010011, 001110, 010001, 010100
        4-bit regroup (rhs)     ==  0100 0101 0010 0011 1001 0001 0100 1100 1110 0100 0101 0100
        as hex #                ==  4523914ce454
        as decimal #            ==  76019063907412
    */

    TestCbc8020Device::combineFirmwarePoints(points);

    BOOST_REQUIRE_EQUAL(points.size(), 3);

    BOOST_REQUIRE_EQUAL(points[0]->getValue(), 205);
    BOOST_REQUIRE_EQUAL(points[0]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[0]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    BOOST_REQUIRE_EQUAL(points[1]->getValue(), 14);
    BOOST_REQUIRE_EQUAL(points[1]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[1]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    BOOST_REQUIRE_EQUAL(points[2]->getValue(), 76019063907412);
    BOOST_REQUIRE_EQUAL(points[2]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[2]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevision);

    delete_container(points);
}

BOOST_AUTO_TEST_CASE(test_firmware_points_both_present_max_length_output_truncated)
{
    CtiPointDataMsg *msg1 = new CtiPointDataMsg(), *msg2 = new CtiPointDataMsg();
    Cti::Protocol::Interface::pointlist_t points;

    msg1->setValue(205);    // 0xCD == 12.13
    msg1->setType(AnalogPointType);
    msg1->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    msg2->setValue(147);
    msg2->setType(AnalogPointType);
    msg2->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    points.push_back(msg1);
    points.push_back(msg2);

    /*
        Resulting firmware version should be '12.13.1#'
     
        chars                   ==  '1',    '2',    '.',    '1',    '3',    '.',    '1',    '#'
        hex                     ==  31,     32,     2e,     31,     33,     2e,     31,     23
        encoded (hex - 0x20)    ==  11,     12,     0e,     11,     13,     0e,     11,     03
        6-bit binary            ==  010001, 010010, 001110, 010001, 010011, 001110, 010001, 000011
        4-bit regroup (rhs)     ==  0100 0101 0010 0011 1001 0001 0100 1100 1110 0100 0100 0011
        as hex #                ==  4523914ce443
        as decimal #            ==  76019063907395
    */

    TestCbc8020Device::combineFirmwarePoints(points);

    BOOST_REQUIRE_EQUAL(points.size(), 3);

    BOOST_REQUIRE_EQUAL(points[0]->getValue(), 205);
    BOOST_REQUIRE_EQUAL(points[0]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[0]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    BOOST_REQUIRE_EQUAL(points[1]->getValue(), 147);
    BOOST_REQUIRE_EQUAL(points[1]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[1]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    BOOST_REQUIRE_EQUAL(points[2]->getValue(), 76019063907395);
    BOOST_REQUIRE_EQUAL(points[2]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[2]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevision);

    delete_container(points);
}

BOOST_AUTO_TEST_SUITE_END()
