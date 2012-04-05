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

AttributeService::AttributeService()
{
}

/**
 * Returns all Points listed as extra paos.
 *
 *
 * @param paoId
 *
 * @return std::list<LitePoint>
 */
std::list<LitePoint> AttributeService::getExtraPaoPoints(int paoId)
{
    std::list<int> pointIds;

    {
        string sql("SELECT pointId FROM ExtraPaoPointAssignment WHERE paoId = ");
        sql += CtiNumStr(paoId);

        DatabaseConnection conn;
        DatabaseReader rdr(conn, sql);
        rdr.execute();

        int pointId;
        while(rdr.isValid() && rdr() )
        {
            rdr["pointId"] >> pointId;
            pointIds.push_back(pointId);
        }
    }

    //Call Point function
    std::list<LitePoint> points = getLitePointsById(pointIds);

    return points;
}

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

    return pointId == 0 ? LitePoint() : getLitePointsById(pointId);
}

LitePoint AttributeService::getLitePointsById(int pointId)
{
    std::list<int> pointIds;
    pointIds.push_back(pointId);

    std::list<LitePoint> points = getLitePointsById(pointIds);

    if (points.size() != 1)
    {
        return LitePoint();
    }

    return points.front();
}

std::list<LitePoint> AttributeService::getLitePointsById(const std::list<int>& pointIds)
{
    std::list<LitePoint> points;

    std::string sql( "SELECT P.PointId, PointType, PointName, PAOBjectId, PointOffset, StateOneControl"
                     " FROM Point P LEFT OUTER JOIN POINTSTATUS PS ON P.PointId = PS.PointId"
                     " WHERE P.PointId in (" );

    for each(int pointId in pointIds)
    {
        sql += CtiNumStr(pointId);
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
            int temp;
            string tempStr;

            rdr["PointId"] >> temp;
            point.setPointId(temp);

            rdr["PointType"] >> tempStr;
            CtiPointType_t type = resolvePointType(tempStr);
            point.setPointType(type);

            rdr["PointName"] >> tempStr;
            point.setPointName(tempStr);

            rdr["PAOBjectId"] >> temp;
            point.setPaoId(temp);

            rdr["PointOffset"] >> temp;
            point.setPointOffset(temp);

            if ( ! rdr["StateOneControl"].isNull() )
            {
                rdr["StateOneControl"] >> tempStr;
                point.setStateOneControl(tempStr);
            }

            points.push_back(point);
        }
    }

    return points;
}
