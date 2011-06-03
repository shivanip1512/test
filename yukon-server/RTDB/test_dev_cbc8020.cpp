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
    using Cbc8020Device::combineFirmwarePoints;
};

static const CtiTime before(CtiDate(1, 6, 2011), 12, 34, 56);
static const CtiTime  after(CtiDate(2, 6, 2011), 12, 34, 56);

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
    
    BOOST_REQUIRE_EQUAL(points.size(), 1);

    BOOST_REQUIRE_EQUAL(points[0]->getValue(), 4.01);
    BOOST_REQUIRE_EQUAL(points[0]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[0]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

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
    
    // Three go in, two are combined, and two come back!
    BOOST_REQUIRE_EQUAL(points.size(), 2);

    BOOST_REQUIRE_EQUAL(points[0]->getValue(), 42);
    BOOST_REQUIRE_EQUAL(points[0]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[0]->getId(),    19);

    BOOST_REQUIRE_EQUAL(points[1]->getValue(), 16.23);
    BOOST_REQUIRE_EQUAL(points[1]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[1]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    delete_container(points);
}

BOOST_AUTO_TEST_CASE(test_firmware_points_major_extra_minor)
{
    CtiPointDataMsg *msg1 = new CtiPointDataMsg(), *msg2 = new CtiPointDataMsg(), *msg3 = new CtiPointDataMsg();
    Cti::Protocol::Interface::pointlist_t points;
    
    msg1->setValue(3);
    msg1->setType(AnalogPointType);
    msg1->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMajor);
    
    msg2->setValue(18);
    msg2->setType(AnalogPointType);
    msg2->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    msg3->setValue(12);
    msg3->setType(AnalogPointType);
    msg3->setId(64);

    points.push_back(msg1);
    points.push_back(msg3); // This is where the change is from the previous test!
    points.push_back(msg2); // Order matters!

    TestCbc8020Device::combineFirmwarePoints(points);
    
    // Three go in, two are combined, and two come back!
    BOOST_REQUIRE_EQUAL(points.size(), 2);

    BOOST_REQUIRE_EQUAL(points[0]->getValue(), 12);
    BOOST_REQUIRE_EQUAL(points[0]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[0]->getId(),    64);

    BOOST_REQUIRE_EQUAL(points[1]->getValue(), 3.18);
    BOOST_REQUIRE_EQUAL(points[1]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[1]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    delete_container(points);
}

BOOST_AUTO_TEST_CASE(test_firmware_points_minor_major_extra)
{
    CtiPointDataMsg *msg1 = new CtiPointDataMsg(), *msg2 = new CtiPointDataMsg(), *msg3 = new CtiPointDataMsg();
    Cti::Protocol::Interface::pointlist_t points;
    
    msg1->setValue(7);
    msg1->setType(AnalogPointType);
    msg1->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMajor);
    
    msg2->setValue(28);
    msg2->setType(AnalogPointType);
    msg2->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    msg3->setValue(18);
    msg3->setType(AnalogPointType);
    msg3->setId(99);

    points.push_back(msg2);
    points.push_back(msg1); // This is where the change is from the previous test!
    points.push_back(msg3); // Order matters!

    TestCbc8020Device::combineFirmwarePoints(points);
    
    // Three go in, two are combined, and two come back!
    BOOST_REQUIRE_EQUAL(points.size(), 2);

    BOOST_REQUIRE_EQUAL(points[0]->getValue(), 18);
    BOOST_REQUIRE_EQUAL(points[0]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[0]->getId(),    99);

    BOOST_REQUIRE_EQUAL(points[1]->getValue(), 7.28);
    BOOST_REQUIRE_EQUAL(points[1]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[1]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    delete_container(points);
}

BOOST_AUTO_TEST_CASE(test_firmware_points_minor_extra_major)
{
    CtiPointDataMsg *msg1 = new CtiPointDataMsg(), *msg2 = new CtiPointDataMsg(), *msg3 = new CtiPointDataMsg();
    Cti::Protocol::Interface::pointlist_t points;
    
    msg1->setValue(7);
    msg1->setType(AnalogPointType);
    msg1->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMajor);
    
    msg2->setValue(28);
    msg2->setType(AnalogPointType);
    msg2->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    msg3->setValue(18);
    msg3->setType(AnalogPointType);
    msg3->setId(99);

    points.push_back(msg2);
    points.push_back(msg3); // This is where the change is from the previous test!
    points.push_back(msg1); // Order matters!

    TestCbc8020Device::combineFirmwarePoints(points);
    
    // Three go in, two are combined, and two come back!
    BOOST_REQUIRE_EQUAL(points.size(), 2);

    BOOST_REQUIRE_EQUAL(points[0]->getValue(), 18);
    BOOST_REQUIRE_EQUAL(points[0]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[0]->getId(),    99);

    BOOST_REQUIRE_EQUAL(points[1]->getValue(), 7.28);
    BOOST_REQUIRE_EQUAL(points[1]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[1]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    delete_container(points);
}

BOOST_AUTO_TEST_CASE(test_firmware_points_extra_major_minor)
{
    CtiPointDataMsg *msg1 = new CtiPointDataMsg(), *msg2 = new CtiPointDataMsg(), *msg3 = new CtiPointDataMsg();
    Cti::Protocol::Interface::pointlist_t points;
    
    msg1->setValue(7);
    msg1->setType(AnalogPointType);
    msg1->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMajor);
    
    msg2->setValue(28);
    msg2->setType(AnalogPointType);
    msg2->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    msg3->setValue(18);
    msg3->setType(AnalogPointType);
    msg3->setId(99);

    points.push_back(msg3);
    points.push_back(msg1); // This is where the change is from the previous test!
    points.push_back(msg2); // Order matters!

    TestCbc8020Device::combineFirmwarePoints(points);
    
    // Three go in, two are combined, and two come back!
    BOOST_REQUIRE_EQUAL(points.size(), 2);

    BOOST_REQUIRE_EQUAL(points[0]->getValue(), 18);
    BOOST_REQUIRE_EQUAL(points[0]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[0]->getId(),    99);

    BOOST_REQUIRE_EQUAL(points[1]->getValue(), 7.28);
    BOOST_REQUIRE_EQUAL(points[1]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[1]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    delete_container(points);
}

BOOST_AUTO_TEST_CASE(test_firmware_points_extra_minor_major)
{
    CtiPointDataMsg *msg1 = new CtiPointDataMsg(), *msg2 = new CtiPointDataMsg(), *msg3 = new CtiPointDataMsg();
    Cti::Protocol::Interface::pointlist_t points;
    
    msg1->setValue(7);
    msg1->setType(AnalogPointType);
    msg1->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMajor);
    
    msg2->setValue(28);
    msg2->setType(AnalogPointType);
    msg2->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMinor);

    msg3->setValue(18);
    msg3->setType(AnalogPointType);
    msg3->setId(99);

    points.push_back(msg3);
    points.push_back(msg2); // This is where the change is from the previous test!
    points.push_back(msg1); // Order matters!

    TestCbc8020Device::combineFirmwarePoints(points);
    
    // Three go in, two are combined, and two come back!
    BOOST_REQUIRE_EQUAL(points.size(), 2);

    BOOST_REQUIRE_EQUAL(points[0]->getValue(), 18);
    BOOST_REQUIRE_EQUAL(points[0]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[0]->getId(),    99);

    BOOST_REQUIRE_EQUAL(points[1]->getValue(), 7.28);
    BOOST_REQUIRE_EQUAL(points[1]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[1]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    delete_container(points);
}

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
    msg1->setTime(before);

    msg2->setValue(4);
    msg2->setType(AnalogPointType);
    msg2->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMajor);
    msg2->setTime(after);

    msg3->setValue(28);
    msg3->setType(AnalogPointType);
    msg3->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMinor);
    msg3->setTime(after);

    msg4->setValue(58);
    msg4->setType(AnalogPointType);
    msg4->setId(TestCbc8020Device::PointOffset_FirmwareRevisionMinor);
    msg4->setTime(before);

    points.push_back(msg1);
    points.push_back(msg2);
    points.push_back(msg3);
    points.push_back(msg4);

    TestCbc8020Device::combineFirmwarePoints(points);
    
    // Three go in, two are combined, and two come back!
    BOOST_REQUIRE_EQUAL(points.size(), 1);

    /**
     * Irregular case where two majors were encountered followed by 
     * two minors. We expect to keep the value of the most recent 
     * major message (by timestamp) though, and expect that the 
     * other one will be removed from the vector of points. 
     * Similarly for the minor messages. 
     */
    BOOST_REQUIRE_EQUAL(points[0]->getValue(), 4.28);
    BOOST_REQUIRE_EQUAL(points[0]->getType(),  AnalogPointType);
    BOOST_REQUIRE_EQUAL(points[0]->getId(),    TestCbc8020Device::PointOffset_FirmwareRevisionMajor);

    delete_container(points);
}
