#pragma once

#include <limits.h>
#include <set>

#include "row_reader.h"
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "database_connection.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "types.h"
#include "logger.h"

class IM_EX_CTIYUKONDB CtiTableDeviceWindow : public CtiMemDBObject, public Cti::Loggable
{
public:
    typedef std::set< int > CtiWindowSet_t;
protected:

   LONG        _ID;
   LONG        _type;      // scan, peak, alt rate
   LONG        _open;       // seconds since midnight
   LONG        _duration;   // seconds window is open
   LONG        _alternateOpen;    // seconds since midnight
   LONG        _alternateDuration; // seconds window is open

   // Bookkeeping
   BOOL        _updated;    // Used to determine updated state of scan rate...
   mutable CtiWindowSet_t _signaledRateActive;
   mutable CtiWindowSet_t _signaledRateSent;

public:

   CtiTableDeviceWindow();
   virtual ~CtiTableDeviceWindow();

   CtiTableDeviceWindow(const CtiTableDeviceWindow &aRef);

   CtiTableDeviceWindow& operator=(const CtiTableDeviceWindow &aRef);

   LONG getType() const;
   CtiTableDeviceWindow& setType( const LONG aWindowType );

   LONG getID() const;
   CtiTableDeviceWindow& setID( const LONG did );

   LONG getOpen() const;
   CtiTableDeviceWindow& setOpen( const LONG aSecondsSinceMidnight );

   LONG getAlternateOpen() const;
   CtiTableDeviceWindow& setAlternateOpen( const LONG aSecondsSinceMidnight );

   LONG getDuration() const;
   CtiTableDeviceWindow& setDuration( const LONG aDuration );

   LONG getAlternateDuration() const;
   CtiTableDeviceWindow& setAlternateDuration( const LONG aDuration );

   BOOL getUpdated() const;
   CtiTableDeviceWindow& setUpdated( const BOOL aBool );

    LONG calculateClose(LONG aOpen, LONG aDuration) const;

   static std::string getSQLCoreStatement();
   static std::string addIDSQLClause(const Cti::Database::id_set &paoids);

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   virtual std::string toString() const override;

   static std::string getTableName();

   bool addSignaledRateActive(int rate) const;
   bool addSignaledRateSent(int rate) const;
   bool isSignaledRateScheduled(int rate) const;
   bool verifyWindowMatch() const;

};
