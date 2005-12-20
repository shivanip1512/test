
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
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <rw/db/datetime.h>

#include "dbmemobject.h"
#include "yukon.h"


class IM_EX_CTIYUKONDB CtiTableDeviceScanData : public CtiMemDBObject
{
protected:

   LONG                 _deviceID;

   CtiTime         lastFreezeTime;
   CtiTime         prevFreezeTime;
   CtiTime         lastLPTime;

   LONG                 lastFreezeNumber;
   LONG                 prevFreezeNumber;

   CtiTime         _nextScan[ScanRateInvalid];
   CtiTime               _lastCommunicationTime[ScanRateInvalid];

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
   CtiTime getNextScan(INT a) const;
   CtiTableDeviceScanData& setNextScan(INT a, const CtiTime &b);
   CtiTime nextNearestTime(int maxrate = ScanRateInvalid) const;
   CtiTableDeviceScanData& setLastFreezeNumber( const LONG aLastFreezeNumber );
   LONG  getPrevFreezeNumber() const;
   LONG& getPrevFreezeNumber();
   CtiTableDeviceScanData& setPrevFreezeNumber( const LONG aPrevFreezeNumber );
   CtiTime  getLastFreezeTime() const;
   CtiTableDeviceScanData& setLastFreezeTime( const CtiTime& aLastFreezeTime );
   CtiTime  getPrevFreezeTime() const;
   CtiTableDeviceScanData& setPrevFreezeTime( const CtiTime& aPrevFreezeTime );
   CtiTime  getLastLPTime() const;
   CtiTableDeviceScanData& setLastLPTime( const CtiTime& aLastFreezeTime );
   CtiTime getLastCommunicationTime(int i) const;
   CtiTableDeviceScanData& setLastCommunicationTime( int i, const CtiTime& tme );


   void DecodeDatabaseReader( RWDBReader& rdr );
   virtual RWDBStatus Insert();
   virtual RWDBStatus Restore();
   virtual RWDBStatus Update();
   virtual RWDBStatus Delete();
   virtual string getTableName() const;

   RWDBStatus Update(RWDBConnection &conn);

};
#endif // #ifndef __TBL_DV_SCANDATA_H__
