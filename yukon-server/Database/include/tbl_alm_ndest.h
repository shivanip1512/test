#pragma once

#include <rw/thr/recursiv.h>
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableNotificationDestination
{
public:

   typedef enum
   {
      eEmailDestination,
      eNumericPagerDestination,
      eAlphaPagerDestination,

      eInvalidDestination

   } CtiNotificationDestinationType_t;

   typedef enum
   {
      eToEmail,
      eCCEmail,

      eInvalidEmail

   } CtiNotificationEmailType_t;


protected:

   LONG           _notificationGroupID;  // Implied by collection's ownership!
   LONG           _recipientID;
   INT            _destinationOrder;

private:

   bool           _isDirty;
   mutable CtiMutex _classMutex;

public:
   CtiTableNotificationDestination();
   CtiTableNotificationDestination(const CtiTableNotificationDestination& aRef);
   virtual ~CtiTableNotificationDestination();

   CtiTableNotificationDestination& operator=(const CtiTableNotificationDestination& aRef);

   bool operator<( const CtiTableNotificationDestination &rhs ) const;
   bool operator==( const CtiTableNotificationDestination &rhs ) const;
   bool operator()(const CtiTableNotificationDestination& aRef) const;

   LONG getGroupID() const;
   INT getDestinationOrder() const;
   INT getRecipientID() const;

   CtiTableNotificationDestination& setGroupID(const LONG &aL);
   CtiTableNotificationDestination& setDestinationOrder(const INT &aInt);
   CtiTableNotificationDestination& setRecipientID(const LONG &id);

   void dump() const;

   static std::string getTableName();

   virtual void DecodeDatabaseReader(Cti::RowReader& rdr);

   CtiTableNotificationDestination& setDirty( bool dirt );

   bool isDirty() const;
};
