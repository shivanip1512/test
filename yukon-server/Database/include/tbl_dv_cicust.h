
#pragma warning( disable : 4786)
#ifndef __TBL_DV_CICUST_H__
#define __TBL_DV_CICUST_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_cicust
*
* Class:  CtiTableCICustomerBase
* Date:   4/2/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_dv_cicust.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:39 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
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

   set< int > _custContacts;             // the contacts
   set< int > _custLocationIDs;          // the contacts' locationIDs

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

   INTSET getRecipientSet() const;
   CtiTableCICustomerBase& setRecipientSet( const INTSET &rhs);
   vector<int> getRecipientVector() const;

   void dumpRecipients() const;
   void dump() const;

   static RWCString getTableName();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Restore();
   virtual RWDBStatus Delete();

   static void getSQL(RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
};
#endif // #ifndef __TBL_DV_CICUST_H__
