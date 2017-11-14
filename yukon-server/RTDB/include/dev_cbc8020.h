#pragma once

#include "dev_cbcdnp.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Cbc8020Device : public CbcDnpDevice
{
protected:

    using Inherited = CbcDnpDevice;

    AttributeMapping::AttributeList getMappableAttributes() const override;

    void processPoints( Protocols::Interface::pointlist_t &points ) override;

    struct FirmwareRevisionOffsets
    {
        int major;
        int minor;
    };

    static void combineFirmwarePoints( Protocols::Interface::pointlist_t &points, const FirmwareRevisionOffsets firmwareOffsets );

private:
    
    int getDnpPointOffset  (const Attribute attribute);
    int getDnpControlOffset(const Attribute attribute) override;
};

}
}
