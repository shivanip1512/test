#pragma once

#include "row_reader.h"
#include <limits.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "types.h"
#include "logger.h"
#include "database_connection.h"

/*-----------------------------------------------------------------------------*
 *  Makes use of resolveScanType(string)
 *-----------------------------------------------------------------------------*/

class IM_EX_CTIYUKONDB CtiTableDeviceScanRate : public CtiMemDBObject, private boost::noncopyable, public Cti::Loggable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableDeviceScanRate(const CtiTableDeviceScanRate&);
    CtiTableDeviceScanRate& operator=(const CtiTableDeviceScanRate&);

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

   virtual std::string toString() const override;

   static std::string getTableName();

};
