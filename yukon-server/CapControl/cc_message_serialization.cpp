#include "precompiled.h"

#include "message_serialization.h"
#include "message_serialization_util.h"
#include "message_factory.h"
#include "cc_message_serialization.h"

#include <vector>

using namespace std;

namespace Cti {
namespace Messaging {
namespace Serialization {

namespace {

struct CCMessageFactoryRegister
{
    CCMessageFactoryRegister()
    {
        g_messageFactory.registerSerializer <::CtiCCShutdown,           Thrift::CCShutdown>             ( &populateThrift, &populateMessage, "CCShutdown"       );
        g_messageFactory.registerSerializer <::CtiCCServerResponse,     Thrift::CCServerResponse>       ( &populateThrift, &populateMessage, "CCServerResponse" );

        // base message are removed from the factory:
        // g_messageFactory.registerSerializer <::CapControlMessage,       Thrift::CCMessage>              ( &populateThrift, &populateMessage, "CCMessage" );
        g_messageFactory.registerSerializer <::CtiCCCapBankStatesMsg,   Thrift::CCCapBankStates>        ( &populateThrift, &populateMessage, "CCCapBankStates"  );
        g_messageFactory.registerSerializer <::CtiCCObjectMoveMsg,      Thrift::CCObjectMove>           ( &populateThrift, &populateMessage, "CCObjectMove" );
        g_messageFactory.registerSerializer <::CtiCCGeoAreasMsg,        Thrift::CCGeoAreas>             ( &populateThrift, &populateMessage, "CCGeoAreas" );
        g_messageFactory.registerSerializer <::CtiCCSpecialAreasMsg,    Thrift::CCSpecialAreas>         ( &populateThrift, &populateMessage, "CCSpecialAreas" );
        g_messageFactory.registerSerializer <::CtiCCSubstationBusMsg,   Thrift::CCSubstationBus>        ( &populateThrift, &populateMessage, "CCSubstationBus" );
        g_messageFactory.registerSerializer <::CtiCCSubstationsMsg,     Thrift::CCSubstations>          ( &populateThrift, &populateMessage, "CCSubstations" );
        g_messageFactory.registerSerializer <::DeleteItem,              Thrift::CCDeleteItem>           ( &populateThrift, &populateMessage, "CCDeleteItem" );
        g_messageFactory.registerSerializer <::SystemStatus,            Thrift::CCSystemStatus>         ( &populateThrift, &populateMessage, "CCSystemStatus" );
        g_messageFactory.registerSerializer <::VoltageRegulatorMessage, Thrift::CCVoltageRegulator>     ( &populateThrift, &populateMessage, "CCVoltageRegulator" );

        g_messageFactory.registerSerializer <::CapControlCommand,       Thrift::CCCommand>              ( &populateThrift, &populateMessage, "CCCommand" );
        g_messageFactory.registerSerializer <::DynamicCommand,          Thrift::CCDynamicCommand>       ( &populateThrift, &populateMessage, "CCDynamicCommand" );
        g_messageFactory.registerSerializer <::ItemCommand,             Thrift::CCItemCommand>          ( &populateThrift, &populateMessage, "CCItemCommand" );
        g_messageFactory.registerSerializer <::ChangeOpState,           Thrift::CCChangeOpState>        ( &populateThrift, &populateMessage, "CCChangeOpState" );
        g_messageFactory.registerSerializer <::CtiCCCapBankMoveMsg,     Thrift::CCCapBankMove>          ( &populateThrift, &populateMessage, "CCCapBankMove" );
        g_messageFactory.registerSerializer <::VerifyBanks,             Thrift::CCVerifyBanks>          ( &populateThrift, &populateMessage, "CCVerifyBanks" );
        g_messageFactory.registerSerializer <::VerifyInactiveBanks,     Thrift::CCVerifyInactiveBanks>  ( &populateThrift, &populateMessage, "CCVerifyInactiveBanks" );
        g_messageFactory.registerSerializer <::VerifySelectedBank,      Thrift::CCVerifySelectedBank>   ( &populateThrift, &populateMessage, "CCVerifySelectedBank" );
    }
};

const CCMessageFactoryRegister g_ccMessageFactoryRegister;

}

//=============================================================================
//  CCShutdown
//=============================================================================

MessagePtr<Thrift::CCShutdown>::type populateThrift( const ::CtiCCShutdown& imsg )
{
    MessagePtr<Thrift::CCShutdown>::type omsg( new Thrift::CCShutdown );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));

    return omsg;
}

MessagePtr<::CtiCCShutdown>::type populateMessage( const Thrift::CCShutdown& imsg )
{
    MessagePtr<::CtiCCShutdown>::type omsg( new ::CtiCCShutdown );

    static_cast<::CtiMessage&>(*omsg)           = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  CCServerResponse
//=============================================================================

MessagePtr<Thrift::CCServerResponse>::type populateThrift( const ::CtiCCServerResponse& imsg )
{
    MessagePtr<Thrift::CCServerResponse>::type omsg( new Thrift::CCServerResponse );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__messageId                      ( imsg.getMessageId() );
    omsg->__set__responseType                   ( imsg.getResponseType() );
    omsg->__set__response                       ( imsg.getResponse() );

    return omsg;
}

MessagePtr<::CtiCCServerResponse>::type populateMessage( const Thrift::CCServerResponse& imsg )
{
    MessagePtr<::CtiCCServerResponse>::type omsg( new ::CtiCCServerResponse(
                                                  imsg._messageId,
                                                  imsg._responseType,
                                                  imsg._response ));

    static_cast<::CtiMessage&>(*omsg)           = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  CCMessage
//=============================================================================

MessagePtr<Thrift::CCMessage>::type populateThrift( const ::CapControlMessage& imsg )
{
    MessagePtr<Thrift::CCMessage>::type omsg( new Thrift::CCMessage );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));

    return omsg;
}

MessagePtr<::CapControlMessage>::type populateMessage( const Thrift::CCMessage& imsg )
{
    MessagePtr<::CapControlMessage>::type omsg( new ::CapControlMessage );

    static_cast<::CtiMessage&>(*omsg)           = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  CCCapBankStates
//=============================================================================

MessagePtr<Thrift::CCCapBankStates>::type populateThrift( const ::CtiCCCapBankStatesMsg& imsg )
{
    MessagePtr<Thrift::CCCapBankStates>::type omsg( new Thrift::CCCapBankStates );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CapControlMessage&>(imsg) ));
    omsg->__set__ccCapBankStates                ( transformContainer<vector<Thrift::CCState>>( *imsg.getCCCapBankStates(), populateThrift ));

    return omsg;
}

MessagePtr<::CtiCCCapBankStatesMsg>::type populateMessage( const Thrift::CCCapBankStates& imsg )
{
    MessagePtr<::CtiCCCapBankStatesMsg>::type omsg( new ::CtiCCCapBankStatesMsg(
                                                  transformContainer<::CtiCCState_vec>( imsg._ccCapBankStates, populateMessage )));

    static_cast<::CapControlMessage&>(*omsg)    = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  CCState
//=============================================================================

MessagePtr<Thrift::CCState>::type populateThrift( const ::CtiCCState& imsg )
{
    MessagePtr<Thrift::CCState>::type omsg( new Thrift::CCState );

    omsg->__set__text                           ( imsg.getText() );
    omsg->__set__foregroundColor                ( imsg.getForegroundColor() );
    omsg->__set__backgroundColor                ( imsg.getBackgroundColor() );

    return omsg;
}

MessagePtr<::CtiCCState>::type populateMessage( const Thrift::CCState& imsg )
{
    MessagePtr<::CtiCCState>::type omsg( new ::CtiCCState );

    omsg->setText                               ( imsg._text );
    omsg->setForegroundColor                    ( imsg._foregroundColor );
    omsg->setBackgroundColor                    ( imsg._backgroundColor );

    return omsg;
}

//=============================================================================
//  CCObjectMove
//=============================================================================

MessagePtr<Thrift::CCObjectMove>::type populateThrift( const ::CtiCCObjectMoveMsg& imsg )
{
    MessagePtr<Thrift::CCObjectMove>::type omsg( new Thrift::CCObjectMove );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CapControlMessage&>(imsg) ));
    omsg->__set__permanentFlag                  ( imsg.getPermanentFlag() );
    omsg->__set__oldParentId                    ( imsg.getOldParentId() );
    omsg->__set__objectId                       ( imsg.getObjectId() );
    omsg->__set__newParentId                    ( imsg.getNewParentId() );
    omsg->__set__switchingOrder                 ( imsg.getSwitchingOrder() );
    omsg->__set__closeOrder                     ( imsg.getCloseOrder() );
    omsg->__set__tripOrder                      ( imsg.getTripOrder() );

    return omsg;
}

MessagePtr<::CtiCCObjectMoveMsg>::type populateMessage( const Thrift::CCObjectMove& imsg )
{
    MessagePtr<::CtiCCObjectMoveMsg>::type omsg( new ::CtiCCObjectMoveMsg(
                                                  imsg._permanentFlag,
                                                  imsg._oldParentId,
                                                  imsg._objectId,
                                                  imsg._newParentId,
                                                  imsg._switchingOrder,
                                                  imsg._closeOrder,
                                                  imsg._tripOrder ));

    static_cast<::CapControlMessage&>(*omsg)    = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  CCGeoAreas
//=============================================================================

MessagePtr<Thrift::CCGeoAreas>::type populateThrift( const ::CtiCCGeoAreasMsg& imsg )
{
    MessagePtr<Thrift::CCGeoAreas>::type omsg( new Thrift::CCGeoAreas );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CapControlMessage&>(imsg) ));
    omsg->__set__msgInfoBitMask                 ( imsg.getBitMask() );
    omsg->__set__ccGeoAreas                     ( transformContainer<vector<Thrift::CCArea>>( *imsg.getCCGeoAreas(), populateThrift ));

    return omsg;
}

MessagePtr<::CtiCCGeoAreasMsg>::type populateMessage( const Thrift::CCGeoAreas& imsg )
{
    MessagePtr<::CtiCCGeoAreasMsg>::type omsg( new ::CtiCCGeoAreasMsg(
                                                  transformContainer<::CtiCCArea_vec>( imsg._ccGeoAreas, populateMessage ),
                                                  imsg._msgInfoBitMask ));

    static_cast<::CapControlMessage&>(*omsg)    = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  CCArea
//=============================================================================

MessagePtr<Thrift::CCArea>::type populateThrift( const ::CtiCCArea& imsg )
{
    MessagePtr<Thrift::CCArea>::type omsg( new Thrift::CCArea );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CapControlPao&>(imsg) ));
    omsg->__set__ovUvDisabledFlag               ( imsg.getOvUvDisabledFlag() );
    omsg->__set__substationIds                  ( transformContainer<vector<int32_t>>( imsg.getSubstationIds() ));

    double pfDisplayValue    = imsg.getPFactor();
    double estPfDisplayValue = imsg.getEstPFactor();

    // Modifying the display value of pFactor to represent +100% values as a negative value.
    if (pfDisplayValue > 1)
    {
        pfDisplayValue -= 2;
    }
    if (estPfDisplayValue > 1)
    {
        estPfDisplayValue -= 2;
    }

    omsg->__set__pfDisplayValue                 ( pfDisplayValue );
    omsg->__set__estPfDisplayValue              ( estPfDisplayValue );
    omsg->__set__voltReductionControlValue      ( imsg.getVoltReductionControlValue() );
    omsg->__set__childVoltReductionFlag         ( imsg.getChildVoltReductionFlag() );

    return omsg;
}

MessagePtr<::CtiCCArea>::type populateMessage( const Thrift::CCArea& imsg )
{
    MessagePtr<::CtiCCArea>::type omsg( new ::CtiCCArea );

    // no implementation found

    return omsg;
}

//=============================================================================
//  CCSpecialAreas
//=============================================================================

MessagePtr<Thrift::CCSpecialAreas>::type populateThrift( const ::CtiCCSpecialAreasMsg& imsg )
{
    MessagePtr<Thrift::CCSpecialAreas>::type omsg( new Thrift::CCSpecialAreas );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CapControlMessage&>(imsg) ));

    omsg->__set__ccSpecialAreas                 ( transformContainer<vector<Thrift::CCSpecial>>( *imsg.getCCSpecialAreas(), populateThrift ));

    return omsg;
}

MessagePtr<::CtiCCSpecialAreasMsg>::type populateMessage( const Thrift::CCSpecialAreas& imsg )
{
    MessagePtr<::CtiCCSpecialAreasMsg>::type omsg( new ::CtiCCSpecialAreasMsg(
                                                   transformContainer<::CtiCCSpArea_vec>( imsg._ccSpecialAreas, populateMessage )));

    static_cast<::CapControlMessage&>(*omsg)    = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  CCSpecial
//=============================================================================

MessagePtr<Thrift::CCSpecial>::type populateThrift( const ::CtiCCSpecial& imsg )
{
    MessagePtr<Thrift::CCSpecial>::type omsg( new Thrift::CCSpecial );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CapControlPao&>(imsg) ));
    omsg->__set__substationIds                  ( transformContainer<vector<int32_t>>( imsg.getSubstationIds() ));

    double pfDisplayValue    = imsg.getPFactor();
    double estPfDisplayValue = imsg.getEstPFactor();

    // Modifying the display value of pFactor to represent +100% values as a negative value.
    if (pfDisplayValue > 1)
    {
        pfDisplayValue -= 2;
    }
    if (estPfDisplayValue > 1)
    {
        estPfDisplayValue -= 2;
    }

    omsg->__set__ovUvDisabledFlag               ( imsg.getOvUvDisabledFlag() );
    omsg->__set__pfDisplayValue                 ( pfDisplayValue );
    omsg->__set__estPfDisplayValue              ( estPfDisplayValue );
    omsg->__set__voltReductionControlValue      ( imsg.getVoltReductionControlValue() );

    return omsg;
}

MessagePtr<::CtiCCSpecial>::type populateMessage( const Thrift::CCSpecial& imsg )
{
    MessagePtr<::CtiCCSpecial>::type omsg( new ::CtiCCSpecial );

    // no implementation found

    return omsg;
}

//=============================================================================
//  CCSubstationBus
//=============================================================================

MessagePtr<Thrift::CCSubstationBus>::type populateThrift( const ::CtiCCSubstationBusMsg& imsg )
{
    MessagePtr<Thrift::CCSubstationBus>::type omsg( new Thrift::CCSubstationBus );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CapControlMessage&>(imsg) ));
    omsg->__set__msgInfoBitMask                 ( imsg.getMsgInfoBitMask() );
    omsg->__set__ccSubstationBuses              ( transformContainer<vector<Thrift::CCSubstationBusItem>>( *imsg.getCCSubstationBuses(), populateThrift ));

    return omsg;
}

MessagePtr<::CtiCCSubstationBusMsg>::type populateMessage( const Thrift::CCSubstationBus& imsg )
{
    MessagePtr<::CtiCCSubstationBusMsg>::type omsg( new ::CtiCCSubstationBusMsg(
                                                  transformContainer<CtiCCSubstationBus_vec>( imsg._ccSubstationBuses, populateMessage ),
                                                  imsg._msgInfoBitMask ));

    static_cast<::CapControlMessage&>(*omsg)    = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  CCSubstationBusItem
//=============================================================================

MessagePtr<Thrift::CCSubstationBusItem>::type populateThrift( const ::CtiCCSubstationBus& imsg )
{
    MessagePtr<Thrift::CCSubstationBusItem>::type omsg( new Thrift::CCSubstationBusItem );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CapControlPao&>(imsg) ));

    double tempVar;
    double tempVolt;
    double tempWatt;

    long   tempAltSubId                  = imsg.getAltDualSubId();
    double temppowerfactorvalue          = imsg.getPowerFactorValue();
    double tempestimatedpowerfactorvalue = imsg.getEstimatedPowerFactorValue();
    bool   tempDualBusEnabled            = imsg.getDualBusEnable();

    if( imsg.getDualBusEnable() && imsg.getSwitchOverStatus() || imsg.getPrimaryBusFlag() )
    {
        tempVolt = imsg.getAltSubVoltVal();
        tempVar  = imsg.getAltSubVarVal();
        tempWatt = imsg.getAltSubWattVal();

        if( imsg.getPrimaryBusFlag() )
        {
            tempDualBusEnabled = true;
            tempAltSubId       = imsg.getAlterateBusIdForPrimary();
        }
    }
    else
    {
        tempVolt = imsg.getCurrentvoltloadpointvalue();
        tempVar  = imsg.getCurrentvarloadpointvalue();
        tempWatt = imsg.getCurrentwattloadpointvalue();
    }

    // Modifying the display value of pFactor to represent +100% values as a negative value.
    if( temppowerfactorvalue > 1 )
    {
        temppowerfactorvalue -= 2;
    }
    if( tempestimatedpowerfactorvalue > 1 )
    {
        tempestimatedpowerfactorvalue -= 2;
    }

    // make strategy values returned through getPeak...()/getOffPeak...() good for IVVC too.
    imsg.getStrategy()->setPeakTimeFlag( imsg.getPeakTimeFlag() );

    omsg->__set__parentId                        ( imsg.getParentId() );
    omsg->__set__strategy_maxDailyOperation      ( imsg.getStrategy()->getMaxDailyOperation() );
    omsg->__set__strategy_maxOperationDisableFlag( imsg.getStrategy()->getMaxOperationDisableFlag() );
    omsg->__set__currentVarLoadPointId           ( imsg.getCurrentVarLoadPointId() );
    omsg->__set__varValue                        ( tempVar );
    omsg->__set__currentWattLoadPointId          ( imsg.getCurrentWattLoadPointId() );
    omsg->__set__wattValue                       ( tempWatt );
    omsg->__set__mapLocationId                   ( imsg.getMapLocationId() );
    omsg->__set__strategy_controlUnits           ( imsg.getStrategy()->getControlUnits() );
    omsg->__set__decimalPlaces                   ( imsg.getDecimalPlaces() );
    omsg->__set__newPointDataReceivedFlag        ( imsg.getNewPointDataReceivedFlag() );
    omsg->__set__busUpdatedflag                  ( imsg.getBusUpdatedFlag() );
    omsg->__set__lastCurrentVarPointUpdateTime   ( CtiTimeToMilliseconds( imsg.getLastCurrentVarPointUpdateTime() ));
    omsg->__set__estimatedVarLoadPointId         ( imsg.getEstimatedVarLoadPointId() );
    omsg->__set__estimatedVarLoadPointValue      ( imsg.getEstimatedVarLoadPointValue() );
    omsg->__set__dailyOperationsAnalogPointId    ( imsg.getDailyOperationsAnalogPointId() );
    omsg->__set__powerFactorPointId              ( imsg.getPowerFactorPointId() );
    omsg->__set__estimatedPowerFactorPointId     ( imsg.getEstimatedPowerFactorPointId() );
    omsg->__set__currentDailyOperations          ( imsg.getCurrentDailyOperations() );
    omsg->__set__peakTimeFlag                    ( imsg.getPeakTimeFlag() );
    omsg->__set__recentlyControlledFlag          ( imsg.getRecentlyControlledFlag() );
    omsg->__set__lastOperationTime               ( CtiTimeToMilliseconds( imsg.getLastOperationTime() ));
    omsg->__set__varValueBeforeControl           ( imsg.getVarValueBeforeControl() );
    omsg->__set__powerFactorValue                ( temppowerfactorvalue );
    omsg->__set__estimatedPowerFactorValue       ( tempestimatedpowerfactorvalue );
    omsg->__set__currentVarPointQuality          ( imsg.getCurrentVarPointQuality() );
    omsg->__set__waiveControlFlag                ( imsg.getWaiveControlFlag() );
    omsg->__set__strategy_peakLag                ( imsg.getStrategy()->getPeakLag() );
    omsg->__set__strategy_offPeakLag             ( imsg.getStrategy()->getOffPeakLag() );
    omsg->__set__strategy_peakLead               ( imsg.getStrategy()->getPeakLead() );
    omsg->__set__strategy_offPeakLead            ( imsg.getStrategy()->getOffPeakLead() );
    omsg->__set__currentVoltLoadPointId          ( imsg.getCurrentVoltLoadPointId() );
    omsg->__set__voltValue                       ( tempVolt );
    omsg->__set__verificationFlag                ( imsg.getVerificationFlag() );
    omsg->__set__switchOverStatus                ( imsg.getSwitchOverStatus() );
    omsg->__set__currentWattPointQuality         ( imsg.getCurrentWattPointQuality() );
    omsg->__set__currentVoltPointQuality         ( imsg.getCurrentVoltPointQuality() );
    omsg->__set__targetVarValue                  ( imsg.getTargetVarValue() );
    omsg->__set__solution                        ( imsg.getSolution() );
    omsg->__set__ovUvDisabledFlag                ( imsg.getOvUvDisabledFlag() );
    omsg->__set__strategy_peakPFSetPoint         ( imsg.getStrategy()->getPeakPFSetPoint() );
    omsg->__set__strategy_offPeakPFSetPoint      ( imsg.getStrategy()->getOffPeakPFSetPoint() );
    omsg->__set__strategy_controlMethod          ( imsg.getStrategy()->getControlMethod() );
    omsg->__set__phaseAValue                     ( imsg.getPhaseAValue() );
    omsg->__set__phaseBValue                     ( imsg.getPhaseBValue() );
    omsg->__set__phaseCValue                     ( imsg.getPhaseCValue() );
    omsg->__set__likeDayControlFlag              ( imsg.getLikeDayControlFlag() );
    omsg->__set__displayOrder                    ( imsg.getDisplayOrder() );
    omsg->__set__voltReductionFlag               ( imsg.getVoltReductionFlag() );
    omsg->__set__usePhaseData                    ( imsg.getUsePhaseData() );
    omsg->__set__primaryBusFlag                  ( imsg.getPrimaryBusFlag() );
    omsg->__set__altSubId                        ( tempAltSubId );
    omsg->__set__dualBusEnabled                  ( tempDualBusEnabled );
    omsg->__set__strategyId                      ( imsg.getStrategyId() );
    omsg->__set__ccFeeders                       ( transformContainer<vector<Thrift::CCFeeder>>( imsg.getCCFeeders(), populateThrift ));

    return omsg;
}

MessagePtr<::CtiCCSubstationBus>::type populateMessage( const Thrift::CCSubstationBusItem& imsg )
{
    MessagePtr<::CtiCCSubstationBus>::type omsg( new ::CtiCCSubstationBus );

    // no implementation found

    return omsg;
}

//=============================================================================
//  CCFeeder
//=============================================================================

MessagePtr<Thrift::CCFeeder>::type populateThrift( const ::CtiCCFeeder& imsg )
{
    MessagePtr<Thrift::CCFeeder>::type omsg( new Thrift::CCFeeder );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CapControlPao&>(imsg) ));

    double temppowerfactorvalue          = imsg.getPowerFactorValue();
    double tempestimatedpowerfactorvalue = imsg.getEstimatedPowerFactorValue();

    if( temppowerfactorvalue > 1 )
    {
        temppowerfactorvalue -= 2;
    }
    if( tempestimatedpowerfactorvalue > 1 )
    {
        tempestimatedpowerfactorvalue -= 2;
    }

    // make strategy values returned through getPeak...()/getOffPeak...() good for IVVC too.
    imsg.getStrategy()->setPeakTimeFlag( imsg.getPeakTimeFlag() );

    omsg->__set__parentId                        ( imsg.getParentId() );
    omsg->__set__strategy_maxDailyOperation      ( imsg.getStrategy()->getMaxDailyOperation() );
    omsg->__set__strategy_maxOperationDisableFlag( imsg.getStrategy()->getMaxOperationDisableFlag() );
    omsg->__set__currentVarLoadPointId           ( imsg.getCurrentVarLoadPointId() );
    omsg->__set__currentVarLoadPointValue        ( imsg.getCurrentVarLoadPointValue() );
    omsg->__set__currentWattLoadPointId          ( imsg.getCurrentWattLoadPointId() );
    omsg->__set__currentWattLoadPointValue       ( imsg.getCurrentWattLoadPointValue() );
    omsg->__set__mapLocationId                   ( imsg.getMapLocationId() );
    omsg->__set__displayOrder                    ( imsg.getDisplayOrder() );
    omsg->__set__newPointDataReceivedFlag        ( imsg.getNewPointDataReceivedFlag() );
    omsg->__set__lastCurrentVarPointUpdateTime   ( CtiTimeToMilliseconds( imsg.getLastCurrentVarPointUpdateTime() ));
    omsg->__set__estimatedVarLoadPointId         ( imsg.getEstimatedVarLoadPointId() );
    omsg->__set__estimatedVarLoadPointValue      ( imsg.getEstimatedVarLoadPointValue() );
    omsg->__set__dailyOperationsAnalogPointId    ( imsg.getDailyOperationsAnalogPointId() );
    omsg->__set__powerFactorPointId              ( imsg.getPowerFactorPointId() );
    omsg->__set__estimatedPowerFactorPointId     ( imsg.getEstimatedPowerFactorPointId() );
    omsg->__set__currentDailyOperations          ( imsg.getCurrentDailyOperations() );
    omsg->__set__recentlyControlledFlag_or_performingVerificationFlag
                                                 ( imsg.getRecentlyControlledFlag() || imsg.getPerformingVerificationFlag() );
    omsg->__set__lastOperationTime               ( CtiTimeToMilliseconds( imsg.getLastOperationTime() ));
    omsg->__set__varValueBeforeControl           ( imsg.getVarValueBeforeControl() );
    omsg->__set__powerFactorValue                ( temppowerfactorvalue );
    omsg->__set__estimatedPowerFactorValue       ( tempestimatedpowerfactorvalue );
    omsg->__set__currentVarPointQuality          ( imsg.getCurrentVarPointQuality() );
    omsg->__set__waiveControlFlag                ( imsg.getWaiveControlFlag() );
    omsg->__set__strategy_controlUnits           ( imsg.getStrategy()->getControlUnits() );
    omsg->__set__decimalPlaces                   ( imsg.getDecimalPlaces() );
    omsg->__set__peakTimeFlag                    ( imsg.getPeakTimeFlag() );
    omsg->__set__strategy_peakLag                ( imsg.getStrategy()->getPeakLag() );
    omsg->__set__strategy_offPeakLag             ( imsg.getStrategy()->getOffPeakLag() );
    omsg->__set__strategy_PeakLead               ( imsg.getStrategy()->getPeakLead() );
    omsg->__set__strategy_OffPeakLead            ( imsg.getStrategy()->getOffPeakLead() );
    omsg->__set__currentVoltLoadPointId          ( imsg.getCurrentVoltLoadPointId() );
    omsg->__set__currentVoltLoadPointValue       ( imsg.getCurrentVoltLoadPointValue() );
    omsg->__set__currentWattPointQuality         ( imsg.getCurrentWattPointQuality() );
    omsg->__set__currentVoltPointQuality         ( imsg.getCurrentVoltPointQuality() );
    omsg->__set__targetVarValue                  ( imsg.getTargetVarValue() );
    omsg->__set__solution                        ( imsg.getSolution() );
    omsg->__set__ovUvDisabledFlag                ( imsg.getOvUvDisabledFlag() );
    omsg->__set__strategy_peakPFSetPoint         ( imsg.getStrategy()->getPeakPFSetPoint() );
    omsg->__set__strategy_offPeakPFSetPoint      ( imsg.getStrategy()->getOffPeakPFSetPoint() );
    omsg->__set__strategy_controlMethod          ( imsg.getStrategy()->getControlMethod() );
    omsg->__set__phaseAValue                     ( imsg.getPhaseAValue() );
    omsg->__set__phaseBValue                     ( imsg.getPhaseBValue() );
    omsg->__set__phaseCValue                     ( imsg.getPhaseCValue() );
    omsg->__set__likeDayControlFlag              ( imsg.getLikeDayControlFlag() );
    omsg->__set__usePhaseData                    ( imsg.getUsePhaseData() );
    omsg->__set__originalParentId                ( imsg.getOriginalParent().getOriginalParentId() );
    omsg->__set__ccCapbanks                      ( transformContainer<vector<Thrift::CCCapBank>>( imsg.getCCCapBanks(), populateThrift ));

    return omsg;
}

MessagePtr<::CtiCCFeeder>::type populateMessage( const Thrift::CCFeeder& imsg )
{
    MessagePtr<::CtiCCFeeder>::type omsg( new ::CtiCCFeeder );

    // no implementation found

    return omsg;
}

//=============================================================================
//  CCCapBank
//=============================================================================

MessagePtr<Thrift::CCCapBank>::type populateThrift( const ::CtiCCCapBank& imsg )
{
    MessagePtr<Thrift::CCCapBank>::type omsg( new Thrift::CCCapBank );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CapControlPao&>(imsg) ));
    omsg->__set__parentId                       ( imsg.getParentId() );
    omsg->__set__maxDailyOps                    ( imsg.getMaxDailyOps() );
    omsg->__set__maxOpsDisableFlag              ( imsg.getMaxOpsDisableFlag() );
    omsg->__set__alarmInhibitFlag               ( imsg.getAlarmInhibitFlag() );
    omsg->__set__controlInhibitFlag             ( imsg.getControlInhibitFlag() );
    omsg->__set__operationalState               ( imsg.getOperationalState() );
    omsg->__set__controllerType                 ( imsg.getControllerType() );
    omsg->__set__controlDeviceId                ( imsg.getControlDeviceId() );
    omsg->__set__bankSize                       ( imsg.getBankSize() );
    omsg->__set__typeOfSwitch                   ( imsg.getTypeOfSwitch() );
    omsg->__set__switchManufacture              ( imsg.getSwitchManufacture() );
    omsg->__set__mapLocationId                  ( imsg.getMapLocationId() );
    omsg->__set__recloseDelay                   ( imsg.getRecloseDelay() );
    omsg->__set__controlOrder                   ( imsg.getControlOrder() );
    omsg->__set__statusPointId                  ( imsg.getStatusPointId() );
    omsg->__set__controlStatus                  ( imsg.getControlStatus() );
    omsg->__set__operationAnalogPointId         ( imsg.getOperationAnalogPointId() );
    omsg->__set__totalOperations                ( imsg.getTotalOperations() );
    omsg->__set__lastStatusChangeTime           ( CtiTimeToMilliseconds( imsg.getLastStatusChangeTime() ));
    omsg->__set__tagsControlStatus              ( imsg.getTagsControlStatus() );
    omsg->__set__originalParentId               ( imsg.getOriginalParent().getOriginalParentId() );
    omsg->__set__currentDailyOperations         ( imsg.getCurrentDailyOperations() );
    omsg->__set__ignoreFlag                     ( imsg.getIgnoreFlag() );
    omsg->__set__ignoreReason                   ( imsg.getIgnoredReason() );
    omsg->__set__ovUvDisabledFlag               ( imsg.getOvUvDisabledFlag() );
    omsg->__set__tripOrder                      ( imsg.getTripOrder() );
    omsg->__set__closeOrder                     ( imsg.getCloseOrder() );
    omsg->__set__controlDeviceType              ( imsg.getControlDeviceType() );
    omsg->__set__sBeforeVars                    ( imsg.getBeforeVarsString() );
    omsg->__set__sAfterVars                     ( imsg.getAfterVarsString() );
    omsg->__set__sPercentChange                 ( imsg.getPercentChangeString() );
    omsg->__set__maxDailyOpsHitFlag             ( imsg.getMaxDailyOpsHitFlag() );
    omsg->__set__ovUvSituationFlag              ( imsg.getOvUvSituationFlag() );
    omsg->__set__controlStatusQuality           ( imsg.getControlStatusQuality() );
    omsg->__set__localControlFlag               ( imsg.getLocalControlFlag() );
    omsg->__set__partialPhaseInfo               ( imsg.getPartialPhaseInfo() );

    return omsg;
}

MessagePtr<::CtiCCCapBank>::type populateMessage( const Thrift::CCCapBank& imsg )
{
    MessagePtr<::CtiCCCapBank>::type omsg( new ::CtiCCCapBank );

    // no implementation found

    return omsg;
}

//=============================================================================
//  CCSubstations
//=============================================================================

MessagePtr<Thrift::CCSubstations>::type populateThrift( const ::CtiCCSubstationsMsg& imsg )
{
    MessagePtr<Thrift::CCSubstations>::type omsg( new Thrift::CCSubstations );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CapControlMessage&>(imsg) ));
    omsg->__set__msgInfoBitMask                 ( imsg.getMsgInfoBitMask() );
    omsg->__set__ccSubstations                  ( transformContainer<vector<Thrift::CCSubstationItem>>( *imsg.getCCSubstations(), populateThrift ));

    return omsg;
}

MessagePtr<::CtiCCSubstationsMsg>::type populateMessage( const Thrift::CCSubstations& imsg )
{
    MessagePtr<::CtiCCSubstationsMsg>::type omsg( new ::CtiCCSubstationsMsg(
                                                  transformContainer<CtiCCSubstation_vec>( imsg._ccSubstations, populateMessage ),
                                                  imsg._msgInfoBitMask ));

    static_cast<::CapControlMessage&>(*omsg)    = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  CCSubstationItem
//=============================================================================

MessagePtr<Thrift::CCSubstationItem>::type populateThrift( const ::CtiCCSubstation& imsg )
{
    MessagePtr<Thrift::CCSubstationItem>::type omsg( new Thrift::CCSubstationItem );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CapControlPao&>(imsg) ));
    omsg->__set__parentId                       ( imsg.getParentId() );
    omsg->__set__ovUvDisabledFlag               ( imsg.getOvUvDisabledFlag() );
    omsg->__set__subBusIds                      ( transformContainer<vector<int32_t>>( imsg.getCCSubIds() ));

    double pfDisplayValue    = imsg.getPFactor();
    double estPfDisplayValue = imsg.getEstPFactor();

    // Modifying the display value of pFactor to represent +100% values as a negative value.
    if (pfDisplayValue > 1)
    {
        pfDisplayValue -= 2;
    }
    if (estPfDisplayValue > 1)
    {
        estPfDisplayValue -= 2;
    }

    omsg->__set__pfDisplayValue                 ( pfDisplayValue );
    omsg->__set__estPfDisplayValue              ( estPfDisplayValue );
    omsg->__set__saEnabledFlag                  ( imsg.getSaEnabledFlag() );
    omsg->__set__saEnabledId                    ( imsg.getSaEnabledId() );
    omsg->__set__voltReductionFlag              ( imsg.getVoltReductionFlag() );
    omsg->__set__recentlyControlledFlag         ( imsg.getRecentlyControlledFlag() );
    omsg->__set__childVoltReductionFlag         ( imsg.getChildVoltReductionFlag() );

    return omsg;
}

MessagePtr<::CtiCCSubstation>::type populateMessage( const Thrift::CCSubstationItem& imsg )
{
    MessagePtr<::CtiCCSubstation>::type omsg( new ::CtiCCSubstation );

    // no implementation found

    return omsg;
}

//=============================================================================
//  CCDeleteItem
//=============================================================================

MessagePtr<Thrift::CCDeleteItem>::type populateThrift( const ::DeleteItem& imsg )
{
    MessagePtr<Thrift::CCDeleteItem>::type omsg( new Thrift::CCDeleteItem );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CapControlMessage&>(imsg) ));
    omsg->__set__itemId                         ( imsg.getItemId() );

    return omsg;
}

MessagePtr<::DeleteItem>::type populateMessage( const Thrift::CCDeleteItem& imsg )
{
    MessagePtr<::DeleteItem>::type omsg( new ::DeleteItem(
                                                  imsg._itemId ));

    static_cast<::CapControlMessage&>(*omsg)    = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  CCSystemStatus
//=============================================================================

MessagePtr<Thrift::CCSystemStatus>::type populateThrift( const ::SystemStatus& imsg )
{
    MessagePtr<Thrift::CCSystemStatus>::type omsg( new Thrift::CCSystemStatus );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CapControlMessage&>(imsg) ));
    omsg->__set__systemState                    ( imsg.getState() );

    return omsg;
}

MessagePtr<::SystemStatus>::type populateMessage( const Thrift::CCSystemStatus& imsg )
{
    MessagePtr<::SystemStatus>::type omsg( new ::SystemStatus(
                                                  imsg._systemState ));

    static_cast<::CapControlMessage&>(*omsg)    = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  CCVoltageRegulator
//=============================================================================

MessagePtr<Thrift::CCVoltageRegulator>::type populateThrift( const ::VoltageRegulatorMessage& imsg )
{
    MessagePtr<Thrift::CCVoltageRegulator>::type omsg( new Thrift::CCVoltageRegulator );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CapControlMessage&>(imsg) ));
    omsg->__set__regulators                     ( transformContainer<vector<Thrift::CCVoltageRegulatorItem>>( imsg.getVoltageRegulators(), populateThrift ));

    return omsg;
}

MessagePtr<::VoltageRegulatorMessage>::type populateMessage( const Thrift::CCVoltageRegulator& imsg )
{
    MessagePtr<::VoltageRegulatorMessage>::type omsg( new ::VoltageRegulatorMessage );

    // no implementation found

    return omsg;
}

//=============================================================================
//  CCVoltageRegulatorItem
//=============================================================================

MessagePtr<Thrift::CCVoltageRegulatorItem>::type populateThrift( const ::Cti::CapControl::VoltageRegulator& imsg )
{
    MessagePtr<Thrift::CCVoltageRegulatorItem>::type omsg( new Thrift::CCVoltageRegulatorItem );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CapControlPao&>(imsg) ));
    omsg->__set__parentId                       ( 0 ); //parentId Must be here for clients...
    omsg->__set__lastTapOperation               ( imsg.getLastControlOperation() );
    omsg->__set__lastTapOperationTime           ( CtiTimeToMilliseconds( imsg.getLastControlOperationTime() ));
    omsg->__set__regulatorType                  ( imsg.getType() );
    omsg->__set__recentTapOperation             ( imsg.getRecentTapOperation() );
    omsg->__set__lastOperatingMode              ( imsg.getLastOperatingMode() );
    omsg->__set__lastCommandedOperatingMode     ( imsg.getLastCommandedOperatingMode() );

    return omsg;
}

//=============================================================================
//  CCCommand
//=============================================================================

MessagePtr<Thrift::CCCommand>::type populateThrift( const ::CapControlCommand& imsg )
{
    MessagePtr<Thrift::CCCommand>::type omsg( new Thrift::CCCommand );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__messageId                      ( imsg.getMessageId() );
    omsg->__set__commandId                      ( imsg.getCommandId() );

    return omsg;
}

MessagePtr<::CapControlCommand>::type populateMessage( const Thrift::CCCommand& imsg )
{
    MessagePtr<::CapControlCommand>::type omsg( new ::CapControlCommand(
                                                  imsg._commandId,
                                                  imsg._messageId ));

    static_cast<::CtiMessage&>(*omsg)           = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  CCDynamicCommand
//=============================================================================

MessagePtr<Thrift::CCDynamicCommand>::type populateThrift( const ::DynamicCommand& imsg )
{
    MessagePtr<Thrift::CCDynamicCommand>::type omsg( new Thrift::CCDynamicCommand );

    // no implementation found

    return omsg;
}

MessagePtr<::DynamicCommand>::type populateMessage( const Thrift::CCDynamicCommand& imsg )
{
    MessagePtr<::DynamicCommand>::type omsg( new ::DynamicCommand );

    static_cast<::CapControlCommand&>(*omsg)    = *populateMessage( imsg._baseMessage );
    omsg->setCommandType                        ( (DynamicCommand::CommandType)imsg._commandType );
    omsg->setLongParameters                     ( transformContainer<::DynamicCommand::LongParameterMap>( imsg._longParameters ));
    omsg->setDoubleParameters                   ( transformContainer<::DynamicCommand::DoubleParameterMap>( imsg._doubleParameters ));

    return omsg;
}

//=============================================================================
//  CCItemCommand
//=============================================================================

MessagePtr<Thrift::CCItemCommand>::type populateThrift( const ::ItemCommand& imsg )
{
    MessagePtr<Thrift::CCItemCommand>::type omsg( new Thrift::CCItemCommand );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CapControlCommand&>(imsg) ));
    omsg->__set__itemId                         ( imsg.getItemId() );

    return omsg;
}

MessagePtr<::ItemCommand>::type populateMessage( const Thrift::CCItemCommand& imsg )
{
    MessagePtr<::ItemCommand>::type omsg( new ::ItemCommand );

    static_cast<::CapControlCommand&>(*omsg)    = *populateMessage( imsg._baseMessage );
    omsg->setItemId                             ( imsg._itemId );

    return omsg;
}

//=============================================================================
//  CCChangeOpState
//=============================================================================

MessagePtr<Thrift::CCChangeOpState>::type populateThrift( const ::ChangeOpState& imsg )
{
    MessagePtr<Thrift::CCChangeOpState>::type omsg( new Thrift::CCChangeOpState );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::ItemCommand&>(imsg) ));
    omsg->__set__opStateName                    ( imsg.getOpStateName() );

    return omsg;
}

MessagePtr<::ChangeOpState>::type populateMessage( const Thrift::CCChangeOpState& imsg )
{
    MessagePtr<::ChangeOpState>::type omsg( new ::ChangeOpState );

    static_cast<::ItemCommand&>(*omsg)          = *populateMessage( imsg._baseMessage );
    omsg->setOpStateName                        ( imsg._opStateName );

    return omsg;
}

//=============================================================================
//  CCCapBankMove
//=============================================================================

MessagePtr<Thrift::CCCapBankMove>::type populateThrift( const ::CtiCCCapBankMoveMsg& imsg )
{
    MessagePtr<Thrift::CCCapBankMove>::type omsg( new Thrift::CCCapBankMove );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::ItemCommand&>(imsg) ));
    omsg->__set__permanentFlag                  ( imsg.getPermanentFlag() );
    omsg->__set__oldFeederId                    ( imsg.getOldFeederId() );
    omsg->__set__newFeederId                    ( imsg.getNewFeederId() );
    omsg->__set__capSwitchingOrder              ( imsg.getCapSwitchingOrder() );
    omsg->__set__closeOrder                     ( imsg.getCloseOrder() );
    omsg->__set__tripOrder                      ( imsg.getTripOrder() );

    return omsg;
}

MessagePtr<::CtiCCCapBankMoveMsg>::type populateMessage( const Thrift::CCCapBankMove& imsg )
{
    MessagePtr<::CtiCCCapBankMoveMsg>::type omsg( new ::CtiCCCapBankMoveMsg(
                                                  imsg._permanentFlag,
                                                  imsg._oldFeederId,
                                                  imsg._newFeederId,
                                                  imsg._capSwitchingOrder,
                                                  imsg._closeOrder,
                                                  imsg._tripOrder ));

    static_cast<::ItemCommand&>(*omsg)          = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  CCVerifyBanks
//=============================================================================

MessagePtr<Thrift::CCVerifyBanks>::type populateThrift( const ::VerifyBanks& imsg )
{
    MessagePtr<Thrift::CCVerifyBanks>::type omsg( new Thrift::CCVerifyBanks );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::ItemCommand&>(imsg) ));
    omsg->__set__disableOvUv                    ( imsg.getDisableOvUv() );

    return omsg;
}


MessagePtr<::VerifyBanks>::type populateMessage( const Thrift::CCVerifyBanks& imsg )
{
    MessagePtr<::VerifyBanks>::type omsg( new ::VerifyBanks );

    static_cast<::ItemCommand&>(*omsg)          = *populateMessage( imsg._baseMessage );
    omsg->setDisableOvUv                        ( imsg._disableOvUv );

    return omsg;
}

//=============================================================================
//  CCVerifyInactiveBanks
//=============================================================================

MessagePtr<Thrift::CCVerifyInactiveBanks>::type populateThrift( const ::VerifyInactiveBanks& imsg )
{
    MessagePtr<Thrift::CCVerifyInactiveBanks>::type omsg( new Thrift::CCVerifyInactiveBanks );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::VerifyBanks&>(imsg) ));
    omsg->__set__bankInactiveTime               ( imsg.getBankInactiveTime() );

    return omsg;
}

MessagePtr<::VerifyInactiveBanks>::type populateMessage( const Thrift::CCVerifyInactiveBanks& imsg )
{
    MessagePtr<::VerifyInactiveBanks>::type omsg( new ::VerifyInactiveBanks );

    static_cast<::VerifyBanks&>(*omsg)          = *populateMessage( imsg._baseMessage );
    omsg->setBankInactiveTime                   ( imsg._bankInactiveTime );

    return omsg;
}

//=============================================================================
//  CCVerifySelectedBank
//=============================================================================

MessagePtr<Thrift::CCVerifySelectedBank>::type populateThrift( const ::VerifySelectedBank& imsg )
{
    MessagePtr<Thrift::CCVerifySelectedBank>::type omsg( new Thrift::CCVerifySelectedBank );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::VerifyBanks&>(imsg) ));
    omsg->__set__bankId                         ( imsg.getBankId() );

    return omsg;
}

MessagePtr<::VerifySelectedBank>::type populateMessage( const Thrift::CCVerifySelectedBank& imsg )
{
    MessagePtr<::VerifySelectedBank>::type omsg( new ::VerifySelectedBank );

    static_cast<::VerifyBanks&>(*omsg)          = *populateMessage( imsg._baseMessage );
    omsg->setBankId                             ( imsg._bankId );

    return omsg;
}

//=============================================================================
//  CCPao
//=============================================================================

MessagePtr<Thrift::CCPao>::type populateThrift( const ::CapControlPao& imsg )
{
    MessagePtr<Thrift::CCPao>::type omsg( new Thrift::CCPao );

    omsg->__set__paoId                          ( imsg.getPaoId() );
    omsg->__set__paoCategory                    ( imsg.getPaoCategory() );
    omsg->__set__paoClass                       ( imsg.getPaoClass() );
    omsg->__set__paoName                        ( imsg.getPaoName() );
    omsg->__set__paoType                        ( imsg.getPaoType() );
    omsg->__set__paoDescription                 ( imsg.getPaoDescription() );
    omsg->__set__disableFlag                    ( imsg.getDisableFlag() );

    return omsg;
}

}
}
}
