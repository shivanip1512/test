
#ifndef __DEV_CARRIER_H__
#define __DEV_CARRIER_H__

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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:49 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw\cstring.h>
#include <rw\thr\mutex.h>

#include "dev_dlcbase.h"
#include "tbl_carrier.h"
#include "tbl_metergrp.h"
#include "tbl_loadprofile.h"
#include "tbl_dv_mctiedport.h"


class IM_EX_DEVDB CtiDeviceCarrier : public CtiDeviceDLCBase
{
protected:

   CtiTableDeviceLoadProfile  LoadProfile;
   CtiTableDeviceMeterGroup   MeterGroup;

public:

   typedef CtiDeviceDLCBase Inherited;

   CtiDeviceCarrier() {}

   CtiDeviceCarrier(const CtiDeviceCarrier& aRef)
   {
      *this = aRef;
   }

   virtual ~CtiDeviceCarrier() {}

   CtiDeviceCarrier& operator=(const CtiDeviceCarrier& aRef)
   {
      if(this != &aRef)
      {
         Inherited::operator=(aRef);

         LockGuard guard(monitor());

         MeterGroup        = aRef.getMeterGroup();
         CarrierSettings   = aRef.getCarrierSettings();
      }
      return *this;
   }

   CtiTableDeviceMeterGroup  getMeterGroup() const
   {
      return MeterGroup;
   }

   CtiTableDeviceMeterGroup& getMeterGroup()
   {
      LockGuard guard(monitor());
      return MeterGroup;
   }
   CtiDeviceCarrier& setMeterGroup( const CtiTableDeviceMeterGroup & aMeterGroup )
   {
      LockGuard guard(monitor());
      MeterGroup = aMeterGroup;
      return *this;
   }

   CtiTableDeviceLoadProfile  getLoadProfile() const
   {
      return LoadProfile;
   }
   CtiTableDeviceLoadProfile&  getLoadProfile()
   {
      LockGuard guard(monitor());
      return LoadProfile;
   }
   CtiDeviceCarrier& setLoadProfile( const CtiTableDeviceLoadProfile & aLoadProfile )
   {
      LockGuard guard(monitor());
      LoadProfile = aLoadProfile;
      return *this;
   }

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
   {
      Inherited::getSQL(db, keyTable, selector);
      CtiTableDeviceLoadProfile::getSQL(db, keyTable, selector);
      CtiTableDeviceMeterGroup::getSQL(db, keyTable, selector);
      //  only used/decoded in the MCT 360/370 - so left outer joined
      CtiTableDeviceMCTIEDPort::getSQL(db, keyTable, selector);
   }


   virtual void DecodeDatabaseReader(RWDBReader &rdr)
   {
      INT iTemp;
      RWDBNullIndicator isNull;

      Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

      if(getDebugLevel() & 0x0800) cout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
      LoadProfile.DecodeDatabaseReader(rdr);
      MeterGroup.DecodeDatabaseReader(rdr);
   }

   virtual LONG getLastIntervalDemandRate() const      { return LoadProfile.getLastIntervalDemandRate();}      // From CtiTableDeviceLoadProfile

   virtual bool isMeter() const;
   virtual RWCString getMeterGroupName() const;
   virtual RWCString getAlternateMeterGroupName() const;

};

inline bool CtiDeviceCarrier::isMeter() const                        { return true; }
inline RWCString CtiDeviceCarrier::getMeterGroupName() const            { return getMeterGroup().getCollectionGroup();}
inline RWCString CtiDeviceCarrier::getAlternateMeterGroupName() const   { return getMeterGroup().getTestCollectionGroup();}


#endif // #ifndef __DEV_CARRIER_H__
