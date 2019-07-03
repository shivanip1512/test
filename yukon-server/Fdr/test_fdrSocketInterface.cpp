#include <boost/test/unit_test.hpp>

#include "fdrsocketinterface.h"
#include "ctidate.h"

#include <string>

using namespace std::string_literals;

BOOST_AUTO_TEST_SUITE( test_fdrSocketInterface )

struct testSocketInterface : public CtiFDRSocketInterface {
    using Inherited = CtiFDRSocketInterface;

    using Inherited::setPointTimeVariation;
    using Inherited::setSendToList;

    bool built = false;

    testSocketInterface() : CtiFDRSocketInterface("testSocketInterface"s, 1337, 120) {
    }

    bool loadList(std::string &aDirection, CtiFDRPointList &aList) override {
        return true;
    }
    CHAR *buildForeignSystemHeartbeatMsg(void) override {
        return nullptr;
    }
    INT getMessageSize(CHAR *data) override {
        return 0;
    }
    std::string decodeClientName(CHAR *data) override {
        return "";
    }
    bool buildAndWriteToForeignSystem(CtiFDRPoint &aPoint) override {
        return built = true;
    }
    int processMessageFromForeignSystem(CHAR *data) override {
        return 0;
    }
    bool loadTranslationLists(void) override {
        return false;
    }
    bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList) override {
        return true;
    }
};

BOOST_AUTO_TEST_CASE( test_sendMessageToForeignSys )
{
    CtiDate d { 14, 3, 2009 };
    CtiTime t { d, 12, 34, 56 };

    testSocketInterface si;

    CtiFDRPointList sendToList;

    CtiFDRManager fdrManager("Test manager");

    sendToList.setPointList(&fdrManager);

    si.setSendToList(sendToList);

    //Initialize the interface to have a point in a group.
    CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

    fdrPoint->setPointID(43);
    fdrPoint->setLastTimeStamp(t);
    fdrPoint->setValue(31);

    fdrPoint->setDestinationList(std::vector<CtiFDRDestination>{{fdrPoint->getPointID(), "unused", "Test Destination"}});

    fdrManager.getMap().emplace(fdrPoint->getPointID(), fdrPoint);

    CtiPointDataMsg m;
    m.setId(43);
    m.setTime(t + 500);
    m.setTags(TAG_POINT_OLD_TIMESTAMP);
    m.setValue(31);

    //  Test point time variation
    {
        si.setPointTimeVariation(900);

        BOOST_CHECK_EQUAL(si.sendMessageToForeignSys(&m), true);

        //  Make sure our point was not updated
        BOOST_CHECK_EQUAL(fdrPoint->getLastTimeStamp(), t);
        BOOST_CHECK_EQUAL(si.built, false);

        si.setPointTimeVariation(0);
    }

    //  Test registration filtering
    {
        m.setTags(TAG_POINT_MOA_REPORT);

        BOOST_CHECK_EQUAL(si.sendMessageToForeignSys(&m), false);

        //  Make sure our point was updated
        BOOST_CHECK_EQUAL(fdrPoint->getLastTimeStamp(), t + 500);
        BOOST_CHECK_EQUAL(si.built, false);

        m.resetTags(TAG_POINT_MOA_REPORT);
    }

    //  Test missing point ID
    {
        m.setId(42);

        BOOST_CHECK_EQUAL(si.sendMessageToForeignSys(&m), false);
        BOOST_CHECK_EQUAL(si.built, false);

        m.setId(43);
    }

    //  Test epoch filtering
    {
        CtiTime preEpoch { CtiDate { 25, 12, 1999 }, 12, 34, 56 };

        m.setTime(preEpoch);

        BOOST_CHECK_EQUAL(si.sendMessageToForeignSys(&m), false);
        BOOST_CHECK_EQUAL(fdrPoint->getLastTimeStamp(), preEpoch);
        BOOST_CHECK_EQUAL(si.built, false);

        fdrPoint->setLastTimeStamp(t);
        m.setTime(t + 500);
    }

    //  Test registration filtering
    {
        si.setRegistered(false);

        BOOST_CHECK_EQUAL(si.sendMessageToForeignSys(&m), true);
        BOOST_CHECK_EQUAL(fdrPoint->getLastTimeStamp(), t + 500);
        BOOST_CHECK_EQUAL(si.built, false);

        fdrPoint->setLastTimeStamp(t);
        si.setRegistered(true);
    }

    //  Test successful build-and-write
    {
        BOOST_CHECK_EQUAL(si.sendMessageToForeignSys(&m), true);
        BOOST_CHECK_EQUAL(fdrPoint->getLastTimeStamp(), t + 500);
        BOOST_CHECK_EQUAL(si.built, true);
    }

    //  Reset point lists so the destructors don't try to delete the local variables
    sendToList.setPointList(nullptr);
    si.setSendToList(sendToList);
}

BOOST_AUTO_TEST_SUITE_END()
