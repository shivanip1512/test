
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_scanrate
*
* Date:   8/18/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_scanrate.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:09 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TBL_SCANRATE_H__
#define __TBL_SCANRATE_H__

#include "row_reader.h"
#include <limits.h>

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
#include "database_connection.h"

/*-----------------------------------------------------------------------------*
 *  Makes use of resolveScanType(string)
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

   static std::string getSQLCoreStatement();

   static std::string addIDSQLClause(const Cti::Database::id_set &paoids);

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   virtual void DumpData();

   static std::string getTableName();

};
#endif // #ifndef __TBL_SCANRATE_H__
