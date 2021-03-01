#include "precompiled.h"

#include "sql_util.h"

namespace Cti::Database {

std::string getDeviceGroupTables(std::string memberTableAlias, size_t groupSegments)
{
    std::string sql = "DeviceGroupMember " + memberTableAlias;

    std::string childTableAlias = memberTableAlias;
    std::string childColumn = "devicegroupid";

    //  Join to each table in the group heirarchy
    for( size_t index = 0; index <= groupSegments; index++ )
    {
        auto parentTableAlias = "DG" + std::to_string(groupSegments - index);

        sql += " JOIN DeviceGroup " + parentTableAlias;
        sql += " ON " + parentTableAlias + ".devicegroupid"
                " = " + childTableAlias + "." + childColumn;

        childTableAlias = parentTableAlias;
        childColumn = "parentdevicegroupid";
    }

    return sql;
}

std::string getDeviceGroupWhere(std::string prefix, size_t groupSegments)
{
    //  Anchor the root group
    std::string sql = 
        "DG0.parentdevicegroupid IS NULL"
        " AND DG0.groupname = ' '";

    //  Add the group name checks
    for( size_t i = 1; i <= groupSegments; i++ )
    {
        sql += " AND DG" + std::to_string(i) + ".groupname = ?";
    }

    return sql;
}

std::string getRfnAddressInDeviceGroupSql(size_t groupSegments)
{
    const auto tables_sql = getDeviceGroupTables("dgm", groupSegments);
    const auto where_sql = getDeviceGroupWhere("dgm", groupSegments);

    return
        "SELECT COUNT(*)"
        " FROM " + tables_sql +
            " JOIN RfnAddress r ON r.DeviceId=dgm.YukonPaoId"
        " WHERE " + where_sql +
            " AND r.Manufacturer = ?"
            " AND r.Model = ?"
            " AND r.SerialNumber = ?";
}

}