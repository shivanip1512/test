#pragma once

#include <windows.h>
#include "ctitime.h"

#include "dlldefs.h"
#include "pointdefs.h"
#include "utility.h"
#include "yukon.h"
#include "row_reader.h"
#include "database_connection.h"

#include <boost/shared_ptr.hpp>
#include "boostutil.h"
using boost::shared_ptr;

class IM_EX_CTIYUKONDB CtiTableMeterReadLog
{
protected:

    long _logID;
    long _deviceID;
    long _requestID;
    long _statusCode;
    CtiTime _time;

private:

public:

   CtiTableMeterReadLog(   LONG     logid          = 0L,
                           LONG     devid          = 0L,
                           LONG     reqid          = 0L,
                           LONG     code           = 0L,
                           const CtiTime    &tme    = CtiTime() ) :
      _logID(logid),
      _deviceID(devid),
      _requestID(reqid),
      _statusCode(code),
      _time(tme)
   {
   }

   CtiTableMeterReadLog(const CtiTableMeterReadLog& aRef)
   {
      *this = aRef;
   }

   virtual ~CtiTableMeterReadLog() {}

   CtiTableMeterReadLog& operator=(const CtiTableMeterReadLog& aRef);
   virtual bool operator<(const CtiTableMeterReadLog& aRef) const;

   virtual void Insert();
   virtual void Insert(Cti::Database::DatabaseConnection &conn);
   virtual std::string getTableName() const;

   virtual void DecodeDatabaseReader( Cti::RowReader& rdr );

   LONG                    getLogID() const;
   LONG                    getDeviceID() const;
   LONG                    getRequestLogID() const;
   LONG                    getStatusCode() const;
   CtiTime                 getTime() const;

   CtiTableMeterReadLog&   setLogID(LONG id);
   CtiTableMeterReadLog&   setDeviceID(LONG id);
   CtiTableMeterReadLog&   setRequestLogID(LONG id);
   CtiTableMeterReadLog&   setStatusCode(LONG code);
   CtiTableMeterReadLog&   setTime(const CtiTime &newTime);
};

typedef shared_ptr< CtiTableMeterReadLog > CtiTableMeterReadLogSPtr;
