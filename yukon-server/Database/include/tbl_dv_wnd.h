
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_wnd.h
*
* Date:   10/03/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_dv_wnd.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:58:15 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TBL_DEVICEWINDOW_H__
#define __TBL_DEVICEWINDOW_H__

#include <rw/db/reader.h>
#include <rw\cstring.h>
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/datetime.h>
#include <rw/rwtime.h>
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

class IM_EX_CTIYUKONDB CtiTableDeviceWindow : public CtiMemDBObject
{
protected:

   LONG        _ID;
   LONG        _type;      // scan, peak, alt rate
   LONG        _open;       // seconds since midnight
   LONG        _duration;   // seconds window is open
   LONG        _alternateOpen;    // seconds since midnight
   LONG        _alternateDuration; // seconds window is open

   // Bookkeeping
   BOOL        _updated;      // Used to determine updated state of scan rate...

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

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   virtual void DumpData();

   static RWCString getTableName();

   virtual RWDBStatus Restore();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Delete();

};
#endif // #ifndef __TBL_SCANWINDOW_H__
