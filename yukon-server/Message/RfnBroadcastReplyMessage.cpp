#include "precompiled.h"

#include "RfnBroadcastReplyMessage.h"

namespace Cti {
namespace Messaging {
namespace Rfn {

const IM_EX_MSG BroadcastResult BroadcastResult::Success("Success");
const IM_EX_MSG BroadcastResult BroadcastResult::Failure("Failure");
const IM_EX_MSG BroadcastResult BroadcastResult::NetworkTimeout("Network timeout");
const IM_EX_MSG BroadcastResult BroadcastResult::Timeout("Timeout");

}
}
}
