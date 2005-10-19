
#pragma warning( disable : 4786)
#ifndef __TBL_DV_SCANDATA_H__
#define __TBL_DV_SCANDATA_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_scandata
*
* Class:  CtiTableDeviceScanData
* Date:   8/16/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_dv_scandata.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/10/19 19:10:21 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <rw/db/datetime.h>
#include <rw/rwtime.h>
#include <rw/cstring.h>

#include "dbmemobject.h"
#include "yukon.h"


class IM_EX_CTIYUKONDB CtiTableDeviceScanData : public CtiMemDBObject
{
protected:

   LONG                 _deviceID;

   RWDBDateTime         lastFreezeTime;
   RWDBDateTime         prevFreezeTime;
   RWDBDateTime         lastLPTime;

   LONG                 lastFreezeNumber;
   LONG                 prevFreezeNumber;

   RWDBDateTime         _nextScan[ScanRateInvalid];
   RWTime               _lastCommunicationTime[ScanRateInvalid];

private:

   public:
   CtiTableDeviceScanData(LONG did = 0);
   CtiTableDeviceScanData(const CtiTableDeviceScanData& aRef);
   virtual ~CtiTableDeviceScanData();

   CtiTableDeviceScanData& operator=(const CtiTableDeviceScanData& aRef);
   LONG getDeviceID() const;
   CtiTableDeviceScanData& setDeviceID(LONG id);

   LONG  getLastFreezeNumber() const;
   LONG& getLastFreezeNumber();
   RWTime getNextScan(INT a) const;
   CtiTableDeviceScanData& setNextScan(INT a, const RWTime &b);
   RWTime nextNearestTime(int maxrate = ScanRateInvalid) const;
   CtiTableDeviceScanData& setLastFreezeNumber( const LONG aLastFreezeNumber );
   LONG  getPrevFreezeNumber() const;
   LONG& getPrevFreezeNumber();
   CtiTableDeviceScanData& setPrevFreezeNumber( const LONG aPrevFreezeNumber );
   RWTime  getLastFreezeTime() const;
   CtiTableDeviceScanData& setLastFreezeTime( const RWTime& aLastFreezeTime );
   RWTime  getPrevFreezeTime() const;
   CtiTableDeviceScanData& setPrevFreezeTime( const RWTime& aPrevFreezeTime );
   RWTime  getLastLPTime() const;
   CtiTableDeviceScanData& setLastLPTime( const RWTime& aLastFreezeTime );
   RWTime getLastCommunicationTime(int i) const;
   CtiTableDeviceScanData& setLastCommunicationTime( int i, const RWTime& tme );


   void DecodeDatabaseReader( RWDBReader& rdr );
   virtual RWDBStatus Insert();
   virtual RWDBStatus Restore();
   virtual RWDBStatus Update();
   virtual RWDBStatus Delete();
   virtual RWCString getTableName() const;

   RWDBStatus Update(RWDBConnection &conn);

};
#endif // #ifndef __TBL_DV_SCANDATA_H__
