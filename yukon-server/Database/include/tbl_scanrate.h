
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_scanrate
*
* Date:   8/18/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_scanrate.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:58:20 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TBL_SCANRATE_H__
#define __TBL_SCANRATE_H__

#include <rw/db/reader.h>
#include <rw\cstring.h>
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/datetime.h>
#include <rw/rwtime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "desolvers.h"
#include "yukon.h"
#include "types.h"
#include "logger.h"

/*-----------------------------------------------------------------------------*
 *  Makes use of resolveScanType(RWCString)
 *-----------------------------------------------------------------------------*/

class IM_EX_CTIYUKONDB CtiTableDeviceScanRate : public CtiMemDBObject
{
protected:

   LONG        _deviceID;
   LONG        _scanType;      // iScanRate, Accumulator, Integrity.
   INT         _scanGroup;
   LONG        _scanRate;
   INT         _alternateRate;

   // Bookkeeping
   BOOL        _updated;      // Used to determine updated state of scan rate...

public:

   CtiTableDeviceScanRate();

   CtiTableDeviceScanRate(const CtiTableDeviceScanRate &aRef);

   CtiTableDeviceScanRate& operator=(const CtiTableDeviceScanRate &aRef);

   LONG getScanType() const;
   CtiTableDeviceScanRate& setScanType( const LONG aScanRate );

   LONG getDeviceID() const;
   CtiTableDeviceScanRate& setDeviceID( const LONG did );

   INT getScanGroup() const;
   CtiTableDeviceScanRate& setScanGroup( const INT aInt );

   INT getAlternateRate() const;
   CtiTableDeviceScanRate& setAlternateRate( const INT bInt );

   LONG getScanRate() const;
   CtiTableDeviceScanRate& setScanRate( const LONG st );

   BOOL getUpdated() const;
   CtiTableDeviceScanRate& setUpdated( const BOOL aBool );

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   virtual void DumpData();

   static RWCString getTableName();

   virtual RWDBStatus Restore();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Delete();

};
#endif // #ifndef __TBL_SCANRATE_H__
