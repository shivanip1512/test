#include "precompiled.h"

#include "dev_carrier.h"

using std::string;
using std::endl;

namespace Cti {
namespace Devices {

CarrierDevice::CarrierDevice()
{
    loadProfile.reset(new CtiTableDeviceLoadProfile());
}

boost::shared_ptr<DataAccessLoadProfile> CarrierDevice::getLoadProfile()
{
    return loadProfile;
}

string CarrierDevice::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                     "YP.disableflag, DV.deviceid, DV.alarminhibit, DV.controlinhibit, DCS.address, "
                                     "RTS.routeid, DLP.lastintervaldemandrate, DLP.loadprofiledemandrate, "
                                     "DLP.loadprofilecollection, DLP.voltagedmdinterval, DLP.voltagedmdrate, MCT.deviceid, "
                                     "MCT.connectedied, MCT.password, MCT.iedscanrate, MCT.defaultdataclass, "
                                     "MCT.defaultdataoffset, MCT.realtimescan "
                                   "FROM Device DV, DeviceLoadProfile DLP, DeviceCarrierSettings DCS, YukonPAObject YP "
                                     "LEFT OUTER JOIN DeviceRoutes RTS ON YP.paobjectid = RTS.deviceid "
                                     "LEFT OUTER JOIN DeviceMCTIEDPort MCT ON YP.paobjectid = MCT.deviceid "
                                   "WHERE YP.paobjectid = DLP.deviceid AND YP.paobjectid = DCS.deviceid AND "
                                     "YP.paobjectid = DV.deviceid";

    return sqlCore;
}

void CarrierDevice::DecodeDatabaseReader(RowReader &rdr)
{
    INT iTemp;

    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }

    loadProfile->DecodeDatabaseReader(rdr);
}

LONG CarrierDevice::getLastIntervalDemandRate()
{
    return getLoadProfile()->getLastIntervalDemandRate();
}

inline bool CarrierDevice::isMeter() const
{
    return true;
}

}
}


