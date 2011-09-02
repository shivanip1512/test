#pragma once

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
