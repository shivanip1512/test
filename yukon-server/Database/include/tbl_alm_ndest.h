
#pragma warning( disable : 4786)
#ifndef __TBL_ALM_NDEST_H__
#define __TBL_ALM_NDEST_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_alm_ndest
*
* Class:  CtiTableNotificationDestination
* Date:   10/11/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_alm_ndest.h-arc  $
* REVISION     :  $Revision: 1.4.24.1 $
* DATE         :  $Date: 2008/11/18 20:11:29 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

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
#endif // #ifndef __TBL_ALM_NDEST_H__
