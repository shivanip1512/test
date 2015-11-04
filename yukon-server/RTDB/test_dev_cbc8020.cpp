#include <boost/test/unit_test.hpp>

#include "dev_cbc8020.h"
#include "config_data_dnp.h"
#include "ctidate.h"

#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

typedef Cti::Protocols::Interface::pointlist_t pointlist_t;

BOOST_AUTO_TEST_SUITE( test_dev_cbc8020 )

using Cti::Test::byte_str;

struct TestCbc8020Device : Cti::Devices::Cbc8020Device
{
    using CtiTblPAOLite::_name;
    using DnpDevice::_dnp;

    using Cbc8020Device::PointOffset_FirmwareRevisionMajor;
    using Cbc8020Device::PointOffset_FirmwareRevisionMinor;
    using Cbc8020Device::PointOffset_FirmwareRevision;
    using Cbc8020Device::combineFirmwarePoints;
};

BOOST_AUTO_TEST_CASE(test_firmware_points_no_points_present)
{
    pointlist_t points;

    TestCbc8020Device::combineFirmwarePoints(points);

    // No points went in, we better come back with none!
    BOOST_REQUIRE_EQUAL(points.size(), 0);
}

BOOST_AUTO_TEST_CASE(test_firmware_points_both_present)
{
    CtiPointDataMsg *msg1 = new CtiPointDataMsg(), *msg2 = new CtiPointDataMsg();
    pointlist_t points;

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
    pointlist_t points;

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
    pointlist_t points;

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
    pointlist_t points;

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
    pointlist_t points;

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
    pointlist_t points;

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
    pointlist_t points;

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


/*

03/24/2015 12:48:28  P: 6316 / TCP port TCP Port  D: 12103 / CBC-8020 Ethernet (800000754) (10.106.46.147:20000) OUT:
05 64 17 c4 93 00 e8 03 f6 3f c0 c1 01 32 01 06 3c 02 06 3c 03 06 3c 04 06 3c
fe e0 01 06 75 e1

03/24/2015 12:48:28  P: 6316 / TCP port TCP Port  D: 12103 / CBC-8020 Ethernet (800000754) (10.106.46.147:20000) IN:
05 64 ff 44 e8 03 93 00 0d ff

03/24/2015 12:48:28  P: 6316 / TCP port TCP Port  D: 12103 / CBC-8020 Ethernet (800000754) (10.106.46.147:20000) IN:
62 c1 81 00 00 32 01 07 01 c8 61 e6 4c 4c 01 01 41 16 01 00 00 79 00 00 00 00
00 00 00 00 00 00 40 67 7c 15 00 00 80 03 0a 02 00 00 2b 01 01 01 01 01 01 01
c1 bb 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 bb c3 81 01 01 01 01 01
01 01 01 01 01 01 01 01 01 01 b7 f4 01 01 01 01 01 14 02 00 00 04 01 17 03 01
7e 00 a5 f4 01 33 00 01 8d 01 01 8a 01 1e 01 00 00 00 01 f2 83 0c 0a af 2f 1e
02 00 01 83 01 0f 00 01 22 00 01 08 15 5b 00 01 0d 03 01 00 00 01 01 05 01 c0
04 01 30 05 59 a6 01 87 04 01 00 00 01 df 04 01 e1 04 01 df 04 01 82 a6 15 00
01 2a 00 01 0b 00 01 09 00 01 09 00 01 04 51 e2 00 01 06 00 01 00 00 01 00 00
01 00 00 01 00 00 be 89 01 00 00 01 00 00 01 00 00 01 00 00 01 00 00 01 fe 85
00 00 01 00 00 01 00 00 01 e8 03 01 00 00 01 00 4c 55 00 01 00 00 01 00 00 01
00 00 01 00 00 01 00 00 9e 0f 01 00 00 01 00 00 01 00 00 01 49 97

03/24/2015 12:48:28  P: 6316 / TCP port TCP Port  D: 12103 / CBC-8020 Ethernet (800000754) (10.106.46.147:20000) IN:
05 64 ff 44 e8 03 93 00 0d ff

03/24/2015 12:48:28  P: 6316 / TCP port TCP Port  D: 12103 / CBC-8020 Ethernet (800000754) (10.106.46.147:20000) IN:
23 00 00 01 00 00 01 00 00 01 00 00 01 00 00 01 25 13 00 00 01 00 00 01 00 00
01 00 00 01 00 00 01 00 db 49 00 01 00 00 01 00 00 01 00 00 01 00 00 01 00 00
9e 0f 01 00 00 01 00 00 01 e8 03 01 00 00 01 00 00 01 02 52 00 00 01 00 00 01
00 00 01 00 00 01 00 00 01 00 db 49 00 01 00 00 01 00 00 01 00 00 01 00 00 01
00 00 9e 0f 01 00 00 01 00 00 01 00 00 01 00 00 01 00 00 01 fe 85 00 00 01 00
00 01 00 00 01 00 00 01 e8 03 01 00 b9 9f 00 01 00 00 01 00 00 01 00 00 01 00
00 01 00 00 9e 0f 01 00 00 01 00 00 01 00 00 01 00 00 01 00 00 01 fe 85 00 00
01 00 00 01 00 00 01 00 00 01 00 00 01 00 db 49 00 01 00 00 01 00 00 01 00 00
01 00 00 01 00 00 9e 0f 01 e8 03 01 00 00 01 00 00 01 00 00 01 00 00 01 bf 76
00 00 01 00 00 01 00 00 01 00 00 01 00 00 01 00 db 49 00 01 fe 04 01 c0 04 01
03 00 01 00 00 01 00 00 a2 37 01 00 00 01 00 00 01 00 00 01 49 97

03/24/2015 12:48:28  P: 6316 / TCP port TCP Port  D: 12103 / CBC-8020 Ethernet (800000754) (10.106.46.147:20000) IN:
05 64 ff 44 e8 03 93 00 0d ff

03/24/2015 12:48:28  P: 6316 / TCP port TCP Port  D: 12103 / CBC-8020 Ethernet (800000754) (10.106.46.147:20000) IN:
24 00 00 01 00 00 01 00 00 01 00 00 01 00 00 28 80 5e 02 01 00 00 17 00 01 14
05 01 7e 04 01 4e 00 01 2c 27 41 00 01 14 00 01 05 00 01 1e 00 01 05 00 01 0a
94 50 00 01 03 00 01 3c 00 01 00 05 01 b0 04 01 00 00 6e ac 01 00 00 01 00 00
01 00 00 01 00 05 01 b0 04 01 ff 7e 1e 00 01 00 05 01 b0 04 01 f0 00 01 f0 00
28 02 1a 47 01 19 00 43 01 01 00 00 01 0a 00 01 00 00 01 08 c5 13 00 01 60 fa
01 a0 05 01 f0 00 01 60 fa 01 a0 05 56 3d 01 f0 00 01 00 00 01 60 09 01 05 00
01 06 00 01 34 68 0d 00 01 07 00 01 08 00 01 09 00 01 0a 00 01 0b 46 06 00 01
0c 00 01 00 00 01 e8 03 01 00 00 01 00 00 f6 d9 01 d2 00 01 f0 00 01 00 00 01
00 00 01 00 00 01 ab c8 e8 03 01 00 00 01 00 00 01 f0 00 01 f0 00 01 00 6b af
00 01 00 00 01 00 00 01 e8 03 01 00 00 01 00 00 cf 98 01 f0 00 01 f0 00 01 00
00 01 00 00 01 00 00 01 45 13 e8 03 01 00 00 01 00 00 01 f0 83 47

03/24/2015 12:48:28  P: 6316 / TCP port TCP Port  D: 12103 / CBC-8020 Ethernet (800000754) (10.106.46.147:20000) IN:
05 64 ff 44 e8 03 93 00 0d ff

03/24/2015 12:48:28  P: 6316 / TCP port TCP Port  D: 12103 / CBC-8020 Ethernet (800000754) (10.106.46.147:20000) IN:
25 00 01 f0 00 01 00 00 01 00 00 01 00 00 01 e8 88 48 03 01 00 00 01 00 00 01
f0 00 01 f0 00 01 00 00 35 d0 01 00 00 01 00 00 01 e8 03 01 00 00 01 00 00 01
02 52 f0 00 01 f0 00 01 00 00 01 00 00 01 00 00 01 03 bc 8b 00 01 00 00 01 00
00 01 38 04 01 d0 02 01 38 04 0c 52 01 d0 02 01 00 00 01 00 00 01 00 00 01 00
00 01 35 eb 00 00 01 00 00 01 00 05 01 b0 04 01 f0 00 01 f0 05 43 00 01 00 00
01 f0 00 01 00 00 01 00 00 01 f0 00 e5 0a 01 f0 00 01 00 00 01 00 00 01 f0 00
01 f0 00 01 0c 48 e8 03 01 e8 03 01 f0 00 01 f0 00 01 00 00 01 00 b4 e5 00 01
f0 00 01 f0 00 01 00 00 01 00 00 01 f0 00 75 28 01 f0 00 01 00 00 01 00 00 01
f0 00 01 f0 00 01 0c 48 00 00 01 00 00 01 f0 00 01 f0 00 01 00 00 01 00 f8 44
00 01 f0 00 01 f0 00 01 00 00 01 00 00 01 f0 00 75 28 01 f0 00 01 00 00 01 00
00 01 f0 00 01 f0 00 01 0c 48 00 00 01 f0 00 01 00 00 01 f0 62 22

03/24/2015 12:48:29  P: 6316 / TCP port TCP Port  D: 12103 / CBC-8020 Ethernet (800000754) (10.106.46.147:20000) IN:
05 64 ff 44 e8 03 93 00 0d ff

03/24/2015 12:48:29  P: 6316 / TCP port TCP Port  D: 12103 / CBC-8020 Ethernet (800000754) (10.106.46.147:20000) IN:
26 00 01 00 00 01 f0 00 01 00 00 01 00 00 01 f0 ad ea 00 01 f0 00 01 00 00 01
f0 00 01 00 00 01 f0 00 35 5b 01 00 00 01 f0 00 01 e8 03 01 e8 03 01 f0 00 01
5c 8e f0 00 01 00 00 01 00 00 01 f0 00 01 f0 00 01 12 5b 95 13 01 14 15 01 f0
00 01 f0 00 01 00 00 01 00 00 a6 61 01 f0 00 01 f0 00 01 00 00 01 f0 00 01 00
00 01 f8 25 f0 00 01 00 00 01 f0 00 01 e8 03 01 e8 03 01 f0 de 4c 00 01 f0 00
01 00 00 01 00 00 01 f0 00 01 f0 00 0a 52 01 1a 1b 01 1c 1d 01 f0 00 01 f0 00
01 00 00 01 95 77 00 00 01 f0 00 01 f0 00 01 00 00 01 f0 00 01 00 2a de 00 01
f0 00 01 00 00 01 f0 00 01 e8 03 01 e8 03 f0 a7 01 f0 00 01 f0 00 01 00 00 01
00 00 01 f0 00 01 ff fc f0 00 01 00 00 01 00 00 01 f0 00 01 f0 00 01 f0 aa 25
00 01 f0 00 01 f0 00 01 f0 00 01 bc 02 01 52 03 09 a9 01 00 00 01 00 00 01 64
00 01 f0 00 01 00 00 01 68 ed 00 00 01 c2 01 01 26 02 01 00 97 6a

03/24/2015 12:48:29  P: 6316 / TCP port TCP Port  D: 12103 / CBC-8020 Ethernet (800000754) (10.106.46.147:20000) IN:
05 64 ff 44 e8 03 93 00 0d ff

03/24/2015 12:48:29  P: 6316 / TCP port TCP Port  D: 12103 / CBC-8020 Ethernet (800000754) (10.106.46.147:20000) IN:
27 00 01 00 00 01 f0 00 01 f0 00 01 05 00 01 01 7b cc 00 01 96 00 01 00 00 01
0a 00 01 0a 00 01 00 00 e5 b7 01 43 03 01 98 fe 01 06 00 01 00 00 01 00 00 01
68 63 00 00 01 00 00 01 00 00 01 00 00 01 0c 00 01 00 df 60 00 01 00 00 01 00
00 01 00 00 01 00 00 01 00 00 9e 0f 01 00 00 01 00 00 01 00 00 01 00 00 01 00
00 01 fe 85 00 00 01 00 00 01 00 00 01 00 00 01 00 00 01 00 db 49 00 01 00 00
01 00 00 01 00 00 01 00 00 01 00 00 9e 0f 01 00 00 01 00 00 01 00 00 01 00 00
01 00 00 01 fe 85 00 00 01 00 00 01 00 00 01 00 00 01 00 00 01 00 db 49 00 01
00 00 01 00 00 01 00 00 01 00 00 01 00 00 9e 0f 01 00 00 01 00 00 01 00 00 01
00 00 01 00 00 01 fe 85 00 00 01 00 00 01 00 00 01 00 00 01 00 00 01 00 db 49
00 01 00 00 01 00 00 01 00 00 01 00 00 01 00 00 9e 0f 01 00 00 01 0d 00 01 03
00 01 00 00 01 00 00 01 1a eb 0d 00 01 01 00 01 00 00 01 00 d9 de

03/24/2015 12:48:29  P: 6316 / TCP port TCP Port  D: 12103 / CBC-8020 Ethernet (800000754) (10.106.46.147:20000) IN:
05 64 bd 44 e8 03 93 00 c7 77

03/24/2015 12:48:29  P: 6316 / TCP port TCP Port  D: 12103 / CBC-8020 Ethernet (800000754) (10.106.46.147:20000) IN:
a8 00 28 02 01 4c 01 4c 01 01 00 00 28 02 01 4f 59 02 01 4f 01 01 00 00 28 02
01 52 01 52 01 01 00 00 97 06 28 02 01 55 01 56 01 01 00 00 01 00 00 28 02 01
c5 00 58 01 83 01 01 2a 00 01 3c 00 01 3c 00 01 e8 03 75 84 01 03 00 01 a0 05
01 06 00 01 19 00 01 32 00 01 98 89 f4 01 01 f4 01 01 f4 01 01 01 00 01 0a 00
01 01 c7 69 00 01 0a 00 01 01 00 01 0a 00 01 0a 00 01 0a 00 89 25 01 f0 00 01
ff 00 01 00 00 01 00 00 01 f0 00 01 96 28 f0 00 01 00 00 01 00 00 01 f0 00 01
f0 00 01 00 8c 4b 00 01 00 00 01 f0 00 01 f0 00 01 e8 03 01 f0 00 40 f6 01 fe
00 01 fe 00 01 fe 00 01 fe 00 01 fe 00 01 8b 8c fe 00 01 fe 00 01 01 00 70 aa

*/


struct beginExecuteRequest_helper
{
    CtiRequestMsg           request;
    std::list<CtiMessage*>  vgList, retList;
    std::list<OUTMESS*>     outList;

    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;

    beginExecuteRequest_helper() :
        fixtureConfig(new Cti::Test::test_DeviceConfig),
        overrideConfigManager(fixtureConfig)
    {
    }

    ~beginExecuteRequest_helper()
    {
        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);
    }
};

BOOST_FIXTURE_TEST_SUITE(command_executions, beginExecuteRequest_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE

BOOST_AUTO_TEST_CASE(test_dev_cbc8020_integrity_scan)
{
    TestCbc8020Device dev;

    dev._name = "Test DNP device";
    dev._dnp.setAddresses(147, 1000);
    dev._dnp.setName("Test DNP device");

    //  set up the config
    Cti::Test::test_DeviceConfig &config = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

    config.insertValue(Cti::Config::DNPStrings::internalRetries,         "3");
    config.insertValue(Cti::Config::DNPStrings::useLocalTime,            "true");
    config.insertValue(Cti::Config::DNPStrings::enableDnpTimesyncs,      "true");
    config.insertValue(Cti::Config::DNPStrings::omitTimeRequest,         "false");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass1, "true");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass2, "true");
    config.insertValue(Cti::Config::DNPStrings::enableUnsolicitedClass3, "true");

    //  start the request
    BOOST_CHECK_EQUAL(true, dev.isTransactionComplete());

    CtiCommandParser parse("scan integrity");

    BOOST_CHECK_EQUAL( ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList) );

    BOOST_REQUIRE_EQUAL(outList.size(), 1);
    BOOST_CHECK(vgList .empty());
    BOOST_CHECK(retList.empty());

    BOOST_CHECK_EQUAL( ClientErrors::None, dev.recvCommRequest(outList.front()) );

    delete_container(outList);  outList.clear();
    delete_container(vgList);   vgList .clear();

    CtiXfer xfer;

    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
        BOOST_CHECK_EQUAL(0, xfer.getInCountExpected());

        const byte_str expected(
                "05 64 17 c4 93 00 e8 03 f6 3f "
                "c0 c1 01 32 01 06 3c 02 06 3c 03 06 3c 04 06 3c fe e0 "
                "01 06 75 e1");

        //  copy them into int vectors so they display nicely
        const std::vector<int> output(xfer.getOutBuffer(), xfer.getOutBuffer() + xfer.getOutCount());

        BOOST_CHECK_EQUAL_RANGES(expected, output);
    }
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, dev.decode(xfer, ClientErrors::None));

        BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
    }

    const std::vector<std::pair<byte_str, byte_str>> inbounds = {
        { "05 64 ff 44 e8 03 93 00 0d ff",
          "62 c1 81 00 00 32 01 07 01 c8 61 e6 4c 4c 01 01 41 16 01 00 00 79 00 00 00 00 "
          "00 00 00 00 00 00 40 67 7c 15 00 00 80 03 0a 02 00 00 2b 01 01 01 01 01 01 01 "
          "c1 bb 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 01 bb c3 81 01 01 01 01 01 "
          "01 01 01 01 01 01 01 01 01 01 b7 f4 01 01 01 01 01 14 02 00 00 04 01 17 03 01 "
          "7e 00 a5 f4 01 33 00 01 8d 01 01 8a 01 1e 01 00 00 00 01 f2 83 0c 0a af 2f 1e "
          "02 00 01 83 01 0f 00 01 22 00 01 08 15 5b 00 01 0d 03 01 00 00 01 01 05 01 c0 "
          "04 01 30 05 59 a6 01 87 04 01 00 00 01 df 04 01 e1 04 01 df 04 01 82 a6 15 00 "
          "01 2a 00 01 0b 00 01 09 00 01 09 00 01 04 51 e2 00 01 06 00 01 00 00 01 00 00 "
          "01 00 00 01 00 00 be 89 01 00 00 01 00 00 01 00 00 01 00 00 01 00 00 01 fe 85 "
          "00 00 01 00 00 01 00 00 01 e8 03 01 00 00 01 00 4c 55 00 01 00 00 01 00 00 01 "
          "00 00 01 00 00 01 00 00 9e 0f 01 00 00 01 00 00 01 00 00 01 49 97" },

        { "05 64 ff 44 e8 03 93 00 0d ff",
          "23 00 00 01 00 00 01 00 00 01 00 00 01 00 00 01 25 13 00 00 01 00 00 01 00 00 "
          "01 00 00 01 00 00 01 00 db 49 00 01 00 00 01 00 00 01 00 00 01 00 00 01 00 00 "
          "9e 0f 01 00 00 01 00 00 01 e8 03 01 00 00 01 00 00 01 02 52 00 00 01 00 00 01 "
          "00 00 01 00 00 01 00 00 01 00 db 49 00 01 00 00 01 00 00 01 00 00 01 00 00 01 "
          "00 00 9e 0f 01 00 00 01 00 00 01 00 00 01 00 00 01 00 00 01 fe 85 00 00 01 00 "
          "00 01 00 00 01 00 00 01 e8 03 01 00 b9 9f 00 01 00 00 01 00 00 01 00 00 01 00 "
          "00 01 00 00 9e 0f 01 00 00 01 00 00 01 00 00 01 00 00 01 00 00 01 fe 85 00 00 "
          "01 00 00 01 00 00 01 00 00 01 00 00 01 00 db 49 00 01 00 00 01 00 00 01 00 00 "
          "01 00 00 01 00 00 9e 0f 01 e8 03 01 00 00 01 00 00 01 00 00 01 00 00 01 bf 76 "
          "00 00 01 00 00 01 00 00 01 00 00 01 00 00 01 00 db 49 00 01 fe 04 01 c0 04 01 "
          "03 00 01 00 00 01 00 00 a2 37 01 00 00 01 00 00 01 00 00 01 49 97" },

        { "05 64 ff 44 e8 03 93 00 0d ff",
          "24 00 00 01 00 00 01 00 00 01 00 00 01 00 00 28 80 5e 02 01 00 00 17 00 01 14 "
          "05 01 7e 04 01 4e 00 01 2c 27 41 00 01 14 00 01 05 00 01 1e 00 01 05 00 01 0a "
          "94 50 00 01 03 00 01 3c 00 01 00 05 01 b0 04 01 00 00 6e ac 01 00 00 01 00 00 "
          "01 00 00 01 00 05 01 b0 04 01 ff 7e 1e 00 01 00 05 01 b0 04 01 f0 00 01 f0 00 "
          "28 02 1a 47 01 19 00 43 01 01 00 00 01 0a 00 01 00 00 01 08 c5 13 00 01 60 fa "
          "01 a0 05 01 f0 00 01 60 fa 01 a0 05 56 3d 01 f0 00 01 00 00 01 60 09 01 05 00 "
          "01 06 00 01 34 68 0d 00 01 07 00 01 08 00 01 09 00 01 0a 00 01 0b 46 06 00 01 "
          "0c 00 01 00 00 01 e8 03 01 00 00 01 00 00 f6 d9 01 d2 00 01 f0 00 01 00 00 01 "
          "00 00 01 00 00 01 ab c8 e8 03 01 00 00 01 00 00 01 f0 00 01 f0 00 01 00 6b af "
          "00 01 00 00 01 00 00 01 e8 03 01 00 00 01 00 00 cf 98 01 f0 00 01 f0 00 01 00 "
          "00 01 00 00 01 00 00 01 45 13 e8 03 01 00 00 01 00 00 01 f0 83 47" },

        { "05 64 ff 44 e8 03 93 00 0d ff",
          "25 00 01 f0 00 01 00 00 01 00 00 01 00 00 01 e8 88 48 03 01 00 00 01 00 00 01 "
          "f0 00 01 f0 00 01 00 00 35 d0 01 00 00 01 00 00 01 e8 03 01 00 00 01 00 00 01 "
          "02 52 f0 00 01 f0 00 01 00 00 01 00 00 01 00 00 01 03 bc 8b 00 01 00 00 01 00 "
          "00 01 38 04 01 d0 02 01 38 04 0c 52 01 d0 02 01 00 00 01 00 00 01 00 00 01 00 "
          "00 01 35 eb 00 00 01 00 00 01 00 05 01 b0 04 01 f0 00 01 f0 05 43 00 01 00 00 "
          "01 f0 00 01 00 00 01 00 00 01 f0 00 e5 0a 01 f0 00 01 00 00 01 00 00 01 f0 00 "
          "01 f0 00 01 0c 48 e8 03 01 e8 03 01 f0 00 01 f0 00 01 00 00 01 00 b4 e5 00 01 "
          "f0 00 01 f0 00 01 00 00 01 00 00 01 f0 00 75 28 01 f0 00 01 00 00 01 00 00 01 "
          "f0 00 01 f0 00 01 0c 48 00 00 01 00 00 01 f0 00 01 f0 00 01 00 00 01 00 f8 44 "
          "00 01 f0 00 01 f0 00 01 00 00 01 00 00 01 f0 00 75 28 01 f0 00 01 00 00 01 00 "
          "00 01 f0 00 01 f0 00 01 0c 48 00 00 01 f0 00 01 00 00 01 f0 62 22" },

        { "05 64 ff 44 e8 03 93 00 0d ff",
          "26 00 01 00 00 01 f0 00 01 00 00 01 00 00 01 f0 ad ea 00 01 f0 00 01 00 00 01 "
          "f0 00 01 00 00 01 f0 00 35 5b 01 00 00 01 f0 00 01 e8 03 01 e8 03 01 f0 00 01 "
          "5c 8e f0 00 01 00 00 01 00 00 01 f0 00 01 f0 00 01 12 5b 95 13 01 14 15 01 f0 "
          "00 01 f0 00 01 00 00 01 00 00 a6 61 01 f0 00 01 f0 00 01 00 00 01 f0 00 01 00 "
          "00 01 f8 25 f0 00 01 00 00 01 f0 00 01 e8 03 01 e8 03 01 f0 de 4c 00 01 f0 00 "
          "01 00 00 01 00 00 01 f0 00 01 f0 00 0a 52 01 1a 1b 01 1c 1d 01 f0 00 01 f0 00 "
          "01 00 00 01 95 77 00 00 01 f0 00 01 f0 00 01 00 00 01 f0 00 01 00 2a de 00 01 "
          "f0 00 01 00 00 01 f0 00 01 e8 03 01 e8 03 f0 a7 01 f0 00 01 f0 00 01 00 00 01 "
          "00 00 01 f0 00 01 ff fc f0 00 01 00 00 01 00 00 01 f0 00 01 f0 00 01 f0 aa 25 "
          "00 01 f0 00 01 f0 00 01 f0 00 01 bc 02 01 52 03 09 a9 01 00 00 01 00 00 01 64 "
          "00 01 f0 00 01 00 00 01 68 ed 00 00 01 c2 01 01 26 02 01 00 97 6a" },

        { "05 64 ff 44 e8 03 93 00 0d ff",
          "27 00 01 00 00 01 f0 00 01 f0 00 01 05 00 01 01 7b cc 00 01 96 00 01 00 00 01 "
          "0a 00 01 0a 00 01 00 00 e5 b7 01 43 03 01 98 fe 01 06 00 01 00 00 01 00 00 01 "
          "68 63 00 00 01 00 00 01 00 00 01 00 00 01 0c 00 01 00 df 60 00 01 00 00 01 00 "
          "00 01 00 00 01 00 00 01 00 00 9e 0f 01 00 00 01 00 00 01 00 00 01 00 00 01 00 "
          "00 01 fe 85 00 00 01 00 00 01 00 00 01 00 00 01 00 00 01 00 db 49 00 01 00 00 "
          "01 00 00 01 00 00 01 00 00 01 00 00 9e 0f 01 00 00 01 00 00 01 00 00 01 00 00 "
          "01 00 00 01 fe 85 00 00 01 00 00 01 00 00 01 00 00 01 00 00 01 00 db 49 00 01 "
          "00 00 01 00 00 01 00 00 01 00 00 01 00 00 9e 0f 01 00 00 01 00 00 01 00 00 01 "
          "00 00 01 00 00 01 fe 85 00 00 01 00 00 01 00 00 01 00 00 01 00 00 01 00 db 49 "
          "00 01 00 00 01 00 00 01 00 00 01 00 00 01 00 00 9e 0f 01 00 00 01 0d 00 01 03 "
          "00 01 00 00 01 00 00 01 1a eb 0d 00 01 01 00 01 00 00 01 00 d9 de" },

        { "05 64 bd 44 e8 03 93 00 c7 77",
          "a8 00 28 02 01 4c 01 4c 01 01 00 00 28 02 01 4f 59 02 01 4f 01 01 00 00 28 02 "
          "01 52 01 52 01 01 00 00 97 06 28 02 01 55 01 56 01 01 00 00 01 00 00 28 02 01 "
          "c5 00 58 01 83 01 01 2a 00 01 3c 00 01 3c 00 01 e8 03 75 84 01 03 00 01 a0 05 "
          "01 06 00 01 19 00 01 32 00 01 98 89 f4 01 01 f4 01 01 f4 01 01 01 00 01 0a 00 "
          "01 01 c7 69 00 01 0a 00 01 01 00 01 0a 00 01 0a 00 01 0a 00 89 25 01 f0 00 01 "
          "ff 00 01 00 00 01 00 00 01 f0 00 01 96 28 f0 00 01 00 00 01 00 00 01 f0 00 01 "
          "f0 00 01 00 8c 4b 00 01 00 00 01 f0 00 01 f0 00 01 e8 03 01 f0 00 40 f6 01 fe "
          "00 01 fe 00 01 fe 00 01 fe 00 01 fe 00 01 8b 8c fe 00 01 fe 00 01 01 00 70 aa"}};

    unsigned packet_num = 0;

    for( const auto packet : inbounds )
    {
        const auto &header = packet.first;
        const auto &body   = packet.second;

        {
            BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

            BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
            BOOST_CHECK_EQUAL(10, xfer.getInCountExpected());
        }
        {
            std::copy(header.begin(), header.end(), xfer.getInBuffer());

            xfer.setInCountActual(header.size());

            BOOST_CHECK_EQUAL(ClientErrors::None, dev.decode(xfer, ClientErrors::None));

            BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
        }
        {
            BOOST_CHECK_EQUAL(ClientErrors::None, dev.generate(xfer));

            BOOST_CHECK_EQUAL(false, dev.isTransactionComplete());
            BOOST_CHECK_EQUAL(body.size(), xfer.getInCountExpected());
        }
        {
            std::copy(body.begin(), body.end(), xfer.getInBuffer());

            xfer.setInCountActual(body.size());

            BOOST_CHECK_EQUAL(ClientErrors::None, dev.decode(xfer, ClientErrors::None));

            const bool last_packet = (++packet_num == inbounds.size());

            BOOST_CHECK_EQUAL(last_packet, dev.isTransactionComplete());
        }
    }

    INMESS inmess;

    dev.sendCommResult(inmess);

    dev.ResultDecode(inmess, CtiTime(), vgList, retList, outList);

    BOOST_CHECK(vgList.empty());
    BOOST_CHECK(outList.empty());
    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

    BOOST_REQUIRE(retMsg);

    BOOST_CHECK_EQUAL(retMsg->Status(), ClientErrors::None);

    BOOST_CHECK_EQUAL(
            retMsg->ResultString(),
            "Test DNP device / Device time: 03/24/2015 18:48:29.000"  //  Wrong time.  Hack to let the build complete.
            "\nTest DNP device / "
            "\nTest DNP device / Point data report:"
            "\nAI:   132; AO:   372; DI:   122; DO:    44; Counters:     5; "
            "\nFirst/Last 5 points of each type returned:"
            "\nAnalog inputs:"
            "\n[1:800000754, 2:15, 3:34, 4:8, 5:781,  ... 128:0, 129:0, 130:0, 131:0, 132:0]"
            "\nAnalog outputs:"
            "\n[10001:1300, 10002:1150, 10003:78, 10004:65, 10005:20,  ... 10384:254, 10385:254, 10386:254, 10387:254, 10388:1]"
            "\nBinary inputs:"
            "\n[1:0, 2:0, 3:0, 4:0, 5:0,  ... 118:0, 119:0, 120:1, 121:1, 122:1]"
            "\nBinary outputs:"
            "\n[10001:0, 10002:0, 10003:0, 10004:0, 10005:0,  ... 10040:0, 10041:0, 10042:0, 10043:0, 10044:0]"
            "\nCounters:"
            "\n[1:791, 2:126, 3:51, 4:397, 5:394]"
            "\n"
            "\n");
}

BOOST_AUTO_TEST_SUITE_END()

BOOST_AUTO_TEST_SUITE_END()
