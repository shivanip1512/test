#include "precompiled.h"

#include "database_connection.h"
#include "database_reader.h"
#include "dbaccess.h"
#include "dllbase.h"
#include "tbl_contact_notification.h"
#include "logger.h"


using std::string;
using std::endl;

CtiTableContactNotification::CtiTableContactNotification(LONG id) :
  _contactNotifID(id),
  _contactID(0),
  _notificationCategoryID(0),
  _disabled(TRUE),
  _notification(""),
  _dirty(false) {
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

const string& CtiTableContactNotification::getNotification() const {
  return _notification;
}

CtiTableContactNotification& CtiTableContactNotification::setNotification(const string& notif) {
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

std::string CtiTableContactNotification::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableContactNotification";
    itemList.add("NotificationCategoryID") << getNotificationCategoryID();
    itemList.add("ContactID")              << getContactID();
    itemList.add("NotificationCategoryID") << getNotificationCategoryID();
    itemList.add("Disabled")               << isDisabled();
    itemList.add("Notification")           << getNotification();
    itemList.add("Dirty")                  << isDirty();

    return itemList.toString();
}

bool CtiTableContactNotification::Restore()
{
    static const string sql =  "SELECT CTN.contactnotifid, CTN.contactid, CTN.notificationcategoryid, CTN.disableflag, "
                                   "CTN.notification "
                               "FROM ContactNotification CTN "
                               "WHERE CTN.contactnotifid = ?";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader reader(connection, sql);

    reader << getContactNotificationID();

    reader.execute();

    if( reader() ) {
      DecodeDatabaseReader(reader);
      return true;
    }

    return false;
}

void CtiTableContactNotification::DecodeDatabaseReader(Cti::RowReader& rdr) {
  string tmpStr;

  rdr["contactnotifid"] >> _contactNotifID;
  rdr["contactid"] >> _contactID;
  rdr["notificationcategoryid"] >> _notificationCategoryID;
  rdr["disableflag"] >> tmpStr;
  rdr["notification"] >> _notification;

  std::transform(tmpStr.begin(), tmpStr.end(), tmpStr.begin(), tolower);
  _disabled = (tmpStr[(size_t)0] == 'y');

  setDirty(false);  // Not dirty anymore
}

string CtiTableContactNotification::getTableName() {
  return string("ContactNotification");
}

