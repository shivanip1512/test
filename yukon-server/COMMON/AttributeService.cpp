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
 * @param attribute
 *
 * @return LitePoint
 */
LitePoint AttributeService::getPointByPaoAndAttribute(int paoId, const Attribute& attribute)
{
    // new style Attribute to old style PointAttribute mappings
    //  for things that use CapControl Policy based classes -- regulators and cbc heartbeat stuff

    static const std::map< std::string, std::string >  _translation
    {
        { "SOURCE_VOLTAGE",         "VOLTAGE_X"         },
        { "VOLTAGE",                "VOLTAGE_Y"         },
        { "HEARTBEAT_TIMER_CONFIG", "KEEP_ALIVE_TIMER"  }
    };

    static const std::string sql = 
        "SELECT "
            "PointId "
        "FROM "
            "ExtraPaoPointAssignment "
        "WHERE "
            "PAObjectId = ? "
                "AND Attribute = ?";

	std::string name = Cti::mapFindOrDefault( _translation, attribute.getName(), attribute.getName() );

    DatabaseConnection  conn;
    DatabaseReader      rdr( conn, sql );

    rdr
        << paoId
        << name
            ;

    rdr.execute();

    int pointId = 0;

    if ( rdr() )
    {
        rdr["PointId"] >> pointId;
    }

    return pointId == 0 ? LitePoint() : getLitePointById(pointId);
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
            "X.MULTIPLIER "
        "FROM "
            "Point P "
            "LEFT OUTER JOIN POINTCONTROL PC ON P.PointId = PC.PointId "
            "LEFT OUTER JOIN POINTSTATUSCONTROL PSC ON PC.PointId = PSC.PointId "
            "LEFT OUTER JOIN ("
                "SELECT "
                    "PACC.POINTID, "
                    "PACC.MULTIPLIER "
                "FROM "
                    "POINTACCUMULATOR PACC "
                    "JOIN POINT P ON PACC.POINTID = P.POINTID "
                "UNION "
                "SELECT "
                    "PA.POINTID, "
                    "PA.MULTIPLIER "
                "FROM "
                    "POINTANALOG PA "
                    "JOIN POINT P ON PA.POINTID = P.POINTID"
                ") X ON X.POINTID = P.POINTID "
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
