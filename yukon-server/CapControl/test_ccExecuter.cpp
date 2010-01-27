#define BOOST_AUTO_TEST_MAIN "CapControl Executer"

#include <boost/test/unit_test.hpp>

#include "yukon.h"
#include "ccsubstationbusstore.h"
#include "ccexecutor.h"

#include "ccUnitTestUtil.h"
#include "LoadTapChanger.h"

#include "AttributeService.h"
#include "PointAttribute.h"
#include "ccmessage.h"

/**
 * Created to hide the DB from unit tests. Returns good results.
 *
 */
class TestAttributeService : public AttributeService
{
    public:
        virtual std::list<LitePoint> getExtraPaoPoints(int paoId)
        {
            //No Op. Just hiding the database
            std::list<LitePoint> pointList;
            return pointList;
        }
        virtual LitePoint getPointByPaoAndAttribute(int paoId, const PointAttribute& attribute)
        {
            switch (attribute.value())
            {
                case PointAttribute::KeepAliveAttribute:
                {
                    //Analog
                    LitePoint point;
                    point.setPointType(AnalogPointType);\
                    point.setPointId(42);
                    return point;
                }
                case PointAttribute::RaiseTapAttribute:
                {
                    //status Truefalse
                    LitePoint point;
                    point.setPointType(StatusPointType);
                    point.setPointId(42);
                    return point;
                }
                case PointAttribute::LowerTapAttribute:
                {
                    //status Truefalse
                    LitePoint point;
                    point.setPointType(StatusPointType);
                    point.setPointId(42);
                    return point;
                }
                case PointAttribute::LtcVoltageAttribute:
                {
                    //Analog
                    LitePoint point;
                    point.setPointType(AnalogPointType);
                    point.setPointId(42);
                    return point;
                }
                case PointAttribute::AutoRemoteControlAttribute:
                {
                    //status Remote Local
                    LitePoint point;
                    point.setPointType(StatusPointType);
                    point.setPointId(42);
                    return point;
                }
                case PointAttribute::TapPositionAttribute:
                {
                    //Analog
                    LitePoint point;
                    point.setPointType(AnalogPointType);
                    point.setPointId(42);
                    return point;
                }
                case PointAttribute::UnknownAttribute:
                default:
                {
                    return LitePoint();
                }
            }
        }
};

/**
 * Created to hide the DB from unit tests. Returns Invalid
 * points.
 *
 */
class TestAttributeService_ErrorCase : public AttributeService
{
    public:
        virtual std::list<LitePoint> getExtraPaoPoints(int paoId)
        {
            //No Op. Just hiding the database
            std::list<LitePoint> pointList;
            return pointList;
        }
        virtual LitePoint getPointByPaoAndAttribute(int paoId, const PointAttribute& attribute)
        {
            return LitePoint();
        }
};


void preTestSetup()
{
    Test_CtiCCSubstationBusStore* testStore = new Test_CtiCCSubstationBusStore();
    CtiCCSubstationBusStore::setInstance(testStore);

    LoadTapChangerPtr ltcPtr = new LoadTapChanger();
    ltcPtr->setPaoId(1);
    ltcPtr->setPaoName("Ltc Number 1");
    testStore->insertLtcToPaoMap(ltcPtr);

}

BOOST_AUTO_TEST_CASE(test_scanLtcIntegrity)
{
    preTestSetup();

    std::vector<CtiMessage*> toDispatch;
    std::vector<CtiCCEventLogMsg*> events;
    std::vector<CtiRequestMsg*> requests;

    //Shortcutting to just the number of messages returned.
    //These tests could be enhanced by checking the messages returned to see if they are expected.

    //Successful command.
    {
        delete_container(toDispatch);
        delete_container(events);
        delete_container(requests);
        toDispatch.clear();
        events.clear();
        requests.clear();

        TestAttributeService* attributeService = new TestAttributeService();
        CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::LTC_SCAN_INTEGRITY, 1);//Consumed in exectuor.
        CtiCCCommandExecutor commandExecutor(commandMsg);
        commandExecutor.setAttributeService(attributeService);

        commandExecutor.scanLtcIntegrity(commandMsg->getCommand(),toDispatch,events,requests);

        BOOST_CHECK(toDispatch.size() == 1);
        BOOST_CHECK(events.size() == 0);
        BOOST_CHECK(requests.size() == 1);
    }

    //PaoId of 0 Error case. No out messages is the only way to tell... Add error flags?
    {
        delete_container(toDispatch);
        delete_container(events);
        delete_container(requests);
        toDispatch.clear();
        events.clear();
        requests.clear();

        TestAttributeService* attributeService = new TestAttributeService();
        CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::LTC_SCAN_INTEGRITY, 0);//Consumed in exectuor.
        CtiCCCommandExecutor commandExecutor(commandMsg);
        commandExecutor.setAttributeService(attributeService);

        commandExecutor.scanLtcIntegrity(commandMsg->getCommand(),toDispatch,events,requests);

        BOOST_CHECK(toDispatch.size() == 0);
        BOOST_CHECK(events.size() == 0);
        BOOST_CHECK(requests.size() == 0);
    }

    //PaoId of 2 does not exist. No out messages is the only way to tell... Add error flags?
    {
        delete_container(toDispatch);
        delete_container(events);
        delete_container(requests);
        toDispatch.clear();
        events.clear();
        requests.clear();

        TestAttributeService* attributeService = new TestAttributeService();
        CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::LTC_SCAN_INTEGRITY, 2);//Consumed in exectuor.
        CtiCCCommandExecutor commandExecutor(commandMsg);
        commandExecutor.setAttributeService(attributeService);

        commandExecutor.scanLtcIntegrity(commandMsg->getCommand(),toDispatch,events,requests);

        BOOST_CHECK(toDispatch.size() == 0);
        BOOST_CHECK(events.size() == 0);
        BOOST_CHECK(requests.size() == 0);
    }

    //Point Attribute Not Found. No out messages is the only way to tell... Add error flags?
    {
        delete_container(toDispatch);
        delete_container(events);
        delete_container(requests);
        toDispatch.clear();
        events.clear();
        requests.clear();

        TestAttributeService_ErrorCase* attributeService = new TestAttributeService_ErrorCase();
        CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::LTC_SCAN_INTEGRITY, 1);//Consumed in exectuor.
        CtiCCCommandExecutor commandExecutor(commandMsg);
        commandExecutor.setAttributeService(attributeService);

        commandExecutor.scanLtcIntegrity(commandMsg->getCommand(),toDispatch,events,requests);

        BOOST_CHECK(toDispatch.size() == 0);
        BOOST_CHECK(events.size() == 0);
        BOOST_CHECK(requests.size() == 0);
    }
}

BOOST_AUTO_TEST_CASE(test_sendLtcRemoteControl)
{
    preTestSetup();

    std::vector<CtiMessage*> toDispatch;
    std::vector<CtiCCEventLogMsg*> events;
    std::vector<CtiRequestMsg*> requests;

    //Shortcutting to just the number of messages returned.
    //These tests could be enhanced by checking the messages returned to see if they are expected.

    //Successful command.
    {
        delete_container(toDispatch);
        delete_container(events);
        delete_container(requests);
        toDispatch.clear();
        events.clear();
        requests.clear();

        TestAttributeService* attributeService = new TestAttributeService();
        CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::LTC_REMOTE_CONTROL_ENABLE, 1);//Consumed in exectuor.
        CtiCCCommandExecutor commandExecutor(commandMsg);
        commandExecutor.setAttributeService(attributeService);

        commandExecutor.sendLtcRemoteControl(commandMsg->getCommand(),toDispatch,events,requests);

        BOOST_CHECK(toDispatch.size() == 1);
        BOOST_CHECK(events.size() == 0);
        BOOST_CHECK(requests.size() == 1);
    }

    //PaoId of 0 Error case. No out messages is the only way to tell... Add error flags?
    {
        delete_container(toDispatch);
        delete_container(events);
        delete_container(requests);
        toDispatch.clear();
        events.clear();
        requests.clear();

        TestAttributeService* attributeService = new TestAttributeService();
        CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::LTC_REMOTE_CONTROL_ENABLE, 0);//Consumed in exectuor.
        CtiCCCommandExecutor commandExecutor(commandMsg);
        commandExecutor.setAttributeService(attributeService);

        commandExecutor.sendLtcRemoteControl(commandMsg->getCommand(),toDispatch,events,requests);

        BOOST_CHECK(toDispatch.size() == 0);
        BOOST_CHECK(events.size() == 0);
        BOOST_CHECK(requests.size() == 0);
    }

    //PaoId of 2 does not exist. No out messages is the only way to tell... Add error flags?
    {
        delete_container(toDispatch);
        delete_container(events);
        delete_container(requests);
        toDispatch.clear();
        events.clear();
        requests.clear();

        TestAttributeService* attributeService = new TestAttributeService();
        CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::LTC_REMOTE_CONTROL_ENABLE, 2);//Consumed in exectuor.
        CtiCCCommandExecutor commandExecutor(commandMsg);
        commandExecutor.setAttributeService(attributeService);

        commandExecutor.sendLtcRemoteControl(commandMsg->getCommand(),toDispatch,events,requests);

        BOOST_CHECK(toDispatch.size() == 0);
        BOOST_CHECK(events.size() == 0);
        BOOST_CHECK(requests.size() == 0);
    }

    //Point Attribute Not Found. No out messages is the only way to tell... Add error flags?
    {
        delete_container(toDispatch);
        delete_container(events);
        delete_container(requests);
        toDispatch.clear();
        events.clear();
        requests.clear();

        TestAttributeService_ErrorCase* attributeService = new TestAttributeService_ErrorCase();
        CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::LTC_REMOTE_CONTROL_ENABLE, 1);//Consumed in exectuor.
        CtiCCCommandExecutor commandExecutor(commandMsg);
        commandExecutor.setAttributeService(attributeService);

        commandExecutor.sendLtcRemoteControl(commandMsg->getCommand(),toDispatch,events,requests);

        BOOST_CHECK(toDispatch.size() == 0);
        BOOST_CHECK(events.size() == 0);
        BOOST_CHECK(requests.size() == 0);
    }
}

BOOST_AUTO_TEST_CASE(test_sendLtcTapPosition)
{
    preTestSetup();

    std::vector<CtiMessage*> toDispatch;
    std::vector<CtiRequestMsg*> requests;

    //Shortcutting to just the number of messages returned.
    //These tests could be enhanced by checking the messages returned to see if they are expected.

    //Successful command.
    {
        delete_container(toDispatch);
        delete_container(requests);
        toDispatch.clear();
        requests.clear();

        TestAttributeService* attributeService = new TestAttributeService();
        CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::LTC_TAP_POSITION_RAISE, 1);//Consumed in exectuor.
        CtiCCCommandExecutor commandExecutor(commandMsg);
        commandExecutor.setAttributeService(attributeService);

        commandExecutor.sendLtcTapPosition(commandMsg->getCommand(),toDispatch,requests);

        BOOST_CHECK(toDispatch.size() == 1);
        BOOST_CHECK(requests.size() == 1);
    }

    //PaoId of 0 Error case. No out messages is the only way to tell... Add error flags?
    {
        delete_container(toDispatch);
        delete_container(requests);
        toDispatch.clear();
        requests.clear();

        TestAttributeService* attributeService = new TestAttributeService();
        CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::LTC_TAP_POSITION_RAISE, 0);//Consumed in exectuor.
        CtiCCCommandExecutor commandExecutor(commandMsg);
        commandExecutor.setAttributeService(attributeService);

        commandExecutor.sendLtcTapPosition(commandMsg->getCommand(),toDispatch,requests);

        BOOST_CHECK(toDispatch.size() == 0);
        BOOST_CHECK(requests.size() == 0);
    }

    //PaoId of 2 does not exist. No out messages is the only way to tell... Add error flags?
    {
        delete_container(toDispatch);
        delete_container(requests);
        toDispatch.clear();
        requests.clear();

        TestAttributeService* attributeService = new TestAttributeService();
        CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::LTC_TAP_POSITION_RAISE, 2);//Consumed in exectuor.
        CtiCCCommandExecutor commandExecutor(commandMsg);
        commandExecutor.setAttributeService(attributeService);

        commandExecutor.sendLtcTapPosition(commandMsg->getCommand(),toDispatch,requests);

        BOOST_CHECK(toDispatch.size() == 0);
        BOOST_CHECK(requests.size() == 0);
    }

    //Point Attribute Not Found. No out messages is the only way to tell... Add error flags?
    {
        delete_container(toDispatch);
        delete_container(requests);
        toDispatch.clear();
        requests.clear();

        TestAttributeService_ErrorCase* attributeService = new TestAttributeService_ErrorCase();
        CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::LTC_TAP_POSITION_RAISE, 1);//Consumed in exectuor.
        CtiCCCommandExecutor commandExecutor(commandMsg);
        commandExecutor.setAttributeService(attributeService);

        commandExecutor.sendLtcTapPosition(commandMsg->getCommand(),toDispatch,requests);

        BOOST_CHECK(toDispatch.size() == 0);
        BOOST_CHECK(requests.size() == 0);
    }
}

BOOST_AUTO_TEST_CASE(test_sendLtcKeepAlive)
{
    preTestSetup();

    std::vector<CtiMessage*> toDispatch;
    std::vector<CtiRequestMsg*> requests;

    //Shortcutting to just the number of messages returned.
    //These tests could be enhanced by checking the messages returned to see if they are expected.

    //Successful command.
    {
        delete_container(toDispatch);
        toDispatch.clear();

        TestAttributeService* attributeService = new TestAttributeService();
        CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::LTC_KEEP_ALIVE, 1);//Consumed in exectuor.
        CtiCCCommandExecutor commandExecutor(commandMsg);
        commandExecutor.setAttributeService(attributeService);

        commandExecutor.sendLtcKeepAlive(commandMsg->getCommand(),toDispatch);

        BOOST_CHECK(toDispatch.size() == 2);
    }

    //PaoId of 0 Error case. No out messages is the only way to tell... Add error flags?
    {
        delete_container(toDispatch);
        toDispatch.clear();

        TestAttributeService* attributeService = new TestAttributeService();
        CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::LTC_KEEP_ALIVE, 0);//Consumed in exectuor.
        CtiCCCommandExecutor commandExecutor(commandMsg);
        commandExecutor.setAttributeService(attributeService);

        commandExecutor.sendLtcKeepAlive(commandMsg->getCommand(),toDispatch);

        BOOST_CHECK(toDispatch.size() == 0);
    }

    //PaoId of 2 does not exist. No out messages is the only way to tell... Add error flags?
    {
        delete_container(toDispatch);
        toDispatch.clear();

        TestAttributeService* attributeService = new TestAttributeService();
        CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::LTC_KEEP_ALIVE, 2);//Consumed in exectuor.
        CtiCCCommandExecutor commandExecutor(commandMsg);
        commandExecutor.setAttributeService(attributeService);

        commandExecutor.sendLtcKeepAlive(commandMsg->getCommand(),toDispatch);

        BOOST_CHECK(toDispatch.size() == 0);
    }

    //Point Attribute Not Found. No out messages is the only way to tell... Add error flags?
    {
        delete_container(toDispatch);
        toDispatch.clear();

        TestAttributeService_ErrorCase* attributeService = new TestAttributeService_ErrorCase();
        CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::LTC_KEEP_ALIVE, 1);//Consumed in exectuor.
        CtiCCCommandExecutor commandExecutor(commandMsg);
        commandExecutor.setAttributeService(attributeService);

        commandExecutor.sendLtcKeepAlive(commandMsg->getCommand(),toDispatch);

        BOOST_CHECK(toDispatch.size() == 0);
    }
}
