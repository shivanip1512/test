#include "yukon.h"
#include "dev_paging_tap_base.h"

DeviceTapPagingTerminalBase::DeviceTapPagingTerminalBase()
{
}

DeviceTapPagingTerminalBase::DeviceTapPagingTerminalBase(const DeviceTapPagingTerminalBase& aRef)
{
    
}

DeviceTapPagingTerminalBase::~DeviceTapPagingTerminalBase()
{
}

DeviceTapPagingTerminalBase& DeviceTapPagingTerminalBase::operator=(const DeviceTapPagingTerminalBase& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        _tapTable = aRef.getTap();
    }
    return *this;
}

const CtiTableDeviceTapPaging& DeviceTapPagingTerminalBase::getTap() const
{
    return _tapTable;
}

string DeviceTapPagingTerminalBase::getSQLCoreStatement() const
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

void DeviceTapPagingTerminalBase::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    _tapTable.DecodeDatabaseReader(rdr);
}
