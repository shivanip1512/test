/*-----------------------------------------------------------------------------*
*
* File:   dev_carrier
*
* Date:   5/20/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.11.2.1 $
* DATE         :  $Date: 2008/11/18 20:11:28 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "dev_carrier.h"

using namespace Cti;

CtiDeviceCarrier::CtiDeviceCarrier()
{
    loadProfile.reset(new CtiTableDeviceLoadProfile());
}

CtiDeviceCarrier::CtiDeviceCarrier(const CtiDeviceCarrier &aRef)
{
    *this = aRef;
}

CtiDeviceCarrier::~CtiDeviceCarrier() {}

CtiDeviceCarrier &CtiDeviceCarrier::operator=(const CtiDeviceCarrier &aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        CtiLockGuard<CtiMutex> guard(_classMutex);

        CarrierSettings   = aRef.getCarrierSettings();
    }

    return *this;
}

boost::shared_ptr<DataAccessLoadProfile> CtiDeviceCarrier::getLoadProfile()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    return boost::static_pointer_cast<DataAccessLoadProfile>(loadProfile);
}

string CtiDeviceCarrier::getSQLCoreStatement() const
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

void CtiDeviceCarrier::DecodeDatabaseReader(Cti::RowReader &rdr)
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

LONG CtiDeviceCarrier::getLastIntervalDemandRate()
{
    return getLoadProfile()->getLastIntervalDemandRate();
}

inline bool CtiDeviceCarrier::isMeter() const
{
    return true;
}

