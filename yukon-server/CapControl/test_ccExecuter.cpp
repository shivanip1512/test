#define BOOST_AUTO_TEST_MAIN "CapControl Executer"

#include <boost/test/unit_test.hpp>

#include "yukon.h"
#include "ccsubstationbusstore.h"
#include "ccexecutor.h"
#include "ccUnitTestUtil.h"
#include "AttributeService.h"
#include "PointAttribute.h"
#include "ccmessage.h"
#include "VoltageRegulatorLoader.h"
#include "GangOperatedVoltageRegulator.h"

// Objects
using Cti::CapControl::VoltageRegulator;
using Cti::CapControl::VoltageRegulatorLoader;
using Cti::CapControl::VoltageRegulatorManager;
using Cti::CapControl::GangOperatedVoltageRegulator;

// Exceptions
using Cti::CapControl::NoVoltageRegulator;
using Cti::CapControl::MissingPointAttribute;


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
                case PointAttribute::TapUpAttribute:
                {
                    //status Truefalse
                    LitePoint point;
                    point.setPointType(StatusPointType);
                    point.setPointId(42);
                    return point;
                }
                case PointAttribute::TapDownAttribute:
                {
                    //status Truefalse
                    LitePoint point;
                    point.setPointType(StatusPointType);
                    point.setPointId(42);
                    return point;
                }
                case PointAttribute::VoltageAttribute:
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


class VoltageRegulatorUnitTestLoader : public VoltageRegulatorLoader
{
public:

    virtual VoltageRegulatorManager::VoltageRegulatorMap load(const long Id)
    {
        VoltageRegulatorManager::VoltageRegulatorMap    regulators;

        regulators[1] = VoltageRegulatorManager::SharedPtr(new GangOperatedVoltageRegulator);
        regulators[1]->setPaoId(1);
        regulators[1]->setPaoType(VoltageRegulator::LoadTapChanger);

        return regulators;
    }
};


struct TestCapControlBusStore : public CtiCCSubstationBusStore
{
    void setVoltageRegulatorManager(boost::shared_ptr<Cti::CapControl::VoltageRegulatorManager> manager)
    {
        _voltageRegulatorManager = manager;
    }

    void initialize()
    {
        TestAttributeService    attributes;

        boost::shared_ptr<VoltageRegulatorManager> manager( new VoltageRegulatorManager( std::auto_ptr<VoltageRegulatorUnitTestLoader>( new VoltageRegulatorUnitTestLoader ) ) );
        manager->setAttributeService( & attributes );

        setVoltageRegulatorManager(manager);
        reloadVoltageRegulatorFromDatabase(-1);   // reload all
    }
};


struct TestCapControlBusStore_ErrorCase : public CtiCCSubstationBusStore
{
    void setVoltageRegulatorManager(boost::shared_ptr<Cti::CapControl::VoltageRegulatorManager> manager)
    {
        _voltageRegulatorManager = manager;
    }

    void initialize()
    {
        TestAttributeService_ErrorCase    attributes;

        boost::shared_ptr<VoltageRegulatorManager> manager( new VoltageRegulatorManager( std::auto_ptr<VoltageRegulatorUnitTestLoader>( new VoltageRegulatorUnitTestLoader ) ) );
        manager->setAttributeService( & attributes );

        setVoltageRegulatorManager(manager);
        reloadVoltageRegulatorFromDatabase(-1);   // reload all
    }
};


BOOST_AUTO_TEST_CASE(test_scanVolatgeRegulatorIntegrity_Success)
{
    TestCapControlBusStore * theStore = new TestCapControlBusStore();
    CtiCCSubstationBusStore::setInstance( theStore );
    theStore->initialize();

    std::vector<CtiMessage*>        toDispatch;
    std::vector<CtiCCEventLogMsg*>  events;
    std::vector<CtiRequestMsg*>     requests;

    CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::VOLTAGE_REGULATOR_INTEGRITY_SCAN, 1);//Consumed in executor.
    CtiCCCommandExecutor commandExecutor(commandMsg);

    BOOST_CHECK_NO_THROW( commandExecutor.scanVoltageRegulatorIntegrity(commandMsg->getCommand(),toDispatch,events,requests) );

    BOOST_CHECK_EQUAL( 1, toDispatch.size() );
    BOOST_CHECK_EQUAL( 0, events.size()     );
    BOOST_CHECK_EQUAL( 1, requests.size()   );

    CtiCCSubstationBusStore::deleteInstance();
}


BOOST_AUTO_TEST_CASE(test_scanVolatgeRegulatorIntegrity_NoVoltageRegulator_Exception)
{
    TestCapControlBusStore * theStore = new TestCapControlBusStore();
    CtiCCSubstationBusStore::setInstance( theStore );
    theStore->initialize();

    std::vector<CtiMessage*>        toDispatch;
    std::vector<CtiCCEventLogMsg*>  events;
    std::vector<CtiRequestMsg*>     requests;

    CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::VOLTAGE_REGULATOR_INTEGRITY_SCAN, 2);//Consumed in executor.
    CtiCCCommandExecutor commandExecutor(commandMsg);

    BOOST_CHECK_THROW( commandExecutor.scanVoltageRegulatorIntegrity(commandMsg->getCommand(),toDispatch,events,requests),
                       NoVoltageRegulator );

    BOOST_CHECK_EQUAL( 0, toDispatch.size() );
    BOOST_CHECK_EQUAL( 0, events.size()     );
    BOOST_CHECK_EQUAL( 0, requests.size()   );

    CtiCCSubstationBusStore::deleteInstance();
}


BOOST_AUTO_TEST_CASE(test_scanVolatgeRegulatorIntegrity_MissingPointAttribute_Exception)
{
    TestCapControlBusStore_ErrorCase * theStore = new TestCapControlBusStore_ErrorCase();
    CtiCCSubstationBusStore::setInstance( theStore );
    theStore->initialize();

    std::vector<CtiMessage*>        toDispatch;
    std::vector<CtiCCEventLogMsg*>  events;
    std::vector<CtiRequestMsg*>     requests;

    CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::VOLTAGE_REGULATOR_INTEGRITY_SCAN, 1);//Consumed in executor.
    CtiCCCommandExecutor commandExecutor(commandMsg);

    BOOST_CHECK_THROW( commandExecutor.scanVoltageRegulatorIntegrity(commandMsg->getCommand(),toDispatch,events,requests),
                       MissingPointAttribute );

    BOOST_CHECK_EQUAL( 0, toDispatch.size() );
    BOOST_CHECK_EQUAL( 0, events.size()     );
    BOOST_CHECK_EQUAL( 0, requests.size()   );

    CtiCCSubstationBusStore::deleteInstance();
}


BOOST_AUTO_TEST_CASE(test_sendVolatgeRegulatorRemoteControl_Success)
{
    TestCapControlBusStore * theStore = new TestCapControlBusStore();
    CtiCCSubstationBusStore::setInstance( theStore );
    theStore->initialize();

    std::vector<CtiMessage*>        toDispatch;
    std::vector<CtiCCEventLogMsg*>  events;
    std::vector<CtiRequestMsg*>     requests;

    CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::VOLTAGE_REGULATOR_REMOTE_CONTROL_ENABLE, 1);//Consumed in executor.
    CtiCCCommandExecutor commandExecutor(commandMsg);

    BOOST_CHECK_NO_THROW( commandExecutor.sendVoltageRegulatorRemoteControl(commandMsg->getCommand(),toDispatch,events,requests) );

    BOOST_CHECK_EQUAL( 2, toDispatch.size() );
    BOOST_CHECK_EQUAL( 0, events.size()     );
    BOOST_CHECK_EQUAL( 1, requests.size()   );

    CtiCCSubstationBusStore::deleteInstance();
}


BOOST_AUTO_TEST_CASE(test_sendVolatgeRegulatorRemoteControl_NoVoltageRegulator_Exception)
{
    TestCapControlBusStore * theStore = new TestCapControlBusStore();
    CtiCCSubstationBusStore::setInstance( theStore );
    theStore->initialize();

    std::vector<CtiMessage*>        toDispatch;
    std::vector<CtiCCEventLogMsg*>  events;
    std::vector<CtiRequestMsg*>     requests;

    CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::VOLTAGE_REGULATOR_REMOTE_CONTROL_ENABLE, 2);//Consumed in executor.
    CtiCCCommandExecutor commandExecutor(commandMsg);

    BOOST_CHECK_THROW( commandExecutor.sendVoltageRegulatorRemoteControl(commandMsg->getCommand(),toDispatch,events,requests),
                       NoVoltageRegulator );

    BOOST_CHECK_EQUAL( 0, toDispatch.size() );
    BOOST_CHECK_EQUAL( 0, events.size()     );
    BOOST_CHECK_EQUAL( 0, requests.size()   );

    CtiCCSubstationBusStore::deleteInstance();
}


BOOST_AUTO_TEST_CASE(test_sendVolatgeRegulatorRemoteControl_MissingPointAttribute_Exception)
{
    TestCapControlBusStore_ErrorCase * theStore = new TestCapControlBusStore_ErrorCase();
    CtiCCSubstationBusStore::setInstance( theStore );
    theStore->initialize();

    std::vector<CtiMessage*>        toDispatch;
    std::vector<CtiCCEventLogMsg*>  events;
    std::vector<CtiRequestMsg*>     requests;

    CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::VOLTAGE_REGULATOR_REMOTE_CONTROL_ENABLE, 1);//Consumed in executor.
    CtiCCCommandExecutor commandExecutor(commandMsg);

    BOOST_CHECK_THROW( commandExecutor.sendVoltageRegulatorRemoteControl(commandMsg->getCommand(),toDispatch,events,requests),
                       MissingPointAttribute );

    BOOST_CHECK_EQUAL( 0, toDispatch.size() );
    BOOST_CHECK_EQUAL( 0, events.size()     );
    BOOST_CHECK_EQUAL( 0, requests.size()   );

    CtiCCSubstationBusStore::deleteInstance();
}


BOOST_AUTO_TEST_CASE(test_sendVolatgeRegulatorTapPosition_Success)
{
    TestCapControlBusStore * theStore = new TestCapControlBusStore();
    CtiCCSubstationBusStore::setInstance( theStore );
    theStore->initialize();

    std::vector<CtiMessage*>        toDispatch;
    std::vector<CtiCCEventLogMsg*>  events;
    std::vector<CtiRequestMsg*>     requests;

    CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::VOLTAGE_REGULATOR_TAP_POSITION_RAISE, 1);//Consumed in executor.
    CtiCCCommandExecutor commandExecutor(commandMsg);

    BOOST_CHECK_NO_THROW( commandExecutor.sendVoltageRegulatorTapPosition(commandMsg->getCommand(),toDispatch,events,requests) );

    BOOST_CHECK_EQUAL( 1, toDispatch.size() );
    BOOST_CHECK_EQUAL( 1, events.size() );
    BOOST_CHECK_EQUAL( 1, requests.size()   );

    CtiCCSubstationBusStore::deleteInstance();
}


BOOST_AUTO_TEST_CASE(test_sendVolatgeRegulatorTapPosition_NoVoltageRegulator_Exception)
{
    TestCapControlBusStore * theStore = new TestCapControlBusStore();
    CtiCCSubstationBusStore::setInstance( theStore );
    theStore->initialize();

    std::vector<CtiMessage*>        toDispatch;
    std::vector<CtiCCEventLogMsg*>  events;
    std::vector<CtiRequestMsg*>     requests;

    CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::VOLTAGE_REGULATOR_TAP_POSITION_RAISE, 2);//Consumed in executor.
    CtiCCCommandExecutor commandExecutor(commandMsg);

    BOOST_CHECK_THROW( commandExecutor.sendVoltageRegulatorTapPosition(commandMsg->getCommand(),toDispatch,events,requests),
                       NoVoltageRegulator );

    BOOST_CHECK_EQUAL( 0, toDispatch.size() );
    BOOST_CHECK_EQUAL( 0, events.size() );
    BOOST_CHECK_EQUAL( 0, requests.size()   );

    CtiCCSubstationBusStore::deleteInstance();
}


BOOST_AUTO_TEST_CASE(test_sendVolatgeRegulatorTapPosition_MissingPointAttribute_Exception)
{
    TestCapControlBusStore_ErrorCase * theStore = new TestCapControlBusStore_ErrorCase();
    CtiCCSubstationBusStore::setInstance( theStore );
    theStore->initialize();

    std::vector<CtiMessage*>        toDispatch;
    std::vector<CtiCCEventLogMsg*>  events;
    std::vector<CtiRequestMsg*>     requests;

    CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::VOLTAGE_REGULATOR_TAP_POSITION_RAISE, 1);//Consumed in executor.
    CtiCCCommandExecutor commandExecutor(commandMsg);

    BOOST_CHECK_THROW( commandExecutor.sendVoltageRegulatorTapPosition(commandMsg->getCommand(),toDispatch,events,requests),
                       MissingPointAttribute );

    BOOST_CHECK_EQUAL( 0, toDispatch.size() );
    BOOST_CHECK_EQUAL( 0, events.size() );
    BOOST_CHECK_EQUAL( 0, requests.size()   );

    CtiCCSubstationBusStore::deleteInstance();
}


BOOST_AUTO_TEST_CASE(test_sendVolatgeRegulatorKeepAlive_Success)
{
    TestCapControlBusStore * theStore = new TestCapControlBusStore();
    CtiCCSubstationBusStore::setInstance( theStore );
    theStore->initialize();

    std::vector<CtiMessage*>        toDispatch;
    std::vector<CtiRequestMsg*>     requests;

    CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::VOLTAGE_REGULATOR_KEEP_ALIVE, 1);//Consumed in executor.
    CtiCCCommandExecutor commandExecutor(commandMsg);

    BOOST_CHECK_NO_THROW( commandExecutor.sendVoltageRegulatorKeepAlive(commandMsg->getCommand(),toDispatch,requests) );

    BOOST_CHECK_EQUAL( 1, toDispatch.size() );
    BOOST_CHECK_EQUAL( 1, requests.size()   );

    CtiCCSubstationBusStore::deleteInstance();
}


BOOST_AUTO_TEST_CASE(test_sendVolatgeRegulatorKeepAlive_NoVoltageRegulator_Exception)
{
    TestCapControlBusStore * theStore = new TestCapControlBusStore();
    CtiCCSubstationBusStore::setInstance( theStore );
    theStore->initialize();

    std::vector<CtiMessage*>        toDispatch;
    std::vector<CtiRequestMsg*>     requests;

    CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::VOLTAGE_REGULATOR_KEEP_ALIVE, 2);//Consumed in executor.
    CtiCCCommandExecutor commandExecutor(commandMsg);

    BOOST_CHECK_THROW( commandExecutor.sendVoltageRegulatorKeepAlive(commandMsg->getCommand(),toDispatch,requests),
                       NoVoltageRegulator );

    BOOST_CHECK_EQUAL( 0, toDispatch.size() );
    BOOST_CHECK_EQUAL( 0, requests.size()   );

    CtiCCSubstationBusStore::deleteInstance();
}


BOOST_AUTO_TEST_CASE(test_sendVolatgeRegulatorKeepAlive_MissingPointAttribute_Exception)
{
    TestCapControlBusStore_ErrorCase * theStore = new TestCapControlBusStore_ErrorCase();
    CtiCCSubstationBusStore::setInstance( theStore );
    theStore->initialize();

    std::vector<CtiMessage*>        toDispatch;
    std::vector<CtiRequestMsg*>     requests;

    CtiCCCommand* commandMsg = new CtiCCCommand(CtiCCCommand::VOLTAGE_REGULATOR_KEEP_ALIVE, 1);//Consumed in executor.
    CtiCCCommandExecutor commandExecutor(commandMsg);

    BOOST_CHECK_THROW( commandExecutor.sendVoltageRegulatorKeepAlive(commandMsg->getCommand(),toDispatch,requests),
                       MissingPointAttribute );

    BOOST_CHECK_EQUAL( 0, toDispatch.size() );
    BOOST_CHECK_EQUAL( 0, requests.size()   );

    CtiCCSubstationBusStore::deleteInstance();
}

