
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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:42 $
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

   union
   {
      UINT _flags;

      struct
      {
         UINT      starting       : 1;
         UINT      frozen         : 1;
         UINT      freezePending  : 1;
         UINT      pending        : 1;
         UINT      freezeFailed   : 1;
         UINT      resetting      : 1;
         UINT      resetFailed    : 1;
         UINT      forced         : 1;
         UINT      exception      : 1;
         UINT      integrity      : 1;
         UINT      meterread      : 1;    // On a DLC scan this is set iff accums are to be read!
      };
   };

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

   UINT  getFlags() const;

   INT  clearScanFlags(INT i = 0);
   INT  resetScanFlags(INT i = 0);
   BOOL isScanStarting() const;
   BOOL setScanStarting(BOOL b = TRUE);
   BOOL resetScanStarting(BOOL b = FALSE);
   BOOL isScanIntegrity() const;
   BOOL setScanIntegrity(BOOL b = TRUE);
   BOOL resetScanIntegrity(BOOL b = FALSE);
   BOOL isScanFrozen() const;
   BOOL setScanFrozen(BOOL b = TRUE);
   BOOL resetScanFrozen(BOOL b = FALSE);
   BOOL isScanFreezePending() const;
   BOOL setScanFreezePending(BOOL b = TRUE);
   BOOL resetScanFreezePending(BOOL b = FALSE);
   BOOL isScanPending() const;
   BOOL setScanPending(BOOL b = TRUE);
   BOOL resetScanPending(BOOL b = FALSE);
   BOOL isScanFreezeFailed() const;
   BOOL setScanFreezeFailed(BOOL b = TRUE);
   BOOL resetScanFreezeFailed(BOOL b = FALSE);
   BOOL isScanResetting() const;
   BOOL setScanResetting(BOOL b = TRUE);
   BOOL resetScanResetting(BOOL b = FALSE);
   BOOL isScanResetFailed() const;
   BOOL setScanResetFailed(BOOL b = TRUE);
   BOOL resetScanResetFailed(BOOL b = FALSE);
   BOOL isScanForced() const;
   BOOL setScanForced(BOOL b = TRUE);
   BOOL resetScanForced(BOOL b = FALSE);
   BOOL isScanException() const;
   BOOL setScanException(BOOL b = TRUE);
   BOOL resetScanException(BOOL b = FALSE);
   BOOL isMeterRead() const;
   BOOL setMeterRead(BOOL b = TRUE);
   BOOL resetMeterRead(BOOL b = FALSE);
   LONG  getLastFreezeNumber() const;
   LONG& getLastFreezeNumber();
   RWTime getNextScan(INT a) const;
   CtiTableDeviceScanData& setNextScan(INT a, const RWTime &b);
   RWTime nextNearestTime() const;
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

};
#endif // #ifndef __TBL_DV_SCANDATA_H__
