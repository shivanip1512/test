#include "precompiled.h"
#include "dev_paging.h"

using namespace Cti::Devices;
using std::string;
using std::endl;

DevicePaging::DevicePaging()
{
}

const CtiTableDeviceTapPaging& DevicePaging::getPaging() const
{
    return _tapTable;
}

string DevicePaging::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                     "YP.disableflag, DV.deviceid, DV.alarminhibit, DV.controlinhibit, CS.portid, "
                                     "DUS.phonenumber, DUS.minconnecttime, DUS.maxconnecttime, DUS.linesettings, "
                                     "DUS.baudrate, IED.password, IED.slaveaddress, TPS.pagernumber, TPS.sender, "
                                     "TPS.securitycode, TPS.postpath "
                                   "FROM Device DV, DeviceTapPagingSettings TPS, DeviceIED IED, DeviceDirectCommSettings CS, "
                                     "YukonPAObject YP LEFT OUTER JOIN DeviceDialupSettings DUS ON "
                                     "YP.paobjectid = DUS.deviceid "
                                   "WHERE YP.paobjectid = TPS.deviceid AND YP.paobjectid = IED.deviceid AND "
                                     "YP.paobjectid = DV.deviceid AND YP.paobjectid = CS.deviceid";

    return sqlCore;
}

void DevicePaging::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }

    _tapTable.DecodeDatabaseReader(rdr);
}
