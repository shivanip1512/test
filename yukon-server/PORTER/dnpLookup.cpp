#include "precompiled.h"

#include "dnpLookup.h"
#include "dev_single.h"
#include "std_helper.h"

namespace Cti {
namespace Porter {

bool DnpLookup::dnp_addresses::operator<(const DnpLookup::dnp_addresses rhs) const
{
    return std::tie(master, outstation) < std::tie(rhs.master, rhs.outstation);
}

std::string DnpLookup::dnp_addresses::toString() const
{
    return "("
        + std::to_string(master) + "," 
        + std::to_string(outstation) + ")";
}

auto DnpLookup::getDnpAddresses(const CtiDeviceSingle & device) -> dnp_addresses
{
    return { (unsigned short)device.getMasterAddress(),
             (unsigned short)device.getAddress() };
}


boost::optional<const long> DnpLookup::getDeviceIdForAddress(dnp_addresses address) const
{
    return mapFind(_dnpAddress_to_id.left, address);
}

bool DnpLookup::addDevice(const CtiDeviceSingle& device)
{
    return _dnpAddress_to_id.insert({ getDnpAddresses(device), device.getID() }).second;
}

void DnpLookup::updateDevice(const CtiDeviceSingle& device)
{
    deleteDevice(device.getID());
    addDevice(device);
}

void DnpLookup::deleteDevice(long deviceId)
{
    _dnpAddress_to_id.right.erase(deviceId);
}

bool DnpLookup::empty() const
{
    return _dnpAddress_to_id.empty();
}

}
}

