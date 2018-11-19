#pragma once

#include <boost/bimap.hpp>
#include <boost/optional.hpp>

class CtiDeviceSingle;

namespace Cti {
namespace Porter {

class DnpLookup 
{
public:

    struct dnp_addresses
    {
        unsigned short master;
        unsigned short outstation;

        bool operator<(const dnp_addresses rhs) const;

        std::string toString() const;
    };

    static dnp_addresses getDnpAddresses(const CtiDeviceSingle &device);

    boost::optional<const long> getDeviceIdForAddress(dnp_addresses address) const;

    bool addDevice   (const CtiDeviceSingle& device);
    void updateDevice(const CtiDeviceSingle& device);
    void deleteDevice(long deviceId);

    bool empty() const;

private:

    using dnp_address_id_bimap = boost::bimap<dnp_addresses, long>;

    dnp_address_id_bimap _dnpAddress_to_id;
};

}
}
