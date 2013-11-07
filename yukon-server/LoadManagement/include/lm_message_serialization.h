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


MessagePtr<Thrift::LMMessage>::type                 populateThrift  ( const ::CtiLMMessage& imsg );
MessagePtr<::CtiLMMessage>::type                    populateMessage ( const Thrift::LMMessage& imsg );

MessagePtr<Thrift::LMCommand>::type                 populateThrift  ( const ::CtiLMCommand& imsg );
MessagePtr<::CtiLMCommand>::type                    populateMessage ( const Thrift::LMCommand& imsg );

MessagePtr<Thrift::LMManualControlRequest>::type    populateThrift  ( const ::CtiLMManualControlRequest& imsg );
MessagePtr<::CtiLMManualControlRequest>::type       populateMessage ( const Thrift::LMManualControlRequest& imsg );

MessagePtr<Thrift::LMManualControlResponse>::type   populateThrift  ( const ::CtiLMManualControlResponse& imsg );
MessagePtr<::CtiLMManualControlResponse>::type      populateMessage ( const Thrift::LMManualControlResponse& imsg );

MessagePtr<Thrift::LMEnergyExchangeControl>::type   populateThrift  ( const ::CtiLMEnergyExchangeControlMsg& imsg );
MessagePtr<::CtiLMEnergyExchangeControlMsg>::type   populateMessage ( const Thrift::LMEnergyExchangeControl& imsg );

MessagePtr<Thrift::LMEnergyExchangeAccept>::type    populateThrift  ( const ::CtiLMEnergyExchangeAcceptMsg& imsg );
MessagePtr<::CtiLMEnergyExchangeAcceptMsg>::type    populateMessage ( const Thrift::LMEnergyExchangeAccept& imsg );

MessagePtr<Thrift::LMControlAreas>::type            populateThrift  ( const ::CtiLMControlAreaMsg& imsg );

MessagePtr<Thrift::LMCurtailmentAcknowledge>::type  populateThrift  ( const ::CtiLMCurtailmentAcknowledgeMsg& imsg );
MessagePtr<::CtiLMCurtailmentAcknowledgeMsg>::type  populateMessage ( const Thrift::LMCurtailmentAcknowledge& imsg );

MessagePtr<Thrift::LMDynamicGroupData>::type        populateThrift  ( const ::CtiLMDynamicGroupDataMsg& imsg );

MessagePtr<Thrift::LMDynamicProgramData>::type      populateThrift  ( const ::CtiLMDynamicProgramDataMsg& imsg );

MessagePtr<Thrift::LMDynamicControlAreaData>::type  populateThrift  ( const ::CtiLMDynamicControlAreaDataMsg& imsg );

MessagePtr<Thrift::LMDynamicTriggerData>::type      populateThrift  ( const ::CtiLMDynamicTriggerDataMsg& imsg );

MessagePtr<Thrift::LMConstraintViolation>::type     populateThrift  ( const ::ConstraintViolation& imsg );
MessagePtr<::ConstraintViolation>::type             populateMessage ( const Thrift::LMConstraintViolation& imsg );

MessagePtr<Thrift::LMControlAreaTrigger>::type      populateThrift  ( const ::CtiLMControlAreaTrigger& imsg );

MessagePtr<Thrift::LMProgramControlWindow>::type    populateThrift  ( const ::CtiLMProgramControlWindow& imsg );

MessagePtr<Thrift::LMProgramBase>::type             populateThrift  ( const ::CtiLMProgramBase& imsg );

MessagePtr<Thrift::LMControlAreaItem>::type         populateThrift  ( const ::CtiLMControlArea& imsg );
MessagePtr<::CtiLMControlArea>::type                populateMessage ( const Thrift::LMControlAreaItem& imsg );

}
}
}
