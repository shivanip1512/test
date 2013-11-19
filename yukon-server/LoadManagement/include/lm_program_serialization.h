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

MessagePtr<Thrift::LMProgramBase>::type                   populateThrift ( const ::CtiLMProgramBase& imsg );

MessagePtr<Thrift::LMProgramControlWindow>::type          populateThrift ( const ::CtiLMProgramControlWindow& imsg );

MessagePtr<Thrift::LMProgramCurtailment>::type            populateThrift ( const ::CtiLMProgramCurtailment& imsg );

MessagePtr<Thrift::LMCurtailCustomer>::type               populateThrift ( const ::CtiLMCurtailCustomer& imsg );

MessagePtr<Thrift::LMCICustomerBase>::type                populateThrift ( const ::CtiLMCICustomerBase& imsg );

MessagePtr<Thrift::LMProgramDirect>::type                 populateThrift ( const ::CtiLMProgramDirect& imsg );

MessagePtr<Thrift::LMProgramDirectGear>::type             populateThrift ( const ::CtiLMProgramDirectGear& imsg );

MessagePtr<Thrift::LMProgramEnergyExchange>::type         populateThrift ( const ::CtiLMProgramEnergyExchange& imsg );

MessagePtr<Thrift::LMEnergyExchangeOffer>::type           populateThrift ( const ::CtiLMEnergyExchangeOffer& imsg );

MessagePtr<Thrift::LMEnergyExchangeOfferRevision>::type   populateThrift ( const ::CtiLMEnergyExchangeOfferRevision& imsg );

MessagePtr<Thrift::LMEnergyExchangeHourlyOffer>::type     populateThrift ( const ::CtiLMEnergyExchangeHourlyOffer& imsg );

MessagePtr<Thrift::LMEnergyExchangeCustomer>::type        populateThrift ( const ::CtiLMEnergyExchangeCustomer& imsg );

MessagePtr<Thrift::LMEnergyExchangeCustomerReply>::type   populateThrift ( const ::CtiLMEnergyExchangeCustomerReply& imsg );

MessagePtr<Thrift::LMEnergyExchangeHourlyCustomer>::type  populateThrift ( const ::CtiLMEnergyExchangeHourlyCustomer& imsg );


extern MessageFactory<::CtiLMProgramBase> g_lmProgramFactory;

}
}
}
