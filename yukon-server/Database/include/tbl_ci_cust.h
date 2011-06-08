
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
* REVISION     :  $Revision: 1.2.24.2 $
* DATE         :  $Date: 2008/11/18 20:11:29 $
*
* Copyright (c) 1999-2003 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include <rw/thr/recursiv.h>
#include "row_reader.h"

#include "dlldefs.h"
#include "yukon.h"

#include <set>
#include <vector>

class IM_EX_CTIYUKONDB CtiTableCICustomerBase
{
public:

   typedef std::set< int > INTSET;
   typedef std::set< int >::iterator INTSETITERATOR;
   typedef std::set< int >::const_iterator CONST_INTSETITERATOR;

protected:

   LONG        _id;
   std::set< int > _contactNotificationIDs; // this customers contacts contactNotificationIDs *digest that*

private:
    mutable CtiMutex _classMutex;
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
   std::vector<int> getContactNotificationVector() const;

   void dumpContactNotifications() const;
   void dump() const;

   static std::string getTableName();
   virtual bool Restore();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};
#endif // #ifndef __TBL_CI_CUST_H__
