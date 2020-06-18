#include "precompiled.h"

#include "mgr_meter_programming.h"

#include "mgr_device.h"
#include "mgr_dyn_paoinfo.h"
#include "dev_rfn.h"

#include "database_reader.h"
#include "encryption.h"
#include "checksums.h"
#include "std_helper.h"
#include "error.h"

#include <meterProgramConverter/exports.h>
#include <io.h>

using Cti::Logging::Range::Hex::operator<<;

namespace Cti::MeterProgramming {

namespace {
    std::mutex programMux;
    MeterProgrammingManager::Bytes globalBuffer;
}

auto MeterProgrammingManager::getProgram(const std::string guid) -> ErrorOr<Bytes>
{
    CTILOG_DEBUG(dout, guid);

    if( std::lock_guard lg(programMux); 
        auto existingProgram = mapFindRef(_programs, guid) )
    {
        CTILOG_DEBUG(dout, "Returning existing program" << FormattedList::of(
            "GUID", guid,
            "Program size", existingProgram->size()));

        return *existingProgram;
    }

    CTILOG_DEBUG(dout, "Program not found, loading: " << guid);

    return loadProgram(guid);
}

ErrorOr<size_t> MeterProgrammingManager::getProgramSize(const std::string guid)
{
    CTILOG_DEBUG(dout, guid);

    if( std::lock_guard lg(programMux);
        auto existingProgram = mapFindRef(_programs, guid) )
    {
        CTILOG_DEBUG(dout, "Returning existing program" << FormattedList::of(
            "GUID", guid,
            "Program size", existingProgram->size()));

        //  Avoid allocating a copy of the entire meter program
        return existingProgram->size();
    }

    CTILOG_DEBUG(dout, "Program not found, loading: " << guid);

    return loadProgram(guid)
            .map([](const Bytes& program) {
                return program.size();
            });
}

auto MeterProgrammingManager::loadRawProgram(const std::string guid) -> ErrorOr<RawProgram>
{
    CTILOG_DEBUG(dout, guid);

    std::string sql = "select program, password from MeterProgram where guid = ?";

    Database::DatabaseConnection conn;
    Database::DatabaseReader rdr { conn, sql };

    rdr << guid;

    rdr.execute();

    if( ! rdr() )
    {
        CTILOG_ERROR(dout, "Could not retrieve MeterProgram entry for guid " << guid);
        
        return ClientErrors::MeterProgramNotFound;
    }

    const auto program = rdr["program"].as<Bytes>();
    const auto encryptedPasswordString = rdr["password"].as<std::string>();

    const auto encryptedPassword = convertHexStringToBytes(encryptedPasswordString);

    const auto password = Cti::Encryption::decrypt(Cti::Encryption::SharedKeyfile, encryptedPassword);

    CTILOG_DEBUG(dout, "Loaded program from DB" << FormattedList::of(
        "Program length", program.size(),
        "Program MD5", arrayToRange(calculateMd5Digest(program)),
        "Encrypted password string length", encryptedPasswordString.size(),
        "Encrypted password length", encryptedPassword.size(),
        "Decrypted password length", password.size()));

    return RawProgram { program, password };
}


auto MeterProgrammingManager::loadProgram(const std::string guid) -> ErrorOr<Bytes>
{
    CTILOG_DEBUG(dout, guid);

    return loadRawProgram(guid)
        .flatMap<Bytes>([this, guid](const RawProgram& raw) -> ErrorOr<Bytes> {
            std::lock_guard lg(programMux);

            auto convertedBuffer = convertRawProgram(raw, guid);

            if( convertedBuffer.empty() )
            {
                CTILOG_ERROR(dout, "Raw program conversion failed for guid " << guid);

                return ClientErrors::InvalidMeterProgram;
            }

            CTILOG_DEBUG(dout, "Converted meter program" << FormattedList::of(
                "GUID", guid,
                "MD5", arrayToRange(calculateMd5Digest(globalBuffer)),
                "Converted size", globalBuffer.size()));

            return _programs[guid] = convertedBuffer;
        });
}

namespace {

struct stdout_state
{
    const int stdout_fd;
    const int stdout_fd_orig;
    HANDLE readPipe;
    HANDLE writePipe;
    int writePipe_fd;
};

stdout_state capture_printf()
{
    const int stdout_fd = 1;
    const int stdout_fd_orig = _dup(stdout_fd);
    HANDLE readPipe  = INVALID_HANDLE_VALUE;
    HANDLE writePipe = INVALID_HANDLE_VALUE;
    int writePipe_fd = -1;

    if( ! CreatePipe(&readPipe, &writePipe, nullptr, 64 * 1024) )
    {
        CTILOG_DEBUG(dout, "Could not create pipe for stdout redirection");
    }
    else
    {
        writePipe_fd = _open_osfhandle(reinterpret_cast<intptr_t>(writePipe), 0);

        if( _dup2(writePipe_fd, stdout_fd) )
        {
            CTILOG_DEBUG(dout, "Could not reassign stdout to writePipe " << writePipe_fd);

            _close(writePipe_fd);
            writePipe_fd = -1;
        }
    }

    return { stdout_fd, stdout_fd_orig, readPipe, writePipe, writePipe_fd };
}

std::vector<std::string> restore_printf(const stdout_state state)
{
    std::vector<std::string> results;

    if( state.writePipe_fd != -1 )
    {
        _dup2(state.stdout_fd_orig, state.stdout_fd);
        _close(state.writePipe_fd);
    }
    if( state.readPipe != INVALID_HANDLE_VALUE )
    {
        std::array<char, 8192> buffer;

        DWORD bytesRead;

        for( int chunk = 1; ReadFile(state.readPipe, buffer.data(), buffer.size(), &bytesRead, nullptr) && bytesRead; ++chunk )
        {
            results.emplace_back(
                buffer.data(), 
                buffer.data() + bytesRead);
        }

        CloseHandle(state.readPipe);
    }

    return results;
}

}

auto MeterProgrammingManager::convertRawProgram(const RawProgram &raw, const std::string guid) -> Bytes
{
    //  convert to char
    std::vector<char> charProgram{ raw.program.begin(),  raw.program.end() };
    std::vector<char> charPassword{ raw.password.begin(), raw.password.end() };

    FILEINFO_t fileInfo{ charProgram.data(), static_cast<uint16_t>(charProgram.size()),
                            charPassword.data(), static_cast<uint8_t>(charPassword.size()) };

    globalBuffer.clear();

    auto captureToBuffer = [](void *buf, size_t len) {
        CTILOG_DEBUG(dout, "Conversion callback " << buf << ":" << len);
        auto ucBuf = reinterpret_cast<unsigned char*>(buf);
        globalBuffer.assign(ucBuf, ucBuf + len);
    };

    const auto state = capture_printf();
    
    if( auto error = conProcessBlob(&fileInfo, captureToBuffer) )
    {
        CTILOG_ERROR(dout, "Error processing meter program buffer" << FormattedList::of(
            "Error", error,
            "GUID", guid));
    }

    CTILOG_DEBUG(dout, "Output from RMP converter DLL BEGIN");

    for( const auto& line : restore_printf(state) )
    {
        CTILOG_DEBUG(dout, line);
    }

    CTILOG_DEBUG(dout, "Output from RMP converter DLL END");

    return globalBuffer;
}

std::string MeterProgrammingManager::getAssignedGuid(const RfnIdentifier rfnIdentifier)
{
    CTILOG_DEBUG(dout, rfnIdentifier);

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

        return {};
    }

    return rdr.as<std::string>();
}

bool MeterProgrammingManager::isAssigned(const RfnIdentifier rfnIdentifier, const std::string guid)
{
    CTILOG_DEBUG(dout, rfnIdentifier << ": " << guid);

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
    if( isAssigned(rfnIdentifier, guid) )
    {
        if( const auto totalSize = getProgramSize(guid) )
        {
            const double percentage = 100.0 * size / *totalSize;

            return percentage;
        }
        else
        {
            CTILOG_ERROR(dout, CtiError::GetErrorString(totalSize.error()) << FormattedList::of(
                "GUID", guid,
                "RfnIdentifier", rfnIdentifier));
        }
    }
    return 0.0;
}

IM_EX_CONFIG std::unique_ptr<MeterProgrammingManager> gMeterProgrammingManager = std::make_unique<MeterProgrammingManager>();

}