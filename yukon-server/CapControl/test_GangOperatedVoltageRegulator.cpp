#define BOOST_AUTO_TEST_MAIN "Test CapControl Gang Operated Voltage Regulators"

#include <boost/test/unit_test.hpp>
#include <vector>

#include "yukon.h"
#include "capcontroller.h"
#include "ccmessage.h"
#include "PointAttribute.h"
#include "AttributeService.h"
#include "VoltageRegulatorManager.h"
#include "GangOperatedVoltageRegulator.h"

// Objects
using Cti::CapControl::VoltageRegulatorManager;
using Cti::CapControl::GangOperatedVoltageRegulator;

// Exceptions
using Cti::CapControl::MissingPointAttribute;


struct TestCtiCapController : public CtiCapController
{
    TestCtiCapController()
    {
        CtiCapController::setInstance(this);
    }

    ~TestCtiCapController()
    {
        for each ( const CtiMessage * p in signalMessages )
        {
            delete p;
        }
        for each ( CtiRequestMsg *p in requestMessages )
        {
            delete p;
        }
        for each ( CtiMessage *p in eventMessages )
        {
            delete p;
        }
    }

    virtual void sendMessageToDispatch(CtiMessage* message)
    {
        signalMessages.push_back(message);
    }
    virtual void manualCapBankControl(CtiRequestMsg* pilRequest, CtiMultiMsg* multiMsg = NULL)
    {
        requestMessages.push_back(pilRequest);
    }
    virtual void sendEventLogMessage(CtiMessage* message)
    {
        eventMessages.push_back(message);
    }

    std::vector<CtiMessage*>    signalMessages;
    std::vector<CtiRequestMsg*> requestMessages;
    std::vector<CtiMessage*>    eventMessages;
};


struct TestAttributeService : public AttributeService
{
    virtual std::list<LitePoint> getExtraPaoPoints(int paoId)
    {
        return std::list<LitePoint>();
    }
    virtual LitePoint getPointByPaoAndAttribute(int paoId, const PointAttribute& attribute)
    {
        switch ( attribute.value() )
        {
            case PointAttribute::VoltageYAttribute:
            {
                return LitePoint( 2203,  AnalogPointType, "VoltageY", 1001, 2 );
            }
            case PointAttribute::TapUpAttribute:
            {
                return LitePoint( 3100,  StatusPointType, "TapUp", 1003, 4 );
            }
            case PointAttribute::TapDownAttribute:
            {
                return LitePoint( 3101,  StatusPointType, "TapDown", 1004, 5 );
            }
            case PointAttribute::KeepAliveAttribute:
            {
                return LitePoint( 4200,  AnalogPointType, "KeepAlive", 1007, 1 );
            }
            case PointAttribute::AutoRemoteControlAttribute:
            {
                return LitePoint( 5600,  StatusPointType, "AutoRemoteControl", 1009, 6 );
            }
            case PointAttribute::TapPositionAttribute:
            {
                return LitePoint( 3500,  AnalogPointType, "TapPosition", 1013, 3 );
            }
            case PointAttribute::UnknownAttribute:
            default:
            {
            }
        }

        return LitePoint();
    }
};


/******* Tests ***********/


BOOST_AUTO_TEST_CASE(test_GangOperatedVolatgeRegulator_IntegrityScan_Fail)
{
    TestCtiCapController    capController;

    VoltageRegulatorManager::SharedPtr  regulator( new GangOperatedVoltageRegulator );
    

    BOOST_CHECK_THROW( regulator->executeIntegrityScan(), MissingPointAttribute );
    

    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_GangOperatedVolatgeRegulator_IntegrityScan_Success)
{
    TestCtiCapController    capController;
    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new GangOperatedVoltageRegulator );

    regulator->setPaoName("Test Regulator #1");
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_NO_THROW( regulator->executeIntegrityScan() );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );
    
    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 2203, signalMsg->getId() );     // ID of the 'VoltageY' LitePoint
    BOOST_CHECK_EQUAL( "Integrity Scan", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );
    
    CtiRequestMsg * requestMsg = capController.requestMessages.front();
    
    BOOST_REQUIRE( requestMsg );
    
    BOOST_CHECK_EQUAL( 1001, requestMsg->DeviceId() );  // PaoID of the 'VoltageY' LitePoint
    BOOST_CHECK_EQUAL( "scan integrity", requestMsg->CommandString() );
    
    
    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_GangOperatedVolatgeRegulator_TapUp_Fail)
{
    TestCtiCapController    capController;

    VoltageRegulatorManager::SharedPtr  regulator( new GangOperatedVoltageRegulator );
    

    BOOST_CHECK_THROW( regulator->executeTapUpOperation(), MissingPointAttribute );
    

    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_GangOperatedVolatgeRegulator_TapUp_Success)
{
    TestCtiCapController    capController;
    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new GangOperatedVoltageRegulator );

    regulator->setPaoName("Test Regulator #1");
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_NO_THROW( regulator->executeTapUpOperation() );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );
    
    BOOST_REQUIRE( signalMsg );
    
    BOOST_CHECK_EQUAL( 3100, signalMsg->getId() );     // ID of the 'TapUp' LitePoint
    BOOST_CHECK_EQUAL( "Raise Tap Position", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1003, requestMsg->DeviceId() );  // PaoID of the 'TapUp' LitePoint
    BOOST_CHECK_EQUAL( "control close select pointid 3100",
                       requestMsg->CommandString() );   // ID of the 'TapUp' LitePoint


    BOOST_REQUIRE_EQUAL( 1, capController.eventMessages.size() );

    CtiCCEventLogMsg * eventMsg = dynamic_cast<CtiCCEventLogMsg *>( capController.eventMessages.front() );

    BOOST_REQUIRE( eventMsg );

    BOOST_CHECK_EQUAL( 19, eventMsg->getEventType() );      // 19 is a 'Tap' event ID
    BOOST_CHECK_EQUAL( "Raise Tap Position", eventMsg->getText() );
}


BOOST_AUTO_TEST_CASE(test_GangOperatedVolatgeRegulator_TapDown_Fail)
{
    TestCtiCapController    capController;

    VoltageRegulatorManager::SharedPtr  regulator( new GangOperatedVoltageRegulator );
    

    BOOST_CHECK_THROW( regulator->executeTapDownOperation(), MissingPointAttribute );
    

    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_GangOperatedVolatgeRegulator_TapDown_Success)
{
    TestCtiCapController    capController;
    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new GangOperatedVoltageRegulator );

    regulator->setPaoName("Test Regulator #1");
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_NO_THROW( regulator->executeTapDownOperation() );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );
    
    BOOST_REQUIRE( signalMsg );
    
    BOOST_CHECK_EQUAL( 3101, signalMsg->getId() );     // ID of the 'TapDown' LitePoint
    BOOST_CHECK_EQUAL( "Lower Tap Position", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1004, requestMsg->DeviceId() );  // PaoID of the 'TapDown' LitePoint
    BOOST_CHECK_EQUAL( "control close select pointid 3101",
                       requestMsg->CommandString() );   // ID of the 'TapDown' LitePoint


    BOOST_REQUIRE_EQUAL( 1, capController.eventMessages.size() );

    CtiCCEventLogMsg * eventMsg = dynamic_cast<CtiCCEventLogMsg *>( capController.eventMessages.front() );

    BOOST_REQUIRE( eventMsg );

    BOOST_CHECK_EQUAL( 19, eventMsg->getEventType() );      // 19 is a 'Tap' event ID
    BOOST_CHECK_EQUAL( "Lower Tap Position", eventMsg->getText() );
}


BOOST_AUTO_TEST_CASE(test_GangOperatedVolatgeRegulator_EnableKeepAlive_Fail)
{
    TestCtiCapController    capController;

    VoltageRegulatorManager::SharedPtr  regulator( new GangOperatedVoltageRegulator );
    

    BOOST_CHECK_THROW( regulator->executeEnableKeepAlive(), MissingPointAttribute );
    

    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_GangOperatedVolatgeRegulator_EnableKeepAlive_Success)
{
    TestCtiCapController    capController;
    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new GangOperatedVoltageRegulator );

    regulator->setPaoName("Test Regulator #1");
    regulator->loadAttributes( &attributes );
    regulator->setKeepAliveConfig(123);    // this is the value we'll look for in the resulting request message


    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive() );
    

    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 1, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 123",
                       requestMsg->CommandString() );       // 'putvalue analog <offset> <value>'


    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_GangOperatedVolatgeRegulator_DisableKeepAlive_Fail)
{
    TestCtiCapController    capController;

    VoltageRegulatorManager::SharedPtr  regulator( new GangOperatedVoltageRegulator );
    

    BOOST_CHECK_THROW( regulator->executeDisableKeepAlive(), MissingPointAttribute );
    

    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_GangOperatedVolatgeRegulator_DisableKeepAlive_Success)
{
    TestCtiCapController    capController;
    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new GangOperatedVoltageRegulator );

    regulator->setPaoName("Test Regulator #1");
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_NO_THROW( regulator->executeDisableKeepAlive() );
    

    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 1, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 0",
                       requestMsg->CommandString() );       // 'putvalue analog <offset> <value>'


    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_GangOperatedVolatgeRegulator_EnableRemoteControl_Fail)
{
    TestCtiCapController    capController;

    VoltageRegulatorManager::SharedPtr  regulator( new GangOperatedVoltageRegulator );
    

    BOOST_CHECK_THROW( regulator->executeEnableRemoteControl(), MissingPointAttribute );
    

    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_GangOperatedVolatgeRegulator_EnableRemoteControl_Success)
{
    TestCtiCapController    capController;
    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new GangOperatedVoltageRegulator );

    regulator->setPaoName("Test Regulator #1");
    regulator->loadAttributes( &attributes );
    regulator->setKeepAliveConfig(123);    // this is the value we'll look for in the resulting request message


    BOOST_CHECK_NO_THROW( regulator->executeEnableRemoteControl() );


    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Enable Remote Control", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        
    BOOST_CHECK_EQUAL( 123, signalMsg->getPointValue() );   // 'KeepAlive' reload value

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.back() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 1, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 123",
                       requestMsg->CommandString() );       // 'putvalue analog <offset> <value>'


    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_GangOperatedVolatgeRegulator_DisableRemoteControl_Fail)
{
    TestCtiCapController    capController;

    VoltageRegulatorManager::SharedPtr  regulator( new GangOperatedVoltageRegulator );
    

    BOOST_CHECK_THROW( regulator->executeDisableRemoteControl(), MissingPointAttribute );
    

    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_GangOperatedVolatgeRegulator_DisableRemoteControl_Success)
{
    TestCtiCapController    capController;
    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new GangOperatedVoltageRegulator );

    regulator->setPaoName("Test Regulator #1");
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_NO_THROW( regulator->executeDisableRemoteControl() );


    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Disable Remote Control", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        
    BOOST_CHECK_EQUAL( 0, signalMsg->getPointValue() );     // 'KeepAlive' reload value

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.back() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 1, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 0",
                       requestMsg->CommandString() );       // 'putvalue analog <offset> <value>'


    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}

