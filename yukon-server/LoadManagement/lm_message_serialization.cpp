#include "precompiled.h"

#include "message_serialization.h"
#include "message_serialization_util.h"
#include "lm_program_serialization.h"
#include "lm_message_serialization.h"

using namespace std;

namespace Cti {
namespace Messaging {
namespace Serialization {

namespace {

struct LMMessageFactoryRegister
{
    LMMessageFactoryRegister()
    {
        g_messageFactory.registerSerializer <::CtiLMMessage,                   Thrift::LMMessage>                ( &populateThrift, &populateMessage, "LMMessage" );
        g_messageFactory.registerSerializer <::CtiLMCommand,                   Thrift::LMCommand>                ( &populateThrift, &populateMessage, "LMCommand" );
        g_messageFactory.registerSerializer <::CtiLMManualControlRequest,      Thrift::LMManualControlRequest>   ( &populateThrift, &populateMessage, "LMManualControlRequest" );
        g_messageFactory.registerSerializer <::CtiLMManualControlResponse,     Thrift::LMManualControlResponse>  ( &populateThrift, &populateMessage, "LMManualControlResponse" );
        g_messageFactory.registerSerializer <::CtiLMEnergyExchangeControlMsg,  Thrift::LMEnergyExchangeControl>  ( &populateThrift, &populateMessage, "LMEnergyExchangeControl" );
        g_messageFactory.registerSerializer <::CtiLMEnergyExchangeAcceptMsg,   Thrift::LMEnergyExchangeAccept>   ( &populateThrift, &populateMessage, "LMEnergyExchangeAccept" );
        g_messageFactory.registerSerializer <::CtiLMControlAreaMsg,            Thrift::LMControlAreas>           ( &populateThrift, NULL,         "LMControlAreas" );
        g_messageFactory.registerSerializer <::CtiLMCurtailmentAcknowledgeMsg, Thrift::LMCurtailmentAcknowledge> ( &populateThrift, &populateMessage, "LMCurtailmentAcknowledge" );
        g_messageFactory.registerSerializer <::CtiLMDynamicGroupDataMsg,       Thrift::LMDynamicGroupData>       ( &populateThrift, NULL,         "LMDynamicGroupData" );
        g_messageFactory.registerSerializer <::CtiLMDynamicProgramDataMsg,     Thrift::LMDynamicProgramData>     ( &populateThrift, NULL,         "LMDynamicProgramData" );
        g_messageFactory.registerSerializer <::CtiLMDynamicTriggerDataMsg,     Thrift::LMDynamicTriggerData>     ( &populateThrift, NULL,         "LMDynamicTriggerData" );
        g_messageFactory.registerSerializer <::CtiLMDynamicControlAreaDataMsg, Thrift::LMDynamicControlAreaData> ( &populateThrift, NULL,         "LMDynamicControlAreaData" );
    }
};

const LMMessageFactoryRegister g_lmMessageFactoryRegister;

// populateThrift program base pointer (boost::shared_ptr) to generic message
Thrift::GenericMessage serializeGenericProgramPtr( const ::CtiLMProgramBaseSPtr program )
{
    return serializeGeneric( *program, g_lmProgramFactory );
}

}

//=============================================================================
//  LMMessage
//=============================================================================

MessagePtr<Thrift::LMMessage>::type populateThrift( const ::CtiLMMessage& imsg )
{
    MessagePtr<Thrift::LMMessage>::type omsg( new Thrift::LMMessage );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CtiMessage&>(imsg) ));
    omsg->__set__message                        ( imsg.getMessage() );

    return omsg;
}

MessagePtr<::CtiLMMessage>::type populateMessage( const Thrift::LMMessage& imsg )
{
    MessagePtr<::CtiLMMessage>::type omsg( new ::CtiLMMessage(
                                                  imsg._message ));

    static_cast<::CtiMessage&>(*omsg)           = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  LMCommand
//=============================================================================

MessagePtr<Thrift::LMCommand>::type populateThrift( const ::CtiLMCommand& imsg )
{
    MessagePtr<Thrift::LMCommand>::type omsg( new Thrift::LMCommand );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CtiLMMessage&>(imsg) ));
    omsg->__set__command                        ( imsg.getCommand() );
    omsg->__set__paoId                          ( imsg.getPAOId() );
    omsg->__set__number                         ( imsg.getNumber() );
    omsg->__set__value                          ( imsg.getValue() );
    omsg->__set__count                          ( imsg.getCount() );
    omsg->__set__auxId                          ( imsg.getAuxId() );

    return omsg;
}

MessagePtr<::CtiLMCommand>::type populateMessage( const Thrift::LMCommand& imsg )
{
    MessagePtr<::CtiLMCommand>::type omsg( new ::CtiLMCommand(
                                                  imsg._command,
                                                  imsg._paoId,
                                                  imsg._number,
                                                  imsg._value,
                                                  imsg._count,
                                                  imsg._auxId ));

    static_cast<::CtiLMMessage&>(*omsg)         = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  LMManualControlRequest
//=============================================================================

MessagePtr<Thrift::LMManualControlRequest>::type populateThrift( const ::CtiLMManualControlRequest& imsg )
{
    MessagePtr<Thrift::LMManualControlRequest>::type omsg( new Thrift::LMManualControlRequest );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CtiLMMessage&>(imsg) ));
    omsg->__set__command                        ( imsg.getCommand() );
    omsg->__set__paoId                          ( imsg.getPAOId() );
    omsg->__set__notifyTime                     ( CtiTimeToMilliseconds( imsg.getNotifyTime() ));
    omsg->__set__startTime                      ( CtiTimeToMilliseconds( imsg.getStartTime() ));
    omsg->__set__stopTime                       ( CtiTimeToMilliseconds( imsg.getStopTime() ));
    omsg->__set__startGear                      ( imsg.getStartGear() );
    omsg->__set__startPriority                  ( imsg.getStartPriority() );
    omsg->__set__additionalInfo                 ( imsg.getAdditionalInfo() );
    omsg->__set__constraintCmd                  ( imsg.getConstraintCmd() );
    omsg->__set__originSource                   ( imsg.getOrigin() );

    return omsg;
}

MessagePtr<::CtiLMManualControlRequest>::type populateMessage( const Thrift::LMManualControlRequest& imsg )
{
    MessagePtr<::CtiLMManualControlRequest>::type omsg( new ::CtiLMManualControlRequest(
                                                  imsg._command,
                                                  imsg._paoId,
                                                  MillisecondsToCtiTime( imsg._notifyTime ),
                                                  MillisecondsToCtiTime( imsg._startTime ),
                                                  MillisecondsToCtiTime( imsg._stopTime ),
                                                  imsg._startGear,
                                                  imsg._startPriority,
                                                  imsg._additionalInfo,
                                                  imsg._constraintCmd,
                                                  imsg._originSource));

    static_cast<::CtiLMMessage&>(*omsg)         = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  LMManualControlResponse
//=============================================================================

MessagePtr<Thrift::LMManualControlResponse>::type populateThrift( const ::CtiLMManualControlResponse& imsg )
{
    MessagePtr<Thrift::LMManualControlResponse>::type omsg( new Thrift::LMManualControlResponse );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CtiLMMessage&>(imsg) ));
    omsg->__set__paoId                          ( imsg.getPAOId() );
    omsg->__set__constraintViolations           ( transformContainer<vector<Thrift::LMConstraintViolation>>( imsg.getConstraintViolations(), populateThrift ));
    omsg->__set__bestFitAction                  ( imsg.getBestFitAction() );

    return omsg;
}

MessagePtr<::CtiLMManualControlResponse>::type populateMessage( const Thrift::LMManualControlResponse& imsg )
{
    MessagePtr<::CtiLMManualControlResponse>::type omsg( new ::CtiLMManualControlResponse );

    omsg->setPAOId                              ( imsg._paoId );
    omsg->setConstraintViolations               ( transformContainer<vector<::ConstraintViolation>>( imsg._constraintViolations, populateMessage ));
    omsg->setBestFitAction                      ( imsg._bestFitAction );
    static_cast<::CtiLMMessage&>(*omsg)         = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  LMConstraintViolation
//=============================================================================

MessagePtr<Thrift::LMConstraintViolation>::type populateThrift( const ::ConstraintViolation& imsg )
{
    MessagePtr<Thrift::LMConstraintViolation>::type omsg( new Thrift::LMConstraintViolation );

    omsg->__set__errorCode                      ( imsg.getErrorCode() );
    omsg->__set__doubleParams                   ( imsg.getDoubleParams() );
    omsg->__set__integerParams                  ( imsg.getIntegerParams() );
    omsg->__set__stringParams                   ( imsg.getStringParams() );
    omsg->__set__datetimeParams                 ( transformContainer<vector<int64_t>>( imsg.getDateTimeParams(), CtiTimeToMilliseconds ));

    return omsg;
}

MessagePtr<::ConstraintViolation>::type populateMessage( const Thrift::LMConstraintViolation& imsg )
{
    MessagePtr<::ConstraintViolation>::type omsg( new ::ConstraintViolation(
                                                  imsg._errorCode,
                                                  imsg._doubleParams,
                                                  imsg._integerParams,
                                                  imsg._stringParams,
                                                  transformContainer<vector<CtiTime>>( imsg._datetimeParams, MillisecondsToCtiTime )));

    return omsg;
}

//=============================================================================
//  LMEnergyExchangeControl
//=============================================================================

MessagePtr<Thrift::LMEnergyExchangeControl>::type populateThrift( const ::CtiLMEnergyExchangeControlMsg& imsg )
{
    MessagePtr<Thrift::LMEnergyExchangeControl>::type omsg( new Thrift::LMEnergyExchangeControl );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CtiLMMessage&>(imsg) ));
    omsg->__set__command                        ( imsg.getCommand() );
    omsg->__set__paoId                          ( imsg.getPAOId() );
    omsg->__set__offerId                        ( imsg.getOfferId() );
    omsg->__set__offerDate                      ( CtiTimeToMilliseconds( imsg.getOfferDate() ));
    omsg->__set__notificationDatetime           ( CtiTimeToMilliseconds( imsg.getNotificationDateTime() ));
    omsg->__set__expirationDatetime             ( CtiTimeToMilliseconds( imsg.getExpirationDateTime() ));
    omsg->__set__additionalInfo                 ( imsg.getAdditionalInfo() );
    omsg->__set__amountsRequested               ( imsg.getAmountsRequested() );
    omsg->__set__pricesOffered                  ( transformContainer<vector<int32_t>>( imsg.getPricesOffered() ));

    return omsg;
}

MessagePtr<::CtiLMEnergyExchangeControlMsg>::type populateMessage( const Thrift::LMEnergyExchangeControl& imsg )
{
    MessagePtr<::CtiLMEnergyExchangeControlMsg>::type omsg( new ::CtiLMEnergyExchangeControlMsg(
                                                  imsg._command,
                                                  imsg._paoId,
                                                  imsg._offerId,
                                                  MillisecondsToCtiTime( imsg._offerDate ),
                                                  MillisecondsToCtiTime( imsg._notificationDatetime ),
                                                  MillisecondsToCtiTime( imsg._expirationDatetime ),
                                                  imsg._additionalInfo,
                                                  imsg._amountsRequested,
                                                  transformContainer<vector<LONG>>( imsg._pricesOffered )));

    static_cast<::CtiLMMessage&>(*omsg)         = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  LMEnergyExchangeAccept
//=============================================================================

MessagePtr<Thrift::LMEnergyExchangeAccept>::type populateThrift( const ::CtiLMEnergyExchangeAcceptMsg& imsg )
{
    MessagePtr<Thrift::LMEnergyExchangeAccept>::type omsg( new Thrift::LMEnergyExchangeAccept );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CtiLMMessage&>(imsg) ));
    omsg->__set__paoId                          ( imsg.getPAOId() );
    omsg->__set__offerId                        ( imsg.getOfferId() );
    omsg->__set__revisionNumber                 ( imsg.getRevisionNumber() );
    omsg->__set__acceptStatus                   ( imsg.getAcceptStatus() );
    omsg->__set__ipAddressOfAcceptUser          ( imsg.getIPAddressOfAcceptUser() );
    omsg->__set__userIdName                     ( imsg.getUserIdName() );
    omsg->__set__nameOfAcceptPerson             ( imsg.getNameOfAcceptPerson() );
    omsg->__set__energyExchangeNotes            ( imsg.getEnergyExchangeNotes() );
    omsg->__set__amountsCommitted               ( imsg.getAmountsCommitted() );

    return omsg;
}

MessagePtr<::CtiLMEnergyExchangeAcceptMsg>::type populateMessage( const Thrift::LMEnergyExchangeAccept& imsg )
{
    MessagePtr<::CtiLMEnergyExchangeAcceptMsg>::type omsg( new ::CtiLMEnergyExchangeAcceptMsg(
                                                  imsg._paoId,
                                                  imsg._offerId,
                                                  imsg._revisionNumber,
                                                  imsg._acceptStatus,
                                                  imsg._ipAddressOfAcceptUser,
                                                  imsg._userIdName,
                                                  imsg._nameOfAcceptPerson,
                                                  imsg._energyExchangeNotes,
                                                  imsg._amountsCommitted ));

    static_cast<::CtiLMMessage&>(*omsg)         = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  LMControlAreas
//=============================================================================

MessagePtr<Thrift::LMControlAreas>::type populateThrift( const ::CtiLMControlAreaMsg& imsg )
{
    MessagePtr<Thrift::LMControlAreas>::type omsg( new Thrift::LMControlAreas );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CtiLMMessage&>(imsg) ));
    omsg->__set__msgInfoBitMask                 ( imsg.getMsgInfoBitMask() );
    omsg->__set__controlAreas                   ( transformContainer<vector<Thrift::LMControlAreaItem>>( *imsg.getControlAreas(), populateThrift ));

    return omsg;
}

//=============================================================================
//  LMControlAreaItem
//=============================================================================

MessagePtr<Thrift::LMControlAreaItem>::type populateThrift( const ::CtiLMControlArea& imsg )
{
    MessagePtr<Thrift::LMControlAreaItem>::type omsg( new Thrift::LMControlAreaItem );

    omsg->__set__paoId                          ( imsg.getPAOId() );
    omsg->__set__paoCategory                    ( imsg.getPAOCategory() );
    omsg->__set__paoClass                       ( imsg.getPAOClass() );
    omsg->__set__paoName                        ( imsg.getPAOName() );
    omsg->__set__paoTypeString                  ( imsg.getPAOTypeString() );
    omsg->__set__paoDescription                 ( imsg.getPAODescription() );
    omsg->__set__disableFlag                    ( imsg.getDisableFlag() );
    omsg->__set__defOperationalState            ( imsg.getDefOperationalState() );
    omsg->__set__controlInterval                ( imsg.getControlInterval() );
    omsg->__set__minResponseTime                ( imsg.getMinResponseTime() );
    omsg->__set__defDailyStartTime              ( imsg.getDefStartSecondsFromDayBegin() );
    omsg->__set__defDailyStopTime               ( imsg.getDefStopSecondsFromDayBegin() );
    omsg->__set__requireAllTriggersActiveFlag   ( imsg.getRequireAllTriggersActiveFlag() );
    omsg->__set__nextCheckTime                  ( CtiTimeToMilliseconds( imsg.getNextCheckTime() ));
    omsg->__set__newPointDataReceivedFlag       ( imsg.getNewPointDataReceivedFlag() );
    omsg->__set__updatedFlag                    ( imsg.getUpdatedFlag() );
    omsg->__set__controlAreaStatusPointId       ( imsg.getControlAreaStatusPointId() );
    omsg->__set__controlAreaState               ( imsg.getControlAreaState() );
    omsg->__set__currentPriority                ( imsg.getCurrentPriority() );
    omsg->__set__currentDailyStartTime          ( imsg.getCurrentStartSecondsFromDayBegin() );
    omsg->__set__currentDailyStopTime           ( imsg.getCurrentStopSecondsFromDayBegin() );
    omsg->__set__lmControlAreaTriggers          ( transformContainer<vector<Thrift::LMControlAreaTrigger>>( imsg.getLMControlAreaTriggers(), populateThrift ));
    omsg->__set__lmPrograms                     ( transformContainer<vector<Thrift::GenericMessage>>( imsg.getLMPrograms(), serializeGenericProgramPtr ));

    return omsg;
}

//=============================================================================
//  LMControlAreaTrigger
//=============================================================================

MessagePtr<Thrift::LMControlAreaTrigger>::type populateThrift( const ::CtiLMControlAreaTrigger& imsg )
{
    MessagePtr<Thrift::LMControlAreaTrigger>::type omsg( new Thrift::LMControlAreaTrigger );

    omsg->__set__paoId                          ( imsg.getPAOId() );
    omsg->__set__triggerNumber                  ( imsg.getTriggerNumber() );
    omsg->__set__triggerType                    ( imsg.getTriggerType() );
    omsg->__set__pointId                        ( imsg.getPointId() );
    omsg->__set__pointValue                     ( imsg.getPointValue() );
    omsg->__set__lastPointValueTimestamp        ( CtiTimeToMilliseconds( imsg.getLastPointValueTimestamp() ));
    omsg->__set__normalState                    ( imsg.getNormalState() );
    omsg->__set__threshold                      ( imsg.getThreshold() );
    omsg->__set__projectionType                 ( imsg.getProjectionType() );
    omsg->__set__projectionPoints               ( imsg.getProjectionPoints() );
    omsg->__set__projectAheadDuration           ( imsg.getProjectAheadDuration() );
    omsg->__set__thresholdKickPercent           ( imsg.getThresholdKickPercent() );
    omsg->__set__minRestoreOffset               ( imsg.getMinRestoreOffset() );
    omsg->__set__peakPointId                    ( imsg.getPeakPointId() );
    omsg->__set__peakPointValue                 ( imsg.getPeakPointValue() );
    omsg->__set__lastPeakPointValueTimestamp    ( CtiTimeToMilliseconds( imsg.getLastPeakPointValueTimestamp() ));
    omsg->__set__projectedPointValue            ( imsg.getProjectedPointValue() );

    return omsg;
}

//=============================================================================
//  LMCurtailmentAcknowledge
//=============================================================================

MessagePtr<Thrift::LMCurtailmentAcknowledge>::type populateThrift( const ::CtiLMCurtailmentAcknowledgeMsg& imsg )
{
    MessagePtr<Thrift::LMCurtailmentAcknowledge>::type omsg( new Thrift::LMCurtailmentAcknowledge );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CtiLMMessage&>(imsg) ));
    omsg->__set__paoId                          ( imsg.getPAOId() );
    omsg->__set__curtailReferenceId             ( imsg.getCurtailReferenceId() );
    omsg->__set__acknowledgeStatus              ( imsg.getAcknowledgeStatus() );
    omsg->__set__ipAddressOfAckUser             ( imsg.getIPAddressOfAckUser() );
    omsg->__set__userIdName                     ( imsg.getUserIdName() );
    omsg->__set__nameOfAckPerson                ( imsg.getNameOfAckPerson() );
    omsg->__set__curtailmentNotes               ( imsg.getCurtailmentNotes() );

    return omsg;
}

MessagePtr<::CtiLMCurtailmentAcknowledgeMsg>::type populateMessage( const Thrift::LMCurtailmentAcknowledge& imsg )
{
    MessagePtr<::CtiLMCurtailmentAcknowledgeMsg>::type omsg( new ::CtiLMCurtailmentAcknowledgeMsg(
                                                  imsg._paoId,
                                                  imsg._curtailReferenceId,
                                                  imsg._acknowledgeStatus,
                                                  imsg._ipAddressOfAckUser,
                                                  imsg._userIdName,
                                                  imsg._nameOfAckPerson,
                                                  imsg._curtailmentNotes ));

    static_cast<::CtiLMMessage&>(*omsg)         = *populateMessage( imsg._baseMessage );

    return omsg;
}

//=============================================================================
//  LMDynamicGroupData
//=============================================================================

MessagePtr<Thrift::LMDynamicGroupData>::type populateThrift( const ::CtiLMDynamicGroupDataMsg& imsg )
{
    MessagePtr<Thrift::LMDynamicGroupData>::type omsg( new Thrift::LMDynamicGroupData );

    omsg->__set__paoId                          ( imsg.getPaoId() );
    omsg->__set__disableFlag                    ( imsg.getDisableFlag() );
    omsg->__set__groupControlState              ( imsg.getGroupControlState() );
    omsg->__set__currentHoursDaily              ( imsg.getCurrentHoursDaily() );
    omsg->__set__currentHoursMonthly            ( imsg.getCurrentHoursMonthly() );
    omsg->__set__currentHoursSeasonal           ( imsg.getCurrentHoursSeasonal() );
    omsg->__set__currentHoursAnnually           ( imsg.getCurrentHoursAnnually() );
    omsg->__set__lastControlSent                ( CtiTimeToMilliseconds( imsg.getLastControlSent() ));
    omsg->__set__controlStartTime               ( CtiTimeToMilliseconds( imsg.getControlStartTime() ));
    omsg->__set__controlCompleteTime            ( CtiTimeToMilliseconds( imsg.getControlCompleteTime() ));
    omsg->__set__nextControlTime                ( CtiTimeToMilliseconds( imsg.getNextControlTime() ));
    omsg->__set__internalState                  ( imsg.getInternalState() ); //What the heck is this???
    omsg->__set__dailyOps                       ( imsg.getDailyOps() );

    return omsg;
}

//=============================================================================
//  LMDynamicProgramData
//=============================================================================

MessagePtr<Thrift::LMDynamicProgramData>::type populateThrift( const ::CtiLMDynamicProgramDataMsg& imsg )
{
    MessagePtr<Thrift::LMDynamicProgramData>::type omsg( new Thrift::LMDynamicProgramData );

    omsg->__set__paoId                          ( imsg.getPaoId() );
    omsg->__set__disableFlag                    ( imsg.getDisableFlag() );
    omsg->__set__currentGearNumber              ( imsg.getCurrentGearNumber() + 1 );
    omsg->__set__lastGroupControlled            ( imsg.getLastGroupControlled() );
    omsg->__set__programState                   ( imsg.getProgramState() );
    omsg->__set__reductionTotal                 ( imsg.getReductionTotal() );
    omsg->__set__directStartTime                ( CtiTimeToMilliseconds( imsg.getDirectStartTime() ));
    omsg->__set__directStopTime                 ( CtiTimeToMilliseconds( imsg.getDirectStopTime() ));
    omsg->__set__notifyActiveTime               ( CtiTimeToMilliseconds( imsg.getNotifyActiveTime() ));
    omsg->__set__notifyInactiveTime             ( CtiTimeToMilliseconds( imsg.getNotifyInactiveTime() ));
    omsg->__set__startedRampingOutTime          ( CtiTimeToMilliseconds( imsg.getStartedRampingOutTime() ));
    omsg->__set__originSource                   ( imsg.getOrigin() );

    return omsg;
}

//=============================================================================
//  LMDynamicControlAreaData
//=============================================================================

MessagePtr<Thrift::LMDynamicControlAreaData>::type populateThrift( const ::CtiLMDynamicControlAreaDataMsg& imsg )
{
    MessagePtr<Thrift::LMDynamicControlAreaData>::type omsg( new Thrift::LMDynamicControlAreaData );

    omsg->__set__paoId                          ( imsg.getPaoId() );
    omsg->__set__disableFlag                    ( imsg.getDisableFlag() );
    omsg->__set__nextCheckTime                  ( CtiTimeToMilliseconds( imsg.getNextCheckTime() ));
    omsg->__set__controlAreaState               ( imsg.getControlAreaState() );
    omsg->__set__currentPriority                ( imsg.getCurrentPriority() );
    omsg->__set__currentDailyStartTime          ( imsg.getCurrentDailyStartTime() );
    omsg->__set__currentDailyStopTime           ( imsg.getCurrentDailyStopTime() );
    omsg->__set__triggers                       ( transformContainer<vector<Thrift::LMDynamicTriggerData>>( imsg.getTriggers(), populateThrift ));

    return omsg;
}

//=============================================================================
//  LMDynamicTriggerDataMsg
//=============================================================================

MessagePtr<Thrift::LMDynamicTriggerData>::type populateThrift( const ::CtiLMDynamicTriggerDataMsg& imsg )
{
    MessagePtr<Thrift::LMDynamicTriggerData>::type omsg( new Thrift::LMDynamicTriggerData );

    omsg->__set__paoId                          ( imsg.gePaoId() );
    omsg->__set__triggerNumber                  ( imsg.getTriggerNumber() );
    omsg->__set__pointValue                     ( imsg.getPointValue() );
    omsg->__set__lastPointValueTimestamp        ( CtiTimeToMilliseconds( imsg.getLastPointValueTimestamp() ));
    omsg->__set__normalState                    ( imsg.getNormalState() );
    omsg->__set__threshold                      ( imsg.getThreshold() );
    omsg->__set__peakPointValue                 ( imsg.getPeakPointValue() );
    omsg->__set__lastPeakPointValueTimestamp    ( CtiTimeToMilliseconds( imsg.getLastPeakPointValueTimestamp() ));
    omsg->__set__projectedPointValue            ( imsg.getProjectedPointValue() );

    return omsg;
}


}
}
}
