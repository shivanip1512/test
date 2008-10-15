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
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2008/10/15 17:03:44 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include "dev_carrier.h"


CtiDeviceCarrier::CtiDeviceCarrier() {}

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

        LockGuard guard(monitor());

        CarrierSettings   = aRef.getCarrierSettings();
    }

    return *this;
}

CtiTableDeviceLoadProfile CtiDeviceCarrier::getLoadProfile() const
{
    return LoadProfile;
}

CtiTableDeviceLoadProfile &CtiDeviceCarrier::getLoadProfile()
{
    LockGuard guard(monitor());
    return LoadProfile;
}

CtiDeviceCarrier &CtiDeviceCarrier::setLoadProfile( const CtiTableDeviceLoadProfile &aLoadProfile )
{
    LockGuard guard(monitor());
    LoadProfile = aLoadProfile;
    return *this;
}

void CtiDeviceCarrier::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
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
    LoadProfile.DecodeDatabaseReader(rdr);
}

LONG CtiDeviceCarrier::getLastIntervalDemandRate() const
{
    return LoadProfile.getLastIntervalDemandRate();      // From CtiTableDeviceLoadProfile
}

inline bool CtiDeviceCarrier::isMeter() const
{
    return true;
}

