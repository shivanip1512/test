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
        setAllPointsLoaded(true);
    }

    using CtiPointManager::addPoint;
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

    //  configure and add one status point with a control offset
    CtiPointStatus  *point_status1 = Cti::Test::makeControlPoint(device1_id, status1_id, point1_offset, control1_offset, ControlType_Normal);

    manager.addPoint(point_status1);
    //  we need to replace the following tests with point replacements from addPoint on the same pointid
/*
    //  make sure everything's still copasetic
    BOOST_CHECK_EQUAL(manager.getControlOffsetEqual(device1_id, control1_offset).get(),                point_status1);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual   (device1_id, point1_offset, StatusPointType).get(), point_status1);

    point_status1->setPointOffset(point2_offset);

    manager.updatePointMaps(*point_status1, device1_id, StatusPointType, point1_offset, control1_offset);

    //  make sure everything's still copasetic
    BOOST_CHECK_EQUAL(manager.getControlOffsetEqual(device1_id, control1_offset).get(),                point_status1);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual   (device1_id, point1_offset, StatusPointType).get(), (CtiPointBase *)0);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual   (device1_id, point2_offset, StatusPointType).get(), point_status1);

    point_status1->setControlOffset(control2_offset);

    manager.updatePointMaps(*point_status1, device1_id, StatusPointType, point2_offset, control1_offset);

    //  make sure everything's still copasetic
    BOOST_CHECK_EQUAL(manager.getControlOffsetEqual(device1_id, control1_offset).get(),                (CtiPointBase *)0);
    BOOST_CHECK_EQUAL(manager.getControlOffsetEqual(device1_id, control2_offset).get(),                point_status1);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual   (device1_id, point2_offset, StatusPointType).get(), point_status1);

    point_status1->setDeviceID(device2_id);

    manager.updatePointMaps(*point_status1, device1_id, StatusPointType, point2_offset, control2_offset);

    //  make sure everything's still copasetic
    BOOST_CHECK_EQUAL(manager.getControlOffsetEqual(device1_id, control2_offset).get(),                (CtiPointBase *)0);
    BOOST_CHECK_EQUAL(manager.getControlOffsetEqual(device2_id, control2_offset).get(),                point_status1);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual   (device1_id, point2_offset, StatusPointType).get(), (CtiPointBase *)0);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual   (device2_id, point2_offset, StatusPointType).get(), point_status1);

    //  this is a little weird - it's still a status point, but it will be treated as an analog
    //    this points to a problem in the code structure - you shouldn't be able to set the type
    //    of a point, it should be defined by its class
    point_status1->setType(AnalogPointType);
    point_status1->setControlOffset(0);

    manager.updatePointMaps(*point_status1, device2_id, StatusPointType, point2_offset, control2_offset);

    //  make sure everything's still copasetic
    BOOST_CHECK_EQUAL(manager.getControlOffsetEqual(device2_id, control2_offset).get(),                (CtiPointBase *)0);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual   (device2_id, point2_offset, StatusPointType).get(), (CtiPointBase *)0);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual   (device2_id, point2_offset, AnalogPointType).get(), point_status1);
*/
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

/*
BOOST_AUTO_TEST_CASE(test_mgr_point_get_equal_by_pao)
{
    Test_CtiPointManager manager;

    CtiPointStatus *point_status1 = Cti::Test::makeStatusPoint(device1_id, status1_id, 1);
    CtiPointStatus *point_status2 = Cti::Test::makeStatusPoint(device1_id, status2_id, 2);
    CtiPointAnalog *point_analog1 = Cti::Test::makeAnalogPoint(device1_id, analog1_id, 1);
    CtiPointAnalog *point_analog2 = Cti::Test::makeAnalogPoint(device1_id, analog2_id, 2);
    CtiPointAnalog *point_analog3 = Cti::Test::makeAnalogPoint(device2_id, analog3_id, 1);

    manager.addPoint(point_status1);
    manager.addPoint(point_status2);
    manager.addPoint(point_analog1);
    manager.addPoint(point_analog2);
    manager.addPoint(point_analog3);

    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual(device1_id, 1, StatusPointType).get(), point_status1);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual(device1_id, 2, StatusPointType).get(), point_status2);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual(device1_id, 1, AnalogPointType).get(), point_analog1);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual(device1_id, 2, AnalogPointType).get(), point_analog2);
    BOOST_CHECK_EQUAL(manager.getOffsetTypeEqual(device2_id, 1, AnalogPointType).get(), point_analog3);
}
*/

BOOST_AUTO_TEST_SUITE_END()
