
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
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:39 $
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
public:

   typedef set< CtiTableNotificationDestination > DESTINATIONSET;

protected:

   LONG                 _notificationGroupID;
   RWCString            _groupName;

   RWCString            _emailFromAddress;
   RWCString            _emailSubject;
   RWCString            _emailMessage;

   RWCString            _numericPagerMessage;

   BOOL                 _groupDisabled;

   DESTINATIONSET       _destinationSet;

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
   RWCString getEmailFromAddress() const;
   RWCString getEmailSubject() const;
   RWCString getEmailMessage() const;
   RWCString getNumericPagerMessage() const;
   DESTINATIONSET getDestinations() const;
   BOOL isDisabled() const;
   vector<int> getRecipientVector();


   CtiTableNotificationGroup& setGroupID( const LONG &aRef );
   CtiTableNotificationGroup& setGroupName( const RWCString &aStr );
   CtiTableNotificationGroup& setEmailFromAddress( const RWCString &aStr );
   CtiTableNotificationGroup& setEmailSubject( const RWCString &aStr );
   CtiTableNotificationGroup& setEmailMessage( const RWCString &aStr );
   CtiTableNotificationGroup& setNumericPagerMessage( const RWCString &aStr );
   CtiTableNotificationGroup& setDisabled( const BOOL b = TRUE );
   CtiTableNotificationGroup& setDestinations( const DESTINATIONSET dest );
   CtiTableNotificationGroup& setDirty( bool dirt );

   bool isDirty() const;

   void dump() const;
   void dumpDestinations() const;

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   static RWCString getTableName();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Restore();
   virtual RWDBStatus Delete();

   virtual void DecodeDatabaseReader(RWDBReader& rdr);

};
#endif // #ifndef __TBL_ALM_NGROUP_H__
