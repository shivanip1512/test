#define BOOST_AUTO_TEST_MAIN "Test CapControl Phase Operated Voltage Regulators"

#include <boost/test/unit_test.hpp>
#include <vector>

#include "yukon.h"
#include "capcontroller.h"
#include "ccmessage.h"
#include "PointAttribute.h"
#include "AttributeService.h"
#include "VoltageRegulatorManager.h"
#include "PhaseOperatedVoltageRegulator.h"
#include "ccutil.h"

// Objects
using Cti::CapControl::VoltageRegulatorManager;
using Cti::CapControl::PhaseOperatedVoltageRegulator;

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
            case PointAttribute::VoltageXAttribute:
            {
                return LitePoint( 2202,  AnalogPointType, "VoltageX", 1000, 1 );
            }
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
                return LitePoint( 4200,  AnalogPointType, "KeepAlive", 1007, 10001 );
            }
            case PointAttribute::AutoRemoteControlAttribute:
            {
                return LitePoint( 5600,  StatusPointType, "AutoRemoteControl", 1009, 6 );
            }
            case PointAttribute::TapPositionAttribute:
            {
                return LitePoint( 3500,  AnalogPointType, "TapPosition", 1013, 3 );
            }
            case PointAttribute::TerminateAttribute:
            {
                return LitePoint( 7500,  StatusPointType, "Terminate", 1022, 9 );
            }
            case PointAttribute::AutoBlockEnableAttribute:
            {
                return LitePoint( 8100,  StatusPointType, "AutoBlock", 1026, 12 );
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


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_IntegrityScan_Fail)
{
    TestCtiCapController    capController;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );
    
    BOOST_CHECK_THROW( regulator->executeIntegrityScan(), MissingPointAttribute );
    
    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_IntegrityScan_Success)
{
    TestCtiCapController    capController;
    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );

    regulator->setPaoName("Test Regulator #1");
    regulator->loadAttributes( &attributes );

    BOOST_CHECK_NO_THROW( regulator->executeIntegrityScan() );


    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );
    
    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 2202, signalMsg->getId() );     // ID of the 'VoltageX' LitePoint
    BOOST_CHECK_EQUAL( "Integrity Scan", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.back() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 2203, signalMsg->getId() );     // ID of the 'VoltageY' LitePoint
    BOOST_CHECK_EQUAL( "Integrity Scan", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        


    BOOST_REQUIRE_EQUAL( 2, capController.requestMessages.size() );
    
    CtiRequestMsg * requestMsg = capController.requestMessages.front();
    
    BOOST_REQUIRE( requestMsg );
    
    BOOST_CHECK_EQUAL( 1000, requestMsg->DeviceId() );  // PaoID of the 'VoltageX' LitePoint
    BOOST_CHECK_EQUAL( "scan integrity", requestMsg->CommandString() );
    
    requestMsg = capController.requestMessages.back();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1001, requestMsg->DeviceId() );  // PaoID of the 'VoltageY' LitePoint
    BOOST_CHECK_EQUAL( "scan integrity", requestMsg->CommandString() );


    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_TapUp_Fail)
{
    TestCtiCapController    capController;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );
    
    BOOST_CHECK_THROW( regulator->executeTapUpOperation(), MissingPointAttribute );
    
    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_TapUp_Success)
{
    TestCtiCapController    capController;
    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );

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

    BOOST_CHECK_EQUAL( capControlIvvcTapOperation, eventMsg->getEventType() );
    BOOST_CHECK_EQUAL( "Raise Tap Position", eventMsg->getText() );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_TapDown_Fail)
{
    TestCtiCapController    capController;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );
    
    BOOST_CHECK_THROW( regulator->executeTapDownOperation(), MissingPointAttribute );
    
    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_TapDown_Success)
{
    TestCtiCapController    capController;
    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );

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

    BOOST_CHECK_EQUAL( capControlIvvcTapOperation, eventMsg->getEventType() );
    BOOST_CHECK_EQUAL( "Lower Tap Position", eventMsg->getText() );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_EnableKeepAlive_Fail)
{
    TestCtiCapController    capController;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );
    
    BOOST_CHECK_THROW( regulator->executeEnableKeepAlive(), MissingPointAttribute );
    
    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_EnableKeepAliveFromRemoteMode_Success)
{
    using Cti::CapControl::VoltageRegulator;

    TestCtiCapController    capController;
    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );

    regulator->setPaoName("Test Regulator #1");
    regulator->loadAttributes( &attributes );

    // put regulator into remote mode

    CtiPointDataMsg remoteMode( 5600, 1.0, NormalQuality, StatusPointType );

    regulator->handlePointData( &remoteMode );

    BOOST_CHECK_EQUAL( VoltageRegulator::RemoteMode, regulator->getOperatingMode() );

    // set our keep alive timer

    CtiPointDataMsg keepAlive( 4200, 100.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( &keepAlive );


    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive() );
    

    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 101",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'


    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_EnableKeepAliveFromRemoteMode_Success_with_Rollover)
{
    using Cti::CapControl::VoltageRegulator;

    TestCtiCapController    capController;
    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );

    regulator->setPaoName("Test Regulator #1");
    regulator->loadAttributes( &attributes );

    // put regulator into remote mode

    CtiPointDataMsg remoteMode( 5600, 1.0, NormalQuality, StatusPointType );

    regulator->handlePointData( &remoteMode );

    BOOST_CHECK_EQUAL( VoltageRegulator::RemoteMode, regulator->getOperatingMode() );

    // set our keep alive timer

    CtiPointDataMsg keepAlive( 4200, 32767.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( &keepAlive );


    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive() );
    

    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 0",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'


    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_EnableKeepAliveFromAutoMode_Success)
{
    using Cti::CapControl::VoltageRegulator;

    TestCtiCapController    capController;
    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );

    regulator->setPaoName("Test Regulator #1");
    regulator->loadAttributes( &attributes );

    // put regulator into auto mode

    CtiPointDataMsg autoMode( 5600, 0.0, NormalQuality, StatusPointType );

    regulator->handlePointData( &autoMode );

    BOOST_CHECK_EQUAL( VoltageRegulator::LocalMode, regulator->getOperatingMode() );

    // set our keep alive timer

    CtiPointDataMsg keepAlive( 4200, 100.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( &keepAlive );


    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive() );
    

    BOOST_REQUIRE_EQUAL( 3, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[0] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[1] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[2] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 8100, signalMsg->getId() );             // PaoID of the 'AutoBlock' LitePoint
    BOOST_CHECK_EQUAL( "Auto Block Enable", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        


    BOOST_REQUIRE_EQUAL( 3, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages[0];

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 101",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'

    requestMsg = capController.requestMessages[1];

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 102",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'

    requestMsg = capController.requestMessages[2];

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1026, requestMsg->DeviceId() );      // PaoID of the 'AutoBlock' LitePoint
    BOOST_CHECK_EQUAL( "control close select pointid 8100",
                       requestMsg->CommandString() );       // ID of the 'AutoBlock' LitePoint


    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_EnableKeepAliveFromAutoMode_Success_with_Rollover)
{
    using Cti::CapControl::VoltageRegulator;

    TestCtiCapController    capController;
    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );

    regulator->setPaoName("Test Regulator #1");
    regulator->loadAttributes( &attributes );

    // put regulator into auto mode

    CtiPointDataMsg autoMode( 5600, 0.0, NormalQuality, StatusPointType );

    regulator->handlePointData( &autoMode );

    BOOST_CHECK_EQUAL( VoltageRegulator::LocalMode, regulator->getOperatingMode() );

    // set our keep alive timer

    CtiPointDataMsg keepAlive( 4200, 32766.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( &keepAlive );


    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive() );
    

    BOOST_REQUIRE_EQUAL( 3, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[0] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[1] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[2] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 8100, signalMsg->getId() );             // PaoID of the 'AutoBlock' LitePoint
    BOOST_CHECK_EQUAL( "Auto Block Enable", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        


    BOOST_REQUIRE_EQUAL( 3, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages[0];

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 32767",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'

    requestMsg = capController.requestMessages[1];

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 0",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'

    requestMsg = capController.requestMessages[2];

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1026, requestMsg->DeviceId() );      // PaoID of the 'AutoBlock' LitePoint
    BOOST_CHECK_EQUAL( "control close select pointid 8100",
                       requestMsg->CommandString() );       // ID of the 'AutoBlock' LitePoint


    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_DisableKeepAlive_Fail)
{
    TestCtiCapController    capController;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );
    
    BOOST_CHECK_THROW( regulator->executeDisableKeepAlive(), MissingPointAttribute );
    
    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_DisableKeepAlive_Success)
{
    TestCtiCapController    capController;
    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );

    regulator->setPaoName("Test Regulator #1");
    regulator->loadAttributes( &attributes );

    BOOST_CHECK_NO_THROW( regulator->executeDisableKeepAlive() );
    
        
    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 7500, signalMsg->getId() );     // ID of the 'Terminate' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1022, requestMsg->DeviceId() );  // PaoID of the 'Terminate' LitePoint
    BOOST_CHECK_EQUAL( "control close select pointid 7500",
                       requestMsg->CommandString() );   // ID of the 'Terminate' LitePoint


    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_EnableRemoteControl_Fail)
{
    TestCtiCapController    capController;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );
    
    BOOST_CHECK_THROW( regulator->executeEnableRemoteControl(), MissingPointAttribute );
    
    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_EnableRemoteControlFromRemoteMode_Success)
{
    using Cti::CapControl::VoltageRegulator;

    TestCtiCapController    capController;
    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );

    regulator->setPaoName("Test Regulator #1");
    regulator->loadAttributes( &attributes );

    // put regulator into remote mode

    CtiPointDataMsg remoteMode( 5600, 1.0, NormalQuality, StatusPointType );

    regulator->handlePointData( &remoteMode );

    BOOST_CHECK_EQUAL( VoltageRegulator::RemoteMode, regulator->getOperatingMode() );

    // set our keep alive timer

    CtiPointDataMsg keepAlive( 4200, 100.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( &keepAlive );


    BOOST_CHECK_NO_THROW( regulator->executeEnableRemoteControl() );
    

    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Enable Remote Control", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.back() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        

    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 101",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'


    BOOST_REQUIRE_EQUAL( 1, capController.eventMessages.size() );

    CtiCCEventLogMsg * eventMsg = dynamic_cast<CtiCCEventLogMsg *>( capController.eventMessages.front() );

    BOOST_REQUIRE( eventMsg );

    BOOST_CHECK_EQUAL( capControlIvvcRemoteControlEvent, eventMsg->getEventType() );
    BOOST_CHECK_EQUAL( "Enable Remote Control", eventMsg->getText() );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_EnableRemoteControlFromAutoMode_Success)
{
    using Cti::CapControl::VoltageRegulator;

    TestCtiCapController    capController;
    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );

    regulator->setPaoName("Test Regulator #1");
    regulator->loadAttributes( &attributes );

    // put regulator into auto mode

    CtiPointDataMsg autoMode( 5600, 0.0, NormalQuality, StatusPointType );

    regulator->handlePointData( &autoMode );

    BOOST_CHECK_EQUAL( VoltageRegulator::LocalMode, regulator->getOperatingMode() );

    // set our keep alive timer

    CtiPointDataMsg keepAlive( 4200, 100.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( &keepAlive );


    BOOST_CHECK_NO_THROW( regulator->executeEnableRemoteControl() );
    

    BOOST_REQUIRE_EQUAL( 4, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[0] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Enable Remote Control", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[1] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[2] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[3] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 8100, signalMsg->getId() );             // PaoID of the 'AutoBlock' LitePoint
    BOOST_CHECK_EQUAL( "Auto Block Enable", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        


    BOOST_REQUIRE_EQUAL( 3, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages[0];

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 101",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'

    requestMsg = capController.requestMessages[1];

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 102",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'

    requestMsg = capController.requestMessages[2];

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1026, requestMsg->DeviceId() );      // PaoID of the 'AutoBlock' LitePoint
    BOOST_CHECK_EQUAL( "control close select pointid 8100",
                       requestMsg->CommandString() );       // ID of the 'AutoBlock' LitePoint


    BOOST_REQUIRE_EQUAL( 1, capController.eventMessages.size() );

    CtiCCEventLogMsg * eventMsg = dynamic_cast<CtiCCEventLogMsg *>( capController.eventMessages.front() );

    BOOST_REQUIRE( eventMsg );

    BOOST_CHECK_EQUAL( capControlIvvcRemoteControlEvent, eventMsg->getEventType() );
    BOOST_CHECK_EQUAL( "Enable Remote Control", eventMsg->getText() );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_DisableRemoteControl_Fail)
{
    TestCtiCapController    capController;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );
    
    BOOST_CHECK_THROW( regulator->executeDisableRemoteControl(), MissingPointAttribute );
    
    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.eventMessages.size() );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_DisableRemoteControl_Success)
{
    TestCtiCapController    capController;
    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );

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

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.back() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 7500, signalMsg->getId() );          // ID of the 'Terminate' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );        


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1022, requestMsg->DeviceId() );      // PaoID of the 'Terminate' LitePoint
    BOOST_CHECK_EQUAL( "control close select pointid 7500",
                       requestMsg->CommandString() );       // ID of the 'Terminate' LitePoint


    BOOST_REQUIRE_EQUAL( 1, capController.eventMessages.size() );

    CtiCCEventLogMsg * eventMsg = dynamic_cast<CtiCCEventLogMsg *>( capController.eventMessages.front() );

    BOOST_REQUIRE( eventMsg );

    BOOST_CHECK_EQUAL( capControlIvvcRemoteControlEvent, eventMsg->getEventType() );
    BOOST_CHECK_EQUAL( "Disable Remote Control", eventMsg->getText() );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_QueryAutoRemoteStatus_Fail)
{
    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );

    regulator->setPaoName("Test Regulator #1");

    BOOST_CHECK_THROW( regulator->getOperatingMode(), MissingPointAttribute );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_QueryAutoRemoteStatus_Success)
{
    using Cti::CapControl::VoltageRegulator;

    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );

    regulator->setPaoName("Test Regulator #1");
    regulator->loadAttributes( &attributes );

    BOOST_CHECK_NO_THROW( regulator->getOperatingMode() );

    // Haven't received a point update since creation - we don't know our operating mode

    BOOST_CHECK_EQUAL( VoltageRegulator::UnknownMode, regulator->getOperatingMode() );

    // point update that tells us we are in local mode
    
    CtiPointDataMsg pointDataMsg( 5600, 0.0, NormalQuality, StatusPointType );

    regulator->handlePointData( &pointDataMsg );

    BOOST_CHECK_EQUAL( VoltageRegulator::LocalMode, regulator->getOperatingMode() );

    // point update that tells us we are in remote mode

    pointDataMsg.setValue(1.0);

    regulator->handlePointData( &pointDataMsg );

    BOOST_CHECK_EQUAL( VoltageRegulator::RemoteMode, regulator->getOperatingMode() );
}


BOOST_AUTO_TEST_CASE(test_PhaseOperatedVolatgeRegulator_TapUp_Success_with_Phase_A_info)
{
    TestCtiCapController    capController;
    TestAttributeService    attributes;

    VoltageRegulatorManager::SharedPtr  regulator( new PhaseOperatedVoltageRegulator );

    regulator->setPaoName("Test Regulator #1");
    regulator->loadAttributes( &attributes );

    regulator->setPhase( Cti::CapControl::Phase_A );

    BOOST_CHECK_NO_THROW( regulator->executeTapUpOperation() );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );
    
    BOOST_REQUIRE( signalMsg );
    
    BOOST_CHECK_EQUAL( 3100, signalMsg->getId() );     // ID of the 'TapUp' LitePoint
    BOOST_CHECK_EQUAL( "Raise Tap Position - Phase: A", signalMsg->getText() );
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

    BOOST_CHECK_EQUAL( capControlIvvcTapOperation, eventMsg->getEventType() );
    BOOST_CHECK_EQUAL( "Raise Tap Position - Phase: A", eventMsg->getText() );
}

