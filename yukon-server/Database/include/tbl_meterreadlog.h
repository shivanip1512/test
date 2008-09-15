
/*-----------------------------------------------------------------------------*
*
* File:   tbl_meterreadlog
*
* Date:   1/24/2007
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/Database/Include/tbl_meterreadlog.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2008/09/15 17:59:18 $
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_METERREADLOG_H__
#define __TBL_METERREADLOG_H__

#include <windows.h>
#include "ctitime.h"
#include <rw/db/datetime.h>

#include "dlldefs.h"
#include "pointdefs.h"
#include "utility.h"
#include "yukon.h"

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
   virtual RWBoolean operator<(const CtiTableMeterReadLog& aRef) const;

   virtual void Insert();
   virtual void Insert(RWDBConnection &conn);
   virtual string getTableName() const;

   virtual void DecodeDatabaseReader( RWDBReader& rdr );

   LONG                    getLogID() const;
   LONG                    getDeviceID() const;
   LONG                    getRequestLogID() const;
   LONG                    getStatusCode() const;
   CtiTime                 getTime() const;

   CtiTableMeterReadLog&   setLogID(LONG id);
   CtiTableMeterReadLog&   setDeviceID(LONG id);
   CtiTableMeterReadLog&   setRequestLogID(LONG id);
   CtiTableMeterReadLog&   setStatusCode(LONG code);
   CtiTableMeterReadLog&   setTime(const CtiTime &rwt);
};

typedef shared_ptr< CtiTableMeterReadLog > CtiTableMeterReadLogSPtr;
#endif // #ifndef __TBL_METERREADLOG_H__
