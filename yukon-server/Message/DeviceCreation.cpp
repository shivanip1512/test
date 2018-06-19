#include "precompiled.h"

#include "std_helper.h"
#include "DeviceCreation.h"

namespace Cti       {
namespace Messaging {

RfnDeviceCreationRequestMessage::RfnDeviceCreationRequestMessage( const RfnIdentifier & rfnId )
    :   rfnIdentifier( rfnId )
{
    // empty
}

RfnDeviceCreationReplyMessage::RfnDeviceCreationReplyMessage()
{
    // empty
}


}
}

