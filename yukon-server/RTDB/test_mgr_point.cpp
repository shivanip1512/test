/*
 * file test_mgr_point.cpp
 *
 * Author: Matt Fisher
 * Date: 09/11/2007 10:58
 *
 *
 */

#include <boost/test/floating_point_comparison.hpp>

#define BOOST_TEST_MAIN "Test mgr_point"
#include <boost/test/unit_test.hpp>
#include <string>
#include <iostream>

//  for the winsock.h inclusion by mgr_point.h (?!)
#include "yukon.h"
#include "mgr_point.h"
#include "pt_status.h"
#include "pt_analog.h"
#include "pt_accum.h"


#define BOOST_AUTO_TEST_MAIN "Test Point Manager"
using boost::unit_test_framework::test_suite;

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

template <class T>
T *make_point(long deviceid, long pointid, CtiPointType_t type, int offset)
{
    T *new_point = new T();

    new_point->setID(pointid);
    new_point->setDeviceID(deviceid);
    new_point->setType(type);
    new_point->setPointOffset(offset);
    new_point->setUpdatedFlag(true);

    return new_point;
}


BOOST_AUTO_TEST_CASE(test_mgr_point_get_control_offset)
{
    Test_CtiPointManager manager;
    Test_CtiPointStatus  *point_status1,
                         *point_status2,
                         *point_status3;

    const int control_offset_contentious = 15,
              control_offset_unique      = 999;

    //  configure and add one status point with a control offset
    point_status1 = make_point<Test_CtiPointStatus>(device1_id, status1_id, StatusPointType, point1_offset);
    point_status2 = make_point<Test_CtiPointStatus>(device2_id, status2_id, StatusPointType, point2_offset);
    point_status3 = make_point<Test_CtiPointStatus>(device2_id, status3_id, StatusPointType, point3_offset);

    point_status1->setControlOffset(control_offset_contentious);
    point_status2->setControlOffset(control_offset_contentious);
    point_status3->setControlOffset(control_offset_unique);

    point_status1->setControlType(NormalControlType);
    point_status2->setControlType(NormalControlType);
    point_status3->setControlType(NormalControlType);

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
    Test_CtiPointStatus  *point_status1;  //  status point so we can check control offset

    const int control1_offset = 42,
              control2_offset = 49;

    //  configure and add one status point with a control offset
    point_status1 = make_point<Test_CtiPointStatus>(device1_id, status1_id, StatusPointType, point1_offset);

    point_status1->setControlOffset(control1_offset);
    point_status1->setControlType(NormalControlType);

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

    Test_CtiPointStatus *point_status1,
                        *point_status2;
    Test_CtiPointAnalog *point_analog1,
                        *point_analog2,
                        *point_analog3;

    point_status1 = make_point<Test_CtiPointStatus>(device1_id, status1_id, StatusPointType, point1_offset);
    point_status2 = make_point<Test_CtiPointStatus>(device1_id, status2_id, StatusPointType, point2_offset);
    point_analog1 = make_point<Test_CtiPointAnalog>(device1_id, analog1_id, AnalogPointType, point1_offset);
    point_analog2 = make_point<Test_CtiPointAnalog>(device1_id, analog2_id, AnalogPointType, point2_offset);
    point_analog3 = make_point<Test_CtiPointAnalog>(device2_id, analog3_id, AnalogPointType, point1_offset);

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

    Test_CtiPointStatus *point_status1,
                        *point_status2;
    Test_CtiPointAnalog *point_analog1,
                        *point_analog2,
                        *point_analog3;

    point_status1 = make_point<Test_CtiPointStatus>(StatusPointType, status1_id, device1_id);
    point_status2 = make_point<Test_CtiPointStatus>(StatusPointType, status2_id, device1_id);
    point_analog1 = make_point<Test_CtiPointAnalog>(AnalogPointType, analog1_id, device1_id);
    point_analog2 = make_point<Test_CtiPointAnalog>(AnalogPointType, analog2_id, device1_id);
    point_analog3 = make_point<Test_CtiPointAnalog>(AnalogPointType, analog3_id, device2_id);

    point_status1->setPointOffset(1);
    point_status2->setPointOffset(2);
    point_analog1->setPointOffset(1);
    point_analog2->setPointOffset(2);
    point_analog3->setPointOffset(1);

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
