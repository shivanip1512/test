#define BOOST_AUTO_TEST_MAIN "Test Mgr PtClients"

#include "yukon.h"
#include "mgr_ptclients.h"
#include "pt_status.h"
#include "pt_analog.h"

#include <boost/thread/thread.hpp>
#include <boost/test/unit_test.hpp>
#include <boost/test/auto_unit_test.hpp>
#include <boost/test/floating_point_comparison.hpp>

using namespace std;

using boost::unit_test_framework::test_suite;

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

enum
{
    begin_id   =  9,

    analog1_id,
    analog2_id,
    analog3_id,

    status1_id,
    status2_id,

    device1_id,
    device2_id,

    point1_offset = 1,
    point2_offset,
};

BOOST_AUTO_TEST_CASE(test_alarming)
{
    Test_CtiPointClientManager manager;
    BOOST_CHECK(manager.entries() == 0);

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

    CtiPointSPtr point_status1_sptr = CtiPointSPtr(point_status1);

    Test_CtiTablePointAlarming alarm;
    alarm = manager.getAlarming(point_status1_sptr);

    BOOST_CHECK(!(alarm.getAlarmCategory(0) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(1) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(2) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(3) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(4) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(5) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(6) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(7) > SignalEvent));
    BOOST_CHECK(alarm.getAutoAckStates() == 0);

    alarm.setAlarmCategory(1, 3);

    BOOST_CHECK(alarm.getAlarmCategory(1) > SignalEvent);

    manager.addAlarming(alarm);

    alarm = manager.getAlarming(point_status1_sptr);

    BOOST_CHECK(alarm.getAlarmCategory(1) > SignalEvent);

    BOOST_CHECK(!(alarm.getAlarmCategory(0) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(2) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(3) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(4) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(5) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(6) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(7) > SignalEvent));
    BOOST_CHECK(alarm.getAutoAckStates() == 0);

    manager.removeAlarming(status1_id);

    alarm = manager.getAlarming(point_status1_sptr);

    BOOST_CHECK(!(alarm.getAlarmCategory(0) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(1) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(2) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(3) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(4) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(5) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(6) > SignalEvent));
    BOOST_CHECK(!(alarm.getAlarmCategory(7) > SignalEvent));
    BOOST_CHECK(alarm.getAutoAckStates() == 0);
}

BOOST_AUTO_TEST_CASE(test_dynamic)
{
    Test_CtiPointClientManager manager;
    BOOST_CHECK(manager.entries() == 0);

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

    CtiPointSPtr point_status1_sptr = CtiPointSPtr(point_status1);

    CtiDynamicPointDispatchSPtr dynamic = manager.getDynamic(point_status1_sptr);

    BOOST_CHECK(!dynamic);

    dynamic = CtiDynamicPointDispatchSPtr(CTIDBG_new CtiDynamicPointDispatch(status1_id));
    manager.setDynamic(status1_id, dynamic);
    
    CtiDynamicPointDispatchSPtr pDispatch = manager.getDynamic(point_status1_sptr);
    BOOST_CHECK(pDispatch);

    BOOST_CHECK_EQUAL(pDispatch.get(), dynamic.get());

    manager.erase(status1_id);
    pDispatch = manager.getDynamic(point_status1_sptr);

    BOOST_CHECK(!pDispatch);
}
