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
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2006/09/21 21:31:37 $
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
   CtiTableDeviceMeterGroup   MeterGroup;

public:

   CtiDeviceCarrier();
   CtiDeviceCarrier(const CtiDeviceCarrier &aRef);

   virtual ~CtiDeviceCarrier();

   CtiDeviceCarrier &operator=(const CtiDeviceCarrier &aRef);

   CtiTableDeviceMeterGroup  getMeterGroup() const;
   CtiTableDeviceMeterGroup &getMeterGroup();

   CtiDeviceCarrier &setMeterGroup( const CtiTableDeviceMeterGroup &aMeterGroup );

   CtiTableDeviceLoadProfile  getLoadProfile() const;
   CtiTableDeviceLoadProfile &getLoadProfile();

   CtiDeviceCarrier &setLoadProfile( const CtiTableDeviceLoadProfile &aLoadProfile );

   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   virtual LONG getLastIntervalDemandRate() const;

   virtual bool isMeter() const;
   virtual string getMeterGroupName() const;
   virtual string getAlternateMeterGroupName() const;
   virtual string getBillingGroupName() const;
};


#endif // #ifndef __DEV_CARRIER_H__
