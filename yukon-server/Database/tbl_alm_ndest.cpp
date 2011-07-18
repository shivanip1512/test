#include "precompiled.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_alm_ndest
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_alm_ndest.cpp-arc  $
* REVISION     :  $Revision: 1.5.24.1 $
* DATE         :  $Date: 2008/11/18 20:11:29 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include "row_reader.h"

#include "dbaccess.h"
#include "tbl_alm_ndest.h"
#include "logger.h"

using std::string;
using std::endl;

CtiTableNotificationDestination::~CtiTableNotificationDestination()
{

}

LONG CtiTableNotificationDestination::getGroupID() const
{

   return _notificationGroupID;
}
INT CtiTableNotificationDestination::getDestinationOrder() const
{

   return _destinationOrder;
}
INT CtiTableNotificationDestination::getRecipientID() const
{

   return _recipientID;
}

CtiTableNotificationDestination& CtiTableNotificationDestination::setGroupID(const LONG &aL)
{

   _notificationGroupID = aL;
   return *this;
}

CtiTableNotificationDestination& CtiTableNotificationDestination::setDestinationOrder(const INT &aInt)
{

   _destinationOrder = aInt;
   return *this;
}

CtiTableNotificationDestination& CtiTableNotificationDestination::setRecipientID(const LONG &id)
{

   _recipientID = id;
   return *this;
}

string CtiTableNotificationDestination::getTableName()
{
   return string("NotificationDestination");
}

void CtiTableNotificationDestination::DecodeDatabaseReader(Cti::RowReader& rdr)
{


   rdr["notificationgroupid"]   >> _notificationGroupID;
   rdr["destinationorder"]      >> _destinationOrder;
   rdr["recipientid"]            >> _recipientID;

   setDirty(false);  // Not dirty anymore

   return;
}

bool CtiTableNotificationDestination::operator<( const CtiTableNotificationDestination &rhs ) const
{
   return (getDestinationOrder() < rhs.getDestinationOrder() );
}

bool CtiTableNotificationDestination::operator==( const CtiTableNotificationDestination &rhs ) const
{
   return ( (getGroupID()           == rhs.getGroupID()) &&
            (getDestinationOrder()  == rhs.getDestinationOrder()) );
}

bool CtiTableNotificationDestination::operator()(const CtiTableNotificationDestination& aRef) const
{
   return operator<(aRef);
}

void CtiTableNotificationDestination::dump() const
{


   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << " Order " << _destinationOrder;
      dout << " Notification Group " << _notificationGroupID;
      dout << " Recipient ID " << _recipientID << endl;
   }
}

CtiTableNotificationDestination& CtiTableNotificationDestination::setDirty( bool dirt )
{

   _isDirty = dirt;
   return *this;
}
bool CtiTableNotificationDestination::isDirty() const
{

   return _isDirty;
}

CtiTableNotificationDestination& CtiTableNotificationDestination::operator=(const CtiTableNotificationDestination& aRef)
{
   if(this != &aRef)
   {
      setGroupID(aRef.getGroupID());
      setDestinationOrder(aRef.getDestinationOrder());
      setRecipientID(aRef.getRecipientID());
      setDirty( aRef.isDirty() );
   }
   return *this;
}

CtiTableNotificationDestination::CtiTableNotificationDestination() :
   _isDirty(true),
   _notificationGroupID(-1),
   _destinationOrder(0),
   _recipientID(0)
{}

CtiTableNotificationDestination::CtiTableNotificationDestination(const CtiTableNotificationDestination& aRef)
{
   *this = aRef;
}



