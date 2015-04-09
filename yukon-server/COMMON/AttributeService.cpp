#include "precompiled.h"

#include "AttributeService.h"
#include "resolvers.h"
#include "numstr.h"
#include "dbaccess.h"
#include "database_reader.h"
#include "database_connection.h"


using Cti::Database::DatabaseConnection;
using Cti::Database::DatabaseReader;
using std::string;

/**
 * Returns the point associated with the ExtraPao and Attribute.
 *
 * @param paoId
 * @param attribute
 *
 * @return LitePoint
 */
LitePoint AttributeService::getPointByPaoAndAttribute(int paoId, const PointAttribute& attribute)
{
    int pointId = 0;
    string attributeName = attribute.name();

    {
        string sql("SELECT pointId FROM ExtraPaoPointAssignment WHERE paobjectid = ");
        sql += CtiNumStr(paoId);
        sql += " AND Attribute = '" + attributeName + "'";

        DatabaseConnection conn;
        DatabaseReader rdr(conn, sql);
        rdr.execute();

        if(rdr.isValid() && rdr() )
        {
            rdr["pointId"] >> pointId;
        }
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
            "PSC.StateZeroControl, "
            "PSC.StateOneControl, "
            "PC.ControlOffset, "
            "X.MULTIPLIER "
        "FROM "
            "Point P "
            "LEFT OUTER JOIN POINTSTATUSCONTROL PSC ON P.PointId = PSC.PointId "
            "LEFT OUTER JOIN POINTCONTROL PC ON P.PointId = PC.PointId "
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
            LitePoint point;
            int tempInt;
            string tempStr;
            double  multiplier;

            rdr["PointId"] >> tempInt;
            point.setPointId(tempInt);

            rdr["PointType"] >> tempStr;
            point.setPointType(resolvePointType(tempStr));

            rdr["PointName"] >> tempStr;
            point.setPointName(tempStr);

            rdr["PAOBjectId"] >> tempInt;
            point.setPaoId(tempInt);

            rdr["PointOffset"] >> tempInt;
            point.setPointOffset(tempInt);

            if ( ! rdr["StateZeroControl"].isNull() )
            {
                rdr["StateZeroControl"] >> tempStr;
                point.setStateZeroControl(tempStr);
            }

            if ( ! rdr["StateOneControl"].isNull() )
            {
                rdr["StateOneControl"] >> tempStr;
                point.setStateOneControl(tempStr);
            }

            if ( ! rdr["ControlOffset"].isNull() )
            {
                rdr["ControlOffset"] >> tempInt;
                point.setControlOffset(tempInt);
            }

            if ( ! rdr["MULTIPLIER"].isNull() )
            {
                rdr["MULTIPLIER"] >> multiplier;
                point.setMultiplier(multiplier);
            }

            points.push_back(point);
        }
    }

    return points;
}
