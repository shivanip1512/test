/*-----------------------------------------------------------------------------*
*
* File:   dev_carrier
*
* Class:  CtiDeviceCarrier
* Date:   8/19/1999
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_carrier.h-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2008/10/28 19:21:43 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_CARRIER_H__
#define __DEV_CARRIER_H__


#include <rw\thr\mutex.h>

#include "dev_dlcbase.h"
#include "tbl_carrier.h"
#include "tbl_metergrp.h"
#include "tbl_loadprofile.h"
#include "tbl_dv_mctiedport.h"


class IM_EX_DEVDB CtiDeviceCarrier : public CtiDeviceDLCBase
{
private:

    typedef CtiDeviceDLCBase Inherited;

protected:

   CtiTableDeviceLoadProfile  LoadProfile;

public:

   CtiDeviceCarrier();
   CtiDeviceCarrier(const CtiDeviceCarrier &aRef);

   virtual ~CtiDeviceCarrier();

   CtiDeviceCarrier &operator=(const CtiDeviceCarrier &aRef);

   CtiTableDeviceLoadProfile  getLoadProfile() const;
   CtiTableDeviceLoadProfile &getLoadProfile();

   CtiDeviceCarrier &setLoadProfile( const CtiTableDeviceLoadProfile &aLoadProfile );

   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const;

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   virtual LONG getLastIntervalDemandRate() const;

   virtual bool isMeter() const;
};


#endif // #ifndef __DEV_CARRIER_H__
