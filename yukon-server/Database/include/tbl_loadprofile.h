/*-----------------------------------------------------------------------------*
*
* File:   tbl_loadprofile
*
* Date:   8/20/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_loadprofile.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/04/11 20:04:54 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)

#ifndef __TBL_LOADPROFILE_H__
#define __TBL_LOADPROFILE_H__

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
public:

    enum
    {
        MaxCollectedChannel = 4
    };

protected:

   LONG     _deviceID;
   INT      _lastIntervalDemandRate;
   INT      _loadProfileDemandRate;
   INT      _voltageDemandInterval;
   INT      _voltageProfileRate;

   BOOL     _channelValid[MaxCollectedChannel];

private:

public:

   CtiTableDeviceLoadProfile();

   CtiTableDeviceLoadProfile(const CtiTableDeviceLoadProfile& aRef);

   virtual ~CtiTableDeviceLoadProfile();

   CtiTableDeviceLoadProfile& operator=(const CtiTableDeviceLoadProfile& aRef);

   INT  getLastIntervalDemandRate() const;
   INT  getLoadProfileDemandRate()  const;
   INT  getVoltageDemandInterval()  const;
   INT  getVoltageProfileRate() const;
/*
   CtiTableDeviceLoadProfile& setLoadProfileDemandRate( const INT aRate );
   CtiTableDeviceLoadProfile& setLastIntervalDemandRate( const INT aDemandInterval );
*/
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
