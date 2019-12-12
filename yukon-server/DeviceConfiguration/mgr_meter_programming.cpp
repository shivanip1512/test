#include "precompiled.h"

#include "mgr_meter_programming.h"

#include "mgr_device.h"
#include "mgr_dyn_paoinfo.h"
#include "dev_rfn.h"

#include "database_reader.h"
#include "encryption.h"
#include "std_helper.h"

#include <meterProgramConverter/exports.h>

namespace Cti::MeterProgramming {

namespace {
    std::mutex programMux;
    MeterProgrammingManager::Bytes globalBuffer;
}

auto MeterProgrammingManager::getProgram(const std::string guid) -> Bytes
{
    if( std::lock_guard lg(programMux); 
        auto existingProgram = mapFindRef(_programs, guid) )
    {
        return *existingProgram;
    }

    return loadProgram(guid);
}

size_t MeterProgrammingManager::getProgramSize(const std::string guid)
{
    if( std::lock_guard lg(programMux); 
        auto existingProgram = mapFindRef(_programs, guid) )
    {
        return existingProgram->size();
    }

    return loadProgram(guid).size();
}

auto MeterProgrammingManager::loadRawProgram(const std::string guid) -> RawProgram
{
    std::string sql = "select program, password from MeterProgram where guid = ?";

    Database::DatabaseConnection conn;
    Database::DatabaseReader rdr { conn, sql };

    rdr << guid;

    rdr.execute();

    if( ! rdr() )
    {
        CTILOG_ERROR(dout, "Could not retrieve MeterProgram entry for guid " << guid);
        
        return {};
    }

    const auto program = rdr["program"] .as<Bytes>();
    const auto encryptedPasswordString = rdr["password"].as<std::string>();

    const auto encryptedPassword = convertHexStringToBytes(encryptedPasswordString);

    const auto password = Cti::Encryption::decrypt(Cti::Encryption::SharedKeyfile, encryptedPassword);

    return { program, password };
}


auto MeterProgrammingManager::loadProgram(const std::string guid) -> Bytes
{
    auto raw = loadRawProgram(guid);

    if( raw.program.empty() )
    {
        CTILOG_ERROR(dout, "No raw program for guid " << guid);

        return {};
    }

    //  convert to char
    std::vector<char> charProgram  { raw.program.begin(),  raw.program.end() };
    std::vector<char> charPassword { raw.password.begin(), raw.password.end() };

    FILEINFO_t fileInfo { charProgram.data(), static_cast<uint16_t>(charProgram.size()), 
                          charPassword.data(), static_cast<uint8_t>(charPassword.size()) };

    std::lock_guard lg(programMux);

    auto captureToBuffer = [](void *buf, size_t len) {
        auto ucBuf = reinterpret_cast<unsigned char*>(buf);
        globalBuffer.assign(ucBuf, ucBuf + len);
    };

    if( auto error = conProcessBlob(&fileInfo, captureToBuffer) )
    {
        CTILOG_ERROR(dout, "Error processing meter program buffer" << FormattedList::of(
            "Error", error,
            "GUID", guid));

        return {};
    }

    return _programs[guid] = globalBuffer;
}

std::optional<ProgramDescriptor> MeterProgrammingManager::describeAssignedProgram(const RfnIdentifier rfnIdentifier)
{
    const std::string sql =
        "select guid"
        " from"
            " MeterProgramAssignment mpa"
            " join RfnAddress r on mpa.deviceid=r.deviceid"
        " WHERE"
            " r.SerialNumber=?"
            " AND r.Manufacturer=?"
            " AND r.Model=?";

    Database::DatabaseConnection conn;
    Database::DatabaseReader rdr { conn, sql };

    rdr << rfnIdentifier.serialNumber;
    rdr << rfnIdentifier.manufacturer;
    rdr << rfnIdentifier.model;

    rdr.execute();

    if( ! rdr() )
    {
        CTILOG_WARN(dout, "Could not retrieve Meter Program Assignment for " << rfnIdentifier);
        return std::nullopt;
    }

    auto guid = rdr.as<std::string>();

    auto programSize = getProgramSize(guid);

    if( ! programSize )
    {
        CTILOG_WARN(dout, "No program returned for GUID " << guid << " assigned to " << rfnIdentifier);
        return std::nullopt;
    }

    return ProgramDescriptor { guid, programSize };
}

bool MeterProgrammingManager::isUploading(const RfnIdentifier rfnIdentifier, const std::string guid)
{
    const std::string sql = 
        "select 1"
        " from"
            " MeterProgramAssignment mpa"
                " join RfnAddress r on mpa.deviceid=r.deviceid"
        " WHERE"
            " mpa.Guid=?"
            " AND r.SerialNumber=?"
            " AND r.Manufacturer=?"
            " AND r.Model=?";

    Database::DatabaseConnection conn;
    Database::DatabaseReader rdr { conn, sql };

    rdr << guid;
    rdr << rfnIdentifier.serialNumber;
    rdr << rfnIdentifier.manufacturer;
    rdr << rfnIdentifier.model;
    
    rdr.execute();

    return rdr();
}

double MeterProgrammingManager::calculateMeterProgrammingProgress(RfnIdentifier rfnIdentifier, std::string guid, size_t size)
{
    if( isUploading(rfnIdentifier, guid) )
    {
        const size_t totalSize  = getProgram(guid).size();
        const double percentage = 100.0 * size / totalSize;

        return percentage;
    }
    return 0.0;
}

IM_EX_CONFIG std::unique_ptr<MeterProgrammingManager> gMeterProgrammingManager = std::make_unique<MeterProgrammingManager>();

}