#pragma once

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
