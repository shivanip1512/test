
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_loadprofile
*
* Date:   8/20/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_loadprofile.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:58:15 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TBL_LOADPROFILE_H__
#define __TBL_LOADPROFILE_H__

#define MAX_COLLECTED_CHANNEL 4
#include <rw/db/select.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <rw\cstring.h>
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/db.h>
#include <rw/db/datetime.h>
#include <rw/rwtime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "yukon.h"
#include "vcomdefs.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"

class IM_EX_CTIYUKONDB CtiTableDeviceLoadProfile : public CtiMemDBObject
{
protected:

   LONG     _deviceID;
   INT      _lastIntervalDemandRate;
   INT      _loadProfileDemandRate;

   BOOL     _channelValid[ MAX_COLLECTED_CHANNEL ];

private:

public:

   CtiTableDeviceLoadProfile();

   CtiTableDeviceLoadProfile(const CtiTableDeviceLoadProfile& aRef);

   virtual ~CtiTableDeviceLoadProfile();

   CtiTableDeviceLoadProfile& operator=(const CtiTableDeviceLoadProfile& aRef);

   INT  getLastIntervalDemandRate() const;

   CtiTableDeviceLoadProfile& setLastIntervalDemandRate( const INT aDemandInterval );

   INT  getLoadProfileDemandRate() const;

   CtiTableDeviceLoadProfile& setLoadProfileDemandRate( const INT aRate );

   BOOL isChannelValid(const INT ch) const;

   CtiTableDeviceLoadProfile& setChannelValid( const INT ch, const BOOL val = TRUE );

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   static RWCString getTableName();

   LONG getDeviceID() const;
   CtiTableDeviceLoadProfile& setDeviceID( const LONG did);

   virtual RWDBStatus Restore();
   virtual RWDBStatus Update();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Delete();

};
#endif // #ifndef __TBL_LOADPROFILE_H__
