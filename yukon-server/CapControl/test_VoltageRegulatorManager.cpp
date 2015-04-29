#include <boost/test/unit_test.hpp>

#include "GangOperatedVoltageRegulator.h"
#include "ccsubstationbusstore.h"

// Objects
using Cti::CapControl::VoltageRegulator;
using Cti::CapControl::VoltageRegulatorLoader;
using Cti::CapControl::VoltageRegulatorManager;
using Cti::CapControl::GangOperatedVoltageRegulator;

// Exceptions
using Cti::CapControl::NoVoltageRegulator;
using Cti::CapControl::MissingPointAttribute;

BOOST_AUTO_TEST_SUITE( test_VoltageRegulatorManager )

class VoltageRegulatorUnitTestLoader : public VoltageRegulatorLoader
{
public:

    virtual VoltageRegulatorManager::VoltageRegulatorMap load(const long Id)
    {
        VoltageRegulatorManager::VoltageRegulatorMap    regulators;

        regulators[25] = VoltageRegulatorManager::SharedPtr(new GangOperatedVoltageRegulator);
        regulators[25]->setPaoId(25);
        regulators[25]->setPaoType(VoltageRegulator::LoadTapChanger);

        regulators[30] = VoltageRegulatorManager::SharedPtr(new GangOperatedVoltageRegulator);
        regulators[30]->setPaoId(30);
        regulators[30]->setPaoType(VoltageRegulator::LoadTapChanger);

        return regulators;
    }
};


class TestAttributeService : public AttributeService
{
public:

    virtual LitePoint getPointByPaoAndAttribute(int paoId, const PointAttribute& attribute)
    {
        AttributeStore::const_iterator iter = _store.find( std::make_pair(paoId, attribute) );

        return iter != _store.end()
                    ? iter->second
                    : LitePoint();
    }

private:

    typedef std::map<std::pair<int, PointAttribute>, LitePoint>     AttributeStore;

    static const AttributeStore _store;

    static AttributeStore initStore()
    {
        AttributeStore  store;

        store.insert( std::make_pair( std::make_pair( 25, PointAttribute::TapUp ),
                                      LitePoint(242, StatusPointType, "Tap Up", 0, 100, "", "", 1.0, 0 ) ) );

        store.insert( std::make_pair( std::make_pair( 25, PointAttribute::TapDown ),
                                      LitePoint(342, StatusPointType, "Tap Down", 0, 101, "", "", 1.0, 0 ) ) );

        store.insert( std::make_pair( std::make_pair( 25, PointAttribute::VoltageY ),
                                      LitePoint(442, AnalogPointType, "Voltage", 0, 102, "", "", 1.0, 0 ) ) );

        store.insert( std::make_pair( std::make_pair( 25, PointAttribute::AutoRemoteControl ),
                                      LitePoint(542, StatusPointType, "Auto Remote", 0, 103, "", "", 1.0, 0 ) ) );

        store.insert( std::make_pair( std::make_pair( 25, PointAttribute::TapPosition ),
                                      LitePoint(642, AnalogPointType, "Tap Position", 0, 104, "", "", 1.0, 0 ) ) );

        store.insert( std::make_pair( std::make_pair( 30, PointAttribute::TapUp ),
                                      LitePoint(245, StatusPointType, "Tap Up", 0, 100, "", "", 1.0, 0 ) ) );

        store.insert( std::make_pair( std::make_pair( 30, PointAttribute::TapDown ),
                                      LitePoint(345, StatusPointType, "Tap Down", 0, 101, "", "", 1.0, 0 ) ) );

        store.insert( std::make_pair( std::make_pair( 30, PointAttribute::VoltageY ),
                                      LitePoint(445, AnalogPointType, "Voltage", 0, 102, "", "", 1.0, 0 ) ) );

        store.insert( std::make_pair( std::make_pair( 30, PointAttribute::AutoRemoteControl ),
                                      LitePoint(545, StatusPointType, "Auto Remote", 0, 103, "", "", 1.0, 0 ) ) );

        return store;
    }
};

const TestAttributeService::AttributeStore TestAttributeService::_store = initStore();


struct TestCapControlBusStore : public CtiCCSubstationBusStore
{
    void setVoltageRegulatorManager(std::unique_ptr<Cti::CapControl::VoltageRegulatorManager> manager)
    {
        _voltageRegulatorManager = std::move(manager);
    }

    void initialize()
    {
        TestAttributeService    attributes;

        std::unique_ptr<VoltageRegulatorManager> manager = std::make_unique<VoltageRegulatorManager>( std::make_unique<VoltageRegulatorUnitTestLoader>() );
        manager->setAttributeService( & attributes );

        setVoltageRegulatorManager(std::move(manager));
        reloadVoltageRegulatorFromDatabase(-1);   // reload all
    }
};


BOOST_AUTO_TEST_CASE(test_VoltageRegulatorManager_test_getter_exceptions)
{
    TestCapControlBusStore * theStore = new TestCapControlBusStore();
    CtiCCSubstationBusStore::setInstance( theStore );
    theStore->initialize();

    VoltageRegulatorManager *manager = theStore->getVoltageRegulatorManager();

    BOOST_CHECK_THROW( manager->getVoltageRegulator(100), NoVoltageRegulator );

    BOOST_CHECK_NO_THROW( manager->getVoltageRegulator(25) );

    CtiCCSubstationBusStore::deleteInstance();
}


BOOST_AUTO_TEST_CASE(test_VoltageRegulatorManager_LoadTapChanger_Loads_OK)
{
    TestCapControlBusStore * theStore = new TestCapControlBusStore();
    CtiCCSubstationBusStore::setInstance( theStore );
    theStore->initialize();

    VoltageRegulatorManager *manager = theStore->getVoltageRegulatorManager();

    VoltageRegulatorManager::SharedPtr regulator = manager->getVoltageRegulator(25);

    VoltageRegulator::IDSet registeredPoints = regulator->getRegistrationPoints();

    BOOST_CHECK_EQUAL(   5, registeredPoints.size() );

    // unavailable attribute
    BOOST_CHECK_THROW( regulator->getPointByAttribute(PointAttribute::KeepAlive) , MissingPointAttribute );

    // available attributes
    BOOST_CHECK_EQUAL( 242, regulator->getPointByAttribute(PointAttribute::TapUp).getPointId() );
    BOOST_CHECK_EQUAL( 342, regulator->getPointByAttribute(PointAttribute::TapDown).getPointId() );
    BOOST_CHECK_EQUAL( 442, regulator->getPointByAttribute(PointAttribute::VoltageY).getPointId() );
    BOOST_CHECK_EQUAL( 542, regulator->getPointByAttribute(PointAttribute::AutoRemoteControl).getPointId() );
    BOOST_CHECK_EQUAL( 642, regulator->getPointByAttribute(PointAttribute::TapPosition).getPointId() );

    // test updated flag
    BOOST_CHECK_EQUAL(  true, regulator->isUpdated() );

    regulator->setUpdated(false);
    BOOST_CHECK_EQUAL( false, regulator->isUpdated() );

    BOOST_CHECK_EQUAL( VoltageRegulator::LoadTapChangerType, regulator->getType() );

    CtiCCSubstationBusStore::deleteInstance();
}


BOOST_AUTO_TEST_CASE(test_VoltageRegulatorManager_LoadTapChanger_Loads_with_missing_attribute)
{
    TestCapControlBusStore * theStore = new TestCapControlBusStore();
    CtiCCSubstationBusStore::setInstance( theStore );
    theStore->initialize();

    VoltageRegulatorManager *manager = theStore->getVoltageRegulatorManager();

    VoltageRegulatorManager::SharedPtr regulator = manager->getVoltageRegulator(30);

    VoltageRegulator::IDSet registeredPoints = regulator->getRegistrationPoints();

    BOOST_CHECK_EQUAL(   4, registeredPoints.size() );

    // unavailable attribute
    BOOST_CHECK_THROW( regulator->getPointByAttribute(PointAttribute::KeepAlive) , MissingPointAttribute );

    // missing attribute
    BOOST_CHECK_THROW( regulator->getPointByAttribute(PointAttribute::TapPosition) , MissingPointAttribute );

    // available attributes
    BOOST_CHECK_EQUAL( 245, regulator->getPointByAttribute(PointAttribute::TapUp).getPointId() );
    BOOST_CHECK_EQUAL( 345, regulator->getPointByAttribute(PointAttribute::TapDown).getPointId() );
    BOOST_CHECK_EQUAL( 445, regulator->getPointByAttribute(PointAttribute::VoltageY).getPointId() );
    BOOST_CHECK_EQUAL( 545, regulator->getPointByAttribute(PointAttribute::AutoRemoteControl).getPointId() );

    BOOST_CHECK_EQUAL( VoltageRegulator::LoadTapChangerType, regulator->getType() );

    CtiCCSubstationBusStore::deleteInstance();
}

BOOST_AUTO_TEST_SUITE_END()
