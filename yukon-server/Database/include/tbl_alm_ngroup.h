
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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/12/20 17:16:07 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <set>

#include <rw/db/db.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "tbl_alm_ndest.h"
#include "yukon.h"

using std::set;
using std::vector;

class IM_EX_CTIYUKONDB CtiTableNotificationGroup : public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
protected:

   LONG                 _notificationGroupID;
   string               _groupName;
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
   string getGroupName() const;
   BOOL isDisabled() const;
   vector<int> getRecipientVector();


   CtiTableNotificationGroup& setGroupID( const LONG &aRef );
   CtiTableNotificationGroup& setGroupName( const string &aStr );
   CtiTableNotificationGroup& setDisabled( const BOOL b = TRUE );
   CtiTableNotificationGroup& setDirty( bool dirt );

   bool isDirty() const;

   void dump() const;

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   static string getTableName();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Restore();
   virtual RWDBStatus Delete();

   virtual void DecodeDatabaseReader(RWDBReader& rdr);

};
#endif // #ifndef __TBL_ALM_NGROUP_H__
