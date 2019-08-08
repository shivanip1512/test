#include "precompiled.h"

#include "message_serialization_util.h"
#include "lm_group_serialization.h"
#include "lm_program_serialization.h"

using namespace std;

namespace Cti {
namespace Messaging {
namespace Serialization {

MessageFactory<CtiLMProgramBase> g_lmProgramFactory(::Cti::Messaging::ActiveMQ::MessageType::prefix);

namespace {

struct LMProgramFactoryRegister
{
    LMProgramFactoryRegister()
    {
        g_lmProgramFactory.registerSerializer <::CtiLMProgramCurtailment,    Thrift::LMProgramCurtailment>    ( &populateThrift, NULL, "LMProgramCurtailment" );
        g_lmProgramFactory.registerSerializer <::CtiLMProgramDirect,         Thrift::LMProgramDirect>         ( &populateThrift, NULL, "LMProgramDirect" );
        g_lmProgramFactory.registerSerializer <::CtiLMProgramEnergyExchange, Thrift::LMProgramEnergyExchange> ( &populateThrift, NULL, "LMProgramEnergyExchange" );
    }
};

const LMProgramFactoryRegister g_lmProgramFactoryRegister;

// serialize group base pointer (boost::shared_ptr) to generic message
Thrift::GenericMessage serializeGenericGroupPtr( const ::CtiLMGroupPtr group )
{
    return serializeGeneric( *group, g_lmGroupFactory );
}

// serialize program base to generic message
Thrift::GenericMessage serializeGenericProgram( const ::CtiLMProgramBase& program )
{
    return serializeGeneric( program, g_lmProgramFactory );
}

}

//=============================================================================
//  LMProgramBase
//=============================================================================

MessagePtr<Thrift::LMProgramBase>::type populateThrift( const ::CtiLMProgramBase& imsg )
{
    MessagePtr<Thrift::LMProgramBase>::type omsg( new Thrift::LMProgramBase );

    omsg->__set__paoId                          ( imsg.getPAOId() );
    omsg->__set__paoCategory                    ( imsg.getPAOCategory() );
    omsg->__set__paoClass                       ( imsg.getPAOClass() );
    omsg->__set__paoName                        ( imsg.getPAOName() );
    omsg->__set__paoTypeString                  ( imsg.getPAOTypeString() );
    omsg->__set__paoDescription                 ( imsg.getPAODescription() );
    omsg->__set__disableFlag                    ( imsg.getDisableFlag() );
    omsg->__set__startPriority                  ( imsg.getStartPriority() );
    omsg->__set__stopPriority                   ( imsg.getStopPriority() );
    omsg->__set__controlType                    ( imsg.getControlType() );
    omsg->__set__availableWeekdays              ( imsg.getAvailableWeekDays() );
    omsg->__set__maxHoursDaily                  ( imsg.getMaxHoursDaily() );
    omsg->__set__maxHoursMonthly                ( imsg.getMaxHoursMonthly() );
    omsg->__set__maxHoursSeasonal               ( imsg.getMaxHoursSeasonal() );
    omsg->__set__maxHoursAnnually               ( imsg.getMaxHoursAnnually() );
    omsg->__set__minActivateTime                ( imsg.getMinActivateTime() );
    omsg->__set__minRestartTime                 ( imsg.getMinRestartTime() );
    omsg->__set__programStatusPointId           ( imsg.getProgramStatusPointId() );
    omsg->__set__programState                   ( imsg.getProgramState() );
    omsg->__set__reductionAnalogPointId         ( imsg.getReductionAnalogPointId() );
    omsg->__set__reductionTotal                 ( imsg.getReductionTotal() );
    omsg->__set__startedControlling             ( CtiTimeToMilliseconds( imsg.getStartedControlling() ));
    omsg->__set__lastControlSent                ( CtiTimeToMilliseconds( imsg.getLastControlSent() ));
    omsg->__set__manualControlReceivedFlag      ( imsg.getManualControlReceivedFlag() );
    omsg->__set__lmProgramControlWindows        ( transformContainer<vector<Thrift::LMProgramControlWindow>>( imsg.getLMProgramControlWindows(), populateThrift ));

    return omsg;
}

//=============================================================================
//  LMProgramControlWindow
//=============================================================================

MessagePtr<Thrift::LMProgramControlWindow>::type populateThrift( const ::CtiLMProgramControlWindow& imsg )
{
    MessagePtr<Thrift::LMProgramControlWindow>::type omsg( new Thrift::LMProgramControlWindow );

    omsg->__set__paoId                          ( imsg.getPAOId() );
    omsg->__set__windowNumber                   ( imsg.getWindowNumber() );
    omsg->__set__availableStartTime             ( imsg.getAvailableStartTimeDaily() );
    omsg->__set__availableStopTime              ( imsg.getAvailableStopTimeDaily() );

    return omsg;
}

//=============================================================================
//  LMProgramCurtailment
//=============================================================================

MessagePtr<Thrift::LMProgramCurtailment>::type populateThrift( const ::CtiLMProgramCurtailment& imsg )
{
    MessagePtr<Thrift::LMProgramCurtailment>::type omsg( new Thrift::LMProgramCurtailment );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CtiLMProgramBase&>(imsg) ));
    omsg->__set__minNotifyTime                  ( imsg.getMinNotifyTime() );
    omsg->__set__heading                        ( imsg.getHeading() );
    omsg->__set__messageHeader                  ( imsg.getMessageHeader() );
    omsg->__set__messageFooter                  ( imsg.getMessageFooter() );
    omsg->__set__acktimeLimit                   ( imsg.getAckTimeLimit() );
    omsg->__set__canceledMsg                    ( imsg.getCanceledMsg() );
    omsg->__set__stoppedEarlyMsg                ( imsg.getStoppedEarlyMsg() );
    omsg->__set__curtailReferenceId             ( imsg.getCurtailReferenceId() );
    omsg->__set__actionDateTime                 ( CtiTimeToMilliseconds( imsg.getActionDateTime() ));
    omsg->__set__notificationDateTime           ( CtiTimeToMilliseconds( imsg.getNotificationDateTime() ));
    omsg->__set__curtailmentStartTime           ( CtiTimeToMilliseconds( imsg.getCurtailmentStartTime() ));
    omsg->__set__curtailmentStopTime            ( CtiTimeToMilliseconds( imsg.getCurtailmentStopTime() ));
    omsg->__set__runStatus                      ( imsg.getRunStatus() );
    omsg->__set__additionalInfo                 ( imsg.getAdditionalInfo() );
    omsg->__set__lmProgramCurtailmentCustomers  ( transformContainer<vector<Thrift::LMCurtailCustomer>>( imsg.getLMProgramCurtailmentCustomers(), populateThrift ));

    return omsg;
}

//=============================================================================
//  LMCurtailCustomer
//=============================================================================

MessagePtr<Thrift::LMCurtailCustomer>::type populateThrift( const ::CtiLMCurtailCustomer& imsg )
{
    MessagePtr<Thrift::LMCurtailCustomer>::type omsg( new Thrift::LMCurtailCustomer );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CtiLMCICustomerBase&>(imsg) ));
    omsg->__set__requireAck                     ( imsg.getRequireAck() );
    omsg->__set__curtailReferenceId             ( imsg.getCurtailReferenceId() );
    omsg->__set__acknowledgeStatus              ( imsg.getAcknowledgeStatus() );
    omsg->__set__ackDatetime                    ( CtiTimeToMilliseconds( imsg.getAckDateTime() ));
    omsg->__set__ipAddressOfAckUser             ( imsg.getIPAddressOfAckUser() );
    omsg->__set__userIdName                     ( imsg.getUserIdName() );
    omsg->__set__nameOfAckPerson                ( imsg.getNameOfAckPerson() );
    omsg->__set__curtailmentNotes               ( imsg.getCurtailmentNotes() );
    omsg->__set__ackLateFlag                    ( imsg.getAckLateFlag() );

    return omsg;
}

//=============================================================================
//  LMCurtailCustomerBase
//=============================================================================

MessagePtr<Thrift::LMCICustomerBase>::type populateThrift( const ::CtiLMCICustomerBase& imsg )
{
    MessagePtr<Thrift::LMCICustomerBase>::type omsg( new Thrift::LMCICustomerBase );

    omsg->__set__customerId                     ( imsg.getCustomerId() );
    omsg->__set__companyName                    ( imsg.getCompanyName() );
    omsg->__set__customerDemandLevel            ( imsg.getCustomerDemandLevel() );
    omsg->__set__curtailAmount                  ( imsg.getCurtailAmount() );
    omsg->__set__curtailmentAgreement           ( imsg.getCurtailmentAgreement() );
    omsg->__set__timeZone                       ( imsg.getTimeZone() );
    omsg->__set__customerOrder                  ( imsg.getCustomerOrder() );

    return omsg;
}

//=============================================================================
//  LMProgramDirect
//=============================================================================

MessagePtr<Thrift::LMProgramDirect>::type populateThrift( const ::CtiLMProgramDirect& imsg )
{
    MessagePtr<Thrift::LMProgramDirect>::type omsg( new Thrift::LMProgramDirect );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CtiLMProgramBase&>(imsg) ));
    omsg->__set__currentGearNumber              ( imsg.getCurrentGearNumber() + 1 );
    omsg->__set__lastGroupControlled            ( imsg.getLastGroupControlled() );
    omsg->__set__directStartTime                ( CtiTimeToMilliseconds( imsg.getDirectStartTime() ));
    omsg->__set__directstopTime                 ( CtiTimeToMilliseconds( imsg.getDirectStopTime() ));
    omsg->__set__notifyActiveTime               ( CtiTimeToMilliseconds( imsg.getNotifyActiveTime() ));
    omsg->__set__notifyInactiveTime             ( CtiTimeToMilliseconds( imsg.getNotifyInactiveTime() ));
    omsg->__set__startedRampingOut              ( CtiTimeToMilliseconds( imsg.getStartedRampingOutTime() ));
    omsg->__set__triggerOffset                  ( imsg.getTriggerOffset() );
    omsg->__set__triggerRestoreOffset           ( imsg.getTriggerRestoreOffset() );
    omsg->__set__constraintOverride             ( imsg.getConstraintOverride() );
    omsg->__set__lmProgramDirectGears           ( transformContainer<vector<Thrift::LMProgramDirectGear>>( imsg.getLMProgramDirectGears(), populateThrift ));
    omsg->__set__lmProgramDirectGroups          ( transformContainer<vector<Thrift::GenericMessage>>( imsg.getLMProgramDirectGroups(), serializeGenericGroupPtr ));

    // Only send active master/subordinate programs
    // Since Thrift doesn't support struct tree, we are forced to use generic

    vector<Thrift::GenericMessage> activeMasters;
    for each( const CtiLMProgramDirectSPtr prog in imsg.getMasterPrograms() )
    {
        if( prog->getProgramState() != CtiLMProgramBase::InactiveState )
        {
            activeMasters.push_back( serializeGenericProgram( *prog ));
        }
    }
    omsg->__set__activeMasters                  ( activeMasters );

    vector<Thrift::GenericMessage> activeSubordinates;
    for each( const CtiLMProgramDirectSPtr prog in imsg.getSubordinatePrograms() )
    {
        if( prog->getProgramState() != CtiLMProgramBase::InactiveState )
        {
            activeSubordinates.push_back( serializeGenericProgram( *prog ));
        }
    }
    omsg->__set__activeSubordinates             ( activeSubordinates );
    omsg->__set__originSource                   ( imsg.getOrigin() );

    return omsg;
}

//=============================================================================
//  LMProgramDirectGear
//=============================================================================

MessagePtr<Thrift::LMProgramDirectGear>::type populateThrift( const ::CtiLMProgramDirectGear& imsg )
{
    MessagePtr<Thrift::LMProgramDirectGear>::type omsg( new Thrift::LMProgramDirectGear );

    omsg->__set__programPaoId                   ( imsg.getProgramPAOId() );
    omsg->__set__gearName                       ( imsg.getGearName() );
    omsg->__set__gearNumber                     ( imsg.getGearNumber() );
    omsg->__set__controlMethod                  ( imsg.getControlMethod() );
    omsg->__set__methodRate                     ( imsg.getMethodRate() );
    omsg->__set__methodPeriod                   ( imsg.getMethodPeriod() );
    omsg->__set__methodRateCount                ( imsg.getMethodRateCount() );
    omsg->__set__cyclereFreshRate               ( imsg.getCycleRefreshRate() );
    omsg->__set__methodStopType                 ( imsg.getMethodStopType() );
    omsg->__set__changeCondition                ( imsg.getChangeCondition() );
    omsg->__set__changeDuration                 ( imsg.getChangeDuration() );
    omsg->__set__changePriority                 ( imsg.getChangePriority() );
    omsg->__set__changeTriggerNumber            ( imsg.getChangeTriggerNumber() );
    omsg->__set__changeTriggerOffset            ( imsg.getChangeTriggerOffset() );
    omsg->__set__percentReduction               ( imsg.getPercentReduction() );
    omsg->__set__groupSelectionMethod           ( imsg.getGroupSelectionMethod() );
    omsg->__set__methodOptionType               ( imsg.getMethodOptionType() );
    omsg->__set__methodOptionMax                ( imsg.getMethodOptionMax() );
    omsg->__set__rampInInterval                 ( imsg.getRampInInterval() );
    omsg->__set__rampInPercent                  ( imsg.getRampInPercent() );
    omsg->__set__rampOutInterval                ( imsg.getRampOutInterval() );
    omsg->__set__rampOutPercent                 ( imsg.getRampOutPercent() );
    omsg->__set__kwReduction                    ( imsg.getKWReduction() );

    return omsg;
}

//=============================================================================
//  LMProgramEnergyExchange
//=============================================================================

MessagePtr<Thrift::LMProgramEnergyExchange>::type populateThrift( const ::CtiLMProgramEnergyExchange& imsg )
{
    MessagePtr<Thrift::LMProgramEnergyExchange>::type omsg( new Thrift::LMProgramEnergyExchange );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CtiLMProgramBase&>(imsg) ));
    omsg->__set__minNotifyTime                  ( imsg.getMinNotifyTime() );
    omsg->__set__heading                        ( imsg.getHeading() );
    omsg->__set__messageHeader                  ( imsg.getMessageHeader() );
    omsg->__set__messageFooter                  ( imsg.getMessageFooter() );
    omsg->__set__canceledMsg                    ( imsg.getCanceledMsg() );
    omsg->__set__stoppedEarlyMsg                ( imsg.getStoppedEarlyMsg() );
    omsg->__set__lmEnergyExchangeOffers         ( transformContainer<vector<Thrift::LMEnergyExchangeOffer>>( imsg.getLMEnergyExchangeOffers(), populateThrift ));
    omsg->__set__lmEnergyExchangeCustomers      ( transformContainer<vector<Thrift::LMEnergyExchangeCustomer>>( imsg.getLMEnergyExchangeCustomers(), populateThrift ));

    return omsg;
}

//=============================================================================
//  LMEnergyExchangeOffer
//=============================================================================

MessagePtr<Thrift::LMEnergyExchangeOffer>::type populateThrift( const ::CtiLMEnergyExchangeOffer& imsg )
{
    MessagePtr<Thrift::LMEnergyExchangeOffer>::type omsg( new Thrift::LMEnergyExchangeOffer );

    omsg->__set__paoId                          ( imsg.getPAOId() );
    omsg->__set__offerId                        ( imsg.getOfferId() );
    omsg->__set__runStatus                      ( imsg.getRunStatus() );
    omsg->__set__offerDate                      ( CtiTimeToMilliseconds( imsg.getOfferDate() ));
    omsg->__set__lmEnergyExchangeOfferRevisions ( transformContainer<vector<Thrift::LMEnergyExchangeOfferRevision>>( imsg.getLMEnergyExchangeOfferRevisions(), populateThrift ));

    return omsg;
}

//=============================================================================
//  LMEnergyExchangeOfferRevision
//=============================================================================

MessagePtr<Thrift::LMEnergyExchangeOfferRevision>::type populateThrift( const ::CtiLMEnergyExchangeOfferRevision& imsg )
{
    MessagePtr<Thrift::LMEnergyExchangeOfferRevision>::type omsg( new Thrift::LMEnergyExchangeOfferRevision );

    omsg->__set__offerId                        ( imsg.getOfferId() );
    omsg->__set__revisionNumber                 ( imsg.getRevisionNumber() );
    omsg->__set__actionDatetime                 ( CtiTimeToMilliseconds( imsg.getActionDateTime() ));
    omsg->__set__notificationDatetime           ( CtiTimeToMilliseconds( imsg.getNotificationDateTime() ));
    omsg->__set__offerexpirationDatetime        ( CtiTimeToMilliseconds( imsg.getOfferExpirationDateTime() ));
    omsg->__set__additionalInfo                 ( imsg.getAdditionalInfo() );
    omsg->__set__lmEnergyExchangeHourlyOffers   ( transformContainer<vector<Thrift::LMEnergyExchangeHourlyOffer>>( imsg.getLMEnergyExchangeHourlyOffers(), populateThrift ));

    return omsg;
}

//=============================================================================
//  LMEnergyExchangeHourlyOffer
//=============================================================================

MessagePtr<Thrift::LMEnergyExchangeHourlyOffer>::type populateThrift( const ::CtiLMEnergyExchangeHourlyOffer& imsg )
{
    MessagePtr<Thrift::LMEnergyExchangeHourlyOffer>::type omsg( new Thrift::LMEnergyExchangeHourlyOffer );

    omsg->__set__offerId                        ( imsg.getOfferId() );
    omsg->__set__revisionNumber                 ( imsg.getRevisionNumber() );
    omsg->__set__hour                           ( imsg.getHour() );
    omsg->__set__price                          ( imsg.getPrice() );
    omsg->__set__amountRequested                ( imsg.getAmountRequested() );

    return omsg;
}

//=============================================================================
//  LMEnergyExchangeCustomer
//=============================================================================

MessagePtr<Thrift::LMEnergyExchangeCustomer>::type populateThrift( const ::CtiLMEnergyExchangeCustomer& imsg )
{
    MessagePtr<Thrift::LMEnergyExchangeCustomer>::type omsg( new Thrift::LMEnergyExchangeCustomer );

    omsg->__set__baseMessage                    ( *populateThrift( static_cast<const ::CtiLMCICustomerBase&>(imsg) ));
    omsg->__set__lmEnergyExchangeCustomerReplies( transformContainer<vector<Thrift::LMEnergyExchangeCustomerReply>>( imsg.getLMEnergyExchangeCustomerReplies(), populateThrift ));

    return omsg;
}

//=============================================================================
//  LMEnergyExchangeCustomerReply
//=============================================================================

MessagePtr<Thrift::LMEnergyExchangeCustomerReply>::type populateThrift( const ::CtiLMEnergyExchangeCustomerReply& imsg )
{
    MessagePtr<Thrift::LMEnergyExchangeCustomerReply>::type omsg( new Thrift::LMEnergyExchangeCustomerReply );

    omsg->__set__customerId                     ( imsg.getCustomerId() );
    omsg->__set__offerId                        ( imsg.getOfferId() );
    omsg->__set__acceptStatus                   ( imsg.getAcceptStatus() );
    omsg->__set__acceptDatetime                 ( CtiTimeToMilliseconds( imsg.getAcceptDateTime() ));
    omsg->__set__revisionNumber                 ( imsg.getRevisionNumber() );
    omsg->__set__ipAddressOfAcceptUser          ( imsg.getIPAddressOfAcceptUser() );
    omsg->__set__userIdName                     ( imsg.getUserIdName() );
    omsg->__set__nameOfAcceptPerson             ( imsg.getNameOfAcceptPerson() );
    omsg->__set__energyExchangeNotes            ( imsg.getEnergyExchangeNotes() );
    omsg->__set__lmEnergyExchangeHourlyCustomers( transformContainer<vector<Thrift::LMEnergyExchangeHourlyCustomer>>( imsg.getLMEnergyExchangeHourlyCustomers(), populateThrift ));

    return omsg;
}

//=============================================================================
//  LMEnergyExchangeCustomerReply
//=============================================================================

MessagePtr<Thrift::LMEnergyExchangeHourlyCustomer>::type populateThrift( const ::CtiLMEnergyExchangeHourlyCustomer& imsg )
{
    MessagePtr<Thrift::LMEnergyExchangeHourlyCustomer>::type omsg( new Thrift::LMEnergyExchangeHourlyCustomer );

    omsg->__set__customerId                     ( imsg.getCustomerId() );
    omsg->__set__offerId                        ( imsg.getOfferId() );
    omsg->__set__revisionNumber                 ( imsg.getRevisionNumber() );
    omsg->__set__hour                           ( imsg.getHour() );
    omsg->__set__amountCommitted                ( imsg.getAmountCommitted() );

    return omsg;
}


}
}
}
