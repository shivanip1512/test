#pragma once

#include "dlldefs.h"
#include "message_factory.h"

#include "message.h"
#include "msg_cmd.h"
#include "msg_dbchg.h"
#include "msg_lmcontrolhistory.h"
#include "msg_multi.h"
#include "msg_notif_alarm.h"
#include "msg_notif_email.h"
#include "msg_notif_lmcontrol.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "msg_queuedata.h"
#include "msg_reg.h"
#include "msg_requestcancel.h"
#include "msg_server_req.h"
#include "msg_server_resp.h"
#include "msg_signal.h"
#include "msg_tag.h"
#include "msg_trace.h"

#include "Thrift/Message_types.h"
#include "Thrift/Command_types.h"
#include "Thrift/DBChange_types.h"
#include "Thrift/LMControlHistory_types.h"
#include "Thrift/Multi_types.h"
#include "Thrift/NotifAlarm_types.h"
#include "Thrift/NotifEmail_types.h"
#include "Thrift/NotifCustomerEmail_types.h"
#include "Thrift/NotifLMControl_types.h"
#include "Thrift/Request_types.h"
#include "Thrift/PointData_types.h"
#include "Thrift/PointRegistration_types.h"
#include "Thrift/QueueData_types.h"
#include "Thrift/Registration_types.h"
#include "Thrift/RequestCancel_types.h"
#include "Thrift/Return_types.h"
#include "Thrift/ServerRequest_types.h"
#include "Thrift/ServerResponse_types.h"
#include "Thrift/Signal_types.h"
#include "Thrift/Tag_types.h"
#include "Thrift/Trace_types.h"

namespace Cti {
namespace Messaging {
namespace Serialization {

IM_EX_MSG MessagePtr<Thrift::Message>::type               populateThrift  ( const ::CtiMessage& imsg );
IM_EX_MSG MessagePtr<::CtiMessage>::type                  populateMessage ( const Thrift::Message& imsg );

IM_EX_MSG MessagePtr<Thrift::Command>::type               populateThrift  ( const ::CtiCommandMsg& imsg );
IM_EX_MSG MessagePtr<::CtiCommandMsg>::type               populateMessage ( const Thrift::Command& imsg );

IM_EX_MSG MessagePtr<Thrift::DBChange>::type              populateThrift  ( const ::CtiDBChangeMsg& imsg );
IM_EX_MSG MessagePtr<::CtiDBChangeMsg>::type              populateMessage ( const Thrift::DBChange& imsg );

IM_EX_MSG MessagePtr<Thrift::LMControlHistory>::type      populateThrift  ( const ::CtiLMControlHistoryMsg& imsg );
IM_EX_MSG MessagePtr<::CtiLMControlHistoryMsg>::type      populateMessage ( const Thrift::LMControlHistory& imsg );

IM_EX_MSG MessagePtr<Thrift::Multi>::type                 populateThrift  ( const ::CtiMultiMsg& imsg );
IM_EX_MSG MessagePtr<::CtiMultiMsg>::type                 populateMessage ( const Thrift::Multi& imsg );

IM_EX_MSG MessagePtr<Thrift::NotifAlarm>::type            populateThrift  ( const ::CtiNotifAlarmMsg& imsg );
IM_EX_MSG MessagePtr<::CtiNotifAlarmMsg>::type            populateMessage ( const Thrift::NotifAlarm& imsg );

IM_EX_MSG MessagePtr<Thrift::NotifEmail>::type            populateThrift  ( const ::CtiNotifEmailMsg& imsg );
IM_EX_MSG MessagePtr<::CtiNotifEmailMsg>::type            populateMessage ( const Thrift::NotifEmail& imsg );

IM_EX_MSG MessagePtr<Thrift::NotifCustomerEmail>::type    populateThrift  ( const ::CtiCustomerNotifEmailMsg& imsg );
IM_EX_MSG MessagePtr<::CtiCustomerNotifEmailMsg>::type    populateMessage ( const Thrift::NotifCustomerEmail& imsg );

IM_EX_MSG MessagePtr<Thrift::NotifLMControl>::type        populateThrift  ( const ::CtiNotifLMControlMsg& imsg );
IM_EX_MSG MessagePtr<::CtiNotifLMControlMsg>::type        populateMessage ( const Thrift::NotifLMControl& imsg );

IM_EX_MSG MessagePtr<Thrift::Request>::type               populateThrift  ( const ::CtiRequestMsg& imsg );
IM_EX_MSG MessagePtr<::CtiRequestMsg>::type               populateMessage ( const Thrift::Request& imsg );

IM_EX_MSG MessagePtr<Thrift::Return>::type                populateThrift  ( const ::CtiReturnMsg& imsg );
IM_EX_MSG MessagePtr<::CtiReturnMsg>::type                populateMessage ( const Thrift::Return& imsg );

IM_EX_MSG MessagePtr<Thrift::PointData>::type             populateThrift  ( const ::CtiPointDataMsg& imsg );
IM_EX_MSG MessagePtr<::CtiPointDataMsg>::type             populateMessage ( const Thrift::PointData& imsg );

IM_EX_MSG MessagePtr<Thrift::PointRegistration>::type     populateThrift  ( const ::CtiPointRegistrationMsg& imsg );
IM_EX_MSG MessagePtr<::CtiPointRegistrationMsg>::type     populateMessage ( const Thrift::PointRegistration& imsg );

IM_EX_MSG MessagePtr<Thrift::QueueData>::type             populateThrift  ( const ::CtiQueueDataMsg& imsg );
IM_EX_MSG MessagePtr<::CtiQueueDataMsg>::type             populateMessage ( const Thrift::QueueData& imsg );

IM_EX_MSG MessagePtr<Thrift::Registration>::type          populateThrift  ( const ::CtiRegistrationMsg& imsg );
IM_EX_MSG MessagePtr<::CtiRegistrationMsg>::type          populateMessage ( const Thrift::Registration& imsg );

IM_EX_MSG MessagePtr<Thrift::RequestCancel>::type         populateThrift  ( const ::CtiRequestCancelMsg& imsg );
IM_EX_MSG MessagePtr<::CtiRequestCancelMsg>::type         populateMessage ( const Thrift::RequestCancel& imsg );

IM_EX_MSG MessagePtr<Thrift::ServerRequest>::type         populateThrift  ( const ::CtiServerRequestMsg& imsg );
IM_EX_MSG MessagePtr<::CtiServerRequestMsg>::type         populateMessage ( const Thrift::ServerRequest& imsg );

IM_EX_MSG MessagePtr<Thrift::ServerResponse>::type        populateThrift  ( const ::CtiServerResponseMsg& imsg );
IM_EX_MSG MessagePtr<::CtiServerResponseMsg>::type        populateMessage ( const Thrift::ServerResponse& imsg );

IM_EX_MSG MessagePtr<Thrift::Signal>::type                populateThrift  ( const ::CtiSignalMsg& imsg );
IM_EX_MSG MessagePtr<::CtiSignalMsg>::type                populateMessage ( const Thrift::Signal& imsg );

IM_EX_MSG MessagePtr<Thrift::Tag>::type                   populateThrift  ( const ::CtiTagMsg& imsg );
IM_EX_MSG MessagePtr<::CtiTagMsg>::type                   populateMessage ( const Thrift::Tag& imsg );

IM_EX_MSG MessagePtr<Thrift::Trace>::type                 populateThrift  ( const ::CtiTraceMsg& imsg );
IM_EX_MSG MessagePtr<::CtiTraceMsg>::type                 populateMessage ( const Thrift::Trace& imsg );

}
}
}
