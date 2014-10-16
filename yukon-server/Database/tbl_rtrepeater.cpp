#include "precompiled.h"

#include "tbl_rtrepeater.h"
#include "logger.h"

using std::string;
using std::endl;

std::string CtiTableRepeaterRoute::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableRepeaterRoute";
    itemList.add("Repeater DeviceID")      << DeviceID;
    itemList.add("Repeater RouteID")       << _routeID;
    itemList.add("Repeater Variable Bits") << VarBit;
    itemList.add("Repeater Order")         << RepeaterOrder;

    return itemList.toString();
}

string CtiTableRepeaterRoute::getSQLCoreStatement()
{
    static const string sql =  "SELECT RT.routeid, RP.deviceid, RP.variablebits, RP.repeaterorder "
                               "FROM Route RT, RepeaterRoute RP "
                               "WHERE RT.routeid = RP.routeid ORDER BY RT.routeid ASC, RP.repeaterorder ASC";

    return sql;
}

CtiTableRepeaterRoute::CtiTableRepeaterRoute(Cti::RowReader &rdr)
{
    string rwsTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    rdr["deviceid"] >> DeviceID;
    rdr["routeid"] >> _routeID;

    rdr["variablebits"] >> rwsTemp;
    VarBit = atoi(rwsTemp.c_str());

    rdr["repeaterorder"] >> RepeaterOrder;
}

RWBoolean CtiTableRepeaterRoute::operator<( const CtiTableRepeaterRoute& RP )
{
    return(RepeaterOrder < RP.getRepeaterOrder() );
}

RWBoolean CtiTableRepeaterRoute::operator==( const CtiTableRepeaterRoute& RP )
{
    // This better not ever happen!
    return( RepeaterOrder == RP.getRepeaterOrder() );
}

string CtiTableRepeaterRoute::getTableName()
{
    return "RepeaterRoute";
}

