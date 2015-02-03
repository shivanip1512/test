#include "precompiled.h"

#include "tbl_rtmacro.h"
#include "database_connection.h"
#include "database_reader.h"

using std::string;

namespace Cti {
namespace Tables {

string MacroRouteTable::getSQLCoreStatement()
{
    static const string sql =
        "SELECT RT.routeid, MR.singlerouteid, MR.routeorder"
        " FROM Route RT, MacroRoute MR "
        " WHERE RT.routeid = MR.routeid"
        " ORDER BY MR.routeid ASC, MR.routeorder ASC";

    return sql;
}

MacroRouteTable::MacroRouteTable(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from MacroRoute");
    }

    rdr["routeid"]       >> RouteID;
    rdr["singlerouteid"] >> _singleRouteID;
    rdr["routeorder"]    >> RouteOrder;
}

std::string MacroRouteTable::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableMacroRoute";
    itemList.add("Macro Standard RouteID / Order") << getSingleRouteID() <<" / "<< getRouteOrder();

    return itemList.toString();
}

LONG  MacroRouteTable::getRouteID() const
{
    return RouteID;
}

INT  MacroRouteTable::getRouteOrder() const
{
    return RouteOrder;
}

INT MacroRouteTable::getSingleRouteID() const
{
    return _singleRouteID;
}

bool MacroRouteTable::operator<(const MacroRouteTable& t2) const
{
    return getRouteOrder() < t2.getRouteOrder();
}

}
}
