
/*-----------------------------------------------------------------------------*
*
* File:   test_signalmanager.cpp
*
* Date:   1/11/2008
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/COMMON/INCLUDE/test_queue.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2008/01/14 17:23:09 $
*
* Copyright (c) 2008 Cannon Technologies All rights reserved.
*-----------------------------------------------------------------------------*/
#define BOOST_AUTO_TEST_MAIN "Test SignalManager"

#include <boost/thread/thread.hpp>
#include <boost/test/unit_test.hpp>
#include <boost/test/auto_unit_test.hpp>
#include <boost/test/floating_point_comparison.hpp>
#include "yukon.h"
#include "tbl_pt_alarm.h"
#include "signalmanager.h"

using namespace std;

using boost::unit_test_framework::test_suite;

BOOST_AUTO_UNIT_TEST(test_signalmanager_signal_add)
{
    CtiSignalManager manager;
    BOOST_CHECK(manager.empty());

    CtiSignalMsg testMessage1, testMessage2;
    testMessage1.setCondition(0);
    testMessage2.setCondition(0);
    manager.addSignal(testMessage1);
    manager.addSignal(testMessage2);
    //there is no condition yet so this signal should not have been added.
    BOOST_CHECK(manager.empty());
    BOOST_CHECK(!manager.dirty());

    testMessage1.setCondition(CtiTablePointAlarming::commandFailure);//This one is pretty common
    testMessage2.setCondition(CtiTablePointAlarming::uncommandedStateChange);//This one is less common
    testMessage1.setSignalCategory(SignalEvent); //Common
    testMessage2.setSignalCategory(SignalAlarm0); // Also common
    testMessage1.setTags(TAG_ACTIVE_CONDITION);
    testMessage2.setTags(TAG_ACTIVE_CONDITION);
    manager.addSignal(testMessage1, true); //dont mark dirty is true
    BOOST_CHECK(!manager.empty());
    BOOST_CHECK(!manager.dirty());
    BOOST_CHECK_EQUAL(manager.entries(), 1);
    manager.addSignal(testMessage2);
    BOOST_CHECK(!manager.empty());
    BOOST_CHECK(manager.dirty());
    BOOST_CHECK_EQUAL(manager.entries(), 2);
    manager.addSignal(testMessage2); // re-add an existing signal, should not add another.
    BOOST_CHECK_EQUAL(manager.entries(), 2);
}

BOOST_AUTO_UNIT_TEST(test_signalmanager_alarming)
{
    CtiSignalManager manager;
    BOOST_CHECK(manager.empty());

    CtiSignalMsg testMessage1, testMessage2;
    testMessage1.setId(1);
    testMessage2.setId(2);
    testMessage1.setCondition(CtiTablePointAlarming::commandFailure);
    testMessage2.setCondition(CtiTablePointAlarming::uncommandedStateChange);
    testMessage1.setSignalCategory(SignalEvent);
    testMessage2.setSignalCategory(SignalAlarm0);
    testMessage1.setTags(TAG_ACTIVE_CONDITION | TAG_ACTIVE_ALARM | TAG_UNACKNOWLEDGED_ALARM);
    testMessage2.setTags(TAG_ACTIVE_CONDITION);
    manager.addSignal(testMessage1);
    manager.addSignal(testMessage2);
    BOOST_CHECK_EQUAL(manager.entries(), 2);
    //basic setup complete.
    manager.setAlarmActive(1, CtiTablePointAlarming::commandFailure);
    BOOST_CHECK(manager.isAlarmed(1, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(manager.isAlarmActive(1, CtiTablePointAlarming::commandFailure));
    //Check our other point
    BOOST_CHECK(!manager.isAlarmed(2, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(!manager.isAlarmed(2, CtiTablePointAlarming::uncommandedStateChange));
    BOOST_CHECK(!manager.isAlarmActive(2, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(!manager.isAlarmActive(2, CtiTablePointAlarming::uncommandedStateChange));
    BOOST_CHECK(manager.isAlarmUnacknowledged(1, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(!manager.isAlarmUnacknowledged(1, CtiTablePointAlarming::uncommandedStateChange));

    manager.setAlarmAcknowledged(1, CtiTablePointAlarming::uncommandedStateChange);//This is not the alarm!
    BOOST_CHECK(manager.isAlarmed(1, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(manager.isAlarmActive(1, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(!manager.isAlarmed(1, CtiTablePointAlarming::uncommandedStateChange));
    BOOST_CHECK(!manager.isAlarmActive(1, CtiTablePointAlarming::uncommandedStateChange));
    BOOST_CHECK(manager.isAlarmUnacknowledged(1, CtiTablePointAlarming::commandFailure));

    manager.setAlarmAcknowledged(1, CtiTablePointAlarming::commandFailure);
    BOOST_CHECK(manager.isAlarmed(1, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(manager.isAlarmActive(1, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(!manager.isAlarmUnacknowledged(1, CtiTablePointAlarming::commandFailure));
    manager.clearAlarms(1);
    manager.setAlarmActive(1, CtiTablePointAlarming::commandFailure, false);
    BOOST_CHECK(!manager.isAlarmed(1, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(!manager.isAlarmActive(1, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(!manager.isAlarmUnacknowledged(1, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK_EQUAL(manager.entries(), 1);

    manager.addSignal(testMessage1);
    testMessage1.setCondition(CtiTablePointAlarming::uncommandedStateChange);
    manager.addSignal(testMessage1);
    BOOST_CHECK_EQUAL(manager.entries(), 3);
    manager.setAlarmActive(1, CtiTablePointAlarming::commandFailure, false);
    BOOST_CHECK_EQUAL(manager.entries(), 3);
    manager.setAlarmAcknowledged(1, CtiTablePointAlarming::commandFailure);
    BOOST_CHECK_EQUAL(manager.entries(), 2);
    manager.setAlarmAcknowledged(1, CtiTablePointAlarming::uncommandedStateChange);
    BOOST_CHECK_EQUAL(manager.entries(), 2);
    manager.setAlarmActive(1, CtiTablePointAlarming::uncommandedStateChange, false);
    BOOST_CHECK_EQUAL(manager.entries(), 1);
}

BOOST_AUTO_UNIT_TEST(test_signalmanager_getters)
{
    CtiSignalManager manager;
    CtiSignalMsg testMessage1, testMessage2;
    testMessage1.setId(1);
    testMessage2.setId(1);
    testMessage1.setCondition(CtiTablePointAlarming::commandFailure);
    testMessage2.setCondition(CtiTablePointAlarming::uncommandedStateChange);
    testMessage1.setSignalCategory(SignalEvent);
    testMessage2.setSignalCategory(SignalAlarm0);
    testMessage1.setTags(TAG_ACTIVE_CONDITION | TAG_ACTIVE_ALARM);
    testMessage2.setTags(TAG_ACTIVE_CONDITION | TAG_UNACKNOWLEDGED_ALARM);
    manager.addSignal(testMessage1);
    manager.addSignal(testMessage2);
    BOOST_CHECK_EQUAL(manager.entries(), 2);

    BOOST_CHECK_EQUAL(manager.getConditionTags(1, CtiTablePointAlarming::commandFailure), TAG_ACTIVE_CONDITION | TAG_ACTIVE_ALARM);
    BOOST_CHECK_EQUAL(manager.getConditionTags(1, CtiTablePointAlarming::uncommandedStateChange), TAG_ACTIVE_CONDITION | TAG_UNACKNOWLEDGED_ALARM);

    BOOST_CHECK_EQUAL(manager.getTagMask(1),TAG_ACTIVE_CONDITION | TAG_ACTIVE_ALARM | TAG_UNACKNOWLEDGED_ALARM);
    manager.setAlarmAcknowledged(1, CtiTablePointAlarming::commandFailure, true);
    BOOST_CHECK_EQUAL(manager.getTagMask(1),TAG_ACTIVE_CONDITION | TAG_ACTIVE_ALARM | TAG_UNACKNOWLEDGED_ALARM);
    manager.setAlarmAcknowledged(1, CtiTablePointAlarming::uncommandedStateChange, true);
    BOOST_CHECK_EQUAL(manager.getTagMask(1),TAG_ACTIVE_CONDITION | TAG_ACTIVE_ALARM);

    CtiSignalMsg* messagePtr;
    messagePtr = manager.getAlarm(1, CtiTablePointAlarming::commandFailure);
    BOOST_CHECK(messagePtr != NULL);
    if( messagePtr != NULL )
    {
        BOOST_CHECK(messagePtr->getCondition() == testMessage1.getCondition());
        BOOST_CHECK(messagePtr->getSignalCategory() == testMessage1.getSignalCategory());
        BOOST_CHECK(messagePtr->getTags() == testMessage1.getTags());
        delete messagePtr;
        messagePtr = NULL;
    }

    CtiMultiMsg* tempMulti = manager.getPointSignals(1);
    BOOST_CHECK(tempMulti != NULL);
    if( tempMulti != NULL )
    {
        BOOST_CHECK_EQUAL(tempMulti->getCount(), 2);
        if( tempMulti->getCount() == 2 )
        {
            CtiSignalMsg *pMsg1 = (CtiSignalMsg*)tempMulti->getData()[0];
            CtiSignalMsg *pMsg2 = (CtiSignalMsg*)tempMulti->getData()[1];
            BOOST_CHECK_EQUAL(pMsg1->getId(), pMsg2->getId());
            BOOST_CHECK(pMsg1->getSignalCategory() != pMsg2->getSignalCategory());
        }
        delete tempMulti;
        tempMulti = NULL;
    }

    tempMulti = manager.getAllAlarmSignals();
    BOOST_CHECK(tempMulti != NULL);
    if( tempMulti != NULL )
    {
        BOOST_CHECK_EQUAL(tempMulti->getCount(), 1);
        delete tempMulti;
        tempMulti = NULL;
    }

    tempMulti = manager.getCategorySignals(SignalEvent);
    BOOST_CHECK(tempMulti != NULL);
    if( tempMulti != NULL )
    {
        BOOST_CHECK_EQUAL(tempMulti->getCount(), 1);
        delete tempMulti;
        tempMulti = NULL;
    }

}
