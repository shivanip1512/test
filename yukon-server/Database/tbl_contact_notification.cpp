#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_contact_notification
*
* Date:   2/3/2002
*
* Author: Corey G. Plender
*         Aaron Lauinger
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_contact_notification.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/02/10 23:23:48 $
*
* Copyright (c) 1999-2003 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <rw\rwtime.h>
#include <rw\cstring.h>

#include "dbaccess.h"
#include "dllbase.h"
#include "tbl_contact_notification.h"
#include "logger.h"

CtiTableContactNotification::CtiTableContactNotification(LONG id) :
  _contactNotifID(id),
  _contactID(0),
  _notificationCategoryID(0),
  _disabled(TRUE),
  _notification("") {
}

CtiTableContactNotification::CtiTableContactNotification(const CtiTableContactNotification& aRef) {
  *this = aRef;
}

CtiTableContactNotification::~CtiTableContactNotification() {
}

CtiTableContactNotification& CtiTableContactNotification::operator=(const CtiTableContactNotification& aRef) {
  if(this != &aRef) {
    _contactNotifID = aRef._contactNotifID;
    _contactID = aRef._contactID;
    _notificationCategoryID = aRef._notificationCategoryID;
    _disabled = aRef._disabled;
    _notification = aRef._notification;
    _dirty = aRef._dirty;
  }
  return *this;
}

bool CtiTableContactNotification::operator<(const CtiTableContactNotification& rhs) const {
  return(_contactNotifID < rhs._contactNotifID);
}

bool CtiTableContactNotification::operator==( const CtiTableContactNotification &rhs ) const {
  return(_contactNotifID == rhs._contactNotifID);
}

bool CtiTableContactNotification::operator()(const CtiTableContactNotification& aRef) const {
  return operator<(aRef);
}

LONG CtiTableContactNotification::getContactNotificationID() const {
  return _contactNotifID;
}

CtiTableContactNotification& CtiTableContactNotification::setContactNotificationID(LONG id) {
  _contactNotifID = id;
  return *this;
}

LONG CtiTableContactNotification::getContactID() const {
  return _contactID;
}

CtiTableContactNotification& CtiTableContactNotification::setContactID(LONG id) {
  _contactID = id;
  return *this;
}

LONG CtiTableContactNotification::getNotificationCategoryID() const {
  return _notificationCategoryID;
}

CtiTableContactNotification& CtiTableContactNotification::setNotificationCategoryID(LONG id) {
  _notificationCategoryID = id;
  return *this;
}

BOOL CtiTableContactNotification::isDisabled() const {
  return _disabled;
}

CtiTableContactNotification& CtiTableContactNotification::setDisabled(BOOL disabled) {
  _disabled = disabled;
  return *this;
}

const RWCString& CtiTableContactNotification::getNotification() const {
  return _notification;
}

CtiTableContactNotification& CtiTableContactNotification::setNotification(const RWCString& notif) {
  _notification = notif;
  return *this;
}

bool CtiTableContactNotification::isDirty() const {
  return _dirty;
}

CtiTableContactNotification& CtiTableContactNotification::setDirty( bool dirt ) {
  _dirty = dirt;
  return *this;
}

void CtiTableContactNotification::dump() const {
  CtiLockGuard<CtiLogger> doubt_guard(dout);

  dout << "NotificationCategoryID: " << getNotificationCategoryID() << endl;
  dout << "ContactID: " << getContactID() << endl;
  dout << "NotificationCategoryID: " << getNotificationCategoryID() << endl;
  dout << "Disabled: " << isDisabled() << endl;
  dout << "Notification: " << getNotification() << endl;
  dout << "Dirty: " << isDirty() << endl;
}

RWDBStatus CtiTableContactNotification::Restore() {
  CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
  RWDBConnection conn = getConnection();

  RWDBStatus dbstat;

  {
    RWDBTable table = getDatabase().table(getTableName());
    RWDBSelector selector = getDatabase().selector();

    selector <<
      table["contactnotifid"] <<
      table["contactid"] <<
      table["notificationcategoryid"] <<
      table["disableflag"] <<
      table["notification"];

    selector.where(table["contactnotifid"] == getContactNotificationID());

    RWDBReader reader = selector.reader( conn );

    dbstat = selector.status();

    if( reader() ) {
      DecodeDatabaseReader(reader);
    }
  }

    return dbstat;
}

void CtiTableContactNotification::DecodeDatabaseReader(RWDBReader& rdr) {
  RWCString rwstemp;

  rdr["contactnotifid"] >> _contactNotifID;
  rdr["contactid"] >> _contactID;
  rdr["notificationcategoryid"] >> _notificationCategoryID;
  rdr["disableflag"] >> rwstemp;
  rdr["notification"] >> _notification;

  rwstemp.toLower();
  _disabled = (rwstemp[(size_t)0] == 'y');

  setDirty(false);  // Not dirty anymore
}

RWCString CtiTableContactNotification::getTableName() {
  return RWCString("ContactNotification");
}

void CtiTableContactNotification::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) {
  keyTable = db.table(getTableName());

  selector <<
    keyTable["contactnotificationid"] <<
    keyTable["contactid"] <<
    keyTable["notificationcategoryid"] <<
    keyTable["disableflag"] <<
    keyTable["notification"];

  selector.from(keyTable);
}

