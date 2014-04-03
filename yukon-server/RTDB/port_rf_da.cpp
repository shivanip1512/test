#include "precompiled.h"

#include "logger.h"
#include "port_rf_da.h"
#include "tbl_rfnidentifier.h"

using std::string;
using std::endl;

namespace Cti   {
namespace Ports {

RfDaPort::RfDaPort()
{
}

RfnIdentifier RfDaPort::getRfnIdentifier() const
{
    return _rfnId;
}


std::string RfDaPort::getSQLCoreStatement()
{
    static const std::string sql =
        "SELECT "
            "YP.paobjectid, 'PORT' category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
            "'N' alarminhibit, 'None' commonprotocol, 'Y' performancealarm, 90 performthreshold, '(none)' sharedporttype, 1025 sharedsocketnumber, "
            "RFN.manufacturer, RFN.model, RFN.serialnumber "
        "FROM "
            "YukonPAObject YP "
            "JOIN RfnAddress RFN ON YP.paobjectid = RFN.deviceid "
        "WHERE "
            "type='RF-DA'";

    return sql;
}


void RfDaPort::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    using Database::Tables::RfnIdentifierTable;

    try
    {
        Inherited::DecodeDatabaseReader(rdr);

        _rfnId = RfnIdentifierTable::DecodeDatabaseReader(rdr);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

ULONG RfDaPort::getDelay(int Offset) const
{
    //if( Offset == EXTRA_DELAY )
    return 30;  //  30 seconds for the data read timeout - but we will need to increment timeouts
}

}
}



