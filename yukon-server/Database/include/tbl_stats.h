#ifndef __TBL_STATS_H__
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_stats
*
* Date:   8/18/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_stats.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:09 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define __TBL_STATS_H__

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

class IM_EX_CTIYUKONDB CtiTableDeviceStatistics : CtiMemDBObject
{
protected:

   LONG           _deviceID;
   INT            Type;
   INT            Attempts;
   INT            CommLineErrors;
   INT            SystemErrors;
   INT            DLCErrors;
   CtiTime         StartTime;
   CtiTime         StopTime;

public:

   CtiTableDeviceStatistics();

   CtiTableDeviceStatistics(const CtiTableDeviceStatistics& aRef);

   virtual ~CtiTableDeviceStatistics();

   CtiTableDeviceStatistics& operator=(const CtiTableDeviceStatistics& aRef);

   INT  getType() const;

   CtiTableDeviceStatistics& setType( const INT aType );

   INT  getAttempts() const;

   CtiTableDeviceStatistics& setAttempts( const INT aAttempts );

   INT  getCommLineErrors() const;

   CtiTableDeviceStatistics& setCommLineErrors( const INT aCommLineErrors );

   INT  getSystemErrors() const;

   CtiTableDeviceStatistics& setSystemErrors( const INT aSystemErrors );

   INT  getDLCErrors() const;

   CtiTableDeviceStatistics& setDLCErrors( const INT aDLCErrors );

   CtiTime  getStartTime() const;

   CtiTableDeviceStatistics& setStartTime( const CtiTime& aStartTime );

   CtiTime  getStopTime() const;

   CtiTableDeviceStatistics& setStopTime( const CtiTime& aStopTime );

   LONG  getDeviceID() const;

   CtiTableDeviceStatistics& setDeviceID( const LONG did);

   /* These guys are handled different since they are multi-keyed */
   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   virtual void DumpData();
   static std::string getTableName();

};
#endif // #ifndef __TBL_STATS_H__
