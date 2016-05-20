#pragma once

#include "types.h"
#include "pointtypes.h"
#include "dlldefs.h"
#include "db_entry_defines.h"

#include <string>

namespace Cti {

enum AddressUsage
{
    AddressUsage_Invalid = 0,
    AddressUsage_Versacom,
    AddressUsage_Expresscom,

    AddressUsage_Maximum
};

}

IM_EX_CTIBASE CtiPointType_t resolvePointType   (const std::string& str);
IM_EX_CTIBASE INT resolvePointArchiveType       (const std::string& str);
IM_EX_CTIBASE INT resolveCapControlType         (const std::string& str);
IM_EX_CTIBASE INT resolveLoadManagementType     (const std::string& str);

IM_EX_CTIBASE INT resolvePAOType(const std::string& category, const std::string& str);

IM_EX_CTIBASE INT resolveDeviceType             (const std::string& str);
IM_EX_CTIBASE INT resolvePortType               (const std::string& str);
IM_EX_CTIBASE INT resolveLoadManagementType     (const std::string& str);
IM_EX_CTIBASE INT resolveCapControlType         (const std::string& str);
IM_EX_CTIBASE INT resolveRouteType              (const std::string& str);

IM_EX_CTIBASE INT resolvePAOClass               (const std::string& str);
IM_EX_CTIBASE INT resolvePAOCategory            (const std::string& str);
IM_EX_CTIBASE INT resolveScanType               (const std::string& str);
IM_EX_CTIBASE INT resolveStatisticsType         (const std::string& str);
IM_EX_CTIBASE INT resolveProtocol               (const std::string& str);
IM_EX_CTIBASE INT resolveAmpUseType             (const std::string& str);
IM_EX_CTIBASE INT resolveRouteType              (const std::string& str);
IM_EX_CTIBASE bool resolveIsDeviceTypeSingle(INT Type);
IM_EX_CTIBASE INT resolveRelayUsage             (const std::string& str);
IM_EX_CTIBASE INT resolveAWordTime(INT Seconds);
IM_EX_CTIBASE INT resolveAddressUsage           (const std::string& str, int type);
IM_EX_CTIBASE std::string   resolveDBChanged(INT dbnum);
IM_EX_CTIBASE std::string   resolveDBChangeType(INT type);
IM_EX_CTIBASE INT resolveDBCategory             (const std::string& str);
IM_EX_CTIBASE INT resolveSlaveAddress(const INT DeviceType, const std::string& str);
IM_EX_CTIBASE CtiControlType_t  resolveControlType(const std::string& str);
IM_EX_CTIBASE LONG resolveDeviceWindowType      (const std::string& str);

IM_EX_CTIBASE bool isKnownUnsupportedDevice     (const std::string& _str);

