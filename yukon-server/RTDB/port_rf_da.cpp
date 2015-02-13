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
            "type='RFN-1200'"; //  Future RF-DA device types will need to be added here as well.

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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}


static std::atomic<long> RfDaConcurrentRequests = 0;


void RfDaPort::incQueueSubmittal()
{
    ++RfDaConcurrentRequests;

    CtiPort::incQueueSubmittal();
}


void RfDaPort::incQueueProcessed()
{
    --RfDaConcurrentRequests;

    CtiPort::incQueueProcessed();
}


unsigned RfDaPort::concurrentRequests() const
{
    return RfDaConcurrentRequests;
}


}
}



