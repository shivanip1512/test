
#pragma warning( disable : 4786)
#ifndef __TBL_CI_CUST_H__
#define __TBL_CI_CUST_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_ci_cust.h
*
* Class:  CtiTableCICustomerBase
* Date:   4/2/2001
*
* Author: Corey G. Plender
*         Aaron Lauinger
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_ci_cust.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/02/19 16:02:52 $
*
* Copyright (c) 1999-2003 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <set>
#include <vector>
using namespace std;

#include <rw/db/db.h>
#include <rw/rwtime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "yukon.h"


class IM_EX_CTIYUKONDB CtiTableCICustomerBase : public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
public:

   typedef set< int > INTSET;
   typedef set< int >::iterator INTSETITERATOR;
   typedef set< int >::const_iterator CONST_INTSETITERATOR;

protected:

   LONG        _id;
   set< int > _contactNotificationIDs; // this customers contacts contactNotificationIDs *digest that*

private:

public:
   CtiTableCICustomerBase(LONG id = -1);
   CtiTableCICustomerBase(const CtiTableCICustomerBase& aRef);
   virtual ~CtiTableCICustomerBase();

   CtiTableCICustomerBase& operator=(const CtiTableCICustomerBase& aRef);
   bool operator<( const CtiTableCICustomerBase &rhs ) const;
   bool operator==( const CtiTableCICustomerBase &rhs ) const;
   bool operator()(const CtiTableCICustomerBase& aRef) const;

   size_t   entries() const;

   LONG getID() const;
   CtiTableCICustomerBase& setID( const LONG &id);

   INTSET getContactNotificationSet() const;
   CtiTableCICustomerBase& setContactNotificationSet(const INTSET& rhs);
   vector<int> getContactNotificationVector() const;

   void dumpContactNotifications() const;
   void dump() const;

   static RWCString getTableName();
   virtual RWDBStatus Restore();

   static void getSQL(RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
};
#endif // #ifndef __TBL_CI_CUST_H__
