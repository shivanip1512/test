#include <boost/test/unit_test.hpp>

#include "mgr_point.h"
#include "pt_status.h"
#include "pt_analog.h"
#include "pt_accum.h"

#include "rtdb_test_helpers.h"

BOOST_AUTO_TEST_SUITE( test_mgr_point )

enum
{
    begin_id   =  9,

    analog1_id,
    analog2_id,
    analog3_id,

    status1_id,
    status2_id,
    status3_id,

    device1_id,
    device2_id,

    point1_offset = 1,
    point2_offset,
    point3_offset,
    point4_offset,
};


struct Test_CtiPointManager : public CtiPointManager
{
    Test_CtiPointManager()
    {
        setAllPointsLoaded(true, test_tag);
    }

    using CtiPointManager::addPoint;

    time_t test_currentTime = 0x50000000;  //  Arbitrarily chosen - July 13, 2012 at 6:01:20 AM

    time_t currentTime() override
    {
        return test_currentTime;
    }

    int test_maxPointsAllowed = 100000;

    int maxPointsAllowed() override
    {
        return test_maxPointsAllowed;
    }
};


BOOST_AUTO_TEST_CASE(test_mgr_point_get_control_offset)
{
    Test_CtiPointManager manager;

    const int control_offset_contentious = 15,
              control_offset_unique      = 999;

    //  configure and add one status point with a control offset
    CtiPointStatus *point_status1 = Cti::Test::makeControlPoint(device1_id, status1_id, point1_offset, control_offset_contentious, ControlType_Normal);
    CtiPointStatus *point_status2 = Cti::Test::makeControlPoint(device2_id, status2_id, point2_offset, control_offset_contentious, ControlType_Normal);
    CtiPointStatus *point_status3 = Cti::Test::makeControlPoint(device2_id, status3_id, point3_offset, control_offset_unique,      ControlType_Normal);

    manager.addPoint(point_status1);
    manager.addPoint(point_status2);
    manager.addPoint(point_status3);

    //  make sure everything's still copasetic
    BOOST_CHECK_EQUAL(manager.getControlOffsetEqual(device1_id, control_offset_contentious).get(), point_status1);
    BOOST_CHECK_EQUAL(manager.getControlOffsetEqual(device2_id, control_offset_contentious).get(), point_status2);
    BOOST_CHECK_EQUAL(manager.getControlOffsetEqual(device2_id, control_offset_unique).get(),      point_status3);
}


BOOST_AUTO_TEST_CASE(test_mgr_point_changes)
{
    Test_CtiPointManager manager;

    const int control1_offset = 42,
              control2_offset = 49;

    CtiPointStatus* point_status1;
    
    //  configure and add one status point with a control offset
    point_status1 = Cti::Test::makeControlPoint(device1_id, status1_id, point1_offset, control1_offset, ControlType_Normal);

    manager.addPoint(point_status1);

    //  make sure everything's still copasetic
    BOOST_CHECK_EQUAL(manager.getControlOffsetEqual(device1_id, control1_offset).get(),                point_status1);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual   (device1_id, point1_offset, StatusPointType).get(), point_status1);

    point_status1 = Cti::Test::makeControlPoint(device1_id, status1_id, point2_offset, control1_offset, ControlType_Normal);
    manager.addPoint(point_status1);

    //  make sure everything's still copasetic
    BOOST_CHECK_EQUAL(manager.getControlOffsetEqual(device1_id, control1_offset).get(),                point_status1);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual   (device1_id, point1_offset, StatusPointType).get(), (CtiPointBase *)0);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual   (device1_id, point2_offset, StatusPointType).get(), point_status1);

    point_status1 = Cti::Test::makeControlPoint(device1_id, status1_id, point2_offset, control2_offset, ControlType_Normal);
    manager.addPoint(point_status1);

    //  make sure everything's still copasetic
    BOOST_CHECK_EQUAL(manager.getControlOffsetEqual(device1_id, control1_offset).get(),                (CtiPointBase *)0);
    BOOST_CHECK_EQUAL(manager.getControlOffsetEqual(device1_id, control2_offset).get(),                point_status1);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual   (device1_id, point2_offset, StatusPointType).get(), point_status1);

    point_status1 = Cti::Test::makeControlPoint(device2_id, status1_id, point2_offset, control2_offset, ControlType_Normal);
    manager.addPoint(point_status1);

    //  make sure everything's still copasetic
    BOOST_CHECK_EQUAL(manager.getControlOffsetEqual(device1_id, control2_offset).get(),                (CtiPointBase *)0);
    BOOST_CHECK_EQUAL(manager.getControlOffsetEqual(device2_id, control2_offset).get(),                point_status1);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual   (device1_id, point2_offset, StatusPointType).get(), (CtiPointBase *)0);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual   (device2_id, point2_offset, StatusPointType).get(), point_status1);
}


BOOST_AUTO_TEST_CASE(test_mgr_point_get_type_offset)
{
    Test_CtiPointManager manager;

    CtiPointStatus *point_status1 = Cti::Test::makeStatusPoint(device1_id, status1_id, point1_offset);
    CtiPointStatus *point_status2 = Cti::Test::makeStatusPoint(device1_id, status2_id, point2_offset);
    CtiPointAnalog *point_analog1 = Cti::Test::makeAnalogPoint(device1_id, analog1_id, point1_offset);
    CtiPointAnalog *point_analog2 = Cti::Test::makeAnalogPoint(device1_id, analog2_id, point2_offset);
    CtiPointAnalog *point_analog3 = Cti::Test::makeAnalogPoint(device2_id, analog3_id, point1_offset);

    manager.addPoint(point_status1);
    manager.addPoint(point_status2);
    manager.addPoint(point_analog1);
    manager.addPoint(point_analog2);
    manager.addPoint(point_analog3);

    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual(device1_id, point1_offset, StatusPointType).get(), point_status1);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual(device1_id, point2_offset, StatusPointType).get(), point_status2);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual(device1_id, point1_offset, AnalogPointType).get(), point_analog1);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual(device1_id, point2_offset, AnalogPointType).get(), point_analog2);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual(device2_id, point1_offset, AnalogPointType).get(), point_analog3);
}

BOOST_AUTO_TEST_CASE(test_mgr_point_get_equal_by_pao)
{
    Test_CtiPointManager manager;

    manager.addPoint(Cti::Test::makeStatusPoint(device1_id, status1_id, 1));
    manager.addPoint(Cti::Test::makeStatusPoint(device1_id, status2_id, 2));
    manager.addPoint(Cti::Test::makeAnalogPoint(device1_id, analog1_id, 1));
    manager.addPoint(Cti::Test::makeAnalogPoint(device1_id, analog2_id, 2));
    manager.addPoint(Cti::Test::makeAnalogPoint(device2_id, analog3_id, 1));

    std::vector<CtiPointSPtr> points;

    manager.getEqualByPAO(device1_id, points);

    BOOST_REQUIRE_EQUAL(points.size(), 4);

    BOOST_CHECK_EQUAL(points[0]->getDeviceID(), device1_id);
    BOOST_CHECK_EQUAL(points[1]->getDeviceID(), device1_id);
    BOOST_CHECK_EQUAL(points[2]->getDeviceID(), device1_id);
    BOOST_CHECK_EQUAL(points[3]->getDeviceID(), device1_id);

    points.clear();

    manager.getEqualByPAO(device2_id, points);

    BOOST_REQUIRE_EQUAL(points.size(), 1);

    BOOST_CHECK_EQUAL(points[0]->getDeviceID(), device2_id);
}

BOOST_AUTO_TEST_CASE(test_mgr_point_get_equal_by_name)
{
    Test_CtiPointManager manager;

    manager.addPoint(Cti::Test::makeStatusPoint(device1_id, status1_id, 1));
    manager.addPoint(Cti::Test::makeStatusPoint(device1_id, status2_id, 2));
    manager.addPoint(Cti::Test::makeStatusPoint(device2_id, status3_id, 1));

    {
        auto pt = manager.getEqualByName(device1_id, "Status1");

        BOOST_REQUIRE(pt);

        BOOST_CHECK_EQUAL(pt->getDeviceID(), device1_id);
        BOOST_CHECK_EQUAL(pt->getPointID(), status1_id);
    }

    {
        auto pt = manager.getEqualByName(device1_id, "Status2");

        BOOST_REQUIRE(pt);

        BOOST_CHECK_EQUAL(pt->getDeviceID(), device1_id);
        BOOST_CHECK_EQUAL(pt->getPointID(), status2_id);
    }

    {
        auto pt = manager.getEqualByName(device2_id, "Status1");

        BOOST_REQUIRE(pt);

        BOOST_CHECK_EQUAL(pt->getDeviceID(), device1_id);  //  BUG!
        BOOST_CHECK_EQUAL(pt->getPointID(), status1_id);   //  BUG!
    }

    {
        auto pt = manager.getEqualByName(device2_id, "Status2");

        BOOST_REQUIRE(pt);  //  BUG!

        BOOST_CHECK_EQUAL(pt->getDeviceID(), device1_id);  //  BUG!
        BOOST_CHECK_EQUAL(pt->getPointID(), status2_id);   //  BUG!
    }
}

BOOST_AUTO_TEST_CASE(test_pointExpiration)
{
    Test_CtiPointManager mgr;

    mgr.test_maxPointsAllowed = 3;

    CtiPointStatus *point_status1 = Cti::Test::makeStatusPoint(device1_id, status1_id, point1_offset);
    CtiPointStatus *point_status2 = Cti::Test::makeStatusPoint(device1_id, status2_id, point2_offset);
    CtiPointAnalog *point_analog1 = Cti::Test::makeAnalogPoint(device1_id, analog1_id, point1_offset);
    CtiPointAnalog *point_analog2 = Cti::Test::makeAnalogPoint(device1_id, analog2_id, point2_offset);
    CtiPointAnalog *point_analog3 = Cti::Test::makeAnalogPoint(device2_id, analog3_id, point1_offset);

    mgr.addPoint(point_status1);
    mgr.addPoint(point_status2);
    mgr.addPoint(point_analog1);
    mgr.addPoint(point_analog2);
    mgr.addPoint(point_analog3);

    //  Check expiration immediately, make sure nothing expired.
    BOOST_CHECK_EQUAL(mgr.entries(), 5);
    mgr.processExpired();
    BOOST_CHECK_EQUAL(mgr.entries(), 5);

    //  Two minutes later, access status 1, then check expiration and make sure nothing expired.
    mgr.test_currentTime += 120;
    BOOST_CHECK_EQUAL(mgr.getPoint(status1_id).get(), point_status1);
    mgr.processExpired();
    BOOST_CHECK_EQUAL(mgr.entries(), 5);

    //  Two minutes later, access status 2, then check expiration and make sure nothing expired.
    mgr.test_currentTime += 120;
    BOOST_CHECK_EQUAL(mgr.getPoint(status2_id).get(), point_status2);
    mgr.processExpired();
    BOOST_CHECK_EQUAL(mgr.entries(), 5);

    //  Two minutes later, access analog 1, then check expiration and make sure the other two analogs expired.
    mgr.test_currentTime += 120;
    BOOST_CHECK_EQUAL(mgr.getPoint(analog1_id).get(), point_analog1);
    mgr.processExpired();
    BOOST_CHECK_EQUAL(mgr.entries(), 3);

    BOOST_CHECK_EQUAL(mgr.getPoint(status1_id).get(), point_status1);
    BOOST_CHECK_EQUAL(mgr.getPoint(status2_id).get(), point_status2);
    BOOST_CHECK_EQUAL(mgr.getPoint(analog1_id).get(), point_analog1);
    BOOST_CHECK( ! mgr.getPoint(analog2_id));
    BOOST_CHECK( ! mgr.getPoint(analog3_id));

    //  Two minutes later, access analog 1, then check expiration and make sure nothing expired.
    mgr.test_currentTime += 120;
    BOOST_CHECK_EQUAL(mgr.getPoint(analog1_id).get(), point_analog1);
    mgr.processExpired();
    BOOST_CHECK_EQUAL(mgr.entries(), 3);
}

BOOST_AUTO_TEST_SUITE_END()
