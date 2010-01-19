#include "yukon.h"

#include "AttributeService.h"
#include "resolvers.h"
#include "numstr.h"
#include "dbaccess.h"
#include "rwutil.h"

#include <rw/db/db.h>

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

        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBReader rdr = ExecuteQuery( conn, sql );

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
    int pointId;
    string attributeName = attribute.name();

    {
        string sql("SELECT pointId FROM ExtraPaoPointAssignment WHERE paoId = ");
        sql += CtiNumStr(paoId);
        sql += " AND Attribute = '" + attributeName + "'";

        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBReader rdr = ExecuteQuery( conn, sql );

        if(rdr.isValid() && rdr() )
        {
            rdr["pointId"] >> pointId;
        }
    }

    LitePoint point = getLitePointsById(pointId);

    return point;
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

    string sql("SELECT PointId,PointType,PointName,PAOBjectId,PointOffset FROM Point WHERE pointid in (");
    for each(int pointId in pointIds)
    {
        sql += CtiNumStr(pointId);
        sql += ",";
    }
    sql.replace(sql.find_last_of(","),1,")");

    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBReader rdr = ExecuteQuery( conn, sql );

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

            points.push_back(point);
        }
    }

    return points;
}
