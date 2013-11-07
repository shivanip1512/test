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
#include "Thrift/MCOverrideRequest_types.h"
#include "Thrift/MCInfo_types.h"
#include "Thrift/MCSchedule_types.h"
#include "Thrift/MCScript_types.h"

namespace Cti {
namespace Messaging {
namespace Serialization {

MessagePtr<Thrift::MCUpdateSchedule>::type    populateThrift  ( const ::CtiMCUpdateSchedule& imsg );
MessagePtr<::CtiMCUpdateSchedule>::type       populateMessage ( const Thrift::MCUpdateSchedule& imsg );

MessagePtr<Thrift::MCAddSchedule>::type       populateThrift  ( const ::CtiMCAddSchedule& imsg );
MessagePtr<::CtiMCAddSchedule>::type          populateMessage ( const Thrift::MCAddSchedule& imsg );

MessagePtr<Thrift::MCDeleteSchedule>::type    populateThrift  ( const ::CtiMCDeleteSchedule& imsg );
MessagePtr<::CtiMCDeleteSchedule>::type       populateMessage ( const Thrift::MCDeleteSchedule& imsg );

MessagePtr<Thrift::MCRetrieveSchedule>::type  populateThrift  ( const ::CtiMCRetrieveSchedule& imsg );
MessagePtr<::CtiMCRetrieveSchedule>::type     populateMessage ( const Thrift::MCRetrieveSchedule& imsg );

MessagePtr<Thrift::MCRetrieveScript>::type    populateThrift  ( const ::CtiMCRetrieveScript& imsg );
MessagePtr<::CtiMCRetrieveScript>::type       populateMessage ( const Thrift::MCRetrieveScript& imsg );

MessagePtr<Thrift::MCOverrideRequest>::type   populateThrift  ( const ::CtiMCOverrideRequest& imsg );
MessagePtr<::CtiMCOverrideRequest>::type      populateMessage ( const Thrift::MCOverrideRequest& imsg );

MessagePtr<Thrift::MCInfo>::type              populateThrift  ( const ::CtiMCInfo& imsg );
MessagePtr<::CtiMCInfo>::type                 populateMessage ( const Thrift::MCInfo& imsg );

MessagePtr<Thrift::MCSchedule>::type          populateThrift  ( const ::CtiMCSchedule& imsg );
MessagePtr<::CtiMCSchedule>::type             populateMessage ( const Thrift::MCSchedule& imsg );

MessagePtr<Thrift::MCScript>::type            populateThrift  ( const ::CtiMCScript& imsg );
MessagePtr<::CtiMCScript>::type               populateMessage ( const Thrift::MCScript& imsg );

}
}
}
