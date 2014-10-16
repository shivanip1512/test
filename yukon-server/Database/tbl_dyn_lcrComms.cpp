#include "precompiled.h"

#include "logger.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_writer.h"
#include "database_util.h"
#include "tbl_dyn_lcrComms.h"




CtiTableDynamicLcrCommunications::CtiTableDynamicLcrCommunications()
    :   _needPreInsert( true ),
        _deviceID( -1 )
{
    // empty...
}


void CtiTableDynamicLcrCommunications::DecodeDatabaseReader( Cti::RowReader & rdr )
{
    if ( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG(dout, "Decoding DB read from DynamicLcrCommunications");
    }

    if ( ! rdr["LcrCommsExist"].isNull() )
    {
        _needPreInsert = false;
    }

    rdr["deviceid"] >> _deviceID;
}


bool CtiTableDynamicLcrCommunications::prepareTableForUpdates()
{
    if ( _deviceID == -1 )
    {
        CTILOG_ERROR(dout, "Dynamic LCR communications collection disabled. DeviceID not set.");
        
        return false;
    }

    if ( _needPreInsert )
    {
        const std::string sql =
            "INSERT INTO "
                "DynamicLcrCommunications (DeviceId) "
            "VALUES "
                "(" + CtiNumStr( _deviceID ) + ")";

        Cti::Database::DatabaseConnection   connection;
        Cti::Database::DatabaseWriter       writer( connection, sql );

        _needPreInsert = ! Cti::Database::executeCommand( writer, __FILE__, __LINE__, Cti::Database::LogDebug::Enable );
    }

    return ! _needPreInsert;
}


void CtiTableDynamicLcrCommunications::updateRelayRuntime( const int relay_number, const CtiTime & latest_runtime )
{
    const std::string column_name = "Relay" + CtiNumStr( relay_number ) + "Runtime";

    updateTime( column_name, latest_runtime );
    updateNonZeroRuntime( latest_runtime );
}


void CtiTableDynamicLcrCommunications::updateNonZeroRuntime( const CtiTime & nonzero_runtime )
{
    updateTime( "LastNonZeroRuntime", nonzero_runtime );
}


void CtiTableDynamicLcrCommunications::updateLastCommsTime( const CtiTime & latest_comms )
{
    updateTime( "LastCommunication", latest_comms );
}


void CtiTableDynamicLcrCommunications::updateTime( const std::string column_name, const CtiTime & new_time )
{
    if ( prepareTableForUpdates() )
    {
        const std::string sql =
            "UPDATE "
                "DynamicLcrCommunications "
            "SET "
                + column_name + " = "
                "(CASE "
                    "WHEN COALESCE(" + column_name + ", CAST('01-JAN-1900' AS DATE)) < ? THEN ? "
                    "ELSE " + column_name + " "
                "END) "
            "WHERE "
                "DeviceId = " + CtiNumStr( _deviceID );

        Cti::Database::DatabaseConnection   connection;
        Cti::Database::DatabaseWriter       writer( connection, sql );

        writer
            << new_time
            << new_time;

        Cti::Database::executeCommand( writer, __FILE__, __LINE__, Cti::Database::LogDebug::Enable );
    }
}

