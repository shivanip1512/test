#include "precompiled.h"

#include "AttributeService.h"
#include "resolvers.h"
#include "numstr.h"
#include "dbaccess.h"
#include "database_reader.h"
#include "database_connection.h"
#include "std_helper.h"

using Cti::Database::DatabaseConnection;
using Cti::Database::DatabaseReader;

/**
 * Returns the point associated with the ExtraPao and Attribute.
 *
 * @param paoId
 * @param attributes
 *
 * @return LitePoints
 */
AttributeService::AttributeMapping AttributeService::getPointsByPaoAndAttributes( int paoId, std::vector<Attribute>& attributes )
{

    // new style Attribute to old style PointAttribute mappings
    //  for things that use CapControl Policy based classes -- regulators and cbc heartbeat stuff

    std::string sql =
        "SELECT "
            "EP.Attribute, "
            "P.PointId, "
            "P.PointType, "
            "P.PointName, "
            "P.PAOBjectId, "
            "P.PointOffset, "
            "P.StateGroupId, "
            "PC.ControlOffset, "
            "PSC.ControlType, "
            "PSC.StateZeroControl, "
            "PSC.StateOneControl, "
            "PSC.CloseTime1, "
            "PSC.CloseTime2, "
            "COALESCE(PACC.MULTIPLIER, PA.MULTIPLIER) AS MULTIPLIER "
        "FROM "
            "ExtraPaoPointAssignment EP "
            "JOIN Point P ON EP.PointId = P.POINTID "
            "LEFT OUTER JOIN POINTCONTROL PC ON P.PointId = PC.PointId "
            "LEFT OUTER JOIN POINTSTATUSCONTROL PSC ON PC.PointId = PSC.PointId "
            "LEFT OUTER JOIN POINTACCUMULATOR PACC ON PACC.POINTID = P.POINTID "
            "LEFT OUTER JOIN POINTANALOG PA ON PA.POINTID = P.POINTID "
        "WHERE "
            "EP.PAObjectId = ? "
                "AND EP.Attribute IN (";
 
    for (const auto & attribute : attributes)
    {
        sql += "'" + attribute.getName() + "'";
        sql += ",";
    }

    sql.replace(sql.find_last_of(","), 1, ")");

    DatabaseConnection  conn;
    DatabaseReader      rdr(conn, sql);

    rdr << paoId;

    rdr.execute();

    AttributeMapping pointMapping;

    while ( rdr() )
    {
        try 
        {
            pointMapping.emplace( Attribute::Lookup( rdr["Attribute"].as<std::string>() ), rdr );
        }
        catch( AttributeNotFound::exception & ex )
        {
            CTILOG_EXCEPTION_WARN( dout, ex );
        }
    }

    return pointMapping;

}

LitePoint AttributeService::getLitePointById(int pointId)
{
    std::vector<int> pointIds;
    pointIds.push_back(pointId);

    std::vector<LitePoint> points = getLitePointsById(pointIds);

    if (points.size() != 1)
    {
        return LitePoint();
    }

    return points.front();
}

std::vector<LitePoint> AttributeService::getLitePointsById(const std::vector<int>& pointIds)
{
    std::vector<LitePoint> points;

    std::string sql =
        "SELECT "
            "P.PointId, "
            "P.PointType, "
            "P.PointName, "
            "P.PAOBjectId, "
            "P.PointOffset, "
            "P.StateGroupId, "
            "PC.ControlOffset, "
            "PSC.ControlType, "
            "PSC.StateZeroControl, "
            "PSC.StateOneControl, "
            "PSC.CloseTime1, "
            "PSC.CloseTime2, "
            "COALESCE(PACC.MULTIPLIER, PA.MULTIPLIER) AS MULTIPLIER "
        "FROM "
            "Point P "
            "LEFT OUTER JOIN POINTCONTROL PC ON P.PointId = PC.PointId "
            "LEFT OUTER JOIN POINTSTATUSCONTROL PSC ON PC.PointId = PSC.PointId "
            "LEFT OUTER JOIN POINTACCUMULATOR PACC ON PACC.POINTID = P.POINTID "
            "LEFT OUTER JOIN POINTANALOG PA ON PA.POINTID = P.POINTID "
        "WHERE "
            "P.POINTID IN (";

    for ( const int pointId : pointIds )
    {
        sql += std::to_string( pointId );
        sql += ",";
    }

    sql.replace(sql.find_last_of(","),1,")");

    {
        DatabaseConnection conn;
        DatabaseReader rdr(conn, sql);
        rdr.execute();

        while(rdr.isValid() && rdr() )
        {
            LitePoint point( rdr );

            points.push_back(point);
        }
    }

    return points;
}
