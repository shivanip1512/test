#include <boost/test/unit_test.hpp>

#include "tbl_pt_alarm.h"
#include "signalmanager.h"

using namespace std;

BOOST_AUTO_TEST_SUITE( test_signalmanager )

struct test_CtiSignalManager : CtiSignalManager
{
    void deleteDynamicPointAlarming(long pointID, int condition) override
    {
        //  no-op, do not connect to the DB
    }
};

BOOST_AUTO_TEST_CASE(test_signalmanager_signal_add)
{
    test_CtiSignalManager manager;
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
    manager.addSignal(testMessage1, false); //mark dirty is false
    BOOST_CHECK(!manager.empty());
    BOOST_CHECK(!manager.dirty());
    BOOST_CHECK_EQUAL(manager.entries(), 1);
    BOOST_CHECK_EQUAL(manager.pointMapEntries(), 1);
    manager.addSignal(testMessage2);
    BOOST_CHECK(!manager.empty());
    BOOST_CHECK(manager.dirty());
    BOOST_CHECK_EQUAL(manager.entries(), 2);
    BOOST_CHECK_EQUAL(manager.pointMapEntries(), 2);
    manager.addSignal(testMessage2); // re-add an existing signal, should not add another.
    BOOST_CHECK_EQUAL(manager.entries(), 2);
    BOOST_CHECK_EQUAL(manager.pointMapEntries(), 2);
}

// Please note this alarm also tests a bug with the two maps that are now in the signal manager.
// If you change it be sure to leave in the double checks for entries and pointMapEntries
// Or even better dont change it and create a new test.
BOOST_AUTO_TEST_CASE(test_signalmanager_alarming)
{
    test_CtiSignalManager manager;
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
    BOOST_CHECK_EQUAL(manager.pointMapEntries(), 2);
    //basic setup complete.
    delete manager.setAlarmActive(1, CtiTablePointAlarming::commandFailure);
    BOOST_CHECK(manager.isAlarmed(1, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(manager.isAlarmActive(1, CtiTablePointAlarming::commandFailure));
    //Check our other point
    BOOST_CHECK(!manager.isAlarmed(2, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(!manager.isAlarmed(2, CtiTablePointAlarming::uncommandedStateChange));
    BOOST_CHECK(!manager.isAlarmActive(2, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(!manager.isAlarmActive(2, CtiTablePointAlarming::uncommandedStateChange));
    BOOST_CHECK(manager.isAlarmUnacknowledged(1, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(!manager.isAlarmUnacknowledged(1, CtiTablePointAlarming::uncommandedStateChange));

    delete manager.setAlarmAcknowledged(1, CtiTablePointAlarming::uncommandedStateChange);//This is not the alarm!
    BOOST_CHECK(manager.isAlarmed(1, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(manager.isAlarmActive(1, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(!manager.isAlarmed(1, CtiTablePointAlarming::uncommandedStateChange));
    BOOST_CHECK(!manager.isAlarmActive(1, CtiTablePointAlarming::uncommandedStateChange));
    BOOST_CHECK(manager.isAlarmUnacknowledged(1, CtiTablePointAlarming::commandFailure));

    delete manager.setAlarmAcknowledged(1, CtiTablePointAlarming::commandFailure);
    BOOST_CHECK(manager.isAlarmed(1, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(manager.isAlarmActive(1, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(!manager.isAlarmUnacknowledged(1, CtiTablePointAlarming::commandFailure));
    delete manager.clearAlarms(1);
    delete manager.setAlarmActive(1, CtiTablePointAlarming::commandFailure, false);
    BOOST_CHECK(!manager.isAlarmed(1, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(!manager.isAlarmActive(1, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK(!manager.isAlarmUnacknowledged(1, CtiTablePointAlarming::commandFailure));
    BOOST_CHECK_EQUAL(manager.entries(), 1);
    BOOST_CHECK_EQUAL(manager.pointMapEntries(), 1);

    manager.addSignal(testMessage1);
    testMessage1.setCondition(CtiTablePointAlarming::uncommandedStateChange);
    manager.addSignal(testMessage1);
    BOOST_CHECK_EQUAL(manager.entries(), 3);
    BOOST_CHECK_EQUAL(manager.pointMapEntries(), 3);
    delete manager.setAlarmActive(1, CtiTablePointAlarming::commandFailure, false);
    BOOST_CHECK_EQUAL(manager.entries(), 3);
    BOOST_CHECK_EQUAL(manager.pointMapEntries(), 3);
    delete manager.setAlarmAcknowledged(1, CtiTablePointAlarming::commandFailure);
    BOOST_CHECK_EQUAL(manager.entries(), 2);
    BOOST_CHECK_EQUAL(manager.pointMapEntries(), 2);
    delete manager.setAlarmAcknowledged(1, CtiTablePointAlarming::uncommandedStateChange);
    BOOST_CHECK_EQUAL(manager.entries(), 2);
    BOOST_CHECK_EQUAL(manager.pointMapEntries(), 2);
    delete manager.setAlarmActive(1, CtiTablePointAlarming::uncommandedStateChange, false);
    BOOST_CHECK_EQUAL(manager.entries(), 1);
    BOOST_CHECK_EQUAL(manager.pointMapEntries(), 1);
}

BOOST_AUTO_TEST_CASE(test_signalmanager_getters)
{
    test_CtiSignalManager manager;
    CtiSignalMsg *testMessage1, *testMessage2;
    testMessage1 = CTIDBG_new CtiSignalMsg(1);
    testMessage2 = CTIDBG_new CtiSignalMsg(1);
    testMessage1->setCondition(CtiTablePointAlarming::commandFailure);
    testMessage2->setCondition(CtiTablePointAlarming::uncommandedStateChange);
    testMessage1->setSignalCategory(SignalEvent);
    testMessage2->setSignalCategory(SignalAlarm0);
    testMessage1->setTags(TAG_ACTIVE_CONDITION | TAG_ACTIVE_ALARM);
    testMessage2->setTags(TAG_ACTIVE_CONDITION | TAG_UNACKNOWLEDGED_ALARM);
    manager.addSignal(*testMessage1);
    manager.addSignal(*testMessage1);
    manager.addSignal(*testMessage2);
    BOOST_CHECK_EQUAL(manager.entries(), 2);
    BOOST_CHECK_EQUAL(manager.pointMapEntries(), 2);
    delete testMessage1;
    testMessage1 = 0;
    delete testMessage2;
    testMessage2 = 0;

    BOOST_CHECK_EQUAL(manager.getConditionTags(1, CtiTablePointAlarming::commandFailure), TAG_ACTIVE_CONDITION | TAG_ACTIVE_ALARM);
    BOOST_CHECK_EQUAL(manager.getConditionTags(1, CtiTablePointAlarming::uncommandedStateChange), TAG_ACTIVE_CONDITION | TAG_UNACKNOWLEDGED_ALARM);

    BOOST_CHECK_EQUAL(manager.getTagMask(1),TAG_ACTIVE_CONDITION | TAG_ACTIVE_ALARM | TAG_UNACKNOWLEDGED_ALARM);
    delete manager.setAlarmAcknowledged(1, CtiTablePointAlarming::commandFailure, true);
    BOOST_CHECK_EQUAL(manager.getTagMask(1),TAG_ACTIVE_CONDITION | TAG_ACTIVE_ALARM | TAG_UNACKNOWLEDGED_ALARM);
    delete manager.setAlarmAcknowledged(1, CtiTablePointAlarming::uncommandedStateChange, true);
    BOOST_CHECK_EQUAL(manager.getTagMask(1), TAG_ACTIVE_CONDITION | TAG_ACTIVE_ALARM);

    CtiSignalMsg* messagePtr;
    messagePtr = manager.getAlarm(1, CtiTablePointAlarming::commandFailure);
    BOOST_CHECK(messagePtr != NULL);
    if( messagePtr != NULL )
    {
        BOOST_CHECK(messagePtr->getCondition() == CtiTablePointAlarming::commandFailure);
        BOOST_CHECK(messagePtr->getSignalCategory() == SignalEvent);
        BOOST_CHECK(messagePtr->getTags() == (TAG_ACTIVE_CONDITION | TAG_ACTIVE_ALARM));
        delete messagePtr;
        messagePtr = NULL;
    }

    {
        auto tempMulti = manager.getPointSignals(1);
        BOOST_CHECK(tempMulti);
        if( tempMulti )
        {
            BOOST_CHECK_EQUAL(tempMulti->getCount(), 2);
            if( tempMulti->getCount() == 2 )
            {
                CtiSignalMsg *pMsg1 = (CtiSignalMsg*)tempMulti->getData()[0];
                CtiSignalMsg *pMsg2 = (CtiSignalMsg*)tempMulti->getData()[1];
                BOOST_CHECK_EQUAL(pMsg1->getId(), pMsg2->getId());
                BOOST_CHECK(pMsg1->getSignalCategory() != pMsg2->getSignalCategory());
            }
        }
    }

    auto tempMulti = manager.getAllAlarmSignals();
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

    delete manager.setAlarmActive(1, CtiTablePointAlarming::uncommandedStateChange, false);
    BOOST_CHECK_EQUAL(manager.entries(), 1);
    BOOST_CHECK_EQUAL(manager.pointMapEntries(), 1);

    {
        auto tempMulti = manager.getPointSignals(1);
        BOOST_CHECK(tempMulti);
        if( tempMulti )
        {
            BOOST_CHECK_EQUAL(tempMulti->getCount(), 1);
        }

        tempMulti = manager.getPointSignals(3);
        if( tempMulti )
        {
            BOOST_CHECK_EQUAL(tempMulti->getCount(), 0);
        }
    }
}

BOOST_AUTO_TEST_SUITE_END()
