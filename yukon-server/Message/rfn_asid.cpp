#include "precompiled.h"

#include "rfn_asid.h"

namespace Cti {
namespace Messaging {
namespace Rfn {

    //Reserved = 0,
    //C1218PassThrough = 1,
    const ApplicationServiceIdentifiers ApplicationServiceIdentifiers::ChannelManager = ApplicationServiceIdentifiers(0x02);
    //DlmsPassThrough = 3,
    //MeterTimeSynchronization = 4,
    //HanManager = 5,
    const ApplicationServiceIdentifiers ApplicationServiceIdentifiers::EventManager   = ApplicationServiceIdentifiers(0x06);
    //ConfigurationManagement = 8,
    //HubMeterCommandSet = 0x81,
    const ApplicationServiceIdentifiers ApplicationServiceIdentifiers::E2EDT          = ApplicationServiceIdentifiers(0x09);
    //E2EAP_SNMP = 0x0a,
    const ApplicationServiceIdentifiers ApplicationServiceIdentifiers::E2EAP_DNP3     = ApplicationServiceIdentifiers(0x0b);

}
}
}
