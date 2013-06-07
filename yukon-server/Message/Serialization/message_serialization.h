#pragma once

#include "dlldefs.h"
#include "message_factory.h"

#include "message.h"
#include "msg_cmd.h"
#include "msg_commerrorhistory.h"
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
#include "Msg_reg.h"
#include "msg_requestcancel.h"
#include "msg_server_req.h"
#include "msg_server_resp.h"
#include "msg_signal.h"
#include "msg_tag.h"
#include "msg_trace.h"

#include "Thrift/Message_types.h"
#include "Thrift/Command_types.h"
#include "Thrift/CommErrorHistory_types.h"
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

IM_EX_MSG MessagePtr<Thrift::Message>::type               serialize   ( const ::CtiMessage& imsg );
IM_EX_MSG MessagePtr<::CtiMessage>::type                  deserialize ( const Thrift::Message& imsg );

IM_EX_MSG MessagePtr<Thrift::Command>::type               serialize   ( const ::CtiCommandMsg& imsg );
IM_EX_MSG MessagePtr<::CtiCommandMsg>::type               deserialize ( const Thrift::Command& imsg );

IM_EX_MSG MessagePtr<Thrift::CommErrorHistory>::type      serialize   ( const ::CtiCommErrorHistoryMsg& imsg );
IM_EX_MSG MessagePtr<::CtiCommErrorHistoryMsg>::type      deserialize ( const Thrift::CommErrorHistory& imsg );

IM_EX_MSG MessagePtr<Thrift::DBChange>::type              serialize   ( const ::CtiDBChangeMsg& imsg );
IM_EX_MSG MessagePtr<::CtiDBChangeMsg>::type              deserialize ( const Thrift::DBChange& imsg );

IM_EX_MSG MessagePtr<Thrift::LMControlHistory>::type      serialize   ( const ::CtiLMControlHistoryMsg& imsg );
IM_EX_MSG MessagePtr<::CtiLMControlHistoryMsg>::type      deserialize ( const Thrift::LMControlHistory& imsg );

IM_EX_MSG MessagePtr<Thrift::Multi>::type                 serialize   ( const ::CtiMultiMsg& imsg );
IM_EX_MSG MessagePtr<::CtiMultiMsg>::type                 deserialize ( const Thrift::Multi& imsg );

IM_EX_MSG MessagePtr<Thrift::NotifAlarm>::type            serialize   ( const ::CtiNotifAlarmMsg& imsg );
IM_EX_MSG MessagePtr<::CtiNotifAlarmMsg>::type            deserialize ( const Thrift::NotifAlarm& imsg );

IM_EX_MSG MessagePtr<Thrift::NotifEmail>::type            serialize   ( const ::CtiNotifEmailMsg& imsg );
IM_EX_MSG MessagePtr<::CtiNotifEmailMsg>::type            deserialize ( const Thrift::NotifEmail& imsg );

IM_EX_MSG MessagePtr<Thrift::NotifCustomerEmail>::type    serialize   ( const ::CtiCustomerNotifEmailMsg& imsg );
IM_EX_MSG MessagePtr<::CtiCustomerNotifEmailMsg>::type    deserialize ( const Thrift::NotifCustomerEmail& imsg );

IM_EX_MSG MessagePtr<Thrift::NotifLMControl>::type        serialize   ( const ::CtiNotifLMControlMsg& imsg );
IM_EX_MSG MessagePtr<::CtiNotifLMControlMsg>::type        deserialize ( const Thrift::NotifLMControl& imsg );

IM_EX_MSG MessagePtr<Thrift::Request>::type               serialize   ( const ::CtiRequestMsg& imsg );
IM_EX_MSG MessagePtr<::CtiRequestMsg>::type               deserialize ( const Thrift::Request& imsg );

IM_EX_MSG MessagePtr<Thrift::Return>::type                serialize   ( const ::CtiReturnMsg& imsg );
IM_EX_MSG MessagePtr<::CtiReturnMsg>::type                deserialize ( const Thrift::Return& imsg );

IM_EX_MSG MessagePtr<Thrift::PointData>::type             serialize   ( const ::CtiPointDataMsg& imsg );
IM_EX_MSG MessagePtr<::CtiPointDataMsg>::type             deserialize ( const Thrift::PointData& imsg );

IM_EX_MSG MessagePtr<Thrift::PointRegistration>::type     serialize   ( const ::CtiPointRegistrationMsg& imsg );
IM_EX_MSG MessagePtr<::CtiPointRegistrationMsg>::type     deserialize ( const Thrift::PointRegistration& imsg );

IM_EX_MSG MessagePtr<Thrift::QueueData>::type             serialize   ( const ::CtiQueueDataMsg& imsg );
IM_EX_MSG MessagePtr<::CtiQueueDataMsg>::type             deserialize ( const Thrift::QueueData& imsg );

IM_EX_MSG MessagePtr<Thrift::Registration>::type          serialize   ( const ::CtiRegistrationMsg& imsg );
IM_EX_MSG MessagePtr<::CtiRegistrationMsg>::type          deserialize ( const Thrift::Registration& imsg );

IM_EX_MSG MessagePtr<Thrift::RequestCancel>::type         serialize   ( const ::CtiRequestCancelMsg& imsg );
IM_EX_MSG MessagePtr<::CtiRequestCancelMsg>::type         deserialize ( const Thrift::RequestCancel& imsg );

IM_EX_MSG MessagePtr<Thrift::ServerRequest>::type         serialize   ( const ::CtiServerRequestMsg& imsg );
IM_EX_MSG MessagePtr<::CtiServerRequestMsg>::type         deserialize ( const Thrift::ServerRequest& imsg );

IM_EX_MSG MessagePtr<Thrift::ServerResponse>::type        serialize   ( const ::CtiServerResponseMsg& imsg );
IM_EX_MSG MessagePtr<::CtiServerResponseMsg>::type        deserialize ( const Thrift::ServerResponse& imsg );

IM_EX_MSG MessagePtr<Thrift::Signal>::type                serialize   ( const ::CtiSignalMsg& imsg );
IM_EX_MSG MessagePtr<::CtiSignalMsg>::type                deserialize ( const Thrift::Signal& imsg );

IM_EX_MSG MessagePtr<Thrift::Tag>::type                   serialize   ( const ::CtiTagMsg& imsg );
IM_EX_MSG MessagePtr<::CtiTagMsg>::type                   deserialize ( const Thrift::Tag& imsg );

IM_EX_MSG MessagePtr<Thrift::Trace>::type                 serialize   ( const ::CtiTraceMsg& imsg );
IM_EX_MSG MessagePtr<::CtiTraceMsg>::type                 deserialize ( const Thrift::Trace& imsg );

}
}
}
