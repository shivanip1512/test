#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_alm_ndest
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_alm_ndest.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:23:47 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <rw\rwtime.h>
#include <rw\cstring.h>

#include "dbaccess.h"
#include "tbl_alm_ndest.h"
#include "logger.h"

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

RWCString CtiTableNotificationDestination::getTableName()
{
   return RWCString("NotificationDestination");
}

RWDBStatus CtiTableNotificationDestination::Insert()
{
   CtiLockGuard<CtiLogger> doubt_guard(dout);
   dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;

   return RWDBStatus();
}

RWDBStatus CtiTableNotificationDestination::Update()
{
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   return RWDBStatus();
}

RWDBStatus CtiTableNotificationDestination::Restore()
{
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   return RWDBStatus();
}
RWDBStatus CtiTableNotificationDestination::Delete()
{
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   return RWDBStatus();
}

void CtiTableNotificationDestination::getSQL(RWDBDatabase &db,  RWDBTable &table, RWDBSelector &selector)
{
   table = db.table( getTableName() );

   selector <<
      table["notificationgroupid"] <<
      table["destinationorder"] <<
      table["recipientid"];

   selector.from(table);
}

void CtiTableNotificationDestination::DecodeDatabaseReader(RWDBReader& rdr)
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

CtiTableNotificationDestination::~CtiTableNotificationDestination();

CtiTableNotificationDestination& CtiTableNotificationDestination::operator=(const CtiTableNotificationDestination& aRef);

