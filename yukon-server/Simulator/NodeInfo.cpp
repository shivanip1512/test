#include "precompiled.h"

#include "NodeInfo.h"

#include "database_reader.h"

#include "std_helper.h"

#include <json.hpp>

#include <filesystem>
#include <fstream>
#include <random>

namespace Cti::Simulator {

namespace fs = std::filesystem;
    
using json = nlohmann::json;

extern std::mt19937_64 gen;

namespace {

    const fs::path NodeInfoDirectory = fs::path("SimulatorRfNodeInfo");

    fs::path constructNodeInfoPath(const RfnIdentifier& rfnId)
    {
        const auto nodeInfoFilename = rfnId.toString() + ".json";

        return NodeInfoDirectory / nodeInfoFilename;
    }

    bool validateNodeInfoDirectory()
    {
        if( const auto dirStatus = fs::status(NodeInfoDirectory);
            fs::exists(dirStatus) )
        {
            const auto canonicalDirName = fs::canonical(NodeInfoDirectory).string();

            CTILOG_DEBUG(dout, "Path exists, confirming it is a directory: " << canonicalDirName);

            if( ! fs::is_directory(dirStatus) )
            {
                CTILOG_ERROR(dout, "Path exists, but is not a directory, cannot use for storing NodeInfo: " << canonicalDirName);
                return false;
            }
        }
        else
        {
            const auto currentPathName = fs::current_path().string();

            CTILOG_DEBUG(dout, "Path does not exist, creating directory: " << NodeInfoDirectory.string() << " in " << currentPathName);

            if( ! fs::create_directory(NodeInfoDirectory) )
            {
                CTILOG_ERROR(dout, "Could not create directory: " << NodeInfoDirectory.string() << " in " << currentPathName << ", cannot store NodeInfo");
                return false;
            }
        }

        return true;
    }

    const std::string NodeInfo_ConfigurationId = "configurationId";

    const std::vector<std::string> remoteGuids {
        //low-mid-high-and-version (final nibble)
        //1b21dd213814000 - 100-ns counts between Gregorian epoch and Unix epoch
        //719,528 days between Gregorian epoch and Unix epoch
        // 1 ms = 10,000 100ns increments
        // 1 s = 10,000,000 100 ns increments
        //  d952eb22-0a3e-11ea = 1ea0a3ed952eb22 - epoch = 37EC6CC5D1AB22 = 1574107563 856 1570 = 2019-11-18 20:06:03
        "d952eb22-0a3e-11ea-8d71-362b9e155667",  //  1574107563 856 1570 = 2019-11-18 20:06:03
        "d952ee74-0a4e-11ea-8d71-362b9e155667",  //  1574114435 803 8306 = 2019-11-18 22:00:35
        "d952efdc-0a5e-11ea-8d71-362b9e155667",  //  1574121307 751 5042 = 2019-11-18 23:55:07
        "d952f112-0a6e-11ea-8d71-362b9e155667",  //  1574128179 699 1778 = 2019-11-19 01:49:39
        "d952f23e-0a7e-11ea-8d71-362b9e155667",  //  1574135051 646 8514 = 2019-11-19 03:44:11
        "d952f36a-0a8e-11ea-8d71-362b9e155667",  //  1574141923 594 5250 = 2019-11-19 05:38:43
        "d952f68a-0a9e-11ea-8d71-362b9e155667",  //  1574148795 542 1986 = 2019-11-19 07:33:15
        "d952f7d4-0aae-11ea-8d71-362b9e155667",  //  1574155667 489 8722 = 2019-11-19 09:27:47
        "d952f90a-0abe-11ea-8d71-362b9e155667",  //  1574162539 437 5458 = 2019-11-19 11:22:19
        "d952fa40-0ace-11ea-8d71-362b9e155667",  //  1574169411 385 2194 = 2019-11-19 13:16:51
    };

    const std::vector<std::string> yukonGuids {
        "EE8358B0-92B7-4603-A148-A06E5489D4C7",
        "70A9331E-BA48-4F7A-ADDD-525CAD8FD578",
        "0474477D-1D69-459D-A8E8-65F71EC46923",
        "B9D7E497-5907-42E3-980B-D3F8A47F8D11",
        "770E5A24-A3AF-4985-B15F-F40718A4F183",
    };

    std::uniform_int_distribution<unsigned> selector;

    std::string GenerateConfigurationId()
    {
        const std::vector<GuidPrefix> sources { 
            GuidPrefix::Yukon, 
            GuidPrefix::Optical,
            GuidPrefix::Unknown,
            GuidPrefix::Unprogrammed,
            GuidPrefix::InsufficientFirmware };

        const auto index = selector(gen);

        const auto source = sources[index % sources.size()];

        return static_cast<char>(source) 
            + (source == GuidPrefix::Yukon
                ? yukonGuids[index % yukonGuids.size()]
                : remoteGuids[index % remoteGuids.size()]);
    }
}


NodeInfo::NodeInfo(const RfnIdentifier& rfnId)
    :   _rfnId { rfnId }
{
    if( ! loadFromFile() )
    {
        if( ! loadFromDatabase() )
        {
            _configurationId = GenerateConfigurationId();
        }

        store();
    }
}

bool NodeInfo::loadFromFile()
{
    const auto nodeInfoPath = constructNodeInfoPath(_rfnId);

    if( ! fs::exists(nodeInfoPath) )
    {
        CTILOG_DEBUG(dout, "File did not exist for " << _rfnId);

        return false;
    }

    std::ifstream nodeInfoStream { nodeInfoPath };

    json nodeInfo;

    nodeInfoStream >> nodeInfo;

    CTILOG_TRACE(dout, _rfnId << ": " << nodeInfo.dump());

    const auto configItr = nodeInfo.find(NodeInfo_ConfigurationId);

    if( configItr == nodeInfo.end() )
    {
        CTILOG_WARN(dout, "Loaded JSON from file for " << _rfnId << ", but configurationId not found:\n" << nodeInfo.dump());

        return false;
    }

    if( ! configItr->is_string() )
    {
        CTILOG_WARN(dout, "Loaded JSON from file for " << _rfnId << ", but configurationId not a string:\n" << nodeInfo.dump());

        return false;
    }

    _configurationId = configItr->get<std::string>();

    return true;
}

bool NodeInfo::loadFromDatabase()
{
    const auto loadingSql =
        "select"
            " mps.ReportedGuid, mps.Source"
        " from"
            " MeterProgramStatus mps"
            " join RfnAddress r on mps.DeviceId = r.DeviceId"
        " where"
            " r.Manufacturer = ?"
            " and r.Model = ?"
            " and r.SerialNumber = ?";

    Database::DatabaseConnection connection;
    Database::DatabaseReader rdr(connection, loadingSql);

    rdr << _rfnId.manufacturer;
    rdr << _rfnId.model;
    rdr << _rfnId.serialNumber;

    rdr.execute();

    if( ! rdr() )
    {
        CTILOG_DEBUG(dout, "No rows for " << _rfnId);

        return false;
    }

    _configurationId = 
        rdr["Source"].as<std::string>() 
        + rdr["ReportedGuid"].as<std::string>();

    return true;
}

void NodeInfo::store()
{
    CTILOG_INFO(dout, "Storing info for " << _rfnId);

    if( ! validateNodeInfoDirectory() )
    {
        CTILOG_ERROR(dout, "Could not validate node info directory, not persisting NodeInfo for " << _rfnId);
        return;
    }

    json serializer;

    serializer[NodeInfo_ConfigurationId] = _configurationId;

    const auto nodeInfoPath = constructNodeInfoPath(_rfnId);

    std::ofstream nodeInfoStream { nodeInfoPath };

    nodeInfoStream << serializer;
}

NodeInfo NodeInfo::of(const RfnIdentifier& rfnId)
{
    return NodeInfo(rfnId);
}

void NodeInfo::setConfigurationId(const GuidPrefix prefix, const std::string& guid)
{
    _configurationId = static_cast<char>(prefix) + guid;

    store();
}

std::string NodeInfo::getConfigurationId()
{
    return _configurationId;
}

}