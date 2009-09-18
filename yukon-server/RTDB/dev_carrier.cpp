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

void CtiDeviceCarrier::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableDeviceLoadProfile::getSQL(db, keyTable, selector);
    //  only used/decoded in the MCT 360/370/470 - so left outer joined
    CtiTableDeviceMCTIEDPort::getSQL(db, keyTable, selector);
}


void CtiDeviceCarrier::DecodeDatabaseReader(RWDBReader &rdr)
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

