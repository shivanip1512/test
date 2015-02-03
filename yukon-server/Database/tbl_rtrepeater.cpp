#include "precompiled.h"

#include "tbl_rtrepeater.h"
#include "row_reader.h"
#include "dllbase.h"
#include "logger.h"

using std::string;

std::string CtiTableRepeaterRoute::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableRepeaterRoute";
    itemList.add("Repeater DeviceID")      << _deviceID;
    itemList.add("Repeater RouteID")       << _routeID;
    itemList.add("Repeater Variable Bits") << _varBit;
    itemList.add("Repeater Order")         << _repeaterOrder;

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
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from RepeaterRoute");
    }

    rdr["deviceid"] >> _deviceID;
    rdr["routeid"]  >> _routeID;

    string varBitStr;
    rdr["variablebits"] >> varBitStr;
    _varBit = atoi(varBitStr.c_str());

    rdr["repeaterorder"] >> _repeaterOrder;
}

bool CtiTableRepeaterRoute::operator<( const CtiTableRepeaterRoute& RP ) const
{
    return _repeaterOrder < RP.getRepeaterOrder();
}

