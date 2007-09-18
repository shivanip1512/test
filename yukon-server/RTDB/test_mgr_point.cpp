/*
 * file test_mgr_point.cpp
 *  
 * Author: Matt Fisher
 * Date: 09/11/2007 10:58
 * 
 * 
 */


#include <boost/test/unit_test.hpp>

#include <string>
#include <iostream>

//  for the winsock2.h inclusion by mgr_point.h (?!)
#define _WIN32_WINNT 0x0400

#include "mgr_point.h"
#include "pt_status.h"
#include "pt_analog.h"
#include "pt_accum.h"


#define BOOST_AUTO_TEST_MAIN "Test Point Manager"
#include <boost/test/auto_unit_test.hpp>
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
};

template <class T>
T *make_point(CtiPointType_t type, int pointid, int deviceid)
{
    T *new_point = new T();

    new_point->setID(pointid);
    new_point->setDeviceID(deviceid);
    new_point->setType(type);
    new_point->setUpdatedFlag(true);

    return new_point;
}


BOOST_AUTO_UNIT_TEST(test_mgr_point_get_control_offset)
{
    Test_CtiPointManager manager;
    Test_CtiPointStatus  *point_status1, 
                         *point_status2,
                         *point_status3;

    const int control_offset_contentious = 15,
              control_offset_unique      = 999;

    //  configure and add one status point with a control offset
    point_status1 = make_point<Test_CtiPointStatus>(StatusPointType, status1_id, device1_id);
    point_status2 = make_point<Test_CtiPointStatus>(StatusPointType, status2_id, device2_id);
    point_status3 = make_point<Test_CtiPointStatus>(StatusPointType, status3_id, device2_id);

    point_status1->setControlOffset(control_offset_contentious);
    point_status2->setControlOffset(control_offset_contentious);
    point_status3->setControlOffset(control_offset_unique);

    manager.addPoint(point_status1);
    manager.addPoint(point_status2);
    manager.addPoint(point_status3);

    //  make sure everything's still copasetic
    BOOST_CHECK_EQUAL(manager.getControlOffsetEqual(device1_id, control_offset_contentious).get(), point_status1);
    BOOST_CHECK_EQUAL(manager.getControlOffsetEqual(device2_id, control_offset_contentious).get(), point_status2);
    BOOST_CHECK_EQUAL(manager.getControlOffsetEqual(device2_id, control_offset_unique).get(),      point_status3);
}


BOOST_AUTO_UNIT_TEST(test_mgr_point_get_type_offset)
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

