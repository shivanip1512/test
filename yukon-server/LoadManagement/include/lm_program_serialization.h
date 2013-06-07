#pragma once

#include "message_factory.h"

#include "lmprogrambase.h"
#include "lmprogramcontrolwindow.h"
#include "lmprogramcurtailment.h"
#include "lmcurtailcustomer.h"
#include "lmcicustomerbase.h"
#include "lmprogramdirect.h"
#include "lmprogramdirectgear.h"
#include "lmprogramenergyexchange.h"
#include "lmenergyexchangeoffer.h"
#include "lmenergyexchangeofferrevision.h"
#include "lmenergyexchangehourlyoffer.h"
#include "lmenergyexchangecustomer.h"
#include "lmenergyexchangecustomerreply.h"
#include "lmenergyexchangehourlycustomer.h"

#include "Thrift/LMControlAreas_types.h"
#include "Thrift/LMMessage_types.h"

namespace Cti {
namespace Messaging {
namespace Serialization {

MessagePtr<Thrift::LMProgramBase>::type                   serialize ( const ::CtiLMProgramBase& imsg );

MessagePtr<Thrift::LMProgramControlWindow>::type          serialize ( const ::CtiLMProgramControlWindow& imsg );

MessagePtr<Thrift::LMProgramCurtailment>::type            serialize ( const ::CtiLMProgramCurtailment& imsg );

MessagePtr<Thrift::LMCurtailCustomer>::type               serialize ( const ::CtiLMCurtailCustomer& imsg );

MessagePtr<Thrift::LMCICustomerBase>::type                serialize ( const ::CtiLMCICustomerBase& imsg );

MessagePtr<Thrift::LMProgramDirect>::type                 serialize ( const ::CtiLMProgramDirect& imsg );

MessagePtr<Thrift::LMProgramDirectGear>::type             serialize ( const ::CtiLMProgramDirectGear& imsg );

MessagePtr<Thrift::LMProgramEnergyExchange>::type         serialize ( const ::CtiLMProgramEnergyExchange& imsg );

MessagePtr<Thrift::LMEnergyExchangeOffer>::type           serialize ( const ::CtiLMEnergyExchangeOffer& imsg );

MessagePtr<Thrift::LMEnergyExchangeOfferRevision>::type   serialize ( const ::CtiLMEnergyExchangeOfferRevision& imsg );

MessagePtr<Thrift::LMEnergyExchangeHourlyOffer>::type     serialize ( const ::CtiLMEnergyExchangeHourlyOffer& imsg );

MessagePtr<Thrift::LMEnergyExchangeCustomer>::type        serialize ( const ::CtiLMEnergyExchangeCustomer& imsg );

MessagePtr<Thrift::LMEnergyExchangeCustomerReply>::type   serialize ( const ::CtiLMEnergyExchangeCustomerReply& imsg );

MessagePtr<Thrift::LMEnergyExchangeHourlyCustomer>::type  serialize ( const ::CtiLMEnergyExchangeHourlyCustomer& imsg );


extern MessageFactory<::CtiLMProgramBase> g_lmProgramFactory;

}
}
}
