#include "yukon.h"

#include "tbl_rtrepeater.h"
#include "logger.h"
#include "rwutil.h"

void CtiTableRepeaterRoute::DumpData()
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << " Repeater DeviceID                          " << DeviceID << endl;
    dout << " Repeater RouteID                           " << _routeID << endl;
    dout << " Repeater Variable Bits                     " << VarBit << endl;
    dout << " Repeater Order                             " << RepeaterOrder << endl;
}

void CtiTableRepeaterRoute::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table("Route");
    RWDBTable routetbl = db.table(getTableName().c_str() );

    selector <<
    keyTable["routeid"] <<
    routetbl["deviceid"] <<
    routetbl["variablebits"] <<
    routetbl["repeaterorder"];

    selector.from(keyTable);
    selector.from(routetbl);

    selector.where( selector.where() && keyTable["routeid"] == routetbl["routeid"]);

    selector.orderBy(keyTable["routeid"]);
    selector.orderBy(routetbl["repeaterorder"]);
}

CtiTableRepeaterRoute::CtiTableRepeaterRoute(RWDBReader &rdr)
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

RWBoolean CtiTableRepeaterRoute::operator<( const CtiTableRepeaterRoute& t2 )
{
    return(RepeaterOrder < t2.getRepeaterOrder() );
}

RWBoolean CtiTableRepeaterRoute::operator==( const CtiTableRepeaterRoute& t2 )
{
    // This better not ever happen!
    return( RepeaterOrder == t2.getRepeaterOrder() );
}

string CtiTableRepeaterRoute::getTableName()
{
    return "RepeaterRoute";
}

