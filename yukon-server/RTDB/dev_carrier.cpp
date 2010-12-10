#include "yukon.h"

#include "dev_carrier.h"

namespace Cti {
namespace Devices {

CarrierDevice::CarrierDevice()
{
    loadProfile.reset(new CtiTableDeviceLoadProfile());
}

CarrierDevice::CarrierDevice(const CarrierDevice &aRef)
{
    *this = aRef;
}

CarrierDevice::~CarrierDevice() {}

CarrierDevice &CarrierDevice::operator=(const CarrierDevice &aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        CtiLockGuard<CtiMutex> guard(_classMutex);

        CarrierSettings   = aRef.CarrierSettings;
    }

    return *this;
}

boost::shared_ptr<DataAccessLoadProfile> CarrierDevice::getLoadProfile()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    return boost::static_pointer_cast<DataAccessLoadProfile>(loadProfile);
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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
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


