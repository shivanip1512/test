#include "precompiled.h"

#include "rfn_asid.h"

namespace Cti {
namespace Messaging {
namespace Rfn {

ApplicationServiceIdentifiers::ApplicationServiceIdentifiers(unsigned char value_) :
    value(value_)
{
}

typedef ApplicationServiceIdentifiers Asid;

//Reserved = 0,
//C1218PassThrough = 1,
const Asid Asid::ChannelManager    (0x02);
//DlmsPassThrough = 3,
//MeterTimeSynchronization = 4,
//HanManager = 5,
const Asid Asid::EventManager      (0x06);
//ConfigurationManagement = 8,
const Asid Asid::HubMeterCommandSet(0x81);
const Asid Asid::E2EDT             (0x09);
//E2EAP_SNMP = 0x0a,
const Asid Asid::E2EAP_DNP3        (0x0b);

}
}
}
