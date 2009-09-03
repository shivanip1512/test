/*-----------------------------------------------------------------------------*
*
* File:   tbl_loadprofile
*
* Date:   8/20/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_loadprofile.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2007/03/22 17:22:39 $
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
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/db.h>
#include <rw/db/datetime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include <bitset>

#include "yukon.h"
#include "vcomdefs.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "da_load_profile.h"

class IM_EX_CTIYUKONDB CtiTableDeviceLoadProfile : public CtiMemDBObject, public DataAccessLoadProfile
{
public:

    enum
    {
        MaxCollectedChannel = 4
    };

private:

protected:

   LONG  _deviceID;
   INT   _lastIntervalDemandRate;
   INT   _loadProfileDemandRate;
   INT   _voltageDemandInterval;
   INT   _voltageProfileRate;

   std::bitset<MaxCollectedChannel> _channelsValid;

public:

   CtiTableDeviceLoadProfile();

   CtiTableDeviceLoadProfile(const CtiTableDeviceLoadProfile& aRef);

   virtual ~CtiTableDeviceLoadProfile();

   CtiTableDeviceLoadProfile& operator=(const CtiTableDeviceLoadProfile& aRef);

   virtual int  getLastIntervalDemandRate() const;
   virtual int  getLoadProfileDemandRate()  const;
   virtual int  getVoltageDemandInterval()  const;
   virtual int  getVoltageProfileRate()     const;
   virtual bool isChannelValid(int channel) const;

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   static string getTableName();

   LONG getDeviceID() const;

   virtual RWDBStatus Restore();
   virtual RWDBStatus Update();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Delete();
};

#endif // #ifndef __TBL_LOADPROFILE_H__
