#include "yukon.h"

#include "tbl_rtrepeater.h"
#include "logger.h"

using std::string;
using std::endl;

void CtiTableRepeaterRoute::DumpData()
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << " Repeater DeviceID                          " << DeviceID << endl;
    dout << " Repeater RouteID                           " << _routeID << endl;
    dout << " Repeater Variable Bits                     " << VarBit << endl;
    dout << " Repeater Order                             " << RepeaterOrder << endl;
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

