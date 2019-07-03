#include <boost/test/unit_test.hpp>

#include "fdrsocketserver.h"
#include "ctidate.h"

#include <string>

using namespace std::string_literals;

BOOST_AUTO_TEST_SUITE( test_fdrSocketServer )

struct testSocketServer : public CtiFDRSocketServer {
    using super = CtiFDRSocketServer;

    using super::setPointTimeVariation;
    using super::getSendToList;

    testSocketServer() : CtiFDRSocketServer("testSocketInterface"s) {
    }

    int processMessageFromForeignSystem(Cti::Fdr::ServerConnection& connection, const char* data, unsigned int size) override {
        return 0;
    }
    bool loadTranslationLists(void) override {
        return false;
    }
    bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList) override {
        return true;
    }
    void cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList) override {
    }
    unsigned int getMessageSize(const char* data) override {
        return 0;
    }
    unsigned int getHeaderLength() override {
        return 0;
    }
    CtiFDRClientServerConnectionSPtr createNewConnection(SOCKET newConnection) override {
        return nullptr;
    }
    bool buildForeignSystemMessage(const CtiFDRDestination& destination, char** buffer, unsigned int& bufferSize) override {
        return true;
    }
};

BOOST_AUTO_TEST_CASE(test_sendMessageToForeignSys)
{
    CtiDate d { 14, 3, 2009 };
    CtiTime t { d, 12, 34, 56 };

    testSocketServer ss;

    //Initialize the interface to have a point in a group.
    CtiFDRPointSPtr fdrPoint(new CtiFDRPoint());

    fdrPoint->setPointID(43);
    fdrPoint->setLastTimeStamp(t);
    fdrPoint->setValue(31);

    fdrPoint->setDestinationList(std::vector<CtiFDRDestination>{{fdrPoint->getPointID(), "unused", "Test Destination"}});

    ss.getSendToList().getPointList()->getMap().emplace(fdrPoint->getPointID(), fdrPoint);

    CtiPointDataMsg m;
    m.setId(43);
    m.setTime(t + 500);
    m.setTags(TAG_POINT_OLD_TIMESTAMP);
    m.setValue(31);

    //  Test old timestamp filtering
    {
        ss.setIgnoreOldData(true);

        BOOST_CHECK_EQUAL(ss.sendMessageToForeignSys(&m), false);

        //  Make sure our point was not updated
        BOOST_CHECK_EQUAL(fdrPoint->getLastTimeStamp(), t);

        ss.setIgnoreOldData(false);
    }

    //  Test point time variation
    {
        ss.setPointTimeVariation(900);

        BOOST_CHECK_EQUAL(ss.sendMessageToForeignSys(&m), false);

        //  Make sure our point was not updated
        BOOST_CHECK_EQUAL(fdrPoint->getLastTimeStamp(), t);

        ss.setPointTimeVariation(0);
    }

    //  Test registration filtering
    {
        m.setTags(TAG_POINT_MOA_REPORT);

        BOOST_CHECK_EQUAL(ss.sendMessageToForeignSys(&m), false);

        //  Make sure our point was updated
        BOOST_CHECK_EQUAL(fdrPoint->getLastTimeStamp(), t + 500);

        m.resetTags(TAG_POINT_MOA_REPORT);
    }

    //  Test missing point ID
    {
        m.setId(42);

        BOOST_CHECK_EQUAL(ss.sendMessageToForeignSys(&m), false);

        m.setId(43);
    }

    //  Test epoch filtering
    {
        CtiTime preEpoch{ CtiDate { 25, 12, 1999 }, 12, 34, 56 };

        m.setTime(preEpoch);

        BOOST_CHECK_EQUAL(ss.sendMessageToForeignSys(&m), false);
        BOOST_CHECK_EQUAL(fdrPoint->getLastTimeStamp(), preEpoch);

        fdrPoint->setLastTimeStamp(t);
        m.setTime(t + 500);
    }
}

BOOST_AUTO_TEST_SUITE_END()
