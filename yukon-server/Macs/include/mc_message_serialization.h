#pragma once

#include "message_factory.h"

#include "mc_msg.h"
#include "mc_sched.h"
#include "mc_script.h"

#include "Thrift/MCUpdateSchedule_types.h"
#include "Thrift/MCAddSchedule_types.h"
#include "Thrift/MCDeleteSchedule_types.h"
#include "Thrift/MCRetrieveSchedule_types.h"
#include "Thrift/MCRetrieveScript_types.h"
#include "Thrift/MCVerifyScript_types.h"
#include "Thrift/MCOverrideRequest_types.h"
#include "Thrift/MCInfo_types.h"
#include "Thrift/MCSchedule_types.h"
#include "Thrift/MCScript_types.h"

namespace Cti {
namespace Messaging {
namespace Serialization {

MessagePtr<Thrift::MCUpdateSchedule>::type    serialize   ( const ::CtiMCUpdateSchedule& imsg );
MessagePtr<::CtiMCUpdateSchedule>::type       deserialize ( const Thrift::MCUpdateSchedule& imsg );

MessagePtr<Thrift::MCAddSchedule>::type       serialize   ( const ::CtiMCAddSchedule& imsg );
MessagePtr<::CtiMCAddSchedule>::type          deserialize ( const Thrift::MCAddSchedule& imsg );

MessagePtr<Thrift::MCDeleteSchedule>::type    serialize   ( const ::CtiMCDeleteSchedule& imsg );
MessagePtr<::CtiMCDeleteSchedule>::type       deserialize ( const Thrift::MCDeleteSchedule& imsg );

MessagePtr<Thrift::MCRetrieveSchedule>::type  serialize   ( const ::CtiMCRetrieveSchedule& imsg );
MessagePtr<::CtiMCRetrieveSchedule>::type     deserialize ( const Thrift::MCRetrieveSchedule& imsg );

MessagePtr<Thrift::MCRetrieveScript>::type    serialize   ( const ::CtiMCRetrieveScript& imsg );
MessagePtr<::CtiMCRetrieveScript>::type       deserialize ( const Thrift::MCRetrieveScript& imsg );

MessagePtr<Thrift::MCVerifyScript>::type      serialize   ( const ::CtiMCVerifyScript& imsg );
MessagePtr<::CtiMCVerifyScript>::type         deserialize ( const Thrift::MCVerifyScript& imsg );

MessagePtr<Thrift::MCOverrideRequest>::type   serialize   ( const ::CtiMCOverrideRequest& imsg );
MessagePtr<::CtiMCOverrideRequest>::type      deserialize ( const Thrift::MCOverrideRequest& imsg );

MessagePtr<Thrift::MCInfo>::type              serialize   ( const ::CtiMCInfo& imsg );
MessagePtr<::CtiMCInfo>::type                 deserialize ( const Thrift::MCInfo& imsg );

MessagePtr<Thrift::MCSchedule>::type          serialize   ( const ::CtiMCSchedule& imsg );
MessagePtr<::CtiMCSchedule>::type             deserialize ( const Thrift::MCSchedule& imsg );

MessagePtr<Thrift::MCScript>::type            serialize   ( const ::CtiMCScript& imsg );
MessagePtr<::CtiMCScript>::type               deserialize ( const Thrift::MCScript& imsg );

}
}
}
