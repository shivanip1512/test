#pragma once

#include "rfn_identifier.h"

namespace Cti::Simulator {

enum class GuidPrefix : char
{
    Yukon = 'R',
    Optical = 'P',
    Unknown = 'N',
    Unprogrammed = 'U',
    InsufficientFirmware = 'X'
};
    
class NodeInfo
{
public:
    static NodeInfo of(const RfnIdentifier&);

    void setConfigurationId(GuidPrefix prefix, const std::string& guid);
    std::string getConfigurationId();

private:
    NodeInfo(const RfnIdentifier& rfnId);

    RfnIdentifier _rfnId;
    std::string _configurationId;

    void store();

    bool loadFromFile();
    bool loadFromDatabase();
};

}