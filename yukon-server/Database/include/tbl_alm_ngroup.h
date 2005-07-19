
#pragma warning( disable : 4786)
#ifndef __TBL_ALM_NGROUP_H__
#define __TBL_ALM_NGROUP_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_alm_ngroup
*
* Class:  CtiTableNotificationGroup
* Date:   10/11/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_alm_ngroup.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/07/19 22:48:52 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <set>
using namespace std;

#include <rw/db/db.h>
#include <rw/rwtime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "tbl_alm_ndest.h"
#include "yukon.h"


class IM_EX_CTIYUKONDB CtiTableNotificationGroup : public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
protected:

   LONG                 _notificationGroupID;
   RWCString            _groupName;
   BOOL                 _groupDisabled;
   bool                 _isDirty;


private:

public:

   CtiTableNotificationGroup( LONG gid = 0L);
   CtiTableNotificationGroup(const CtiTableNotificationGroup& aRef);
   virtual ~CtiTableNotificationGroup();

   CtiTableNotificationGroup& operator=(const CtiTableNotificationGroup& aRef);
   bool operator<( const CtiTableNotificationGroup &rhs ) const;
   bool operator==( const CtiTableNotificationGroup &rhs ) const;
   bool operator()(const CtiTableNotificationGroup& aRef) const;

   size_t   entries() const;

   LONG getGroupID() const;
   RWCString getGroupName() const;
   BOOL isDisabled() const;
   vector<int> getRecipientVector();


   CtiTableNotificationGroup& setGroupID( const LONG &aRef );
   CtiTableNotificationGroup& setGroupName( const RWCString &aStr );
   CtiTableNotificationGroup& setDisabled( const BOOL b = TRUE );
   CtiTableNotificationGroup& setDirty( bool dirt );

   bool isDirty() const;

   void dump() const;

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   static RWCString getTableName();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Restore();
   virtual RWDBStatus Delete();

   virtual void DecodeDatabaseReader(RWDBReader& rdr);

};
#endif // #ifndef __TBL_ALM_NGROUP_H__
