#pragma once

#include <boost/ptr_container/ptr_vector.hpp>
#include "strategyLoader.h"
#include "StrategyManager.h"

#include "test_serialization.h"

#include "CapControlPao.h"
#include "Conductor.h"
#include "ccarea.h"
#include "ccAreaBase.h"
#include "cccapbank.h"
#include "ccfeeder.h"
#include "ccmonitorpoint.h"
#include "ccsparea.h"
#include "ccstate.h"
#include "ccsubstation.h"
#include "ccsubstationbus.h"
#include "DynamicCommand.h"
#include "GangOperatedVoltageRegulator.h"
#include "MsgAreas.h"
#include "MsgBankMove.h"
#include "MsgCapBankStates.h"
#include "MsgCapControlCommand.h"
#include "MsgCapControlMessage.h"
#include "MsgCapControlServerResponse.h"
#include "MsgCapControlShutdown.h"
#include "MsgChangeOpState.h"
#include "MsgDeleteItem.h"
#include "MsgItemCommand.h"
#include "MsgObjectMove.h"
#include "MsgSpecialAreas.h"
#include "MsgSubStationBus.h"
#include "MsgSubStations.h"
#include "MsgSystemStatus.h"
#include "MsgVerifyBanks.h"
#include "MsgVerifyInactiveBanks.h"
#include "MsgVerifySelectedBank.h"
#include "MsgVoltageRegulator.h"
#include "PhaseOperatedVoltageRegulator.h"
#include "VoltageRegulator.h"

/*-----------------------------------------------------------------------------
    CCShutdown
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiCCShutdown> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiCCShutdown );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();
    }
};

/*-----------------------------------------------------------------------------
    CCServerResponse
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiCCServerResponse> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiCCServerResponse );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiCCServerResponse &imsg = dynamic_cast<CtiCCServerResponse&>(*_imsg);

        GenerateRandom( imsg._messageId );
        GenerateRandom( imsg._responseType, 0, 2 ); // responseType must be <=> [0,2]
        GenerateRandom( imsg._response );
    }
};

/*-----------------------------------------------------------------------------
    CCMessage
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CapControlMessage> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CapControlMessage );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();
    }
};

/*-----------------------------------------------------------------------------
    CCState
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiCCState> : public TestCaseBase<CtiCCState>
{
    void Populate()
    {
        CtiCCState &imsg = dynamic_cast<CtiCCState&>(*_imsg);

        GenerateRandom( imsg._text );
        GenerateRandom( imsg._foregroundcolor );
        GenerateRandom( imsg._backgroundcolor );
    }
};

/*-----------------------------------------------------------------------------
    CCCapBankStates
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiCCCapBankStatesMsg> : public TestCase<CapControlMessage>
{
    CtiCCState_vec state_vec;

    void Create()
    {
        _imsg.reset( new CtiCCCapBankStatesMsg( state_vec ));
    }

    void Populate()
    {
        TestCase<CapControlMessage>::Populate();

        CtiCCCapBankStatesMsg &imsg = dynamic_cast<CtiCCCapBankStatesMsg&>(*_imsg);

        const int maxCapBankStates = GenerateRandom<int>( 2, 5 );

        for( int item_nbr=0; item_nbr < maxCapBankStates; item_nbr++ )
        {
            imsg._ccCapBankStates->push_back( new CtiCCState );
            TestCaseItem<CtiCCState> tc_item;
            tc_item.Populate( imsg._ccCapBankStates->back() );
        }
    }
};

/*-----------------------------------------------------------------------------
    CCObjectMove
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiCCObjectMoveMsg> : public TestCase<CapControlMessage>
{
    void Create()
    {
        _imsg.reset( new CtiCCObjectMoveMsg( false, 0, 0, 0, 0 ));
    }

    void Populate()
    {
        TestCase<CapControlMessage>::Populate();

        CtiCCObjectMoveMsg &imsg = dynamic_cast<CtiCCObjectMoveMsg&>(*_imsg);

        GenerateRandom( imsg._permanentflag );
        GenerateRandom( imsg._oldparentid );
        GenerateRandom( imsg._objectid );
        GenerateRandom( imsg._newparentid );
        GenerateRandom( imsg._switchingorder );
        GenerateRandom( imsg._closeOrder );
        GenerateRandom( imsg._tripOrder );
    }
};

/*-----------------------------------------------------------------------------
    CCPao
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CapControlPao> : public TestCaseBase<CapControlPao>
{
    void Populate()
    {
        CapControlPao &imsg = dynamic_cast<CapControlPao&>(*_imsg);

        GenerateRandom( imsg._paoId );
        GenerateRandom( imsg._paoCategory );
        GenerateRandom( imsg._paoClass );
        GenerateRandom( imsg._paoName );
        GenerateRandom( imsg._paoType );
        GenerateRandom( imsg._paoDescription );
        GenerateRandom( imsg._disableFlag );
    }
};

/*-----------------------------------------------------------------------------
    CCArea
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiCCArea> : public TestCase<CapControlPao>
{
    void Populate()
    {
        TestCase<CapControlPao>::Populate();

        {
            CtiCCAreaBase &imsg = static_cast<CtiCCAreaBase&>(*_imsg);

            GenerateRandom( imsg._subStationIds );
            GenerateRandom( imsg._ovUvDisabledFlag );
            GenerateRandom( imsg._pfactor );
            GenerateRandom( imsg._estPfactor );
            GenerateRandom( imsg._voltReductionControlValue );
        }

        {
            CtiCCArea &imsg = static_cast<CtiCCArea&>(*_imsg);

            GenerateRandom( imsg._childVoltReductionFlag );
        }
    }
};

/*-----------------------------------------------------------------------------
    CCGeoAreas
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiCCGeoAreasMsg> : public TestCase<CapControlMessage>
{
    CtiCCArea_vec area_vec;

    void Create()
    {
        _imsg.reset( new CtiCCGeoAreasMsg( area_vec, 0 ));
    }

    void Populate()
    {
        TestCase<CapControlMessage>::Populate();

        CtiCCGeoAreasMsg &imsg = dynamic_cast<CtiCCGeoAreasMsg&>(*_imsg);

        GenerateRandom( imsg._msgInfoBitMask );

        const int maxGeoAreas = GenerateRandom<int>( 2, 5 );

        for( int item_nbr=0; item_nbr < maxGeoAreas; item_nbr++ )
        {
            imsg._ccGeoAreas->push_back( new CtiCCArea );
            TestCaseItem<CtiCCArea> tc_item;
            tc_item.Populate( imsg._ccGeoAreas->back() );
        }
    }
};

/*-----------------------------------------------------------------------------
    CCSpecial
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiCCSpecial> : public TestCase<CapControlPao>
{
    void Populate()
    {
        TestCase<CapControlPao>::Populate();

        CtiCCAreaBase &imsg = static_cast<CtiCCAreaBase&>(*_imsg);

        GenerateRandom( imsg._subStationIds );
        GenerateRandom( imsg._ovUvDisabledFlag );
        GenerateRandom( imsg._pfactor );
        GenerateRandom( imsg._estPfactor );
        GenerateRandom( imsg._voltReductionControlValue );
    }
};

/*-----------------------------------------------------------------------------
    CCSpecialAreas
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiCCSpecialAreasMsg> : public TestCase<CapControlMessage>
{
    CtiCCSpArea_vec special_vec;

    void Create()
    {
        _imsg.reset( new CtiCCSpecialAreasMsg( special_vec ));
    }

    void Populate()
    {
        TestCase<CapControlMessage>::Populate();

        CtiCCSpecialAreasMsg &imsg = dynamic_cast<CtiCCSpecialAreasMsg&>(*_imsg);

        const int maxSpecialAreas = GenerateRandom<int>( 2, 5 );

        for( int item_nbr=0; item_nbr < maxSpecialAreas; item_nbr++ )
        {
            imsg._ccSpecialAreas->push_back( new CtiCCSpecial );
            TestCaseItem<CtiCCSpecial> tc_item;
            tc_item.Populate( imsg._ccSpecialAreas->back() );
        }
    }
};

/*-----------------------------------------------------------------------------
    CCBank
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiCCCapBank> : public TestCase<CapControlPao>
{
    void Populate()
    {
        TestCase<CapControlPao>::Populate();

        CtiCCCapBank &imsg = static_cast<CtiCCCapBank&>(*_imsg);

        GenerateRandom( imsg._parentId );
        GenerateRandom( imsg._maxdailyops );
        GenerateRandom( imsg._maxopsdisableflag );
        GenerateRandom( imsg._alarminhibitflag );
        GenerateRandom( imsg._controlinhibitflag );
        GenerateRandom( imsg._operationalstate );
        GenerateRandom( imsg._controllertype );
        GenerateRandom( imsg._controldeviceid );
        GenerateRandom( imsg._banksize );
        GenerateRandom( imsg._typeofswitch );
        GenerateRandom( imsg._switchmanufacture );
        GenerateRandom( imsg._maplocationid );
        GenerateRandom( imsg._reclosedelay );
        GenerateRandom( imsg._controlorder );
        GenerateRandom( imsg._statuspointid );
        GenerateRandom( imsg._controlstatus );
        GenerateRandom( imsg._operationanalogpointid );
        GenerateRandom( imsg._totaloperations );
        GenerateRandom( imsg._laststatuschangetime );
        GenerateRandom( imsg._tagscontrolstatus );
        GenerateRandom( imsg.getOriginalParent()._originalParentId );
        GenerateRandom( imsg._currentdailyoperations );
        GenerateRandom( imsg._ignoreFlag );
        GenerateRandom( imsg._ignoreReason );
        GenerateRandom( imsg._ovUvDisabledFlag );
        GenerateRandom( imsg._triporder );
        GenerateRandom( imsg._closeorder );
        GenerateRandom( imsg._controlDeviceType );
        GenerateRandom( imsg._sBeforeVars );
        GenerateRandom( imsg._sAfterVars );
        GenerateRandom( imsg._sPercentChange );
        GenerateRandom( imsg._maxDailyOpsHitFlag );
        GenerateRandom( imsg._ovuvSituationFlag );
        GenerateRandom( imsg._controlStatusQuality );
        GenerateRandom( imsg._localControlFlag );
        GenerateRandom( imsg._partialPhaseInfo );
    }
};

/*-----------------------------------------------------------------------------
    ControlStrategy
-----------------------------------------------------------------------------*/

class TestControlStrategy : public ControlStrategy
{
    std::string _controlUnit;
    std::string _controlMethod;
    double      _peakLag;
    double      _offPeakLag;
    double      _peakLead;
    double      _offPeakLead;
    double      _peakPFSetPoint;
    double      _offPeakPFSetPoint;

public:

    TestControlStrategy()
    {}

    virtual const std::string getControlUnits() const
    {
        return _controlUnit;
    }

    virtual const std::string getControlMethod() const
    {
        return _controlMethod;
    }

    virtual double getPeakLag() const
    {
        return _peakLag;
    }

    virtual double getOffPeakLag() const
    {
        return _offPeakLag;
    }

    virtual double getPeakLead() const
    {
        return _peakLead;
    }

    virtual double getOffPeakLead() const
    {
        return _offPeakLead;
    }

    virtual double getPeakPFSetPoint() const
    {
        return _peakPFSetPoint;
    }

    virtual double getOffPeakPFSetPoint() const
    {
        return _offPeakPFSetPoint;
    }

    // randomize members of the control strategy that will be serialized and sent
    void randomize()
    {
        const std::string controlUnit_items[] = {
                NoControlUnit,
                KVarControlUnit,
                VoltsControlUnit,
                MultiVoltControlUnit,
                MultiVoltVarControlUnit,
                PFactorKWKVarControlUnit,
                IntegratedVoltVarControlUnit,
                TimeOfDayControlUnit
        };

        const std::string controlMethod_items[] = {
                IndividualFeederControlMethod,
                SubstationBusControlMethod,
                BusOptimizedFeederControlMethod,
                ManualOnlyControlMethod,
                TimeOfDayControlMethod
        };

        GenerateRandom( _maxDailyOperation );
        GenerateRandom( _maxOperationDisableFlag );
        GenerateRandom( _controlUnit,   controlUnit_items );
        GenerateRandom( _controlMethod, controlMethod_items );
        GenerateRandom( _peakLag );
        GenerateRandom( _offPeakLag );
        GenerateRandom( _peakLead );
        GenerateRandom( _offPeakLead );
        GenerateRandom( _peakPFSetPoint );
        GenerateRandom( _offPeakPFSetPoint );
    }

    //
    // these methods are not use for serialization
    //

    virtual void execute()
    {}

    virtual void setControlMethod(const std::string & method)
    {}

    virtual void restoreParameters( const std::string &name, const std::string &type, const std::string &value )
    {}

    virtual const ControlUnitType getUnitType() const
    {
        return None;
    }

    virtual const ControlMethodType getMethodType() const
    {
        return NoMethod;
    }
};

/*-----------------------------------------------------------------------------
    StrategyLoader
-----------------------------------------------------------------------------*/
class TestStrategyLoader : public StrategyLoader
{
    StrategyManager::StrategyMap _strategyMap;

public:

    enum
    {
        TEST_STRATEGY_ID = 0
    };

    TestStrategyLoader()
    {
        StrategyManager::SharedPtr strategy( new TestControlStrategy );
        _strategyMap.insert( std::pair<long, StrategyManager::SharedPtr>( TEST_STRATEGY_ID, strategy ));
    }

    virtual StrategyManager::StrategyMap load(const long ID)
    {
        return _strategyMap;
    }
};

/*-----------------------------------------------------------------------------
    StrategyManager
-----------------------------------------------------------------------------*/
class TestStrategyManager : public StrategyManager
{
public:

    TestStrategyManager() : StrategyManager( std::unique_ptr<StrategyLoader>( new TestStrategyLoader ))
    {
        reloadAll();
    }

    static StrategyManager *getNewInstance()
    {
        static boost::ptr_vector<StrategyManager> strategyManagers;

        strategyManagers.push_back( new TestStrategyManager );
        return &strategyManagers.back();
    }
};

/*-----------------------------------------------------------------------------
    CCFeeder
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiCCFeeder> : public TestCase<CapControlPao>
{
    void Populate()
    {
        TestCase<CapControlPao>::Populate();

        CtiCCFeeder &imsg = static_cast<CtiCCFeeder&>(*_imsg);

        GenerateRandom( imsg._parentId );
        GenerateRandom( imsg._currentVarLoadPointId );
        GenerateRandom( imsg._currentVarLoadPointValue );
        GenerateRandom( imsg._currentWattLoadPointId );
        GenerateRandom( imsg._currentWattLoadPointValue );
        GenerateRandom( imsg._mapLocationId );
        GenerateRandom( imsg._displayorder );
        GenerateRandom( imsg._newPointDataReceivedFlag );
        GenerateRandom( imsg._lastCurrentVarPointUpdateTime );
        GenerateRandom( imsg._estimatedVarLoadPointId );
        GenerateRandom( imsg._estimatedVarLoadPointValue );
        GenerateRandom( imsg._dailyOperationsAnalogPointId );
        GenerateRandom( imsg._powerFactorPointId );
        GenerateRandom( imsg._estimatedPowerFactorPointId );
        GenerateRandom( imsg._currentDailyOperations );

        {
            bool recentlyControlledFlag_or_performingVerificationFlag;
            GenerateRandom( recentlyControlledFlag_or_performingVerificationFlag );

            imsg._recentlyControlledFlag   = recentlyControlledFlag_or_performingVerificationFlag;
            imsg._performingVerificationFlag = recentlyControlledFlag_or_performingVerificationFlag;
        }

        GenerateRandom( imsg._lastOperationTime );
        GenerateRandom( imsg._varValueBeforeControl );
        GenerateRandom( imsg._powerFactorValue );
        GenerateRandom( imsg._estimatedPowerFactorValue );
        GenerateRandom( imsg._currentVarPointQuality );
        GenerateRandom( imsg._waiveControlFlag );
        GenerateRandom( imsg._decimalPlaces );
        GenerateRandom( imsg._peakTimeFlag );
        GenerateRandom( imsg._currentVoltLoadPointId );
        GenerateRandom( imsg._currentVoltLoadPointValue );
        GenerateRandom( imsg._currentWattPointQuality );
        GenerateRandom( imsg._currentVoltPointQuality );
        GenerateRandom( imsg._targetVarValue );
        GenerateRandom( imsg._solution );
        GenerateRandom( imsg._ovUvDisabledFlag );
        GenerateRandom( imsg._phaseAvalue );
        GenerateRandom( imsg._phaseBvalue );
        GenerateRandom( imsg._phaseCvalue );
        GenerateRandom( imsg._likeDayControlFlag );
        GenerateRandom( imsg._usePhaseData );
        GenerateRandom( imsg.getOriginalParent()._originalParentId );

        // careful here, this is a sorted vector (just generate 1)
        const int max_item_nbr = 1;

        for( int item_nbr=0; item_nbr < max_item_nbr; item_nbr++ )
        {
            imsg._cccapbanks.push_back( new CtiCCCapBank );
            TestCaseItem<CtiCCCapBank> tc_item;
            tc_item.Populate( imsg._cccapbanks.back() );
        }

        // set and randomize the control strategy
        imsg._strategyId = TestStrategyLoader::TEST_STRATEGY_ID;
        TestControlStrategy& strategy = dynamic_cast<TestControlStrategy&>(*(imsg.getStrategy()));
        strategy.randomize();
    }
};

/*-----------------------------------------------------------------------------
    CCSubstationBusItem
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiCCSubstationBus> : public TestCase<CapControlPao>
{
    TestStrategyManager _testStrategyManager;

    void Populate()
    {
        TestCase<CapControlPao>::Populate();

        CtiCCSubstationBus &imsg = static_cast<CtiCCSubstationBus&>(*_imsg);

        GenerateRandom( imsg._parentId );
        GenerateRandom( imsg._currentVarLoadPointId );
        GenerateRandom( imsg._currentVarLoadPointValue );
        GenerateRandom( imsg._currentWattLoadPointId );
        GenerateRandom( imsg._currentWattLoadPointValue );
        GenerateRandom( imsg._mapLocationId );
        GenerateRandom( imsg._decimalPlaces );
        GenerateRandom( imsg._newPointDataReceivedFlag );
        GenerateRandom( imsg._busupdatedflag );
        GenerateRandom( imsg._lastCurrentVarPointUpdateTime );
        GenerateRandom( imsg._estimatedVarLoadPointId );
        GenerateRandom( imsg._estimatedVarLoadPointValue );
        GenerateRandom( imsg._dailyOperationsAnalogPointId );
        GenerateRandom( imsg._powerFactorPointId );
        GenerateRandom( imsg._estimatedPowerFactorPointId );
        GenerateRandom( imsg._currentDailyOperations );
        GenerateRandom( imsg._peaktimeflag );
        GenerateRandom( imsg._recentlyControlledFlag );
        GenerateRandom( imsg._lastOperationTime );
        GenerateRandom( imsg._varValueBeforeControl );
        GenerateRandom( imsg._powerFactorValue );
        GenerateRandom( imsg._estimatedPowerFactorValue );
        GenerateRandom( imsg._currentVarPointQuality );
        GenerateRandom( imsg._waiveControlFlag );
        GenerateRandom( imsg._currentVoltLoadPointId );
        GenerateRandom( imsg._currentVoltLoadPointValue );
        GenerateRandom( imsg._verificationFlag );
        GenerateRandom( imsg._switchOverStatus );
        GenerateRandom( imsg._currentWattPointQuality );
        GenerateRandom( imsg._currentVoltPointQuality );
        GenerateRandom( imsg._targetVarValue );
        GenerateRandom( imsg._solution );
        GenerateRandom( imsg._ovUvDisabledFlag );
        GenerateRandom( imsg._phaseAvalue );
        GenerateRandom( imsg._phaseBvalue );
        GenerateRandom( imsg._phaseCvalue );
        GenerateRandom( imsg._likeDayControlFlag );
        GenerateRandom( imsg._displayOrder );
        GenerateRandom( imsg._voltReductionFlag );
        GenerateRandom( imsg._usePhaseData );

        // imsg._primaryBusFlag should remain false in this test

        GenerateRandom( imsg._altDualSubId );
        GenerateRandom( imsg._dualBusEnable );

        // {
        //    // _strategyId this is overwritten for testing controlStrategy
        //    Controllable &item = static_cast<Controllable&>(*_imsg);
        //    GenerateRandom( item._strategyId );
        // }

        const int maxFeeders = GenerateRandom<int>( 2, 5 );

        for( int item_nbr=0; item_nbr < maxFeeders; item_nbr++ )
        {
            imsg._ccfeeders.push_back( new CtiCCFeeder( TestStrategyManager::getNewInstance() ));
            TestCaseItem<CtiCCFeeder> tc_item;
            tc_item.Populate( imsg._ccfeeders[item_nbr] );
        }

        // re-copy to alternate (this is preferred over constraining more flags, if we randomize those with different value,
        // Java client would need a more complete model to predict values )
        imsg._altSubVarVal  = imsg._currentVarLoadPointValue;
        imsg._altSubWattVal = imsg._currentWattLoadPointValue;
        imsg._altSubVoltVal = imsg._currentVoltLoadPointValue;

        // set and randomize the control strategy
        imsg._strategyId = TestStrategyLoader::TEST_STRATEGY_ID;
        TestControlStrategy& strategy = dynamic_cast<TestControlStrategy&>(*(imsg.getStrategy()));
        strategy.randomize();
    }
};

/*-----------------------------------------------------------------------------
    CCSubstationBus
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiCCSubstationBusMsg> : public TestCase<CapControlMessage>
{
    CtiCCSubstationBus_vec substationBus_vec;

    void Create()
    {
        _imsg.reset( new CtiCCSubstationBusMsg( substationBus_vec ));
    }

    void Populate()
    {
        TestCase<CapControlMessage>::Populate();

        CtiCCSubstationBusMsg &imsg = dynamic_cast<CtiCCSubstationBusMsg&>(*_imsg);

        GenerateRandom( imsg._msgInfoBitMask );

        const int maxSubstationBuses = GenerateRandom<int>( 2, 5 );

        for( int item_nbr=0; item_nbr < maxSubstationBuses; item_nbr++ )
        {
            imsg._substationBuses->push_back( new CtiCCSubstationBus( TestStrategyManager::getNewInstance() ));
            TestCaseItem<CtiCCSubstationBus> tc_item;
            tc_item.Populate( imsg._substationBuses->back() );
        }
    }
};

/*-----------------------------------------------------------------------------
    CCSubstationItem
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiCCSubstation> : public TestCase<CapControlPao>
{
    void Populate()
    {
        TestCase<CapControlPao>::Populate();

        CtiCCSubstation &imsg = static_cast<CtiCCSubstation&>(*_imsg);

        GenerateRandom( imsg._parentId );
        GenerateRandom( imsg._ovUvDisabledFlag );
        GenerateRandom( imsg._subBusIds );
        GenerateRandom( imsg._pfactor );
        GenerateRandom( imsg._estPfactor );
        GenerateRandom( imsg._saEnabledFlag );
        GenerateRandom( imsg._saEnabledId );
        GenerateRandom( imsg._voltReductionFlag );
        GenerateRandom( imsg._recentlyControlledFlag );
        GenerateRandom( imsg._childVoltReductionFlag );
    }
};

/*-----------------------------------------------------------------------------
    CCSubstations
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiCCSubstationsMsg> : public TestCase<CapControlMessage>
{
    CtiCCSubstation_vec substations_vec;

    void Create()
    {
        _imsg.reset( new CtiCCSubstationsMsg( substations_vec ));
    }

    void Populate()
    {
        TestCase<CapControlMessage>::Populate();

        CtiCCSubstationsMsg &imsg = dynamic_cast<CtiCCSubstationsMsg&>(*_imsg);

        GenerateRandom( imsg._msgInfoBitMask );

        const int maxSubstations = GenerateRandom<int>( 2, 5 );

        for( int item_nbr=0; item_nbr < maxSubstations; item_nbr++ )
        {
            imsg._substations->push_back( new CtiCCSubstation );
            TestCaseItem<CtiCCSubstation> tc_item;
            tc_item.Populate( imsg._substations->back() );
        }
    }
};

/*-----------------------------------------------------------------------------
    CCDeleteItem
-----------------------------------------------------------------------------*/
template<>
struct TestCase<DeleteItem> : public TestCase<CapControlMessage>
{
    void Create()
    {
        _imsg.reset( new DeleteItem( 0 ) );
    }

    void Populate()
    {
        TestCase<CapControlMessage>::Populate();

        DeleteItem &imsg = dynamic_cast<DeleteItem&>(*_imsg);

        GenerateRandom( imsg._itemId );
    }
};

/*-----------------------------------------------------------------------------
    CCSystemStatus
-----------------------------------------------------------------------------*/
template<>
struct TestCase<SystemStatus> : public TestCase<CapControlMessage>
{
    void Create()
    {
        _imsg.reset( new SystemStatus( 0 ) );
    }

    void Populate()
    {
        TestCase<CapControlMessage>::Populate();

        SystemStatus &imsg = dynamic_cast<SystemStatus&>(*_imsg);

        GenerateRandom( imsg._systemState );
    }
};


/*-----------------------------------------------------------------------------
    CCVoltageRegulatorItem
-----------------------------------------------------------------------------*/
template<>
struct TestCase<Cti::CapControl::VoltageRegulator> : public TestCase<CapControlPao>
{
    void Populate()
    {
        TestCase<CapControlPao>::Populate();

        Cti::CapControl::VoltageRegulator &imsg = static_cast<Cti::CapControl::VoltageRegulator&>(*_imsg);

        const int TapOperation_items[]  = { (int)Cti::CapControl::VoltageRegulator::None,
                                            (int)Cti::CapControl::VoltageRegulator::LowerTap,
                                            (int)Cti::CapControl::VoltageRegulator::RaiseTap };

        const int OperatingMode_items[] = { (int)Cti::CapControl::VoltageRegulator::RemoteMode,
                                            (int)Cti::CapControl::VoltageRegulator::LocalMode };

        GenerateRandom( (int&)imsg._lastControlOperation, TapOperation_items );
        GenerateRandom( imsg._lastControlOperationTime );

        {
            Cti::CapControl::GangOperatedVoltageRegulator* p_imsg = dynamic_cast<Cti::CapControl::GangOperatedVoltageRegulator*>( &imsg );

            if( p_imsg )
            {
                GenerateRandom( p_imsg->_recentTapOperation );
                GenerateRandom( (int&)p_imsg->_lastOperatingMode,          OperatingMode_items );
                GenerateRandom( (int&)p_imsg->_lastCommandedOperatingMode, OperatingMode_items );
            }
        }

        {
            Cti::CapControl::PhaseOperatedVoltageRegulator* p_imsg = dynamic_cast<Cti::CapControl::PhaseOperatedVoltageRegulator*>( &imsg );

            if( p_imsg )
            {
                GenerateRandom( p_imsg->_recentTapOperation );
                GenerateRandom( (int&)p_imsg->_lastOperatingMode,          OperatingMode_items );
                GenerateRandom( (int&)p_imsg->_lastCommandedOperatingMode, OperatingMode_items );
            }
        }
    }
};

/*-----------------------------------------------------------------------------
    CCVoltageRegulator
-----------------------------------------------------------------------------*/
template<>
struct TestCase<VoltageRegulatorMessage> : public TestCase<CapControlMessage>
{
    void Create()
    {
        _imsg.reset( new VoltageRegulatorMessage );
    }

    void Populate()
    {
        TestCase<CapControlMessage>::Populate();

        VoltageRegulatorMessage &imsg = dynamic_cast<VoltageRegulatorMessage&>(*_imsg);

        imsg.regulators.push_back( new Cti::CapControl::GangOperatedVoltageRegulator );
        imsg.regulators.push_back( new Cti::CapControl::PhaseOperatedVoltageRegulator );

        for( int item_nbr=0; item_nbr < imsg.regulators.size(); item_nbr++ )
        {
            TestCaseItem<Cti::CapControl::VoltageRegulator> tc_item;
            tc_item.Populate( imsg.regulators[item_nbr] );
        }
    }
};

/*-----------------------------------------------------------------------------
    CCCommand
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CapControlCommand> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CapControlCommand );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CapControlCommand &imsg = dynamic_cast<CapControlCommand&>(*_imsg);

        GenerateRandom( imsg._commandId );
        GenerateRandom( imsg._messageId );
    }
};

/*-----------------------------------------------------------------------------
    CCDynamicCommand
-----------------------------------------------------------------------------*/
template<>
struct TestCase<DynamicCommand> : public TestCase<CapControlCommand>
{
    void Create()
    {
        _imsg.reset( new DynamicCommand );
    }

    void Populate()
    {
        TestCase<CapControlCommand>::Populate();

        DynamicCommand &imsg = dynamic_cast<DynamicCommand&>(*_imsg);

        const int CommandType_items[]  = { (int)DynamicCommand::UNDEFINED,
                                           (int)DynamicCommand::DELTA };

        const int Parameter_items[]    = { (int)DynamicCommand::DEVICE_ID,
                                           (int)DynamicCommand::POINT_ID,
                                           (int)DynamicCommand::POINT_RESPONSE_DELTA,
                                           (int)DynamicCommand::POINT_RESPONSE_STATIC_DELTA };

        GenerateRandom( (int&)imsg._commandType,  CommandType_items );

        {
            long lvalue;

            imsg._longParameters[(int)DynamicCommand::DEVICE_ID]                     = GenerateRandom( lvalue );
            imsg._longParameters[(int)DynamicCommand::POINT_ID]                      = GenerateRandom( lvalue );
            imsg._longParameters[(int)DynamicCommand::POINT_RESPONSE_DELTA]          = GenerateRandom( lvalue );
            imsg._longParameters[(int)DynamicCommand::POINT_RESPONSE_STATIC_DELTA]   = GenerateRandom( lvalue );
        }


        {
            double dvalue;

            imsg._doubleParameters[(int)DynamicCommand::DEVICE_ID]                   = GenerateRandom( dvalue );
            imsg._doubleParameters[(int)DynamicCommand::POINT_ID]                    = GenerateRandom( dvalue );
            imsg._doubleParameters[(int)DynamicCommand::POINT_RESPONSE_DELTA]        = GenerateRandom( dvalue );
            imsg._doubleParameters[(int)DynamicCommand::POINT_RESPONSE_STATIC_DELTA] = GenerateRandom( dvalue );
        }
    }
};

/*-----------------------------------------------------------------------------
     CCItemCommand
-----------------------------------------------------------------------------*/
template<>
struct TestCase<ItemCommand> : public TestCase<CapControlCommand>
{
    void Create()
    {
        _imsg.reset( new ItemCommand );
    }

    void Populate()
    {
        TestCase<CapControlCommand>::Populate();

        ItemCommand &imsg = dynamic_cast<ItemCommand&>(*_imsg);

        GenerateRandom( imsg._itemId );
    }
};

/*-----------------------------------------------------------------------------
     CCChangeOpState
-----------------------------------------------------------------------------*/
template<>
struct TestCase<ChangeOpState> : public TestCase<ItemCommand>
{
    void Create()
    {
        _imsg.reset( new ChangeOpState );
    }

    void Populate()
    {
        TestCase<ItemCommand>::Populate();

        ChangeOpState &imsg = dynamic_cast<ChangeOpState&>(*_imsg);

        const std::string opStateName_items[]  = { "Fixed",
                                                   "Switched",
                                                   "StandAlone",
                                                   "Uninstalled" };

        GenerateRandom( imsg._opStateName, opStateName_items );
    }
};

/*-----------------------------------------------------------------------------
    CCCapBankMove
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiCCCapBankMoveMsg> : public TestCase<ItemCommand>
{
    void Create()
    {
        _imsg.reset( new CtiCCCapBankMoveMsg( 0, 0, 0, 0, 0, 0 ));
    }

    void Populate()
    {
        TestCase<ItemCommand>::Populate();

        CtiCCCapBankMoveMsg &imsg = dynamic_cast<CtiCCCapBankMoveMsg&>(*_imsg);

        GenerateRandom( imsg._permanentflag, 0, 1 ); // <=> [0,1]
        GenerateRandom( imsg._oldfeederid );
        GenerateRandom( imsg._newfeederid );
        GenerateRandom( imsg._capswitchingorder );
        GenerateRandom( imsg._closeOrder );
        GenerateRandom( imsg._tripOrder );
    }
};

/*-----------------------------------------------------------------------------
    CCVerifyBanks
-----------------------------------------------------------------------------*/
template<>
struct TestCase<VerifyBanks> : public TestCase<ItemCommand>
{
    void Create()
    {
        _imsg.reset( new VerifyBanks );
    }

    void Populate()
    {
        TestCase<ItemCommand>::Populate();

        VerifyBanks &imsg = dynamic_cast<VerifyBanks&>(*_imsg);

        GenerateRandom( imsg._disableOvUv );
    }
};

/*-----------------------------------------------------------------------------
    CCVerifyInactiveBanks
-----------------------------------------------------------------------------*/
template<>
struct TestCase<VerifyInactiveBanks> : public TestCase<VerifyBanks>
{
    void Create()
    {
        _imsg.reset( new VerifyInactiveBanks );
    }

    void Populate()
    {
        TestCase<VerifyBanks>::Populate();

        VerifyInactiveBanks &imsg = dynamic_cast<VerifyInactiveBanks&>(*_imsg);

        GenerateRandom( imsg._bankInactiveTime );
    }
};

/*-----------------------------------------------------------------------------
    CCVerifySelectedBank
-----------------------------------------------------------------------------*/
template<>
struct TestCase<VerifySelectedBank> : public TestCase<VerifyBanks>
{
    void Create()
    {
        _imsg.reset( new VerifySelectedBank );
    }

    void Populate()
    {
        TestCase<VerifyBanks>::Populate();

        VerifySelectedBank &imsg = dynamic_cast<VerifySelectedBank&>(*_imsg);

        GenerateRandom( imsg._bankId );
    }
};
