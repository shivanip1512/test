#pragma once

#include "message_factory.h"

#include "lmmessage.h"
#include "lmprogramcontrolwindow.h"

#include "Thrift/LMCommand_types.h"
#include "Thrift/LMControlAreas_types.h"
#include "Thrift/LMCurtailmentAcknowledge_types.h"
#include "Thrift/LMDynamicControlAreaData_types.h"
#include "Thrift/LMDynamicGroupData_types.h"
#include "Thrift/LMDynamicProgramData_types.h"
#include "Thrift/LMDynamicTriggerData_types.h"
#include "Thrift/LMEnergyExchangeAccept_types.h"
#include "Thrift/LMEnergyExchangeControl_types.h"
#include "Thrift/LMManualControlRequest_types.h"
#include "Thrift/LMManualControlResponse_types.h"
#include "Thrift/LMMessage_types.h"

namespace Cti {
namespace Messaging {
namespace Serialization {


MessagePtr<Thrift::LMMessage>::type                 serialize   ( const ::CtiLMMessage& imsg );
MessagePtr<::CtiLMMessage>::type                    deserialize ( const Thrift::LMMessage& imsg );

MessagePtr<Thrift::LMCommand>::type                 serialize   ( const ::CtiLMCommand& imsg );
MessagePtr<::CtiLMCommand>::type                    deserialize ( const Thrift::LMCommand& imsg );

MessagePtr<Thrift::LMManualControlRequest>::type    serialize   ( const ::CtiLMManualControlRequest& imsg );
MessagePtr<::CtiLMManualControlRequest>::type       deserialize ( const Thrift::LMManualControlRequest& imsg );

MessagePtr<Thrift::LMManualControlResponse>::type   serialize   ( const ::CtiLMManualControlResponse& imsg );
MessagePtr<::CtiLMManualControlResponse>::type      deserialize ( const Thrift::LMManualControlResponse& imsg );

MessagePtr<Thrift::LMEnergyExchangeControl>::type   serialize   ( const ::CtiLMEnergyExchangeControlMsg& imsg );
MessagePtr<::CtiLMEnergyExchangeControlMsg>::type   deserialize ( const Thrift::LMEnergyExchangeControl& imsg );

MessagePtr<Thrift::LMEnergyExchangeAccept>::type    serialize   ( const ::CtiLMEnergyExchangeAcceptMsg& imsg );
MessagePtr<::CtiLMEnergyExchangeAcceptMsg>::type    deserialize ( const Thrift::LMEnergyExchangeAccept& imsg );

MessagePtr<Thrift::LMControlAreas>::type            serialize   ( const ::CtiLMControlAreaMsg& imsg );

MessagePtr<Thrift::LMCurtailmentAcknowledge>::type  serialize   ( const ::CtiLMCurtailmentAcknowledgeMsg& imsg );
MessagePtr<::CtiLMCurtailmentAcknowledgeMsg>::type  deserialize ( const Thrift::LMCurtailmentAcknowledge& imsg );

MessagePtr<Thrift::LMDynamicGroupData>::type        serialize   ( const ::CtiLMDynamicGroupDataMsg& imsg );

MessagePtr<Thrift::LMDynamicProgramData>::type      serialize   ( const ::CtiLMDynamicProgramDataMsg& imsg );

MessagePtr<Thrift::LMDynamicControlAreaData>::type  serialize   ( const ::CtiLMDynamicControlAreaDataMsg& imsg );

MessagePtr<Thrift::LMDynamicTriggerData>::type      serialize   ( const ::CtiLMDynamicTriggerDataMsg& imsg );

MessagePtr<Thrift::LMConstraintViolation>::type     serialize   ( const ::ConstraintViolation& imsg );
MessagePtr<::ConstraintViolation>::type             deserialize ( const Thrift::LMConstraintViolation& imsg );

MessagePtr<Thrift::LMControlAreaTrigger>::type      serialize   ( const ::CtiLMControlAreaTrigger& imsg );

MessagePtr<Thrift::LMProgramControlWindow>::type    serialize   ( const ::CtiLMProgramControlWindow& imsg );

MessagePtr<Thrift::LMProgramBase>::type             serialize   ( const ::CtiLMProgramBase& imsg );

MessagePtr<Thrift::LMControlAreaItem>::type         serialize   ( const ::CtiLMControlArea& imsg );
MessagePtr<::CtiLMControlArea>::type                deserialize ( const Thrift::LMControlAreaItem& imsg );

}
}
}
