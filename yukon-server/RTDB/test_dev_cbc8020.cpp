#define BOOST_AUTO_TEST_MAIN "Test Cbc8020Device"

#include "yukon.h"
#include "dev_cbc8020.h"
#include "ctitime.h"
#include "ctidate.h"
#include "utility.h"

#include <boost/test/unit_test.hpp>

using boost::unit_test_framework::test_suite;
using Cti::Devices::Cbc8020Device;

struct TestCbc8020Device : Cbc8020Device
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

    TestCbc8020Device::combineFirmwarePoints(points);
    
    BOOST_REQUIRE_EQUAL(points.size(), 3);

    BOOST_REQUIRE_EQUAL(points[0]->getValue(), 4);
    BOOST_REQUIRE_EQUAL(points[0]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[0]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMajor);
                               
    BOOST_REQUIRE_EQUAL(points[1]->getValue(), 1);
    BOOST_REQUIRE_EQUAL(points[1]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[1]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    BOOST_REQUIRE_EQUAL(points[2]->getValue(), 4.01);
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

    BOOST_REQUIRE_EQUAL(points[3]->getValue(), 16.23);
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

    BOOST_REQUIRE_EQUAL(points[4]->getValue(), 4.28);
    BOOST_REQUIRE_EQUAL(points[4]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[4]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevision);

    delete_container(points);
}
